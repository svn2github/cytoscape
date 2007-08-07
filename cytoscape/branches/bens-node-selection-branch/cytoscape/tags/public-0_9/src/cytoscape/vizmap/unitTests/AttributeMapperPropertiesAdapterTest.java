// AttributeMapperPropertiesAdapterTest.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import java.util.Properties;

import cytoscape.vizmap.AttributeMapperPropertiesAdapter;
import cytoscape.vizmap.AttributeMapper;
import cytoscape.vizmap.AttributeMapperCategories;
import cytoscape.vizmap.VizMapperCategories;

//----------------------------------------------------------------------------
public class AttributeMapperPropertiesAdapterTest extends TestCase {

//----------------------------------------------------------------------------
    public AttributeMapperPropertiesAdapterTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testBasics () throws Exception { 
	AttributeMapperPropertiesAdapter a =
	    new AttributeMapperPropertiesAdapter();
	assertTrue( a.getAttributeMapper() == null );
	assertTrue( a.getAttributeMapperCategories() == null );

	AttributeMapper mapper = new AttributeMapper(null);
	a.setAttributeMapper(mapper);
	assertTrue( a.getAttributeMapper() == mapper );

	AttributeMapperCategories cat = new VizMapperCategories();
	a.setAttributeMapperCategories(cat);
	assertTrue( a.getAttributeMapperCategories() == cat );

	AttributeMapperPropertiesAdapter a2 =
	    new AttributeMapperPropertiesAdapter(mapper, cat);
	assertTrue( a2.getAttributeMapper() == mapper );
	assertTrue( a2.getAttributeMapperCategories() == cat );
    }
//----------------------------------------------------------------------------
    public void testNullSafety() throws Exception {
	AttributeMapperPropertiesAdapter a =
	    new AttributeMapperPropertiesAdapter();
	a.applyRangeProperties( new Integer(0), null );
	a.applyAllRangeProperties(null);
	Properties props = new Properties();
	props.put("name","value");
	a.applyAllRangeProperties(props);
	a.applyRangeProperties( new Integer(0), props );

	AttributeMapper mapper = new AttributeMapper(null);
	a.setAttributeMapper(mapper);
	a.applyRangeProperties( new Integer(0), null );
	a.applyAllRangeProperties(null);
	a.applyAllRangeProperties(props);
	a.applyRangeProperties( new Integer(0), props );

	a.setAttributeMapper(null);
	AttributeMapperCategories cat = new VizMapperCategories();
	a.setAttributeMapperCategories(cat);
	a.applyRangeProperties( new Integer(0), null );
	a.applyAllRangeProperties(null);
	a.applyAllRangeProperties(props);
	a.applyRangeProperties( new Integer(0), props );
    }
//---------------------------------------------------------------------------
    /* applyAllRangeProperties() method currently tested within the
     * AttributeMapperCategoriesTest; since this method calls the
     * applyRangeProperties(rangeAttribute) method, I'm assuming that
     * is also correct for now, and I'll write the explicit test later.
     */
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (AttributeMapperPropertiesAdapterTest.class));
    }
//----------------------------------------------------------------------------
}
