// DiscreteMapperTest.java
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

import cytoscape.vizmap.ValueMapper;
import cytoscape.vizmap.DiscreteMapper;

//----------------------------------------------------------------------------
public class DiscreteMapperTest extends TestCase {

//----------------------------------------------------------------------------
    public DiscreteMapperTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testBasics() throws Exception {
	Map myMap = new HashMap();
	DiscreteMapper mapper = new DiscreteMapper(myMap);
	assertTrue( mapper.getValueMap() == myMap );
	Map anotherMap = new HashMap();
	mapper.setValueMap(anotherMap);
	assertTrue( mapper.getValueMap() == anotherMap );

	ValueMapper vMapper = mapper;
    }
//----------------------------------------------------------------------------
    public void testFunction() throws Exception {
	Map myMap = new HashMap();
	Integer key = new Integer(0);
	String value = new String("zero");
	myMap.put( key, value );
	DiscreteMapper mapper = new DiscreteMapper(myMap);
	Object returnVal = mapper.getRangeValue(key);
	assertTrue( returnVal instanceof String );
	String rString = (String)returnVal;
	assertTrue( rString.equals(value) );

	returnVal = mapper.getRangeValue( new Integer(0) );
	assertTrue( returnVal instanceof String );
	rString = (String)returnVal;
	assertTrue( returnVal.equals(value) );

	returnVal = mapper.getRangeValue( new Integer(1) );
	assertTrue( returnVal == null );
    }
//-------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (DiscreteMapperTest.class));
    }
//----------------------------------------------------------------------------
}
