// DataMatrixFileReader.java
//-----------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix;
//-----------------------------------------------------------------------------------
import java.io.*;
import java.util.*;
import cytoscape.data.readers.*;
//-----------------------------------------------------------------------------------
public class DataMatrixFileReader extends DataMatrixReader {
  ArrayList matrices = new ArrayList ();
//-----------------------------------------------------------------------------------
public DataMatrixFileReader (String protocol, String path)
{
  super (protocol, path);

} // ctor
//-----------------------------------------------------------------------------------
public DataMatrix [] get () throws Exception
{
  return (DataMatrix []) matrices.toArray (new DataMatrix [0]);
}
//-----------------------------------------------------------------------------------
public void read () throws Exception
{
  String rawText = null;

  if (protocol.equals ("file://")) {
    File file = new File (path);
    if (!file.exists ())
      throw new IllegalArgumentException ("cannot find file named '" + path + "'");
    if (!file.canRead ())
      throw new IllegalArgumentException ("cannot read file named '" + path + "'");
    TextFileReader reader = new TextFileReader (path);
    reader.read ();
    rawText = reader.getText ();
    }

  else if (protocol.equals ("jar://")) {
    TextJarReader reader = new TextJarReader (protocol + path);
    reader.read ();
    rawText = reader.getText ();
    }

  else if (protocol.equals ("http://")) {
    TextHttpReader reader = new TextHttpReader (protocol + path);
    reader.read ();
    rawText = reader.getText ();
    }

  parseText (rawText);

} // read
//-----------------------------------------------------------------------------------
protected void parseText (String rawText) throws Exception
{
  String [] lines = rawText.split ("\n");
  String [] titles = lines [0].split ("\t");

  DataMatrix matrix = new DataMatrix (protocol + path);
  int dataRows = lines.length - 1;
  int dataColumns = titles.length - 1;

  matrix.setSize (dataRows, dataColumns);
  String rowTitlesTitle = titles [0];
  String [] columnTitles = new String [titles.length - 1];
  for (int i=1; i < titles.length; i++)
    columnTitles [i-1] = titles [i];

  matrix.setRowTitlesTitle (rowTitlesTitle);
  matrix.setColumnTitles (columnTitles);

  ArrayList rowTitleList = new ArrayList ();
  for (int row = 1; row < lines.length; row++) {
    String rowString = lines [row];
    String [] tokens = rowString.split ("\t");
    String rowTitle = tokens [0];
    rowTitleList.add (rowTitle);
    for (int col=1; col < tokens.length; col++) {
      String tmp = tokens [col];
       try {
         double value = (new Double (tmp)).doubleValue ();
         matrix.set (row-1, col-1, value);
         }
       catch (NumberFormatException nfe) {
         String msg = "cannot convert '" + tmp + "' to double, at row " + (row-1) + 
                      " column " + (col-1) + " while reading file " + path;
         throw new IllegalArgumentException (msg);
         }
      } // for col
    } // for row

  matrix.setRowTitles ((String []) rowTitleList.toArray (new String [0]));

  matrices.add (matrix);

} // read
//-----------------------------------------------------------------------------------
} // class DataMatrixFileReader
