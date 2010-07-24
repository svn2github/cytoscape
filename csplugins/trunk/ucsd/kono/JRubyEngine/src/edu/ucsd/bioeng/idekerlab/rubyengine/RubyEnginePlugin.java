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
package edu.ucsd.bioeng.idekerlab.rubyengine;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.jruby.embed.ScriptingContainer;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import edu.ucsd.bioeng.idekerlab.rubyengine.console.CyIRBConsole;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManagerPlugin;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.engine.ScriptingEngine;


/**
 *
 */
public class RubyEnginePlugin extends CytoscapePlugin implements ScriptingEngine {
	private static final String ENGINE_NAME = "jruby";
	
	private static final String ENGINE_VERSION = "1.5.1";
	
	private static final String ENGINE_DISPLAY_NAME = "Ruby Scripting Engine (JRuby v1.5.1 and BioRuby 1.4.0)";
	private static final Icon ICON = new ImageIcon(RubyEnginePlugin.class.getResource("/images/ruby.png"));
	private static final Icon CONSOLE_ICON = new ImageIcon(RubyEnginePlugin.class.getResource("/images/ruby22x22.png"));
	
	private static RubyEnginePlugin engine = new RubyEnginePlugin();
	
	private static final CyLogger logger = CyLogger.getLogger();
	
	private static String jRubyHome;
	
	/**
	 * Creates a new RubyEnginePlugin object.
	 */
	public RubyEnginePlugin() {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDisplayName() {
		return ENGINE_DISPLAY_NAME;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon() {
		return ICON;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdentifier() {
		return ENGINE_NAME;
	}
	
	/**
	 * Returns version number of Scripting Engine as string
	 * 
	 * @return
	 */
	public static String getEngineVersion() {
		return ENGINE_VERSION;
	}
	
	
	public static String getJRubyHome() {
		return jRubyHome;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public static void register() {
		
		//System.setProperty("jruby.home", "/Users/kono/Documents/jruby-1.5.1");
		jRubyHome = System.getenv("JRUBY_HOME");
		
		if(jRubyHome == null)
			throw new IllegalStateException("This system does not have enviroment variable \"JRUBY_HOME.\"  Please set it and restart Cytoscape.");
		System.setProperty("org.jruby.embed.class.path", jRubyHome);
		logger.info("JRuby Home Dir = " + jRubyHome);
		
		final ScriptingContainer container = new ScriptingContainer();
		ScriptEngineManagerPlugin.getManager().registerEngine(ENGINE_NAME, engine);

		logger.info("JRuby scripting engine registered.");

		final JMenuItem consoleMenuItem = new JMenuItem(new AbstractAction("Open Ruby Console") {
				public void actionPerformed(ActionEvent e) {
					try {
						CyIRBConsole.showConsole();
					} catch (Exception e1) {
						logger.error("Could not start JRuby Colsone", e1);
					}
				}
			});

		consoleMenuItem.setIcon(CONSOLE_ICON);

		ScriptEngineManagerPlugin.getManager().addConsoleMenu(consoleMenuItem);

		logger.info("JRuby colsole registered.");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		// If already registered, ignore.
		if (ScriptEngineManagerPlugin.getManager().getEngine(ENGINE_NAME) != null)
			return;

		if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			// Register this to ScriptEngineManager.
			try {
				register();
			} catch(Exception ex) {
				logger.error("Could not register JRuby Engine to the Manager.", ex);
			}
		}
	}
}
