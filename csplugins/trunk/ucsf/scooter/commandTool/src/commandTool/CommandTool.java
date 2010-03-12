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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;

import commandTool.ui.CommandToolDialog;

public class CommandTool extends CytoscapePlugin implements ActionListener {
	private CyLogger logger = null;

	/**
	 * We don't do much at initialization time
	 */
	public CommandTool() {
		logger = CyLogger.getLogger(CommandTool.class);

		// Add ourselves to the menu
		JMenuItem menu = new JMenuItem("Command Tool...");
		menu.addActionListener(this);

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
		                            .getMenu("Plugins");
		pluginMenu.add(menu);

		// Register the "command" namespace

	}

	public void actionPerformed(ActionEvent e) {
		// Popup our dialog
		CommandToolDialog d = new CommandToolDialog(Cytoscape.getDesktop(), logger);
		d.pack();
		d.setLocationRelativeTo(Cytoscape.getDesktop());
		d.setVisible(true);
	}

}
