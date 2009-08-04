package org.cytoscape.cmdline.internal;

import org.apache.commons.cli.Option;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;

public class TFWrapper {	
	TaskFactory factory;
	TaskManager taskManager;
	TunableInterceptor ti;
	String name;
	
	TFWrapper(TaskFactory fact,TaskManager taskManager, TunableInterceptor ti) {
		this.factory = fact;
		this.taskManager = taskManager;
		this.ti = ti;
		this.name = fact.getTask().getClass().getSimpleName();
	}
	
   	public Option getOption() {
           return new Option(name,false,name);
   	}
   	
   	public String getName() { 
		return "-"+name;
	}
   	
   	public TunableInterceptor getTI(){
   		return ti;
   	}
   	
   	public TaskManager getTM(){
   		return taskManager;
   	}
   	
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
