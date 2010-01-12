/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/* Rev 1 Code Cleaning,  Docs
 * Rev 2 SPQRL, performance, minor features
 * 
 */

package fr.pasteur.sysbio.rdfscape;
import java.awt.event.ActionEvent;
import java.net.URL;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import fr.pasteur.sysbio.rdfscape.browsing.BrowserManager;
import fr.pasteur.sysbio.rdfscape.cytomapper.CytoMapper;
import fr.pasteur.sysbio.rdfscape.cytoscape.CytoscapeDealer;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;
import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.namespacemanagement.NamespaceManager;
import fr.pasteur.sysbio.rdfscape.ontologyhandling.OntologyLoaderManager;
import fr.pasteur.sysbio.rdfscape.ontologyhandling.RDFWrappersFactory;
import fr.pasteur.sysbio.rdfscape.patterns.PatternManager;
import fr.pasteur.sysbio.rdfscape.query.QueryManager;
import fr.pasteur.sysbio.rdfscape.reasoning.ReasonerManager;

import fr.pasteur.sysbio.rdfscape.context.ContextManager;

/**
 * @author andrea@pasteur.fr
 * This is the plugin "root". Acts as container and coordinator for other moduels
 *
 */
public class RDFScape extends CytoscapePlugin {
	/**
	 * KnowledgeWrapper wraps the knowledge base
	 * CommonMemory contains infos on Cytoscape to Knowledge base relations
	 * These classes are not modules
	 */
	static private  KnowledgeWrapper myKnowledgeEngine=null;
	static private  CommonMemory	myMemory=null;
	
	/**
	 * The following classses are modules (with their eventual view)
	 * 
	 */
	static private PatternManager patternManager=null;
	static private ContextManager contextManager=null;
	
	static private OntologyLoaderManager ontologyLoaderManager=null;
	static private NamespaceManager namespaceManager=null;
	static private ReasonerManager reasonerManager=null;
	static private BrowserManager browserManager=null;
	static private QueryManager queryManager=null;
	static private CytoMapper	cytoMapper=null;
	static private CytoscapeDealer cytoscapeDealer=null;
	static private HelpManager helpManager=null;
	
	/**
	 * Visual element for RDFScape plugin
	 */
	static private RDFScapePanel myPanel=null;
	
	/**
	 * Constructor. Note that the real construction is done in the init() method.
	 * Wether the init() is in the constructor or in the menu-action related functions 
	 * determines wether we are going to have a single instance of RDFScape or more. 
	 */
	public RDFScape() {
		super();
		System.out.println("Registering RDFScape plugin");
		RDFScapeStartAction rdfScapeStartAction=new RDFScapeStartAction(this); 
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(rdfScapeStartAction);
		init(); //Here we initialize all the static fields of the plugin
		

	}
	
	/**
	 * This builds the plugin.
	 */
	private boolean init() {
		System.out.println("Building RDFScape plugin");
		/*
		 * Note: order matters! It is the same order objects load values from context...
		 * Though this just change the way defaults are "intepreteted" in "power"
		 */
		/**
		 * contextManager and  myMemory are required by other modules!
		 */
		contextManager=new ContextManager();
		myMemory=new CommonMemory();
		
		try {
			myKnowledgeEngine=new JenaWrapper();
			namespaceManager=new NamespaceManager();
			ontologyLoaderManager=new OntologyLoaderManager();
			cytoscapeDealer=new CytoscapeDealer();
			browserManager=new BrowserManager();
			patternManager=new PatternManager();
			cytoMapper=new CytoMapper();
			reasonerManager=new ReasonerManager();
			queryManager=new QueryManager();
			helpManager=new HelpManager();
		} catch (Exception e) {
			System.out.println("Something went wrong during initialization ");
			System.out.println(e.getMessage());
			
		};
		System.out.println("RDFScape plugin built");
		URL vizmapURL= Utilities.class.getResource("../../../../extras/rdfscapevizmap.props");
		System.out.println("RDFSCape vizmap file: "+vizmapURL);
		Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,
				vizmapURL); 
		
		
		
