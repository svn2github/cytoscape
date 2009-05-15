package org.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.example.tunable.*;
import org.example.tunable.internal.props.*;
import org.example.tunable.internal.cl.*;
import org.example.command.*;

import java.util.*;


public class AppCL2
{
    public static void main(String[] args) {

		List<taskFactory> list = new ArrayList<taskFactory>();
		list.add(new taskFactory(new TunableSampler(), "This is a TaskFactory example for TunableSampler\n youpitralala" ));
		list.add(new taskFactory(new PrintSomething(), "This is a TaskFactory example for PrintSomething\n same thing here"));
		list.add(new taskFactory(new JActiveModules(), "This is a TaskFactory example for JActiveModules\n different informations about the task" ));
		
		Options options = new Options();
		for ( taskFactory tf : list ){
			options.addOption( tf.getOption() );
		}
		options.addOption("lT", "listTask", false, "Display all the available taskFactories.");
		
		
		Map<String,List<String>> mapShortArgs = new HashMap<String,List<String>>();
		for(Option opt :options.getOptions())mapShortArgs.put("-"+opt.getOpt().toString(),new ArrayList<String>());
		

		List<String> listValidedTasks = new ArrayList<String>();
		
		int lastIdx = 0;
		String lastArg = null;
		
		for(String argsString : args){
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
					printHelp(options);
					System.exit(0);
				}
				else mapShortArgs.get(lastArg).add(argsString);
			}
		}

		
		for(String argsString : args){
			if(argsString.equals("-lT")){
				mapShortArgs.get(argsString).add("-lT");
				listValidedTasks.add("-lT");
			}
		}
		
		
		
//		System.out.println("Map Short Args :");
//		for(String st : mapShortArgs.keySet())
//			System.out.println(st+" = " + mapShortArgs.get(st));
//		System.out.println("\n\n");
//		
//		System.out.println("List Valided Tasks :");
//		for(String st : listValidedTasks)
//			System.out.println(st);
//		System.out.println("\n\n\n");
		
		
		
		
		
        String[] keys = new String[listValidedTasks.size()];
        int g=0;
        
        for(String key : listValidedTasks){
       			keys[g] = key;
       			g++;
        }
                
		
        CommandLineParser parser = new PosixParser();
        CommandLine line = null;
        
        
        try{
			line = parser.parse(options,keys);
   		}catch(ParseException pe){
			System.err.println("Parsing command line failed: " + pe.getMessage());
			printHelp(options);
			System.exit(1);
		}

   		if (line.hasOption("lT")) {
        	System.out.println("The General Help has been called");
			printHelp(options);
			System.exit(0);
			
        }
   		
   		for(String st : listValidedTasks){
   			for(taskFactory tf : list){
   				if(st.equals(tf.getName())){
   					tf.checkFactory(line, mapShortArgs,listValidedTasks);
   				}
   			}
   		}
        
        if(args.length==0){
        	printHelp(options);
			System.exit(0);
        }
    }
    
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		System.out.println("\n");
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [Options]","\nHere are the different taskFactories implemented :", options,"");
	}

	
	
    static private class taskFactory{
    	Command com;
    	String desc;
    	String name;
    	
    	private taskFactory(Command com,String desc){
    		this.com=com;
    		this.desc=desc;
    		this.name=com.getClass().getSimpleName();
    	}
    	
    	Option getOption(){
            return new Option(name.substring(0, 3),name,false,desc);
    	}
    	
    	String getName(){
    		return "-"+name.substring(0, 3);
    	}
    	
    	void checkFactory(CommandLine line,Map<String,List<String>> map, List<String> list){
//    		if(line.hasOption(name.substring(0, 3))){
        		System.out.println("########### factory loaded = "+name+" ###########");
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
        		getFactory(com,Args);
//    		}
    	}
    	
    	
    	void getFactory(Command com,String[] args){
    		TunableInterceptor cl = new CLTunableInterceptor(args);
    		cl.loadTunables(com);
//    		if ( com instanceof HandlerController )
//    			((HandlerController)com).controlHandlers(cl.getHandlers(com));
    		cl.createUI(com);
    		System.out.println("\n"+"result of command execution:");
    		com.execute();
    		System.out.println();

    		
    		Properties p = new Properties();
    		p.setProperty("printSomething.firstName","marge");
    		TunableInterceptor lp = new LoadPropsInterceptor(p);
    		lp.loadTunables(com);
//    		if ( com instanceof HandlerController )
//    			((HandlerController)com).controlHandlers(lp.getHandlers(com));
    		lp.createUI(com);
    		System.out.println("result of command execution after properties have been loaded:");
    		com.execute();
    		System.out.println();
    		
    		
    		Properties store = new Properties();
    		TunableInterceptor sp = new StorePropsInterceptor(store);
    		sp.loadTunables(com);
//    		if ( com instanceof HandlerController )
//    			((HandlerController)com).controlHandlers(sp.getHandlers(com));
    		sp.createUI(com);
    		System.out.println("result of storing properties interceptor:\n"+store.toString()+"\n");
    	}
    }
}
