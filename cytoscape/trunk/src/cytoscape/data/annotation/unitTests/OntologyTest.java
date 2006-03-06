
/*
  File: OntologyTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

// OntologyTest.java


//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.annotation.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import cytoscape.data.annotation.*;
import cytoscape.unitTests.AllTests;
//------------------------------------------------------------------------------
/**
 * test the Ontology class
 */
public class OntologyTest extends TestCase {


//------------------------------------------------------------------------------
public OntologyTest (String name) 
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
  String expectedCurator = "KEGG";
  String expectedOntologyType = "Metabolic Pathways";

  Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology ();


  assertTrue (ontology.getCurator().equals (expectedCurator));
  assertTrue (ontology.getType().equals (expectedOntologyType));

  OntologyDescription description = ontology.getDescription ();
  assertTrue (description.getCurator().equals (expectedCurator));
  assertTrue (description.getType().equals (expectedOntologyType));
  
} // testCtor
//-------------------------------------------------------------------------
/**
 * add OntologyTerm's, and get them back
 */
public void testAdd () throws Exception
{ 
  AllTests.standardOut("testAdd");
  String curator = "KEGG";
  String ontologyType = "Metabolic Pathways";
  Ontology ontology = new Ontology (curator, ontologyType);

  OntologyTerm metabolism = new OntologyTerm ("Metabolism", 90001);
  OntologyTerm carbohydrateMetabolism = new OntologyTerm ("Carbohydrate Metabolism", 80001);
  carbohydrateMetabolism.addParent (metabolism.getId ());

  ontology.add (metabolism);
  ontology.add (carbohydrateMetabolism);

  assertTrue (ontology.size () == 2);
  assertTrue (ontology.containsTerm (metabolism.getId ()));
  assertTrue (ontology.containsTerm (carbohydrateMetabolism.getId ()));

  OntologyTerm retrievedTerm0 = ontology.getTerm (metabolism.getId ());
  OntologyTerm retrievedTerm1 = ontology.getTerm (carbohydrateMetabolism.getId ());

  assertTrue (retrievedTerm0.getName ().equals ("Metabolism"));
  assertTrue (retrievedTerm1.getName ().equals ("Carbohydrate Metabolism"));

  assertTrue (ontology.getTerms().size() == 2);
  
} // testAdd
//-------------------------------------------------------------------------
/**
 * and retrieve the full leaf-to-root hierarchy for 
 * a few different terms, expressed as int's, which are the key
 * identifiers of the OntologyTerms held in an Ontology
 */
public void testGetHierarchyInts () throws Exception
{ 
  AllTests.standardOut("testGetHierarchyInts");
  Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology ();

    // a node with a single parent
  int [][] h = ontology.getAllHierarchyPaths (80001);
  assertTrue (h.length == 1);
  assertTrue (h [0].length == 2);
  assertTrue (h [0][0] == 90001);
  assertTrue (h [0][1] == 80001);


    // a node with one parent, one grandparent
  h = ontology.getAllHierarchyPaths (40);
  assertTrue (h.length == 1);
  assertTrue (h [0].length == 3);
  assertTrue (h [0][0] == 90001);
  assertTrue (h [0][1] == 80001);
  assertTrue (h [0][2] == 40);

    // a node with two parents, each with a parent and grandparent
  h = ontology.getAllHierarchyPaths (666);

  assertTrue (h.length == 2);
  assertTrue (h [0].length == 4);
  assertTrue (h [0][0] == 90001);
  assertTrue (h [0][1] == 80007);
  assertTrue (h [0][2] == 500);
  assertTrue (h [0][3] == 666);

  assertTrue (h [1].length == 4);
  assertTrue (h [1][0] == 90001);
  assertTrue (h [1][1] == 80007);
  assertTrue (h [1][2] == 530);
  assertTrue (h [1][3] == 666);

  
} // testGetHierarchyInts
//-------------------------------------------------------------------------
/**
 * as above, but retrieve and test for the ontology term names this time
 */
public void testGetHierarchyNames () throws Exception
{ 
  AllTests.standardOut("testGetHierarchyNames");
  Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology ();

    // a node with a single parent
  String [][] h = ontology.getAllHierarchyPathsAsNames (80001);

  printHierarchy (h);

  assertTrue (h.length == 1);
  assertTrue (h [0].length == 2);
  assertTrue (h [0][0].equals ("Metabolism"));
  assertTrue (h [0][1].equals ("Carbohydrate Metabolism"));



    // a node with one parent, one grandparent
  h = ontology.getAllHierarchyPathsAsNames (40);
  assertTrue (h.length == 1);
  assertTrue (h [0].length == 3);
  assertTrue (h [0][0].equals ("Metabolism"));
  assertTrue (h [0][1].equals ("Carbohydrate Metabolism"));
  assertTrue (h [0][2].equals ("Nucleotide Metabolism"));

    // a node with two parents, each with a parent and grandparent

  h = ontology.getAllHierarchyPathsAsNames (666);
  printHierarchy (h);

  assertTrue (h.length == 2);

  assertTrue (h [0].length == 4);
  assertTrue (h [0][0].equals ("Metabolism"));
  assertTrue (h [0][1].equals ("Glutamate metabolism"));
  assertTrue (h [0][2].equals ("Biosynthesis of Secondary Metabolites"));
  assertTrue (h [0][3].equals ("Two Parents"));

  assertTrue (h [1].length == 4);
  assertTrue (h [1][0].equals ("Metabolism"));
  assertTrue (h [1][1].equals ("Glutamate metabolism"));
  assertTrue (h [1][2].equals ("Starch and sucrose metabolism"));
  assertTrue (h [1][3].equals ("Two Parents"));
  
} // testGetHierarchy
//-------------------------------------------------------------------------
private void printHierarchy (int [][] hierarchy)
{
  for (int i=0; i < hierarchy.length; i++) {
    int [] path = hierarchy [i];
    for (int j=0; j < path.length; j++)
      AllTests.standardOut(path [j] + " ");
      AllTests.standardOut("\n");
    } // for i

} // printHierarchy
//-------------------------------------------------------------------------
private void printHierarchy (String [][] hierarchy)
{
  for (int i=0; i < hierarchy.length; i++) {
    String [] path = hierarchy [i];
    for (int j=0; j < path.length; j++) {
      AllTests.standardOut(path [j]);
      if (j < (path.length -1)) AllTests.standardOut (", ");
      }
    AllTests.standardOut("\n");
    } // for i

} // printHierarchy
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (OntologyTest.class));
}
//------------------------------------------------------------------------------
} // OntologyTest


