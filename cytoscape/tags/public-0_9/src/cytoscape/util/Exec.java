// Exec.java
// exec a child process, and get its stdout & stderr
//---------------------------------------------------------------------------
// rcs:  $Revision$ $Date$
//---------------------------------------------------------------------------
package cytoscape.util;
//---------------------------------------------------------------------------
import java.lang.Runtime;
import java.lang.Process;
import java.io.*;

import java.lang.Runtime;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Vector;
import java.util.Enumeration;
//---------------------------------------------------------------------------
public class Exec {
  String [] cmd;
  Vector stdoutResults;
  Vector stderrResults;
  String stringToSendToStandardInput;
  boolean runInBackground = false;
//---------------------------------------------------------------------------
public Exec  ()
{
  this (null);
}
//---------------------------------------------------------------------------
public Exec  (String [] cmd)
{
  this.cmd = cmd;
  stdoutResults = new Vector (100);   // just guessing...
  stderrResults = new Vector (10);
}
//---------------------------------------------------------------------------
public void setStandardInput (String input)
{
  stringToSendToStandardInput = input;
}
//---------------------------------------------------------------------------
public void setRunInBackground (boolean newValue)
{
  runInBackground = newValue;
  int length = cmd.length;
  String [] revisedCmd = new String [length + 1];
  
  for (int i=0; i < length; i++)
    revisedCmd [i] = cmd [i];
  
  revisedCmd [length] = " &";

  cmd = revisedCmd;
  
} // setRunInBackground
//---------------------------------------------------------------------------
public String getCmd ()
{
  StringBuffer sb = new StringBuffer ();
  for (int i=0; i < cmd.length; i++) {
    sb.append (cmd [i]);
    sb.append (" ");
    }

  return sb.toString ();

} // getCmd
//---------------------------------------------------------------------------
public int run ()
{
  int execExitValue = -1;  // be pessimistic

  StringBuffer cmdSB = new StringBuffer ();
  for (int i=0; i < cmd.length; i++) {
    cmdSB.append (cmd [i]);
    cmdSB.append (" ");
    }

  try {
    Runtime runtime =  Runtime.getRuntime ();
    // System.out.println (" --> just before exec: \n\t" + getCmd ());
    //Process process = runtime.exec (cmd);
    Process process = runtime.exec (cmdSB.toString ());
    BufferedReader stdoutReader = 
      new BufferedReader (new InputStreamReader (process.getInputStream()));
    BufferedReader stderrReader = 
      new BufferedReader (new InputStreamReader (process.getErrorStream()));

    if (stringToSendToStandardInput != null) {
        // A PrintStream adds functionality to another output stream, namely the
        // ability to print representations of various data values
        // conveniently. Two other features are provided as well. Unlike other
        // output streams, a PrintStream never throws an IOException; instead,
        // exceptional situations merely set an internal flag that can be tested
        // via the checkError method. Optionally, a PrintStream can be created so
        // as to flush automatically; this means that the flush method is
        // automatically invoked after a byte array is written, one of
        // the println methods is invoked, or a newline character or
        // byte ('\n') is written.
      PrintStream stdinWriter = new PrintStream (process.getOutputStream(),true);
      stdinWriter.print (stringToSendToStandardInput);
      stdinWriter.close ();
      }

    try {
      execExitValue = process.waitFor ();
      }
    catch (InterruptedException e) {
      e.printStackTrace ();
      }

    String stdoutResult;
    while ((stdoutResult = stdoutReader.readLine()) != null) {
      stdoutResults.addElement (stdoutResult);
      }

    String stderrResult;
    while ((stderrResult = stderrReader.readLine()) != null) {
      stderrResults.addElement (stderrResult);
      }
    }// try
  catch (IOException e) {
    e.printStackTrace ();
    }

  return execExitValue;

} // run
//---------------------------------------------------------------------------
public Vector getStdout ()
{
  return stdoutResults;
}
//---------------------------------------------------------------------------
public Vector getStderr ()
{
  return stderrResults;
}
//---------------------------------------------------------------------------
} // Exec.java
