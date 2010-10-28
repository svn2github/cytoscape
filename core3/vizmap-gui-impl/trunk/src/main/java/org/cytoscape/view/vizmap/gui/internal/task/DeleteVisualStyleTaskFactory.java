package org.cytoscape.view.vizmap.gui.internal.task;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class DeleteVisualStyleTaskFactory implements TaskFactory {

	private final CyEventHelper eventHelper;
	private final SelectedVisualStyleManager manager;

	public DeleteVisualStyleTaskFactory(final CyEventHelper eventHelper,
			final SelectedVisualStyleManager manager) {
		this.manager = manager;
		this.eventHelper = eventHelper;

	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new DeleteVisualStyleTask(eventHelper, manager));
	}

}
