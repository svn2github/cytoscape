// loadAnnotation
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
 *  load an annotation into an rmi biodata server.  an annotation -- necessarily
 *  accompanied by an ontology, whose location is specified in the annotation's xml
 *  file -- specifies the relationship of an entity (i.e., a gene) to one or more nodes
 *  (integers) in the ontology.  from this assignment, the full ontological hierarchy
 *  can be deduced.
 *  
 *  @see csplugsin.data.annotation.Annotation
 *  @see csplugsin.data.annotation.Ontology
 */
public class loadAnnotation {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{ 
  if (args.length != 2) {
    System.err.println ("usage:  loadAnnotation <server name> <annotation.xml>");
    System.exit (1);
    }

  String serverName = args [0];
  BioDataServer server = new BioDataServer (serverName);

  String filename = args [1];

  File xmlFile = new File (filename);
  if (!xmlFile.canRead ()) {
    System.err.println ("--- cytoscape.data.servers.loadAnnotation error:  cannot read");
    System.err.println ("        " + filename);
    System.exit (1);
    }

  AnnotationXmlReader reader = new AnnotationXmlReader (xmlFile);
  server.addAnnotation (reader.getAnnotation ());

  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadAnnotation
