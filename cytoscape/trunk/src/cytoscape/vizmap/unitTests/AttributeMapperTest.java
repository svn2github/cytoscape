// AttributeMapperTest.java

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

import java.util.Map;
import java.util.HashMap;

import cytoscape.vizmap.AttributeMapper;
import cytoscape.vizmap.ValueMapper;
import cytoscape.vizmap.DiscreteMapper;

//----------------------------------------------------------------------------
public class AttributeMapperTest extends TestCase {

//----------------------------------------------------------------------------
    public AttributeMapperTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testBasics () throws Exception { 
	AttributeMapper mapper = new AttributeMapper(null);

	Integer i = new Integer(0);
	assertTrue( mapper.getControllingDomainAttributeName(i) == null );
	assertTrue( mapper.getValueMapper(i) == null );

	Map defaultValues = mapper.getDefaultValues();
	assertTrue( defaultValues != null );
	assertTrue( defaultValues.size() == 0 );
	assertTrue( mapper.getDefaultValue(i) == null );
	Object o = new Object();
	mapper.setDefaultValue(i,o);
	assertTrue( mapper.getDefaultValue(i) == o );
	assertTrue( mapper.getDefaultValues().size() == 1 );

	Map dValues = new HashMap();
	mapper.setDefaultValues(dValues);
	defaultValues = mapper.getDefaultValues();
	assertTrue( defaultValues != null );
	assertTrue( defaultValues.size() == 0 );
	assertTrue( mapper.getDefaultValue(i) == null );

	assertTrue( mapper.getUseDefaultsOnly() == false );
	mapper.setUseDefaultsOnly(true);
	assertTrue( mapper.getUseDefaultsOnly() == true );

	dValues.put(i,o);
	AttributeMapper mapper2 = new AttributeMapper(dValues);
	defaultValues = mapper2.getDefaultValues();
	assertTrue( defaultValues != null );
	assertTrue( defaultValues.size() == 1 );
	assertTrue( mapper2.getDefaultValue(i) == o );
    }
//---------------------------------------------------------------------------
    public void testFunctions() throws Exception {
	Map defaultValues = new HashMap();
	defaultValues.put( new Integer(0), new Double(0) );
	AttributeMapper attrMapper = new AttributeMapper(defaultValues);
	assertTrue( attrMapper.getControllingDomainAttributeName(new Integer(0)) == null );
	assertTrue( attrMapper.getValueMapper(new Integer(0)) == null );

	Map valueMap = new HashMap();
	valueMap.put( new String("five"), new Double(5.0) );
	ValueMapper vMapper = new DiscreteMapper(valueMap);

	attrMapper.setAttributeMapEntry( new Integer(0), new String("number"),
					 vMapper);
	assertTrue( attrMapper.getControllingDomainAttributeName(new Integer(0)).equals("number") );
	assertTrue( attrMapper.getValueMapper(new Integer(0)) == vMapper );

	Map attrBundle = new HashMap();
	attrBundle.put("number","five");
	attrBundle.put("data","0.5");

	Object o1 = attrMapper.getRangeValue( attrBundle,new Integer(0) );
	assertTrue( o1 instanceof Double );
	assertTrue( ((Double)o1).doubleValue() == 5.0 );
	Object o2 = attrMapper.getRangeValue( attrBundle, new Integer(1) );
	assertTrue( o2 == null );

	Object o3 = attrMapper.getRangeValue( null, new Integer(0) );
	assertTrue( o3 instanceof Double );
	assertTrue( ((Double)o3).doubleValue() == 0.0 );
	Object o4 = attrMapper.getRangeValue( new HashMap(), new Integer(0) );
	assertTrue( o4 instanceof Double );
	assertTrue( ((Double)o4).doubleValue() == 0.0 );

	attrMapper.setUseDefaultsOnly(true);
	Object o5 = attrMapper.getRangeValue( attrBundle, new Integer(0) );
	assertTrue( o5 instanceof Double );
	assertTrue( ((Double)o5).doubleValue() == 0.0 );

	attrMapper.setUseDefaultsOnly(false);
	Object o6 = attrMapper.getRangeValue( attrBundle, new Integer(0) );
	assertTrue( o6 instanceof Double );
	assertTrue( ((Double)o6).doubleValue() == 5.0 );
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (AttributeMapperTest.class));
    }
//----------------------------------------------------------------------------
}


