package edu.cmu.minorthird.classify.transform;

import edu.cmu.minorthird.classify.*;
import edu.cmu.minorthird.classify.algorithms.random.NegativeBinomial;
import edu.cmu.minorthird.classify.algorithms.random.Poisson;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import java.util.*;

/**
 * @author Edoardo Airoldi
 * Date: Nov 24, 2003
 */

public class T1InstanceTransform implements InstanceTransform {

  static private Logger log = Logger.getLogger(T1InstanceTransform.class);

  private double ALPHA; // tolerance level for the FDR in selecting features
  private int MIN_WORDS; // minimum number of features to keep, EVEN IF NOT significant
  private int MAX_WORDS; // minimum number of features to keep, EVEN IF NOT significant
  private int SAMPLE; // points sampled to estimate T1's PDF, and compute p-values

  private Map T1values;

  private Map muPosExamples;
  private Map deltaPosExamples;
  private Map muNegExamples;
  private Map deltaNegExamples;
  private Map featurePdf; // model for f: can be "Poisson" or "Negative-Binomial"

  public T1InstanceTransform() {
    this.ALPHA = 0.05;
    this.MIN_WORDS = 50;  // 0,...,49
    this.MAX_WORDS = Integer.MAX_VALUE;
    this.SAMPLE = 2500;
    this.T1values = new TreeMap();
    this.muPosExamples = new TreeMap();
    this.muNegExamples = new TreeMap();
    this.deltaPosExamples = new TreeMap();
    this.deltaNegExamples = new TreeMap();
    this.featurePdf = new TreeMap();
  }


  /** Create a transformed copy of the instance. */
  public Instance transform(Instance instance) {
    System.out.println("Warning: cannot transform instance with T1 Statistic!");
    return instance;
  }

  /** Create a transformed copy of the example. */
  public Example transform(Example example){
    System.out.println("Warning: cannot transform example with T1 Statistic!");
    return example;
  }

  /** Create a transformed copy of a dataset. */
  public Dataset transform(Dataset dataset){
    BasicFeatureIndex index = new BasicFeatureIndex(dataset);

    final Comparator VAL_COMPARATOR = new Comparator() {
      public int compare(Object o1, Object o2) {
        Pair p1 = (Pair)o1;
        Pair p2 = (Pair)o2;
        if (p1.value<p2.value) return -1;
        else if (p1.value>p2.value) return 1;
        else return (p1.feature).compareTo( p2.feature );
      }
    };

    // Initialize pValues
    List pValue = new ArrayList();

    // loop features
    for (Feature.Looper i=index.featureIterator(); i.hasNext(); )
    {
      Feature f = i.nextFeature();
      // Sample T1 values
      double[] T1array = sampleT1Values( f );
      // compute p-value for Feature f
      Pair p = computePValue( T1array,f );
      pValue.add( p );
    }

    // FDR correction to decide which features to retain
    TreeMap availableFeatures = selectFeaturesViaFDR( pValue,VAL_COMPARATOR );
    // create transformed dataset
    BasicDataset maskeDataset = createMaskedDataset( dataset,availableFeatures );

    return maskeDataset;
  }

  private BasicDataset createMaskedDataset(Dataset dataset, TreeMap availableFeatures)
  {
    BasicDataset maskeDataset = new BasicDataset();
    for (Example.Looper i=dataset.iterator(); i.hasNext(); ) {
      Example e = i.nextExample();
      Instance mi = new MaskedInstance( e.asInstance(),availableFeatures );
      Example ex = new Example( mi,e.getLabel() );
      maskeDataset.add( ex );
    }
    return maskeDataset;
  }

  private TreeMap selectFeaturesViaFDR(List pValue, final Comparator VAL_COMPARATOR)
  {
    TreeMap availableFeatures = new TreeMap();
    Collections.sort( pValue, VAL_COMPARATOR);
    int greatestIndexBeforeAccept = -1; // does not return any word at -1
    for (int j=1; j<=pValue.size(); j++) {
      double line = ((double)j) * ALPHA / ((double)pValue.size());
      if ( line>((Pair)pValue.get(j-1)).value ) greatestIndexBeforeAccept = j-1;
    }
    //System.out.println("max index = "+greatestIndexBeforeAccept);
    //System.out.println("total words = "+pValue.size());
    greatestIndexBeforeAccept = Math.min( MAX_WORDS,Math.min( pValue.size()-1,Math.max( greatestIndexBeforeAccept,MIN_WORDS ) ) );
    System.out.println("Retained "+greatestIndexBeforeAccept+" fetures, out of "+pValue.size());
    for (int j=0; j<=greatestIndexBeforeAccept; j++) {
      availableFeatures.put( ((Pair)pValue.get(j)).feature,new Integer(1) );
    }
    return availableFeatures;
  }

