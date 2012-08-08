package org.cytoscape.view.vizmap.gui.internal.editor.mappingeditor;

import java.awt.Color;
import java.awt.Paint;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

public class GradientEditor extends AbstractContinuousMappingEditor<Double, Color> {

	public GradientEditor(final CyNetworkTableManager manager, final CyApplicationManager appManager,
			final EditorManager editorManager, final VisualMappingManager vmm) {
		super(manager, appManager, editorManager, vmm);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof ContinuousMapping == false)
			throw new IllegalArgumentException("Value should be ContinuousMapping: this is " + value);
		final CyNetwork currentNetwork = appManager.getCurrentNetwork();
		if (currentNetwork == null)
			return;

		ContinuousMapping<?, ?> mTest = (ContinuousMapping<?, ?>) value;
		// TODO: error chekcing

		mapping = (ContinuousMapping<Double, Color>) value;
		@SuppressWarnings("unchecked")
		Class<? extends CyIdentifiable> type = (Class<? extends CyIdentifiable>) mapping.getVisualProperty()
				.getTargetDataType();
		
		final String colName = mapping.getMappingColumnName();
		final CyTable table;
		if(currentNetwork.getTable(type, CyNetwork.LOCAL_ATTRS).getColumn(colName)!= null)
			table = currentNetwork.getTable(type, CyNetwork.LOCAL_ATTRS);
		else
			table=currentNetwork.getTable(type, CyNetwork.SHARED_ATTRS);
		
		this.editorPanel = new GradientEditorPanel(vmm.getCurrentVisualStyle(), mapping, table, appManager,
				editorManager.getValueEditor(Paint.class), vmm);
	}
}
