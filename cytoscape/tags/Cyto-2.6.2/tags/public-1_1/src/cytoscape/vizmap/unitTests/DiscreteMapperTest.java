// DiscreteMapperTest.java

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


