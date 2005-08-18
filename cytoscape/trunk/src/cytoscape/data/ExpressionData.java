//ExpressionData.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//--------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------
package cytoscape.data;
//--------------------------------------------------------------------
import java.util.*;
import java.io.*;

import cytoscape.*;
import cytoscape.task.TaskMonitor;
import cytoscape.data.*;

import cytoscape.data.readers.*;
//--------------------------------------------------------------------
/**
 * This class provides a reader for the common file format for expression
 * data and an interface to access the data.<P>
 *
 *
 * There are variations in the file format used; the following assumptions
 * about the file format are considered valid. Attempting to read a file
 * that does not satisfy these assumptions is not guaranteed to work.<P>
 *
 * 1. A token is a consecutive sequence of alphanumeric characters separated
 *    by whitespace.<BR>
 * 2. The file consists of an arbitrary number of lines, each of which
 *    contains the same number of tokens (except for possibly the first line)
 *    and has a total length less than 8193 characters.<BR>
 * 3. The first line of the file is a header line with one of the following
 *    three formats:<P>
 *
 *    <text> <text> cond1 cond2 ... condN cond1 cond2 ... condN NumSigConds<P>
 *
 *    <text> <text> cond1 cond2 ... condN<P>
 * 
 *    <\t><\t>RATIOS<\t><\t>...LAMBDAS<P>
 *
 * Here cond1 through condN are the names of the conditions. In the first
 * case, the two sequences of condition names must match exactly in order
 * and lexicographically; each name among cond1 ... condN must be unique.
 * In the second case, each name must be unique, but need only appear once.
 * The last label, NumSigConds, is optional.<BR>
 * The third case is the standard header for a MTX file. The numer of '\t' 
 * characters between the words "RATIOS" and "LAMBDAS" is equal to the number
 * of ratio columns in the file (which must be equal to the number of lambda 
 * columns).<P>
 *
 * 4. Each successive line represents the measurements for a partcular gene,
 *    and has one of the following two formats, depending on the header:<P>
 *
 *   <FNAME> <CNAME> E E ... E S S ... S I<P>
 *
 *   <FNAME> <CNAME> E E ... E<P>
 *
 *  where <FNAME> is the formal name of the gene, <CNAME> is the common name,
 *  the E's are tokens, parsable as doubles, representing the expression
 *  level change for each condition, the S's are tokens parsable as doubles
 *  representing the statistical significance of the expression level change,
 *  and I is an optional integer giving the number of conditions in which
 *  the expression level change was significant for this gene.<P>
 *
 *  The first format is used in conjuction with the first or third header formats.
 *  The second format is used in conjunction with the second header format.<P>
 *
 * 5. An optional last line can be included with the following form:<P>
 *
 *  NumSigGenes: I I ... I<P>
 *
 *  where there are N I's, each an integer representing the number of
 *  significant genes in that condition.<P>
 */
public class ExpressionData implements Serializable{
  private TaskMonitor taskMonitor;

  public static final int MAX_LINE_SIZE = 8192;
  
  /**
   * Kinds of significance values
   */
  public static final int PVAL = 0;
  public static final int LAMBDA = 1;
  public static final int NONE = 2;
  public static final int UNKNOWN = 3;
 
  protected int significanceType = 3;

    String filename;
    int numGenes;
    int numConds;
    int extraTokens;
    boolean haveSigValues;

    Vector geneNames;
    Vector geneDescripts;
    Vector condNames;
    Hashtable geneNameToIndex;
    Hashtable condNameToIndex;
    double minExp;
    double maxExp;
    double minSig;
    double maxSig;
    Vector allMeasurements;

//--------------------------------------------------------------------

    public ExpressionData() {
	filename = null;
	numGenes = 0;
	numConds = 0;
	extraTokens = 0;
  haveSigValues = false;
	this.initDataStructures();
    }
    public ExpressionData(String filename) throws IOException {
	this.filename = null;
	numGenes = 0;
	numConds = 0;
	extraTokens = 0;
        haveSigValues = false;
	this.initDataStructures();
	this.loadData(filename);
    }

