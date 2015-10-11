package edu.cmu.minorthird.ui;

import edu.cmu.minorthird.text.*;
import edu.cmu.minorthird.text.learn.*;
import edu.cmu.minorthird.util.*;
import edu.cmu.minorthird.util.gui.*;
import edu.cmu.minorthird.classify.experiments.*;
import edu.cmu.minorthird.classify.*;

// Don't use: import java.io.*;  Because this causes java.io.Console to be included, 
// which conflicts with edu.cmu.minorthird.util.gui.Console
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/** Main UI program.  With enough support to make it configurable
 * interactively, by command lines, or by a file.
 */

public abstract class UIMain implements CommandLineProcessor.Configurable, Console.Task
{			
    private JPanel errorPanel;
    public static JButton viewButton;
    public Console.Task main;

    //
    // some basic parameters and CommandLineProcessor items shared by everyone
    //

    protected CommandLineUtil.BaseParams base = new CommandLineUtil.BaseParams();
    protected CommandLineUtil.GUIParams gui = new CommandLineUtil.GUIParams();

    public CommandLineUtil.BaseParams getBaseParameters() { return base; }
    public void setBaseParameters(CommandLineUtil.BaseParams base) { this.base=base; }
	
    public void helloWorld() { System.out.println("hello world"); }

    /**Do the main action, after setting all parameters. */
    abstract public void doMain();
	
    /** Return the result of the action. */
    abstract public Object getMainResult(); 
    
    /** Returns whether base.labels exits */
    public boolean getLabels(){
	return (base.labels != null);
    }
			
