package edu.cmu.minorthird.classify.transform;

import edu.cmu.minorthird.classify.*;
import edu.cmu.minorthird.classify.algorithms.random.Estimate;
import edu.cmu.minorthird.classify.algorithms.random.Estimators;
import edu.cmu.minorthird.classify.algorithms.random.Poisson;
import edu.cmu.minorthird.classify.algorithms.random.NegativeBinomial;
import org.apache.log4j.Logger;

import java.util.*;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;

/**
* @author Edoardo Airoldi  (eairoldi@cs.cmu.edu)
* Date: Mar 6, 2005
*/

public class D2TransformLearner implements InstanceTransformLearner
{
  private static final String defaultEncoding = "UTF8";
static private Logger log = Logger.getLogger(D2TransformLearner.class);
  private ExampleSchema schema;

  // re-initialized at each call of batchTrain(Dataset)
  private Map T1values;
  private Map muPosExamples;
  private Map deltaPosExamples;
  private Map muNegExamples;
  private Map deltaNegExamples;
  private Map featurePdf; // model for f: can be "Poisson" or "Negative-Binomial"
  private Map T1valuesMany;
  private ArrayList classParameters;
  private ArrayList featureGivenClassParameters;

  // defaults
  private double ALPHA; // tolerance level for the FDR in selecting features
  private int MIN_WORDS; // minimum number of features to keep, EVEN IF NOT all significant
  private int MAX_WORDS; // maximum number of features to keep, EVEN IF MORE are significant
  private int SAMPLE; // points sampled to estimate T1's PDF, and compute p-values

  private double REF_LENGTH; // word-length of the reference document
  private String PDF; // distribution for D^2 p-values
  private String WHAT_IF_MANY_CLASSES; // "max" or "sum", only relevant for multiple-class schemas
  private String APPROX; // "sample" or "delta-method" or "saddle-point"

  public D2TransformLearner()
  {
     this.ALPHA = 0.05;
     this.MIN_WORDS = 5;  // corresponding to indices 0,1,2,3,4
     this.MAX_WORDS = Integer.MAX_VALUE; // that is, all features in Dataset
     this.SAMPLE = 2500; // this is fast, but for rare features may be better 10000 or 100000

     this.PDF = "Poisson";
     this.APPROX = "sample";
     this.REF_LENGTH = 100.0;
     this.WHAT_IF_MANY_CLASSES = "max";
  }

  /** ExampleSchema not used here ... */
  public void setSchema(ExampleSchema schema) {;}

