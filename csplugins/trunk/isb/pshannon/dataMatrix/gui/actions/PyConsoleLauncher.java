// PyConsole
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions;
//-----------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import org.python.util.InteractiveConsole;
import org.python.core.*;
import spyconsole.*;

import cytoscape.Cytoscape;
import csplugins.isb.pshannon.py.*;
import csplugins.isb.pshannon.dataMatrix.gui.*;

//-----------------------------------------------------------------------------------
public class PyConsoleLauncher {

  protected DataMatrixBrowser browser;
  protected ThreadedSPyConsole pythonConsole;
  protected Thread consoleThread;
  JFrame consoleFrame;

//-----------------------------------------------------------------------------------
public PyConsoleLauncher (DataMatrixBrowser browser) 
{
  this.browser = browser;
  browser.addActionToToolbar (new ThisAction ());
}
//-----------------------------------------------------------------------------------
class ThisAction extends AbstractAction {

  ThisAction () {super ("Python Console");}

  public void actionPerformed (ActionEvent e) {
 
    System.out.println ("---- dataMatrix.gui.actions.PyConsoleLauncher props");
    Properties sysProps = System.getProperties ();
    String [] keys = (String []) sysProps.keySet().toArray (new String [0]);
    for (int k=0; k < keys.length; k++)
      System.out.println (k + ") " + keys [k] + ": " + sysProps.get (keys [k]));


    
    pythonConsole = new ThreadedSPyConsole ();
    consoleThread = new Thread (pythonConsole, "jythonConsoleThread");
    consoleThread.start();
    consoleFrame = new JFrame ("Cytoscape Python Console");
    consoleFrame.getContentPane().add (pythonConsole.getConsolePanel(), BorderLayout.CENTER);
    consoleFrame.setSize (500, 400);
    consoleFrame.setVisible (true);
    setupPythonConsole (pythonConsole);
    } // actionPerformed

} // ThisAction
//----------------------------------------------------------------------------
protected void setupPythonConsole (ThreadedSPyConsole pyConsole)
{

  //System.out.println ("about to import rosuda...");
  //pyConsole.exec ("from org.rosuda.JRclient import *");
  //System.out.println ("after importing rosuda...");

  pyConsole.set ("mb", browser);
  WebReader webreader = new WebReader ();
  pyConsole.set ("webreader", webreader);
  String scriptHome = "http://db.systemsbiology.net/cytoscape/scripts";
  pyConsole.set ("scriptHome", scriptHome);

  ArrayList scriptUrls = new ArrayList ();

  String [] args = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
  for (int i=0; i < args.length; i++) {
    System.out.println (i + ") " + args [i]);
    if (args [i].equalsIgnoreCase ("--script") && args.length > i+1)
      scriptUrls.add (args [i+1]);
    } // for i

  System.out.println ("scripts to load: " + scriptUrls.size ());


  for (int s=0; s < scriptUrls.size (); s++) {
    String scriptUrl = (String) scriptUrls.get (s);
    String url = scriptHome + "/" + scriptUrl;
    System.out.println ("about to load " + url);
    pyConsole.set ("scriptUrl", url);
    pyConsole.exec ("scriptText = webreader.read (scriptUrl)");
    pyConsole.exec ("obj = compile (scriptText, '<string>', 'exec')");
    pyConsole.exec ("eval (obj)");
    }

}
//----------------------------------------------------------------------------
} // class PyConsoleLauncher


