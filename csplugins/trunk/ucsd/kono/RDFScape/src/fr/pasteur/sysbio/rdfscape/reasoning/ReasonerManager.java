/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.reasoning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.AbstractListModel;

import fr.pasteur.sysbio.rdfscape.Contextualizable;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.RDFScapeModuleInterface;
import fr.pasteur.sysbio.rdfscape.context.ContextManager;
import fr.pasteur.sysbio.rdfscape.knowledge.InfRuleObject;
import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.knowledge.TripleStoreWrapper;



/**
 * @author andrea@psteur.fr
 * Reasoner Manager handles rules and forward them ,together with inference settings, to the
 * knowledgeWrapper. It also handles the selection of different knowledge engines.
 * 
 */
public class ReasonerManager  extends AbstractListModel implements RDFScapeModuleInterface, Contextualizable {
	private ReasonerManagerPanel myPanel=null;
	private ArrayList rules=null;					//rules
	private String[] optionDefaults=null;
	// TODO to re-introduce... private String[] reasoningEngines={"JENA","TripleStore","Pellet","Sesame","Joseki"};	//The reasoners supported. This object is also a small wrapper of KnowledgeWrapper....
	private String[] reasoningEngines={"JENA"};
	private String	reasoningEngine="JENA";
	//private String[] ruleType={"POST","PRE"};
	
	public ReasonerManager() throws Exception {
		super();
		System.out.print("\tReasonerManager... ");
		if(RDFScape.getContextManager()==null)
			throw new Exception("Unable to build Reasoner Manager : missing ContextManager");
		RDFScape.getContextManager().addContextualizableElement(this);
		setActiveEngine("Jena");	// This is our default. We should have a reasoner at all the time.
		initialize();
		System.out.println("Ok");
		
	}
	/**
	 * @param knowledge
	 */
	/*
	public void setKnowledge(KnowledgeWrapper km) {
		knowledgeWrapper=km;
		initialize();
		
		
	}
	*/
	/** 
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#initialize()
	 * This takes Reasoner Manager to post-constructor state.
	 */
	public boolean initialize() {
		return initializeRules() && initializeSettings(); 	
	}
	public boolean initializeRules() {
		int oldSize=0;
		if(rules!=null)
			oldSize=rules.size();
			
		rules=new ArrayList();
		
		if(oldSize!=0) fireIntervalRemoved(this,0,oldSize);
		return true;
	}
	public boolean initializeSettings() {
		optionDefaults=RDFScape.getKnowledgeEngine().getReasonerActualOptions(); 
		return true;
	}
	
	
	/** (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#canOperate()
	 */
	public boolean canOperate() {
		// TODO what should this verify ?
		return true;
	}

	/**
	 * Updates the knowledge manager on reasoing settings and issues aupdate of knowledge
	 */
	public void buildKnowledge() {
		RDFScape.getKnowledgeEngine().addRuleSet(rules);
		//knowledgeWrapper.setReasonerActualOptions(optionDefaults);
		RDFScape.getKnowledgeEngine().touch();
	}

	

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#touch()
	 */
	public void touch() {
		forwardRules();
		RDFScape.getKnowledgeEngine().touch();
		
		
	}

	
	public String[][] getOptions() {
		return RDFScape.getKnowledgeEngine().getReasonerOptions();
	}
	public String[] getDefaultOptions() {
		return RDFScape.getKnowledgeEngine().getReasonerActualOptions();
	}

	/**
	 * @param selectedOptions
	 */
	public void setSelectedOptions(String[] selectedOptions) {
		String[] paramNames=RDFScape.getKnowledgeEngine().getReasonerOptionNames();
		for (int i = 0; i < selectedOptions.length; i++) {
			RDFScape.getKnowledgeEngine().setReasonerParameter(paramNames[i],selectedOptions[i]);
		}
		
	}

