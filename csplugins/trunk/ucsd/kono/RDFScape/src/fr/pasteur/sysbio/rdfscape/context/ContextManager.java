/**
 * Copyright 2006-2008 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 4, 2005
 *
 */
package fr.pasteur.sysbio.rdfscape.context;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.Contextualizable;
import fr.pasteur.sysbio.rdfscape.DefaultSettings;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.RDFScapeModuleInterface;

/**
 * @author andrea@sgtp.net
 * Manages a list of contexts, handles reasoning engine defaults.
 */
public class ContextManager implements RDFScapeModuleInterface {
	//private RDFScape myRDFScapeInstance=null;			//link to the plugin
	private ContextElement activeContext=null;			//The active context
	private ContextManagerPanel myPanel=null;			//Link to the panel
	private Hashtable<String, ContextElement> availableContextElements=null;	//context name->element
	private String contextDirectory=null; 				//context directory
	private ArrayList<Contextualizable> modulesHandled=null;				//a list of modules interested in global save/load
	
	public ContextManager() {
		super();
		System.out.println("<CONTEXT MANAGER>");
		modulesHandled=new ArrayList<Contextualizable>();
		if(initialize()) System.out.println("Ok");
		else {
			System.out.println("Ko");
		}
		System.out.println("</CONTEXT MANAGER>");
	}
	
	public void addContextualizableElement(Contextualizable ct) {
		modulesHandled.add(ct);
	}
	

	/** 
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#initialize()
	 * Load a context list. Verify there is at least one valid context. Otherwise returns false.
	 */
	public boolean initialize() {
		System.out.println("Looking for contexts...");
		availableContextElements=new Hashtable<String, ContextElement>();
		contextDirectory=DefaultSettings.contextsDirectory;
		String globalContextDirectory=System.getProperty("user.dir")+"/"+contextDirectory;
		System.out.println("Context directory= "+globalContextDirectory);
		File rootContextDir=new File(globalContextDirectory);
		if(!rootContextDir.exists()) {
			System.out.println("Context directory is missing. Trying to make it now.");
			rootContextDir.mkdir();
			if(!rootContextDir.exists()) {
				System.out.println("Unable to make directory");
				return false;
			}
		}
		
		int i=0;
		FileFilter fileFilter = new FileFilter() {
	        public boolean accept(File file) {
	            return file.isDirectory();
	        }
	    };
	    File[] dirList=rootContextDir.listFiles(fileFilter);
		for (i = 0; i < dirList.length; i++) {
			System.out.println("Looking for context in "+dirList[i].getPath());
			ContextElement tempContextElement=new ContextElement(dirList[i].getPath());
			if(tempContextElement.check()) {
				availableContextElements.put(dirList[i].getName(),tempContextElement);
				System.out.println("Found a valid context, added to the list of available contexts.");
			}
			else {
				System.out.print(dirList[i].getName()+"Context had errors: ");
				System.out.println(tempContextElement.getErrorString());
			}
				
			
		}
		System.out.println("No more contexts");
		if(isInValidState()) {
			System.out.println("Setting default context");
			activeContext=availableContextElements.elements().nextElement(); 
			return true;
		}
		else {
			System.out.println("No contexts found, making a new context and starting again. If I hang here, complain for poor design...");
			activeContext=new ContextElement(rootContextDir+"/default");
			return initialize();
		
		}
		
		
	
	}

	/** (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#reset()
	 * reset is equivalent to @see fr.pasteur.sysbio.rdfscape.ContextManager#initialize()
	 */
	public void reset() {
		//equivalent to initialize() here.
		initialize();
		
	}



	/** 
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#isInValidState()
	 * At least one context must be present.
	 */
	public boolean isInValidState() {
		if(availableContextElements.size()>0) return true;
		else return false;
	}

	
	public String[] getAvailableContextsNames() {
		int size=availableContextElements.size();
		String[] answer=new String[size];
		int i=0;
		Enumeration<String> e=availableContextElements.keys();
		while(e.hasMoreElements()) {
			answer[i]=(e.nextElement());
			i++;
		}
		System.out.println("#context elements' names "+i);
		return answer;
		
	}

	
	
	ContextElement getContextByName(String name) {
		return availableContextElements.get(name);
	}
	
	public ContextElement getActiveContext() {
		return activeContext;
	}
	/**
	 * @return
	 */
	public String getActiveContextName() {
		if(activeContext==null) return null;
		else return activeContext.getName();
	}

	/**
	 * @param string
	 * 
	 */
	public void deleteContext(String context) {
		ContextElement myContext=availableContextElements.get(context);
		availableContextElements.remove(myContext.getName());
		myContext.delete();
		if(myPanel!=null) myPanel.updateContextElementsList();
		
	}
	

	/**
	 * @param newContext
	 */
	public void addContext(String newContext) {
		if(availableContextElements.containsKey(newContext)) return;
		else {
			ContextElement myContext=new ContextElement(contextDirectory+"/"+newContext);
			availableContextElements.put(newContext,myContext);
			if(myPanel!=null) myPanel.updateContextElementsList();
			// Update ComboBox
		}
		
	}
	
	/**
	 * @param string
	 * 
	 */
	public void loadActiveContext() {
		//We reset wverything here...
		RDFScape.resetModulesBeforeChangeOfContext();
		System.out.println("Going to load (default) context: "+activeContext.getName());
		for (Iterator<Contextualizable> iter = modulesHandled.iterator(); iter.hasNext();) {
			System.out.println(".");
			Contextualizable module = iter.next();
			module.loadFromActiveContext();
			
		}
		RDFScape.activeContextChanged();
		
	}
	
	
	
	
	

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#checkPreconditions()
	 */
	public boolean canOperate() {
		// need nothing
		return true;
	}

	/**
	 * @return
	 */
	public AbstractModulePanel getContextManagerPanel() {
		if(myPanel==null) myPanel=new ContextManagerPanel(this);
		return myPanel;
	}

	public void touch() {
		// TODO Auto-generated method stub
		
	}

	

	/**
	 * @param string
	 */
	public void setActiveContext(String selContext) {
		activeContext=availableContextElements.get(selContext);
		
	}

	/**
	 * 
	 */
	public void saveActiveContext() {
		//We reset wverything here...
		
		System.out.println("Going to save (default) context: "+activeContext.getName());
		for (Iterator<Contextualizable> iter = modulesHandled.iterator(); iter.hasNext();) {
			System.out.println(".");
			Contextualizable element = iter.next();
			element.saveToContext();
			
		}
		
	}
}
	