// BoundaryRangeValuesTest.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.vizmap.BoundaryRangeValues;

//----------------------------------------------------------------------------
public class BoundaryRangeValuesTest extends TestCase {

//----------------------------------------------------------------------------
    public BoundaryRangeValuesTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testAll () throws Exception { 
	BoundaryRangeValues bv = new BoundaryRangeValues();
	assertTrue( bv.lesserValue == null );
	assertTrue( bv.equalValue == null );
	assertTrue( bv.greaterValue == null );

	Double d1 = new Double(1.0);
	Double d2 = new Double(2.0);
	Double d3 = new Double(3.0);
	BoundaryRangeValues bv2 = new BoundaryRangeValues(d1,d2,d3);
	assertTrue( bv2.lesserValue == d1 );
	assertTrue( bv2.equalValue == d2 );
	assertTrue( bv2.greaterValue == d3 );

    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (BoundaryRangeValuesTest.class));
    }
//----------------------------------------------------------------------------
}
