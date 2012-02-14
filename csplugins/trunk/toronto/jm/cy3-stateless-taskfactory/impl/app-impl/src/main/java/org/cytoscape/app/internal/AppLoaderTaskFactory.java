package org.cytoscape.app.internal;


import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class AppLoaderTaskFactory implements TaskFactory<AppLoaderTaskContext> {
	
	private CyAppAdapter adapter;

	public AppLoaderTaskFactory(CyAppAdapter adapter) {
		this.adapter = adapter;
	}

	public TaskIterator createTaskIterator(AppLoaderTaskContext context) {
		return new TaskIterator(new AppLoaderTask(context, adapter));
	}
	
	@Override
	public AppLoaderTaskContext createTaskContext() {
		return new AppLoaderTaskContext();
	}
}
