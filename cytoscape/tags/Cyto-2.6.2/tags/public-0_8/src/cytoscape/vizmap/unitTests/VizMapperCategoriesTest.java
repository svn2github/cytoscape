// VizMapperCategoriesTest.java
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
	assertTrue( defaults.size() == 10 );
	assertTrue( defaults.get(vcat.NODE_FILL_COLOR) != null );
	Map propMap = cat.getPropertyNamesMap();
	assertTrue( propMap.size() == 10 );
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
