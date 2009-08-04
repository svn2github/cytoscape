package org.cytoscape.cmdline.internal;

import org.cytoscape.work.SuperTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;

public class TaskExecutor{
	
	public	TaskExecutor(){};
	
	Task[] tasks;
	TaskManager tm;
	int i=0;
	
	public void setNumberOfTasks(int val){
		tasks = new Task[val];
	}
	
	
	public void intercept(TaskFactory tf,TunableInterceptor ti, TaskManager tm){
		this.tm = tm;
		Task task = tf.getTask();
		ti.loadTunables(task);
   		if ( !ti.createUI(task) )
   			return;
   		tasks[i]=task;
   		i++;
	}
		
	public void execute(){
		SuperTask superTask = new SuperTask("SuperTask",tasks);
		tm.execute(superTask);
	}
	
}