  /** Examine data, build an instance transformer */
  public InstanceTransform batchTrain(Dataset dataset)
  {
     InitReset();
     this.schema = dataset.getSchema();
     int numberOfClasses = schema.getNumberOfClasses();

     if (ExampleSchema.BINARY_EXAMPLE_SCHEMA.equals(schema))
     {
        //
        // binary class case

        BasicFeatureIndex index = new BasicFeatureIndex(dataset);
        double featurePrior = 1.0/index.numberOfFeatures();
        // loop features
        for (Feature.Looper i=index.featureIterator(); i.hasNext(); ) {
           Feature f = i.nextFeature();
           // fill array of <counts_ex(feature), length_ex>
           double[] x = new double[dataset.size()];
           double[] omega = new double[dataset.size()];
           int position=0;
           for (Example.Looper j=dataset.iterator(); j.hasNext(); )
           {
              Example e = j.nextExample();
              x[position] = e.getWeight(f);
              omega[position] = getLength(e); // / REF_LENGTH; // normalization happens in Estimators
              position += 1;
           }
          /* // fill array of <counts_ex(feature), length_ex> for POS class
           double[] xPos = new double[ index.size(f,"POS") ];
           double[] omegaPos = new double[ index.size(f,"POS") ];
           int position=0;
           for (int j=0; j<index.size(f); j++) {
              Example e = index.getExample(f,j);
              if ( "POS".equals( e.getLabel().bestClassName() ) ) {
                 xPos[position] = e.getWeight(f);
                 omegaPos[position] = getLength(e); // / REF_LENGTH; // normalization happens in Estimators
                 position += 1;
              }
           }
           // fill array of <counts(example,feature), length(example)> for NEG class
           double[] xNeg = new double[ index.size(f,"NEG") ];
           double[] omegaNeg = new double[ index.size(f,"NEG") ];
           position=0;
           for (int j=0; j<index.size(f); j++) {
              Example e = index.getExample(f,j);
              if ( "NEG".equals( e.getLabel().bestClassName() ) ) {
                 xNeg[position] = e.getWeight(f);
                 omegaNeg[position] = getLength(e); // / REF_LENGTH; // normalization happens in Estimators
                 position += 1;
              }
           }*/
           // estimate Parameters for the two classes and update the T1-Filter
           setT1( f,T1((int)index.getCounts(f,"POS"),(int)index.getCounts(f,"NEG")) );
           if ( PDF.equals("Poisson") )
           {
              // learn Poisson parameters
              Estimate est = Estimators.estimatePoissonWeightedLambda( x,omega,featurePrior,REF_LENGTH );
              double mu = ((Double)est.getPms().get("lambda")).doubleValue();
              setPosMu( f,mu ); // overall rate of occurrence
              setFeaturePdf( f,"Poisson" );

           } else if ( PDF.equals("Negative-Binomial") )
           {
              throw new UnsupportedOperationException("error: PDF \""+PDF+"\" is not implemented!");
           }
           /*if ( PDF.equals("Poisson") )
           {
              // learn Poisson parameters
              Estimate estPos = Estimators.estimatePoissonWeightedLambda( xPos,omegaPos,featurePrior,REF_LENGTH );
              Estimate estNeg = Estimators.estimatePoissonWeightedLambda( xNeg,omegaNeg,featurePrior,REF_LENGTH );
              double muPos = ((Double)estPos.getPms().get("lambda")).doubleValue();
              double muNeg = ((Double)estNeg.getPms().get("lambda")).doubleValue();
              //System.out.println("ft = "+f+" :: mu+ = "+muPos+", mu- = "+muNeg);
              // update T1 Filter
              setPosMu( f,muPos );
              setNegMu( f,muNeg );
              setFeaturePdf( f,"Poisson" );

           } else if ( PDF.equals("Negative-Binomial") )
           {
              // learn Negative-Binomial parameters
              Estimate estPos = Estimators.estimateNegativeBinomialMuDelta( xPos,omegaPos,featurePrior,REF_LENGTH );
              Estimate estNeg = Estimators.estimateNegativeBinomialMuDelta( xNeg,omegaNeg,featurePrior,REF_LENGTH );
              // update T1 Filter
              setPosMu( f,((Double)estPos.getPms().get("mu")).doubleValue() );
              setPosDelta( f,((Double)estPos.getPms().get("delta")).doubleValue() );
              setNegMu( f,((Double)estNeg.getPms().get("mu")).doubleValue() );
              setNegDelta( f,((Double)estNeg.getPms().get("delta")).doubleValue() );
              setFeaturePdf( f,"Negative-Binomial");
           } */
        }

        // end of binary class case
        //
     }
     else
     {
        //
        // multiple-class case

        BasicFeatureIndex index = new BasicFeatureIndex(dataset);
        ArrayList featureMatrix = new ArrayList();
        ArrayList exampleWeightMatrix = new ArrayList();
        String[] classLabels = new String[numberOfClasses];
        int[] classSizes = new int[numberOfClasses];
        for (int i=0; i<numberOfClasses; i++)
        {
           classLabels[i]=schema.getClassName(i);
           classSizes[i] = index.size(classLabels[i]);
           double[] featureCounts = new double[ classSizes[i] ];
           double[] exampleWeights = new double[ classSizes[i] ];
           featureMatrix.add(featureCounts);
           exampleWeightMatrix.add(exampleWeights);
        }
        // count occurrences of features given class & example weights given class
        double numberOfExamples = ((double)dataset.size());
        double[] countsGivenClass = new double[numberOfClasses];
        double[] examplesGivenClass = new double[numberOfClasses];
        int[] excounter = new int[numberOfClasses];
        for(Example.Looper i=dataset.iterator(); i.hasNext(); )
        {
           Example ex = i.nextExample();
           //System.out.println("label="+ex.getLabel().bestClassName().toString());
           int idx = schema.getClassIndex( ex.getLabel().bestClassName().toString() );
           examplesGivenClass[ idx ] += 1.0;
           for (Feature.Looper j=index.featureIterator(); j.hasNext();)
           {
              Feature f = j.nextFeature();
              countsGivenClass[ idx ] += ex.getWeight(f);
              ((double[])exampleWeightMatrix.get(idx))[ excounter[idx] ] += ex.getWeight(f); // SCALE is HERE !!!
           }
           excounter[idx] += 1;
        }
        // estimate parameters of each features given class
        for( Feature.Looper floo=index.featureIterator(); floo.hasNext(); )
        {
           int[] counter = new int[numberOfClasses];
           double[] sums = new double[numberOfClasses];

           // load vector of counts (by class) for feature f
           Feature ft = floo.nextFeature();
           for( Example.Looper eloo=dataset.iterator(); eloo.hasNext(); )
           {
              Example ex = eloo.nextExample();
              int idx = schema.getClassIndex( ex.getLabel().bestClassName().toString() );
              ((double[])featureMatrix.get(idx))[ counter[idx]++ ] = ex.getWeight(ft);
           }

           if (PDF.equals("Poisson"))
           {
              double featurePrior = 1.0/index.numberOfFeatures();
              for (int j=0; j<numberOfClasses; j++)
              {
                 Estimate est = Estimators.estimateNaiveBayesMean( 1.0,(double)numberOfClasses,examplesGivenClass[j],numberOfExamples );
                 double probabilityOfOccurrence = ((Double)est.getPms().get("mean")).doubleValue();
                 setClassParameter( j,probabilityOfOccurrence );

                 double[] countsFeatureGivenClass = (double[])featureMatrix.get(j);
                 sums[j] = Estimators.Sum(countsFeatureGivenClass);
                 double[] countsGivenExample = (double[])exampleWeightMatrix.get(j);
                 Estimate lambda = Estimators.estimatePoissonWeightedLambda( countsFeatureGivenClass,countsGivenExample,featurePrior,REF_LENGTH );
                 setFeatureGivenClassParameter( ft,j,lambda );
              }
              setFeaturePdf( ft,"Poisson" );
           }
           else if (PDF.equals("Negative-Binomial"))
           {
              double featurePrior = 1.0/index.numberOfFeatures();
              for (int j=0; j<numberOfClasses; j++)
              {
                 Estimate est = Estimators.estimateNaiveBayesMean( 1.0,(double)numberOfClasses,examplesGivenClass[j],numberOfExamples );
                 double probabilityOfOccurrence = ((Double)est.getPms().get("mean")).doubleValue();
                 setClassParameter( j,probabilityOfOccurrence );

                 double[] countsFeatureGivenClass = (double[])featureMatrix.get(j);
                 sums[j] = Estimators.Sum(countsFeatureGivenClass);
                 double[] countsGivenExample = (double[])exampleWeightMatrix.get(j);
                 Estimate mudelta = Estimators.estimateNegativeBinomialMuDelta( countsFeatureGivenClass,countsGivenExample,featurePrior,REF_LENGTH );
                 setFeatureGivenClassParameter( ft,j,mudelta );
              }
              setFeaturePdf( ft,"Negative-Binomial");
           }
           // store T1 values for each pair of indices
           for (int ci=0; ci<numberOfClasses; ci++) {
              for (int cj=(ci+1); cj<numberOfClasses; cj++) {
                 double t1 = T1((int)sums[ci],(int)sums[cj]);
                 setT1many(ft,t1,ci,cj);
              }
           }
        }

        // end of multiple-class class case
        //

     }

     //
     // compute D^2 p-values

     List pValue = new ArrayList();

     if (ExampleSchema.BINARY_EXAMPLE_SCHEMA.equals(schema))
     {
        //
        // binary class case

        if ( APPROX.equals("sample") )
        {
           BasicFeatureIndex index = new BasicFeatureIndex(dataset);
           // loop features
           for (Feature.Looper i=index.featureIterator(); i.hasNext(); )
           {
              Feature f = i.nextFeature();
              double[] T1array = sampleT1Values( f ); // Sample T1 values
              //double[] T1array = sampleT1Values2( f ); // Sample T1 values
              Pair p = computePValue( T1array,f ); // compute p-value for Feature f
              pValue.add( p );
              //System.out.println( p );
           }

        }
        else if ( APPROX.equals("delta-method") )
        {
           throw new UnsupportedOperationException("error: Approximation \""+APPROX+"\" is not implemented!");
        }
        else if ( APPROX.equals("saddle-point") )
        {
           throw new UnsupportedOperationException("error: Approximation \""+APPROX+"\" is not implemented!");
        }
        else
        {
           throw new UnsupportedOperationException("error: Approximation \""+APPROX+"\" is not recognized!");
        }

        // end of binary class case
        //

     } else {

        //
        // multiple-class class case

        if ( WHAT_IF_MANY_CLASSES.equals("max") & APPROX.equals("sample") )
        {
           BasicFeatureIndex index = new BasicFeatureIndex(dataset);
           // loop features
           for (Feature.Looper i=index.featureIterator(); i.hasNext(); )
           {
              Feature f = i.nextFeature();
              Pair maxPair = new Pair(1.0,f);
              for (int ci=0; ci<numberOfClasses; ci++) {
                 for (int cj=(ci+1); cj<numberOfClasses; cj++) {
                    double[] T1array = sampleT1Values( f, ci,cj ); // Sample T1 values
                    Pair p = computePValue( T1array,f, ci,cj ); // compute p-value for Feature f
                    if (p.value<maxPair.value) { maxPair=p; }
                 }
              }
              pValue.add( maxPair );
           }

        }
        else if ( WHAT_IF_MANY_CLASSES.equals("sum") )
        {
           throw new UnsupportedOperationException("error: D^2 extension \""+WHAT_IF_MANY_CLASSES+"\" is not implemented!");
        }
        else
        {
           throw new UnsupportedOperationException("error: D^2 extension \""+WHAT_IF_MANY_CLASSES+"\" is not recognized!");
        }

        // end of multiple-class class case
        //

     }

     // compute D^2 p-values
     //

     //
     // find relevent features using D^2 p-values and FDR correction

     final Comparator VAL_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
           Pair p1 = (Pair)o1;
           Pair p2 = (Pair)o2;
           if (p1.value<p2.value) return -1;
           else if (p1.value>p2.value) return 1;
           else return (p1.feature).compareTo( p2.feature );
        }
     };
     final TreeMap availableFeatures = selectFeaturesViaFDR( pValue,VAL_COMPARATOR );

     // find relevent features using D^2 p-values and FDR correction
     //

     //
     // build an InstanceTransform that removes low-frequency features

     return new AbstractInstanceTransform() {
        public Instance transform(Instance instance) {
           return new MaskedInstance(instance, availableFeatures);
        }
        public String toString() {
           return "[InstanceTransform: model = "+PDF+" by D^2]";
        }
     };
  }

  //
  // getters / setters
  //

  /** Set the PDF for feature f */
  public void setFeaturePdf(Feature f,String pdf) {
     featurePdf.put(f, new String(pdf));
  }

  //
  // for binary case

  /** Set the value of T1 corresponding to feature f */
  private void setT1(Feature f,double delta) {
     Double d = (Double)T1values.get(f);
     if (d==null) T1values.put(f, new Double(delta));
     else System.out.println("Warning: T1 value already set for feature " + f.toString() + "!");
  }

  /** Set mu corresponding to the Positive examples of feature f */
  private void setPosMu(Feature f,double delta) {
     Double d = (Double)muPosExamples.get(f);
     if (d==null) muPosExamples.put(f, new Double(delta));
     else muPosExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  private double getPosMu( Feature f) {
     Double d = (Double)muPosExamples.get( f );
     if (d==null) return 0.0;
     else return d.doubleValue();
  }

  /** Set mu corresponding to the Positive examples of feature f */
  private void setNegMu(Feature f,double delta) {
     Double d = (Double)muNegExamples.get(f);
     if (d==null) muNegExamples.put(f, new Double(delta));
     else muNegExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  private double getNegMu( Feature f) {
     Double d = (Double)muNegExamples.get( f );
     if (d==null) return 0.0;
     else return d.doubleValue();
  }

  /** Set mu corresponding to the Positive examples of feature f */
  private void setPosDelta(Feature f,double delta) {
     Double d = (Double)deltaPosExamples.get(f);
     if (d==null) deltaPosExamples.put(f, new Double(delta));
     else deltaPosExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  private double getPosDelta( Feature f) {
     Double d = (Double)deltaPosExamples.get( f );
     if (d==null) return 0.0;
     else return d.doubleValue();
  }

  /** Set mu corresponding to the Positive examples of feature f */
  private void setNegDelta(Feature f,double delta) {
     Double d = (Double)deltaNegExamples.get(f);
     if (d==null) deltaNegExamples.put(f, new Double(delta));
     else deltaNegExamples.put(f, new Double(d.doubleValue()+delta));
  }

  /** Get mu corresponding to the Positive examples of feature f */
  private double getNegDelta( Feature f) {
     Double d = (Double)deltaNegExamples.get( f );
     if (d==null) return 0.0;
     else return d.doubleValue();
  }

  //
  // for multi-class case

  /** Set the value of T1 corresponding to feature f and classes (i,j) */
  private void setT1many(Feature f,double delta, int i, int j) {
     Double[][] d = (Double[][])T1valuesMany.get(f);
     if (d==null) {
        int N = schema.getNumberOfClasses();
        d = new Double[N][N];
        d[i][j] = new Double(delta);
        T1valuesMany.put(f, d);
     }
     else if (d[i][j]==null)
     {
        d[i][j] = new Double(delta);
     }
     else System.out.println("Warning: T1 value already set for feature " + f.toString() + "!");
  }

  /** Set probability of class j to d */
  public void setClassParameter(int j, double d)
  {
     try {
        classParameters.get( j );
     } catch (Exception x) {
        classParameters.add( j,new Double(d) );
        //System.out.println(". added in "+j+" >> pi="+d);
     }
  }

  public void setFeatureGivenClassParameter(Feature f, int j, Estimate pms)
  {
     HashMap hmap;
     try
     {
        hmap = (HashMap)featureGivenClassParameters.get(j);
        hmap.put( f,pms );
        featureGivenClassParameters.set( j,hmap );
     }
     catch (Exception NoHashMapforClassJ)
     {
        hmap = null;
        hmap = new HashMap();
        hmap.put( f,pms ) ;
        featureGivenClassParameters.add( j,hmap );
     }
  }

  //
  // convenience methods
  //

  /** Initializes or Resets the contents of D2 */
  private void InitReset()
  {
     this.T1values = new TreeMap();
     this.muPosExamples = new TreeMap();
     this.muNegExamples = new TreeMap();
     this.deltaPosExamples = new TreeMap();
     this.deltaNegExamples = new TreeMap();
     this.featurePdf = new TreeMap();

     this.classParameters = new ArrayList();
     this.T1valuesMany = new TreeMap();
     this.featureGivenClassParameters = new ArrayList();
     this.featureGivenClassParameters.add( new WeightedSet() );
  }

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

  private TreeMap selectFeaturesViaFDR(List pValue, final Comparator VAL_COMPARATOR)
  {
     TreeMap availableFeatures = new TreeMap();
     Collections.sort( pValue, VAL_COMPARATOR);
     int greatestIndexBeforeAccept = -1; // does not return any word at -1
     for (int j=1; j<=pValue.size(); j++) {
        double line = ((double)j) * ALPHA / ((double)pValue.size());
        if ( line>((Pair)pValue.get(j-1)).value ) { greatestIndexBeforeAccept = j-1; }
     }
     //System.out.println("max index = "+greatestIndexBeforeAccept);
     //System.out.println("total words = "+pValue.size());
     greatestIndexBeforeAccept = Math.min( MAX_WORDS,Math.min( pValue.size()-1,Math.max( greatestIndexBeforeAccept,MIN_WORDS ) ) );
     System.out.println("Retained "+(greatestIndexBeforeAccept+1)+" fetures, out of "+pValue.size());
     for (int j=0; j<=greatestIndexBeforeAccept; j++) {
        //System.out.println("word="+((Pair)pValue.get(j)).feature+", p-value="+((Pair)pValue.get(j)).value);
        availableFeatures.put( ((Pair)pValue.get(j)).feature,new Integer(1) );
     }
     return availableFeatures;
  }

  private double[] sampleT1Values2(Feature f)
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

  private double[] sampleT1Values(Feature f)
  {
     double[] T1array = new double[ SAMPLE ];
     String s = (String)featurePdf.get(f);
     // Sample from PDF of Feature f
     if ( s.equals("Poisson") ) {
        Poisson Xp = new Poisson( getPosMu(f) );
        Poisson Xn = new Poisson( getPosMu(f) );
        for (int cnt=0; cnt<SAMPLE; cnt++) {
           T1array[cnt] = T1( Xp.nextInt(),Xn.nextInt() );
        }
     } else if ( s.equals("Negative-Binomial") ) {
        throw new UnsupportedOperationException("error: PDF \""+PDF+"\" is not implemented!");
     } else {
        throw new IllegalStateException("Error: PDF not recognized!");
     }
     return T1array;
  }

  private double[] sampleT1Values(Feature f, int ci, int cj)
  {
     double[] T1array = new double[ SAMPLE ];
     String s = (String)featurePdf.get(f);
     // Sample from PDF of Feature f
     if ( s.equals("Poisson") ) {
        Estimate esti = (Estimate) ((HashMap)featureGivenClassParameters.get(ci)).get(f);
        Estimate estj = (Estimate) ((HashMap)featureGivenClassParameters.get(cj)).get(f);
        TreeMap pmi = esti.getPms();
        TreeMap pmj = estj.getPms();
        double lambdai = ((Double)pmi.get("lambda")).doubleValue();
        double lambdaj = ((Double)pmj.get("lambda")).doubleValue();
        double pci = ((Double)classParameters.get(ci)).doubleValue();
        double pcj = ((Double)classParameters.get(cj)).doubleValue();
        double lambda = pci/(pci+pcj)*lambdai + pcj/(pci+pcj)*lambdaj;
        Poisson Xi = new Poisson( lambda );
        Poisson Xj = new Poisson( lambda );
        for (int cnt=0; cnt<SAMPLE; cnt++) {
           T1array[cnt] = T1( Xi.nextInt(),Xj.nextInt() );
        }
     } else if ( s.equals("Negative-Binomial") ) {
        Estimate esti = (Estimate) ((HashMap)featureGivenClassParameters.get(ci)).get(f);
        Estimate estj = (Estimate) ((HashMap)featureGivenClassParameters.get(cj)).get(f);
        TreeMap pmi = esti.getPms();
        TreeMap pmj = estj.getPms();
        TreeMap npi = mudelta2np( ((Double)pmi.get("mu")).doubleValue(),((Double)pmi.get("delta")).doubleValue(),1.0 );
        TreeMap npj = mudelta2np( ((Double)pmj.get("mu")).doubleValue(),((Double)pmj.get("delta")).doubleValue(),1.0 );
        NegativeBinomial Xi = new NegativeBinomial( ((Integer)(npi.get("n"))).intValue(),((Double)(npi.get("p"))).doubleValue() );
        NegativeBinomial Xj = new NegativeBinomial( ((Integer)(npj.get("n"))).intValue(),((Double)(npj.get("p"))).doubleValue() );
        for (int cnt=0; cnt<SAMPLE; cnt++) {
           T1array[cnt] = T1( Xi.nextInt(),Xj.nextInt() );
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

  private Pair computePValue(double[] t1array, Feature f, int ci, int cj)
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
        Double[][] observedT1 = (Double[][])T1valuesMany.get(f);
        if ( t1array[j]<observedT1[ci][cj].doubleValue() ) greatestIndexBeforeT1Observed = j;
     }
     Pair p = new Pair( ((double)(newLength-greatestIndexBeforeT1Observed))/((double)newLength),f );
     return p;
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

  /** Get the total number of words in an Example */
  public double getLength(Example e) {
     double len=0.0;
     for (Feature.Looper i=e.featureIterator(); i.hasNext(); ) {
        Feature f = i.nextFeature();
        len += e.getWeight(f);
     }
     return len;
  }

  /** Compute the T1 statistic corresponding to the counts in two texts */
  public double T1(int x1, int x2) {
     double dx1 = new Integer(x1).doubleValue();
     double dx2 = new Integer(x2).doubleValue();
     double t = Math.pow( (dx1-dx2),2 ) / (dx1+dx2);
     return t;
  }

  //
  // Accessory Methods
  //

  /** Set REF_LENGTH to the desired value */
  public void setREF_LENGTH(double desiredLength) {
     REF_LENGTH = desiredLength;
  }

  /** Set PDF to the desired value */
  public void setPDF(String pdf) {
     PDF = pdf;
  }

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
  // Test D^2 statistics

  static public void main(String[] args)
  {
     // define file's locations here
     String path = "/Users/eairoldi/cmu.research/8.Text.Learning.Group/src.MISC/";
     String fout = "eda-test2.txt";

     try {

        File outFile = new File(path+fout);
        
        PrintStream out = new PrintStream(new FileOutputStream(outFile), false, defaultEncoding );

        //
        // CP data & delta^2 stat

        //File dataFile = new File(path+"CPdata1.m3rd");
        File dataFile = new File(path+"webmaster.3rd");
        Dataset data = DatasetLoader.loadFile( dataFile );

        D2TransformLearner f = new D2TransformLearner();
        f.setREF_LENGTH(100.0);
        f.setSAMPLE(10000);
        f.setALPHA(0.01);
        f.setMIN_WORDS(14);
        f.setMAX_WORDS(14);

        InstanceTransform d2 = f.batchTrain( data );
        data = d2.transform( data );

        BasicFeatureIndex idx = new BasicFeatureIndex(data);
        for (Iterator i=idx.featureIterator(); i.hasNext();)
        {
           Feature ft = (Feature) i.next();
           System.out.println(ft);
        }

     } catch (Exception x) {
        System.out.println( "error:\n"+x );
     }
  }

}