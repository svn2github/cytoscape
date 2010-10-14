package org.cytoscape.view.vizmap.gui.internal.task;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.gui.internal.VizMapPropertySheetBuilder;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class RemoveVisualStyleTaskFactory implements TaskFactory {

	private final VisualMappingManager vmm;
	private final CyNetworkManager cyNetworkManager;
	private final VizMapPropertySheetBuilder vizMapPropertySheetBuilder;
	
	private final SelectedVisualStyleManager manager;

	public RemoveVisualStyleTaskFactory(final VisualMappingManager vmm,
			final SelectedVisualStyleManager manager,
			final CyNetworkManager cyNetworkManager,
			final VizMapPropertySheetBuilder vizMapPropertySheetBuilder) {
		this.cyNetworkManager = cyNetworkManager;
		this.vizMapPropertySheetBuilder = vizMapPropertySheetBuilder;
		this.vmm = vmm;
		this.manager = manager;

	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new RemoveVisualStyleTask(vmm,
				manager, cyNetworkManager,
				vizMapPropertySheetBuilder));
	}

}
