// status
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
public class status {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{

  if (args.length != 2) {
    System.err.println ("usage:  status <rmi host name> <rmi service name>");
    System.exit (1);
    }

  String hostname = args [0];
  String serviceName = args [1];
 
  String serverName = "rmi://" + hostname + "/" + serviceName;
  System.out.println ("--- checking status of " + serverName);
  BioDataServer server = new BioDataServer (serverName);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // status
