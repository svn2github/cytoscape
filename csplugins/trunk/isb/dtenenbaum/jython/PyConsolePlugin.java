// PyConsolePlugin
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package csplugins.isb.dtenenbaum.jython;
//----------------------------------------------------------------------------------------

import java.awt.event.*;

import javax.swing.*;

import java.awt.*;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;

// we're now depending on the sharedData plugin
import csplugins.isb.dtenenbaum.sharedData.*;


//bla bla freaking bla y mas


/** 
 * 
 */
public class PyConsolePlugin extends CytoscapePlugin {
	protected SPyConsoleThread pythonConsole;
	protected Thread consoleThread;
	JFrame consoleFrame;
	boolean bootstrap ;

	// TODO - this ctor doesn't work with arg of "false" if PyConsolePlugin
	// is already running. Can it be fixed or does it matter? We normally only
    // call this with false if we don't already have a console.
	public PyConsolePlugin() {
		this(true);
	}
	
	public PyConsolePlugin(boolean bootstrap) {
		this.bootstrap = bootstrap;
		CytoscapeAction consoleAction = new StartConsole();
		if (bootstrap) {
			consoleAction.putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_Y));
			consoleAction.setPreferredMenu("Plugins");
			Cytoscape.getDesktop().getCyMenus().addAction(consoleAction);
		} else {
			consoleAction.actionPerformed(null);
		}
	}

	protected class StartConsole extends CytoscapeAction {

		StartConsole() {
			super("Python Console...");
		}

		public void actionPerformed(ActionEvent e) {
			StringBuffer strb = new StringBuffer("For help and example scripts, please see\n");
			strb.append("http://db.systemsbiology.net/cytoscape/jython");

	        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
			int numThreads = currentGroup.activeCount();
			Thread[] listOfThreads = new Thread[numThreads];

			currentGroup.enumerate(listOfThreads);
			for (int i = 0; i < numThreads; i++) {
				if (null != listOfThreads[i]) { // in case the list has changed since we created it
					if ("jythonConsoleThread".equals(listOfThreads[i].getName())) {
						// TODO - get the console frame and give it the focus
						System.out.println("There is already a console running, no action taken.");
						return;
					}
				}
			}
			
			System.out.println("starting jython console...");
			
			
			SharedDataSingleton singleton = SharedDataSingleton.getInstance();
			
			pythonConsole = new SPyConsoleThread(strb.toString());
			singleton.put("PyConsole", pythonConsole);
			
			
			consoleThread = new Thread(pythonConsole, "jythonConsoleThread");
			consoleThread.start();
			consoleFrame = new JFrame("Jython Console");
			consoleFrame.getContentPane().add(
				pythonConsole.getConsolePanel(),
				BorderLayout.CENTER);
			consoleFrame.setSize(800, 400);
			consoleFrame.setVisible(true);


			String[] jarNames = ImportPyLibs.importLibs();
			pythonConsole.exec("import sys");

			for (int i = 0; i < jarNames.length; i++) {
				pythonConsole.exec("sys.path.append('" + jarNames[i] + "')");
			}



			String bootCode;
			
			if (bootstrap) {
				bootCode = ImportPyLibs.getResourceCode("__run__.py");
			} else {
				bootCode = ImportPyLibs.getResourceCode("__run2__.py");
			}
			pythonConsole.exec(bootCode);

		} // actionPerformed

	} // inner class StartConsole
	//------------------------------------------------------------------------------
} // class PyConsolePlugin
