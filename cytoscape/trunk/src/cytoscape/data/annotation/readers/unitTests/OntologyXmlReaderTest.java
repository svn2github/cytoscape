// OntologyXmlReaderTest

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
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
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
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.*;
import junit.framework.*;

import cytoscape.data.annotation.OntologyTerm;
import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.readers.OntologyXmlReader;
import cytoscape.data.annotation.readers.*;
//------------------------------------------------------------------------------
/**
 * test the OntologyXmlReader class
 */
public class OntologyXmlReaderTest extends TestCase {

  static String filename = "sampleData/keggOntology.xml";

//------------------------------------------------------------------------------
public OntologyXmlReaderTest (String name) 
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
public void testReadKeggOntology () throws Exception
{ 
  System.out.println ("testReadKeggOntology");
  String filename = "../../sampleData/keggOntology.xml";
  OntologyXmlReader reader = new OntologyXmlReader (new File (filename));
  Ontology ontology = reader.getOntology ();

  assertTrue (ontology.getCurator().equals ("KEGG"));
  assertTrue (ontology.getType().equals ("Pathways"));
  assertTrue (ontology.size () == 194);
  
} // testRead
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (OntologyXmlReaderTest.class));

} // main
//------------------------------------------------------------------------------
} // class OntologyXmlReaderTest


