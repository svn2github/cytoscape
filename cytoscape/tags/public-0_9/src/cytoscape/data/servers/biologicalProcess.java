// biologicalProcess.java:  of BioDataServer
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
public class biologicalProcess {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  if (args.length == 0) {
    System.out.println ("biologicalProcess <leafProcessId> [-v]");
    System.exit (0);
     }

  String intString = args [0];
  int leafProcessId = Integer.parseInt (intString);
  boolean verbose = false;
  if (args.length == 2 && args [1].equals ("-v"))
    verbose = true;

  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  String processName = server.getBioProcessName (leafProcessId);
  //String result [] = server.getBioProcessHierarchy (leafProcessId);
  //for (int i=0; i < result.length; i++) 
  //  System.out.println (i + ") " + result [i]);

} // main
//------------------------------------------------------------------------------
} // biologicalProcess
