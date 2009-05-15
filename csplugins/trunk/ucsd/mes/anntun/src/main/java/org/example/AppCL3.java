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

public class AppCL3
{
    public static void main(String[] args) {

		java.util.List<taskFactory> list = new java.util.ArrayList<taskFactory>();
		list.add(0,new taskFactory(new TunableSampler(), "This is a TaskFactory example for TunableSampler" ));
		list.add(1,new taskFactory(new PrintSomething(), "This is a TaskFactory example for PrintSomething" ));
		list.add(2,new taskFactory(new JActiveModules(), "This is a TaskFactory example for JActiveModules" ));
		
		Options options = new Options();
		for ( taskFactory tf : list ){
			options.addOption( tf.getOption() );
		}
		options.addOption("h", "listTask", false, "Display all the available taskFactories.");
		

		
        CommandLineParser parser = new PosixParser();
        CommandLine line = null;
        String[] args1 = new String[1];
        String[] args2 = null;
		java.util.List test = new java.util.ArrayList();
        
        try {
        	if(args.length>1){
            	args2 = new String[args.length-1];

        		for(int i=0;i<args.length;i++)test.add(args[i]);
        		args1[0]= (String) test.get(0);           

        		for(int i=0;i<test.size()-1;i++)args2[i]=(String) test.get(i+1);
        		line = parser.parse(options, args1);
        	}
        	else line = parser.parse(options, args);

        } catch (ParseException e) {
            System.err.println("Parsing command line failed: " + e.getMessage());
			printHelp(options);
            System.exit(1);
        }

        // use what is found on the command line to set values
        if (line.hasOption("h")) {
			printHelp(options);
			System.exit(0);
        }

        for(taskFactory tf : list) tf.checkFactory(line,args2);

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
    	
    	void checkFactory(CommandLine line,String[] args){
    		if(line.hasOption(name.substring(0, 3))){
    			for(String ar : args)System.out.println("the arguments = "+ar);
    			getFactory(com,args);
    		}
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
