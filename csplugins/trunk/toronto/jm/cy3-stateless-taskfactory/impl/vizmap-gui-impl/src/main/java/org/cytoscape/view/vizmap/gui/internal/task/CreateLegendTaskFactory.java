package org.cytoscape.view.vizmap.gui.internal.task;

import java.awt.Component;

import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CreateLegendTaskFactory extends AbstractTaskFactory {

	private final SelectedVisualStyleManager manager;
	private final Component parent;

	public CreateLegendTaskFactory(final SelectedVisualStyleManager manager,
			final Component parent) {
		this.manager = manager;
		this.parent = parent;
	}

	@Override
	public TaskIterator createTaskIterator(Object context) {
		return new TaskIterator(new CreateLegendTask(manager, parent));
	}

}
