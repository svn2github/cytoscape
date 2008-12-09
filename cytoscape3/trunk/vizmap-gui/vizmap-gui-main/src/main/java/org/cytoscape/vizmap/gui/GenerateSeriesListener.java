
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.vizmap.gui;

import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;

import org.cytoscape.view.GraphView;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;

import java.awt.event.ActionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;


/**
 *
 */
public class GenerateSeriesListener extends AbstractVizMapperAction {
	private final static long serialVersionUID = 121374883715581L;
	private DiscreteMapping dm;

	/**
	 * User wants to Seed the Discrete Mapper with Random Color Values.
	 */
	public void actionPerformed(ActionEvent e) {
		final CyNetwork targetNetwork = Cytoscape.getCurrentNetwork();
		final GraphView targetView = Cytoscape.getCurrentNetworkView();

		/*
		 * Check Selected poperty
		 */
		final int selectedRow = vizMapperMainPanel.getPropertySheetPanel().getTable()
		                                          .getSelectedRow();

		if (selectedRow < 0)
			return;

		final Item item = (Item) vizMapperMainPanel.getPropertySheetPanel().getTable()
		                                           .getValueAt(selectedRow, 0);
		final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
		final Object hidden = prop.getHiddenObject();

		if (hidden instanceof VisualPropertyType) {
			final VisualPropertyType type = (VisualPropertyType) hidden;

			final Map valueMap = new HashMap();
			final ObjectMapping oMap;
			final CyDataTable attr;
			final int nOre;

			if (type.isNodeProp()) {
				attr = targetNetwork.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
				oMap = vmm.getVisualStyle().getNodeAppearanceCalculator().getCalculator(type)
				          .getMapping(0);
				nOre = ObjectMapping.NODE_MAPPING;
			} else {
				attr = targetNetwork.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
				oMap = vmm.getVisualStyle().getEdgeAppearanceCalculator().getCalculator(type)
				          .getMapping(0);
				nOre = ObjectMapping.EDGE_MAPPING;
			}

			if ((oMap instanceof DiscreteMapping) == false)
				return;

			dm = (DiscreteMapping) oMap;

			// final Set<Object> attrSet =
			// loadKeys(oMap.getControllingAttributeName(), attr,
			// oMap, nOre);
			final Set<Object> attrSet = new TreeSet<Object>(attr.getColumnValues(oMap
			                                                                                                           .getControllingAttributeName(),
			                                                                     attr.getColumnTypeMap()
			                                                                         .get(oMap
			                                                                                                              .getControllingAttributeName())));

			final String start = JOptionPane.showInputDialog(vizMapperMainPanel
			                                                                                                                 .getPropertySheetPanel(),
			                                                 "Please enter start value (1st number in the series)",
			                                                 "0");
			final String increment = JOptionPane.showInputDialog(vizMapperMainPanel
			                                                                                                                     .getPropertySheetPanel(),
			                                                     "Please enter increment", "1");

			if ((increment == null) || (start == null))
				return;

			Float inc;
			Float st;

			try {
				inc = Float.valueOf(increment);
				st = Float.valueOf(start);
			} catch (Exception ex) {
				ex.printStackTrace();
				inc = null;
				st = null;
			}

			if ((inc == null) || (inc < 0) || (st == null) || (st == null)) {
				return;
			}

			if (type.getDataType() == Number.class) {
				for (Object key : attrSet) {
					valueMap.put(key, st);
					st = st + inc;
				}
			}

			dm.putAll(valueMap);

			vmm.setNetworkView(targetView);
			Cytoscape.redrawGraph(targetView);

			vizMapperMainPanel.getPropertySheetPanel().removeProperty(prop);

			final VizMapperProperty newRootProp = new VizMapperProperty();

			if (type.isNodeProp())
				vizMapperMainPanel.buildProperty(vmm.getVisualStyle().getNodeAppearanceCalculator()
				                                    .getCalculator(type), newRootProp,
				                                 VizMapperMainPanel.NODE_VISUAL_MAPPING);
			else
				vizMapperMainPanel.buildProperty(vmm.getVisualStyle().getEdgeAppearanceCalculator()
				                                    .getCalculator(type), newRootProp,
				                                 VizMapperMainPanel.EDGE_VISUAL_MAPPING);

			vizMapperMainPanel.removeProperty(prop);
			vizMapperMainPanel.getPropertyMap().get(vmm.getVisualStyle().getName()).add(newRootProp);

			vizMapperMainPanel.expandLastSelectedItem(type.getName());
		} else {
			System.out.println("Invalid.");
		}

		return;
	}
}
