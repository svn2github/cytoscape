// loadThesaurus
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
import cytoscape.data.annotation.readers.*;
//------------------------------------------------------------------------------
/**
 *  load an thesaurus into an rmi biodata server.  an thesaurus -- necessarily
 *  accompanied by an ontology, whose location is specified in the thesaurus's xml
 *  file -- specifies the relationship of an entity (i.e., a gene) to one or more nodes
 *  (integers) in the ontology.  from this assignment, the full ontological hierarchy
 *  can be deduced.
 *  
 *  @see cytoscape.data.thesaurus.Thesaurus
 *  @see cytoscape.data.thesaurus.Ontology
 */
public class loadThesaurus {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{ 
  if (args.length != 2) {
    System.err.println ("usage:  loadThesaurus <server name> <thesaurus flat file>");
    System.exit (1);
    }

  String serverName = args [0];
  BioDataServer server = new BioDataServer ("rmi://localhost/" + serverName);

  String [] filenames = new String [1];
  filenames [0] = args [1];

  System.out.println ("---------- server: " + server);
  System.out.println ("---------- file:   " + filenames [0]);

  server.loadThesaurusFiles (filenames);

  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadThesaurus
