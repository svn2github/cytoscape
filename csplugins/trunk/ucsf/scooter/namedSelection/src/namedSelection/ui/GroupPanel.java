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
package namedSelection.ui;

import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

import namedSelection.NamedSelection;
import namedSelection.ui.GroupCreationDialog;

// System imports
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import giny.model.Node;

/**
 * The GroupPanel is the implementation for the Cytopanel that presents
 * the named selection mechanism to the user.
 */
public class GroupPanel extends JPanel implements ActionListener {
	List<CyGroupViewer> viewerList = null;
	GroupTree navTree = null;
	ButtonGroup depthGroup = null;
	JButton deleteButton = null;
	JPanel depthBox = null;
	int currentDepth = 1;
	boolean ignoreGroupUpdates = false;

	private static final String CLEAR = "clear";
	private static final String DELETE = "delete";
	private static final String NEW = "new";

	/**
	 * Construct a group panel
	 *
	 * @param viewer the CyGroupViewer that created us
	 */
	public GroupPanel () {
		super();
		viewerList = new ArrayList();

		setPreferredSize(new Dimension(200,-1));
		setLayout(new BorderLayout());

		// Create a separate JPanel for our various controls
		JPanel controlPanel = new JPanel();
		// Set up our layout
		BoxLayout layout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
		controlPanel.setLayout(layout);

		// Create a button box at the top 
		JPanel buttonBox = new JPanel();
		// Create clear selection button
		buttonBox.add(createButton("Clear Selection", CLEAR, true));

		// Create new group button
		buttonBox.add(createButton("New Group", NEW, true));

		// Create delete group button
		deleteButton = createButton("Delete Group", DELETE, false);
		buttonBox.add(deleteButton);

		// Border it
		buttonBox.setBorder(BorderFactory.createEtchedBorder());
		controlPanel.add(buttonBox);

		depthBox = new JPanel();
		depthGroup = new ButtonGroup();
		addDepthButtons(1);

		// Border it
		Border depthBorder = BorderFactory.createEtchedBorder();
		TitledBorder dTitleBorder = BorderFactory.createTitledBorder(depthBorder, "Expansion Depth");
		dTitleBorder.setTitlePosition(TitledBorder.LEFT);
		dTitleBorder.setTitlePosition(TitledBorder.TOP);
		depthBox.setBorder(dTitleBorder);
		controlPanel.add(depthBox);

		add(controlPanel, BorderLayout.NORTH);

		// Create our JTree
		navTree = new GroupTree(this);
		navTree.setViewerList(viewerList);
		reload();

		JScrollPane treeView = new JScrollPane(navTree);
		treeView.setBorder(BorderFactory.createEtchedBorder());
		treeView.setBackground(Cytoscape.getDesktop().getBackground());

		add(treeView, BorderLayout.CENTER);
	}

	/**
 	 * Tell the GroupPanel that we have an additional viewer who
 	 * wants to use us
 	 *
 	 * @param viewer the new viewer
 	 */
	public void addViewer(CyGroupViewer viewer) {
		if (viewerList.contains(viewer))
			return;
		viewerList.add(viewer);
		navTree.setViewerList(viewerList);

		// System.out.println("Added new viewer: "+viewer.getViewerName());

		// Do a reload so we can get the new groups
		reload();
	}

	/**
	 * Update the JTree to reflect the creation of a new group
	 *
	 * @param group the CyGroup that just got created
	 */
	public void groupCreated(CyGroup group) {
		reload();
	}

	/**
	 * Update the JTree to reflect the removal of a group
	 *
	 * @param group the CyGroup that just got removed
	 */
	public void groupRemoved(CyGroup group) {
		reload();
	}

	/**
	 * Update the JTree to reflect the change of a group (node
	 * addition or deletion)
	 *
	 * @param group the CyGroup that just got changed
	 */
	public void groupChanged(CyGroup group) {
		reload();
	}

