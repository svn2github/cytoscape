// OntologyXmlReaderTest
//------------------------------------------------------------------------------
// $Revision$  
// $Date$
//------------------------------------------------------------------------------
package csplugins.data.annotation.readers.unitTests;
//------------------------------------------------------------------------------
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.*;
import junit.framework.*;

import csplugins.data.annotation.OntologyTerm;
import csplugins.data.annotation.Ontology;
import csplugins.data.annotation.readers.OntologyXmlReader;
import csplugins.data.annotation.readers.*;
//------------------------------------------------------------------------------
/**
 * test the OntologyXmlReader class
 */
public class OntologyXmlReaderTest extends TestCase {

  static String filename = "sampleData/keggOntology.xml";

//------------------------------------------------------------------------------
public OntologyXmlReaderTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
/**
 * make sure that the ctor properly initializes all relevant data structures
 * as seen through the standard getter methods
 */
public void testReadKeggOntology () throws Exception
{ 
  System.out.println ("testReadKeggOntology");
  String filename = "sampleData/kegg/metabolicPathwayOntology.xml";
  OntologyXmlReader reader = new OntologyXmlReader (new File (filename));
  Ontology ontology = reader.getOntology ();

  assertTrue (ontology.getCurator().equals ("KEGG"));
  assertTrue (ontology.getType().equals ("Metabolic Pathway"));
  assertTrue (ontology.size () == 176);
  
} // testRead
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (OntologyXmlReaderTest.class));

} // main
//------------------------------------------------------------------------------
} // class OntologyXmlReaderTest
