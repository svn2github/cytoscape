// FloatArrayDataProviderTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.common.vectormath.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import csplugins.common.vectormath.*;
//------------------------------------------------------------------------------
public class FloatArrayDataProviderTest extends TestCase {

//------------------------------------------------------------------------------
public FloatArrayDataProviderTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testBasics() throws Exception {
    float[] data = new float[3];
    data[0] = -1.0f;
    data[1] = 1.0f;
    data[2] = 2.0f;
    FloatArrayDataProvider dp = new FloatArrayDataProvider(data);
    assertTrue( dp.size() == 3 );
    assertTrue( dp.getQuick(0) == -1.0 );
    assertTrue( dp.getQuick(1) == 1.0 );
    assertTrue( dp.getQuick(2) == 2.0 );
    dp.setQuick(0,5.0);
    assertTrue( dp.getQuick(0) == 5.0 );
    assertTrue( data[0] == 5.0f );
}
//-------------------------------------------------------------------------
public static void main(String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite(FloatArrayDataProviderTest.class));
}
//------------------------------------------------------------------------------
} // FloatArrayDataProviderTest
