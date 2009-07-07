package org.cytoscape.cmdline.internal;

import java.util.List;
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
           return new Option(name,false,name);
   	}
   	
   	String getName() { 
		return "-"+name;
	}
   	
   	void checkFactory(CommandLine line,Map<String,List<String>> map, List<String> list) {	
//   		System.out.println("\n \n ########### factory loaded = "+name+" ###########");
//       		String tFactoryName = getName();
//       		List<String> lst = new ArrayList<String>();	
//       		
//       		for(int i=0;i<map.get(tFactoryName).size();i++) {
//       			if(map.get(tFactoryName).get(i).contains(" ")) {
//           			int val = map.get(tFactoryName).get(i).indexOf(" ");
//           			lst.add(map.get(tFactoryName).get(i).substring(0, val));
//           			lst.add(map.get(tFactoryName).get(i).substring(val+1));
//       			}
//       			else{
//       				lst.add(map.get(tFactoryName).get(i).toString());
//       			}
//       		}
//
//       		//creation of arguments
//       		String[] args = new String[lst.size()];
//       		for(int i=0;i<lst.size();i++)args[i]=lst.get(i);
//       		
//       		
//       		System.out.println("#####arguments used are : ");
//       		for(int i=0;i<args.length;i++)System.out.println(args[i]+" ");
//       		System.out.println("\n\n");
//       		
//       		executeTask(list.toArray(new String[lst.size()]));
   	}
    	
	public void executeTask() {
   		Task task = factory.getTask();
   		ti.loadTunables(task);
   		if ( !ti.createUI(task) )
   			return;
   		taskManager.execute(task);
	}
}
