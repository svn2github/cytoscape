/*
  File: GraphMerge.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute of Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package GraphMerge;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.util.GraphSetUtils;
import cytoscape.util.CyNetworkNaming;

import cytoscape.view.CyNetworkView;

import giny.model.Edge;
import giny.model.Node;

import giny.view.EdgeView;
import giny.view.Label;
import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * This is a plugin which is used to merge different graph objects into a single
 * graph object. In order to determine which visual attribute is inherited, the
 * user specifies an ordering. The visual mapping information is taken from the
 * highest network in that ordering.
 */
public class GraphMerge extends CytoscapePlugin {
	/**
	 * This constructor registers the plugin action in hte operations menu
	 */
	public GraphMerge() {
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new TestAction());
	}

	public class TestAction extends AbstractAction {
		public TestAction() {
			super("Merge networks");
		}

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {
			MergeDialog dialog = new MergeDialog();
			dialog.setVisible(true);

			if (!dialog.isCancelled()) {
				if (dialog.getOperation() == MergeDialog.UNION) {
					GraphSetUtils.createUnionGraph(dialog.getNetworkList(), true, 
					                CyNetworkNaming.getSuggestedNetworkTitle("Union"));
				} else if (dialog.getOperation() == MergeDialog.INTERSECTION) {
					GraphSetUtils.createIntersectionGraph(dialog.getNetworkList(), true,
								    CyNetworkNaming.getSuggestedNetworkTitle("Intersection"));
				} else if (dialog.getOperation() == MergeDialog.DIFFERENCE) {
					GraphSetUtils.createDifferenceGraph(dialog.getNetworkList(), true, 
					                CyNetworkNaming.getSuggestedNetworkTitle("Difference"));
				}
			}
		}
	}
}
