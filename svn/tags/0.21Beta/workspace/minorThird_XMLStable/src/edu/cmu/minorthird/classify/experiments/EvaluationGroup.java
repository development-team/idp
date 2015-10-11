package edu.cmu.minorthird.classify.experiments;

import edu.cmu.minorthird.util.*;
import edu.cmu.minorthird.util.gui.*;
import edu.cmu.minorthird.classify.ExampleSchema;

import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.awt.*;
import java.awt.event.*;

/** A group of related evaluations.
 *
 * @author William Cohen
 */

public class EvaluationGroup implements Visible,Serializable,Saveable
{
    private final Map members = new TreeMap();
    private Evaluation someEvaluation=null;
    private SummaryViewer sv;

    /** Add an evaluation to the group */
    public void add(String name,Evaluation evaluation)
    {
        if (someEvaluation==null) someEvaluation=evaluation;
        members.put(name,evaluation);
    }

    /** Return an iterator for the names of evaluations in the group. */
    public Iterator evalNameIterator() { return members.keySet().iterator(); }

    /** Return the evaluation associated with this name. */
    public Evaluation getEvaluation(String name) { return (Evaluation)members.get(name); }


    public String toString()
    {
        return members.toString();
    }

    // summary view
    class SummaryViewer extends ComponentViewer {
        private EvaluationGroup group = null;
        public Object[][] table;
        public String[] columnHeads;
        public JTable jtable;

        public JComponent componentFor(Object o) {
            group = (EvaluationGroup)o;
            columnHeads = new String[group.members.keySet().size()+1];
            columnHeads[0] = "Statistic";
            int k=1;
            for (Iterator i=group.members.keySet().iterator(); i.hasNext(); ) {
                Object key = i.next();
                columnHeads[k] = (String)key; // should be name of key
                k++;
            }
            // set number of summary statistics
            ExampleSchema schema = someEvaluation.getSchema();
            int statNumber = 0;
            if ( someEvaluation.isBinary() ) { statNumber = 10 + 2 * schema.getNumberOfClasses(); }
            else {  statNumber = 3 + 2 * schema.getNumberOfClasses(); }
            // get summary statistics
            table = new Object[statNumber][columnHeads.length];
            k=1;
            for (Iterator i=group.members.keySet().iterator(); i.hasNext(); ) {
                Object key = i.next();
                Evaluation v = (Evaluation)group.members.get(key);
                String[] statNames = v.summaryStatisticNames();
                double[] ss = v.summaryStatistics();
                for (int j=0; j<ss.length; j++) {
                    table[j][0] = statNames[j];
                    table[j][k] = new Double(ss[j]);
                }
                k++;
            }
            jtable = new JTable(table,columnHeads);

            final Transform keyTransform = new Transform() {
                public Object transform(Object key) { return group.members.get(key); }
            };
            monitorSelections(jtable,0,keyTransform);
            JScrollPane scroll = new JScrollPane(jtable);
            return scroll;
        }

    };

