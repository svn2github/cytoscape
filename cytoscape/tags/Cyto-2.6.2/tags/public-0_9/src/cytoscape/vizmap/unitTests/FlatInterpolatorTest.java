// FlatInterpolatorTest.java
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

import cytoscape.vizmap.FlatInterpolator;
//----------------------------------------------------------------------------
public class FlatInterpolatorTest extends TestCase {

//----------------------------------------------------------------------------
    public FlatInterpolatorTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testFunction () throws Exception {
        Double d1 = new Double(1.0);
        Double d2 = new Double(2.0);
        Double dMid = new Double(1.5);
        Color c1 = new Color(0,0,0);
        Color c2 = new Color(255,255,255);
        
	FlatInterpolator li = new FlatInterpolator();
        Object returnVal = li.getRangeValue(d1,c1,d2,c2,dMid);
        assertTrue(returnVal == c1);
        
        FlatInterpolator li2 = new FlatInterpolator(FlatInterpolator.LOWER);
        returnVal = li2.getRangeValue(d1,c1,d2,c2,dMid);
        assertTrue(returnVal == c1);

        FlatInterpolator li3 = new FlatInterpolator(FlatInterpolator.UPPER);
        returnVal = li3.getRangeValue(d1,c1,d2,c2,dMid);
        assertTrue(returnVal == c2);
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (FlatInterpolatorTest.class));
    }
//----------------------------------------------------------------------------
}
