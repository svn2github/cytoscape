// getPathForTerm.java:  display all paths to root from a given GO term

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
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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
public class getPathsForTerm {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  if (args.length != 1) {
    System.out.println ("getPath <goTerm>");
    System.exit (0);
     }

  String s = args [0];
  int goTerm = 0;
  goTerm = Integer.parseInt(s);
    
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  Vector idPaths = server.getAllGoHierarchyPaths (goTerm);
  // System.out.println ("number of paths: " + idPaths.size ());

  for (int v=0; v < idPaths.size(); v++) {
    Vector idPath = (Vector) idPaths.elementAt (v);
    for (int m=0; m < idPath.size (); m++) {
      int id = ((Integer) idPath.elementAt (m)).intValue ();
      String molecularFunction = server.getMolecularFunctionName (id);
      System.out.print (id + " ");
      } // for m
    System.out.println ();
    } // for v

} // main
//------------------------------------------------------------------------------
} // getPathsForTerm



