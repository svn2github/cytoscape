package org.cytoscape.cmdline.internal;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;



/**
 * It will catch all the TaskFactories that are implementing <code>TaskFactory</code> interface and store them for further interception and execution
 * 
 * @author pasteur
 *
 */
public class TaskFactoryGrabber {
	
	/**
	 * the <code>Map</code> of <code>TFWrappers</code>
	 */
	private Map<TaskFactory,TFWrapper> taskMap;
	
	/**
	 * Manager to execute the <code>Tasks</code>
	 */
	private TaskManager tm;
	
	/**
	 * Interceptor of <code>Tunables</code> in <code>TaskFactories</code>' classes
	 */
	private TunableInterceptor ti;
	
	/**
	 * The grabber that stores all the <code>TaskFactories</code>
	 * 
	 * @param tm used to execute the <code>Task</code> of the <code>TaskFactory</code> contained in the <code>TFWrapper</code>
	 * @param ti used to intercept the <code>Tunables</code> of the <code>TaskFactory</code> contained in the <code>TFWrapper</code>
	 * @throws InterruptedException
	 */
	public TaskFactoryGrabber(TaskManager tm, TunableInterceptor ti) throws InterruptedException {
		this.tm = tm;
		this.ti = ti;
		taskMap = new HashMap<TaskFactory,TFWrapper>();
	}

	/**
	 * Executed when <code>TaskFactory</code> interface is detected during the loading of all Cytoscape's packages : add the <code>TFWrapper</code> to the <code>Map</code> that stores them
	 * @param factory which has been detected
	 * @param props
	 */
	synchronized public void addTaskFactory(TaskFactory factory, Map props) {
		taskMap.put(factory, new TFWrapper(factory,tm,ti));
	}

	/**
	 * Removes a <code>TaskFactory</code> from the <code>Map</code> that stores them
	 * @param factory which has to be removed
	 * @param props
	 */
	synchronized public void removeTaskFactory(TaskFactory factory, Map props) {
		taskMap.remove(factory);
	}	

	/**
	 * Provides the <code>Map</code> with all the <code>TFWrappers</code> added
	 * @return <code>Map</code> with the <code>TFWrappers</code>
	 */
	synchronized public Map<TaskFactory,TFWrapper> getTaskMap() {
		return new HashMap<TaskFactory,TFWrapper>(taskMap);
	}
	
}
