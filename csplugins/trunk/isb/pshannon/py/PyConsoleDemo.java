// PyConsoleDemo
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package csplugins.isb.pshannon.py;
//----------------------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JOptionPane;

import java.awt.*;

import java.io.*;
import java.util.*;
import java.net.URL;
import javax.jnlp.*;

import csplugins.isb.pshannon.cyInterface.*;
import org.python.util.InteractiveConsole;
import org.python.core.*;

import spyconsole.*;
//----------------------------------------------------------------------------------------
/** 
  * 
 */
public class PyConsoleDemo {

  protected ThreadedSPyConsole pyConsole;
  protected Thread consoleThread;
  JFrame consoleFrame;

//----------------------------------------------------------------------------------------
//public PyConsoleDemo ()
//{
//  runConsole ();
//
//}
//----------------------------------------------------------------------------------------
private void runConsole ()
{

  String dateString = "2004-08-10";
  System.out.println ("starting PyConsolePlugin, built on " + dateString);
  pyConsole = new ThreadedSPyConsole ();
  consoleThread = new Thread (pyConsole, "jythonConsoleThread");
  consoleThread.start ();

  CyInterface cy = new Cytoscape2Impl ();
  pyConsole.set ("cy", cy);

  WebReader webreader = new WebReader ();
  pyConsole.set ("webreader", webreader);

  String scriptHome = "http://db.systemsbiology.net/cytoscape/scripts";
  pyConsole.set ("scriptHome", scriptHome);

  pyConsole.set ("consoleBuildDate", dateString);

  consoleFrame = new JFrame ("Cytoscape Python Console");
  consoleFrame.getContentPane().add (pyConsole.getConsolePanel(), BorderLayout.CENTER);
  consoleFrame.setSize (500, 400);
  consoleFrame.setVisible (true);

} // runConsole
//------------------------------------------------------------------------------
public static void main (String [] args)
{
  PyConsoleDemo demo = new PyConsoleDemo ();
  demo.runConsole ();
}
//------------------------------------------------------------------------------
} // class PyConsoleDemo
