// InteractionTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import cytoscape.data.Interaction;
//------------------------------------------------------------------------------
public class InteractionTest extends TestCase {


//------------------------------------------------------------------------------
public InteractionTest (String name) 
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
public void test3ArgCtor () throws Exception
{ 
  System.out.println ("test3ArgCtor");

  String source = "YNL312W";
  String type = "pd";
  String target = "YPL111W";

  Interaction inter0 = new Interaction (source, target, type);
  assertTrue (inter0.getSource().equals (source));
  assertTrue (inter0.getType().equals (type));
  assertTrue (inter0.numberOfTargets () == 1);
  assertTrue (inter0.getTargets()[0].equals (target));


} // test3ArgCtor
//-------------------------------------------------------------------------
public void test1ArgCtor () throws Exception
{ 
  System.out.println ("test1ArgCtor");

  String rawText0 = "YNL312W pp YPL111W";
  Interaction inter0 = new Interaction (rawText0);
  assertTrue (inter0.getSource().equals ("YNL312W"));
  assertTrue (inter0.getType().equals ("pp"));
  assertTrue (inter0.numberOfTargets () == 1);
  assertTrue (inter0.getTargets()[0].equals ("YPL111W"));

  String rawText1 = "YPL075W pd YDR050C YGR254W YHR174W";
  Interaction inter1 = new Interaction (rawText1);
  assertTrue (inter1.getSource().equals ("YPL075W"));
  assertTrue (inter1.getType().equals ("pd"));
  assertTrue (inter1.numberOfTargets () == 3);
  assertTrue (inter1.getTargets()[0].equals ("YDR050C"));
  assertTrue (inter1.getTargets()[1].equals ("YGR254W"));
  assertTrue (inter1.getTargets()[2].equals ("YHR174W"));

} // test1ArgCtor
//-------------------------------------------------------------------------
public void test1ArgCtorOnDegenerateFrom () throws Exception
// a degenerate form has -only- a source node:  no interaction type
// and no target node
{ 
  System.out.println ("test1ArgCtorOnDegenerateForm");

  String rawText0 = "YNL312W";
  Interaction inter0 = new Interaction (rawText0);
  assertTrue (inter0.getSource().equals ("YNL312W"));
  assertTrue (inter0.getType() == null);
  assertTrue (inter0.numberOfTargets () == 0);

} // test1ArgCtorOnDegenerateForm
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (InteractionTest.class));
}
//------------------------------------------------------------------------------
} // InteractionTest
