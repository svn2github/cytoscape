package org.cytoscape.vizmap.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorFactory;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

public class EditorWindowManager {

	private Map<VisualPropertyType, JDialog> editorWindowMap;

	private PropertyRendererRegistry rendReg;
	private EditorFactory editorFactory;
	private PropertySheetPanel propertySheetPanel;

	public EditorWindowManager(PropertyRendererRegistry rendReg,
			EditorFactory editorFactory, PropertySheetPanel propertySheetPanel) {
		this.editorFactory = editorFactory;
		this.rendReg = rendReg;
		this.propertySheetPanel = propertySheetPanel;

		editorWindowMap = new HashMap<VisualPropertyType, JDialog>();
	}

	public void manageWindow(final String status, VisualPropertyType vpt,
			Object source) {
		if (status.equals(EditorFactory.EDITOR_WINDOW_OPENED)) {
			this.editorWindowMap.put(vpt, (JDialog) source);
		} else if (status.equals(EditorFactory.EDITOR_WINDOW_CLOSED)) {
			final VisualPropertyType type = vpt;

			/*
			 * Update icon
			 */
			final Property[] props = propertySheetPanel.getProperties();
			VizMapperProperty vprop = null;

			for (Property prop : props) {
				vprop = (VizMapperProperty) prop;

				if ((vprop.getHiddenObject() != null)
						&& (type == vprop.getHiddenObject())) {
					vprop = (VizMapperProperty) prop;

					break;
				}
			}

			final Property[] subProps = vprop.getSubProperties();
			vprop = null;

			String name = null;

			for (Property prop : subProps) {
				name = prop.getName();

				if ((name != null) && name.equals(type.getName())) {
					vprop = (VizMapperProperty) prop;

					break;
				}
			}

			final int width = propertySheetPanel.getTable().getCellRect(0, 1,
					true).width;

			final TableCellRenderer cRenderer = editorFactory
					.getContinuousCellRenderer(type, width, 70);
			rendReg.registerRenderer(vprop, cRenderer);
			propertySheetPanel.getTable().repaint();
		}
	}

	public void closeAllEditorWindows() {
		Set<VisualPropertyType> typeSet = editorWindowMap.keySet();
		Set<VisualPropertyType> keySet = new HashSet<VisualPropertyType>();

		for (VisualPropertyType vpt : typeSet) {
			JDialog window = editorWindowMap.get(vpt);
			manageWindow(EditorFactory.EDITOR_WINDOW_CLOSED, vpt, null);
			window.dispose();
			keySet.add(vpt);
		}

		for (VisualPropertyType type : keySet)
			editorWindowMap.remove(type);
	}

	public void removeEditorWindow(VisualPropertyType type) {
		JDialog editor = editorWindowMap.get(type);
		if (editor == null)
			return;

		editor.dispose();
		editorWindowMap.remove(type);
	}

	public boolean isRegistered(VisualPropertyType type) {
		if (editorWindowMap.get(type) != null)
			return true;
		else
			return false;
	}

}
