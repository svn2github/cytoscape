
/*
  File: ExecTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

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
import cytoscape.unitTests.AllTests;

import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
//---------------------------------------------------------------------------
public class ExecTest extends TestCase {

//---------------------------------------------------------------------------
public ExecTest (String name)
{
    super (name);
}

//---------------------------------------------------------------------------
public void notestBasic () 
{
  AllTests.standardOut ("testBasic");

  String [] cmd = {"echo","hello","world"}; 

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
  assertTrue (fullResult.indexOf ("world") >= 0);

} // testBasic
//---------------------------------------------------------------------------
public void testBasicWithThreadedExec () throws Exception
{

  AllTests.standardOut ("testBasicWithThreadedExec");

  String [] cmd = {"echo","hello","world"}; 

  Exec child = new Exec (cmd);
  int result = child.runThreaded ();
  assertTrue (result == 0);
  String stdout = child.getStdoutAsString ();
  String stderr = child.getStderrAsString ();

  AllTests.standardOut  ("stdout: " + stdout);
  AllTests.standardOut  ("stderr: " + stderr);

} // testBasicWithThreadedExec
//---------------------------------------------------------------------------
public void testBasicWithThreadedExec2 () throws Exception
{

  AllTests.standardOut ("testBasicWithThreadedExec2");

  String [] cmd = {"echo","hello","world"}; 

  Exec child = new Exec (cmd);
  int result = child.runThreaded ();
  assertTrue (result == 0);
  String stdout = child.getStdoutAsString ();
  String stderr = child.getStderrAsString ();

  AllTests.standardOut ("stdout: " + stdout);
  AllTests.standardOut ("stderr: " + stderr);

} // testBasicWithThreadedExec2
//---------------------------------------------------------------------------
public void notestBasicInBackground () 
{
  AllTests.standardOut ("testBasicInBackground");

  String [] cmd = {"echo","hello","world"}; 

  Exec child = new Exec (cmd);
  child.setRunInBackground (true);
  AllTests.standardOut ("cmd: " + child.getCmd ());
  int result = child.run ();
  // assertTrue (result == 0);

  Enumeration iterator = child.getStdout().elements ();
  StringBuffer sb = new StringBuffer ();
  sb.append ("=========== STDOUT =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    sb.append (newLine);
    AllTests.standardOut (newLine);
    sb.append ("\n");
    }

  iterator = child.getStderr().elements ();
  sb.append ("=========== STDERR =============\n");
  while (iterator.hasMoreElements ()) {
    String newLine = (String) iterator.nextElement ();
    AllTests.standardOut (newLine);
    sb.append (newLine);
    sb.append ("\n");
    }

  String fullResult = sb.toString ();
  assertTrue (fullResult.indexOf ("world") >= 0);
  AllTests.standardOut ("--> " + fullResult + " <--");

} // testBasicInBackground
//---------------------------------------------------------------------------
public void notestUsingStandardInput () 
{
  AllTests.standardOut ("testUsingStandardInput");

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
  String [] cmd = { "/users/pshannon/data/human/jdrf/web",
                    "http://sewardpark.net"};

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
  AllTests.standardOut (fullResult);

}

//---------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (ExecTest.class));
}
//---------------------------------------------------------------------------
} // ExecTest
