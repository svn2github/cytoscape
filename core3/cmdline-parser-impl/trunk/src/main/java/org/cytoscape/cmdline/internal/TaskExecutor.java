package org.cytoscape.cmdline.internal;

import org.cytoscape.work.SuperTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;


/**
 * To execute asynchronously the <code>Tasks</code>, we use a <code>SuperTask</code> made of <code>Tasks</code> that have been intercepted using a <code>TunableInterceptor</code>
 * 
 * @author pasteur
 *
 */
public class TaskExecutor{
	
	public	TaskExecutor(){};
	
	/**
	 * <code>Tasks</code> that will be executed
	 */
	private Task[] tasks;
	
	/**
	 * Manager to execute the <code>Tasks</code>
	 */
	private TaskManager tm;
	
	/**
	 * Number of <code>Tasks</code>
	 */
	private int numberTasks=0;
	
	/**
	 * to initialize the Array of <code>Tasks</code>
	 * @param val number of <code>Tasks</code> that will be executed
	 */
	public void setNumberOfTasks(int val){
		tasks = new Task[val];
	}
	
	/**
	 * Apply the <code>TunableInterceptor</code> on the <code>Tasks</code> and create an Array that contains those
	 * 
	 * @param tf <code>TaskFactory</code> selected
	 * @param ti interceptor applied on the <code>Task</code>
	 * @param tm execute the <code>Task</code>
	 */
	public void intercept(TaskFactory tf,TunableInterceptor ti, TaskManager tm){
		this.tm = tm;
		Task task = tf.getTask();
		ti.loadTunables(task);
   		if ( !ti.createUI(task) )
   			return;
   		tasks[numberTasks]=task;
   		numberTasks++;
	}
		
	/**
	 * Create a <code>SuperTask</code> of the selected <code>Tasks</code>, and execute them asynchronously
	 */
	public void execute(){
		SuperTask superTask = new SuperTask("SuperTask",tasks);
		tm.execute(superTask);
	}
	
}