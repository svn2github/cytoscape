// clearGoPathTerminators:  delete (and re-initialize) the GoPathTerminators
// in the biodata server.
// see loadGoPathTermintors.java for background
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import java.io.*;
//------------------------------------------------------------------------------
public class clearGoPathTerminators {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  System.out.println ("--- before\n" + server.describe ());
  server.clearGoPathTerminators ();
  System.out.println ("--- after\n" + server.describe ());

} // main
//------------------------------------------------------------------------------
} // clearGoPathTerminators
