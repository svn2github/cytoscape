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
package cytoscape.data.readers.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;

import y.base.*;
import y.view.Graph2D;

import cytoscape.data.readers.GMLReader;
//-----------------------------------------------------------------------------------------
public class GMLReaderTest extends TestCase {

  private static String testDataDir;

//------------------------------------------------------------------------------
public GMLReaderTest (String name) 
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
public void testSmallGraphRead () throws Exception
{ 
  System.out.println ("testSmallGraphRead");
  GMLReader reader = new GMLReader (testDataDir + "/gal.gml");
  reader.read ();
  Graph2D graph = reader.getGraph ();
  assertTrue ("node count", graph.nodeCount () == 11);
  assertTrue ("edge count",  graph.edgeCount () == 10);

} // testSmallGraphRead
//-------------------------------------------------------------------------
public void testMediumGraphRead () throws Exception
{ 
  System.out.println ("testMediumGraphRead");
  GMLReader reader = new GMLReader (testDataDir + "/noLabels.gml");
  reader.read ();
  Graph2D graph = reader.getGraph ();
  assertTrue ("node count", graph.nodeCount () == 332);
  assertTrue ("edge count",  graph.edgeCount () == 362);

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