    public ExpressionData(String filename, TaskMonitor taskMonitor)
            throws IOException {
        this.taskMonitor = taskMonitor;
        this.filename = null;
        numGenes = 0;
        numConds = 0;
        extraTokens = 0;
            haveSigValues = false;
        this.initDataStructures();
        this.loadData(filename);
    }

  public String getFileName(){
    return filename;
  }

  public File getFullPath (){
    File file = new File (filename);
    return file.getAbsoluteFile ();
  }

    private void initDataStructures() {
	/* on overflow, capacity of vector will be increased by
	   "expand" elements all at once; much more efficient when
	   we don't know how many thousand genes are left in the file */
	int expand = 1000;
	if (geneNames != null) {geneNames.clear();}
	geneNames = new Vector(0,expand);
	if (geneDescripts != null) {geneDescripts.clear();}
	geneDescripts = new Vector(0,expand);
	if (condNames != null) {condNames.clear();}
	condNames = new Vector();
	if (geneNameToIndex != null) {geneNameToIndex.clear();}
	geneNameToIndex = new Hashtable();
	if (condNameToIndex != null) {condNameToIndex.clear();}
	condNameToIndex = new Hashtable();
	minExp = Double.MAX_VALUE;
	maxExp = Double.MIN_VALUE;
	minSig = Double.MAX_VALUE;
	maxSig = Double.MIN_VALUE;
	if (allMeasurements != null) {allMeasurements.clear();}
	allMeasurements = new Vector(0,expand);
    }

//--------------------------------------------------------------------

    public boolean oldLoadData(String filename) throws IOException {
	if (filename == null) {return false;}
	BufferedReader input;
    input = new BufferedReader(new FileReader(filename),
				       MAX_LINE_SIZE);

	String headerLine = this.readOneLine(input);
	if (isHeaderLineNull(headerLine,input,filename)) {return false;}
	// added by iliana (iavila@systemsbiology.org) on 11.25.2002
	if (isHeaderLineMTXHeader(headerLine)){ 
	    headerLine = this.readOneLine(input);
	}
	boolean expectPvals = doesHeaderLineHaveDuplicates(headerLine);
	StringTokenizer headerTok = new StringTokenizer(headerLine);
	int numTokens = headerTok.countTokens();
	// if we expect p-values, 4 is the minimum number.
	// if we don't, 3 is the minimum number.  Ergo:
	// either way, we need 3, and if we expectPvals, we need 4.
	if ((numTokens < 3) || ((numTokens<4)&&expectPvals)) {
        StringBuffer msg = new StringBuffer();
	    msg.append ("Bad header format in data file " + filename);
	    msg.append ("\nNumber of tokens parsed: " + numTokens);
	    for (int i=0; i<numTokens; i++) {
		    msg.append("\nToken " + i + ": " + headerTok.nextToken() );
	    }
        throw new IOException (msg.toString());
	}

	double tmpF = numTokens/2.0;
	int tmpI = (int)Math.rint(tmpF);
	int numberOfConditions;
	int haveExtraTokens = 0;
	if(expectPvals) {
	    if ( tmpI == tmpF ) {//missing numSigConds field
		numberOfConditions = (numTokens - 2) / 2;
		haveExtraTokens = 0;
	    } else {
		numberOfConditions = (numTokens - 3) / 2;
		haveExtraTokens = 1;
	    }
	}
	else { numberOfConditions = numTokens - 2; }

	/* eat the first two tokens from the header line */
	headerTok.nextToken();
	headerTok.nextToken();
	/* the next numConds tokens are the condition names */
	Vector cNames = new Vector(numberOfConditions);
	for (int i=0; i<numberOfConditions; i++) {
	    cNames.add( headerTok.nextToken() );
	}
	/* the next numConds tokens should duplicate the previous list
	   of condition names */
	if(expectPvals) {
	    for (int i=0; i<numberOfConditions; i++) {
		String title = headerTok.nextToken();
		if ( !(title.equals( cNames.get(i) )) ) {
            StringBuffer msg = new StringBuffer();
		    msg.append ("Expecting both ratios and p-values.\n");
		    msg.append ("Condition name mismatch in header line"
				       + " of data file " + filename + ": "
				       + cNames.get(i) + " vs. " + title);
		    throw new IOException (msg.toString());
		}
	    }
	}

	/* OK, we have a reasonable header; clobber all old information */
	this.filename = filename;
	this.numConds = numberOfConditions;
	this.extraTokens = haveExtraTokens;
	/* wipe old data */
	initDataStructures();
	/* store condition names */
	condNames = cNames;
	for (int i=0; i<numConds; i++) {
	    condNameToIndex.put( condNames.get(i), new Integer(i) );
	}

	/* parse rest of file line by line */
	String oneLine = this.readOneLine(input);
	int lineCount = 1;
	while (oneLine != null) {
	    lineCount++;
	    parseOneLine(oneLine,lineCount,expectPvals);
	    oneLine = this.readOneLine(input);
	}

	/* save numGenes and build hash of gene names to indices */
	this.numGenes = geneNames.size();
	for (int i=0; i<geneNames.size(); i++) {
	    geneNameToIndex.put( geneNames.get(i), new Integer(i) );
	}

	/* trim capacity of data structures for efficiency */
	geneNames.trimToSize();
	geneDescripts.trimToSize();
	allMeasurements.trimToSize();

	/* try to close file */
	try {
	    input.close();
	} catch (IOException e) {
	}

	return true;
    }//oldLoadData

//--------------------------------------------------------------------
public boolean loadData (String filename) throws IOException {
  if (filename == null) 
   return false;

  String rawText;
    if (filename.trim().startsWith ("jar://")) {
      TextJarReader reader = new TextJarReader (filename);
      reader.read ();
      rawText = reader.getText ();
      }
    else {
      TextFileReader reader = new TextFileReader (filename);
      reader.read ();
      rawText = reader.getText ();
      }
  String [] lines = rawText.split ("\n");

  int lineCount = 0;
  String headerLine = lines [lineCount++];
  if (headerLine == null || headerLine.length () == 0)
    return false;
  
  if (isHeaderLineMTXHeader (headerLine)){
    // for sure we know that the file contains lambdas
    this.significanceType = this.LAMBDA;
    headerLine = lines [lineCount++];
  }

  boolean expectPvals = doesHeaderLineHaveDuplicates(headerLine);
  if(this.significanceType != this.LAMBDA && !expectPvals){
    // we know that we don't have a lambda header and we don't
    // have significance values
    this.significanceType = this.NONE;
  }
  StringTokenizer headerTok = new StringTokenizer(headerLine);
  int numTokens = headerTok.countTokens();

    // if we expect p-values, 4 is the minimum number.
    // if we don't, 3 is the minimum number.  Ergo:
    // either way, we need 3, and if we expectPvals, we need 4.
  if ((numTokens < 3) || ((numTokens<4)&&expectPvals)) {
      StringBuffer msg =  new StringBuffer
              ("Invalid header format in data file.");
      msg.append ("\nNumber of tokens parsed: " + numTokens);
      for (int i=0; i<numTokens; i++) {
        msg.append("\nToken " + i + ": " + headerTok.nextToken() );
      }
      throw new IOException (msg.toString());
    }

  double tmpF = numTokens/2.0;
  int tmpI = (int)Math.rint(tmpF);
  int numberOfConditions;
  int haveExtraTokens = 0;
  if (expectPvals) {
    if (tmpI == tmpF ) {//missing numSigConds field
      numberOfConditions = (numTokens - 2) / 2;
      haveExtraTokens = 0;
      } 
   else {
     numberOfConditions = (numTokens - 3) / 2;
     haveExtraTokens = 1;
     } // else
    }
  else {
    numberOfConditions = numTokens - 2; 
    }

    /* eat the first two tokens from the header line */
  headerTok.nextToken ();
  headerTok.nextToken ();
    /* the next numConds tokens are the condition names */
  Vector cNames = new Vector(numberOfConditions);
  for (int i=0; i<numberOfConditions; i++)
    cNames.add (headerTok.nextToken());
    /* the next numConds tokens should duplicate the previous list of condition names */
  if (expectPvals) {
     for (int i=0; i<numberOfConditions; i++) {
       String title = headerTok.nextToken();
       if ( !(title.equals( cNames.get(i) )) ) {
           StringBuffer msg = new StringBuffer();
           msg.append ("Expecting both ratios and p-values.\n");
           msg.append ("Condition name mismatch in header line"
                            + " of data file " + filename + ": "
                            + cNames.get(i) + " vs. " + title);
           throw new IOException (msg.toString());
         } // if !title
       } // for i
    } // if expectPvals

     /* OK, we have a reasonable header; clobber all old information */
  this.filename = filename;
  this.numConds = numberOfConditions;
  this.extraTokens = haveExtraTokens;
  this.haveSigValues = expectPvals;
    /* wipe old data */
  initDataStructures();
    /* store condition names */
  condNames = cNames;
  for (int i=0; i<numConds; i++) {
     condNameToIndex.put (condNames.get(i), new Integer(i));
    }

    /* parse rest of file line by line */
  if (taskMonitor != null) {
    taskMonitor.setStatus("Reading in Data...");
  }
  for (int i = lineCount; i < lines.length; i++) {

      if (taskMonitor != null) {
        double percentComplete = ((double) i / lines.length) * 100.0;
        taskMonitor.setPercentCompleted((int) percentComplete);
      }

    parseOneLine (lines [i], lineCount, expectPvals);
  }

    /* save numGenes and build hash of gene names to indices */
  this.numGenes = geneNames.size();
  for (int i=0; i<geneNames.size(); i++) {
    geneNameToIndex.put (geneNames.get(i), new Integer(i));
    }

    /* trim capacity of data structures for efficiency */
  geneNames.trimToSize();
  geneDescripts.trimToSize();
  allMeasurements.trimToSize();

  return true;

} // loadData
//--------------------------------------------------------------------

    private boolean doesHeaderLineHaveDuplicates(String hline) {
	boolean retval = false;

	StringTokenizer headerTok = new StringTokenizer(hline);
	int numTokens = headerTok.countTokens();
	if (numTokens < 3) { retval = false; }
	else {

	    headerTok.nextToken();
	    headerTok.nextToken();

	    HashMap names = new HashMap();
	    while ((!retval) && headerTok.hasMoreTokens()) {
		String title = headerTok.nextToken();
		Object titleObject = (Object)title;
		if(names.get( titleObject ) == null) {
		    names.put( titleObject, titleObject);
		}
		else {retval=true;}
	    }
	}

	return retval;
    }
    
    private boolean isHeaderLineNull(String hline, BufferedReader input,
				     String filename) throws IOException {
	    if (hline == null) {
            throw new IOException
                    ("Could not read header line from data file: " + filename);
        }
        return false;
    }

    // added by iliana on 11.25.2002
    // it is convenient for users to load their MTX files as they are
    // the current code requires them to remove the first line
    private boolean isHeaderLineMTXHeader(String hline){
	  boolean b = false;
	  String pattern = "\t+RATIOS\t+LAMBDAS";
	  b = hline.matches(pattern);
	  return b;
    }

    private String readOneLine(BufferedReader f) {
	String s = null;
	try {
	    s = f.readLine();
	} catch (IOException e) {
	}
	return s;
    }

    private void parseOneLine(String oneLine, int lineCount)
            throws IOException {
	    parseOneLine(oneLine,lineCount,true);
    }

