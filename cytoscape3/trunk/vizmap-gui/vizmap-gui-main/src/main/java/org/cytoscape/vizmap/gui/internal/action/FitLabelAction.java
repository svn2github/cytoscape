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

package org.cytoscape.vizmap.gui.internal.action;

import static org.cytoscape.vizmap.VisualPropertyType.NODE_FONT_SIZE;
import static org.cytoscape.vizmap.VisualPropertyType.NODE_HEIGHT;
import static org.cytoscape.vizmap.VisualPropertyType.NODE_WIDTH;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.gui.internal.AbstractVizMapperPanel;
import org.cytoscape.vizmap.gui.internal.VizMapperProperty;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;

import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;

/**
 *
 */
public class FitLabelAction extends AbstractVizMapperAction {

	public FitLabelAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	private final static long serialVersionUID = 121374883744077L;
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
		final int selectedRow = propertySheetPanel.getTable().getSelectedRow();

		if (selectedRow < 0)
			return;

		final Item item = (Item) propertySheetPanel.getTable().getValueAt(
				selectedRow, 0);
		final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
		final Object hidden = prop.getHiddenObject();

		if (hidden instanceof VisualPropertyType) {
			final VisualPropertyType type = (VisualPropertyType) hidden;

			final Map valueMap = new HashMap();
			final ObjectMapping oMap;
			final CyDataTable attr;

			if (type.isNodeProp()) {
				attr = targetNetwork.getNodeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				oMap = vmm.getVisualStyle().getNodeAppearanceCalculator()
						.getCalculator(type).getMapping(0);
			} else {
				attr = targetNetwork.getEdgeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				oMap = vmm.getVisualStyle().getEdgeAppearanceCalculator()
						.getCalculator(type).getMapping(0);
			}

			if ((oMap instanceof DiscreteMapping) == false)
				return;

			dm = (DiscreteMapping) oMap;

			final Calculator nodeLabelCalc = vmm.getVisualStyle()
					.getNodeAppearanceCalculator().getCalculator(
							VisualPropertyType.NODE_LABEL);

			if (nodeLabelCalc == null) {
				return;
			}

			final String ctrAttrName = nodeLabelCalc.getMapping(0)
					.getControllingAttributeName();
			dm.setControllingAttributeName(ctrAttrName, targetNetwork, false);

			// final Set<Object> attrSet =
			// loadKeys(oMap.getControllingAttributeName(), attr, oMap);
			if (vmm.getVisualStyle().getNodeAppearanceCalculator()
					.getNodeSizeLocked()) {
				return;
			}

			DiscreteMapping wm = null;

			if ((type == NODE_WIDTH)) {
				wm = (DiscreteMapping) vmm.getVisualStyle()
						.getNodeAppearanceCalculator()
						.getCalculator(NODE_WIDTH).getMapping(0);

				wm.setControllingAttributeName(ctrAttrName, targetNetwork,
						false);

				Set<Object> attrSet1;

				if (ctrAttrName.equals("ID")) {
					attrSet1 = new TreeSet<Object>();

					for (CyNode node : targetNetwork.getNodeList()) {
						attrSet1.add(node.attrs().get("name", String.class));
					}
				} else {
					// attrSet1 = loadKeys(wm.getControllingAttributeName(),
					// attr, wm,
					// ObjectMapping.NODE_MAPPING);
					attrSet1 = new TreeSet<Object>(attr.getColumnValues(oMap
							.getControllingAttributeName(), attr
							.getColumnTypeMap().get(
									oMap.getControllingAttributeName())));
				}

				Integer height = ((Number) (vmm.getVisualStyle()
						.getNodeAppearanceCalculator().getDefaultAppearance()
						.get(NODE_FONT_SIZE))).intValue();
				vmm.getVisualStyle().getNodeAppearanceCalculator()
						.getDefaultAppearance().set(NODE_HEIGHT, height * 2.5);

				Integer fontSize = ((Number) vmm.getVisualStyle()
						.getNodeAppearanceCalculator().getDefaultAppearance()
						.get(NODE_FONT_SIZE)).intValue();
				int strLen;

				String labelString = null;
				String[] listObj;
				int longest = 0;

				if (attr.getColumnTypeMap().get(ctrAttrName) == List.class) {
					wm.setControllingAttributeName("ID", targetNetwork, false);

					attrSet1 = new TreeSet<Object>();

					for (CyNode node : targetNetwork.getNodeList()) {
						attrSet1.add(node.attrs().get("name", String.class));
					}

					GraphView net = targetView;
					String text;

					for (CyNode node : net.getGraphPerspective().getNodeList()) {
						text = net.getNodeView(node).getLabel().getText();
						strLen = text.length();

						if (strLen != 0) {
							listObj = text.split("\\n");
							longest = 0;

							for (String s : listObj) {
								if (s.length() > longest) {
									longest = s.length();
								}
							}

							strLen = longest;

							if (strLen > 25) {
								valueMap.put(((CyNode) node).attrs().get(
										"name", String.class), strLen
										* fontSize * 0.6);
							} else {
								valueMap.put(((CyNode) node).attrs().get(
										"name", String.class), strLen
										* fontSize * 0.8);
							}
						}
					}
				} else {
					for (Object label : attrSet1) {
						labelString = label.toString();
						strLen = labelString.length();

						if (strLen != 0) {
							if (labelString.contains("\n")) {
								listObj = labelString.split("\\n");
								longest = 0;

								for (String s : listObj) {
									if (s.length() > longest) {
										longest = s.length();
									}
								}

								strLen = longest;
							}

							if (strLen > 25) {
								valueMap.put(label, strLen * fontSize * 0.6);
							} else {
								valueMap.put(label, strLen * fontSize * 0.8);
							}
						}
					}
				}
			} else if ((type == NODE_HEIGHT)) {
				wm = (DiscreteMapping) vmm.getVisualStyle()
						.getNodeAppearanceCalculator().getCalculator(
								NODE_HEIGHT).getMapping(0);

				wm.setControllingAttributeName(ctrAttrName, targetNetwork,
						false);

				Set<Object> attrSet1;

				if (ctrAttrName.equals("ID")) {
					attrSet1 = new TreeSet<Object>();

					for (CyNode node : targetNetwork.getNodeList()) {
						attrSet1.add(node.attrs().get("name", String.class));
					}
				} else {
					// attrSet1 = loadKeys(wm.getControllingAttributeName(),
					// attr, wm,
					// ObjectMapping.NODE_MAPPING);
					attrSet1 = new TreeSet<Object>(attr.getColumnValues(oMap
							.getControllingAttributeName(), attr
							.getColumnTypeMap().get(
									oMap.getControllingAttributeName())));
				}

				Integer fontSize = ((Number) vmm.getVisualStyle()
						.getNodeAppearanceCalculator().getDefaultAppearance()
						.get(NODE_FONT_SIZE)).intValue();
				int strLen;

				String labelString = null;
				String[] listObj;

				if (attr.getColumnTypeMap().get(ctrAttrName) == List.class) {
					wm.setControllingAttributeName("ID", targetNetwork, false);

					attrSet1 = new TreeSet<Object>();

					for (CyNode node : targetNetwork.getNodeList()) {
						attrSet1.add(node.attrs().get("name", String.class));
					}

					GraphView net = targetView;
					String text;

					for (CyNode node : net.getGraphPerspective().getNodeList()) {
						text = net.getNodeView(node).getLabel().getText();
						strLen = text.length();

						if (strLen != 0) {
							listObj = text.split("\\n");
							valueMap.put(((CyNode) node).attrs().get("name",
									String.class), listObj.length * fontSize
									* 1.6);
						}
					}
				} else {
					for (Object label : attrSet1) {
						labelString = label.toString();
						strLen = labelString.length();

						if (strLen != 0) {
							if (labelString.contains("\n")) {
								listObj = labelString.split("\\n");

								strLen = listObj.length;
							} else {
								strLen = 1;
							}

							valueMap.put(label, strLen * fontSize * 1.6);
						}
					}
				}
			}

			wm.putAll(valueMap);

			vmm.setNetworkView(targetView);
			Cytoscape.redrawGraph(targetView);

			propertySheetPanel.removeProperty(prop);

			final VizMapperProperty newRootProp = new VizMapperProperty();

			if (type.isNodeProp())
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
						vmm.getVisualStyle().getNodeAppearanceCalculator()
								.getCalculator(type), newRootProp,
						AbstractVizMapperPanel.NODE_VISUAL_MAPPING,
						propertySheetPanel);
			else
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
						vmm.getVisualStyle().getEdgeAppearanceCalculator()
								.getCalculator(type), newRootProp,
						AbstractVizMapperPanel.EDGE_VISUAL_MAPPING,
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
