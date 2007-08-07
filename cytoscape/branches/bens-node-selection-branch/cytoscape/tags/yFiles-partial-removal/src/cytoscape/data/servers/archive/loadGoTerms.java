// loadGoTerms.java:  load all current GO terms, representing 3

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

// ontologies:  biological process, molecular function, cellular component
// the terms are read from an xml file, which is a simpler form than that
// which comes from wwww.geneontology.org
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.GoTerm;
import cytoscape.data.readers.GoTermXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class loadGoTerms {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  String serverName = "biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  if (args.length != 1) {
    System.out.println ("usage:  loadGoTerms <someFile.xml>");
    System.exit (1);
    }

  String filename = args [0];
  GoTermXmlReader reader = new GoTermXmlReader (new File (filename));
  Vector result = reader.read ();
  System.out.println ("loading " + result.size () + " GO terms...");
  server.addGoTerms (result);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadGoTerms


