package org.cytoscape.work.internal.tunables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cytoscape.cmdline.launcher.CommandLineProvider;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;

import cytoscape.CyNetworkManager;
import cytoscape.internal.task.TaskTunableAction;
import cytoscape.util.CyAction;


public class CLTaskFactoryInterceptor implements CLTaskFactory{
	
	private CommandLineProvider clp;
	private String[] args;

	Map<myFactory,CyAction> taskMap;
	TaskManager taskManager;
	TunableInterceptor interceptor;
	CyNetworkManager netManager;
	
	private Map<String,List<String>> mapShortArgs;
	private List<String> listValidedTasks;
	private Options taskOptions;
    private CommandLineParser parser = new PosixParser();
    private CommandLine line = null;
	
	CLTaskFactoryInterceptor(CommandLineProvider colipr,TaskManager taskManager,CyNetworkManager netManager){
		this.clp = colipr;
		this.taskManager = taskManager;
		//this.interceptor = interceptor;
		this.netManager = netManager;
		
		args = clp.getCommandLineArgs();
		taskMap = new HashMap<myFactory,CyAction>();
		createOptions(taskMap);
		findValidatedTasks(args);
		parseCommandLineArguments(listValidedTasks);
		executeCommandLineArguments(line,args,listValidedTasks,taskMap,mapShortArgs);
	}


	
	public void createOptions(Map<myFactory,CyAction> map){
		taskOptions = new Options();
		for ( myFactory tf : map.keySet() ){
			taskOptions.addOption( tf.getOption() );
		}
		taskOptions.addOption("lT", false, "Help = Display all the available taskFactories.");
		
		mapShortArgs = new HashMap<String,List<String>>();
		for(Option opt : taskOptions.getOptions())mapShortArgs.put("-"+opt.getOpt().toString(),new ArrayList<String>());
	}
	
	
	public void findValidatedTasks(String[] argus){
		listValidedTasks = new ArrayList<String>();
		int lastIdx = 0;
		String lastArg = null;
		
		for(String argsString : argus){
			if(mapShortArgs.containsKey(argsString)){
				lastArg = argsString;
				lastIdx=0;
				listValidedTasks.add(lastArg);
			}
			else {
				if(!argsString.startsWith("-")){
					mapShortArgs.get(lastArg).get(lastIdx).concat(" "+argsString);
					mapShortArgs.get(lastArg).set(lastIdx, mapShortArgs.get(lastArg).get(lastIdx).concat(" "+argsString));
					lastIdx++;
//					listValidedTasks.add(lastArg);
				}
				else if(lastArg==null){
					System.out.println("The Task \"" + argsString + "\" doesn't exist : Check the options");
					printHelp(taskOptions);
					System.exit(0);
				}
				else mapShortArgs.get(lastArg).add(argsString);
			}
		}
		//add the general help for all task
		for(String argsString : argus){
			if(argsString.equals("-lT")){
				mapShortArgs.get(argsString).add("-lT");
				listValidedTasks.add("-lT");
			}
		}
	}

	
	private void parseCommandLineArguments(List<String> listValTasks){
        String[] keys = new String[listValTasks.size()];
        int g=0;
        
        for(String key : listValTasks){
       			keys[g] = key;
       			g++;
       	}
        try{
			line = parser.parse(taskOptions,keys);
   		}catch(ParseException pe){
			System.err.println("Parsing command line failed: " + pe.getMessage());
			printHelp(taskOptions);
			System.exit(1);
		}
	}
	
	private void executeCommandLineArguments(CommandLine line, String[] argus, List<String>listValTasks, Map<myFactory,CyAction> map, Map<String,List<String>> mapArgs){
  		if (line.hasOption("lT")) {
        	System.out.println("The General Help has been called");
			printHelp(taskOptions);
			System.exit(0);	
        }

   		for(String st : listValTasks){
   			for(myFactory tf : map.keySet()){
   				if(st.equals(tf.getName())){
   					tf.checkFactory(line, mapArgs,listValTasks);
   				}
   			}
   		}
        if(argus.length==0){
        	printHelp(taskOptions);
			System.exit(0);
        }
	}
	
	
	
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		System.out.println("\n");
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [Options]","\nHere are the different taskFactories implemented :", options,"");
	}

	
	
	//TO add the Tasks
	public void addAction(CyAction action) {
		addAction( action );
	}
	public void removeAction(CyAction action) {
			removeAction(action);
	}
	public void addTaskFactory(TaskFactory factory, Map props) {
		System.out.println("addTaskFactory called");
		try {
			CyAction action = new TaskTunableAction(taskManager, null, factory, props, netManager);
			taskMap.put(new myFactory(factory),action);
			addAction( action );
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception constructing TaskTunableAction");
		}
	}
	public void removeTaskFactory(TaskFactory factory, Map props) {
		System.out.println("removeTaskFactory called");
		CyAction action = taskMap.remove(factory);
		if ( action != null ) removeAction(action);
	}	
	
	
	
	public class myFactory{	
		TaskFactory factory;
		String name;
		
		private myFactory(TaskFactory fact){
			this.factory = fact;
			this.name = fact.getTask().getClass().getSimpleName();
			System.out.println("Name of the task is :" + name);
		}
		
		
		TaskFactory getTaskFactory(){
			return factory;
		}
		
    	Option getOption(){
            return new Option(name.substring(0, 3),false,name);
    	}
    	
    	String getName(){ return "-"+name.substring(0, 3); }

    	
    	void checkFactory(CommandLine line,Map<String,List<String>> map, List<String> list){
        		
    		System.out.println("\n \n ########### factory loaded = "+name+" ###########");
        		String tFactoryName = new String("-"+name.substring(0, 3));
        		List<String> lst = new ArrayList<String>();	
        		
        		for(int i=0;i<map.get(tFactoryName).size();i++){
        			if(map.get(tFactoryName).get(i).contains(" ")){
            			int val = map.get(tFactoryName).get(i).indexOf(" ");        				
            			lst.add(map.get(tFactoryName).get(i).substring(0, val));
            			lst.add(map.get(tFactoryName).get(i).substring(val+1));
        			}
        			else{
        				lst.add(map.get(tFactoryName).get(i).toString());
        			}        			
        		}
        		String[] Args = new String[lst.size()]; 
        		for(int i=0;i<lst.size();i++)Args[i] = lst.get(i);        		
        		getFactory(Args);
    	}
    	
    	
		void getFactory(String[] argus){
    		interceptor = new CLTunableInterceptor(argus);
    		Task task = factory.getTask();
    		interceptor.loadTunables(task);
    		//	if ( task instanceof HandlerController )
    		//		((HandlerController)task).controlHandlers(interceptor.getHandlers(task));
    		
    		if ( !interceptor.createUI(task) )
    			return;	
    		
    		// execute the task in a separate thread
    		taskManager.execute(task);
		}
	}
}
