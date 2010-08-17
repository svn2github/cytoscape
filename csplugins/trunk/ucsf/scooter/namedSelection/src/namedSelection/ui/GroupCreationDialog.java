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
package namedSelection.ui;

import java.util.ArrayList;
import java.util.List;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

import namedSelection.NamedSelection;

// System imports
import javax.swing.JDialog;

/**
 * The GroupCreationDialog is used to get the information from the
 * user required to create a group.
 */
public class GroupCreationDialog extends JDialog implements ActionListener {
	JTextField groupNameField = null;
	JComboBox viewerChoices = null;
	JCheckBox globalGroup = null;
	List<CyGroupViewer>viewerList = null;
	List<CyNode>currentNodes = null;
	CyGroup group = null;
	
	public GroupCreationDialog(JFrame owner, List<CyNode>currentNodes, List<CyGroupViewer>viewerList) {
		// Start by creating the dialog
		super(owner, "Create a New Group", true);

		this.viewerList = viewerList;
		this.currentNodes = currentNodes;

		// Do some customization
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Create our content pane
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JPanel textPane = new JPanel();
		groupNameField = new JTextField(20);
		JLabel groupLabel = new JLabel("Enter group name: ");
		groupLabel.setLabelFor(groupNameField);
		textPane.add(groupLabel);
		textPane.add(groupNameField);
		contentPane.add(textPane);

		// Add a checkbox for global vs. local group
		globalGroup = new JCheckBox("Global group: ");
		contentPane.add(globalGroup);


		// See if we need to handle the viewer issue....
		if (viewerList.size() > 1) {
			// Yes, add a combo box with the viewer names
			String[] viewerStrings = new String[viewerList.size()];
			int index = 0;
			for (CyGroupViewer viewer: viewerList) {
				viewerStrings[index++] = viewer.getViewerName();
			}
			viewerChoices = new JComboBox(viewerStrings);
			contentPane.add (viewerChoices);
		}

		// Finally, add a separator and our button box
		contentPane.add(new JSeparator());

		JPanel buttonBox = new JPanel();
		JButton yesButton = new JButton("Yes");
		yesButton.setActionCommand("yes");
		yesButton.addActionListener(this);
		yesButton.setDefaultCapable(true);
		buttonBox.add(yesButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		cancelButton.setDefaultCapable(false);
		buttonBox.add(cancelButton);
		contentPane.add(buttonBox);
		add(contentPane);

		JRootPane rootPane = getRootPane();
		rootPane.setDefaultButton(yesButton);

		// Show it!
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if ("cancel".equals(e.getActionCommand())) {
			group = null;
			setVisible(false);
		} else if ("yes".equals(e.getActionCommand())) {
			// Get the name
			String groupName = groupNameField.getText();
		
			// Get the viewer
			CyGroupViewer viewer = viewerList.get(0);
			if (viewerList.size() > 1) {
				String viewerName = (String)viewerChoices.getSelectedItem();
				viewer = CyGroupManager.getGroupViewer(viewerName);
			} 

			// Now, see if this is a global group
			CyNetwork network;
			if (globalGroup.isSelected())
				network = null;
			else
				network = Cytoscape.getCurrentNetwork();
			// Create the group
			group = CyGroupManager.createGroup(groupName, currentNodes, new ArrayList<CyEdge>(), new ArrayList<CyEdge>(), null, network);
			// Already there?
			if (group == null) {
				// Warn the user and return
				JOptionPane.showMessageDialog(this, "The group "+groupName+" already exists!", "Group Exists", JOptionPane.ERROR_MESSAGE);
				return;
			}
			CyGroupManager.setGroupViewer(group, viewer.getViewerName(), Cytoscape.getCurrentNetworkView(), true);
			if (viewer.getViewerName().equals(NamedSelection.viewerName))
				group.setState(NamedSelection.SELECTED);
			setVisible(false);
		}
	}
}
