// WebReaderTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.isb.pshannon.py.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import csplugins.isb.pshannon.py.WebReader;
//------------------------------------------------------------------------------
public class WebReaderTest extends TestCase {

//------------------------------------------------------------------------------
public WebReaderTest (String name) {super(name);}
//------------------------------------------------------------------------------
public void testOne () throws Exception 
{
  System.out.println ("testOne");
  String url = "http://google.com";
  WebReader reader = new WebReader ();
  String result = reader.read (url);
  int googleCount = (result.split ("Google")).length;
  // System.out.println ("count: " + googleCount);
  assertTrue (googleCount > 3);

} // testOne
//-------------------------------------------------------------------------
public static void main(String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite(WebReaderTest.class));
}
//------------------------------------------------------------------------------
} // WebReaderTest
