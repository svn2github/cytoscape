
/*
  File: AnnotationAndOntologyFullTest.java 
  
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

// AnnotationAndOntologyFullTest.java


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
import cytoscape.data.annotation.readers.*;
import cytoscape.unitTests.AllTests;
//------------------------------------------------------------------------------
/**
 * test the Annotation class
 */
public class AnnotationAndOntologyFullTest extends TestCase {


//------------------------------------------------------------------------------
public AnnotationAndOntologyFullTest (String name) 
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
 * an ad hoc test which pinpointed a bug in the following term in my xml version 
 * of kegg pathway ontology.  i had reversed the 'id' and 'isa' values.
 *  
 *  <ontologyTerm>
 *    <name> Unknown Metabolism of Other Amino Acids Subtype </name>
 *    <id> 80106 </id>
 *    <isa> 80006 </isa>    
 *  </ontologyTerm>
 * 
 *  80006 is 'Metabolism of Other Amino Acids'
 * this reversal caused halo's VNG0606G to be described as
 *
 * VNG0606G:   90001  80005  272  
 * VNG0606G:   80006  450  
 * VNG0606G:   90001  80002  920  
 *
 * rather than
 *
 * VNG0606G:  90001  80005  272  
 * VNG0606G:  90001  80006  450  
 * VNG0606G:  90001  80002  920  
 *
 * with my xml bug, the 1st level categorization of annotation #2 is 80006, not 90001
 * 
 */
public void testHaloKegg () throws Exception
{ 
  AllTests.standardOut("testHaloKegg");
  String filename = "testData/haloMetabolicPathway.xml";
  //if (AllTests.runAllTests()) {
//    filename = "src/cytoscape/data/kegg/haloMetabolicPathway.xml";
//  } else {
//    filename = "../../kegg/haloMetabolicPathway.xml";
//  }
  AnnotationXmlReader reader = new AnnotationXmlReader (new File (filename));
  Annotation keggHalo = reader.getAnnotation ();
  AllTests.standardOut (keggHalo.toString());
  String orf = "VNG0606G";
  int [] terms = keggHalo.getClassifications (orf);

  Ontology ontology = keggHalo.getOntology ();
  assertTrue (terms.length == 3);
  for (int i=0; i < terms.length; i++) {
    int [][] paths = ontology.getAllHierarchyPaths (terms [i]);
    AllTests.standardOut  (orf + ": " + terms [i] + " paths: " + paths.length + ":  ");
    for (int p=0; p < paths.length; p++) {
      assertTrue (paths[p].length == 3);
      for (int q=0; q < paths [p].length; q++)
        AllTests.standardOut (paths [p][q] + "  ");
      } // for p
      AllTests.standardOut ("\n");
    } // for i

} // testHaloKegg
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (AnnotationAndOntologyFullTest.class));
}
//------------------------------------------------------------------------------
} // AnnotationAndOntologyFullTest


