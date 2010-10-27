package org.cytoscape.view.vizmap.gui.internal.task;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.gui.internal.VizMapPropertySheetBuilder;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class RemoveVisualStyleTaskFactory implements TaskFactory {
	private final VisualMappingManager vmm;
	private final CyApplicationManager applicationManager;
	private final VizMapPropertySheetBuilder vizMapPropertySheetBuilder;
	
	private final SelectedVisualStyleManager manager;

	public RemoveVisualStyleTaskFactory(final VisualMappingManager vmm,
			final SelectedVisualStyleManager manager,
			final CyApplicationManager applicationManager,
			final VizMapPropertySheetBuilder vizMapPropertySheetBuilder) {
		this.applicationManager = applicationManager;
		this.vizMapPropertySheetBuilder = vizMapPropertySheetBuilder;
		this.vmm = vmm;
		this.manager = manager;

	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new RemoveVisualStyleTask(vmm,
				manager, applicationManager,
				vizMapPropertySheetBuilder));
	}

}
