// ContinuousMapperTest.java

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

import java.util.SortedMap;
import java.util.TreeMap;

import java.awt.Color;

import cytoscape.vizmap.BoundaryRangeValues;
import cytoscape.vizmap.Interpolator;
import cytoscape.vizmap.ValueMapper;
import cytoscape.vizmap.ContinuousMapper;
import cytoscape.vizmap.LinearNumberToColorInterpolator;

//----------------------------------------------------------------------------
public class ContinuousMapperTest extends TestCase {

//----------------------------------------------------------------------------
    public ContinuousMapperTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testBasics() throws Exception {
	ContinuousMapper mapper = new ContinuousMapper();
	assertTrue( mapper.getValueMap() != null );
	assertTrue( mapper.getBoundaryRangeValuesMap() != null );
	assertTrue( mapper.getInterpolator() == null );
	Object returnVal = mapper.getRangeValue( new Object() );
	assertTrue( returnVal == null );

	SortedMap myMap = new TreeMap();
	mapper.setBoundaryRangeValuesMap(myMap);
	assertTrue( mapper.getValueMap() == myMap );
	assertTrue( mapper.getBoundaryRangeValuesMap() == myMap );
	Interpolator i = new LinearNumberToColorInterpolator();
	mapper.setInterpolator(i);
	assertTrue( mapper.getInterpolator() == i );

	ContinuousMapper mapper2 = new ContinuousMapper(myMap,i);
	assertTrue( mapper2.getValueMap() == myMap );
	assertTrue( mapper.getBoundaryRangeValuesMap() == myMap );
	assertTrue( mapper2.getInterpolator() == i );

	SortedMap anotherMap = new TreeMap();
	mapper2.setBoundaryRangeValuesMap(anotherMap);
	assertTrue( mapper2.getValueMap() == anotherMap );
	assertTrue( mapper2.getBoundaryRangeValuesMap() == anotherMap );
	Interpolator i2 = new LinearNumberToColorInterpolator();
	mapper2.setInterpolator(i2);
	assertTrue( mapper2.getInterpolator() == i2 );

	ValueMapper vMapper = mapper2;
	returnVal = vMapper.getRangeValue( new Object() );
	assertTrue( returnVal == null );
    }
//----------------------------------------------------------------------------
    public void testFunction() throws Exception {
	Double key0 = new Double(-4.0);
	BoundaryRangeValues bv0 =
	    new BoundaryRangeValues( new Color(255,0,255),
				     new Color(0,0,0),
				     new Color(200,20,10) );
	Double key1 = new Double(0.0);
	BoundaryRangeValues bv1 =
	    new BoundaryRangeValues( new Color(255,255,255),
				     new Color(255,255,0),
				     new Color(255,255,255) );
	Double key2 = new Double(4.0);
	BoundaryRangeValues bv2 =
	    new BoundaryRangeValues( new Color(15,30,210),
				     new Color(100,100,100),
				     new Color(0,255,255) );
	SortedMap myMap = new TreeMap();
	myMap.put(key0,bv0);
	myMap.put(key1,bv1);
	myMap.put(key2,bv2);
	Interpolator i = new LinearNumberToColorInterpolator();
	ContinuousMapper mapper = new ContinuousMapper(myMap,i);

	Object returnVal = mapper.getRangeValue( new Double(-5.0) );
	assertTrue( returnVal instanceof Color );
	Color c1 = (Color)returnVal;
	assertTrue( c1.getRed() == 255 );
	assertTrue( c1.getGreen() == 0 );
	assertTrue( c1.getBlue() == 255 );
	Color c2 = (Color)(mapper.getRangeValue( new Double(-4.0) ));
	assertTrue( c2.getRed() == 0 );
	assertTrue( c2.getGreen() == 0 );
	assertTrue( c2.getBlue() == 0 );
	Color c3 = (Color)(mapper.getRangeValue( new Double(-3.0) ));
	assertTrue( c3.getRed() == 214 );
	assertTrue( c3.getGreen() == 79 );
	assertTrue( c3.getBlue() == 71 );
	Color c4 = (Color)(mapper.getRangeValue( new Double(0.0) ));
	assertTrue( c4.getRed() == 255 );
	assertTrue( c4.getGreen() == 255 );
	assertTrue( c4.getBlue() == 0 );
	Color c5 = (Color)(mapper.getRangeValue( new Double(1.5) ));
	assertTrue( c5.getRed() == 165 );
	assertTrue( c5.getGreen() == 171 );
	assertTrue( c5.getBlue() == 238 );
	Color c6 = (Color)(mapper.getRangeValue( new Double(4.0) ));
	assertTrue( c6.getRed() == 100 );
	assertTrue( c6.getGreen() == 100 );
	assertTrue( c6.getBlue() == 100 );
	Color c7 = (Color)(mapper.getRangeValue( new Double(5.0) ));
	assertTrue( c7.getRed() == 0 );
	assertTrue( c7.getGreen() == 255 );
	assertTrue( c7.getBlue() == 255 );
    }
//-------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (ContinuousMapperTest.class));
    }
//----------------------------------------------------------------------------
}


