/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package commandTool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import javax.swing.filechooser.FileNameExtensionFilter;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.command.CyCommandManager;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;

import commandTool.CommandTool;
import commandTool.handlers.CommandHandler;
import commandTool.handlers.LogMessageHandler;
import commandTool.handlers.MessageHandler;
import commandTool.handlers.StdMessageHandler;
import commandTool.ui.CommandToolDialog;


public class CommandTool extends CytoscapePlugin implements ActionListener,PropertyChangeListener {
	private CyLogger logger = null;
	private List<File> scriptList = new ArrayList();
	private final static String WINDOW = "window";
	private final static String RUN = "run";
	private final static String SETTINGS = "settings";
	private String lastDirectory = ".";
	public static MessageHandler handlerContext = null;
	private boolean initialized = true;

	public static void setMessageHandlerContext(MessageHandler handler) {
		handlerContext = handler;
	}

	public static MessageHandler getMessageHandlerContext() {
		return handlerContext;
	}

	/**
	 * We don't do much at initialization time
	 */
	public CommandTool() {
		logger = CyLogger.getLogger(CommandTool.class);

		// Add ourselves to the menu
		JMenu menu = new JMenu("Command Tool");
		JMenuItem dialog = new JMenuItem("Command Window...");
		dialog.setActionCommand(WINDOW);
		dialog.addActionListener(this);
		menu.add(dialog);
		JMenuItem run = new JMenuItem("Run Script...");
		run.setActionCommand(RUN);
		run.addActionListener(this);
		menu.add(run);

		/*
		 * Add this in the future when we have settings...
		menu.add(new JSeparator());
		JMenuItem settings = new JMenuItem("Settings...");
		settings.setActionCommand(SETTINGS);
		settings.addActionListener(this);
		menu.add(settings);
		*/

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
		                            .getMenu("Plugins");
		pluginMenu.add(menu);

		// Register the "commandTool" namespace
		new commandTool.handlers.CommandToolHandler();

		// Get the command arguments (in case we've got script files)
		String[] args = CytoscapeInit.getCyInitParams().getArgs();
		parseArgs(args);
	}


	public void propertyChange(PropertyChangeEvent evt) {
		if (initialized) return;

		// Create the message handler we're going to use
		StdMessageHandler mHandler = new StdMessageHandler();
		CommandTool.setMessageHandlerContext(mHandler);

		// If we have any script files open -- process them now.
		for (File file: scriptList) {
			CommandHandler.handleCommandFile(file, null);
		} 

		Cytoscape.getPropertyChangeSupport()
		         .removePropertyChangeListener(Cytoscape.CYTOSCAPE_INITIALIZED, this);
	}

	private void parseArgs(String[] args) {
		// See if there are any scripts
		for (int arg = 0; arg < args.length; arg++) {
			if (args[arg].equals("-S")) {
				// Yup, put it in our file list
				logger.debug("Opening file: "+args[arg+1]);
				File file = new File(args[++arg]);
				if (file == null) {
					// Display an error
					logger.error("Unable to open file: "+args[arg]);
				} else {
					scriptList.add(file);
				}
				Cytoscape.getPropertyChangeSupport()
				         .addPropertyChangeListener(Cytoscape.CYTOSCAPE_INITIALIZED, this);
				initialized = false;
			}
			return;
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(WINDOW)) {
			// Popup our dialog
			CommandToolDialog d = new CommandToolDialog(Cytoscape.getDesktop(), logger);
			d.pack();
			d.setLocationRelativeTo(Cytoscape.getDesktop());
			d.setVisible(true);
		} else if (command.equals(RUN)) {
			// Get the file name
			JFileChooser chooser = new JFileChooser(lastDirectory);
			chooser.setDialogTitle("Select command file to execute");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Cytoscape command files", "com", "txt");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(Cytoscape.getDesktop());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				// Run it
				LogMessageHandler mHandler = new LogMessageHandler(logger);
				CommandTool.setMessageHandlerContext(mHandler);
				CommandHandler.handleCommandFile(chooser.getSelectedFile(), null);
				lastDirectory = chooser.getSelectedFile().getParent();
			}
		} else if (command.equals(SETTINGS)) {
		}
	}
}
