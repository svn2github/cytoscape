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
    private String[] args;
    private List<String> listOfChoosenTasks;
    private Options optionsOfTasks;
    private CommandLineParser parser = new PosixParser();
    private CommandLine line = null;
    private TaskFactoryGrabber grabber;
    //private long time = 0;
    
    CLTaskFactoryInterceptor(CommandLineProvider colipr, TaskFactoryGrabber tfg) {
    	this.clp = colipr;
    	this.grabber = tfg;

    	
    	/*    
		int stable = 0;
		int numTasks = 0;
    	while(stable < 100000) {
			int tmpTasks = grabber.getNumberTasks();
			if (numTasks == tmpTasks) {
				stable++;
			} else {
				numTasks = tmpTasks;
				stable = 0;
			}
		}
		*/

    	taskMap = grabber.getTaskMap();
    	args = clp.getCommandLineCompleteArgs();

    	createTaskOptions();
        findTaskArguments();
        parseTaskArguments();
        executeCommandLineArguments();
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
    	listOfChoosenTasks = new ArrayList<String>();

        int lastIdx = 0;
        String lastArg = null;

        // iterate over args
        for (String argsString : args) {
        	
			// if the arg represents task, add it to the task argument list
            if (tasksWithTheirArgs.containsKey(argsString)) {
                lastArg = argsString;
                lastIdx = 0;
                listOfChoosenTasks.add(lastArg);
                
            }
            else if(tasksWithTheirArgs.containsKey("-"+argsString)){
            	printHelp(optionsOfTasks,"\nAdd \" - \" to "+ argsString + " or check the options below");
            }
            	
            else { // otherwise add it to the list of task specific args
            	if (!argsString.startsWith("-")) {
            		if(lastArg == null){
            			printHelp(optionsOfTasks,"The Task \"" + argsString + "\" doesn't exist : Check the options below");
            		}
            		if(tasksWithTheirArgs.get(lastArg).size()!=0){
            			//tasksWithTheirArgs.get(lastArg).get(lastIdx).concat(" " + argsString);
            			tasksWithTheirArgs.get(lastArg).set(lastIdx,tasksWithTheirArgs.get(lastArg).get(lastIdx).concat(" " + argsString));
            			lastIdx++;
            		}
            	}
            	else if (lastArg == null) {
                    printHelp(optionsOfTasks,"The Task \"" + argsString + "\" doesn't exist : Check the options below");
                } else {
                	tasksWithTheirArgs.get(lastArg).add(argsString);
                }
            }
        }

        
        
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
        for (String argsString : args) {
            if (argsString.equals("-listTasks")) {
            	tasksWithTheirArgs.get(argsString).add("-listTasks");
            	listOfChoosenTasks.add("-listTasks");
            }
        }
    }

    private void parseTaskArguments() {
        try {
            line = parser.parse(optionsOfTasks,listOfChoosenTasks.toArray(new String[listOfChoosenTasks.size()]));
        } catch (ParseException pe) {
            System.err.println("Parsing command line failed: " +pe.getMessage());
            printHelp(optionsOfTasks,"");
            System.exit(1);
        }
    }

    private void executeCommandLineArguments() {
        if (line.hasOption("listTasks")) {
            System.out.println("The General Help has been called");
            printHelp(optionsOfTasks,"The General Help has been called");
        }

        for (String st : listOfChoosenTasks) {
            for (TFWrapper tf : taskMap.values()) {
                if (st.equals(tf.getName())) {

                	String tFactoryName = tf.getName();
               		List<String> lst = new ArrayList<String>();
               		
                	if(tasksWithTheirArgs.get(tFactoryName).size()!=0){
                		
	               		for(int i=0;i<tasksWithTheirArgs.get(tFactoryName).size();i++) {
	               			if(tasksWithTheirArgs.get(tFactoryName).get(i).contains(" ")) {
	                   			int val = tasksWithTheirArgs.get(tFactoryName).get(i).indexOf(" ");
	                   			lst.add(tasksWithTheirArgs.get(tFactoryName).get(i).substring(0, val));
	                   			lst.add(tasksWithTheirArgs.get(tFactoryName).get(i).substring(val+1));
	               			}
	               			else{
	               				lst.add(tasksWithTheirArgs.get(tFactoryName).get(i).toString());
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
               		tf.executeTask();      
               	}
            }
        }

        
        
        if (args.length == 0) {
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
