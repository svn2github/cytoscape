// loadMolecularFunctions.java:  load a hash of vectors 
//    hash (geneName):  processID0, processID1, ...
// from a simple text file, produced by 
//      data/toXml/go/getYeastBiologicalProcessIDs.sql
// and read in, here, with the help of 
//    data/GeneProcessIdTextReader.java
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.readers.GeneAndGoTermTextReader;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
//------------------------------------------------------------------------------
public class loadMolecularFunctions {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  if (args.length != 1) {
    System.out.println ("usage:  loadMolecularFunctions <someFile.txt");
    System.exit (1);
    }

  String filename = args [0];


  GeneAndGoTermTextReader reader = new GeneAndGoTermTextReader (filename);
  Hashtable hash = reader.read ();

  Enumeration keys = hash.keys ();
  int totalCount = 0;
 
  while (keys.hasMoreElements ()) {
    String key = (String) keys.nextElement ();
    Vector list = (Vector) hash.get (key);
    totalCount += list.size ();
    }

  server.addMolecularFunctions (hash);

} // main
//------------------------------------------------------------------------------
} // loadMolecularFunctions
