// ExpressionData:  microarray expression data is a matrix of genes vs conditions,
// with a pair of floating point numbers for each cell:  a ratio, and a measure
// of the statistical significance of that ratio.  this class stores those data,
// provides a parser to read them from the current, semi-standard, semi-structured
// text data file.
//-----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-----------------------------------------------------------------------------------------
package cytoscape.data;
//-----------------------------------------------------------------------------------------
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import cytoscape.data.readers.TextFileReader;
//-----------------------------------------------------------------------------------------
public class ExpressionData {
  private String filename;
  private Vector rawTextLines = new Vector ();
  private int numberOfConditions;
  private int numberOfGenes;
  private double [][] extremeValues = new double[2][2];
  private String [] conditionNames;
  private String [] geneNames;
  private Hashtable geneExpressions = new Hashtable ();
//-----------------------------------------------------------------------------------------
public ExpressionData (String filename)
{
  this.filename = filename;
  read ();
  parse ();
}
//-----------------------------------------------------------------------------------------
public void read ()
{
  TextFileReader data = new TextFileReader (filename);
  data.read ();
  String rawText = data.getText ();
  //System.out.println ("size of raw text: " + rawText.length ());
  //System.out.println (rawText);
  StringTokenizer strtok = new StringTokenizer (rawText, "\n");

  int lineCounter = 0;
  while (strtok.hasMoreElements ()) {
    String newLine = (String) strtok.nextElement ();
    lineCounter++;
    rawTextLines.addElement (newLine);
    }

}  // readFromFile
//-------------------------------------------------------------------------------------------
private String [] tokenize (String s, String delimiter)
{
  Vector tmp = new Vector ();
  StringTokenizer strtok = new StringTokenizer (s);

  while (strtok.hasMoreElements ())
    tmp.addElement (strtok.nextElement ());

  String [] result = new String [tmp.size ()];
  for (int i=0; i < tmp.size (); i++)
    result [i] = (String) tmp.elementAt (i);

  return result;

} // tokenize
//-------------------------------------------------------------------------------------------
void parse ()
{
    double currentRatioMin = 0;
    double currentRatioMax = 0;
    double currentSigMin = 0;
    double currentSigMax = 0;

  String titlesLine = (String) rawTextLines.elementAt (0);
  //System.out.println ("titles: " + titlesLine);

  String [] titles = tokenize (titlesLine, " ");
  //System.out.println ("number of titles: " + titles.length);
  numberOfConditions = (titles.length - 3) / 2;
  //System.out.println ("number of conditions: " + numberOfConditions);

  conditionNames = new String [numberOfConditions];
  for (int t=2; t < (2 + numberOfConditions); t++) {
    //System.out.println ("new condition name " + t + ": " + titles[t]);
    conditionNames [t-2] = (String) titles [t];
    }

   // apparently it is standard for these files to end with a line
   // starting with "NumSigGenes: 5914 5831...."
   // skip it, at least until I understand its purpose

  geneNames = new String [rawTextLines.size() - 2];
  numberOfGenes = 0;
  for (int i=1; i < rawTextLines.size ()-1; i++) {
    String rawTextLine = (String) rawTextLines.elementAt (i);
    String [] tokens = tokenize ((String) rawTextLine, " ");
     //System.out.println ("line " + i + ": " + tokens.length);
    String geneName = tokens [0];
    geneNames [i-1] = geneName;
    // System.out.println (i + ": " + geneName);
    Hashtable conditions = new Hashtable ();
    for (int c=0; c < numberOfConditions; c++) {
      String condition = conditionNames [c];
      int ratioIndex = 2 + c;
      int significanceIndex = numberOfConditions + 2 + c;
      String ratio = tokens [ratioIndex];
      String significance = tokens [significanceIndex];

      double currentRatio = Double.parseDouble(ratio);
      double currentSig   = Double.parseDouble(significance);
      if (currentRatio < currentRatioMin)
	  currentRatioMin = currentRatio;
      if (currentRatio > currentRatioMax)
	  currentRatioMax = currentRatio;
      if (currentSig < currentSigMin)
	  currentSigMin = currentSig;
      if (currentSig > currentSigMax)
	  currentSigMax = currentSig;
	      
      extremeValues[0][0] = currentRatioMin;
      extremeValues[0][1] = currentRatioMax;
      extremeValues[1][0] = currentSigMin;
      extremeValues[1][1] = currentSigMax;

      mRNAMeasurement measurement = new mRNAMeasurement (ratio, significance);
      //System.out.println (c + ": " + measurement);
      conditions.put (condition, measurement);
      } // for c
    geneExpressions.put (geneName, conditions);
    numberOfGenes++;
    } // for i

} // parse
//-------------------------------------------------------------------------------------------
public double[][] getExtremeValues ()
{
    return extremeValues;
}
//-------------------------------------------------------------------------------------------
public int getNumberOfGenes ()
{
  return numberOfGenes;
}
//-------------------------------------------------------------------------------------------
public int getNumberOfConditions ()
{
  return numberOfConditions;
}
//-------------------------------------------------------------------------------------------
public int getCount ()
{
  return geneExpressions.size ();
}
//-------------------------------------------------------------------------------------------
public Hashtable getAllExpressionMeasurements ()
{
  return geneExpressions;
}
//-------------------------------------------------------------------------------------------
public String [] getConditionNames ()
{
  return conditionNames;
}
//-------------------------------------------------------------------------------------------
public String [] getGeneNames ()
{
  return geneNames;
}
//-------------------------------------------------------------------------------------------
public mRNAMeasurement getMeasurement (String gene, String condition)
{
  Hashtable geneInfo = (Hashtable) geneExpressions.get (gene);
  if (geneInfo == null)
   return null;

  mRNAMeasurement measurement = (mRNAMeasurement) geneInfo.get (condition);

  return measurement;

} // getMeasurement
//-------------------------------------------------------------------------------------------
} // ExpressionData
