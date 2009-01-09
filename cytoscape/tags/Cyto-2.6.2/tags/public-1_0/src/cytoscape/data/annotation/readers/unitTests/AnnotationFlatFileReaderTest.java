// AnnotationFlatFileReaderTest

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------
// $Revision$  
// $Date$
//------------------------------------------------------------------------------
package cytoscape.data.annotation.readers.unitTests;
//------------------------------------------------------------------------------
import java.io.*; 
import java.util.*;
import junit.framework.*;

import cytoscape.data.annotation.*;
import cytoscape.data.annotation.readers.*;
//------------------------------------------------------------------------------
/**
 * test the AnnotationFlatFileReader class
 */
public class AnnotationFlatFileReaderTest extends TestCase {


//------------------------------------------------------------------------------
public AnnotationFlatFileReaderTest (String name) 
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
 *  read a small sample GO human annotation
 */
public void testReadSmallAnnotation () throws Exception
{ 
  System.out.println ("testReadSmallAnnotation");

  String filename = "../../sampleData/bioproc.txt";
  AnnotationFlatFileReader reader =  new AnnotationFlatFileReader (new File (filename));
  Annotation annotation = reader.getAnnotation ();
  assertTrue (annotation.size () == 31);
  assertTrue (annotation.getSpecies().equals ("Homo sapiens"));
  assertTrue (annotation.getType().equals ("Biological Process"));

  String protein = "IPI00163805"; // = 0030338
  int [] terms = annotation.getClassifications (protein);
  assertTrue (terms.length == 1);
  assertTrue (terms [0] == 30338);

  protein = "IPI00024413";
  terms = annotation.getClassifications (protein);
  assertTrue (terms.length == 3);
    
  
} // testReadSmallAnnotation
//-------------------------------------------------------------------------
/**
 *  read a small sample GO human annotation, add its ontology, probe
 *  full GO hierarchies
 */
public void testReadSmallAnnotationAddFullGoOntology () throws Exception
{ 
  System.out.println ("testReadSmallAnnotationAddFullGoOntology");
  String filename = "../../sampleData/bioproc.txt";
  AnnotationFlatFileReader reader = new AnnotationFlatFileReader (new File (filename));
  Annotation annotation = reader.getAnnotation ();
  assertTrue (annotation.size () == 31);
  assertTrue (annotation.getSpecies().equals ("Homo sapiens"));
  assertTrue (annotation.getType().equals ("Biological Process"));

  String ontologyFile = "../../sampleData/goOntology.txt";
  OntologyFlatFileReader ontologyReader = new OntologyFlatFileReader (new File (ontologyFile));

  Ontology go = ontologyReader.getOntology ();
  assertTrue (go.size () > 13000);

  annotation.setOntology (go);

  String protein = "IPI00163805"; // = 0030338
  int [] terms = annotation.getClassifications (protein);
  assertTrue (terms.length == 1);
  assertTrue (terms [0] == 30338);
  int [][] paths = go.getAllHierarchyPaths (terms [0]);
  assertTrue (paths.length == 2);
  assertTrue (paths [0].length == 5);
  assertTrue (paths [1].length == 6);

  String [][] pathNames = go.getAllHierarchyPathsAsNames (terms [0]);
  assertTrue (pathNames.length == 2);
  assertTrue (pathNames [0].length == 5);
  assertTrue (pathNames [1].length == 6);

  assertTrue (pathNames [0][0].equalsIgnoreCase ("molecular_function"));
  assertTrue (pathNames [0][1].equalsIgnoreCase ("enzyme"));
  assertTrue (pathNames [0][2].equalsIgnoreCase ("oxidoreductase"));
  assertTrue (pathNames [0][3].equalsIgnoreCase ("monooxygenase"));

  assertTrue (pathNames [1][0].equalsIgnoreCase ("molecular_function"));
  assertTrue (pathNames [1][1].equalsIgnoreCase ("enzyme"));
  assertTrue (pathNames [1][2].equalsIgnoreCase ("oxidoreductase"));
  assertTrue (pathNames [1][3].equalsIgnoreCase (
        "oxidoreductase, acting on paired donors, with incorporation or reduction of molecular oxygen"));

    // the tests could go all the way down to the leaf, but the names get longer, and
    // the above tests should reveal anything gone wrong.


  protein = "IPI00024413";
  terms = annotation.getClassifications (protein);
  assertTrue (terms.length == 3);

} // testReadSmallAnnotationAddFullGoOntology
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (AnnotationFlatFileReaderTest.class));

} // main
//------------------------------------------------------------------------------
} // class AnnotationFlatFileReaderTest


