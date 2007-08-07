// BioDataServerTest.java

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
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.servers.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.data.annotation.*;
import cytoscape.data.annotation.readers.*;
import cytoscape.data.servers.*;
//------------------------------------------------------------------------------
/**
 * test the DataServer class, running it in process (not via RMI)
 */
public class BioDataServerTest extends TestCase {


//------------------------------------------------------------------------------
public BioDataServerTest (String name) 
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
 * create an rmi client of the already running (but not already loaded)
 * test rmi data server  once we have a server object, pass it to the 
 * various data-specific testing methods to make sure it behaves properly.
 */
public void no_testRmiServer () throws Exception
{ 
  System.out.println ("testRmiCtor");
  BioDataServer server = new BioDataServer ("rmi://localhost/test");
  server.clear ();
  doAnnotationTest (server);

  server.clear ();
  doDoubleLoadAnnotationTest (server);

} // testRmiServer
//------------------------------------------------------------------------------
/**
 * create an in-process data server, load it with some data, and 
 * then pass it to the various data-specific testing methods to make 
 * sure it behaves properly.
 */
public void notestInProcessServer () throws Exception
{ 
  System.out.println ("testInProcessServer");
  BioDataServer server = new BioDataServer ("./loadList");
  System.out.println ("-- server:\n" + server.describe ());

  assertTrue (server.getAnnotationCount () == 2);
  AnnotationDescription [] aDescs = server.getAnnotationDescriptions ();
  System.out.println ("aDescs: " + aDescs.length);
  for (int i=0; i < aDescs.length; i++)
    System.out.println (aDescs [i]);

  String species = "Halobacterium sp.";
  String curator = "KEGG";
  String annotationType = "Pathways";
  int size = 1476;

  Annotation retrievedAnnotation = server.getAnnotation (species, curator, annotationType);
  System.out.println ("size: " + retrievedAnnotation.size ());
  assertTrue (retrievedAnnotation.size () == size);

  species = "Homo sapiens";
  curator = "GO";
  annotationType = "all GO ontologies";
  size = 18;

  retrievedAnnotation = server.getAnnotation (species, curator, annotationType);
  System.out.println ("size: " + retrievedAnnotation.size ());
  assertTrue (retrievedAnnotation.size () == size);

  server.clear ();
  doAnnotationTest (server);

  server.clear ();
  doDoubleLoadAnnotationTest (server);


} // testInProcessServer
//------------------------------------------------------------------------------
/**
 * create an in-process data server, load it with some data, and 
 * then pass it to the various data-specific testing methods to make 
 * sure it behaves properly.
 */
public void testInProcessServerUsingFlatFilesSmall () throws Exception
{ 
  System.out.println ("testInProcessServerUsingFlatFilesSmall");
  BioDataServer server = new BioDataServer ();
  String filename = "../../annotation/sampleData/bioprocHumanFull.txt";
  String species;
  String annotationType;

  AnnotationFlatFileReader reader = new AnnotationFlatFileReader (new File (filename));
  Annotation annotation = reader.getAnnotation ();
  assertTrue (annotation.getSpecies().equals ("Homo sapiens"));
  assertTrue (annotation.getType().equals ("Biological Process"));
  

  String ontologyFile = "../../annotation/sampleData/goOntology.txt";

  OntologyFlatFileReader ontologyReader = new OntologyFlatFileReader (new File (ontologyFile));
  Ontology go = ontologyReader.getOntology ();

  annotation.setOntology (go);
  server.addAnnotation (annotation);
  AnnotationDescription [] aDescs = server.getAnnotationDescriptions ();
  for (int i=0; i < aDescs.length; i++)
    System.out.println (aDescs [i]);

  System.out.println ("-- server:\n" + server.describe ());

  AnnotationDescription adesc = new AnnotationDescription ("Homo sapiens", "GO", "Biological Process");
  Annotation retrieved = server.getAnnotation (adesc);
  
  String protein = "IPI00163805"; // = 0030338
  int [] terms = retrieved.getClassifications (protein);
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
  System.out.println ("---- " + pathNames [0][3]);

  assertTrue (pathNames [1][0].equalsIgnoreCase ("molecular_function"));
  assertTrue (pathNames [1][1].equalsIgnoreCase ("enzyme"));
  assertTrue (pathNames [1][2].equalsIgnoreCase ("oxidoreductase"));
  assertTrue (pathNames [1][3].equalsIgnoreCase (
        "oxidoreductase, acting on paired donors, with incorporation or reduction of molecular oxygen"));

    // the tests could go all the way down to the leaf, but the names get longer, and
    // the above tests should reveal anything gone wrong.


  protein = "IPI00024413";
  terms = retrieved.getClassifications (protein);
  assertTrue (terms.length == 3);



} // testInProcessServerSmall
//------------------------------------------------------------------------------
private void doAnnotationTest (BioDataServer server) throws Exception
{
  File xmlFile = new File ("../../kegg/haloMetabolicPathway.xml").getAbsoluteFile();
  AnnotationXmlReader reader = new AnnotationXmlReader (xmlFile);
  server.addAnnotation (reader.getAnnotation ());

  String species = "Halobacterium sp.";
  String curator = "KEGG";
  String annotationType = "Pathways";
  String annotationString = "annotation: " + curator + ", " + annotationType + ", " + species;

  assertTrue (server.getAnnotationCount () == 1);
  String description = server.describe ();
  assertTrue (description.indexOf (annotationString) >= 0);
  AnnotationDescription [] aDescs = server.getAnnotationDescriptions ();
  assertTrue (aDescs.length == 1);
  assertTrue (aDescs [0].toString().indexOf (annotationString) >= 0);

  Annotation keggAnnotation = server.getAnnotation (species, curator, annotationType);
  assertTrue (keggAnnotation.toString().indexOf (annotationString) >= 0);

} // doAnnotationTest
//------------------------------------------------------------------------------
/**
 * in version 1.6 and before of the BioDataServerRmi class, adding a second
 * annotations with the same key (species, curator, type) as the first 
 * caused the first one to be overwritten.
 *
 * this test ensures that that no longer happens.
 *
 */
private void doDoubleLoadAnnotationTest (BioDataServer server) throws Exception
{
  File xmlFile = new File ("../../go/prostasomes39.xml").getAbsoluteFile();
  AnnotationXmlReader reader = new AnnotationXmlReader (xmlFile);
  Annotation humanProstasomesAnnotation = reader.getAnnotation ();
  int prostasomeSize = humanProstasomesAnnotation.size ();
  String species = humanProstasomesAnnotation.getSpecies ();
  String curator = humanProstasomesAnnotation.getCurator ();
  String annotationType = humanProstasomesAnnotation.getType ();

  server.addAnnotation (humanProstasomesAnnotation);

  assertTrue (server.getAnnotationCount () == 1);
  Annotation retrievedAnnotation = server.getAnnotation (species, curator, annotationType);
  assertTrue (retrievedAnnotation.size () == prostasomeSize);

  xmlFile = new File ("../../go/bogus5.xml").getAbsoluteFile();
  reader = new AnnotationXmlReader (xmlFile);
  Annotation bogusHumanAnnotation = reader.getAnnotation ();
  int bogusSize = bogusHumanAnnotation.size ();

  server.addAnnotation (bogusHumanAnnotation);
  retrievedAnnotation = server.getAnnotation (species, curator, annotationType);
  assertTrue (server.getAnnotationCount () == 1);
  assertTrue (retrievedAnnotation.size () == prostasomeSize + bogusSize);

} // doDoubleLoadAnnotationTest
//------------------------------------------------------------------------------
/**
 *  make sure we can read and load any number of xml annotation files 
 */
public void notestLoadAnnotationFiles () throws Exception
{
  System.out.println ("testLoadAnnotationFiles");
  BioDataServer server = new BioDataServer ();
  String [] annotationFiles = {"goCellularComponent.xml",
                               "keggMetabolicPathway.xml"};
  String [] absolutePathNames = new String [annotationFiles.length];
  for (int i=0; i < annotationFiles.length; i++) {
    File f = new File (annotationFiles [i]);
    absolutePathNames [i] = f.getAbsolutePath ();
    System.out.println (absolutePathNames [i]);
    }
  //server.loadAnnotationFiles (absolutePathNames);
  //server.loadAnnotationFiles (annotationFiles);
  AnnotationDescription [] aDescs = server.getAnnotationDescriptions ();
  for (int i=0; i < aDescs.length; i++)
    System.out.println (aDescs [i]);


} // testLoadAnnotationFiles
//-------------------------------------------------------------------------
public void testThesaurusFromFlatFile () throws Exception
{
  System.out.println ("testThesaurusFromFlatFile");
  BioDataServer server = new BioDataServer ();
  String [] thesaurusFiles = {"yeastThesaurusSmall.txt"};
  server.loadThesaurusFiles (thesaurusFiles);

  String species = "Saccharomyces cerevisiae";
  String canonicalName = "YOL165C";
  String commonName = "AAD15";
  assertTrue (server.getCommonName (species, canonicalName).equals (commonName));

  canonicalName = "YPR060C";
  commonName = "ARO7";
  assertTrue (server.getCommonName (species, canonicalName).equals (commonName));
  assertTrue (server.getCommonName (species, commonName).equals (commonName));
  assertTrue (server.getCanonicalName (species, commonName).equals (canonicalName));
  assertTrue (server.getCanonicalName (species, canonicalName).equals (canonicalName));

  String [] allCommonNames = server.getAllCommonNames (species, canonicalName);
  assertTrue (allCommonNames.length == 4);

} // testThesaurusFromFlatFile
//-------------------------------------------------------------------------
public void testThesaurusWithAbsentEntries () throws Exception
{
  System.out.println ("testThesaurusWithAbsentEntries");
  BioDataServer server = new BioDataServer ();
  String [] thesaurusFiles = {"yeastThesaurusSmall.txt"};
  server.loadThesaurusFiles (thesaurusFiles);

  String species = "duck";
  String canonicalName = "duck37";
  String commonName = "duck37";

  assertTrue (server.getCommonName ("duck", "mallardase").equals ("mallardase"));
  assertTrue (server.getCanonicalName ("duck", "mallardase").equals ("mallardase"));

  String [] allCommonNames = server.getAllCommonNames ("duck", "grebase");
  assertTrue (allCommonNames.length == 1);

} //  testThesaurusWithAbsentEntries
//-------------------------------------------------------------------------
private void doFlatFileAnnotationTest (BioDataServer server) throws Exception
{
//  File xmlFile = new File ("../../kegg/haloMetabolicPathway.xml").getAbsoluteFile();
//  AnnotationXmlReader reader = new AnnotationXmlReader (xmlFile);
//  server.addAnnotation (reader.getAnnotation ());
//
//  String species = "Halobacterium sp.";
//  String curator = "KEGG";
//  String annotationType = "Pathways";
//  String annotationString = "annotation: " + curator + ", " + annotationType + ", " + species;
//
//  assertTrue (server.getAnnotationCount () == 1);
//  String description = server.describe ();
//  assertTrue (description.indexOf (annotationString) >= 0);
//  AnnotationDescription [] aDescs = server.getAnnotationDescriptions ();
//  assertTrue (aDescs.length == 1);
//  assertTrue (aDescs [0].toString().indexOf (annotationString) >= 0);
//
//  Annotation keggAnnotation = server.getAnnotation (species, curator, annotationType);
//  assertTrue (keggAnnotation.toString().indexOf (annotationString) >= 0);


  String filename = "../../annotation/sampleData/bioproc.txt";
  String species = "Homo sapiens";
  String type = "biological process";
  AnnotationFlatFileReader reader = new AnnotationFlatFileReader (new File (filename));
  Annotation annotation = reader.getAnnotation ();
  String ontologyFile = "../../sampleData/goOntology.txt";
  OntologyFlatFileReader ontologyReader = new OntologyFlatFileReader (new File (ontologyFile));
  Ontology go = ontologyReader.getOntology ();
  annotation.setOntology (go);
  server.addAnnotation (annotation);
  AnnotationDescription [] aDescs = server.getAnnotationDescriptions ();
  for (int i=0; i < aDescs.length; i++)
    System.out.println (aDescs [i]);

  /*******************************
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
  ***********************/

} // doFlatFileAnnotationTest
//------------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (BioDataServerTest.class));
  System.exit (0);  // needed because otherwise UnicastRemoteObject runs forever

}
//------------------------------------------------------------------------------
} // BioDataServerTest


