// MiscTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.util.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.util.Misc;
import java.awt.Color;
//------------------------------------------------------------------------------
public class MiscTest extends TestCase {


//------------------------------------------------------------------------------
public MiscTest (String name) 
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
public void testParseRGBTest () throws Exception
{ 
  System.out.println ("testRGBParseText");
  Color result = Misc.parseRGBText ("0,0,0");
  assertTrue (result.equals (Color.black));

  result = Misc.parseRGBText ("27,39,121");
  System.out.println ("result: " + result);
  assertTrue (result.getRed () == 27);
  assertTrue (result.getGreen () == 39);
  assertTrue (result.getBlue () == 121);

  result = Misc.parseRGBText (" 27 , 39 , 121 ");
  System.out.println ("result: " + result);
  assertTrue (result.getRed () == 27);
  assertTrue (result.getGreen () == 39);
  assertTrue (result.getBlue () == 121);

  result = Misc.parseRGBText ("255,255,255");
  System.out.println ("result: " + result);
  assertTrue (result.equals (Color.white));

} // testParseRGBTest
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (MiscTest.class));
}
//------------------------------------------------------------------------------
} // MiscTest
