package org.cytoscape.app.internal;


import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class AppLoaderTaskFactory2 implements TaskFactory<AppLoaderTask2Context> {
	
	private CyAppAdapter adapter;

	AppLoaderTaskFactory2(CyAppAdapter adapter) {
		this.adapter = adapter;
	}

	public TaskIterator createTaskIterator(AppLoaderTask2Context context) {
		return new TaskIterator(new AppLoaderTask2(context, adapter));
	}
	
	@Override
	public AppLoaderTask2Context createTaskContext() {
		return new AppLoaderTask2Context();
	}
}
