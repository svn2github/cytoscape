// GeneAndGoTermTextReader.java:  read geneName/GoTermId pairs into
// a hash.  these are obtained from the GO database via sql.  after the
// geneName is mapped to its canonical name, these pairs are stored in the
// rmi bioDataServer.
// the GoTerm may be for any of the 3 ontologies: biological process,
// molecular function, cellular component.  here are one-line samples
// of each:
//
//    ==> yeastGenesAndBiologicalProcessIDs.txt
//    AAC1    6854    ATP/ADP exchange
//    ==> yeastGenesAndCellularComponentIDs.txt
//    AYR1    5811    lipid particle
//    ==> yeastGenesAndMolecularFunctionIDs.txt
//    AAC1    5471    ATP/ADP antiporter
//
// in reading and parsing, the third term (the name of the process, function
// or cellular component) is ignored
//------------------------------------------------------------------------------
//  $Revision$  $Date$
//------------------------------------------------------------------------------
package cytoscape.data.readers;
//------------------------------------------------------------------------------
import java.io.*; 
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
//-------------------------------------------------------------------------
public class GeneAndGoTermTextReader { 
  String filename;
//-------------------------------------------------------------------------
public GeneAndGoTermTextReader (String filename)
{
   this.filename = filename;
}
//-------------------------------------------------------------------------
public Hashtable read () throws Exception
{
  TextFileReader reader = new TextFileReader (filename);
  Hashtable hash = new Hashtable ();
  int size = reader.read ();

  StringTokenizer strtok = new StringTokenizer (reader.getText(), "\n");
  while (strtok.hasMoreTokens ()) {
    String newLine = strtok.nextToken ();
    StringTokenizer lineTokenizer = new StringTokenizer (newLine, "\t ");
    String geneName = lineTokenizer.nextToken ();
    String tmp = lineTokenizer.nextToken ();
    Integer termID = new Integer (tmp);
    if (!hash.containsKey (geneName))
      hash.put (geneName, new Vector ());
    Vector list = (Vector) hash.get (geneName);
    list.addElement (termID);
    }

  return hash;

} // read
//-------------------------------------------------------------------------
} // class GeneAndGoTermTextReader
