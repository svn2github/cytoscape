// PluginInfoTest.java:  a junit test for the class which sets run-time configuration,
// usually from command line arguments
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.*;

import cytoscape.PluginInfo;
//------------------------------------------------------------------------------
public class PluginInfoTest extends TestCase {


//------------------------------------------------------------------------------
public PluginInfoTest (String name) 
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
public void testDefaultCtor () throws Exception
{ 
  System.out.println ("testDefaultCtor");
  PluginInfo pi = new PluginInfo ();

  assertTrue (pi.getAttributeName () == null);
  assertTrue (pi.getFileExtension () == null);
  assertTrue (pi.getClassName () == null);

} // testDefaultCtor
//-------------------------------------------------------------------------
public void testSettersAndGetters () throws Exception
{ 
  System.out.println ("testSettersAndGetters");
  PluginInfo pi = new PluginInfo ();

  String attributeName = "FOO";
  String fileExtension = "foo";
  String className = "cytoscape.plugins.demo.Foo";

  pi.setAttributeName (attributeName);
  pi.setFileExtension (fileExtension);
  pi.setClassName (className);

  assertTrue (pi.getAttributeName().equals (attributeName));
  assertTrue (pi.getFileExtension().equals (fileExtension));
  assertTrue (pi.getClassName().equals (className));

} // testSettersAndGetters
//-------------------------------------------------------------------------
public void testArgCtor () throws Exception
{ 
  System.out.println ("testArgCtor");

  String attributeName = "FOO";
  String fileExtension = "foo";
  String className = "cytoscape.plugins.demo.Foo";
  PluginInfo pi = new PluginInfo (className, fileExtension, attributeName);

  assertTrue (pi.getAttributeName().equals (attributeName));
  assertTrue (pi.getFileExtension().equals (fileExtension));
  assertTrue (pi.getClassName().equals (className));

} // testArgCtor
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (PluginInfoTest.class));
}
//------------------------------------------------------------------------------
} // PluginInfoTest
