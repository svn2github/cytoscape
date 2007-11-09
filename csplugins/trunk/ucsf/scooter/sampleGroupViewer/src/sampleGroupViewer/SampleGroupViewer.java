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
package sampleGroupViewer;

// System imports
import java.util.List;
import java.util.HashMap;
import java.awt.Dimension;

// Swing imports
import javax.swing.JOptionPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;

// Cytoscape group system imports
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

/**
 * A sample CyGroupViewer.  This plugin provides a *very* simple group
 * viewer.  The idea is to put a list of groups up into CytoPanel1 and
 * then allow users to select the members of the group by selecting
 * the group entry in CytoPanel1.  For a more complete implementation
 * of this idea, look at the nameSelectionPlugin.  As a plugin, this
 * class must extend CytoscapePlugin.  We also have it implement
 * CyGroupViewer, but this could also be done in a separate class.
 */
public class SampleGroupViewer extends CytoscapePlugin 
                               implements CyGroupViewer { 

	public static final String viewerName = "sampleGroupViewer";
	public static final double VERSION = 1.0;

	private static GroupPanel groupPanel = null;

  /**
   * SampleGroupViewer interface
   */
	public SampleGroupViewer() {

		// Under normal circumstances, we might create a menu and perhaps
		// setting up node context menu listeners, etc.

		// Add our interface to CytoPanel 1
		groupPanel = new GroupPanel(this);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add("GroupsTest", groupPanel);

		// Register with CyGroup
		CyGroupManager.registerGroupViewer(this);
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
	public void groupCreated(CyGroup group) { 
		groupPanel.groupCreated(group);
	}

	/**
	 * This is called when a new group has been created that
	 * we care about, but the network view may not be the
	 * current network view (e.g. during XGMML creation).
	 *
	 * @param group the CyGroup that was just created
	 * @param view the CyNetworkView that this group is being
	 * created under
	 */
	public void groupCreated(CyGroup group, CyNetworkView view) { 
		// Make sure we get rid of the group node
		CyNode node = group.getGroupNode();
		view.getNetwork().removeNode(node.getRootGraphIndex(), false);
		// In our case, we don't really care about the network view
		groupPanel.groupCreated(group);
	}

	/**
	 * This is called when a group we care about is about to 
	 * be deleted.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) { 
		groupPanel.groupRemoved(group);
	}

	/**
	 * This is called when a group we care about is changed.
	 *
	 * @param group the CyGroup that has changed
	 * @param node the CyNode that caused the change
	 * @param change the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, 
	                         ChangeType change) { 
		groupPanel.groupChanged(group);
	}

	/**
	 * The GroupPanel is the implementation for the Cytopanel that presents
	 * the group list mechanism to the user.
	 */
	public class GroupPanel extends JPanel implements ListSelectionListener {
		CyGroupViewer viewer;
		JList navList;
		DefaultListModel listModel;

		public GroupPanel(CyGroupViewer viewer) {
			super();
			this.viewer = viewer;
	
			// Create the list
			listModel = new DefaultListModel();
			// listModel.addElement("Clear selection");
			navList = new JList(listModel);
			navList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			navList.setLayoutOrientation(JList.VERTICAL);
			navList.setVisibleRowCount(20);
			navList.addListSelectionListener(this);

			JScrollPane listView = new JScrollPane(navList);
			listView.setBorder(BorderFactory.createEtchedBorder());
			navList.setBackground(Cytoscape.getDesktop().getBackground());
			listView.setBackground(Cytoscape.getDesktop().getBackground());
			this.setPreferredSize(new Dimension(240, 600));
			navList.setPreferredSize(new Dimension(240, 580));
			this.add(listView);
		}

		/**
		 * This is called when the user changes the selection
	 	 *
		 * @param e the event that caused us to be called
 		 */
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				//
				CyNetwork network = Cytoscape.getCurrentNetwork();
				for (int i = 0; i < listModel.getSize(); i++) {
					if (navList.getSelectionModel().isSelectedIndex(i)) {
						CyGroup group = (CyGroup)listModel.getElementAt(i);
						network.setSelectedNodeState(group.getNodes(), true);
					} else {
						CyGroup group = (CyGroup)listModel.getElementAt(i);
						network.setSelectedNodeState(group.getNodes(), false);
					}
				}
				Cytoscape.getCurrentNetworkView().updateView();
			}
		}

		// Methods to manage the list of groups
		public void groupCreated(CyGroup group) {
			listModel.addElement(group);
		}

		public void groupRemoved(CyGroup group) {
			listModel.removeElement(group);
		}

		public void groupChanged(CyGroup group) {}
	}
}

