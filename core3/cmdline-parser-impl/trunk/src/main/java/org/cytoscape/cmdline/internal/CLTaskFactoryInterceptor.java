package org.cytoscape.cmdline.internal;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CLTaskFactoryInterceptor {
    private String[] args;
    Map<TaskFactory, TFWrapper> taskMap;
    private Map<String, List<String>> taskSpecificArgs;
    private List<String> taskArguments;
    private Options taskOptions;
    private CommandLineParser parser = new PosixParser();
    private CommandLine line = null;

    CLTaskFactoryInterceptor(CommandLineProvider colipr, TaskFactoryGrabber tfg) {
        args = colipr.getCommandLineArgs();
        taskMap = tfg.getTaskMap();

        createTaskOptions();
        findTaskArguments();
        parseTaskArguments();
        executeCommandLineArguments();
    }

    private void createTaskOptions() {
        taskOptions = new Options();

        // for each task factory, create an option
        for (TFWrapper tf : taskMap.values()) {
            taskOptions.addOption(tf.getOption());
        }

        taskOptions.addOption("lT", false, "Help = Display all the available taskFactories.");

        taskSpecificArgs = new HashMap<String, List<String>>();

        // for each task factory, create a list of strings
        for (Option opt : taskOptions.getOptions())
            taskSpecificArgs.put("-" + opt.getOpt().toString(),
                new ArrayList<String>());
    }

    public void findTaskArguments() {
        taskArguments = new ArrayList<String>();

        int lastIdx = 0;
        String lastArg = null;

        // iterate over args
        for (String argsString : args) {
			// if the arg represents task, add it to the task argument list
            if (taskSpecificArgs.containsKey(argsString)) {
                lastArg = argsString;
                lastIdx = 0;
                taskArguments.add(lastArg);
			// otherwise add it to the list of task specific args 
            } else {
                if (lastArg == null) {
                    System.out.println("The Task \"" + argsString +
                        "\" doesn't exist : Check the options");
                    printHelp(taskOptions);
                    System.exit(0);
                } else if (!argsString.startsWith("-")) {
                    taskSpecificArgs.get(lastArg).get(lastIdx).concat(" " + argsString);
                    taskSpecificArgs.get(lastArg).set(lastIdx,
                        taskSpecificArgs.get(lastArg).get(lastIdx).concat(" " + argsString));
                    lastIdx++;

                    // taskArguments.add(lastArg);
                } else {
                    taskSpecificArgs.get(lastArg).add(argsString);
                }
            }
        }

        //add the general help for all task
        for (String argsString : args) {
            if (argsString.equals("-lT")) {
                taskSpecificArgs.get(argsString).add("-lT");
                taskArguments.add("-lT");
            }
        }
    }

    private void parseTaskArguments() {
        try {
            line = parser.parse(taskOptions,
                    taskArguments.toArray(new String[taskArguments.size()]));
        } catch (ParseException pe) {
            System.err.println("Parsing command line failed: " +
                pe.getMessage());
            printHelp(taskOptions);
            System.exit(1);
        }
    }

    private void executeCommandLineArguments() {
        if (line.hasOption("lT")) {
            System.out.println("The General Help has been called");
            printHelp(taskOptions);
            System.exit(0);
        }

        for (String st : taskArguments) {
            for (TFWrapper tf : taskMap.values()) {
                if (st.equals(tf.getName())) {
                    tf.checkFactory(line, taskSpecificArgs, taskArguments);
                }
            }
        }

        if (args.length == 0) {
            printHelp(taskOptions);
            System.exit(0);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        System.out.println("\n");
        formatter.printHelp("java -Xmx512M -jar cytoscape.jar [Options]",
            "\nHere are the different taskFactories implemented :", options, "");
    }
}
