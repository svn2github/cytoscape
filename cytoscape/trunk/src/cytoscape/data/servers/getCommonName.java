// getCommonName
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
import cytoscape.data.annotation.*;
//------------------------------------------------------------------------------
public class getCommonName {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{

  if (args.length != 4) {
    System.err.println ("usage:  getCommonName <rmi host name> <rmi service name> \"<species>\" <canonicalName>");
    System.exit (1);
    }

  String hostname = args [0];
  String serviceName = args [1];
  String species = args [2];
  String canonicalName = args [3];
 
  String serverName = "rmi://" + hostname + "/" + serviceName;
  BioDataServer server = new BioDataServer (serverName);
  System.out.println (canonicalName + " -> " + server.getCommonName (species, canonicalName));

} // main
//------------------------------------------------------------------------------
} // getCommonName
