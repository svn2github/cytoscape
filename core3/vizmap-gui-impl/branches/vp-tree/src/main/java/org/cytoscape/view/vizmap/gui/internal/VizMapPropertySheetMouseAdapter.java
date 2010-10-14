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

package org.cytoscape.view.vizmap.gui.internal;

import static org.cytoscape.model.CyTableEntry.*;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditor;

import javax.swing.SwingUtilities;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.presentation.property.VisualPropertyUtil;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedEvent;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedListener;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

/**
 * Creates a new Mapping from GUI
 */
public final class VizMapPropertySheetMouseAdapter extends MouseAdapter
		implements SelectedVisualStyleSwitchedListener {
	
	private static final Logger logger = LoggerFactory.getLogger(VizMapPropertySheetMouseAdapter.class);
	
	private VizMapPropertySheetBuilder vizMapPropertySheetBuilder;
	private PropertySheetPanel propertySheetPanel;
	private EditorManager editorManager;

	private VisualStyle selectedStyle;
	
	private final CyComboBoxPropertyEditor mappingTypeEditor;
	
	private final PropertyEditor nodeAttributeEditor;
	private final PropertyEditor edgeAttributeEditor;
	private final PropertyEditor networkAttributeEditor;
	
	
	private static final String[] MAPPING_TYPE = {"Passthrough", "Discrete", "Continuous"};

	/**
	 * Creates a new VizMapPropertySheetMouseAdapter object.
	 * 
	 * @param sheetBuilder
	 *            DOCUMENT ME!
	 * @param propertySheetPanel
	 *            DOCUMENT ME!
	 * @param editorWindowManager
	 *            DOCUMENT ME!
	 */
	public VizMapPropertySheetMouseAdapter(
			VizMapPropertySheetBuilder sheetBuilder,
			PropertySheetPanel propertySheetPanel, VisualStyle selectedStyle, EditorManager editorManager) {

		this.vizMapPropertySheetBuilder = sheetBuilder;
		this.propertySheetPanel = propertySheetPanel;
		this.selectedStyle = selectedStyle;
		this.editorManager = editorManager;
		
		this.mappingTypeEditor = (CyComboBoxPropertyEditor) this.editorManager.getDefaultComboBoxEditor("mappingTypeEditor");
		this.mappingTypeEditor.setAvailableValues(MAPPING_TYPE);
		
		this.nodeAttributeEditor = editorManager.getDataTableComboBoxEditor(NODE);
		this.edgeAttributeEditor = editorManager.getDataTableComboBoxEditor(EDGE);
		this.networkAttributeEditor = editorManager.getDataTableComboBoxEditor(NETWORK);
		
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void mouseClicked(MouseEvent e) {
		
		logger.debug("====================> VizMapper GUI got mouse event: Click = " + e.getClickCount());
		
		
		int selected = propertySheetPanel.getTable().getSelectedRow();
		/*
		 * Adjust height if it's an legend icon.
		 */
		vizMapPropertySheetBuilder.updateTableView();

		if (SwingUtilities.isLeftMouseButton(e) && (0 <= selected)) {
			final Item item = (Item) propertySheetPanel.getTable().getValueAt(selected, 0);
			final Property curProp = item.getProperty();

			if (curProp == null)
				return;
			
			logger.debug("Got prop: " + curProp.getDisplayName());

			final String category = curProp.getCategory();

			if ((e.getClickCount() == 2) && (category != null)
					&& category.equalsIgnoreCase("Unused Properties")) {
				
				// Create new mapping from unused Visual Property.
				
				// FIXME
				((VizMapperProperty<?>) curProp).setEditable(true);

				final VisualProperty<?> vp = (VisualProperty<?>) ((VizMapperProperty<?>) curProp).getHiddenObject();
				propertySheetPanel.removeProperty(curProp);
				
				logger.debug("VP removed: " + vp.getDisplayName());

				final VizMapperProperty newProp = new VizMapperProperty();
				final VizMapperProperty mapProp = new VizMapperProperty();

				newProp.setDisplayName(vp.getDisplayName());
				newProp.setName(vp.getDisplayName());
				newProp.setHiddenObject(vp);
				newProp.setValue("Please select a value!");

				if (VisualPropertyUtil.isChildOf(TwoDVisualLexicon.NODE, vp, selectedStyle.getVisualLexicon())) {
					newProp.setCategory(TwoDVisualLexicon.NODE.getDisplayName());
					((PropertyEditorRegistry) propertySheetPanel.getTable().getEditorFactory()).registerEditor(newProp, nodeAttributeEditor);
					
					logger.debug("This is node prop: " + vp.getDisplayName());
				} else if (VisualPropertyUtil.isChildOf(TwoDVisualLexicon.EDGE, vp, selectedStyle.getVisualLexicon())){
					newProp.setCategory(TwoDVisualLexicon.EDGE.getDisplayName());
					((PropertyEditorRegistry) propertySheetPanel.getTable().getEditorFactory()).registerEditor(newProp, edgeAttributeEditor);
					logger.debug("This is edge prop: " + vp.getDisplayName());
				} else {
					// Network prop
					logger.debug("This is network prop: " + vp.getDisplayName());
				}

				mapProp.setDisplayName("Mapping Type");
				mapProp.setName("Mapping Type");
				
				mapProp.setValue("Please select a mapping type!");
				
				((PropertyEditorRegistry) propertySheetPanel.getTable().getEditorFactory()).registerEditor(mapProp, mappingTypeEditor);
				
				newProp.addSubProperty(mapProp);
				mapProp.setParentProperty(newProp);
				propertySheetPanel.addProperty(0, newProp);

				vizMapPropertySheetBuilder.expandLastSelectedItem(vp
						.getDisplayName());

				propertySheetPanel.getTable().scrollRectToVisible(new Rectangle(0, 0, 10, 10));
				
				propertySheetPanel.repaint();

				return;
				
			} else if ((e.getClickCount() == 1) && (category == null)) {
				/*
				 * Single left-click
				 */
				VisualProperty<?> type = null;

				if ((curProp.getParentProperty() == null)
						&& ((VizMapperProperty) curProp).getHiddenObject() instanceof VisualProperty)
					type = (VisualProperty) ((VizMapperProperty) curProp)
							.getHiddenObject();
				else if (curProp.getParentProperty() != null)
					type = (VisualProperty) ((VizMapperProperty) curProp
							.getParentProperty()).getHiddenObject();
				else
					return;

				final VisualMappingFunction<?, ?> selectedMapping = selectedStyle
						.getVisualMappingFunction(type);

				// TODO: move this function editor manager.
//				if (selectedMapping instanceof ContinuousMapping) {
//					/*
//					 * Need to check other windows.
//					 */
//					if (editorWindowManager.containsKey(type)) {
//						// This means editor is already on display.
//						editorWindowManager.get(type).requestFocus();
//
//						return;
//					} else {
//						try {
//							((JDialog) editorFactory.showContinuousEditor(
//									propertySheetPanel, type))
//									.addPropertyChangeListener(propertySheetPanel);
//						} catch (Exception e1) {
//							e1.printStackTrace();
//						}
//					}
//				}
			}
		}
	}

	public void handleEvent(SelectedVisualStyleSwitchedEvent e) {
		this.selectedStyle = e.getNewVisualStyle();
	}
}
