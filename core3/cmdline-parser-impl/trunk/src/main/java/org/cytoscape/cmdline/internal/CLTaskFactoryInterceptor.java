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



/**
 * <p><pre>Interceptor of the <code>TaskFactories</code> : it will parse the arguments provided by the user through commandline
 *  to detect the <code>TaskFactories</code> and their own arguments
 *  </pre>
 *  </p>
 * 
 * @author pasteur
 *
 */
public class CLTaskFactoryInterceptor {
	
	/**
	 * Provider of the commandline arguments
	 */
	private CommandLineProvider clp;
	
	/**
	 * Map that contains the TFWrappers to get the available <code>TaskFactories</code>
	 */
    private Map<TaskFactory, TFWrapper> taskMap;
    
    /**
     * Arguments detected for each <code>TaskFactory</code>
     */
    private Map<String, List<String>> tasksWithTheirArgs;
    
    /**
     * commandline arguments
     */
    private String[] arguments;
    
    /**
     * <code>TaskFactories</code> that the user needs to execute
     */
    private List<String> choosenTasks;
    
    /**
     * The <code>Options</code> of the available <code>TaskFactories</code>
     */
    private Options optionsOfTasks;
    
    /**
     * Parser
     */
    private CommandLineParser parser = new PosixParser();
    
    /**
     * CommandLine : parsed arguments
     */
    private CommandLine line = null;
    
    /**
     * Grabber that contains the <code>Map</code> of available <code>TaskFactories</code>
     */
    private TaskFactoryGrabber grabber;
    
    /**
     * executor that will create a <code>SuperTask</code> to execute asynchronously the <code>Tasks</code>
     */
    private TaskExecutor executor;
    
    
    /**
     * <p><pre>
     * Interceptor does :
     * <ul>
     * <li> gets the arguments from the commandline</li>
     * <li> gets the <code>Map</code> of the available <code>TaskFactories</code></li>
     * <li> creates the options for each <code>TaskFactory</code></li>
     * <li> detects the arguments for each selected <code>TaskFactory</code> from the commandline's arguments</li>
     * <li> parses these arguments</li>
     * <li> executes the selected <code>TaskFactories</code> by using their <i>parsed arguments</i></li>
     * </ul>
     * </pre></p>
     * 
     * 
     * @param colipr provides the commandline arguments
     * @param tfg grabber that contains all the <code>TFWrappers</code>
     */
    public CLTaskFactoryInterceptor(CommandLineProvider colipr, TaskFactoryGrabber tfg) {
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
    
    

    /**
     * creates an Option for each <code>TaskFactory</code>
     */
    private void createTaskOptions() {
    	optionsOfTasks = new Options();

        // for each task factory, create an option
        for (TFWrapper tf : taskMap.values()) {
        	optionsOfTasks.addOption(tf.getOption());
        }

        //optionsOfTasks.addOption("listTasks", false, "Help = Display all the available taskFactories.");

        tasksWithTheirArgs = new HashMap<String, List<String>>();

        // for each task factory, create a list of strings
        for (Option opt : optionsOfTasks.getOptions())
        	//tasksWithTheirArgs.put("-" + opt.getOpt().toString(),new ArrayList<String>());
        	tasksWithTheirArgs.put("-" + opt.getOpt().toString(),new ArrayList<String>());
    }
    

    /**
     * Find the arguments that are related to <code>TaskFactories</code>
     */
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
/*  	System.out.println("tasksWithTheirArgs :");
        for(String st : tasksWithTheirArgs.keySet())System.out.println(st + " = " + tasksWithTheirArgs.get(st));
        System.out.println("\n\n");
        
        System.out.println("listOfChoosenTasks :");
        for(String st : choosenTasks)System.out.println(st);
        System.out.println("\n\n\n");
*/	        
        
        //add the general help for all task
        for (String arg : arguments) {
            if (arg.equals("-ListTasks")) {
            	tasksWithTheirArgs.get(arg).add("-ListTasks");
            	choosenTasks.add("-ListTasks");
            }
		}

    }

    
    /**
     * Parse the arguments that have been detected
     */
    private void parseTaskArguments() {
        try {
            line = parser.parse(optionsOfTasks,choosenTasks.toArray(new String[choosenTasks.size()]));
        } catch (ParseException pe) {
            System.err.println("Parsing command line failed: " + pe.getMessage());
            printHelp(optionsOfTasks,"");
            System.exit(1);
        }
    }

    
    /**
     * Executes the <code>TaskFactories</code> by using their arguments previously detected
     */
    private void executeCommandLineArguments() {
    	if (line.hasOption("ListTasks")) {
            System.out.println("The General Help has been called");
            printHelp(optionsOfTasks,"The General Help has been called");
        }

    	
        for (String st : choosenTasks) {
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

    /**
     * Display Help to the user when an error has been made in arguments
     * 
     * @param options all the options that are available
     * @param instructions message to inform the user about his error
     */
    private static void printHelp(Options options,String instructions) {
        HelpFormatter formatter = new HelpFormatter();
        System.out.println(instructions);
        formatter.setWidth(140);
        formatter.printHelp("java -Xmx512M -jar headless-cytoscape.jar [Options]",
            "\nHere are the different taskFactories implemented :\n", options, "\nTo run multiple tasks : \" java -jar headless-cytoscape.jar -<task> -<option(s)> -<argument(s)>  -<task> -<option(s)> -<argument(s)> \"");
    	System.exit(0);

    }
}
