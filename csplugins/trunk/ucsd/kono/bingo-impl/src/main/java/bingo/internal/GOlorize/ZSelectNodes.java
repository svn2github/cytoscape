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

package bingo.internal.GOlorize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
//import org.cytoscape.model.CyTableEntry;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import bingo.internal.ontology.Annotation;



public class ZSelectNodes implements ActionListener {
	Set goSelected;
	ResultAndStartPanel result;
	Annotation annotation;

	private final CySwingAppAdapter adapter;
	
	/** Creates a new instance of ZSelectNodes */
	public ZSelectNodes(ResultAndStartPanel result, final CySwingAppAdapter adapter) {
		this.result = result;
		this.adapter = adapter;
	}

	public void actionPerformed(ActionEvent ev) {
		
		this.annotation = result.getAnnotation();
		Map<String, Set<String>> alias = result.getAlias();
		JTable jTable = result.getJTable();
		goSelected = getSelectedGoSet(jTable);
		CyNetworkView currentNetworkView = result.getNetworkView();
		CyNetwork currentNetwork = result.getNetworkView().getModel();
		
		if (currentNetworkView != null) {
			final CyNetwork model = currentNetworkView.getModel();
			final List<CyNode> nodes = model.getNodeList();
			for (final CyNode node : nodes){
				//node.getCyRow().set(CyNetwork.SELECTED, false);
				model.getDefaultNodeTable().getRow(node.getSUID()).set(CyNetwork.SELECTED, false);
				
			}
			final Set<CyNode> selectedNodesSet = new HashSet<CyNode>();

			if (result instanceof ResultPanel) {
				this.annotation = result.getAnnotation();
				final Collection<View<CyNode>> nodeViews = currentNetworkView.getNodeViews();				
				for(View<CyNode> nodeView: nodeViews) {
					Set goAnnot = new HashSet();
					
					final CyNode node = nodeView.getModel();
					//final String nodeName = node.getCyRow().get(CyNetwork.NAME, String.class);
					final String nodeName = currentNetwork.getDefaultNodeTable().getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
					
					Set identifiers = alias.get(nodeName);
					if (identifiers != null) {
						Iterator it = identifiers.iterator();
						while (it.hasNext()) {
							int[] goID = annotation.getClassifications(it.next() + "");
							for (int t = 0; t < goID.length; t++) {
								goAnnot.add(goID[t] + "");
							}
						}
					}

					
					if (goAnnot != null) {
						Iterator it = goAnnot.iterator();
						while (it.hasNext()) {
							if (goSelected.contains((new Integer(it.next() + "")).toString())) {
								selectedNodesSet.add(nodeView.getModel());
								continue;
							}
						}
					}
					// this.annotation=null;
				}
				

				for(final CyNode node: selectedNodesSet){
					//node.getCyRow().set(CyNetwork.SELECTED, true);
					currentNetwork.getDefaultNodeTable().getRow(node.getSUID()).set(CyNetwork.SELECTED, true);
				}

				adapter.getCyEventHelper().flushPayloadEvents();
				currentNetworkView.updateView();
			}

			this.annotation = null;
		}
		
	}

	private HashSet getSelectedGoSet(JTable jTable1) {
		// return a hashmap key = GO terms selected, //n'est utile aue si on
		// s'interesse qu'au genes refiles a bingo
		HashSet goSet = new HashSet();
		ArrayList genesList;

		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (((Boolean) jTable1.getValueAt(i, result.getSelectColumn())).booleanValue() == true) {

				goSet.add((String) jTable1.getValueAt(i, result.getGoTermColumn()));

			}
		}
		return goSet;
	}
}
