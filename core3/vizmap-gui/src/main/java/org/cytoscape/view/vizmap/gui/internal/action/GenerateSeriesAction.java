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
package org.cytoscape.view.vizmap.gui.internal.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.gui.internal.VizMapperMainPanel;
import org.cytoscape.view.vizmap.gui.internal.VizMapperProperty;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import static org.cytoscape.model.GraphObject.*;

/**
 *
 */
// TODO: this function is broken. Need to refactor.
public class GenerateSeriesAction extends AbstractVizMapperAction {

	private final static long serialVersionUID = 121374883715581L;

	public GenerateSeriesAction() {
		super();
	}

	/**
	 * User wants to Seed the Discrete Mapper with Random Color Values.
	 */
	public void actionPerformed(ActionEvent e) {
		final CyNetwork targetNetwork = cyNetworkManager.getCurrentNetwork();

		VisualProperty<?> vp = vizMapperUtil
				.getSelectedVisualProperty(propertySheetPanel);
		DiscreteMapping<?, ?> oMap = vizMapperUtil.getSelectedProperty(
				this.vizMapperMainPanel.getDefaultVisualStyle(),
				propertySheetPanel);

		if (vp != null && oMap != null) {

			final CyDataTable attr;

			if (vp.getObjectType().equals(NODE))
				attr = targetNetwork.getNodeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
			else
				attr = targetNetwork.getEdgeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);

			final Set<Object> attrSet = new TreeSet<Object>(attr
					.getColumnValues(oMap.getMappingAttributeName(), attr
							.getColumnTypeMap().get(
									oMap.getMappingAttributeName())));

			final String start = JOptionPane.showInputDialog(
					propertySheetPanel,
					"Please enter start value (1st number in the series)", "0");
			final String increment = JOptionPane.showInputDialog(
					propertySheetPanel, "Please enter increment", "1");

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

			Map<Object, Object> valueMap = new HashMap<Object, Object>();
			if (vp.getType() == Number.class) {
				for (Object key : attrSet) {
					valueMap.put(key, st);
					st = st + inc;
				}
			}

			oMap.putAll((Map<?, ?>) valueMap);

			propertySheetPanel.removeProperty(prop);

			final VizMapperProperty<?> newRootProp = new VizMapperProperty();

			if (vp.getObjectType().equals(NODE))
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
						oMap, newRootProp,
						VizMapperMainPanel.NODE_VISUAL_MAPPING,
						propertySheetPanel);
			else
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
						oMap, newRootProp,
						VizMapperMainPanel.EDGE_VISUAL_MAPPING,
						propertySheetPanel);

			vizMapPropertySheetBuilder.removeProperty(prop);
			vizMapPropertySheetBuilder.getPropertyMap().get(
					vmm.getVisualStyle().getName()).add(newRootProp);

			vizMapPropertySheetBuilder.expandLastSelectedItem(type.getName());
		} else {
			System.out.println("Invalid.");
		}

		return;
	}
}
