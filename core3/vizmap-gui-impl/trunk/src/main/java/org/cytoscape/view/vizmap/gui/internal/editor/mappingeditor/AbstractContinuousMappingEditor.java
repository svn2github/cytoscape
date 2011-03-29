package org.cytoscape.view.vizmap.gui.internal.editor.mappingeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;

public abstract class AbstractContinuousMappingEditor<K extends Number, V> extends AbstractPropertyEditor {
	
	private static final Dimension DEF_SIZE = new Dimension(650, 400);
	private static final Dimension MIN_SIZE = new Dimension(400, 200);
	
	protected ContinuousMapping<K, V> mapping;
	protected ContinuousMappingEditorPanel<K, V> editorPanel;
	
	protected final CyTableManager manager;
	protected final CyApplicationManager appManager;
	protected final SelectedVisualStyleManager selectedManager;
	protected final EditorManager editorManager;
	
	protected final VisualMappingManager vmm;
	
	public AbstractContinuousMappingEditor(final CyTableManager manager, final CyApplicationManager appManager, 
			final SelectedVisualStyleManager selectedManager, final EditorManager editorManager, final VisualMappingManager vmm) {
	
		this.vmm = vmm;
		this.manager = manager;
		this.appManager = appManager;
		this.selectedManager = selectedManager;
		this.editorManager = editorManager;
		
		editor = new JPanel();
		this.editor.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent ev) {
				final JDialog editorDialog = new JDialog();
				initComponents(editorDialog);
				
				editorDialog.setTitle("Continuous Mapping Editor: Mapping for " + mapping.getVisualProperty().getDisplayName());
				editorDialog.setLocationRelativeTo(editor);
				editorDialog.setVisible(true);
			}
			
			private void initComponents(final JDialog dialog) {

				editorPanel.setBackground(Color.red);
				dialog.setLayout(new BorderLayout());
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.getContentPane().add(editorPanel, BorderLayout.CENTER);

				dialog.setPreferredSize(DEF_SIZE);
				dialog.setMinimumSize(MIN_SIZE);
								
		        dialog.pack();
		    }
		});
		
		
	}
	
	
	@Override public Object getValue() {
		return mapping;
	}
	
}
