// LinearNumberToColorInterpolatorTest.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.vizmap.LinearNumberToColorInterpolator;

import java.awt.Color;
//----------------------------------------------------------------------------
public class LinearNumberToColorInterpolatorTest extends TestCase {

//----------------------------------------------------------------------------
    public LinearNumberToColorInterpolatorTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testFunction () throws Exception {
	LinearNumberToColorInterpolator li =
	    new LinearNumberToColorInterpolator();
	Color c1 = new Color(0,10,20,30);
	Color c2 = new Color(201,191,179,169);

	Object returnVal = li.getRangeValue(0.65,c1,c2);
	assertTrue(returnVal instanceof Color);
	Color cReturn = (Color)returnVal;
	assertTrue( cReturn.getRed() == 131 );
	assertTrue( cReturn.getGreen() == 128 );
	assertTrue( cReturn.getBlue() == 123 );
	assertTrue( cReturn.getAlpha() == 120 );

	Object dummy = new Object();
	returnVal = li.getRangeValue(0.65,c1,dummy);
	assertTrue( returnVal == null);

    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (LinearNumberToColorInterpolatorTest.class));
    }
//----------------------------------------------------------------------------
}
