// VizMapperCategoriesTest.java

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import java.awt.Color;
import java.util.*;

import cytoscape.vizmap.Interpolator;
import cytoscape.vizmap.VizMapperCategories;
import cytoscape.vizmap.AttributeMapperCategories;
import cytoscape.vizmap.AttributeMapperPropertiesAdapter;
import cytoscape.vizmap.AttributeMapper;

//----------------------------------------------------------------------------
public class VizMapperCategoriesTest extends TestCase {

//----------------------------------------------------------------------------
    public VizMapperCategoriesTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testInheritedMethods () throws Exception { 
	VizMapperCategories vcat = new VizMapperCategories();
	AttributeMapperCategories cat = vcat;
	Map defaults = cat.getInitialDefaults();
	assertTrue( defaults.size() == 11 );
	assertTrue( defaults.get(vcat.NODE_FILL_COLOR) != null );
	Map propMap = cat.getPropertyNamesMap();
	assertTrue( propMap.size() == 11 );
	assertTrue( propMap.get(vcat.NODE_FILL_COLOR) != null );
	String cString = "255,0,0";
	Object o = cat.parseRangeAttributeValue( vcat.NODE_FILL_COLOR,
						 cString);
	assertTrue( o != null );
	assertTrue( o instanceof Color);
	Color c = (Color)o;
	assertTrue( c.getRed() == 255 );
	assertTrue( c.getGreen() == 0 );
	assertTrue( c.getBlue() == 0 );
	Interpolator i = cat.getInterpolator(vcat.NODE_FILL_COLOR);
	assertTrue( i != null );
    }
//---------------------------------------------------------------------------
    public void testSpecificMethods() throws Exception {
	VizMapperCategories cat = new VizMapperCategories();
	AttributeMapper mapper = new AttributeMapper(cat.getInitialDefaults());
	AttributeMapperPropertiesAdapter adapter =
	    new AttributeMapperPropertiesAdapter(mapper, cat);
	Properties props = new Properties();
	InputStream istream = new FileInputStream("test.props");
	props.load(istream);

	adapter.applyAllRangeProperties(props);

	Map attrBundle = new HashMap();
	attrBundle.put("expression", new Double(-3.0) );
	attrBundle.put("interaction","pd");

	Color cNode = cat.getNodeFillColor(attrBundle, mapper);
	Color nTest = new Color(255, 64, 64);
	assertTrue( cNode.equals(nTest) );

	Color cEdge = cat.getEdgeColor(attrBundle, mapper);
	Color eTest = new Color(255,255,0);
	assertTrue( cEdge.equals(eTest) );

	/* need tests for the other methods once extra code is written */
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (VizMapperCategoriesTest.class));
    }
//----------------------------------------------------------------------------
}


