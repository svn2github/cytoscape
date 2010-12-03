package org.cytoscape.view.vizmap.gui.internal.editor;

import java.util.Set;

import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;

/**
 * Editor object for all kinds of discrete values such as Node Shape, Line
 * Stroke, etc.
 * 
 * 
 * @param <T>
 */
public class DiscreteValuePropertyEditor<T> extends
		AbstractVisualPropertyEditor<T> {
	
	public DiscreteValuePropertyEditor(Class<T> type, Set<T> values) {
		super(type, new CyComboBoxPropertyEditor());
		
		discreteTableCellRenderer = REG.getRenderer(type);
		//continuousTableCellRenderer = new IconCellRenderer<T>(icons);
		
		CyComboBoxPropertyEditor cbe = (CyComboBoxPropertyEditor) propertyEditor;
		cbe.setAvailableValues(values.toArray());
		
	}
}
