// AnnotationReaderTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.data.readers.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import csplugins.data.readers.*;
//------------------------------------------------------------------------------
/**
 * test the AnnotationReader class
 */
public class AnnotationReaderTest extends TestCase {


//------------------------------------------------------------------------------
public AnnotationReaderTest (String name) 
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
public void testReadKeggAnnotation () throws Exception
{ 
  System.out.println ("testReadKeggAnnotation");

  String filename = "sampleData/keggSample.annotation";
  AnnotationReader reader = new AnnotationReader (filename);
  HashMap annotations = reader.getHashMap ();
  assertTrue (annotations.size () == 5);
  String [] names = reader.getNames ();
  assertTrue (names.length == 5);

  String [] expectedNames =  {"VNG0006G", "VNG0008G", "VNG0009G", "VNG0046G", "VNG0047G"};
  int    [] expectedCounts = {2, 2, 2, 3, 1};
 
  for (int i=0; i < 5; i++) {
    int [] ids = reader.getAnnotationIDs (expectedNames [i]);
    assertTrue (ids.length == expectedCounts [i]);
    }
  
} // testReadKeggAnnotation
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (AnnotationReaderTest.class));

} // main
//------------------------------------------------------------------------------
} // AnnotationReaderTest
