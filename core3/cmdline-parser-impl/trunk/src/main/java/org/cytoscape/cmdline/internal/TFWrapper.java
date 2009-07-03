package org.cytoscape.cmdline.internal;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;

class TFWrapper {	
	TaskFactory factory;
	TaskManager taskManager;
	TunableInterceptor ti;
	String name;
	
	TFWrapper(TaskFactory fact,TaskManager taskManager, TunableInterceptor ti) {
		this.factory = fact;
		this.taskManager = taskManager;
		this.ti = ti;
		this.name = fact.getTask().getClass().getSimpleName();
		System.out.println("Name of the task is :" + name);
	}
	
   	Option getOption() {
           return new Option(name.substring(0,3),false,name);
   	}
   	
   	String getName() { 
		return "-"+name.substring(0, 3); 
	}
   	
   	void checkFactory(CommandLine line,Map<String,List<String>> map, List<String> list) {
       		
   		System.out.println("\n \n ########### factory loaded = "+name+" ###########");
       		String tFactoryName = getName();
       		List<String> lst = new ArrayList<String>();	
       		
       		for(int i=0;i<map.get(tFactoryName).size();i++) {
       			if(map.get(tFactoryName).get(i).contains(" ")) {
           			int val = map.get(tFactoryName).get(i).indexOf(" ");
           			lst.add(map.get(tFactoryName).get(i).substring(0, val));
           			lst.add(map.get(tFactoryName).get(i).substring(val+1));
       			}
       			else{
       				lst.add(map.get(tFactoryName).get(i).toString());
       			}
       		}
       		executeTask(list.toArray(new String[lst.size()]));
   	}
    	
    	
	private void executeTask(String[] argus) {
   		Task task = factory.getTask();
   		ti.loadTunables(task);
   		if ( !ti.createUI(task) )
   			return;	
   		taskManager.execute(task);
	}
}
