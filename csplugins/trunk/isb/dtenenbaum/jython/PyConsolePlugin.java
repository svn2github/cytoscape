// PyConsolePlugin
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package csplugins.isb.dtenenbaum.jython;
//----------------------------------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JFrame;

import csplugins.isb.dtenenbaum.sharedData.SharedDataSingleton;
import csplugins.isb.pshannon.py.ConsoleMenubar;
import csplugins.isb.pshannon.py.ExitAction;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;


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


	public static boolean putInJythonNamespace (String name, Object value)  {
		boolean retVal = false;
		ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
		int numThreads = currentGroup.activeCount();
		Thread[] listOfThreads = new Thread[numThreads];
		SharedDataSingleton singleton = SharedDataSingleton.getInstance();

		currentGroup.enumerate(listOfThreads);
		for (int i = 0; i < numThreads; i++) {
			if (null != listOfThreads[i]) { // in case the list has changed since we created it
				if ("jythonConsoleThread".equals(listOfThreads[i].getName())) {
					if (null != singleton.get("PyConsole")) {
						SPyConsoleThread spc = (SPyConsoleThread)singleton.get("PyConsole");
						spc.set(name, value);
						return true;
					}
				}
			}
		}
		return false;
	}

	protected class StartConsole extends CytoscapeAction {

		StartConsole() {
			super("Python Console...");
		}

		public void actionPerformed(ActionEvent e) {
			SharedDataSingleton singleton = SharedDataSingleton.getInstance();
			StringBuffer strb = new StringBuffer("For help and example scripts, please see\n");
			strb.append("http://db.systemsbiology.net/cytoscape/jython or type\n");
			strb.append("help()\n");
			strb.append("at the prompt.");

	        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
			int numThreads = currentGroup.activeCount();
			Thread[] listOfThreads = new Thread[numThreads];

			// see if there is a previous console running
			// if so, don't start a new one
			// but give the existing one the focus
			currentGroup.enumerate(listOfThreads);
			for (int i = 0; i < numThreads; i++) {
				if (null != listOfThreads[i]) { // in case the list has changed since we created it
					if ("jythonConsoleThread".equals(listOfThreads[i].getName())) {
						if (listOfThreads[i].isAlive()) {
							JFrame cFrame = (JFrame)singleton.get("ConsoleFrame");
							cFrame.requestFocus();
							return;
						}
					}
				}
			}
			
			System.out.println("starting jython console...");
			
			pythonConsole = new SPyConsoleThread(strb.toString());
			singleton.put("PyConsole", pythonConsole);
			
			
			consoleThread = new Thread(pythonConsole, "jythonConsoleThread");
			consoleThread.start();
			consoleFrame = new JFrame("Jython Console");
			consoleFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			    public void windowClosing(java.awt.event.WindowEvent e) {
			    	ExitAction ea = new ExitAction(consoleFrame);
			    	ea.actionPerformed(null);
			    	
			    }
			});


			singleton.put("ConsoleFrame",consoleFrame);
			consoleFrame.setJMenuBar(new ConsoleMenubar(pythonConsole,consoleFrame));
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
