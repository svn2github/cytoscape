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
package org.cytoscape.scripting.groovy;

import cytoscape.Cytoscape;

import cytoscape.logger.CyLogger;

import cytoscape.plugin.CytoscapePlugin;

import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManagerPlugin;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.engine.ScriptingEngine;

import org.apache.bsf.BSFManager;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;

import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;


/**
 *
 */
public class GroovyEnginePlugin extends CytoscapePlugin implements ScriptingEngine {
	private static final String ENGINE_NAME = "groovy";
	private static final String ENGINE_DISPLAY_NAME = "Groovy Scripting Engine (based on Groovy v1.6.7)";
	private static final Icon ICON = new ImageIcon(GroovyEnginePlugin.class.getResource("/images/groovy.png"));
	private static final GroovyEnginePlugin engine = new GroovyEnginePlugin();
	
	// This should be a singleton.
	private static groovy.ui.Console gConsole;

	/**
	 * Creates a new RubyEnginePlugin object.
	 */
	public GroovyEnginePlugin() {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return ENGINE_DISPLAY_NAME;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return ICON;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return ENGINE_NAME;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public static void register() {
		BSFManager.registerScriptingEngine(ENGINE_NAME, "org.codehaus.groovy.bsf.GroovyEngine",
		                                   new String[] { ENGINE_NAME });

		System.out.println("*Groovy scripting engine loaded!");

		try {
			final Class engineClass = Class.forName("edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManager");
			Method method = engineClass.getMethod("registerEngine",
			                                      new Class[] { String.class, ScriptingEngine.class });
			Object ret = method.invoke(null, new Object[] { ENGINE_NAME, engine });
		} catch (Exception e) {
			e.printStackTrace();
		}

		final JMenuItem consoleMenuItem = new JMenuItem(new AbstractAction("Open Groovy Console") {
				public void actionPerformed(ActionEvent e) {					
					// Open groovyConsole application.
					if (gConsole == null) {
						gConsole = new groovy.ui.Console();
					}

					if (gConsole.getFrame() == null) {
						gConsole.run();
					} else if(((JFrame)gConsole.getFrame()).isVisible() == false) {
						gConsole.run();
					}
				}
			});

		consoleMenuItem.setIcon(new ImageIcon(GroovyEnginePlugin.class.getResource("/images/groovy22x22.png")));

		ScriptEngineManagerPlugin.getManager().addConsoleMenu(consoleMenuItem);

		CyLogger.getLogger().info("Groovy scripting engine registered successfully.");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (ScriptEngineManagerPlugin.getManager().getEngine(ENGINE_NAME) != null)
			return;

		if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			// Register this to ScriptEngineManager.
			register();
		}
	}
}