  private double[] sampleT1Values(Feature f)
  {
    double[] T1array = new double[ SAMPLE ];
    String s = (String)featurePdf.get(f);
    // Sample from PDF of Feature f
    if ( s.equals("Poisson") ) {
      Poisson Xp = new Poisson( getPosMu(f) );
      Poisson Xn = new Poisson( getNegMu(f) );
      for (int cnt=0; cnt<SAMPLE; cnt++) {
        T1array[cnt] = T1( Xp.nextInt(),Xn.nextInt() );
      }
    } else if ( s.equals("Negative-Binomial") ) {
      TreeMap npPos = mudelta2np( getPosMu(f),getPosDelta(f),1.0 );
      TreeMap npNeg = mudelta2np( getNegMu(f),getNegDelta(f),1.0 );
      NegativeBinomial Xp = new NegativeBinomial( ((Integer)(npPos.get("n"))).intValue(),((Double)(npPos.get("p"))).doubleValue() );
      NegativeBinomial Xn = new NegativeBinomial( ((Integer)(npNeg.get("n"))).intValue(),((Double)(npNeg.get("p"))).doubleValue() );
      for (int cnt=0; cnt<SAMPLE; cnt++) {
        T1array[cnt] = T1( Xp.nextInt(),Xn.nextInt() );
      }
    } else {
      throw new IllegalStateException("Error: PDF not implemented!");
    }
    return T1array;
  }

  private Pair computePValue(double[] t1array, Feature f)
  {
    Arrays.sort( t1array );
    int newLength = 0;
    for (int j=0; j<t1array.length; j++) {
      if ( new Double(t1array[j]).isNaN() ) {
        newLength = j;
        break;
      } else {
        newLength = t1array.length;
      }
    }
    int greatestIndexBeforeT1Observed = 0;
    for (int j=0; j<t1array.length; j++) {
      if (t1array[j]<((Double)T1values.get(f)).doubleValue()) greatestIndexBeforeT1Observed = j;
    }
    Pair p = new Pair( ((double)(newLength-greatestIndexBeforeT1Observed))/((double)newLength),f );
    return p;
  }


  //
  // Accessory Methods
  //

  /** Set MIN_WORDS to the desired number */
  public void setMIN_WORDS(int number) {
    this.MIN_WORDS = number;
  }

  /** Set MAX_WORDS to the desired number */
  public void setMAX_WORDS(int number) {
    this.MAX_WORDS = number;
  }

  /** Set SAMPLE SIZE to the desired level */
  public void setSAMPLE(int size) {
    this.SAMPLE = size;
  }

  /** Set ALPHA to the desired level */
  public void setALPHA(double desiredLevel) {
    this.ALPHA = desiredLevel;
  }


  //
  // Other Stuff
  //

  /** A class that we use to sort a TreeMap by values */
  private class Pair extends Object {
    double value;
    Feature feature;

    public Pair(double v, Feature f) {
      this.value = v;
      this.feature = f;
    }

    public String toString() {
      return "[ " + this.value + "," + this.feature + " ]"; //this.key + " ]";
    }
  }

  /** Set the PDF for feature f */
  public void setFeaturePdf(Feature f,String pdf) {
    featurePdf.put(f, new String(pdf));
  }

  /** Set the value of T1 corresponding to feature f */
  public void setT1(Feature f,double delta) {
    Double d = (Double)T1values.get(f);
    if (d==null) T1values.put(f, new Double(delta));
    else System.out.println("Warning: T1 value already set for feature " + f.toString() + "!");
  }

  /** Set mu corresponding to the Positive examples of feature f */
  public void setPosMu(Feature f,double delta) {
    Double d = (Double)muPosExamples.get(f);
    if (d==null) muPosExamples.put(f, new Double(delta));
    else muPosExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  public double getPosMu( Feature f) {
    Double d = (Double)muPosExamples.get( f );
    if (d==null) return 0.0;
    else return d.doubleValue();
  }

  /** Set mu corresponding to the Positive examples of feature f */
  public void setNegMu(Feature f,double delta) {
    Double d = (Double)muNegExamples.get(f);
    if (d==null) muNegExamples.put(f, new Double(delta));
    else muNegExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  public double getNegMu( Feature f) {
    Double d = (Double)muNegExamples.get( f );
    if (d==null) return 0.0;
    else return d.doubleValue();
  }

  /** Set mu corresponding to the Positive examples of feature f */
  public void setPosDelta(Feature f,double delta) {
    Double d = (Double)deltaPosExamples.get(f);
    if (d==null) deltaPosExamples.put(f, new Double(delta));
    else deltaPosExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  public double getPosDelta( Feature f) {
    Double d = (Double)deltaPosExamples.get( f );
    if (d==null) return 0.0;
    else return d.doubleValue();
  }

  /** Set mu corresponding to the Positive examples of feature f */
  public void setNegDelta(Feature f,double delta) {
    Double d = (Double)deltaNegExamples.get(f);
    if (d==null) deltaNegExamples.put(f, new Double(delta));
    else deltaNegExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  public double getNegDelta( Feature f) {
    Double d = (Double)deltaNegExamples.get( f );
    if (d==null) return 0.0;
    else return d.doubleValue();
  }

  public double T1(int x1, int x2) {
    double dx1 = new Integer(x1).doubleValue();
    double dx2 = new Integer(x2).doubleValue();
    double t = Math.pow( (dx1-dx2),2 ) / (dx1+dx2);
    return t;
  }

  public TreeMap mudelta2np(double mu, double delta, double omega) {
    //System.out.println("mu="+mu +", delta="+delta);
    TreeMap np = new TreeMap();
    // from mu,delta to n
    int n = (int)Math.ceil(new Double(mu/delta).doubleValue());
    np.put( "n", new Integer(n) );
    // from mu,delta to p
    double p = omega*delta;
    np.put( "p", new Double(p) );
    //System.out.println("n="+n +", p="+p +", omega="+omega);
    return np;
  }

}
