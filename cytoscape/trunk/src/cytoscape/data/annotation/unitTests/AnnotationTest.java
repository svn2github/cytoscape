// AnnotationTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.annotation.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.data.annotation.*;
//------------------------------------------------------------------------------
/**
 * test the Annotation class
 */
public class AnnotationTest extends TestCase {


//------------------------------------------------------------------------------
public AnnotationTest (String name) 
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
public void testCtor () throws Exception
{ 
  System.out.println ("testCtor");
  Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology ();
  String species = "Saccharomyces cerevisiae";
  String type = "pathways";
  Annotation annotation = new Annotation (species, type, ontology);
                           
  assertTrue (annotation.size () == 0);
  assertTrue (annotation.count () == 0);
  assertTrue (annotation.getCurator().equals (ontology.getCurator ()));
  assertTrue (annotation.getType().equals (type));
  assertTrue (annotation.getOntologyType().equals (ontology.getType ()));
  assertTrue (annotation.getSpecies().equals (species));
  
} // testCtor
//-------------------------------------------------------------------------
/**
 *  add anotations (numbers from the Kegg ontology) and make sure that the
 *  resulting counts are correct.
 */
public void testAdd () throws Exception
{ 
  System.out.println ("testAdd");
  Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology ();
  Annotation annotation = new Annotation ("Halobacterium Sp.", "pathways", ontology);
                                                 
  assertTrue (annotation.size () == 0);
  annotation.add ("VNG0006G", 251);
  annotation.add ("VNG0006G", 530);
  annotation.add ("VNG0008G", 520);
  annotation.add ("VNG0008G", 522);
  annotation.add ("VNG0009G", 520);
  annotation.add ("VNG0009G", 522);
  annotation.add ("VNG0046G", 40);
  annotation.add ("VNG0046G", 500);
  annotation.add ("VNG0046G", 520);

  assertTrue (annotation.size () == 9);
  assertTrue (annotation.count () == 4);

} // testAdd
//-------------------------------------------------------------------------
/**
 *  add anotations (numbers from the Kegg ontology) and make sure they
 *  can be retrieved by orf names
 */
public void testGet () throws Exception
{ 
  System.out.println ("testGet");

  Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology ();
  Annotation annotation = new Annotation ("Halobacterium Sp.", "pathways", ontology);

  annotation.add ("VNG0006G", 251);
  annotation.add ("VNG0006G", 530);
  annotation.add ("VNG0008G", 520);
  annotation.add ("VNG0008G", 522);
  annotation.add ("VNG0009G", 520);
  annotation.add ("VNG0046G", 40);
  annotation.add ("VNG0046G", 500);
  annotation.add ("VNG0046G", 520);

  String [] names = annotation.getNames ();
  assertTrue (names.length == 4);

  int [] classifications = annotation.getClassifications ("VNG0006G");
  assertTrue (classifications.length == 2);
  assertTrue (classifications [0] == 251);
  assertTrue (classifications [1] == 530);

  classifications = annotation.getClassifications ("VNG0008G");
  assertTrue (classifications.length == 2);
  assertTrue (classifications [0] == 520);
  assertTrue (classifications [1] == 522);

  classifications = annotation.getClassifications ("VNG0009G");
  assertTrue (classifications.length == 1);
  assertTrue (classifications [0] == 520);

  classifications = annotation.getClassifications ("VNG0046G");
  assertTrue (classifications.length == 3);
  assertTrue (classifications [0] == 40);
  assertTrue (classifications [1] == 500);
  assertTrue (classifications [2] == 520);

} // testGet
//-------------------------------------------------------------------------
/**
 *  test calculation of the maximum depth of the ontology hierarchy for
 *  the current annotation
 */
public void testMaxDepth () throws Exception
{ 
  System.out.println ("testMaxDepth");

  Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology ();
  Annotation annotation = new Annotation ("Halobacterium Sp.", "pathways", ontology);

  annotation.add ("VNG0009G", 520);
  assertTrue (annotation.maxDepth () == 3);

} // testMaxDepth
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (AnnotationTest.class));
}
//------------------------------------------------------------------------------
} // AnnotationTest
