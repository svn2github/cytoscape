/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
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
package namedSelection;

// System imports
import javax.swing.JOptionPane;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// giny imports
import giny.view.NodeView;
import ding.view.*;

// Cytoscape imports
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

/**
 * The NamedSelection class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class NamedSelection extends CytoscapePlugin 
                            implements CyGroupViewer {

	public static final String viewerName = "namedSelection";
	public static final double VERSION = 0.1;
	public static final int NONE = 0;
	public static final int SELECT = 1;
	public static final int UNSELECT = 2;
	public static final int NEW = 3;
	public static final int REMOVE = 4;

	// State values
	private static final int SELECTED = 1;
	private static final int UNSELECTED = 2;

	private CyGroupViewer groupViewer = null;

  /**
   * Create our action and add it to the plugins menu
   */
  public NamedSelection() {
		JMenu menu = new JMenu("Named Selection Tool");
		menu.addMenuListener(new NamedSelectionMenuListener(null));

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);
		// Register with CyGroup
		CyGroup.registerGroupViewer(this);
		this.groupViewer = this; // this makes it easier to get at from inner classes
		System.out.println("namedSelectionPlugin "+VERSION+" initialized");

  }

	// These are required by the CyGroupViewer interface

	/**
	 * Return the name of our viewer
	 *
	 * @return viewer name
	 */
	public String getViewerName() { return viewerName; }

	/**
	 * This is called when a new group has been created that
	 * we care about.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that was just created
	 */
	public void groupCreated(CyGroup group) { }

	/**
	 * This is called when a group we care about is about to 
	 * be deleted.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) { }


	/**
	 * The NamedSelectionMenuListener provides the interface to the structure viz
	 * Node context menu and the plugin menu.
	 */
	public class NamedSelectionMenuListener implements MenuListener {
		private NamedSelectionCommandListener staticHandle;
		private NodeView overNode = null;

		/**
		 * Create the namedSelection menu listener
		 *
		 * @param nv the Cytoscape NodeView the mouse was over
		 */
		NamedSelectionMenuListener(NodeView nv) {
			this.staticHandle = new NamedSelectionCommandListener(NONE,null);
			this.overNode = nv;
		}

	  public void menuCanceled (MenuEvent e) {};
		public void menuDeselected (MenuEvent e) {};

		/**
		 * Process the selected menu
		 *
		 * @param e the MenuEvent for the selected menu
		 */
		public void menuSelected (MenuEvent e)
		{
			JMenu m = (JMenu)e.getSource();
			// Clear the menu
			Component[] subMenus = m.getMenuComponents();
			for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }

			CyNetwork network = Cytoscape.getCurrentNetwork();
			Set currentNodes = network.getSelectedNodes();
			List<CyGroup>groupList = CyGroup.getGroupList(groupViewer);

			// Add our menu items
			{
			  JMenuItem item = new JMenuItem("Create new named selection");
				NamedSelectionCommandListener l = new NamedSelectionCommandListener(NEW, null);
				item.addActionListener(l);
				if (currentNodes.size() > 0) {
					item.setEnabled(true);
				} else {
					item.setEnabled(false);
				}
				m.add(item);
			}

			if (groupList != null && groupList.size() > 0) {
			  JMenu item = new JMenu("Remove named selection");
				addGroupMenu(item, REMOVE, groupList);
				m.add(item);
			} else {
			  JMenuItem item = new JMenuItem("Remove named selection");
				item.setEnabled(false);
				m.add(item);
			}

			if (groupList != null && groupList.size() > 0) {
			  JMenu item = new JMenu("Select");
				addGroupMenu(item, SELECT, groupList);
				m.add(item);
			} else {
			  JMenuItem item = new JMenuItem("Select");
				item.setEnabled(false);
				m.add(item);
			}

			if (groupList != null && groupList.size() > 0) {
			  JMenu item = new JMenu("Unselect");
				addGroupMenu(item, UNSELECT, groupList);
				m.add(item);
			} else {
			  JMenuItem item = new JMenuItem("Unselect");
				item.setEnabled(false);
				m.add(item);
			}
		}

		/**
		 * Add all groups to a menu
		 */
		private void addGroupMenu(JMenu menu, int command, List<CyGroup>groupList) {
				if (groupList == null) return;
				// List current named selections
				Iterator iter = groupList.iterator();
				while (iter.hasNext()) {
					CyGroup group = (CyGroup)iter.next();
					addSubMenu(menu, group.getGroupName(), command, group);
				}
		}

		/**
		 * Add a submenu item to an existing menu
		 *
		 * @param menu the JMenu to add the new submenu to
		 * @param group the group node
		 */
		private void addSubMenu(JMenu menu, String label, int command, CyGroup group) {
			JMenuItem item = new JMenuItem(label);
			NamedSelectionCommandListener l = new NamedSelectionCommandListener(command, group);
			item.addActionListener(l);
		  menu.add(item);
		}
	}
	
  /**
   * This class gets attached to the menu item.
   */
  static class NamedSelectionCommandListener implements ActionListener {
  	private static final long serialVersionUID = 1;
		private int command;
		private CyGroup group = null; // The group we care about

		NamedSelectionCommandListener(int command, CyGroup group) {
			this.command = command;
			this.group = group;
		}

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
			String label = ae.getActionCommand();
			if (command == SELECT) {
				select();
			} else if (command == UNSELECT) {
				unselect();
			} else if (command == NEW) {
				newGroup();
			} else if (command == REMOVE) {
				removeGroup();
			}
		}

		/**
		 * Create a new group.  Eventually, this should be replaced by a more
		 * pleasing dialog that allows the user to choose their own name.
		 */
		private void newGroup() {
			CyNetwork network = Cytoscape.getCurrentNetwork();
			List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());
			List<CyGroup> groupList = CyGroup.getGroupList();
			String groupName = JOptionPane.showInputDialog("Please enter a name for this selection");
			CyGroup group = CyGroup.createGroup(groupName, currentNodes, viewerName);
			group.setState(SELECTED);
		}

		/**
		 * Remove a group.
		 */
		private void removeGroup() {
			CyGroup.removeGroup(group);
		}

		/**
		 * Perform the action associated with a select menu selection
		 */
		private void select() {
			List<CyNode> nodeList = group.getNodes();
			Cytoscape.getCurrentNetwork().setSelectedNodeState(nodeList, true);
			group.setState(SELECTED);
		}

		/**
		 * Perform the action associated with an unselect menu selection
		 */
		private void unselect() {
			List<CyNode> nodeList = group.getNodes();
			Cytoscape.getCurrentNetwork().setSelectedNodeState(nodeList, false);
			group.setState(UNSELECTED);
		}
	}
}
