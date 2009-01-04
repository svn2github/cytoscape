
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.GraphView;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.gui.internal.VizMapperMainPanel;
import org.cytoscape.vizmap.gui.internal.VizMapperProperty;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.MappingCalculator;

import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;


/**
 *
 */
public class ModifyBrightnessAction extends AbstractVizMapperAction {
	private final static long serialVersionUID = 121374883775182L;
	private DiscreteMapping dm;
	protected static final int DARKER = 1;
	protected static final int BRIGHTER = 2;
	private final int functionType;

	/**
	 * Creates a new BrightnessListener object.
	 *
	 * @param type  DOCUMENT ME!
	 */
	public ModifyBrightnessAction(final int type) {
		this.functionType = type;
	}

	/**
	 * User wants to Seed the Discrete Mapper with Random Color Values.
	 */
	public void actionPerformed(ActionEvent e) {
		final CyNetwork targetNetwork = cyNetworkManager.getCurrentNetwork();

		/*
		 * Check Selected poperty
		 */
		final int selectedRow = propertySheetPanel.getTable()
		                                          .getSelectedRow();

		if (selectedRow < 0)
			return;

		final Item item = (Item) propertySheetPanel.getTable()
		                                           .getValueAt(selectedRow, 0);
		final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
		final Object hidden = prop.getHiddenObject();

		if (hidden instanceof VisualProperty) {
			final VisualProperty type = (VisualProperty) hidden;

			final Map valueMap = new HashMap();
			final MappingCalculator oMap;

			final CyDataTable attr;
			final int nOre;

			if (type.isNodeProp()) {
				attr = targetNetwork.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
				oMap = vmm.getVisualStyle().getNodeAppearanceCalculator().getCalculator(type)
				          .getMapping(0);
				nOre = MappingCalculator.NODE_MAPPING;
			} else {
				attr = targetNetwork.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
				oMap = vmm.getVisualStyle().getEdgeAppearanceCalculator().getCalculator(type)
				          .getMapping(0);
				nOre = MappingCalculator.EDGE_MAPPING;
			}

			if ((oMap instanceof DiscreteMapping) == false) {
				return;
			}

			dm = (DiscreteMapping) oMap;

			// final Set<Object> attrSet =
			// loadKeys(oMap.getControllingAttributeName(), attr,
			// oMap, nOre);
			final Set<Object> attrSet = new TreeSet<Object>(attr.getColumnValues(oMap
			                                                                                                                        .getControllingAttributeName(),
			                                                                     attr.getColumnTypeMap()
			                                                                         .get(oMap
			                                                                                                                           .getControllingAttributeName())));

			/*
			 * Create random colors
			 */
			if (type.getDataType() == Color.class) {
				Object c;

				if (functionType == BRIGHTER) {
					for (Object key : attrSet) {
						c = dm.getMapValue(key);

						if ((c != null) && c instanceof Color) {
							valueMap.put(key, ((Color) c).brighter());
						}
					}
				} else if (functionType == DARKER) {
					for (Object key : attrSet) {
						c = dm.getMapValue(key);

						if ((c != null) && c instanceof Color) {
							valueMap.put(key, ((Color) c).darker());
						}
					}
				}
			}

			dm.putAll(valueMap);
			//vmm.setNetworkView(targetView);
			//Cytoscape.redrawGraph(targetView);

			propertySheetPanel.removeProperty(prop);

			final VizMapperProperty newRootProp = new VizMapperProperty();

			if (type.isNodeProp())
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(vmm.getVisualStyle().getNodeAppearanceCalculator()
				                                    .getCalculator(type), newRootProp,
				                                 VizMapperMainPanel.NODE_VISUAL_MAPPING, propertySheetPanel);
			else
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(vmm.getVisualStyle().getEdgeAppearanceCalculator()
				                                    .getCalculator(type), newRootProp,
				                                 VizMapperMainPanel.EDGE_VISUAL_MAPPING, propertySheetPanel);

			vizMapPropertySheetBuilder.removeProperty(prop);
			vizMapPropertySheetBuilder.getPropertyMap().get(vmm.getVisualStyle().getName()).add(newRootProp);

			vizMapPropertySheetBuilder.expandLastSelectedItem(type.getName());
		} else {
			System.out.println("Invalid.");
		}

		return;
	}
}
