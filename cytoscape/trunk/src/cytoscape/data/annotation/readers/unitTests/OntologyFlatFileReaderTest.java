// OntologyFlatFileReaderTest

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
 * test the OntologyFlatFileReader class
 */
public class OntologyFlatFileReaderTest extends TestCase {

  static String filename = "sampleData/keggOntology.xml";

//------------------------------------------------------------------------------
public OntologyFlatFileReaderTest (String name) 
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
 * make sure that we can read a very small subset of the full go ontology,
 * and get back selected values
 */
public void testReadGoOntologySmall () throws Exception
{ 
  System.out.println ("testReadGoOntologySmall");
  String filename = "../../sampleData/goSmall.txt";
  String curator = "GO";
  String ontologyType = "all";
  OntologyFlatFileReader reader = new OntologyFlatFileReader (new File (filename));
  Ontology ontology = reader.getOntology ();

  assertTrue (ontology.getCurator().equals ("GO"));
  assertTrue (ontology.getType().equals ("all"));
  assertTrue (ontology.size () == 22);
  
  assertTrue (ontology.containsTerm (8607));
  OntologyTerm term8607 = ontology.getTerm (8607);
  assertTrue (term8607.getName().equalsIgnoreCase ("phosphorylase kinase, regulator"));
  int [] parents = term8607.getParents ();
  assertTrue (parents.length == 1);
  assertTrue (parents [0] == 19887);
  int [] containers = term8607.getContainers ();
  assertTrue (containers.length == 1);
  assertTrue (containers [0] == 4689);

  assertTrue (ontology.containsTerm (16505));
  OntologyTerm term16505 = ontology.getTerm (16505);
  assertTrue (term16505.getName().equalsIgnoreCase ("apoptotic protease activator"));
  parents = term16505.getParents ();
  assertTrue (parents.length == 2);
  assertTrue (parents [0] == 16504);
  assertTrue (parents [1] == 16506);
  containers = term16505.getContainers ();
  assertTrue (containers.length == 2);
  assertTrue (containers [0] == 4889);
  assertTrue (containers [1] == 16304);


//name: apoptotic protease activator
//id: 16505
//parents: 16504 16506 
//containers: 4889 16304 


} // testRead
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (OntologyFlatFileReaderTest.class));

} // main
//------------------------------------------------------------------------------
} // class OntologyFlatFileReaderTest


