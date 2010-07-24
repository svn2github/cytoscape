/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package edu.ucsd.bioeng.idekerlab.scriptenginemanager;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.engine.ScriptingEngine;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ui.SelectScriptDialog;

/**
 * Wrapper class for BSF Scripting Engine Manager
 * 
 * @author kono
 * 
 */
public class ScriptEngineManager implements PropertyChangeListener {
	
	private static final javax.script.ScriptEngineManager manager;
	
	private static final Icon SCRIPT_ICON = new ImageIcon(ScriptEngineManager.class.getResource("/images/stock_run-macro.png"));
	private static final Icon CONSOLE_ICON = new ImageIcon(ScriptEngineManager.class
			.getResource("/images/gnome-terminal.png"));
	
	
	private final Map<String, ScriptingEngine> registeredNames;
	
	private final JMenu menu;
	private final JMenu consoleMenu;

	static {
		manager = new javax.script.ScriptEngineManager();
	}

	/**
	 * Creates a new ScriptEngineManager object.
	 */
	ScriptEngineManager() {
		
		registeredNames = new ConcurrentHashMap<String, ScriptingEngine>();
		
		menu = new JMenu("Execute Scripts...");
		menu.setIcon(SCRIPT_ICON);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins")
				.add(menu);

		consoleMenu = new JMenu("Scripting Language Consoles");
		consoleMenu.setIcon(CONSOLE_ICON);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins")
				.add(consoleMenu);
	}

	
	javax.script.ScriptEngineManager getManager() {
		return manager;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param id
	 *            DOCUMENT ME!
	 * @param engine
	 *            DOCUMENT ME!
	 */
	public void registerEngine(final String id, final ScriptingEngine engine) {
		registeredNames.put(id, engine);

		menu.add(new JMenuItem(new AbstractAction(engine.getDisplayName()) {
			

			public void actionPerformed(ActionEvent e) {
				SelectScriptDialog.showDialog(id);
			}
		}));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param consoleMenuItem
	 *            DOCUMENT ME!
	 */
	public void addConsoleMenu(final JMenuItem consoleMenuItem) {
		consoleMenu.add(consoleMenuItem);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param engineID
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public ScriptingEngine getEngine(String engineID) {
		return registeredNames.get(engineID);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param engineName
	 *            DOCUMENT ME!
	 * @param scriptFileName
	 *            DOCUMENT ME!
	 * @param arguments
	 *            DOCUMENT ME!
	 * 
	 * @throws BSFException
	 *             DOCUMENT ME!
	 * @throws IOException
	 *             DOCUMENT ME!
	 * @throws ScriptException 
	 */
	public static void execute(final String engineName,
			final String scriptFileName, final Map<String, String> arguments)
			throws ScriptException {
		
		final ScriptEngine engine = manager.getEngineByName(engineName);
		
		if ( engine == null) {
			// Register Engine
			CyLogger.getLogger().error("Error: Can't find " + engineName);
			return;
		}

		try {
			// This is a hack... I need to decide which version of Scripting
			// System is appropriate for Cytoscape 3.
			if (engineName != "jython") {
				final Object returnVal = engine.eval(new FileReader(scriptFileName));

				if (returnVal != null)
					System.out.println("Return Val = [" + returnVal + "]");
				
			} else {
				// Jython uses special console to execute script.

				final Class<?> engineClass = Class
						.forName("edu.ucsd.bioeng.idekerlab.pythonengine.PythonEnginePlugin");
				Method method = engineClass.getMethod("executePythonScript",
						new Class[] { String.class });
				Object ret = method.invoke(null,
						new Object[] { scriptFileName });

			}
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg0
	 *            DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
	}
}
