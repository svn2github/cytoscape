/* * Modified Date: Jul.27.2010
 * * by : Steven Maere
 * */

/*
 * ZSelectNodes.java
 *
 * Created on May 13, 2006, 7:19 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package pingo.GOlorize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class ZSelectNodes implements ActionListener {
	private Set<CyNode> genesSelected;
	private ResultAndStartPanel result;

	private final CyPluginAdapter adapter;

	/** Creates a new instance of ZSelectNodes */
	public ZSelectNodes(final ResultAndStartPanel result, final CyPluginAdapter adapter) {
		this.result = result;
		this.adapter = adapter;
	}

	public void actionPerformed(ActionEvent ev) {
		JTable jTable = result.getJTable();
		genesSelected = getSelectedGeneSet(jTable);

		CyNetworkView currentNetworkView = result.getNetworkView();
		if (currentNetworkView != null) {
			final CyNetwork model = currentNetworkView.getModel();
			final List<CyNode> nodes = model.getNodeList();
			for (final CyNode node : nodes)
				node.getCyRow().set(CyNetwork.SELECTED, false);

			final Set<CyNode> selectedNodesSet = new HashSet<CyNode>();

			if (result instanceof ResultPanel) {
				final Collection<View<CyNode>> nodeViews = currentNetworkView.getNodeViews();
				for (View<CyNode> nodeView : nodeViews) {
					final String nodeName = nodeView.getModel().getCyRow().get(CyTableEntry.NAME, String.class);
					if (genesSelected.contains(nodeName))
						selectedNodesSet.add(nodeView.getModel());
				}
				for (final CyNode node : selectedNodesSet)
					node.getCyRow().set(CyNetwork.SELECTED, true);

				adapter.getCyEventHelper().flushPayloadEvents();
				currentNetworkView.updateView();
			}
		}
	}

	private Set getSelectedGeneSet(JTable jTable1) {
		Set geneSet = new HashSet();

		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (((Boolean) jTable1.getValueAt(i, result.getSelectColumn())).booleanValue() == true) {
				geneSet.add((String) jTable1.getValueAt(i, result.getGeneColumn()));

			}
		}
		return geneSet;
	}
}
