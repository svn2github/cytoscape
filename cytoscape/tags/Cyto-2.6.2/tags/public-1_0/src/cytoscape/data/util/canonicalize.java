// canonicalize.java

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

//   read a simple list of yeast common names from the named file,
//   write the corresponding ORF names (where known) to stdout.
//------------------------------------------------------------------------------
//  $Revision$  
//  $Date$
//------------------------------------------------------------------------------
package cytoscape.data.util;
//------------------------------------------------------------------------------
import java.io.*; 
import java.util.*;
import java.rmi.*;

import cytoscape.data.servers.*;
import cytoscape.data.readers.TextFileReader;
//-------------------------------------------------------------------------
public class canonicalize { 
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  if (args.length != 2) {
    System.err.println ("usage: canonicalize <bioDataServer URI> <simpleListFile>");
    System.err.println ("  bioDataServer URI may be either a directory, or an RMI server:");
    System.err.println ("    /package/genome/cytoscape/data/GO");
    System.err.println ("       or ");
    System.err.println ("   rmi://localhost/biodata");
    System.exit (1);
    }

  String bioDataServerURI = args [0];
  String filename = args [1];
  BioDataServer bioDataServer = BioDataServerFactory.create (bioDataServerURI);

  TextFileReader reader = new TextFileReader (filename);
  reader.read ();
  String fullText = reader.getText ();
  String [] commonNames = fullText.split ("\n");

  for (int i=0; i < commonNames.length; i++) {
    String canonicalName = bioDataServer.getCanonicalName (commonNames [i]);
    System.out.println (canonicalName);
    }


} // main
//------------------------------------------------------------------------------
} // canonicalize


