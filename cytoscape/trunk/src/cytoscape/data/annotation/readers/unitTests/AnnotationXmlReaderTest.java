// AnnotationXmlReaderTest
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

import csplugins.data.annotation.*;
import csplugins.data.annotation.readers.AnnotationXmlReader;
//------------------------------------------------------------------------------
/**
 * test the AnnotationXmlReader class
 */
public class AnnotationXmlReaderTest extends TestCase {


//------------------------------------------------------------------------------
public AnnotationXmlReaderTest (String name) 
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
 *  read a small sample KEGG annotation
 */
public void testReadKeggAnnotation () throws Exception
{ 
  System.out.println ("testReadKeggHaloMetabolicPathway");

  String filename = "sampleData/kegg/haloMetabolicPathway.xml";
  AnnotationXmlReader reader = new AnnotationXmlReader (new File (filename));
  Annotation annotation = reader.getAnnotation ();

  assertTrue (annotation.getSpecies().equals ("Halobacterium sp."));
  assertTrue (annotation.getCurator().equals ("KEGG"));
  assertTrue (annotation.getOntologyType().equals ("Metabolic Pathway"));
  assertTrue (annotation.size () == 10);
  
} // testReadKeggAnnotation
//-------------------------------------------------------------------------
/**
 *  read a small sample GO annotation for yeast, biological process
 */
public void doNotTestReadGoYeastBioProcess () throws Exception
{ 
  System.out.println ("testReadGoYeastBioProcess");

  String filename = "sampleData/GO/yeastBiologicalProcess.xml";
  AnnotationXmlReader reader = new AnnotationXmlReader (new File (filename));
  Annotation annotation = reader.getAnnotation ();

  assertTrue (annotation.getSpecies().equals ("saccharomyces cerevisiae"));
  assertTrue (annotation.getCurator().equals ("GO"));
  assertTrue (annotation.getOntologyType().equals ("Biological Process"));

  assertTrue (annotation.size () == 20);
  
} // testReadGoYeastBioProcess
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (AnnotationXmlReaderTest.class));

} // main
//------------------------------------------------------------------------------
} // class AnnotationXmlReaderTest
