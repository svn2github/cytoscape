// BioDataServerFactory:  create and return a BioDataServer, either an RMI
// server, or a server which runs in-process with the calling application
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data.servers;
//------------------------------------------------------------------------------
import java.rmi.*;
import java .io.File;
//------------------------------------------------------------------------------
public class BioDataServerFactory {

//------------------------------------------------------------------------------
static public BioDataServer create (String serverName)
{
  BioDataServer server;

  if (serverName.indexOf ("rmi://") >= 0) {
    try {
      server = (RMIBioDataServer) Naming.lookup (serverName);
      return server;
      }
    catch (Exception e) {
      System.err.println ("----------- Error!  failed to find rmi server at " + serverName);
      e.printStackTrace ();
      return null;
      } // catch
    } // if "rmi://" in serverName
  else { // look for a readable directory
    File fileTester = new File (serverName);
    if (fileTester.isDirectory () && fileTester.canRead ())
      return new IPBioDataServer (serverName);
    else {
      System.err.println ("----------- Error!  failed to find biodata server directory at " + 
                          serverName);
      return null;
      } // else: could not find a readable directory at <serverName>
     } // else: not an rmi server, look for directory

} // create
//------------------------------------------------------------------------------
} // BioDataServerFactory
