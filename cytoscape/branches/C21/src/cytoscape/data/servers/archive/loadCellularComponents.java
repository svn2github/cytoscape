// loadCellularComponents.java:  load a hash of vectors 

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

//    hash (geneName):  processID0, processID1, ...
// from a simple text file, produced by 
//      data/toXml/go/getYeastBiologicalProcessIDs.sql
// and read in, here, with the help of 
//    data/GeneProcessIdTextReader.java
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.readers.GeneAndGoTermTextReader;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
//------------------------------------------------------------------------------
public class loadCellularComponents {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  if (args.length != 1) {
    System.out.println ("usage:  loadCellularComponents <someFile.txt");
    System.exit (1);
    }

  String filename = args [0];


  GeneAndGoTermTextReader reader = new GeneAndGoTermTextReader (filename);
  Hashtable hash = reader.read ();

  Enumeration keys = hash.keys ();
  int totalCount = 0;
 
  while (keys.hasMoreElements ()) {
    String key = (String) keys.nextElement ();
    Vector list = (Vector) hash.get (key);
    totalCount += list.size ();
    }

  server.addCellularComponents (hash);

} // main
//------------------------------------------------------------------------------
} // loadCellularComponents


