// OntologyTermTest.java
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
 * test the OntologyTerm class
 */
public class OntologyTermTest extends TestCase {


//------------------------------------------------------------------------------
public OntologyTermTest (String name) 
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

  String name = "Carbohydrate Metabolism";
  int id = 80001;
  OntologyTerm term = new OntologyTerm (name, id);

  assertTrue (term.getName().equals (name));  
  assertTrue (term.getId () == id);

  assertTrue (term.numberOfParents () == 0);
  assertTrue (term.numberOfContainers () == 0);
  assertTrue (term.numberOfParentsAndContainers () == 0);

  assertTrue (term.getParents().length == 0);
  assertTrue (term.getContainers().length == 0);
  assertTrue (term.getParentsAndContainers().length == 0);

} // testCtor
//-------------------------------------------------------------------------
public void testAddParentsAndContainers () throws Exception
{ 
  System.out.println ("testAddParentsAndContainers");

  String name = "Carbohydrate Metabolism";
  int id = 80001;
  OntologyTerm term = new OntologyTerm (name, id);

  int [] parents = {123, 456};
  for (int i=0; i < parents.length; i++)
    term.addParent (parents [i]);

  int [] containers = {1, 2, 3, 4};
  for (int i=0; i < containers.length; i++)
    term.addContainer (containers [i]);

  assertTrue (term.getName().equals (name));  
  assertTrue (term.getId () == id);

  assertTrue (term.numberOfParents () == 2);
  assertTrue (term.numberOfContainers () == 4);
  assertTrue (term.numberOfParentsAndContainers () == 6);

  assertTrue (term.getParents().length == 2);
  assertTrue (term.getContainers().length == 4);
  assertTrue (term.getParentsAndContainers().length == 6);

  int [] retrievedParents = term.getParents ();
  assertTrue (retrievedParents [0] == parents [0]);
  assertTrue (retrievedParents [1] == parents [1]);

  int [] retrievedContainers = term.getContainers ();
  assertTrue (retrievedContainers [0] == containers [0]);
  assertTrue (retrievedContainers [1] == containers [1]);
  assertTrue (retrievedContainers [2] == containers [2]);
  assertTrue (retrievedContainers [3] == containers [3]);

} // testAddParentsAndContainers
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (OntologyTermTest.class));
}
//------------------------------------------------------------------------------
} // OntologyTermTest
