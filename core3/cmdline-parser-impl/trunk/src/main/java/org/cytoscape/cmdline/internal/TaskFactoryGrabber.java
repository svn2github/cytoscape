package org.cytoscape.cmdline.internal;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;

public class TaskFactoryGrabber {
	
	private Map<TaskFactory,TFWrapper> taskMap;
	private TaskManager tm;
	private TunableInterceptor ti;
	
	public TaskFactoryGrabber(TaskManager tm, TunableInterceptor ti) throws InterruptedException {
		System.out.println("Grabber Called");
		this.tm = tm;
		this.ti = ti;
		taskMap = new HashMap<TaskFactory,TFWrapper>();
	}

	synchronized public void addTaskFactory(TaskFactory factory, Map props) {
		System.out.println("addTaskFactory called");
		taskMap.put(factory, new TFWrapper(factory,tm,ti));
	}

	synchronized public void removeTaskFactory(TaskFactory factory, Map props) {
		System.out.println("removeTaskFactory called");
		taskMap.remove(factory);
	}	

	synchronized public Map<TaskFactory,TFWrapper> getTaskMap() {
		return new HashMap<TaskFactory,TFWrapper>(taskMap);
	}
	
}
