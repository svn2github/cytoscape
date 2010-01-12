/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import fr.pasteur.sysbio.rdfscape.DefaultSettings;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;

/**
 * @author andrea
 * Manages a set of contexts. Add and delete contexts. Wraps I/O functions.
 *
 */
public class ContextElement {
	private RDFScape myRDFScapeInstance=null;
	
	private String myPath=null;				//base directory for configuration files
	private String myName=null;				//name of the context
	
	
	//private String reasoningEngine=null;
	//private Hashtable reasoningParameters=null;
	private String errors=null;
	
	File myFile=null;
	
	/**
	 * @param directory the directory (full pathname) of the context
	 * 
	 */
	public ContextElement(String directory) {
		super();
		//If not present... create directory structure TODO
		myPath=directory;
		myFile=new File(myPath);
		myName=myFile.getName();
		//System.out.println("Name: "+myName);
		if(!myFile.exists()) {
			System.out.println("Making default context. Name= "+myName);
			makeDefault();
			
		}
		
		//System.out.println("Going to analyze reasoner requirements for this namespace");
		//reasoningParameters=new Hashtable();
		//errors=new String("In context: "+myName+"\n");
		//loadGeneralInfoSettings();
		
	}
	
	
	
	/**
	 * @param myFile
	 */
	private void makeDefault() {
		myFile.mkdir();
		String path=myFile.getAbsolutePath();
		String ontologies=path+"/ontologies";
		String namespaces=path+"/namespaces";
		String infsettings=path+"/infsettings";
		String splitfile=path+"/splitinfo";
		File infsettingsFile=new File(infsettings);
		
		try {
			FileWriter fw=new FileWriter(infsettingsFile);
			String myDefaultKnowledgeEngine=DefaultSettings.defaultKnowledgeEngine;
			if(!myDefaultKnowledgeEngine.equalsIgnoreCase("Jena")) {
				System.out.println("Only Jena is supported as default. Check your default settings");
				return;
			}
			fw.write("Engine: "+DefaultSettings.defaultKnowledgeEngine+"\n");
			/*
			Hashtable optionBlock=JenaWrapper.getDefaultKnowledgeEngineOptions();
			Enumeration keys=optionBlock.keys();
			while(keys.hasMoreElements()) {
				String currentArg=(String) keys.nextElement();
				String currentVal=(String) optionBlock.get(currentArg);
				fw.write(currentArg+" "+currentVal+"\n");
			}
			*/
			fw.close(); // note that this may remain opened... TODO
		}
		catch (Exception e) {
			System.out.println("Unable to write default");
		}
		
		
		String infrules=path+"/infrules";
		File infrulesFile=new File(infrules);
		infrulesFile.mkdir();
		String patterns=path+"/patterns";
		File patternsFile=new File(patterns);
		patternsFile.mkdir();
		String maprules=path+"/maprules";
		File maprulesFile=new File(maprules);
		maprulesFile.mkdir();
		
		
		
	}
	public String getName() {
		return myName;
	}
	
	
	
	public void delete() {
		
		String path=myFile.getAbsolutePath();
		System.out.println("Deleting..."+path);
		String ontologies=path+"/ontologies";
		File ontologiesFile=new File(ontologies);
		deleteDir(ontologiesFile);
		String namespaces=path+"/namespaces";
		File namespacesFile=new File(namespaces);
		namespacesFile.delete();
		String infsettings=path+"/infsettings";
		File infsettingsFile=new File(infsettings);
		infsettingsFile.delete();
		String infrules=path+"/infrules";
		File infrulesFile=new File(infrules);
		deleteDir(infrulesFile);
		String patterns=path+"/patterns";
		File patternsFile=new File(patterns);
		deleteDir(patternsFile);
		
		String maprules=path+"/maprules";
		File maprulesFile=new File(maprules);
		deleteDir(maprulesFile);
		
		myFile.delete(); //it should be empty now!
	
	}
	
	public String getOntologiesListFileName() {
		return myPath+"/ontologies";
		
	}
	
	public String getNamespacesListFileName() {
		return myPath+"/namespaces";
		
	}
	
	public String getReasoningSettingsFileName() {
		return myPath+"/infsettings";
		
	}
	
	public String getReasoningInfRulesDirName() {
		return myPath+"/infrules";
	}
	
	public String getDefaultMapDirName() {
		return myPath+"/maprules";
	}
	public String getPatternsDirName() {
		return myPath+"/patterns";
	}
	/**
	 * @return true if this context elements has an infsetting file inside...
	 */
	public boolean check() {
		if((new File(getReasoningSettingsFileName())).exists()) return true;
		else return false;
	}
	/**
	 * @return a string reporting errors in loading in necessary information in the context 
	 * ( inferene setting)
	 */
	public Object getErrorString() {
		return errors;
	}
	/**
	 * @return a String representing the reasoning engine used (i.e.: Jena)
	 */
	/*
	public String getKnowledgeEngine() {
		return reasoningEngine;
	}
	*/
	/**
	 * @return an Hashtable Arg->Value with reasoner settings
	 */
	/*
	public Hashtable getKnowledgeEngineParameters() {
		return reasoningParameters;
	}
	*/
	/*
	private boolean loadGeneralInfoSettings() {
		int counter = 0;
		String line;
		try {
			String reasoningConfigFile=getReasoningSettingsFileName();
			System.out.println("Test: "+reasoningConfigFile);
			BufferedReader inFile = new BufferedReader(new FileReader(reasoningConfigFile));
			
			while((line = inFile.readLine()) != null) {
				counter++; 
				StringTokenizer st=new StringTokenizer(line);
				String value=st.nextToken();
				String arg=st.nextToken();
				
				System.out.println("READ: "+value+" + "+arg+" <");
				if(value==null || arg==null) {
					errors.concat("Incorrect file format\n");
					return false;
				}
				if(value.equalsIgnoreCase("Engine:")) {
					if(arg.equalsIgnoreCase("Jena"))  reasoningEngine="Jena";
					else { 
						errors.concat("Only Jena engine is supported!\n");
						return false;
					}
						
				}
				else {
					reasoningParameters.put(value,arg);
				}
				counter++;	
		    }
			inFile.close();
			return true;
		}
		catch (Exception e) {
			errors.concat("File error:\n"+e.getStackTrace());
			return false;
		}
	
		
	}
	*/

	 private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }



	public String getSplitConditionsFileName() {
		return myPath+"/splitinfo";
	}



	



	
}
