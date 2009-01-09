// geneInfo.java:  of BioDataServer
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.BindingPair;
import cytoscape.data.BindingPairXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class geneInfo {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  if (args.length == 0) {
    System.out.println ("geneInfo <geneName> [-b (show binding pairs)]");
    System.exit (0);
     }

  String geneName = args [0];
  boolean showBindingPairs = false;

  if (args.length >= 2)
    showBindingPairs = true;
       
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);
  System.out.println (server.getGeneInfo (geneName));
  //System.out.println (server.getMolecularFunction (geneName));
  //if (showBindingPairs)
  //  System.out.println (server.getBindingPairs (geneName));

} // main
//------------------------------------------------------------------------------
} // geneInfo