    private void parseOneLine(String oneLine, int lineCount, boolean sig_vals)
            throws IOException {
	StringTokenizer strtok = new StringTokenizer(oneLine);
	int numTokens = strtok.countTokens();

	if (numTokens == 0) {return;}
	/* first token is gene name, or NumSigGenes */
	String gName = strtok.nextToken();
	if ( gName.startsWith("NumSigGenes") ) {return;}

	if ( (sig_vals && (numTokens < 2*numConds + 2)) ||
	     ((!sig_vals)&&numTokens<numConds+2) ) {
	        throw new IOException ("Warning: parse error on line " + lineCount
			       + "  tokens read: " + numTokens);
	}

	geneNames.add(gName);
	/* store descriptor token */
	geneDescripts.add( strtok.nextToken() );

	String[] expData = new String[numConds];
	for (int i=0; i<numConds; i++) {
	    expData[i] = strtok.nextToken();
	}
	String[] sigData = new String[numConds];
	if(sig_vals) {
	    for (int i=0; i<numConds; i++) {
		sigData[i] = strtok.nextToken();
	    }
	}
	else {
	    for (int i=0; i<numConds; i++) {
		sigData[i] = expData[i];
	    }
	}

	Vector measurements = new Vector(numConds);
	for (int i=0; i<numConds; i++) {
	    mRNAMeasurement m = new mRNAMeasurement(expData[i],sigData[i]);
	    measurements.add(m);
	    double ratio = m.getRatio();
	    double signif = m.getSignificance();
	    if (ratio < minExp) {minExp = ratio;}
	    if (ratio > maxExp) {maxExp = ratio;}
	    if (signif < minSig) {minSig = signif;}
	    if (signif > maxSig) {
        maxSig = signif;
        if(this.significanceType != this.LAMBDA && 
           sig_vals && 
           maxSig > 1){
          this.significanceType = this.LAMBDA;
        }
      }
	}
  
  if(this.significanceType != this.LAMBDA && sig_vals && minSig > 0){
    // We are probably not looking at lambdas, since no significance value was > 1
    // and the header is not a LAMBDA header
    this.significanceType = this.PVAL;
  }

	allMeasurements.add(measurements);
    }//parseOneLine

//--------------------------------------------------------------------
  
  /**
   * Converts all lambdas to p-values.
   * Lambdas are lost after this call.
   */
  public void convertLambdasToPvals (){
    Iterator it = this.allMeasurements.iterator();
    while(it.hasNext()){
      Vector v = (Vector)it.next();
      Iterator it2 = v.iterator();
      while(it2.hasNext()){
        mRNAMeasurement m = (mRNAMeasurement)it2.next();
        double pval = ExpressionData.getPvalueFromLambda(m.getSignificance());
        m.setSignificance(pval);
      }//while it2
    }//while it
  }//convertPValsToLambdas

  /**
   * @return a very close approximation of the pvalue that corresponds to the
   * given lambda value
   */
  static public double getPvalueFromLambda (double lambda){
    double x = StrictMath.sqrt(lambda)/2.0;
    double t = 1.0/(1.0 + 0.3275911*x);
    double erfc = 
      StrictMath.exp(-(x*x)) * ( 
                                0.254829592  * t + 
                                -0.284496736 * StrictMath.pow(t,2.0) +
                                1.421413741  * StrictMath.pow(t,3.0) +
                                -1.453152027 * StrictMath.pow(t,4.0) +
                                1.061405429  * StrictMath.pow(t,5.0)
                                );
    erfc = erfc/2.0;
    if(erfc < 0 || erfc > 1){
      // P-value must be >= 0 and <= 1
      throw new IllegalStateException("The calculated pvalue for lambda = " + lambda + " is " + erfc);
    }
    return erfc;
  }//getPvalueFromLambda

  /**
   * @return one of NONE, UNKNOWN, PVAL, LAMBDA
   */
  public int getSignificanceType (){
    return this.significanceType;
  }//getSignificanceType


    /**
     * Returns a text description of this data object.
     */
    public String getDescription() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        File file = new File (filename);