    public Viewer toGUI()
    {
        if (members.keySet().size()==0)
            return new VanillaViewer("empty EvaluationGroup");

        ParallelViewer main = new ParallelViewer();
        sv = new SummaryViewer();
        main.addSubView( "Summary", new ZoomedViewer(sv, new Evaluation.PropertyViewer()) );

        // for zooming in to a particular experiment
        Viewer indexViewer = new IndexedViewer() {
            public Object[] indexFor(Object o) {
                EvaluationGroup group = (EvaluationGroup)o;
                return  group.members.keySet().toArray();
            }
        };
        TransformedViewer evaluationKeyViewer = new TransformedViewer()	{
            public Object transform(Object o) {
                return members.get(o);
            }
        };
        evaluationKeyViewer.setSubView( someEvaluation.toGUI() );
        ZoomedViewer zooomer = new ZoomedViewer(indexViewer, evaluationKeyViewer);
        zooomer.setHorizontal();

	// precision recall
        Viewer prViewer = new ComponentViewer() {
            public JComponent componentFor(Object o) {
                EvaluationGroup group = (EvaluationGroup)o;
                LineCharter lc = new LineCharter();
                for (Iterator i=group.members.keySet().iterator(); i.hasNext(); ) {
                    Object key = i.next();
                    Evaluation e = (Evaluation)group.members.get(key);
                    double[] p = e.elevenPointPrecision();
                    lc.startCurve( key.toString() );
                    for (int j=0; j<=10; j++) {
                        lc.addPoint(j/10.0, p[j]);
                    }
                }
                return lc.getPanel("11Pt Interpolated Precision vs Recall", "Recall", "Precision");
            }
        };
        Viewer avgPRViewer = new ComponentViewer() {
            public JComponent componentFor(Object o) {
                EvaluationGroup group = (EvaluationGroup)o;
                LineCharter lc = new LineCharter();
		double [] sum = new double[11];
		int n = 0;
                for (Iterator i=group.members.keySet().iterator(); i.hasNext(); ) {
                    Object key = i.next();
                    Evaluation e = (Evaluation)group.members.get(key);
                    double[] p = e.elevenPointPrecision();
		    for (int j=0; j<=10; j++) sum[j] += p[j];
		    n++;
		}
		lc.startCurve( "Average of all "+n+" 11pt P-R Curves" );
		for (int j=0; j<=10; j++) {
		    lc.addPoint(j/10.0, sum[j]/n);
		}
                return lc.getPanel("Averaged 11Pt Interpolated Precision vs Recall", "Recall", "Precision");
            }
        };
        main.addSubView("11Pt Precision/Recall - Details",prViewer);
        main.addSubView("11Pt Precision/Recall - Average",avgPRViewer);
        main.addSubView( "Details", zooomer );
        main.setContent(this);
        return main;
    }

    //
    // Implement Saveable interface. 
    //
    private static final String FORMAT_NAME = "Evaluation Group";
	private static final String defaultEncoding = "UTF8";
    public String[] getFormatNames() { return new String[] {FORMAT_NAME}; } 
    public String getExtensionFor(String s) { return ".xls"; }
    public void saveAs(File file,String format) throws IOException {
        if (!format.equals(FORMAT_NAME)) throw new IllegalArgumentException("illegal format "+format);
        try {
            String name1 = sv.columnHeads[1].substring(0,sv.columnHeads[1].indexOf("."));
            String name2 = sv.columnHeads[2].substring(0,sv.columnHeads[2].indexOf("."));
            //File evaluation = new File(name1 + "_" + name2 + ".txt");
            //FileWriter out = new FileWriter(evaluation);
            // save in default encoding utf8
            // PrintStream out = new PrintStream(new FileOutputStream(file));
            PrintStream out = new PrintStream(new FileOutputStream(file), false, defaultEncoding);

            for(int i=0; i<sv.columnHeads.length; i++) {
                out.print(sv.columnHeads[i]);
                out.print("\t");
            }
            out.print("\n");
            int columns = sv.jtable.getColumnCount();
            int rows = sv.jtable.getRowCount();
            for(int x=0; x<rows; x++) {
                for(int y=0; y<columns; y++) {
                    out.print(sv.table[x][y].toString());
                    out.print("\t ");
                }
                out.print("\n");
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println("Error Opening Excel File");
            e.printStackTrace();
        }
    }
    public Object restore(File file) throws IOException {
        throw new UnsupportedOperationException("Cannot load EvaluationGroup object");
    }

    //
    // test routine
    //
    static public void main(String[] args)
    {
        try {
            EvaluationGroup group = new EvaluationGroup();
            for (int i=0; i<args.length; i++) {
              try {
                Evaluation v = Evaluation.load(new File(args[i]));
                group.add( args[i], v );
              } catch (IOException ex) {
                try {	
                  Evaluation v =(Evaluation)IOUtil.loadSerialized(new File(args[i]));
                  group.add( args[i], v );                  
                } catch (Exception ex2) {
                  System.out.println("usage: EvaluationGroup serializedFile1 serializedFile2 ...");
                  ex2.printStackTrace();
                }
              }
            }
            ViewerFrame f = new ViewerFrame("From file "+args[0], group.toGUI());
        } catch (Exception e) {
            System.out.println("usage: EvaluationGroup serializedFile1 serializedFile2 ...");
            e.printStackTrace();
        }
    }
}
