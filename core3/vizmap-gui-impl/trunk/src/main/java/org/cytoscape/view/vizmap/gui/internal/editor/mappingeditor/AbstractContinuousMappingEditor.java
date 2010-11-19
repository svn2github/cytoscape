package org.cytoscape.view.vizmap.gui.internal.editor.mappingeditor;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;

public abstract class AbstractContinuousMappingEditor<K, V> extends AbstractPropertyEditor {
	
	protected ContinuousMapping<K, V> mapping;
	protected ContinuousMappingEditorPanel<K, V> editorPanel;
	
	protected final CyTableManager manager;
	protected final CyApplicationManager appManager;
	protected final SelectedVisualStyleManager selectedManager;
	protected final EditorManager editorManager;
	
	public AbstractContinuousMappingEditor(final CyTableManager manager, final CyApplicationManager appManager, 
			final SelectedVisualStyleManager selectedManager, final EditorManager editorManager) {
	
		this.manager = manager;
		this.appManager = appManager;
		this.selectedManager = selectedManager;
		this.editorManager = editorManager;
		
		editor = new JPanel();
		this.editor.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				final JDialog editorDialog = new JDialog();
				editorDialog.setLayout(new BorderLayout());
				editorDialog.add(editorPanel, BorderLayout.CENTER);
				editorDialog.pack();
				editorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				editorDialog.setTitle("Discrete Mapping Editor: Mapping for " + mapping.getVisualProperty().getDisplayName());
				editorDialog.setLocationRelativeTo(editor);
				editorDialog.setVisible(true);
			}
		});
		
		
	}
	
	
	@Override public Object getValue() {
		return mapping;
	}
	
}