        sb.append("Data read from: " + file.getName() + lineSep);
        sb.append(lineSep);
        sb.append("Number of genes = " + getNumberOfGenes() + lineSep);
        sb.append("Number of conditions = " + getNumberOfConditions() + lineSep);
        sb.append("Significance values: ");
        if (this.haveSigValues) {sb.append("yes");} else {sb.append("no");}
        sb.append(lineSep).append(lineSep);
        sb.append("MinExp: " + minExp + "    MaxExp: " + maxExp + lineSep);
        if (this.haveSigValues) {
          sb.append("MinSig: " + minSig + "    MaxSig: " + maxSig + lineSep);
          String sigType = null;
          if(this.significanceType == this.UNKNOWN){
            sigType = "unknown";
          }else if(this.significanceType == this.LAMBDA){
            sigType = "lambda values";
          }else if(this.significanceType == this.PVAL){
            sigType = "p-values";
          }else if(this.significanceType == this.NONE){
            sigType = "none";
          }
          sb.append("Type of significance: " + sigType + lineSep);
        }
        return sb.toString();
    }
        
    public int getNumberOfGenes() {return numGenes;}
    public int getNumberOfConditions() {return numConds;}
    public String[] getGeneNames() {
	return (String[])geneNames.toArray(new String[0]);
    }
    public Vector getGeneNamesVector() { return geneNames; }
    public void setGeneNames(Vector newNames) {	
	geneNames = newNames; 
	geneNameToIndex.clear();
	for (int i=0; i<geneNames.size(); i++) {
	    geneNameToIndex.put( geneNames.get(i), new Integer(i) );
	}
    }
    public String[] getGeneDescriptors() {
	return (String[])geneDescripts.toArray(new String[0]);
    }
    public Vector getGeneDescriptorsVector() { return geneDescripts; }
    public void setGeneDescriptors(Vector newDescripts) { geneDescripts = newDescripts; }
    public String[] getConditionNames() {
	return (String[])condNames.toArray(new String[0]);
    }
  public int getConditionIndex (String condition){
    return ((Integer)this.condNameToIndex.get(condition)).intValue();
  }

    public double[][] getExtremeValues() {
	double[][] maxVals = new double[2][2];
	maxVals[0][0] = minExp;
	maxVals[0][1] = maxExp;
	maxVals[1][0] = minSig;
	maxVals[0][1] = maxSig;

	return maxVals;
    }

    public String getGeneDescriptor(String gene) {
	Integer geneIndex = (Integer)geneNameToIndex.get(gene);
	if (geneIndex == null) {return null;}

	return (String)geneDescripts.get( geneIndex.intValue() );
    }
    
    public boolean hasSignificanceValues() {return haveSigValues;}

    public Vector getAllMeasurements() {return allMeasurements;}

    public Vector getMeasurements(String gene) {
	Integer geneIndex = (Integer)geneNameToIndex.get(gene);
	if (geneIndex == null) {return null;}

	Vector measurements =
	    (Vector)( this.getAllMeasurements().get( geneIndex.intValue() ) );
	return measurements;
    }

  public mRNAMeasurement getMeasurement (String gene, String condition) {
	Integer condIndex = (Integer)condNameToIndex.get(condition);
	if (condIndex == null) {
	    return null;}

	Vector measurements = this.getMeasurements(gene);
	if (measurements == null) {
	    return null;
	}

	mRNAMeasurement returnVal =
	    (mRNAMeasurement) measurements.get( condIndex.intValue() );
	return returnVal;
    }


    /**
     * Copies ExpressionData data structure into
     * GraphObjAttributes data structure.
     * @param nodeAttribs Node Attributes Object.
     * @param taskMonitor Task Monitor.  Can be null.
     */
    public void copyToAttribs(GraphObjAttributes nodeAttribs,
                              TaskMonitor taskMonitor) {


      System.out.println( "Copying to attributes" );
      String[] condNames = getConditionNames();
      for(int condNum=0; condNum<condNames.length; condNum++) {
        String condName = condNames[condNum];
        String eStr = condName + "exp";
        String sStr = condName + "sig";
        for (int i=0; i<geneNames.size(); i++) {
          String canName = (String)geneNames.get(i);
                    mRNAMeasurement mm =  getMeasurement(canName,condName);
                    if(mm!=null) {
                      nodeAttribs.set(eStr,canName,mm.getRatio());
                      nodeAttribs.set(sStr,canName,mm.getSignificance());
                    }
                    //  Report on Progress to the Task Monitor.
                    if (taskMonitor != null) {
                      int currentCoordinate = condNum * geneNames.size() + i;
                      int matrixSize = condNames.length * geneNames.size();
                        double percent = ((double) currentCoordinate / matrixSize)
                          * 100.0;
                        taskMonitor.setPercentCompleted((int) percent);
                    }
        }
                nodeAttribs.setClass(eStr,Double.class);
                nodeAttribs.setClass(sStr,Double.class);
      }
    }
}


