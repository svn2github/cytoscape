// ExecTest.java:  junit test for Exec
//
// $Revision$
// $Date$
//
//---------------------------------------------------------------------------
package cytoscape.util.unitTests;
//---------------------------------------------------------------------------
import junit.framework.*;
import cytoscape.util.Exec;
import java.util.Enumeration;
//---------------------------------------------------------------------------
public class ExecTest extends TestCase {
//---------------------------------------------------------------------------
public ExecTest (String name) 
{
  super (name);
}
//---------------------------------------------------------------------------
public void testBasic () 
{
  System.out.println ("testBasic");

  String [] cmd = new String [2];
  cmd [0] = "ls";
  cmd [1] = "-l";

  Exec child = new Exec (cmd);
  int result = child.run ();
  assertTrue (result == 0);

  Enumeration iterator = child.getStdout().elements ();
  StringBuffer sb = new StringBuffer ();
  sb.append ("=========== STDOUT =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    sb.append (newLine);
    // System.out.println (newLine);
    sb.append ("\n");
    }

  iterator = child.getStderr().elements ();
  sb.append ("=========== STDERR =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    //System.out.println (newLine);
    sb.append (newLine);
    sb.append ("\n");
    }

  String fullResult = sb.toString ();
  assertTrue (fullResult.indexOf ("ExecTest.java") >= 0);
  assertTrue (fullResult.indexOf ("ExecTest.class") >= 0);
  // System.out.println (fullResult);

} // testBasic
//---------------------------------------------------------------------------
public void notestBasicInBackground () 
{
  System.out.println ("testBasicInBackground");

  String [] cmd = new String [1];
  cmd [0] = "date";

  Exec child = new Exec (cmd);
  child.setRunInBackground (true);
  System.out.println ("cmd: " + child.getCmd ());
  int result = child.run ();
  // assertTrue (result == 0);

  Enumeration iterator = child.getStdout().elements ();
  StringBuffer sb = new StringBuffer ();
  sb.append ("=========== STDOUT =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    sb.append (newLine);
    System.out.println (newLine);
    sb.append ("\n");
    }

  iterator = child.getStderr().elements ();
  sb.append ("=========== STDERR =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    System.out.println (newLine);
    sb.append (newLine);
    sb.append ("\n");
    }

  String fullResult = sb.toString ();
  //assertTrue (fullResult.indexOf ("ExecTest.class") >= 0);
  System.out.println ("--> " + fullResult + " <--");

} // testBasicInBackground
//---------------------------------------------------------------------------
public void testUsingStandardInput () 
{
  System.out.println ("testUsingStandardInput");

  String [] cmd = {"cat"};
  Exec child = new Exec (cmd);
  child.setStandardInput ("sample input\nsent to cat\nto be echoed\n");
  int result = child.run ();
  assertTrue (result == 0);

  Enumeration iterator = child.getStdout().elements ();
  StringBuffer sb = new StringBuffer ();
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    sb.append (newLine);
    sb.append ("\n");
    }

    // we should see all the contents passed in above
  assertTrue (sb.toString().indexOf ("sample input") >= 0);
  assertTrue (sb.toString().indexOf ("sent to cat") >= 0);
  assertTrue (sb.toString().indexOf ("to be echoed") >= 0);

  iterator = child.getStderr().elements ();
  sb = new StringBuffer ();
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    sb.append (newLine);
    sb.append ("\n");
    }

    // stderr should be empty.
  String stderr = sb.toString ();
  assertTrue (stderr.length() == 0);

} // testUsingStandardInput
//---------------------------------------------------------------------------
public void disabled_testRunNetscape ()
{
  String [] cmd = new String [2];
  cmd [0] = "/users/pshannon/data/human/jdrf/web";
  cmd [1] = "http://sewardpark.net";


  Exec child = new Exec (cmd);
  int result = child.run ();
  assertTrue (result == 0);

  Enumeration iterator = child.getStdout().elements ();
  StringBuffer sb = new StringBuffer ();
  sb.append ("=========== STDOUT =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    sb.append (newLine);
    // System.out.println (newLine);
    sb.append ("\n");
    }

  iterator = child.getStderr().elements ();
  sb.append ("=========== STDERR =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    //System.out.println (newLine);
    sb.append (newLine);
    sb.append ("\n");
    }

  String fullResult = sb.toString ();
  //assertTrue (fullResult.indexOf ("ExecTest.java") >= 0);
  //assertTrue (fullResult.indexOf ("ExecTest.class") >= 0);
  System.out.println (fullResult);

}
//---------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (ExecTest.class));
}
//---------------------------------------------------------------------------
} // ExecTest
