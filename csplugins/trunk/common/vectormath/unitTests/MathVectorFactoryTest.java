// MathVectorFactoryTest.java
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
public class MathVectorFactoryTest extends TestCase {

//------------------------------------------------------------------------------
public MathVectorFactoryTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testAll() throws Exception {
    double[] data1 = new double[3];
    data1[0] = -1.0;
    data1[1] = 1.0;
    data1[2] = 2.0;
    
    float[] fData = new float[2];
    fData[0] = 4.5f;
    fData[1] = -3.2f;

    
    MathVector v1 = MathVectorFactory.makeVector(data1);
    assertTrue( v1.get(2) == 2.0 );
    MathVector v2 = MathVectorFactory.makeVector(new DoubleArrayDataProvider(data1));
    assertTrue( v2.equals(v1,0.0) );
    ReadOnlyMathVector pv1 = MathVectorFactory.makeReadOnlyVector(data1);
    assertTrue( pv1.equals(v1,0.0) );
    ReadOnlyMathVector pv2 = MathVectorFactory.makeReadOnlyVector(new DoubleArrayDataProvider(data1));
    assertTrue( pv2.equals(v1,0.0) );
    
    MathVector v3 = MathVectorFactory.makeVector(fData);
    assertTrue( v3.get(1) == -3.2f );
    MathVector v4 = MathVectorFactory.makeVector(new FloatArrayDataProvider(fData));
    assertTrue( v4.equals(v3,0.0) );
    ReadOnlyMathVector pv3 = MathVectorFactory.makeReadOnlyVector(fData);
    assertTrue( pv3.equals(v3,0.0) );
    ReadOnlyMathVector pv4 = MathVectorFactory.makeReadOnlyVector(new FloatArrayDataProvider(fData));
    assertTrue( pv4.equals(v3,0.0) );
}
//-------------------------------------------------------------------------
public static void main(String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite(MathVectorFactoryTest.class));
}
//------------------------------------------------------------------------------
} // MathVectorFactoryTest
