//ExpressionData.java
//--------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------
package cytoscape.data;
//--------------------------------------------------------------------
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.util.HashMap;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
//--------------------------------------------------------------------
/**
 * This class provides a reader for the common file format for expression
 * data and an interface to access the data.
 *
 *
 * There are variations in the file format used; the following assumptions
 * about the file format are considered valid. Attempting to read a file
 * that does not satisfy these assumptions is not guaranteed to work.
 *
 * 1. A token is a consecutive sequence of alphanumeric characters separated
 *    by whitespace.
 * 2. The file consists of an arbitrary number of lines, each of which
 *    contains the same number of tokens and has a total length less than
 *    8193 characters.
 * 3. The first line of the file is a header line with the following format:
 *
 *    <text> <text> cond1 cond2 ... condN cond1 cond2 ... condN NumSigConds
 *
 * Here cond1 through condN are the names of the conditions. The two sequences
 * of condition names must match exactly in order and lexicographically.
 * The last label, NumSigConds, is optional.
 *
 * 4. Each successive line represents the measurements for a partcular gene,
 *    and has the format
 *
 *   <FNAME> <CNAME> E E ... E S S ... S I
 *
 *  where <FNAME> is the formal name of the gene, <CNAME> is the common name,
 *  the E's are tokens, parsable as doubles, representing the expression
 *  level change for each condition, the S's are tokens parsable as doubles
 *  representing the statistical significance of the expression level change,
 *  and I is an optional integer giving the number of conditions in which
 *  the expression level change was significant for this gene.
 *
 * 5. An optional last line can be included with the following form:
 *
 *  NumSigGenes: I I ... I
 *
 *  where there are N I's, each an integer representing the number of
 *  significant genes in that condition.
 */
public class ExpressionData {

    public static final int MAX_LINE_SIZE = 8192;

    String filename;
    int numGenes;
    int numConds;
    int extraTokens;

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
	this.initDataStructures();
    }
    public ExpressionData(String filename) {
	this.filename = null;
	numGenes = 0;
	numConds = 0;
	extraTokens = 0;
	this.initDataStructures();
	this.loadData(filename);
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

    public boolean loadData(String filename) {
	if (filename == null) {return false;}
	BufferedReader input;
	try {
	    input = new BufferedReader(new FileReader(filename),
				       MAX_LINE_SIZE);
	} catch (IOException e) {
	    System.err.println("Error trying to open data file " + filename);
	    return false;
	}

	String headerLine = this.readOneLine(input);
	if (isHeaderLineNull(headerLine,input,filename)) {return false;}

	boolean expectPvals = doesHeaderLineHaveDuplicates(headerLine);
	StringTokenizer headerTok = new StringTokenizer(headerLine);
	int numTokens = headerTok.countTokens();
	// if we expect p-values, 4 is the minimum number.
	// if we don't, 3 is the minimum number.  Ergo:
	// either way, we need 3, and if we expectPvals, we need 4.
	if ((numTokens < 3) || ((numTokens<4)&&expectPvals)) {
	    System.err.println("Bad header format in data file " + filename);
	    System.err.println("Number of tokens parsed: " + numTokens);
	    for (int i=0; i<numTokens; i++) {
		System.err.println("Token " + i + ": "
				   + headerTok.nextToken() );
	    }
	    try {
		input.close();
	    } catch (IOException e) {}
	    return false;
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

	System.out.println("parsed " + numTokens + " tokens from header line,"
			   + " representing " + numberOfConditions
			   + " conditions.");

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
		    System.err.println("Expecting both ratios and p-values.\n");
		    System.err.println("Condition name mismatch in header line"
				       + " of data file " + filename + ": "
				       + cNames.get(i) + " vs. " + title);
		    return false;
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
    }//loadData

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
		//System.out.println("retval : " + retval);
	    }
	}

	return retval;
    }
    
    private boolean isHeaderLineNull(String hline, BufferedReader input,
				     String filename) {
	if (hline == null) {
	    System.err.println("Could not read header line from data file "
			       + filename);
	    try {
		input.close();
	    } catch (IOException e) {
	    }
	    return true;
	}
	else { return false; }
    }

    private String readOneLine(BufferedReader f) {
	String s = null;
	try {
	    s = f.readLine();
	} catch (IOException e) {
	}
	return s;
    }

    private void parseOneLine(String oneLine, int lineCount) {
	parseOneLine(oneLine,lineCount,true);
    }
    private void parseOneLine(String oneLine, int lineCount, boolean pvals) {
	StringTokenizer strtok = new StringTokenizer(oneLine);
	int numTokens = strtok.countTokens();

	if (numTokens == 0) {return;}
	/* first token is gene name, or NumSigGenes */
	String gName = strtok.nextToken();
	if ( gName.startsWith("NumSigGenes") ) {return;}

	if ( (pvals && (numTokens < 2*numConds + 2)) ||
	     ((!pvals)&&numTokens<numConds+2) ) {
	    System.out.println("Warning: parse error on line " + lineCount
			       + "  tokens read: " + numTokens);
	    return;
	}

	geneNames.add(gName);
	/* store descriptor token */
	geneDescripts.add( strtok.nextToken() );

	String[] expData = new String[numConds];
	for (int i=0; i<numConds; i++) {
	    expData[i] = strtok.nextToken();
	}
	String[] sigData = new String[numConds];
	if(pvals) {
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
	    if (signif > maxSig) {maxSig = signif;}
	}

	allMeasurements.add(measurements);
    }//parseOneLine

//--------------------------------------------------------------------

    public int getNumberOfGenes() {return numGenes;}
    public int getNumberOfConditions() {return numConds;}
    public String[] getGeneNames() {
	return (String[])geneNames.toArray(new String[0]);
    }
    public String[] getGeneDescriptors() {
	return (String[])geneDescripts.toArray(new String[0]);
    }
    public String[] getConditionNames() {
	return (String[])condNames.toArray(new String[0]);
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
	if (condIndex == null) {return null;}

	Vector measurements = this.getMeasurements(gene);
	if (measurements == null) {return null;}

	mRNAMeasurement returnVal =
	    (mRNAMeasurement) measurements.get( condIndex.intValue() );
	return returnVal;
    }
}
