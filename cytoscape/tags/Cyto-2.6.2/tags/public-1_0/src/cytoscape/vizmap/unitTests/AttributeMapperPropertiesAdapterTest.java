// AttributeMapperPropertiesAdapterTest.java

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