    /** Helper to handle command-line processing, in either gui or text mode. */
    public void callMain(final String[] args) 
    {	   
	try {
	    getCLP().processArguments(args);
	    if (!gui.useGUI) {		
		if (base.labels==null) throw new IllegalArgumentException("-labels must be specified");
		if (base.showLabels) new ViewerFrame("Labeled TextBase", new SmartVanillaViewer(base.labels));
		doMain();
	    }
	    else {
		main = this;			    
		final Viewer v = new ComponentViewer() {
			public JComponent componentFor(Object o) 
			{
			    Viewer ts;
			    if(base.classic)
				ts = new TypeSelector(SelectableTypes.CLASSES, "selectableTypes.txt", o.getClass());
			    else ts = new TypeSelector(SelectableTypes.CLASSES, InLineSelectableTypes.CLASSES, 
							 advancedParameters.NAMES, "selectableTypes.txt", o.getClass());
			    ts.setContent(o);								

			    // we'll put the type selector in a nice panel
			    JPanel panel = new JPanel();
			    panel.setBorder(new TitledBorder(StringUtil.toString(args,"Command line: ",""," ")));
			    panel.setLayout(new GridBagLayout());

			    GridBagConstraints gbc;
			    // another panel to allow parameter modifications
			    JPanel subpanel1 = new JPanel();
			    subpanel1.setBorder(new TitledBorder("Parameter modification"));
			    subpanel1.add( ts );
			    gbc = Viewer.fillerGBC(); gbc.weighty=0; 
			    panel.add( subpanel1, gbc  ); 										    							
			    // a control panel for controls
			    JPanel subpanel2 = new JPanel();
			    subpanel2.setBorder(new TitledBorder("Execution controls"));
			    // a button to show the results
			    viewButton = new JButton(new AbstractAction("View results") {
				    public void actionPerformed(ActionEvent event) {
					Viewer rv = new SmartVanillaViewer();
					rv.setContent( getMainResult() );
					ViewerFrame f = new ViewerFrame("Result", rv);
				    }
				});
			    viewButton.setEnabled(false);

			    // another panel for error messages and other outputs
							
			    errorPanel = new JPanel();
			    errorPanel.setBorder(new TitledBorder("Error messages and output"));
			    final Console console = new Console(main, base.labels != null, viewButton);
			    errorPanel.add(console.getMainComponent());

			    // a button to start this thread
			    JButton goButton = new JButton(new AbstractAction("Start Task") {
				    public void actionPerformed(ActionEvent event) {
					console.start();
				    }
				});
																						 
			    // and a button to show the current labels
			    JButton showLabelsButton = new JButton(new AbstractAction("Show labels") {
				    public void actionPerformed(ActionEvent ev) {
					if (base.labels==null) noLabelsMessage(console);
					else new ViewerFrame("Labeled TextBase", new SmartVanillaViewer(base.labels));
				    }
				});

			    // and a button to clear the errorArea
			    JButton clearButton = new JButton(new AbstractAction("Clear window") {
				    public void actionPerformed(ActionEvent ev) {
					console.clear();
				    }
				});

			    // and a button for help
			    JButton helpParamsButton = new JButton(new AbstractAction("Parameters") {
				    public void actionPerformed(ActionEvent ev) {
					PrintStream oldSystemOut = System.out;
					ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
					System.setOut(new PrintStream(outBuffer));
					getCLP().usage(); 
					console.append(outBuffer.toString());
					System.setOut(oldSystemOut);
				    }
				});

			    // and another
			    JButton helpRepositoryButton = new JButton(new AbstractAction("Repository") {
				    public void actionPerformed(ActionEvent ev) {
					repositoryHelp( console );
				    }
				});
			    subpanel2.add( goButton );
			    subpanel2.add( viewButton );
			    subpanel2.add( showLabelsButton );
			    subpanel2.add( clearButton );
			    subpanel2.add( new JLabel("Help:") );
			    subpanel2.add( helpParamsButton );
			    subpanel2.add( helpRepositoryButton );
			    gbc = Viewer.fillerGBC();	gbc.weighty=0; gbc.gridy=1;
			    panel.add(subpanel2, gbc );

			    gbc = Viewer.fillerGBC(); gbc.weighty=1; gbc.gridy=2;
			    panel.add(errorPanel, gbc);

			    // now some progress bars
			    JProgressBar progressBar1 = new JProgressBar();
			    JProgressBar progressBar2 = new JProgressBar();
			    JProgressBar progressBar3 = new JProgressBar();
			    ProgressCounter.setGraphicContext(new JProgressBar[]{progressBar1, progressBar2,progressBar3});
			    gbc = Viewer.fillerGBC();	gbc.weighty=0; gbc.gridy=3;
			    panel.add(progressBar1, gbc);
			    gbc = Viewer.fillerGBC(); gbc.weighty=0; gbc.gridy=4;
			    panel.add(progressBar2, gbc);
			    gbc = Viewer.fillerGBC(); gbc.weighty=0; gbc.gridy=5;
			    panel.add(progressBar3, gbc);

			    return panel;
			}
		    };
		v.setContent(this);
		String className = this.getClass().toString().substring("class ".length());
		ViewerFrame f = new ViewerFrame(className + ":  " + Version.getVersion(),v);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Use option -help for help");
	}
    }	       	
    public void noLabelsMessage(Console console) 
    {
	console.append("\nYou need to specify the labeled data you're using!\n"
		       +"Modify the 'labels' parameters under base parameters section\n"
		       +"of the parameter modification window.\n");
    }
    private void repositoryHelp(Console console)
    {
	console.append(
		       "The Minorthird repository is a collection of data previously labeled for extraction\n"+
		       "or classification learning. One version of the repository containing public data is on:\n"+
		       "   /afs/cs/project/extract-learn/repository.\n"+
		       "\n"+
		       "Your repository is now configured as follows:\n"+
		       "  "+FancyLoader.SCRIPTDIR_PROP+" => "+ FancyLoader.getProperty(FancyLoader.SCRIPTDIR_PROP)+"\n"+
		       "  "+FancyLoader.DATADIR_PROP+" => "+ FancyLoader.getProperty(FancyLoader.DATADIR_PROP)+"\n"+
		       "  "+FancyLoader.LABELDIR_PROP+" => "+ FancyLoader.getProperty(FancyLoader.LABELDIR_PROP)+"\n"+
		       "To change these parameters, put new values in a file \"data.properties\" on your classpath.\n"+
		       "\n"+
		       FancyLoader.SCRIPTDIR_PROP+" should contain bean shell scripts that return labeled datasets,\n"+
		       "encoded as TextLabels objects. Usually these load documents from the directory pointed to by\n"+
		       FancyLoader.DATADIR_PROP+" and load labels from the directory pointed to by\n"
		       +FancyLoader.LABELDIR_PROP+"\n\n"+
		       "Instead of using script names from repositories as the \"keys\" for the -labels options\n"+
		       "you can also use the name of (a) a directory containing XML-marked up data (b) the common stem\n" +
		       "\"foo\" of a pair of files foo.base and foo.labels or (c) the common stem of a pair foo.labels\n" +
		       "and foo, where foo is a directory.\n");
    }
}		



