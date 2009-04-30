
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


import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.internal.VizMapperProperty;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;


/**
 *
 */
public class EditSelectedCellAction extends AbstractVizMapperAction {
	public EditSelectedCellAction() {
		super();
	}

	private static final long serialVersionUID = -6102797200439573667L;

	private EditorManager editorFactory;

	/**
	 * Edit all selected cells at once.
	 * This is for Discrete Mapping only.
	 */
	public void actionPerformed(ActionEvent e) {
		final PropertySheetTable table = propertySheetPanel.getTable();
		final int[] selected = table.getSelectedRows();

		Item item = null;

		// If nothing selected, return.
		if ((selected == null) || (selected.length == 0))
			return;

		/*
		 * Test with the first selected item
		 */
		item = (Item) propertySheetPanel.getTable()
		                                .getValueAt(selected[0], 0);

		VizMapperProperty prop = (VizMapperProperty) item.getProperty();

		if ((prop == null) || (prop.getParentProperty() == null)) {
			return;
		}

		final VisualProperty type = (VisualProperty) ((VizMapperProperty) prop
		                                                                                      .getParentProperty())
		                                .getHiddenObject();

		/*
		 * Extract calculator
		 */
		final VisualMappingFunction mapping;
		final CyDataTable attr;

		final CyNetwork targetNetwork = cyNetworkManager.getCurrentNetwork();
		
		if (type.getObjectType().equals(VisualProperty.NODE)) {
			mapping = vmm.getVisualStyle().getNodeAppearanceCalculator().getCalculator(type)
			             .getMapping(0);
			attr = targetNetwork.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		} else {
			mapping = vmm.getVisualStyle().getEdgeAppearanceCalculator().getCalculator(type)
			             .getMapping(0);
			attr = targetNetwork.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		}

		if (mapping instanceof ContinuousMapping || mapping instanceof PassthroughMapping)
			return;

		Object newValue = null;

		try {
			newValue = editorFactory.showDiscreteEditor(vizMapperMainPanel, type);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (newValue == null)
			return;

		Object key = null;
		final Class<?> keyClass = attr.getColumnTypeMap().get(mapping.getControllingAttributeName());

		for (int i = 0; i < selected.length; i++) {
			/*
			 * First, update property sheet
			 */
			((Item) propertySheetPanel.getTable().getValueAt(selected[i], 0)).getProperty()
			 .setValue(newValue);
			/*
			 * Then update backend.
			 */
			key = ((Item) propertySheetPanel.getTable().getValueAt(selected[i], 0)).getProperty()
			       .getDisplayName();

			if (keyClass == Integer.class) {
				key = Integer.valueOf((String) key);
			} else if (keyClass == Double.class) {
				key = Double.valueOf((String) key);
			} else if (keyClass == Boolean.class) {
				key = Boolean.valueOf((String) key);
			}

			((DiscreteMapping) mapping).putMapValue(key, newValue);
		}

		/*
		 * Update table and current network view.
		 */
		table.repaint();
		//vmm.setNetworkView(targetView);
		//Cytoscape.redrawGraph(targetView);
	}
}
