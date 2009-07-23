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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;


public class CLTaskFactoryInterceptor {
	
	CommandLineProvider clp;
	
    private String[] args;
    Map<TaskFactory, TFWrapper> taskMap;
    private Map<String, List<String>> tasksWithTheirArgs;
    private List<String> listOfChoosenTasks;
    private Options optionsOfTasks;
    private CommandLineParser parser = new PosixParser();
    private CommandLine line = null;

    long time = 0;
    private TaskFactoryGrabber grabber;
    
    CLTaskFactoryInterceptor(CommandLineProvider colipr, TaskFactoryGrabber tfg) {
    	this.clp = colipr;
    	this.grabber = tfg;
    	
//    	time = grabber.getDifference();
//    	while(grabber.getDifference()-time<100){
//    		time = grabber.getDifference();
//    		System.out.println("Number of factory loaded = " + grabber.getNumberTasks());
//    	}

    	taskMap = tfg.getTaskMap();
    	args = clp.getCommandLineCompleteArgs();

    	createTaskOptions();
        findTaskArguments();
        parseTaskArguments();
        executeCommandLineArguments();
    }
    
//    private void executeParsingActions(){
//        findTaskArguments();
//        parseTaskArguments();
//        executeCommandLineArguments();    	
//    }
    

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
			// otherwise add it to the list of task specific args 
            } else {
            	if (!argsString.startsWith("-")) {
            		tasksWithTheirArgs.get(lastArg).get(lastIdx).concat(" " + argsString);
            		tasksWithTheirArgs.get(lastArg).set(lastIdx,tasksWithTheirArgs.get(lastArg).get(lastIdx).concat(" " + argsString));
                    lastIdx++;
            	}
            	else if (lastArg == null) {
                    System.out.println("The Task \"" + argsString + "\" doesn't exist : Check the options");
                    printHelp(optionsOfTasks);
                    System.exit(0);
                /*} else if (!argsString.startsWith("-")) {
                    taskSpecificArgs.get(lastArg).get(lastIdx).concat(" " + argsString);
                    taskSpecificArgs.get(lastArg).set(lastIdx,taskSpecificArgs.get(lastArg).get(lastIdx).concat(" " + argsString));
                    lastIdx++;*/

                    // taskArguments.add(lastArg);
                } else {
                	tasksWithTheirArgs.get(lastArg).add(argsString);
                }
            }
        }

        
        
        //print the different parsed arguments
        System.out.println("tasksWithTheirArgs :");
        for(String st : tasksWithTheirArgs.keySet())
        	System.out.println(st + " = " + tasksWithTheirArgs.get(st));
        System.out.println("\n\n");
        
        System.out.println("listOfChoosenTasks :");
        for(String st : listOfChoosenTasks)
        	System.out.println(st);
        System.out.println("\n\n\n");
        
        
        
        
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
            printHelp(optionsOfTasks);
            System.exit(1);
        }
    }

    private void executeCommandLineArguments() {
        if (line.hasOption("listTasks")) {
            System.out.println("The General Help has been called");
            printHelp(optionsOfTasks);
            System.exit(0);
        }

        for (String st : listOfChoosenTasks) {
            for (TFWrapper tf : taskMap.values()) {
                if (st.equals(tf.getName())) {

                	System.out.println("\n \n ########### factory loaded = "+tf.getName()+" ###########");
                	String tFactoryName = tf.getName();
               		List<String> lst = new ArrayList<String>();	
                	
               		for(int i=0;i<tasksWithTheirArgs.get(tFactoryName).size();i++) {
               			if(tasksWithTheirArgs.get(tFactoryName).get(i).contains(" ")) {
                   			int val = tasksWithTheirArgs.get(tFactoryName).get(i).indexOf(" ");
                   			lst.add(tasksWithTheirArgs.get(tFactoryName).get(i).substring(0, val));
                   			lst.add(tasksWithTheirArgs.get(tFactoryName).get(i).substring(val+1));
               			}
               			else{
               				lst.add(tasksWithTheirArgs.get(tFactoryName).get(i).toString());
               			}
               		}
                	
              		//creation of arguments
               		String[] args = new String[lst.size()];
               		for(int i=0;i<lst.size();i++)args[i]=lst.get(i);
               		
               		clp.setSpecificArgs(args);
               		tf.executeTask();                }
            }
        }

        
        
        if (args.length == 0) {
            printHelp(optionsOfTasks);
            System.exit(0);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        System.out.println("\n");
        formatter.printHelp("java -Xmx512M -jar cytoscape.jar [Options]",
            "\nHere are the different taskFactories implemented :", options, "");
    }

//	public void frameworkEvent(FrameworkEvent event) {
//		if(event.getType() == FrameworkEvent.STARTED)
//			executeParsingActions();
//	}
}
