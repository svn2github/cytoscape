package org.cytoscape.view.vizmap.gui.internal.task;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.gui.internal.VizMapPropertySheetBuilder;
import org.cytoscape.view.vizmap.gui.internal.VizMapperMainPanel;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class RemoveVisualStyleTaskFactory implements TaskFactory {

	private final VisualMappingManager vmm;
	private final VizMapperMainPanel vizMapperMainPanel;
	private final CyNetworkManager cyNetworkManager;
	private final VizMapPropertySheetBuilder vizMapPropertySheetBuilder;

	public RemoveVisualStyleTaskFactory(final VisualMappingManager vmm,
			final VizMapperMainPanel vizMapperMainPanel,
			final CyNetworkManager cyNetworkManager,
			final VizMapPropertySheetBuilder vizMapPropertySheetBuilder) {
		this.cyNetworkManager = cyNetworkManager;
		this.vizMapperMainPanel = vizMapperMainPanel;
		this.vizMapPropertySheetBuilder = vizMapPropertySheetBuilder;
		this.vmm = vmm;

	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new RemoveVisualStyleTask(vmm,
				vizMapperMainPanel, cyNetworkManager,
				vizMapPropertySheetBuilder));
	}

}
