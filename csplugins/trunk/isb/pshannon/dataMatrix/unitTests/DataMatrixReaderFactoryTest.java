// DataMatrixReaderFactoryTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import csplugins.isb.pshannon.dataMatrix.*;
//------------------------------------------------------------------------------
public class DataMatrixReaderFactoryTest extends TestCase {

//------------------------------------------------------------------------------
public DataMatrixReaderFactoryTest (String name) 
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
public void testCreate () throws Exception
{ 
  System.out.println ("testCreate");
  DataMatrixReader reader = DataMatrixReaderFactory.createReader ("file://matrix0.testData");
  assertTrue (reader.getClass () == 
              Class.forName ("csplugins.isb.pshannon.dataMatrix.DataMatrixFileReader"));

  try {
    reader = DataMatrixReaderFactory.createReader ("bogus://matrix0.testData");
    assertTrue (false);
    }
  catch (IllegalArgumentException iae) {;}

} // testCreate
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (DataMatrixReaderFactoryTest.class));

}// main
//------------------------------------------------------------------------------
} // DataMatrixReaderFactoryTest
