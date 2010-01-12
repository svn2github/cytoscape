/**
 * Copyright 2006-2007 Andrea Splendiani
 * Released under GPL license
 */


package fr.pasteur.sysbio.rdfscape.cytomapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.ibm.icu.util.StringTokenizer;

import cytoscape.data.ExpressionData;
import cytoscape.data.mRNAMeasurement;
import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.Contextualizable;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.RDFScapeModuleInterface;
import fr.pasteur.sysbio.rdfscape.cytoscape.CytoscapeDealer;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryAnswerer;

/**
 * @author andrea@sgtp.net
 * CytoMapper manages the link between the ontology and the elements in Cytoscape.
 * CytoMapper has two "view" interfaces, one for the setting of options ({@link}CytoMapperOptionPanel),
 * and one for controlling its operation ({@link}CytoMapperPanel);
 * 
 * In a set of rules a set of conditions to match elements in the graph and elements in the ontology is stated.
 * 
 *
 * TODO Extend mapping to arbitrary attribute.
 * TODO Verify mapping
 * TODO clean and comment
 */
public class CytoMapper implements RDFScapeModuleInterface,Contextualizable{
	
	CytoscapeDealer cytoscapeDealer=null;
	CytoMapperPanel myPanel=null;
	CytoMapperOptionPanel myOptionPanel=null;
	//CytoMapperDataPanel	myDataPanel=null;
	Hashtable<String,MappingRule> mappingRuleList=null;			// TODO ?
	MapperTable mapperTable=null;			// TODO ?
	public int clashes=0;					// TODO ?
	public int totalURIs=0;					// TODO ?
	public int totalIDs=0;					// TODO ?
	public int totalMatchedID=0;			// TODO ?
	public int totalMatchedURI=0;			// TODO ?
	public int multi=0;						// TODO ?
	ExpressionData myData=null;				// TODO ?
	double[][] dataMatrix=null;				// TODO ?
	Hashtable<String,Integer> uriToDataIndex=null;		// TODO ?
	Hashtable conditionToDataMapping=null;	// TODO ?
	
	Hashtable<String,String> id2URI=null;					// TODO ?
	Hashtable<String,ArrayList<String>> uri2ID=null;					// TODO ?
	
	int numberOfConditions=0;
	int numberOfGenesWithData=0;
	int numberOfURIsWithData=0;
	int numberOfDataGenesInUse=0;
	
	/**
	 * Constructor: Generally throw an exception if needed resources are not present yet.
	 * This should never happen as the consuequence of run-time operations, but can only
	 * derive from a different "assembly" of Cytoscape modules.
	 * Note that the CytoMapper is designe to be used without graphical interfaces and possibly
	 * as an independent module.
	 * 
	 * @throws Exception Precondition for the constructio of CytoMapper are not met. 
	 * (Ideally this constraint should be verified at compile time... by flux analysis, in theory)
	 * 
	 */
	public CytoMapper() throws Exception {
		System.out.print("Init CytoMapper...");
		mapperTable=new MapperTable();
		if(RDFScape.getContextManager()==null) {
			throw new Exception("Unable to build CytoMapper : missing ContextManager");
		}
		
		if(RDFScape.getCytoscapeDealer()==null) {
			throw new Exception("Unable to build CytoMapper : missing Cytoscape Dealer");
		}
		System.out.print("prerequisites ok");
		RDFScape.getContextManager().addContextualizableElement(this);
		cytoscapeDealer=RDFScape.getCytoscapeDealer();
		initialize();
		System.out.println("CytoMapper init successful");
	}
	
	
	/**
	 * Initilize/build resources used by the CytoMapper.
	 */
	public boolean initialize() {
		mappingRuleList=new Hashtable<String, MappingRule>();
		uriToDataIndex=new Hashtable<String,Integer>();
		conditionToDataMapping=new Hashtable();
		if(myOptionPanel!=null) {
			myOptionPanel.reset();
			myOptionPanel.resetAfterKnowledgeBaseChange();
		}
		id2URI=new Hashtable();
		uri2ID=new Hashtable();					// TODO ?
		resetMapping();
		return true;
	}
	
