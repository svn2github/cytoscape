package org.cytoscape.cmdline.internal;

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
import org.cytoscape.work.TaskFactory;


public class CLTaskFactoryInterceptor {
	
	CommandLineProvider clp;
    Map<TaskFactory, TFWrapper> taskMap;
    private Map<String, List<String>> tasksWithTheirArgs;
    private String[] arguments;
    private List<String> choosenTasks;
    private Options optionsOfTasks;
    private CommandLineParser parser = new PosixParser();
    private CommandLine line = null;
    private TaskFactoryGrabber grabber;
    private TaskExecutor executor;
    
    
    CLTaskFactoryInterceptor(CommandLineProvider colipr, TaskFactoryGrabber tfg) {
    	this.clp = colipr;
    	this.grabber = tfg;
    	taskMap = grabber.getTaskMap();
    	arguments = clp.getCommandLineCompleteArgs();
    	
    	//Executor collects all the tasks, create a SuperTask, to launch them asynchronously
    	executor = new TaskExecutor();
    	
    	//execute the methods
    	createTaskOptions();
        findTaskArguments();
        parseTaskArguments();
        executeCommandLineArguments();
        
        //Execute the SuperTask that has been created
        executor.execute();
    }
    
    

    private void createTaskOptions() {
    	optionsOfTasks = new Options();

        // for each task factory, create an option
        for (TFWrapper tf : taskMap.values()) {
        	optionsOfTasks.addOption(tf.getOption());
        }

        optionsOfTasks.addOption("listTasks", false, "Help = Display all the available taskFactories.");

        tasksWithTheirArgs = new HashMap<String, List<String>>();

        // for each task factory, create a list of strings
        for (Option opt : optionsOfTasks.getOptions())
        	tasksWithTheirArgs.put("-" + opt.getOpt().toString(),new ArrayList<String>());
    }
    

    public void findTaskArguments() {
    	choosenTasks = new ArrayList<String>();
        int lastIdx = 0;
        String actualTask = null;

        
        for (String arg : arguments) {	
			// if the arg represents task, add it to the task argument list
       		if (tasksWithTheirArgs.containsKey(arg)) {
       			actualTask = arg;
       			lastIdx = 0;
       			choosenTasks.add(actualTask);
       		}
            else if(tasksWithTheirArgs.containsKey("-"+arg)){
            	printHelp(optionsOfTasks,"\nAdd \" - \" to "+ arg + " or check the options below");
            }

            else if(actualTask == null){
    			printHelp(optionsOfTasks,"The Task \"" + arg + "\" doesn't exist : Check the options below");
    		}

            else { 	// otherwise add it to the list of task specific args
            	if (!arg.startsWith("-")) {
            		if(tasksWithTheirArgs.get(actualTask).size()!=0){
            			tasksWithTheirArgs.get(actualTask).set(lastIdx,tasksWithTheirArgs.get(actualTask).get(lastIdx).concat(" " + arg));
            			lastIdx++;
            		}
            	}
                else {
                	tasksWithTheirArgs.get(actualTask).add(arg);
                }
            }
        }

        executor.setNumberOfTasks(choosenTasks.size());
        
        
        
        //print the different parsed arguments
/*        System.out.println("tasksWithTheirArgs :");
        for(String st : tasksWithTheirArgs.keySet())
        	System.out.println(st + " = " + tasksWithTheirArgs.get(st));
        System.out.println("\n\n");
        
        System.out.println("listOfChoosenTasks :");
        for(String st : listOfChoosenTasks)
        	System.out.println(st);
        System.out.println("\n\n\n");
*/		
        
        
        
        
        //add the general help for all task
        for (String arg : arguments) {
            if (arg.equals("-listTasks")) {
            	tasksWithTheirArgs.get(arg).add("-listTasks");
            	choosenTasks.add("-listTasks");
            }
        }
    }

    private void parseTaskArguments() {
        try {
            line = parser.parse(optionsOfTasks,choosenTasks.toArray(new String[choosenTasks.size()]));
        } catch (ParseException pe) {
            System.err.println("Parsing command line failed: " + pe.getMessage());
            printHelp(optionsOfTasks,"");
            System.exit(1);
        }
    }

    private void executeCommandLineArguments() {
        if (line.hasOption("listTasks")) {
            System.out.println("The General Help has been called");
            printHelp(optionsOfTasks,"The General Help has been called");
        }

        for (String st : choosenTasks) {
        	System.out.println("Execution of " + st);
            for (TFWrapper tf : taskMap.values()) {
                if (st.equals(tf.getName())) {

                	String TFactoryName = tf.getName();
               		List<String> lst = new ArrayList<String>();
               		
                	if(tasksWithTheirArgs.get(TFactoryName).size()!=0){
                		
	               		for(int i=0;i<tasksWithTheirArgs.get(TFactoryName).size();i++) {
	               			if(tasksWithTheirArgs.get(TFactoryName).get(i).contains(" ")) {
	                   			int val = tasksWithTheirArgs.get(TFactoryName).get(i).indexOf(" ");
	                   			lst.add(tasksWithTheirArgs.get(TFactoryName).get(i).substring(0, val));
	                   			lst.add(tasksWithTheirArgs.get(TFactoryName).get(i).substring(val+1));
	               			}
	               			else{
	               				lst.add(tasksWithTheirArgs.get(TFactoryName).get(i).toString());
	               				//lst.add("-H");
	               			}
	               		}
                	}
                	else{
                		lst.add("-H");
                	}
                		
                		
              		//creation of arguments
               		String[] args = new String[lst.size()];
               		for(int i=0;i<lst.size();i++)args[i]=lst.get(i);
               		
               		clp.setSpecificArgs(args);
               		
               		//Executor intercepts each task and store it in a SuperTask
               		executor.intercept(tf.getT(),tf.getTI(),tf.getTM());
               	}
            }
        }
        
        
        if (arguments.length == 0) {
            printHelp(optionsOfTasks,"");
        }
    }

    private static void printHelp(Options options,String instructions) {
        HelpFormatter formatter = new HelpFormatter();
        System.out.println(instructions);
        formatter.printHelp("java -Xmx512M -jar headless-cytoscape.jar [Options]",
            "\nHere are the different taskFactories implemented :\n", options, "");
    	System.exit(0);

    }
}
