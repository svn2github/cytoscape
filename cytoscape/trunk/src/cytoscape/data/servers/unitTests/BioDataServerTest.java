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
public void testInProcessServer () throws Exception
{ 
  System.out.println ("testInProcessCtor");
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

} // doAnnotationTest
//------------------------------------------------------------------------------
/**
 *  make sure we can read and load any number of xml annotation files 
 */
public void testLoadAnnotationFiles () throws Exception
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
  server.loadAnnotationFiles (absolutePathNames);
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
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (BioDataServerTest.class));
  System.exit (0);  // needed because otherwise UnicastRemoteObject runs forever

}
//------------------------------------------------------------------------------
} // BioDataServerTest


