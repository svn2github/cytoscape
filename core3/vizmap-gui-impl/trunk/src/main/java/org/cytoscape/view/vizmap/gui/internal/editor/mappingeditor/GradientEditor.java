package org.cytoscape.view.vizmap.gui.internal.editor.mappingeditor;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;

public class GradientEditor extends AbstractPropertyEditor {
	
	
	
	private ContinuousMapping<Double, Color> mapping;
	
	
	public GradientEditor(final CyTableManager manager, final CyApplicationManager appManager, final SelectedVisualStyleManager selectedManager) {
	
		editor = new JPanel();
		this.editor.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				final CyTable attr = manager.getTableMap(mapping.getVisualProperty().getTargetDataType(), appManager.getCurrentNetwork()).get(CyNetwork.DEFAULT_ATTRS);
				GradientEditorPanel editorPanel = new GradientEditorPanel(selectedManager.getCurrentVisualStyle(), mapping, attr);
				JDialog editorDialog = new JDialog();
				editorDialog.add(editorPanel);
				editorDialog.pack();
				
				editorDialog.setTitle("Discrete Mapping Editor: Mapping for " + mapping.getVisualProperty().getDisplayName());
				editorDialog.setLocationRelativeTo(editor);
				editorDialog.setVisible(true);
			}
		});
	}
	
	
	@Override public Object getValue() {
		return mapping;
	}

	
	@Override public void setValue(Object value) {
		if(value instanceof ContinuousMapping == false)
			throw new IllegalArgumentException("Value should be ContinuousMapping: this is " + value);
		
		mapping = (ContinuousMapping<Double, Color>) value;
	}
	
}
