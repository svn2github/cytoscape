/*
  File: AlphabeticalSelectionAction.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

// $Revision: 12968 $
// $Date: 2008-02-06 15:34:25 -0800 (Wed, 06 Feb 2008) $
// $Author: mes $

//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CySwingApplication;
import cytoscape.data.Semantics;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.GraphView;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class AlphabeticalSelectionAction extends CytoscapeAction implements ActionListener {
	private final static long serialVersionUID = 120233986968086L;
	JDialog dialog;
	JButton search;
	JButton cancel;
	JTextField searchField;
	private CySwingApplication desktop;
	/**
	 * Creates a new AlphabeticalSelectionAction object.
	 */
	public AlphabeticalSelectionAction(CySwingApplication desktop, CyNetworkManager netmgr) {
		super("By Name...",netmgr);
		setPreferredMenu("Select.Nodes");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_F, ActionEvent.CTRL_MASK);
		this.desktop = desktop;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancel) {
			dialog.setVisible(false);

			return;
		}

		if ((e.getSource() == searchField) || (e.getSource() == search)) {
			String search_string = searchField.getText();
			selectNodesStartingWith(netmgr.getCurrentNetwork(),
			                                           search_string,
			                                           netmgr.getCurrentNetworkView());
			netmgr.getCurrentNetworkView().updateView();

			return;
		}

		if (dialog == null)
			createDialog();

		dialog.setVisible(true);

		netmgr.getCurrentNetworkView().updateView();
	}

	private JDialog createDialog() {
		dialog = new JDialog(desktop.getJFrame(), "Select Nodes By Name", false);

		JPanel main_panel = new JPanel();
		main_panel.setLayout(new BorderLayout());

		JLabel label = new JLabel("<HTML>Select nodes whose <B>name or synonym</B> is like <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
		main_panel.add(label, BorderLayout.NORTH);

		searchField = new JTextField(30);
		searchField.addActionListener(this);
		main_panel.add(searchField, BorderLayout.CENTER);

		JPanel button_panel = new JPanel();
		search = new JButton("Search");
		cancel = new JButton("Cancel");
		search.addActionListener(this);
		cancel.addActionListener(this);
		button_panel.add(search);
		button_panel.add(cancel);
		main_panel.add(button_panel, BorderLayout.SOUTH);

		dialog.setContentPane(main_panel);
		dialog.pack();

		return dialog;
	}

    public void menuSelected(MenuEvent e) {
        enableForNetwork();
    }


	/**
	 * Selects every node in the current view whose canonical name, label, or
	 * any known synonym starts with the string specified by the second
	 * argument. Note that synonyms are only available if a naming server is
	 * available.
	 *
	 * This method does not change the selection state of any node that doesn't
	 * match the given key, allowing multiple selection queries to be
	 * concatenated.
	 */
	private boolean selectNodesStartingWith(CyNetwork network, String key,
	                                              GraphView networkView) {
		if ((network == null) || (key == null) || (networkView == null)) {
			return false;
		}

		key = key.toLowerCase();

		boolean found = false;
		String callerID = "CyNetworkUtilities.selectNodesStartingWith";

		int nodeFound = 0;
		Vector<CyNode> matchedNodes = new Vector<CyNode>();

		for ( CyNode node : network.getNodeList() ) {
			String nodeUID = node.attrs().get("name",String.class);

			boolean matched = false;

			if ((nodeUID != null) && nodeUID.toLowerCase().matches(key)) {
				matched = true;
				found = true;
				matchedNodes.add(node);
			} else {
				// this list always includes the canonical name itself
				java.util.List synonyms = Semantics.getAllSynonyms(node, network);

				for (Iterator synI = synonyms.iterator(); synI.hasNext();) {
					String synonym = (String) synI.next();

					if (synonym.toLowerCase().matches(key)) {
						matched = true;
						found = true;
						matchedNodes.add(node);

						break;
					}
				} 
			} 

			if (matched)
				nodeFound++;
		} 

		if (nodeFound == 0) {
			JOptionPane.showMessageDialog(null, "No match for the string \"" + key + "\"",
			                              "Error: Node Not Found", JOptionPane.ERROR_MESSAGE);
		}

		if (nodeFound > 0) {
			for ( CyNode n : matchedNodes )
				n.attrs().set("selected",true);
		}

		//System.out.println("node found = " + nodeFound);
		return found;
	}
}