	/**
	 * Reset resources "produced" by the action of the CytoMapper 
	 * (complement Initalize() in setting results of the operation of CytoMapper to default levels)
	 *
	 */
	public void resetMapping() {		
		id2URI=new Hashtable();
		uri2ID=new Hashtable();
		mapperTable.reset();
		clashes=0;
		totalURIs=0;
		totalIDs=0;
		totalMatchedID=0;
		totalMatchedURI=0;
		multi=0;
	}
	/**
	 * @return
	 */

	public CytoMapperOptionPanel getOptionPanel() {
		if(myOptionPanel==null) myOptionPanel=new CytoMapperOptionPanel(this);
		return myOptionPanel;
	}
	
	public CytoMapperPanel getCytoMapperPanel() {
		if(myPanel==null) myPanel=new CytoMapperPanel(this);
		return myPanel;
	}
	/*
	public CytoMapperDataPanel getCytoMapperDataPanel() {
		if(myPanel==null) myDataPanel=new CytoMapperDataPanel(this);
		return myDataPanel;
	}
	*/
	public void addMappingRule(MappingRule mappingRule) {
		// TODO should switch generic types
		mappingRuleList.put(mappingRule.name,mappingRule);
		myOptionPanel.refresh();
	}
	/**
	 * @param parse a Mapping Rule represented as a String from the Panel and adds the corresponding rule to
	 * the list of rules to be used in mapping.
	 */
	public void parseAndAddMappingRule(String fullRuleText) {
		String pattern=new String();
		String name=null;
		String uriVar=null;
		String idVar=null;
		//String idAtt=null;
		System.out.println("Parsing rule:\n"+fullRuleText);
		StringTokenizer myTokenizer=new StringTokenizer(fullRuleText);
		String token=null;
		while(myTokenizer.hasMoreTokens()) {
			token=myTokenizer.nextToken();
			if(token.equalsIgnoreCase("NAME:")) {
				name=myTokenizer.nextToken();
				
			}
			else if(token.equalsIgnoreCase("ID:")) {
				idVar=myTokenizer.nextToken();
			}
			/*
			else if(token.equalsIgnoreCase("IDATT:")) {
				idAtt=myTokenizer.nextToken();
			}
			*/
			else if(token.equalsIgnoreCase("URI:")) {
				uriVar=myTokenizer.nextToken();
			}
			else if(token.equalsIgnoreCase("PATTERN:")) {
				
				pattern=pattern.concat(myTokenizer.nextToken());
				
			}
			else pattern=pattern.concat(" "+token);
		}
		MappingRule myMappingRule=new MappingRule();
		myMappingRule.name=name;
		myMappingRule.patternText=pattern;
		myMappingRule.uriVar=uriVar;
		myMappingRule.idVar=idVar;
		if(myMappingRule.isValid()) {
			addMappingRule(myMappingRule);
			System.out.println("Got it\n");
		}
		else System.out.println("Didn't like it\n");
	}
	

	/**
	 * @return
	 */
	public Hashtable getGenesMapping() {
															
	    return uriToDataIndex;
		
		
		
		
	}

