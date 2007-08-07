// PluginLoaderTest.java:  a junit test for the class which sets run-time configuration,

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

// usually from command line arguments
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.util.*;
import cytoscape.plugin.PluginLoader;
import cytoscape.CytoscapeConfig;
//------------------------------------------------------------------------------
public class PluginLoaderTest extends TestCase {


//------------------------------------------------------------------------------
public PluginLoaderTest (String name)
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
public void testCtor () throws Exception
{
  AllTests.standardOut ("testCtor");
  String bioDataDirectory = "../data/GO";
  String geometryFilename = "../data/galFiltered.gml";
  String interactionsFilename = "../data/tideker0/yeastSmall.intr";
  String expressionFilename   = "../data/tideker0/gal1-20.mrna";
  String nodeAttributeFile1 = "xxx.foo";
  String nodeAttributeFile2 = "xxx.barA";
  String nodeAttributeFile3 = "xxx.zooC";
  String edgeAttributeFile1 = "xxx.edgeA";
  String edgeAttributeFile2 = "xxx.edgeB";

  String [] args = {"-b", bioDataDirectory,
                    "-g", geometryFilename,
                    "-i", interactionsFilename,
                    "-e", expressionFilename,
                    "-n", nodeAttributeFile1,
                    "-n", nodeAttributeFile2,
                    "-n", nodeAttributeFile3,
                    "-j", edgeAttributeFile1,
                    "-j", edgeAttributeFile2,
                    "-h",
                    "-v",
                    };
  CytoscapeConfig cyConfig = new CytoscapeConfig (args);
  PluginLoader loader = new PluginLoader (null);
  Vector allClasses = loader.getClassesToLoad(cyConfig.getProperties());
  //assertTrue( allClasses.contains("cytoscape.plugins.demo.Foo") );
  //assertTrue( allClasses.contains("cytoscape.plugins.demo.Bar") );


} // testDefaultCtor
//-------------------------------------------------------------------------
public static void main (String [] args)
{
  junit.textui.TestRunner.run (new TestSuite (PluginLoaderTest.class));
}
//------------------------------------------------------------------------------
} // PluginLoaderTest
