// loadBind.java:  load binding pairs extracted from bind db, to rmi server
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.BindingPair;
import cytoscape.data.readers.BindingPairXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class loadBind {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  String serverName = "biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  String filename = "bindingPairs.xml";
  BindingPairXmlReader reader = new BindingPairXmlReader (new File (filename));
  Vector result = reader.read ();
  System.out.println ("loading " + result.size () + " binding pairs...");
  server.addBindingPairs (result);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadBind
