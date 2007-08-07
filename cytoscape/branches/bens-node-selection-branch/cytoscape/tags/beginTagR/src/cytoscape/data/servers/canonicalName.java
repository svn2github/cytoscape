// canonicalName.java:  report the standard name for a gene
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class canonicalName {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  if (args.length != 1) {
    System.out.println ("canonicalName <geneName>");
    System.exit (0);
     }
  
  String geneName = args [0];
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);
  System.out.println (server.getCanonicalName (geneName));

} // main
//------------------------------------------------------------------------------
} // canonicalName
