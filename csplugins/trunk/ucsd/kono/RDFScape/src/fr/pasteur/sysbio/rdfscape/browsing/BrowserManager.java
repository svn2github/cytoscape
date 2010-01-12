/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.browsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.Contextualizable;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.context.ContextManager;
import fr.pasteur.sysbio.rdfscape.cytoscape.CytoscapeDealer;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.GraphQueryAnswerer;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryAnswerer;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BrowserManager implements Contextualizable{
	//RDFScape myRDFScapeInstance=null;
	//CytoscapeDealer cytoscapeDealer=null;
	//KnowledgeWrapper myKnowledge=null;
	BrowserPanel myPanel=null;
	BrowserOptionPanel myOptionPanel=null;
	//CytoscapeDealer myCytoscapeDealer=null;
	//CommonMemory commonMemory=null;
	//ContextManager contextManager=null;
	boolean useLabels=false;
	private String splitQuery;
	
	//String ontologyBrowsingMode=null;
	
	/**
	 * @param cytoscapeDealer 
	 * @throws Exception 
	 * 
	 */
	public BrowserManager() throws Exception {
		System.out.print("\tBrowserManager... ");
		if(RDFScape.getCommonMemory()==null) {
			throw new Exception("Cannot build BrowserMnager : no common Memory");
		}
		if(RDFScape.getContextManager()==null) {
			throw new Exception("Cannot build BrowserMnager : no context Manager");
		}
		
		/**
		 * We don't save or read defaults here (yet). So here are our defaults:
		 * 
		 */
		RDFScape.getCommonMemory().setNamespaceConditionInAnd(false);
		RDFScape.getCommonMemory().setCollapseAttributes(false);
		RDFScape.getContextManager().addContextualizableElement(this);
		
		splitQuery=new String();
		System.out.println("Ok");
	}
	
	public BrowserPanel getPanel() {
		if(myPanel==null) myPanel=new BrowserPanel(this);
		return myPanel;
	}
	public BrowserOptionPanel getOptionPanel() {
		myOptionPanel=new BrowserOptionPanel(this);
		myOptionPanel.refreshAfterKnowledgeChange();
		return myOptionPanel;
	}

	public void reset() {
		myPanel.reset();
	}

	/**
	 * @param m
	 * 
	 */
	public void makeNewCytoscapePanel(String mode) {
		if(mode.equals("RDQL") || mode.equals("RDF")) mode="RDF";
		RDFScape.getCytoscapeDealer().makeNewPanel(mode);
		
	}

	/**
	 * @param myNodesStringArray
	 */
	public void plotNodesOnly(String[] myNodesStringArray) {
		for (int i = 0; i < myNodesStringArray.length; i++) {
			System.out.println("Dealin with: "+myNodesStringArray[i]);
			/* TODO to fix
			if(myRDFScapeInstance.myKnowledge.containsResource(ResourceFactory.createResource(myNodesStringArray[i]))) {
				RDFResourceWrapper myRes=myRDFScapeInstance.getMyRDFWrappersFactory().makeRDFResourceWrapper(myNodesStringArray[i]);
				myRDFScapeInstance.getCytoscapeDealer().addPatternNode(myRes);
			}
			*/
			//make RDF object
			//plot
		}
		
	}

	/**
	 * @param text
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public String makeRDFQuery(String text, boolean approx, boolean isSource, boolean isTarget) {
		String query="SELECT ?x \n WHERE ";
		if(approx==false) {
			if(isSource) query.concat("text ");
		}
		return null;
	}

	/**
	 * @param text
	 * @return
	 */
	/*
	public TableModel getResourceInModelAsTable(String text) {
		PatternMatchedTable myResult=new PatternMatchedTable();
		
		ArrayList namespaces=myRDFScapeInstance.myMemory.getNamespacesList();
		int k=0;
		for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
			String ns = (String) iter.next();
			Resource myResource=ResourceFactory.createResource(ns+text);
			/* TODO to fix
			if(myRDFScapeInstance.myKnowledge.containsResource(myResource)) {
				myResult.addString(myResource.toString(),k,0);
				k++;
			}
			
			if(k>0) myResult.addVar("Matches",0);
		}
		return myResult;
	}
*/
	/**
	 * @return
	 */
	public String[] getClassesList() {
		System.out.println("getClasses");
		if(RDFScape.getKnowledgeEngine()==null) {
			System.out.println("Null knowledge");
			return (new String[] {""});
		}
		System.out.println("...");
		/* TODO to fix
		Iterator iter=myRDFScapeInstance.myKnowledge.listNamedClasses();
		ArrayList myList=new ArrayList();
		while(iter.hasNext()) {
			myList.add( ((Resource)(iter.next())).toString() );
			System.out.println(".");
		}
		System.out.println(myList.size());
		
		String[] answer=new String[myList.size()];
		int k=0;
		for (k=0;k<myList.size();k++) {
			System.out.println("-");
			answer[k]=(String) myList.get(k);
			
			
			System.out.println(k);
		}
		
		return answer;
		*/
		return null;
	}

	/**
	 * @param currentClass
	 * @return
	 */
	/*
	public TableModel getInstancesForClassAsTable(String currentClass) {
		PatternMatchedTable myResult=new PatternMatchedTable();
		System.out.println(">> "+currentClass);
		if(currentClass==null) return myResult;
		if(myRDFScapeInstance.myKnowledge==null) {
			System.out.println("NK");
			return myResult;
		}
		
		Resource myRes=myRDFScapeInstance.myKnowledge.getResource(currentClass);
		if(myRes!=null) {
			System.out.println("got "+myRes);
			if(myRes.canAs(OntClass.class)) {
				System.out.println("++");
				OntClass myClass=(OntClass)myRes.as(OntClass.class);
				System.out.println("+++");
				Iterator myIter=myClass.listInstances();
				int k=0;
				while(myIter.hasNext()) {
					System.out.println(k);
					myResult.addString(((Resource)myIter.next()).toString(),k,0);
					k++;
				}
				if(k>0) myResult.addVar("Instances",0);
			}
		}
		
		return myResult;
	}
*/
	/**
	 * 
	 */
	public void setEditable() {
		RDFScape.getCytoscapeDealer().setEditable();
	}

	/**
	 * 
	 */
	public void getSnapshot(String name) {
		System.out.println("Pattern!!!");
		RDFScape.getPatternManager().getSnapshot(name);
		
		
		
	}

	public void setKnowledge(KnowledgeWrapper mk) {
		if(myPanel!=null) myPanel.refreshAfterEngineChange();
	}

	public ArrayList getAvailableQueryElements() {
		if(RDFScape.getKnowledgeEngine()==null) return new ArrayList();
		else return RDFScape.getKnowledgeEngine().getAvailableQueryManagers();
	}

	public void plotValuesInResultTable(AbstractQueryResultTable queryResult) {
		// TODO Auto-generated method stub
		System.out.println("Plotting a "+queryResult.getRowCount()+" x "+queryResult.getColumnCount()+" matrix");
		ArrayList nodeList=new ArrayList();
		for (int i = 0; i < queryResult.getRowCount(); i++) {
			for (int j = 0; j < queryResult.getColumnCount(); j++) {
				int[] tempTuple=new int[2];
				tempTuple[0]=i;
				tempTuple[1]=j;
				System.out.println(i+","+j+": isURI :"+queryResult.isURI(i,j));
				if(queryResult.isURI(i,j)) nodeList.add(tempTuple); 
			}
		}
		int selectedIndexes[][]=new int[nodeList.size()][2];
		int i=0;
		for (Iterator iter = nodeList.iterator(); iter.hasNext();) {
			int[] currentIndex = (int[]) iter.next();
			selectedIndexes[i][0]=currentIndex[0];
			selectedIndexes[i][1]=currentIndex[1];
			i++;
		}
		RDFScape.getCytoscapeDealer().addSelectedSetOfNodes(queryResult, selectedIndexes);
	}
	public void searchValuesInResultTable(AbstractQueryResultTable queryResult) {
		CytoscapeDealer cytoscapeDealer=RDFScape.getCytoscapeDealer();
		if(cytoscapeDealer.hasGraph()==false) {
			RDFScape.warn("No graph to search into");
			return;
		}
		// TODO Auto-generated method stub
		System.out.println("Searching a "+queryResult.getRowCount()+" x "+queryResult.getColumnCount()+" matrix");
		ArrayList nodeList=new ArrayList();
		for (int i = 0; i < queryResult.getRowCount(); i++) {
			for (int j = 0; j < queryResult.getColumnCount(); j++) {
				if(queryResult.isURI(i,j)) {
					cytoscapeDealer.searchURIs(queryResult);
					
				}
				
			}
		}
		
		
	}
	public boolean isNamespaceSelectionInAnd() {
		return RDFScape.getCommonMemory().isNamespaceConditionInAnd();
	}

	public boolean isCollapseAttributesTrue() {
		return RDFScape.getCommonMemory().isCollapseAttributesTrue();
	}

	public void setNamespaceSelectionInAnd(boolean b) {
		RDFScape.getCommonMemory().setNamespaceConditionInAnd(b);
		
	}

	public void setCollapseAttributes(boolean b) {
		RDFScape.getCommonMemory().setCollapseAttributes(b);
		
	}

	public boolean canCollapseDatatypes() {
		return RDFScape.getCytoscapeDealer().canCollapseDatatypes();
	}

	public boolean canExtend() {
		return RDFScape.getCytoscapeDealer().canExtend();
		
	}

	public void setPropagateURI(boolean b) {
		RDFScape.getCommonMemory().setPropagateURI(true);
		
	}

	public boolean canHandleLabels() {
		return KnowledgeWrapper.hasGraphAccessSupport(RDFScape.getKnowledgeEngine());
	}

	public void setUseLabels(boolean b) {
		CommonMemory commonMemory=RDFScape.getCommonMemory();
		CytoscapeDealer cytoscapeDealer=RDFScape.getCytoscapeDealer();
		KnowledgeWrapper myKnowledge=RDFScape.getKnowledgeEngine();
		if(!canHandleLabels()) return;
		if(b!=commonMemory.showRDFSLabels) {
			System.out.println("Updating labels!");
			commonMemory.showRDFSLabels=b;
			if(commonMemory.showRDFSLabels==true) {
				System.out.println("-> to rdfs");
				String[] uris=commonMemory.getURIs();
				for (int i = 0; i < uris.length; i++) {
					String myLabel=((GraphQueryAnswerer)myKnowledge).getRDFLabelForURI(uris[i]);
					if(myLabel!=null) commonMemory.registerURILabel(uris[i],myLabel);
					cytoscapeDealer.updateURILabel(uris[i]);
				}
			}
			else {
				String[] uris=commonMemory.getURIs();
				for (int i = 0; i < uris.length; i++) {
					String myLabel=((GraphQueryAnswerer)myKnowledge).getShortLabelForURI(uris[i]);
					if(myLabel!=null) commonMemory.registerURILabel(uris[i],myLabel);
					cytoscapeDealer.updateURILabel(uris[i]);
					System.out.println("-> to short");
				}
			}
			cytoscapeDealer.updateView();
		}
		
	}

	public boolean canHandleSplit() {
		return KnowledgeWrapper.hasRDQLSupport(RDFScape.getKnowledgeEngine());
	}

	public void setEnableSplit(boolean b) {
		CommonMemory commonMemory=RDFScape.getCommonMemory();
		if(commonMemory.splitEnabled!=b) {
			commonMemory.splitEnabled=b;
			if(commonMemory.splitEnabled) {
				if(splitQuery.length()<1) return;
				RDFScape.getCytoscapeDealer().restoreSplitConditions();
				String tempSplitQuery=splitQuery;
				while(tempSplitQuery.indexOf("OR CONDITION")>0) {
					int div=tempSplitQuery.indexOf("OR CONDITION");
					String head=tempSplitQuery.substring(0,div);
					String tail=tempSplitQuery.substring(div+12,tempSplitQuery.length());
					System.out.println("Query: "+head);
					AbstractQueryResultTable mySplit=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(head);
					tempSplitQuery=tail;
					RDFScape.getCytoscapeDealer().addSplitConditions(mySplit);
					
				}
				System.out.println("Query: "+tempSplitQuery);
				AbstractQueryResultTable mySplit=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(tempSplitQuery);
				RDFScape.getCytoscapeDealer().addSplitConditions(mySplit);
				
			}
		}
		
	}

	public void setSplitConditions(String text) {
		splitQuery=text;
		
	}

	public boolean loadFromActiveContext() {
		splitQuery=new String();
		System.out.print("Saving split query ");
		String splitFile=RDFScape.getContextManager().getActiveContext().getSplitConditionsFileName();
		File splitConditionsFile=new File(splitFile);
		try {
			if(splitConditionsFile.exists()) {
				BufferedReader inFile=new BufferedReader(new FileReader(splitConditionsFile));
				String line=inFile.readLine();
				while(line!=null) {
					splitQuery=splitQuery.concat(line+"\n");
					line=inFile.readLine();
				}
				inFile.close();
			}
			
			
			if(myOptionPanel!=null) myOptionPanel.setSplitConditions(splitQuery);
		} catch (Exception e) {
			System.out.println("Unable to load split conditions");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean saveToContext() {
		System.out.print("Saving split query ");
		String splitFile=RDFScape.getContextManager().getActiveContext().getSplitConditionsFileName();
		File splitConditionsFile=new File(splitFile);
		try {
			splitConditionsFile.createNewFile();
			FileWriter condWriter=new FileWriter(splitConditionsFile);
			condWriter.write(splitQuery);
			condWriter.close();
		} catch (Exception e) {
			System.out.println("Unable to read split conditions");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	

	

	
}
