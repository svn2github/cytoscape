/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
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
package edgeLister;

// System imports
import javax.swing.JOptionPane;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

// giny imports
import giny.view.NodeView;
import ding.view.*;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

/**
 * The GroupTool class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class EdgeLister extends CytoscapePlugin 
                       implements ActionListener {
	public static final double VERSION = 0.1;
	enum MenuCommand {HIDE,REMOVE,RESTORE,SELECT};

	JMenu edgeMenu;
	JMenu	hideMenu;
	JMenu	removeMenu;
	JMenu	restoreMenu;
	JMenu	selectMenu;

	HashMap<String,EdgeSet>edgeMap = new HashMap();

  /**
   * Create our action and add it to the plugins menu
   */
	public EdgeLister() {
		// Create our main plugin menu
		edgeMenu = new JMenu("Edge Memory");

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		{
			JMenuItem item = new JMenuItem("Remember selected edges...");
			item.addActionListener(this);
			edgeMenu.add(item);
		}
		{
			hideMenu = new JMenu("Hide edges");
			hideMenu.setEnabled(false);
			edgeMenu.add(hideMenu);
		}
		{
			removeMenu = new JMenu("Remove list");
			removeMenu.setEnabled(false);
			edgeMenu.add(removeMenu);
		}
		{
			restoreMenu = new JMenu("Restore edges");
			restoreMenu.setEnabled(false);
			edgeMenu.add(restoreMenu);
		}
		{
			selectMenu = new JMenu("Select edges");
			selectMenu.setEnabled(false);
			edgeMenu.add(selectMenu);
		}
		pluginMenu.add(edgeMenu);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith("Remember")) {
			// Get the selected edges
			Set edgeSet = Cytoscape.getCurrentNetwork().getSelectedEdges();
			if (edgeSet.size() == 0) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No edges selected: Only selected edges can be remembered",
				                              "No edges selected", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Get the name
			String name = JOptionPane.showInputDialog(Cytoscape.getDesktop(), "Enter name of remembered edges:",
			                                          "Enter Edge List Name", JOptionPane.PLAIN_MESSAGE);
			// Save it
			edgeMap.put(name, new EdgeSet(edgeSet));
			// Add this list to our menus
			{
				JMenuItem item = new JMenuItem(name);
				item.addActionListener(new MenuHandler(MenuCommand.HIDE));
				hideMenu.add(item);
				hideMenu.setEnabled(true);
			}
			{
				JMenuItem item = new JMenuItem(name);
				item.addActionListener(new MenuHandler(MenuCommand.REMOVE));
				removeMenu.add(item);
				removeMenu.setEnabled(true);
			}
			{
				JMenuItem item = new JMenuItem(name);
				item.addActionListener(new MenuHandler(MenuCommand.RESTORE));
				restoreMenu.add(item);
				restoreMenu.setEnabled(true);
			}
			{
				JMenuItem item = new JMenuItem(name);
				item.addActionListener(new MenuHandler(MenuCommand.SELECT));
				selectMenu.add(item);
				selectMenu.setEnabled(true);
			}
		}
	}

	class EdgeSet {
		List<CyEdge> edgeSet;
		boolean hidden;

		EdgeSet(Set<CyEdge> edges) {
			hidden = false;
			// Make this a shallow copy
			edgeSet = new ArrayList();
			this.edgeSet.addAll(edges);
		}

		void hide() {
			if (hidden) return;
			for (CyEdge edge: edgeSet) {
				Cytoscape.getCurrentNetwork().removeEdge(edge.getRootGraphIndex(),false);
			}
			hidden = true;
		}

		void restore() {
			if (!hidden) return;
			for (CyEdge edge: edgeSet) {
				Cytoscape.getCurrentNetwork().addEdge(edge);
			}
			hidden = false;
		}

		void select() {
			Cytoscape.getCurrentNetwork().setSelectedEdgeState(edgeSet,true);
		}
	}

	class MenuHandler implements ActionListener {
		MenuCommand command;

		MenuHandler(MenuCommand command) {this.command = command; }

		public void actionPerformed(ActionEvent e) {
			String name = e.getActionCommand();
			EdgeSet edgeSet = edgeMap.get(name);
			switch (command) {
			case SELECT:
				edgeSet.select();
				break;
			case HIDE:
				edgeSet.hide();
				break;
			case RESTORE:
				edgeSet.restore();
				break;
			case REMOVE:
				edgeSet.restore();
				edgeMap.remove(name);

				removeItem(hideMenu, name);
				removeItem(restoreMenu, name);
				removeItem(selectMenu, name);
				removeItem(removeMenu, name);
			}
		}

		private void removeItem(JMenu menu, String name) {
			for (int i = 0; i < menu.getItemCount(); i++) {
				if (menu.getItem(i).getText().equals(name)) {
					menu.remove(i);
					return;
				}
			}
		}
	}
}
