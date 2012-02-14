package org.cytoscape.internal.layout.ui;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.layout.LayoutContext;
import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class UndoSupportTaskFactory extends AbstractLayoutAlgorithm<LayoutContext> {
	
	private AbstractLayoutAlgorithm<LayoutContext> delegate;
	private UndoSupport undo;
	private CyEventHelper eventHelper;
	private String name;

	public UndoSupportTaskFactory(AbstractLayoutAlgorithm<LayoutContext> delegate, UndoSupport undo, CyEventHelper eventHelper) {
		super(undo, delegate.getName(), delegate.toString(), delegate.supportsSelectedOnly());
		this.name = delegate.toString();
		this.undo = undo;
		this.delegate = delegate;
		this.eventHelper = eventHelper;
	}
	
	@Override
	public LayoutContext createTaskContext() {
		return delegate.createTaskContext();
	}
	
	@Override
	public TaskIterator createTaskIterator(LayoutContext context) {
		TaskIterator source = delegate.createTaskIterator(context);
		Task[] tasks = new Task[source.getNumTasks() + 1];
		tasks[0] = new UndoSupportTask(name, undo, eventHelper, context.getNetworkView());
		for (int i = 1; i < tasks.length; i++) {
			tasks[i] = source.next();
		}
		return new TaskIterator(tasks.length, tasks);
	}
}
