// AnnotationDescriptionTest
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.data.annotation.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import csplugins.data.annotation.*;
//------------------------------------------------------------------------------
/**
 *  test the AnnotationDescription class, especially with regard to 
 *  the 'equals' and 'hashCode' member functions
 */
public class AnnotationDescriptionTest extends TestCase {


//------------------------------------------------------------------------------
public AnnotationDescriptionTest (String name) 
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
  String species = "Halobacterium sp.";
  String curator = "KEGG";
  String type = "Metabolic Pathway";

  AnnotationDescription desc = 
        new AnnotationDescription (species, curator, type);
                           
  assertTrue (desc.getSpecies().equals (species));
  assertTrue (desc.getCurator().equals (curator));
  assertTrue (desc.getType().equals (type));
  
} // testCtor
//-------------------------------------------------------------------------
/**
 * make sure that the equals method judges equality by the equality
 * of the constitutent parts -- in this case, 3 Strings
 */
public void testEquals () throws Exception
{ 
  System.out.println ("testEquals");
  String species = "Halobacterium sp.";
  String curator = "KEGG";
  String type = "Metabolic Pathway";

  AnnotationDescription desc0 = 
        new AnnotationDescription (species, curator, type);
                           
  AnnotationDescription desc1 = 
        new AnnotationDescription (species, curator, type);
                           
  AnnotationDescription desc2 = 
        new AnnotationDescription ("home sapiens", curator, type);
                           
  assertTrue (desc0.equals (desc1));
  assertTrue (!desc0.equals (desc2));
  assertTrue (!desc0.equals (new Integer (99)));

} // testEquals
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (AnnotationDescriptionTest.class));
}
//------------------------------------------------------------------------------
} // AnnotationDescriptionTest