	/**
	 * 
	 */
	public void forwardRules() {
		RDFScape.getKnowledgeEngine().addRuleSet(rules);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void deleteRule(InfRuleObject ro) {
		int index=getIndexOfRuleObject(ro);
		if(index>=0) {
			System.out.println("Going to remove "+index);
			rules.remove(ro);
			fireIntervalRemoved(this,index,index);
		}
	}
	public void addRuleObject(InfRuleObject ro) {
			rules.add(ro);
			fireIntervalAdded(this,getIndexOfRuleObject(ro),getIndexOfRuleObject(ro));
	}
	
	public InfRuleObject getRule(int i) {
		System.out.println("get "+i);
		return (InfRuleObject) rules.get(i);
	}
	
	
	
	public ReasonerManagerPanel getReasonerManagerPanel() {
		if(myPanel==null) myPanel=new ReasonerManagerPanel(this);
		return myPanel;
	}

	/**
	 * @return
	 */
	public ArrayList getRules() {
		return rules;
	}
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return rules.size();
	}
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int i) {
		
		return (InfRuleObject)rules.get(i);
	}

	/**
	 * @param newObject
	 * @return
	 */
	public int getIndexOfRuleObject(InfRuleObject myObject) {
		return rules.indexOf(myObject);
	}

	/**
	 * @param index
	 * @param text
	 * @param params
	 */
	public void updateRule(int index, String text, String[][] params) {
		System.out.println("Reasoner Manager: updating rule #"+index);
		String isValid=RDFScape.getKnowledgeEngine().validateRule(text);
		if(isValid!=null) {
			RDFScape.warn(isValid+" Note: I'm not checking prefixes correctly: You have to check them yourself in the namesace panel! ");
			
		}
		getRule(index).setRule(text);
		
		System.out.println("Rule ha name: "+getRule(index).getName());
		if(params!=null) {
			for (int i = 0; i < params.length; i++) {
				getRule(index).setParam(params[i][0],params[i][1]);
				System.out.println(params[i][1]+"->"+params[i][0]);
			}
		}
		fireContentsChanged(this,index,index);
		
	}
	
	
	
	
	
	/**
	 * @return
	 */
	public String[] getEngines() {
		return reasoningEngines;
	}

	
	public void setActiveEngine(String string) {
		if(string==null) return;
		KnowledgeWrapper knowledgeWrapper=null;
		try {
		if(string.equalsIgnoreCase("Jena")) {
			knowledgeWrapper=new JenaWrapper();
			reasoningEngine=string;
			RDFScape.setKnowledgeEngine(knowledgeWrapper);
			if(myPanel!=null) myPanel.updateAfterEngineChange();
		}
		else if(string.equalsIgnoreCase("TripleStore")) {
			knowledgeWrapper=new TripleStoreWrapper();
			reasoningEngine=string;
			RDFScape.setKnowledgeEngine(knowledgeWrapper);
			if(myPanel!=null) myPanel.updateAfterEngineChange();
		}
		else {
			RDFScape.warn(string+" is unsupported yet... still at "+reasoningEngine);
		}
		} catch (Exception e) {
			System.out.println("Unable to build KnowledgeWrapper... going to have troubles!");
		}
				
	}

	
	/**
	 * @return
	 */
	public String getActiveEngine() {
		return reasoningEngine;
	}

	


	


	/**
	 * 
	 */
	private void resetRules() {
		int start=0;
		int end=rules.size();
		rules=new ArrayList();
		fireIntervalRemoved(this,start,end);
		
	}
	
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#reset()
	 */
	public void reset() {
		initialize();
		RDFScape.getKnowledgeEngine().reset();
		myPanel.updateAfterEngineChange();
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#update()
	 */
	public void update() {
		
		
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#validate()
	 */
	public boolean isInValidState() {
		 return true;
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#loadFromContext()
	 */
	public boolean loadFromActiveContext() {
		reset();
		boolean a= loadReasoningSettingFromActiveContext();
		boolean b= loadInferenceRulesFromActiveContext();
		return a&&b;
	}
	
	private boolean loadReasoningSettingFromActiveContext() {
		int counter = 0;
		String line;
		String myFileName=RDFScape.getContextManager().getActiveContext().getReasoningSettingsFileName();
		System.out.println("Loading inference settings in : "+myFileName);
		try {
			BufferedReader inFile = new BufferedReader(new FileReader(myFileName));
			System.out.println("Got file");
			while((line = inFile.readLine()) != null) {
				counter++; 
				System.out.println("Line: "+line);
				StringTokenizer st=new StringTokenizer(line);
				String param=st.nextToken();
				String value=st.nextToken();
				if(param==null || value==null) {
					System.out.println("Incorrect file format");
					return false;
				}
				if(param.equalsIgnoreCase("Engine:")) {
					if(value.equalsIgnoreCase("Jena"))  {
						setActiveEngine(value);
					}
					else {
						RDFScape.warn("Unable to use other then Jena... check your analysis context");
					}
				}
				else { 
					RDFScape.getKnowledgeEngine().setReasonerParameter(param,value);
				}
				
				counter++;	
		    }
			inFile.close();
		} catch (Exception e) {
			System.out.println("Problems while reading inference settings file");
			e.printStackTrace();
		}
		return true;
	}
	
	boolean loadInferenceRulesFromActiveContext() {
		int counter = 0;
		String myFileName=RDFScape.getContextManager().getActiveContext().getReasoningInfRulesDirName();
		File rulesDir=new File(myFileName);
		if(!rulesDir.isDirectory()) {
			System.out.println("Unable to locate inference rules");
			return false;
		}
		try {
			File tempRuleFile=null;
			File[] ruleFiles=rulesDir.listFiles();
			for (int i = 0; i < ruleFiles.length; i++) {
				InfRuleObject tempRule=new InfRuleObject();
				System.out.println("Reading inf rule file: "+ruleFiles[i]);
				BufferedReader inFile = new BufferedReader(new FileReader(ruleFiles[i]));
				String line = inFile.readLine();
				if(line==null) {
					System.out.println("Empty rule file");
					break;
				}
				else {
					if(line.equalsIgnoreCase("Y")) tempRule.setActive(true);
					else if(line.equalsIgnoreCase("N")) tempRule.setActive(false);
					else {
						System.out.println("Incorrect rule file format");
						break;
					}
				}
				System.out.println("Y/N :"+line);
				line = inFile.readLine();
				System.out.println("Just read :"+line);
				while(!line.equalsIgnoreCase("RULE")) {
					StringTokenizer st=new StringTokenizer(line);
					String key=st.nextToken();
					String value=null;
					if(st.hasMoreTokens()) {
						value=st.nextToken();
					}
					else value="";
				
					if(key==null || value==null) {
						System.out.println("Incorrect rule format");
						break;
					}
					else {
						tempRule.setParam(key,value);
					}
					line = inFile.readLine();
					if(line==null) {
						System.out.println("Incorrect file format");
						break;
					}
				}
				System.out.println("Params read");
				String ruleText=new String();
				while((line = inFile.readLine()) != null) {
					ruleText=ruleText.concat(line+"\n");
				}
				System.out.println("Rule was: "+ruleText);
				tempRule.setRule(ruleText);
				addRuleObject(tempRule);
		  }
		  
		 
		}
		catch(IOException ioe)
		{
		  System.out.println("Unable to load inference rules");
		  return false;
		}
		return true;
	}
	

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#saveToContext()
	 */
	public boolean saveToContext() {
		boolean a=saveInfSettingsToActiveContext() ;
		boolean b=saveRulesToActiveContext() ;
		return a&&b;
	}

	private boolean saveRulesToActiveContext() {
		int counter = 0;
		String myDirName=RDFScape.getContextManager().getActiveContext().getReasoningInfRulesDirName();
		System.out.println("Saving rules to : "+myDirName);
		File myTargetDir=new File(myDirName);
		File[] myFiles=myTargetDir.listFiles();
		for (int i = 0; i < myFiles.length; i++) {
			myFiles[i].delete();
		}
		System.out.println("Cleaned");
		for (Iterator iter = rules.iterator(); iter.hasNext();) {
			try {
				InfRuleObject rule = (InfRuleObject) iter.next();
				System.out.println("Rule: "+rule.getName());
				FileWriter fo=new FileWriter(new File(myDirName+"/"+rule.getName()));
				if(rule.isActive()) fo.write("Y\n");
				else fo.write("N\n");
				String[] ruleParams=RDFScape.getKnowledgeEngine().getRuleOptionsNames();
				if(ruleParams!=null) {
					for (int i = 0; i <ruleParams.length; i++) {
						fo.write(ruleParams[i]+"\t"+rule.getParam(ruleParams[i])+"\n");
					}
				}
				fo.write("RULE\n");
				fo.write(rule.getRule()+"\n");
				fo.close();
				System.out.println("Ok");
			}
			catch (Exception e) {
				System.out.println("Unable to write rule");
				e.printStackTrace();
				return false;
			}
			
		}  
		 
		return true;
		
	}
	
	private boolean saveInfSettingsToActiveContext() {
		String myFileName=RDFScape.getContextManager().getActiveContext().getReasoningSettingsFileName();
		System.out.println("Saving inference settings to : "+myFileName);
		File infsettingsFile=new File(myFileName);
		
		try {
			FileWriter fw=new FileWriter(infsettingsFile);
			fw.write("Engine: "+reasoningEngine +"\n");
			String[] paramNames=RDFScape.getKnowledgeEngine().getReasonerOptionNames();
			String[] paramValues=RDFScape.getKnowledgeEngine().getReasonerActualOptions();
			if(paramNames!=null) {
				for (int i = 0; i < paramNames.length; i++) {
					fw.write(paramNames[i]+" "+paramValues[i]+"\n");
				}
			}	
			fw.close(); 
			return true;
		}
		catch (Exception e) {
			System.out.println("Unable to write infsetting");
			e.printStackTrace();
			return false;
		}
	
	}
	
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.Contextualizable#registerContextManager(fr.pasteur.sysbio.rdfscape.context.ContextManager)
	 */
	public void registerContextManager(ContextManager cm) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @return
	 */
	public String[][] getRuleOptions() {
		
		return RDFScape.getKnowledgeEngine().getRuleOptions();
	}
	/**
	 * @return
	 */
	public String[] getRuleDefaultOptions() {
		return RDFScape.getKnowledgeEngine().getRuleOptionDefaultValues();
	}
	/**
	 * @return
	 */
	public String[] getRuleOptionParams() {
		return RDFScape.getKnowledgeEngine().getRuleOptionsNames();
	}
	/**
	 * @return
	 */
	/*
	public KnowledgeWrapper getWrappedKnowledge() {
		return RDFScape.getKnowledge();
	}
	*/
	public boolean rulesEnabled() {
		return RDFScape.getKnowledgeEngine().canAddRules();
	}
	public boolean hasReasoningOptions() {
		if(RDFScape.getKnowledgeEngine().getReasonerOptions()==null) return false;
		else return true;
	}
	public void reason() {
		RDFScape.getKnowledgeEngine().touch();
		
	}
	public String[] getReasonerOptionNames() {
		return RDFScape.getKnowledgeEngine().getReasonerOptionNames();
		
	}
	public String[] getReasonerActualOptions() {
		return RDFScape.getKnowledgeEngine().getReasonerActualOptions();
	}

	


	



}

