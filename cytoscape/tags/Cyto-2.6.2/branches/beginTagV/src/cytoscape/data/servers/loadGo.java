// loadGo.java:  load gene products extracted from geneontology db to rmi server
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.GeneProduct;
import cytoscape.data.readers.GeneProductXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class loadGo {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  String serverName = "biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  String filename = "geneProducts.xml";
  GeneProductXmlReader reader = new GeneProductXmlReader (new File (filename));
  Vector result = reader.read ();
  System.out.println ("loading " + result.size () + " gene products...");
  server.addGeneProducts (result);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadGo
