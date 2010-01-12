/**
 * Copyright 2006-2008 Andrea Splendiani
 * Released under GPL license
 */

/*
 * 
 *
 * TODO To comment
 */
package fr.pasteur.sysbio.rdfscape.patterns;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;

import fr.pasteur.sysbio.rdfscape.Contextualizable;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.cytoscape.CytoscapeDealer;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryAnswerer;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PatternManager implements Contextualizable {
	
	Hashtable<String, PatternElement> myPatterns=null;
	PatternManagerPanel myPanel=null;
	
	
	/**
	 * @throws Exception 
	 * 
	 */
	public PatternManager() throws Exception {
		System.out.print("\tPatternManager... ");
		if(RDFScape.getContextManager()==null) {
			throw new Exception("Cannot build Pattern Manager : missing ContextManager");
		}
		//myRDFScapeInstance=rs;
		System.out.println("Building Pattern Manager");
		initialize();
		myPanel=new PatternManagerPanel(this);
		
		//myKnowledge=kw;
		
		RDFScape.getContextManager().addContextualizableElement(this);
		System.out.println("Ok");
	}
	
	public boolean initialize() {
		myPatterns=new Hashtable<String, PatternElement>();
		return true;
	}

	public void reset() {
		java.util.Enumeration<String> patSet=myPatterns.keys();
		while(patSet.hasMoreElements()) {
			myPanel.removePatternElementPanel(myPatterns.get(patSet.nextElement()));
		}
		myPatterns=new Hashtable<String, PatternElement>();
		
	}

	public boolean canOperate() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public PatternElement makePatternOutOfCurrentGraph(String patternName) {
		CytoscapeDealer myCytoscapeDealer=RDFScape.getCytoscapeDealer();
		PatternElement myPattern=new PatternElement(patternName,this);
		myPatterns.put(patternName,myPattern);
		//myPattern.setPatternManager(this);
		Image image=RDFScape.getCytoscapeDealer().getScreenshot(300,300);
		myPattern.setMyImage(image);
		//myPattern.setType(myRDFScapeInstance.getCytoscapeDealer().getCurrentNetworkType());
		//get content from Cytoscape (RDF code)
		String[][] tripleListWithVariables=myCytoscapeDealer.getTripleWithVariablesArrayList();
		String[][] filterConditions=myCytoscapeDealer.getFilterConditionsArray();
		//ArrayList patterns=myRDFScapeInstance.getCytoscapeDealer().getPatternsCollection();
		myPattern.addTriples(tripleListWithVariables);
		myPattern.addFilterConditions(filterConditions);
		
		//myPattern.addPatternCollection(patterns);
		//myPattern.setNamespaceClause(myRDFScapeInstance.getRDQLQueryManager().getQueryNameSpacesClause());
		//add content in separate parts
		//add namespace clause
		
		
		
		return myPattern;
	}
	
	
	
	public PatternManagerPanel getPatternManagerPanel() {
		if(myPanel==null) myPanel=new  PatternManagerPanel(this);
		return myPanel;
	}

	

	
	/**
	 * @param selection
	 * @return
	 */
	public PatternElement getPatternElement(String selection) {
		return myPatterns.get(selection);
	}

	/**
	 * 
	 */
	public void getSnapshot(String s) {
		PatternElement myPatternElement=makePatternOutOfCurrentGraph(s);
		if(myPanel!=null) myPanel.addPatternElementPanel(myPatternElement);
	}

	/**
	 * @param query
	 * @return
	 */
	/*
	public PatternMatchedTable makeRDQLQuery(String query) {
		//TODO to revise
		//return myRDFScapeInstance.getRDQLQueryManager().makeRDQLRichQuery(query);
		return null;
	}
	*/
	/*
	public void addStatementToGraph(String[] triple, String type) {
		// TODO to fix
		//RDFResourceWrapper source=myRDFScapeInstance.getMyRDFWrappersFactory().makeRDFResourceWrapper(triple[0]);
		//RDFResourceWrapper property=myRDFScapeInstance.getMyRDFWrappersFactory().makeRDFResourceWrapper(triple[1]);
		//RDFResourceWrapper target=myRDFScapeInstance.getMyRDFWrappersFactory().makeRDFResourceWrapper(triple[2]);	
		//myRDFScapeInstance.getCytoscapeDealer().addPatternNode(source);
		//myRDFScapeInstance.getCytoscapeDealer().addPatternNode(target);
		//myRDFScapeInstance.getCytoscapeDealer().addPatternEdge(source,property,target);
	}
	*/
	/**
	 * @param triple
	 */
	/*
	public void searchStatementInGraph(String[] triple) {
		// TODO to revise
		
		myRDFScapeInstance.getCytoscapeDealer().searchNode(triple[0]);
		myRDFScapeInstance.getCytoscapeDealer().searchNode(triple[2]);
		myRDFScapeInstance.getCytoscapeDealer().searchEdge(triple[1]);
		
	}
	*/
	/**
	 * @param text
	 * @param myMatchedResultTable
	 * @param valueAt
	 */
	public void makeRuleOutOf(String ruleText, AbstractQueryResultTable myMatchedResultTable, String bite) {
		System.out.println("Adding rule...");
		Hashtable<String,Integer> varIndexes=new Hashtable<String,Integer>();
		for (int i = 0; i < myMatchedResultTable.getColumnCount(); i++) {
			varIndexes.put("?"+myMatchedResultTable.getColumnName(i),new Integer(i));
			System.out.println("index :"+myMatchedResultTable.getColumnName(i)+"->"+i);
			System.out.println(i);
		}
		
		int[] indexes=myMatchedResultTable.getBiteIndexes(bite);
		System.out.println("Patterns # :"+indexes.length);
		for (int i = 0; i < indexes.length; i++) {
			for (Iterator iter = varIndexes.keySet().iterator(); iter.hasNext();) {
				String variableName = (String) iter.next();
				System.out.println("Subst. var "+variableName);
				ruleText=ruleText.replaceAll("\\"+variableName,(String)myMatchedResultTable.getValueAt(indexes[i],(varIndexes.get(variableName)).intValue()));
				System.out.println(ruleText);
			}
		}
		
		System.out.println("Rule "+ruleText);
		//TODO to revise
		//myRDFScapeInstance.getReasonerManager().addRuleObject(new InfRuleObject(ruleText));
			
		
		
		
	}

	/**
	 * @param text
	 * @param patternMatchedTable
	 * @param string
	 */
	public void makePatternOutOf(String patternText, AbstractQueryResultTable myMatchedResultTable, String bite) {
		System.out.println("Adding pattern...");
		Hashtable<String,Integer> varIndexes=new Hashtable<String,Integer>();
		for (int i = 0; i < myMatchedResultTable.getColumnCount(); i++) {
			varIndexes.put("?"+myMatchedResultTable.getColumnName(i),new Integer(i));
			System.out.println("index :"+myMatchedResultTable.getColumnName(i)+"->"+i);
			System.out.println(i);
		}
		
		int[] indexes=myMatchedResultTable.getBiteIndexes(bite);
		System.out.println("Patterns # :"+indexes.length);
		for (int i = 0; i < indexes.length; i++) {
			String tempPatternText=patternText;
			while(tempPatternText.indexOf("(")>=0) {
				String currentPattern=tempPatternText.substring(tempPatternText.indexOf("(")+1,tempPatternText.indexOf(")"));
				tempPatternText=tempPatternText.substring(tempPatternText.indexOf(")")+1);
				System.out.println("current pattern: "+currentPattern);
				System.out.println("rest: "+tempPatternText);
				String[] triple=currentPattern.split("[ ]+");
				if(triple.length!=3) {
					System.out.println("Not a valid pattern...");
					break;
				}
				System.out.println("+"+triple[0]+"+"+triple[1]+"+"+triple[2]+"+");
				String source=triple[0].trim();
				String predicate=triple[1].trim();
				String object=triple[2].trim();
				if(source.indexOf("?")>=0) { 
					if(varIndexes.get(source)!=null)
						source=(String)myMatchedResultTable.getValueAt(indexes[i],(varIndexes.get(source)).intValue());
				}
				else if(source.indexOf(":")>=0) {
					//source=myRDFScapeInstance.getNameSpaceManager().getNameSpaceFromPrefix(source.substring(0,source.indexOf(":")))+source.substring(source.indexOf(":")+1);
				}
				if(predicate.indexOf("?")>=0) {
					if(varIndexes.get(predicate)!=null)
						predicate=(String)myMatchedResultTable.getValueAt(indexes[i],(varIndexes.get(predicate)).intValue());
				} else if(predicate.indexOf(":")>=0) {
					//predicate=myRDFScapeInstance.getNameSpaceManager().getNameSpaceFromPrefix(predicate.substring(0,predicate.indexOf(":")))+predicate.substring(predicate.indexOf(":")+1);
				}
				if(object.indexOf("?")>=0) {
					if(varIndexes.get(object)!=null)
						object=(String)myMatchedResultTable.getValueAt(indexes[i],(varIndexes.get(object)).intValue());
				}
				else if(object.indexOf(":")>=0) {
					//object=myRDFScapeInstance.getNameSpaceManager().getNameSpaceFromPrefix(object.substring(0,object.indexOf(":")))+object.substring(object.indexOf(":")+1);
				}
			
				System.out.println("Source : "+source);
				System.out.println("Predicate : "+predicate);
				System.out.println("Object : "+object);
				/* TODO to fix
				myRDFScapeInstance.myBasicKnowledge.createStatement(
						myRDFScapeInstance.myBasicKnowledge.createResource(source),
						myRDFScapeInstance.myBasicKnowledge.createProperty(predicate),
						myRDFScapeInstance.myBasicKnowledge.createResource(object)
				);
				*/
			}
		}	
	}
	public boolean canAddAndSearchPatterns() {
		if(KnowledgeWrapper.hasRDQLSupport(RDFScape.getKnowledgeEngine())) return true;
		else return false;
	}

	public String makeRDQLQueryFromPattern(PatternElement pe) {
		String[] vars=pe.getVariables();
		String[][] triples=pe.getTriples();
		Hashtable filterConditions=pe.getFilterConditions();
		Hashtable preciseFilterConditions=pe.getPreciseFilterConditions();
		String query=new String("SELECT ");
		for (int i = 0; i < vars.length; i++) {
			query=query+" "+vars[i];
		}
		query=query+"\n WHERE ";
		
		for (int i = 0; i < triples.length; i++) {
			query=query+"(";
			for (int j = 0; j < triples[i].length; j++) {
				if(triples[i][j].indexOf("?")==0) query=query+" "+triples[i][j]+" ";
				else query=query+" <"+triples[i][j]+"> ";
			}
			query=query+")\n";
			
		}
		if(filterConditions.size()>0 || preciseFilterConditions.size()>0) {
			for (int j = 0; j < vars.length; j++) {
				if(filterConditions.get(vars[j])!=null)
					query=query+"AND "+vars[j]+" =~ /"+filterConditions.get(vars[j])+"/\n";
				if(preciseFilterConditions.get(vars[j])!=null)
					query=query+"AND "+vars[j]+" eq \""+preciseFilterConditions.get(vars[j])+"\"\n";
			}
		}
		
		System.out.println(query);
		return query;
	}
	public AbstractQueryResultTable searchVariable(PatternElement pattern) {
		AbstractQueryResultTable myResult=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(makeRDQLQueryFromPattern(pattern));
		RDFScape.getCytoscapeDealer().searchURIs(myResult);
		return myResult;
	}
	public AbstractQueryResultTable addVariable(PatternElement pattern) {
		AbstractQueryResultTable myResult=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(makeRDQLQueryFromPattern(pattern));
		if(myResult.getRowCount()>0) RDFScape.getCytoscapeDealer().addURIs(myResult);
		return myResult;
	}
	public AbstractQueryResultTable searchPattern(PatternElement pattern) {
		AbstractQueryResultTable myResult=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(makeRDQLQueryFromPattern(pattern));
		myResult=expandVariables2Pattern(myResult,pattern);
		RDFScape.getCytoscapeDealer().searchPattern(myResult);
		return myResult;
	}
	public AbstractQueryResultTable addPattern(PatternElement pattern) {
		AbstractQueryResultTable myResult=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(makeRDQLQueryFromPattern(pattern));
		myResult=expandVariables2Pattern(myResult,pattern);
		if(myResult.getRowCount()>0) {
			RDFScape.getCytoscapeDealer().addPattern(myResult);
			RDFScape.patternListChanged();
		}
		return myResult;
	}
	public void remove(PatternElement element) {
		
		
		myPatterns.remove(element.getName());
		myPanel.removePatternElementPanel(element);
		RDFScape.patternListChanged();
		
	}
	public boolean loadFromActiveContext() {
		if(myPanel!=null) myPanel.removeAll();
		System.out.println("Loading patterns");
		String myPatternsDirName=RDFScape.getContextManager().getActiveContext().getPatternsDirName();
		System.out.println("From : "+myPatternsDirName);
		File mySuperPatternDir=new File(myPatternsDirName);
		if(!mySuperPatternDir.exists()) return false;
		if(!mySuperPatternDir.isDirectory()) return false;
		File[] myPatternsDir=mySuperPatternDir.listFiles();
		for (int i = 0; i < myPatternsDir.length; i++) {
			if(myPatternsDir[i].isDirectory()) {
				String patternName=myPatternsDir[i].getName();
				System.out.println("Found "+patternName);
				
				File imageFile=new File(myPatternsDir[i].getAbsoluteFile()+"/image.jpg");
				File triplesFile=new File(myPatternsDir[i].getAbsoluteFile()+"/triples");
				File settingsFile=new File(myPatternsDir[i].getAbsoluteFile()+"/settings");
				File functionFile=new File(myPatternsDir[i].getAbsoluteFile()+"/functions");
				File filtersFile=new File(myPatternsDir[i].getAbsoluteFile()+"/filters");
				/**
				 * reading image
				 */
				PatternElement myPatternElement=new PatternElement(patternName,this);
				try {
					myPatternElement.setMyImage(ImageIO.read(new FileImageInputStream(imageFile)));
				} catch (FileNotFoundException e) {
					System.out.println("Unable to find pattern image file");
					e.printStackTrace();
					//return false;
				} catch (IOException e) {
					System.out.println("Unable to read pattern image file");
					e.printStackTrace();
					//return false;
				}
				
				/**
				 * reading triples
				 */
				String[][] patternTriples=new String[0][3];
				try {
					BufferedReader inFile = new BufferedReader(new FileReader(triplesFile));
					String line = inFile.readLine();
					ArrayList<String[]> triplesArray=new ArrayList<String[]>();
					while(line!=null) {
						String[] currentTriple=new String[3];
						StringTokenizer st=new StringTokenizer(line);
						currentTriple[0]=st.nextToken();
						currentTriple[1]=st.nextToken();
						currentTriple[2]=st.nextToken();
						triplesArray.add(currentTriple);
						line=inFile.readLine();
					}
					myPatternElement.addTriples(triplesArray.toArray(patternTriples));
				} catch (Exception e) {
					System.out.println("Unable to read patterns");
					return false;
				}
				/**
				 * reading function
				 */
				
				//nothing yet
				
				/**
				 * reading settings
				 */
				//nothing yet
				
				
				/**
				 * reading filters
				 */
				ArrayList<String[]> filterConditionsList=new ArrayList<String[]>();
				//ArrayList preciseFilterConditionsList=new ArrayList();
				String[][] filterArray=new String[0][3];
				try {
					BufferedReader inFile = new BufferedReader(new FileReader(filtersFile));
					String line = inFile.readLine();
					while(line!=null) {
						String[] filterElement=new String[3];
						StringTokenizer st=new StringTokenizer(line);
						if(st.hasMoreTokens()) {
							filterElement[0]=st.nextToken();
						}
						else break;
						if(st.hasMoreTokens()) {
							filterElement[1]=st.nextToken().replace('~',' ');
						}
						else break;
						if(st.hasMoreTokens()) {
							filterElement[2]=st.nextToken();
						}
						else break;
						filterConditionsList.add(filterElement);
						
						line=inFile.readLine();
					}
					
				} catch (Exception e) {
					System.out.println("Unable to read patterns");
					return false;
				}
				myPatternElement.addFilterConditions(filterConditionsList.toArray(filterArray));
				myPatterns.put(patternName,myPatternElement);
				if(myPanel!=null) myPanel.addPatternElementPanel(myPatternElement);
				
			}		
			
		}
		
		/*
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
					String value=st.nextToken();
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
		*/
		return true;
	
	}
	public boolean saveToContext() {
		String myDirName=RDFScape.getContextManager().getActiveContext().getPatternsDirName();
		System.out.println("save to context "+myDirName);
		System.out.println("Cleaning...");
		File myDir=new File(myDirName);
		File[] myPatternFiles=myDir.listFiles();
		for (int i = 0; i < myPatternFiles.length; i++) {
			if(myPatternFiles[i].isDirectory()) {
				String name=myPatternFiles[i].getName();
				System.out.println("Already found "+name);
				if(!myPatterns.keySet().contains(name) || myPatterns.get(name)==null) {
					System.out.println("Removing "+name);
					File imageFile=new File(myPatternFiles[i].getAbsoluteFile()+"/image.jpg");
					File triplesFile=new File(myPatternFiles[i].getAbsoluteFile()+"/triples");
					File settingsFile=new File(myPatternFiles[i].getAbsoluteFile()+"/settings");
					File functionFile=new File(myPatternFiles[i].getAbsoluteFile()+"/functions");
					File filtersFile=new File(myPatternFiles[i].getAbsoluteFile()+"/filters");
					
						
					if(imageFile.exists()) imageFile.delete();
					if(triplesFile.exists()) triplesFile.delete();
					if(settingsFile.exists()) settingsFile.delete();
					if(functionFile.exists()) functionFile.delete();
					if(filtersFile.exists()) filtersFile.delete();
				}
				myPatternFiles[i].delete();
			}
		}
		
		
		
		for (Iterator<String> iter = myPatterns.keySet().iterator(); iter.hasNext();) {
			System.out.print("Saving pattern ");
			String patternName = iter.next();
			System.out.println(patternName);
			PatternElement currentPattern=myPatterns.get(patternName);
			String patternDir=myDirName+"/"+currentPattern.getName();
			System.out.println("to : "+patternDir);
			File dir=new File(patternDir);
			File imageFile=new File(patternDir+"/image.jpg");
			File triplesFile=new File(patternDir+"/triples");
			File settingsFile=new File(patternDir+"/settings");
			File functionFile=new File(patternDir+"/functions");
			File filtersFile=new File(patternDir+"/filters");
			
			try {
				dir.mkdir();
				imageFile.createNewFile();
				triplesFile.createNewFile();
				settingsFile.createNewFile();
				functionFile.createNewFile();
				filtersFile.createNewFile();
			} catch (Exception e) {
				System.out.println("Unable to create a pattern file out of :");
				System.out.println(patternDir+"/image.jpg");
				System.out.println(patternDir+"/triples");
				System.out.println(patternDir+"/settings");
				System.out.println(patternDir+"/functions");
				System.out.println(patternDir+"/filters");
				return false;
			}
			/**
			 * Writing triples
			 */
			try {
				FileWriter triplesWriter=new FileWriter(triplesFile);
				String[][] myTriples=currentPattern.getTriples();
				for (int i = 0; i < myTriples.length; i++) {
					for (int j = 0; j < myTriples[0].length; j++) {
						triplesWriter.write(myTriples[i][j]+"\t");
					}
					triplesWriter.write("\n");
				}
				triplesWriter.close();
			} catch (Exception e) {
				System.out.println("Unable to write triples");
				e.printStackTrace();
				return false;
			}
			
			/**
			 * Writing image
			 */
			
			Image myImage=currentPattern.getImage();
			BufferedImage myBufferedImage=new BufferedImage(myImage.getWidth(null),myImage.getHeight(null),BufferedImage.TYPE_INT_RGB);
			Graphics2D go=myBufferedImage.createGraphics();
			go.setColor(Color.LIGHT_GRAY);
			go.fillRect(0,0,myImage.getWidth(null),myImage.getHeight(null));
			go.drawImage(myImage,0,0,null);
			go.dispose();
			
			try {
				ImageIO.write(myBufferedImage,"jpg",imageFile);
				
			} catch (IOException e1) {
				System.out.println("Unable to write image");
				e1.printStackTrace();
				return false;
			}
			/**
			 * writing function
			 */
			// nothing now
			
			FileWriter functionWriter;
			try {
				functionWriter = new FileWriter(functionFile);
				functionWriter.close();
			} catch (IOException e) {
				System.out.println("Unable to write function");
				e.printStackTrace();
				return false;
			}
			
			
			/**
			 * writing settings
			 */
			// nothing now
			FileWriter settingsWriter;
			try {
				settingsWriter = new FileWriter(settingsFile);
				settingsWriter.close();
			} catch (IOException e) {
				System.out.println("Unable to write settings");
				e.printStackTrace();	
				return false;
			}
			
			
			
			/**
			 * writing filters
			 */
			FileWriter filtersWriter;
			try {
				filtersWriter=new FileWriter(filtersFile);
				Hashtable filters=(Hashtable)currentPattern.getFilterConditions();
				Hashtable preciseFilters=(Hashtable)currentPattern.getPreciseFilterConditions();
				
				System.out.println("Filters "+filters.size());
				System.out.println("Precise "+preciseFilters.size());
				Iterator keys=filters.keySet().iterator();
				while(keys.hasNext()) {
					System.out.println("A");
					String currentKey=(String)keys.next();
					String entry=(String)filters.get(currentKey);
					filtersWriter.write(currentKey+"\t"+entry.replace(' ','~')+"\tF\n");
				}
				
				
				keys=preciseFilters.keySet().iterator();
				while(keys.hasNext()) {
					System.out.println("B1");
					String currentKey=(String)keys.next();
					System.out.println("B2");
					String entry=(String)preciseFilters.get(currentKey);
					System.out.println("B3");
					filtersWriter.write(currentKey+"\t"+entry.replace(' ','~')+"\tP\n");
					System.out.println("B4");
				}
				
				filtersWriter.close();
			} catch (Exception e) {
				System.out.println("Unable to write filters");
				e.printStackTrace();
				return false;
			}
			
			
		}
		return true;
	}
	
	private AbstractQueryResultTable expandVariables2Pattern(AbstractQueryResultTable myTable, PatternElement myPattern) {
		return myTable.getExpandedInPattern(myPattern);
		
		
	}
	public int getNumberOfPatterns() {
		return myPatterns.size();
	}


	
		
	
}
