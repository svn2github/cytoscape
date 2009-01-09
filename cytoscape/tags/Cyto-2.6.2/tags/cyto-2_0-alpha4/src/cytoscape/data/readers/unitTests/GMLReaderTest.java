// GMLReaderTest.java

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
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.readers.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import cytoscape.data.readers.GMLReader;
import cytoscape.unitTests.AllTests;
//-----------------------------------------------------------------------------------------
public class GMLReaderTest extends TestCase {
  private static String testDataDir;

//------------------------------------------------------------------------------
public GMLReaderTest (String name) 
{
  super (name);
  if(AllTests.runAllTests()) {
      testDataDir = "testData";
  }
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
public void testSmallGraphRead () throws Exception
{ 
  AllTests.standardOut("testSmallGraphRead");
  //GMLReader reader = new GMLReader (testDataDir + "/gal.gml");
  //reader.read ();
  //RootGraph graph = reader.getRootGraph ();
  
  Cytoscape.clearCytoscape();
  CyNetwork network = Cytoscape.createNetwork( testDataDir + "/gal.gml" );

  assertTrue ("node count", network.getNodeCount () == 11);
  assertTrue ("edge count", network.getEdgeCount () == 10);

} // testSmallGraphRead
//-------------------------------------------------------------------------
public void testMediumGraphRead () throws Exception
{ 
  AllTests.standardOut ("testMediumGraphRead");
  // GMLReader reader = new GMLReader (testDataDir + "/noLabels.gml");
  //reader.read ();
  //RootGraph graph = reader.getRootGraph ();
  
  Cytoscape.clearCytoscape();
  CyNetwork network = Cytoscape.createNetwork( testDataDir + "/noLabels.gml" );

  assertTrue ("node count", network.getNodeCount () == 332);
  // changed to 361, since there must be a dupe...
  assertTrue ("edge count", network.getEdgeCount () == 361);

} // testMediumGraphRead
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  if (args.length != 1) {
    System.out.println ("Error!  must supply path to test data directory on command line");
    System.exit (0);
    }

  testDataDir = args [0];

  junit.textui.TestRunner.run (new TestSuite (GMLReaderTest.class));
}
//------------------------------------------------------------------------------
} // GMLReaderTest