	/**
	 * @return
	 */
	public Hashtable getConditionMappings() {
		return conditionToDataMapping;
	}
	/**
	 * 
	 */
	public void resolve() {
		CommonMemory myMemory=RDFScape.getCommonMemory();
		resetMapping();
		for (Iterator iter = mappingRuleList.keySet().iterator(); iter.hasNext();) {
			String ruleName = (String) iter.next();
			System.out.println("rule : "+ruleName);
			MappingRule myRule=(MappingRule) mappingRuleList.get(ruleName);
			String RDQLquery="SELECT "+myRule.uriVar+" "+myRule.idVar+" \n WHERE "+
			myRule.patternText+"\n";
			String namespaceClause=new String();
			String[] namespaces=myMemory.getNamespaces();
			for (int i = 0; i < namespaces.length; i++) {
				String prefix=myMemory.getNamespacePrefix(namespaces[i]);
				if(prefix!=null) {
					if(!prefix.equalsIgnoreCase("")) {
						namespaceClause=namespaceClause+prefix+" for <"+namespaces[i]+">,\n";
					}
				}
			}
			if(namespaceClause.length()>1) {
				namespaceClause=namespaceClause.substring(0,namespaceClause.length()-2);
				RDQLquery=RDQLquery+"USING\n"+namespaceClause+"\n";
			}
			
			System.out.println(RDQLquery);
			AbstractQueryResultTable myResult=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(RDQLquery);
			
			analyze(myResult);
			mapperTable.add(myResult);
			mapperTable.fireTableDataChanged();
			RDFScape.mappingActionPerformed();
			
			
		}
		
	}
	private void analyze(AbstractQueryResultTable myResult) {
		for (int i = 0; i < myResult.getRowCount(); i++) {
			if(myResult.isURI(i,0) && myResult.isLiteral(i,1)) {
				String uri=myResult.getURI(i,0);
				String id=myResult.getDatatypeValue(i,1);
				ArrayList<String> myIDList= uri2ID.get(uri);
				if(myIDList==null) {
					myIDList=new ArrayList<String>();
					uri2ID.put(uri,myIDList);
				}
				else {
					if(!myIDList.contains(id))
						multi++;
				}
				myIDList.add(id);
				
				if(id2URI.get(id)==null) {
					id2URI.put(id,uri);
				}
				else {
					if(!((String)id2URI.get(id)).equals(uri)) {
						clashes++;
						System.out.println("Clash for:\n "+id+" "+uri+"\nVs");
						System.out.println(id+" "+(String)id2URI.get(id)+"\n");
					}
				}
			}
		}
		totalIDs=id2URI.keySet().size();
		totalURIs=uri2ID.keySet().size();
		
	}
/**
 * 
 */
	private void analyzeExtended(AbstractQueryResultTable myResult) {
		System.out.println("Analyzing matchings");
		for (int i = 0; i < myResult.getRowCount(); i++) {
			System.out.print(i+ ":");
			if(myResult.isURI(i, 0)) {
				String uri=myResult.getURI(i,0);
				System.out.print(uri+"->");
				String id=null;
				if(myResult.isLiteral(i,1)) {
					id=myResult.getDatatypeValue(i,1);
					System.out.print(id+"(l)");
				}
				else if(myResult.isURI(i, 0)) {
					id=myResult.getURI(i,1);
					System.out.print(id);
				}
				else {
					System.out.println("X");
					break;
				}
				
				/**
				 * TODO here we must generalize strategy to more attribute types
				 */
				ArrayList<String> myIDList= uri2ID.get(uri);
				if(myIDList==null) {
					myIDList=new ArrayList<String>();
					uri2ID.put(uri,myIDList);
				}
				else {
					if(!myIDList.contains(id))
						multi++;
				}
				myIDList.add(id);
				
				if(id2URI.get(id)==null) {
					id2URI.put(id,uri);
				}
				else {
					if(!((String)id2URI.get(id)).equals(uri)) {
						clashes++;
						System.out.println("Clash for:\n "+id+" "+uri+"\nVs");
						System.out.println(id+" "+(String)id2URI.get(id)+"\n");
					}
				}
			}
		}
		totalIDs=id2URI.keySet().size();
		totalURIs=uri2ID.keySet().size();
		
	}

	/**
	 * Maps URIs to Cytoscape following the correspondence in id2URI
	 */
	public void map() {
		System.out.println("Map");
		//myPanel.initOntology2GraphBars(uri2ID.keySet().size(),cytoscapeDealer.getNodeCount() );
		
		int[] mapResult=cytoscapeDealer.mapNodes(id2URI);
		totalMatchedID=mapResult[0];
		totalMatchedURI=mapResult[1];
		//myPanel.graphCoverage.setValue(mapResult[0]);
		//myPanel.ontologyCoverage.setValue(mapResult[1]);
		/*
		HashSet usedURIs=new HashSet();
		int mapped=0;
		for (Iterator iter = mapperTable.idToURI.keySet().iterator(); iter.hasNext();) {
			String tempID = (String) iter.next();
			ArrayList tempArray =(ArrayList) mapperTable.idToURI.get(tempID);
			if(cytoscapeDealer.mapURI(tempID,(String) tempArray.get(0))) {
				mapped++;
				for (Iterator temp = tempArray.iterator(); temp.hasNext();) {
					String element = (String) temp.next();
					usedURIs.add(element);
					
				}
				myPanel.graphCoverage.setValue(mapped);
				myPanel.ontologyCoverage.setValue(usedURIs.size());
			}
			
		}*/
		
		
	}

