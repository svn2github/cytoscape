// mRNAMeasurementTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.data.mRNAMeasurement;
//------------------------------------------------------------------------------
public class mRNAMeasurementTest extends TestCase {


//------------------------------------------------------------------------------
public mRNAMeasurementTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
public void testCtor () throws Exception
{ 
  System.out.println ("testCtor");
  mRNAMeasurement measurement = new mRNAMeasurement ("-0.315", "10.495");
  assertTrue (measurement.getRatio () == -0.315);
  assertTrue (measurement.getSignificance () == 10.495);

} // testCtor
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (mRNAMeasurementTest.class));
}
//------------------------------------------------------------------------------
} // mRNAMeasurementTest
