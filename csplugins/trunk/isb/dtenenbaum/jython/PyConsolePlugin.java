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

import java.util.*;

import java.awt.*;


import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;


/** 
 * 
 */
public class PyConsolePlugin extends CytoscapePlugin {
	protected SPyConsoleThread pythonConsole;
	protected Thread consoleThread;
	JFrame consoleFrame;
	HashMap variablesToPassToPython;

	public PyConsolePlugin() {

		CytoscapeAction consoleAction = new StartConsole();
		consoleAction.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(consoleAction);

		variablesToPassToPython = new HashMap();

	}

	protected class StartConsole extends CytoscapeAction {

		StartConsole() {
			super("Python Console...");
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("starting jython console...");
			pythonConsole = new SPyConsoleThread();
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




			String bootCode = ImportPyLibs.getResourceCode("__run__.py");
			pythonConsole.exec(bootCode);

		} // actionPerformed

	} // inner class StartConsole
	//------------------------------------------------------------------------------
} // class SumbitInteractionsPlugin