	public boolean preLinkData() {
		myData= cytoscapeDealer.getExpressionData();
		if(myData==null) {
			System.out.println("No microarray data present");
			RDFScape.warn("Load microarray data first!");
			return false;
		}
		numberOfConditions=myData.getNumberOfConditions();
		System.out.println("Number of conditions for expression data: "+numberOfConditions);
		numberOfGenesWithData=myData.getNumberOfGenes();
		System.out.println("Total number of \"genes\" with associated data: "+myData.getNumberOfGenes());
		System.out.println("I have a "+myData.getNumberOfGenes()+" x "+myData.getNumberOfConditions()+" matrix in mind");
		return true;
	}
	
	/**
	 * 
	 */
	public void linkData() {
		//First, counting...
		numberOfURIsWithData=0;
		HashSet<String> uriWithValues=new HashSet<String>();
		for (Iterator<String> iter = uri2ID.keySet().iterator(); iter.hasNext();) {
			String tempURI =  iter.next();
			ArrayList<String> tempList= uri2ID.get(tempURI);	
			if(tempList.size()>1) {
				System.out.println("More than one Cytoscape Node for URI: "+tempURI+", considering only an arbitrary one");
			}
			if(tempList.size()==0) {
				System.out.println("No IDs for URI: "+tempURI+", this should not happen here!");
			}
			String tempNodeID=tempList.get(0);
			Vector myMeasures=myData.getMeasurements(tempNodeID);
			if(myMeasures==null) System.out.println("No measures found");
			else {
				boolean good=true;
				for (Iterator iterator = myMeasures.iterator(); iterator
						.hasNext();) {
					mRNAMeasurement tempMeasure = (mRNAMeasurement) iterator.next();
					if(tempMeasure==null) good=false;
					else{
						if(tempMeasure.toString().equalsIgnoreCase("")) good=false;
					}
				}
				if(good) uriWithValues.add(tempURI);
				
			}
		}
			
			
		System.out.println("Filling matrix "+uriWithValues.size()+" x "+numberOfConditions);
		dataMatrix=new double[uriWithValues.size()][numberOfConditions];
		int i=0;
		int j=0;
		
		
		for (Iterator<String> iter = uriWithValues.iterator(); iter.hasNext();) {
			String tempURI = iter.next();
			ArrayList<String> tempList= uri2ID.get(tempURI);
			String tempID=tempList.get(0);
			Vector myMeasures=myData.getMeasurements(tempID);
			System.out.println(tempURI+" -> "+tempID+" #"+myMeasures.size());
			uriToDataIndex.put(tempURI,new Integer(i));
			j=0;
			for (Iterator iterator = myMeasures.iterator(); iterator
						.hasNext();) {
				mRNAMeasurement tempMeasure = (mRNAMeasurement) iterator.next();
				dataMatrix[i][j]=tempMeasure.getRatio();
				j++;
			}
			i++;
		}
		numberOfURIsWithData=uriWithValues.size();
		numberOfDataGenesInUse=uriWithValues.size();
		
		System.out.println("TEST:");
		for (int k = 0; k < dataMatrix.length; k++) {
			for (int l = 0; l < dataMatrix[k].length; l++) {
				System.out.print(dataMatrix[k][l]+"\t");
			}
			System.out.println();
		}
		
		
		
	}
	/**
	 * @return
	 */
	public double[][] getDataMatrix() {
		return dataMatrix;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#reset()
	 */
	public void reset() {
		// this is to be defined. Now it is considered as initialize()
		initialize();
		
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#update()
	 */
	public void update() {
		// don-t know!!! //TODO
		
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#validate()
	 */
	public boolean isInValidState() {
		
		return canOperate();
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#checkPreconditions()
	 */
	public boolean canOperate() {
		// TODO must have a graph and a knowledge base, possibly data...
		return true;
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#loadFromContext()
	 */
	public boolean loadFromActiveContext() {
		initialize();
		int counter = 0;
		String myFileName=RDFScape.getContextManager().getActiveContext().getDefaultMapDirName();
		File rulesDir=new File(myFileName);
		if(!rulesDir.isDirectory()) {
			System.out.println("Unable to locate mapping rules");
			return false;
		}
		try {
		  File tempMapRuleFile=null;
		  File[] mapRuleFiles=rulesDir.listFiles();
		  for (int i = 0; i < mapRuleFiles.length; i++) {
		  	System.out.println("Reading mapping rule : "+mapRuleFiles[i].getName());
		  	 Reader reader = new BufferedReader(new FileReader(mapRuleFiles[i]));
	            StringBuffer resultsBuffer = new StringBuffer();
	            char[] buffer = new char[1024];
	            for (int charsRead = 0; (charsRead = reader.read(buffer)) >= 0; )
	            {
	                resultsBuffer.append(buffer, 0, charsRead);
	            }
	            
	            String result = resultsBuffer.toString();
		  	
	            if(mapRuleFiles[i].isFile()) parseAndAddMappingRule( result);
		  }
		  
		  
		}
		catch(IOException ioe)
		{
		  System.out.println("Unable to load mapping rules");
		  return false;
		}
		return true;
		
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#saveToContext()
	 */
	public boolean saveToContext() {
		System.out.println("Saving...");
		int counter = 0;
		String myFileName=RDFScape.getContextManager().getActiveContext().getDefaultMapDirName();
		File rulesDir=new File(myFileName);
		if(!rulesDir.isDirectory()) {
			System.out.println("Unable to locate mapping rules");
			return false;
		}
		File[] myFiles=rulesDir.listFiles();
		for (int i = 0; i < myFiles.length; i++) {
			myFiles[i].delete();
		}
		Enumeration ruleNames=mappingRuleList.keys();
		while(ruleNames.hasMoreElements()) {
			String tempRuleName=(String)ruleNames.nextElement();
		  	System.out.println("Going to save: "+tempRuleName);
			MappingRule tempRule=(MappingRule)mappingRuleList.get(tempRuleName);
		  	File tempMapRuleFile=new File(myFileName+"/"+tempRuleName);
		  	try {
		  		FileWriter tempWriter=new FileWriter(tempMapRuleFile);
		  		tempWriter.write("NAME: "+tempRule.getName()+"\n"+
		  				"ID: "+tempRule.getID()+"\n"+
						"URI: "+tempRule.getURI()+"\n"+
						"PATTERN: "+tempRule.getRuleString());
		  		tempWriter.close();
		  	} catch (Exception e) {
				System.out.println("Unable to save mapping rule to file "+myFileName+"/"+tempRuleName);
				return false;
		  	}
		  
		}
		  
		  
		
		return true;
	}
	

	public boolean canResolve() {
		return KnowledgeWrapper.hasRDQLSupport(RDFScape.getKnowledgeEngine());
	}

	public int getNumberOfMatchedIDs() {
		return totalMatchedID;
	}
	public int getNumberOfMatchedURIs() {
		return totalMatchedURI;
	}
	
	/**
	 * @return the number of URIs in memory (these have been "identified" as resources,
	 * for instance in a CytoMapper rule)
	 */
	public int getNumberOfURIsToBeMatched() {
		return totalURIs;
	}
	
	/**
	 * @return the number of nodes in the graph
	 */
	public int getNumberOfNodesToBeMatched() {
		return RDFScape.getCytoscapeDealer().getNodeCount();
	}
	public void touch() {
		// TODO Auto-generated method stub
		
	}
	
	public int getNumberOfMappingRules() {
		return mappingRuleList.size();
	}


	public int getNumberOfAvailableData() {
		return numberOfGenesWithData;
	}


	public int getNumberOfURIWithData() {
		return numberOfURIsWithData;
	}


	public int getNumberOfDataExtracted() {
		return numberOfDataGenesInUse;
	}


	public ArrayList<String> getCytoscapeIDsForURI(String uri) {
		ArrayList<String> result=new ArrayList<String>();
			if(uri2ID==null) return result;
			ArrayList idList=uri2ID.get(uri);
			if(idList==null) return result;
			if(idList.size()==0) return result;
		
		return idList;
	}


	
	
	
	
	
	
}