		return true;
	
	}
	
	public static void setKnowledgeEngine(KnowledgeWrapper kw) {
		myKnowledgeEngine=kw;
		myMemory.relink();
	}
	
	

	
	

	
	/*
	 * @author andrea@pasteur.fr
	 *
	 * This is "likely" a wrapper of a constructor
	 */
	public final class RDFScapeStartAction extends CytoscapeAction {
		RDFScape myRDFScapeInstance=null; // TODO: is there a clean way to recover the "this" reference of the enclosing object ?
		public RDFScapeStartAction(RDFScape rdfscape) {
			super("RDFScape");
			myRDFScapeInstance=rdfscape;
		}
		public void actionPerformed(ActionEvent e) {
			if(!init()) {
				
				System.out.println("Init problems... sorry");
				return;
			}
			Cytoscape.getVisualMappingManager().setVisualStyle("RDFScape");
			myPanel=new RDFScapePanel();
		}
	}
	
	public static KnowledgeWrapper getKnowledgeEngine() {
		return myKnowledgeEngine;
	}
	public static OntologyLoaderManager getOntologyManager() { 
		return ontologyLoaderManager; 
	}
	public static NamespaceManager getNameSpaceManager() {
		return namespaceManager;
	}
	public static CytoMapper getCytoMapper() {
		return cytoMapper;
	}
	public static ReasonerManager getReasonerManager() {
		return reasonerManager;
	}
	public static PatternManager getPatternManager() {
		return patternManager;
	}
	public static CytoscapeDealer getCytoscapeDealer() {
		return cytoscapeDealer;
	}
	public static BrowserManager getBrowserManager() {
		return browserManager;
	}
	public static QueryManager getRDQLQueryManager() {
		return queryManager;
	}
	public static ContextManager getContextManager() {
		return contextManager;
	}

	public static CommonMemory getCommonMemory() {
		return myMemory;
	}

	public static HelpManager getHelpManager() {
		return helpManager;
	}
	

	/**
	 * Issues global changes when a change of ontology (only additions allowed) occours.
	 * We have monotonic reasoning here. So no need to update more than the namespaces
	 * for visualization porpouses (short names).
	 */
	/**
	 * 
	 */
	public void baseOntologyChanged() {
		// TODO Auto-generated method stub
		
	}
	
	public void ontologyChanged() {
		namespaceManager.touch();
		if(!namespaceManager.canOperate()) warn("Namespaces changed and need a check.");
		//browserManager.update(); //TODO add update QueryManager
	}
	
	public void contextChanged() {
		
	}
	
	//////////////////////////////////
	// EDIT FROM HERE
	//////////////////////////////////
	
	
	
	
	/**
	 * Controls global changes when namespace settings are changed.
	 * Note: namespace setting relates mostly to visualization porposuses.
	 */
	/*
	public void nameSpacesChanged() {
		rdfWrappersFactory.updateAllNameSpaces();
		checkNameSpaceConsistency();
	}
	*/
	/**
	 * Controls global changes when a namespace setting is changed.
	 * Note: namespace setting relates mostly to visualization porposuses.
	 * This method is provided for performance issues.
	 */
	/*
	public void nameSpaceChanged(String s) {
		rdfWrappersFactory.updateNameSpace(s);
		checkNameSpaceConsistency();
	}
	*/
	private void checkNameSpaceConsistency() {
		if(namespaceManager.canOperate()==false)
			myPanel.alert("Namespaces are incosistent!!! \n" +
					"please ensure any namespace has a distinct prefix, and that no prefix is empty\n" +
					"(though the latter is realy only to simplify your life) ");
	}
	/**
	 * @return the panel
	 */
	public static RDFScapePanel getPanel() {
		return myPanel;
	}
	
	
	
	



	

	/**
	 * 
	 */
	public void rulesUpdated() {
		//
		
	}

	/**
	 * @param message
	 */
	public static void warn(String message) {
		//Cytoscape.getDesktop();
		if(myPanel!=null)myPanel.alert(message);
		else System.out.println(message);
	}

	

	

	

	

	
	
	

	

	
	

	public static void resetModulesBeforeChangeOfContext() {
		myMemory.reset();
		ontologyLoaderManager.reset();
		namespaceManager.reset();
		reasonerManager.reset();
		browserManager.reset();
		cytoMapper.reset();
		patternManager.reset();
		cytoscapeDealer.reset();
		
		
	}

	public static void activeContextChanged() {
		myPanel.refreshTabs();
		browserManager.getPanel().refresh();
		reasonerManager.getReasonerManagerPanel().refresh();
	}

	public static void ontologiesLoaded() {
		getReasonerManager().touch();
		myPanel.refreshTabs();
		
	}

	public static void patternListChanged() {
		myPanel.refreshTabs();
		
	}

	public static void mappingActionPerformed() {
		myPanel.refreshTabs();
		
	}

	

	
	
	
	
	
}
