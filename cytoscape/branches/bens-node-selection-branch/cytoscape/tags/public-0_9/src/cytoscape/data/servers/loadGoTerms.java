// loadGoTerms.java:  load all current GO terms, representing 3
// ontologies:  biological process, molecular function, cellular component
// the terms are read from an xml file, which is a simpler form than that
// which comes from wwww.geneontology.org
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.GoTerm;
import cytoscape.data.readers.GoTermXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class loadGoTerms {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  String serverName = "biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  if (args.length != 1) {
    System.out.println ("usage:  loadGoTerms <someFile.xml>");
    System.exit (1);
    }

  String filename = args [0];
  GoTermXmlReader reader = new GoTermXmlReader (new File (filename));
  Vector result = reader.read ();
  System.out.println ("loading " + result.size () + " GO terms...");
  server.addGoTerms (result);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadGoTerms
