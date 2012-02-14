package org.cytoscape.app.internal;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class AppManagerInquireTaskFactory implements TaskFactory<AppManagerInquireTaskContext>{

	public TaskIterator createTaskIterator(AppManagerInquireTaskContext context) {
		return new TaskIterator(new AppManagerInquireTask(context));
	}
	
	@Override
	public AppManagerInquireTaskContext createTaskContext() {
		return new AppManagerInquireTaskContext();
	}
}
