package org.cytoscape.cmdline.internal;

import org.apache.commons.cli.Option;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;


/**
 * A container of a TaskFactory used to provide Option, Name and tools to intercept its Task
 * 
 * @author pasteur
 *
 */
public class TFWrapper {
	
	/**
	 * Cytoscape action provided through a <code>Task</code>
	 */
	TaskFactory factory;
	
	/**
	 * Manager to execute the <code>Tasks</code>
	 */
	TaskManager taskManager;
	
	/**
	 * Interceptor of <code>Tunables</code> in <code>TaskFactories</code>' classes
	 */
	TunableInterceptor ti;
	
	/**
	 * Name of the <code>TaskFactory</code>
	 */
	String name;
	
	
	/**
	 * The wrapper of a TaskFactory that is available for headless mode
	 * 
	 * @param fact <code>TaskFactory</code> that is managed by this wrapper
	 * @param taskManager used to execute the <code>Task</code> of <code>fact</code>
	 * @param ti used to intercept the <code>Tunables</code> contained in the <code>fact</code>
	 */
	TFWrapper(TaskFactory fact,TaskManager taskManager, TunableInterceptor ti) {
		this.factory = fact;
		this.taskManager = taskManager;
		this.ti = ti;
		this.name = fact.getTask().getClass().getSimpleName();
	}
	
	/**
	 * Provides the Option to identify it through <i>commandline</i>
	 * 
	 * @return the Option
	 */
   	public Option getOption() {
           return new Option(name,false,name);
   	}
   	
   	
   	/**
   	 * To get the name of the <code>TaskFactory</code>
   	 * @return the name of the <code>TaskFactory</code>
   	 */
   	public String getName() { 
		return "-"+name;
	}
   	
   	/**
   	 * To get the <code>TunableInterceptor</code> that will be used on <code>Task's Tunables</code>
   	 * @return the Interceptor
   	 */
   	public TunableInterceptor getTI(){
   		return ti;
   	}
   	
   	
   	/**
   	 * To get the <code>TaskManager</code> that will be used to execute <code>Task</code>
   	 * @return the <code>TaskManager</code>
   	 */
   	public TaskManager getTM(){
   		return taskManager;
   	}
   	
   	
   	/**
   	 * To get the <code>TaskFactory</code> that has been wrapped in
   	 * @return <code>TaskFactory</code>
   	 */
   	public TaskFactory getT(){
   		return factory;
   	}
   	
//	public void executeTask() {
//   		Task task = factory.getTask();
//   		ti.loadTunables(task);
//   		if ( !ti.createUI(task) )
//   			return;
//   		taskManager.execute(task);
//	}
}