	public void reload() {
		if (ignoreGroupUpdates)
			return;

		deleteButton.setEnabled(false);
		int maxDepth = navTree.reload();
		if (maxDepth != currentDepth) {
			addDepthButtons(maxDepth);
			currentDepth = maxDepth;
		}
		if (maxDepth > 1)
			deleteButton.setEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (CLEAR.equals(e.getActionCommand())) {

			navTree.clearAll();

		} else if (NEW.equals(e.getActionCommand())) {
			// Check and see if anything is selected
			Set nodeSet = Cytoscape.getCurrentNetwork().getSelectedNodes();
			if (nodeSet != null && nodeSet.size() > 0) {
				// Yes, create the group
				List<CyNode>currentNodes = new ArrayList<CyNode>(nodeSet);
				GroupCreationDialog dd = new GroupCreationDialog(Cytoscape.getDesktop(), currentNodes, viewerList);
				reload();
			} else {
				// No, tell the user
				JOptionPane.showMessageDialog(this, "You must select a set of nodes to be part of the group", 
				                              "No nodes", JOptionPane.ERROR_MESSAGE);
			}

		} else if (DELETE.equals(e.getActionCommand())) {
			// Do we have a path to a group?
			List<CyGroup>groupList = navTree.getSelectedGroups();
			
			if (groupList.size() == 0) {
					JOptionPane.showMessageDialog(this, "No groups are selected", "No groups", JOptionPane.ERROR_MESSAGE);
					return;
			}
			// Yes, do the appropriate "are you sure"
			int ans = JOptionPane.showConfirmDialog(this, "You are deleting "+groupList.size()+" groups.  Are you sure?", 
			                                        "Confirm group delete", JOptionPane.YES_NO_OPTION);
			if (ans == 0) {
				// Delete them
				ignoreGroupUpdates = true;
				for (CyGroup group: groupList) {
					CyGroupManager.removeGroup(group);
				}
				ignoreGroupUpdates = false;
				reload();
			}
		} else if ("1".equals(e.getActionCommand())) {
			navTree.setTreeDepth(1);
		} else if ("2".equals(e.getActionCommand())) {
			navTree.setTreeDepth(2);
		} else if ("3".equals(e.getActionCommand())) {
			navTree.setTreeDepth(3);
		} else if ("4".equals(e.getActionCommand())) {
			navTree.setTreeDepth(4);
		} else if ("5".equals(e.getActionCommand())) {
			navTree.setTreeDepth(5);
		} else if ("6".equals(e.getActionCommand())) {
			navTree.setTreeDepth(6);
		} else if ("7".equals(e.getActionCommand())) {
			navTree.setTreeDepth(7);
		} else if ("8".equals(e.getActionCommand())) {
			navTree.setTreeDepth(8);
		}
	}

	public GroupTree getTree() {
		return navTree;
	}

	private JButton createButton(String label, String command, boolean enabled) {
		JButton newButton = new JButton("<html><span style='font-size: 80%;'>"+label+"</span></html>");
		newButton.setActionCommand(command);
		newButton.addActionListener(this);
		newButton.setEnabled(enabled);
		return newButton;
	}

	private void addDepthButtons(int depth) {
		int maxDepth = depth;
		if (maxDepth > 8) maxDepth = 8;

		// Get the number of buttons currently in the group
		int buttonCount = depthGroup.getButtonCount();
		if (buttonCount > maxDepth) {
			for (Enumeration <AbstractButton> buttons = depthGroup.getElements(); buttons.hasMoreElements() ;) {
				AbstractButton b = buttons.nextElement();
				String command = b.getActionCommand();
				if (Integer.parseInt(command) > maxDepth) {
					depthGroup.remove(b);
					depthBox.remove(b);
				}
			}
		} else {
			for (int count = buttonCount+1; count < maxDepth+1; count++) {
				JRadioButton depthButton = new JRadioButton("<html><span style='font-size: 70%'>"+count+"</span></html>");
				depthButton.setActionCommand(""+count);
				depthButton.addActionListener(this);
				depthGroup.add(depthButton);
				depthBox.add(depthButton);
			}
		}
	}

}
