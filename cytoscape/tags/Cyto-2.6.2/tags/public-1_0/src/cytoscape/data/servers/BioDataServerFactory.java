// BioDataServerFactory:  create and return a BioDataServer, either an RMI

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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


