// loadSynonym.java:  load gene synonyms from xml (originally from YPD?) 
// to rmi server
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.GeneSynonym;
import cytoscape.data.readers.GeneSynonymXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class loadSynonyms {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  if (args.length != 2) {
    System.out.println ("usage:  loadSynonyms <synonyms xml file> <serverName>");
    System.exit (1);
    }

  String filename = args [0];
  String serviceName = args [1];
  String serverName = "rmi://localhost/" + serviceName;
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  GeneSynonymXmlReader reader = new GeneSynonymXmlReader (new File (filename));
  Vector result = reader.read ();
  System.out.println ("loading " + result.size () + " gene synonyms...");
  server.addGeneSynonyms (result);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadSynonyms
