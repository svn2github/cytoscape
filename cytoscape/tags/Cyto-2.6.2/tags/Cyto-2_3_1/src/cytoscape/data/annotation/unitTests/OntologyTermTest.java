
/*
  File: OntologyTermTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// OntologyTermTest.java


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
import cytoscape.unitTests.AllTests;
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
  AllTests.standardOut("testCtor");

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
  AllTests.standardOut ("testAddParentsAndContainers");

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


