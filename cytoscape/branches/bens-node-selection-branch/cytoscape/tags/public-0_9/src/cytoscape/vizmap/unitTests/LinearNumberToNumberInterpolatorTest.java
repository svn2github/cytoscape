// LinearNumberToNumberInterpolatorTest.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.vizmap.LinearNumberToNumberInterpolator;
//----------------------------------------------------------------------------
public class LinearNumberToNumberInterpolatorTest extends TestCase {

//----------------------------------------------------------------------------
    public LinearNumberToNumberInterpolatorTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testFunction () throws Exception {
	LinearNumberToNumberInterpolator li =
	    new LinearNumberToNumberInterpolator();
            
        Integer lowerRange = new Integer(2);
        Integer upperRange = new Integer(3);
        double frac = 0.41;
        Object returnVal = li.getRangeValue(frac, lowerRange, upperRange);
        
        assertTrue(returnVal instanceof Double);
        assertTrue( ((Double)returnVal).doubleValue() - 2.41 < 1.0E-6 );
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (LinearNumberToNumberInterpolatorTest.class));
    }
//----------------------------------------------------------------------------
}
