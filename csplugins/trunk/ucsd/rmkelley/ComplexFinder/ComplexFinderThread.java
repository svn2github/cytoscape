package  ucsd.rmkelley.ComplexFinder;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import java.awt.Dimension;

class ComplexFinderThread extends Thread{
  double absent_score = .00001;
  double physical_beta = 0.9;
  double physical_logBeta = Math.log(physical_beta);
  double physical_logOneMinusBeta = Math.log(1-physical_beta);
  ComplexFinderOptions options;
  double [][] physicalScores;
  CyNetwork physicalNetwork;
  Vector results;

  public ComplexFinderThread(ComplexFinderOptions options){
    this.options = options;
  }

  public void setPhysicalNetwork(CyNetwork physicalNetwork){
    this.physicalNetwork = physicalNetwork;
  }

  public void loadPhysicalScores(File physicalFile){
    //set up the array of physical scores
    try{
      physicalScores = getScores(physicalFile,physicalNetwork);
    }catch(IOException io){
      throw new RuntimeException("Error loading physical score file");
    }
  }

  
  public void run(){
    //validate the user input
    if(physicalNetwork == null){
      throw new RuntimeException("No physical network");
    }
    
    results = new Vector();
    /*
     * Start a search from each individual protein in the 
     * physical network
     */
    int progress = 0;
    Iterator nodeIt = null;
    ProgressMonitor myMonitor = null;
    if(options.selectedSearch){
      nodeIt = physicalNetwork.getFlaggedNodes().iterator();
      myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,physicalNetwork.getFlaggedNodes().size());
    }
    else{
      nodeIt = physicalNetwork.nodesIterator();
      myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,physicalNetwork.getNodeCount());    
    }
    myMonitor.setMillisToPopup(0);
    while(nodeIt.hasNext()){
      System.err.println(""+progress);
      Node seedNode = (Node)nodeIt.next();
      /*
       * I need to correct this, the model starts
       * with one edge, and I should edit this
       * initial genetic score to reflect this
       * Don't need to check for the edge
       * because it already has to be there
       */
      double score = Double.NEGATIVE_INFINITY;
      
      /*
       * Initialize the set that represents 
       * the current members of the complex
       */
      Set members = new HashSet();
      members.add(seedNode);
      
      /*
       * Initialize the set that represents the neighbors in the 
       * physical network
       */
      Set neighbors = new HashSet();
      for(Iterator neighborIt = physicalNetwork.neighborsList(seedNode).iterator();neighborIt.hasNext();){
	Node neighbor = (Node)neighborIt.next();
	if(!members.contains(neighbor)){
	  neighbors.add(neighbor);
	}
      }
                 
      /*
       * Start searching through neighbors to improve
       * our model score
       */
      boolean improved = true;
      double significant_increase = 0;
      while(improved){
	improved = false;
	if(myMonitor.isCanceled()){
	  //throw new RuntimeException("Search cancelled");
	  break;
	}

	Node bestCandidate = null;
	double best_increase = Double.NEGATIVE_INFINITY;
	for(Iterator candidateIt = neighbors.iterator();candidateIt.hasNext();){
	  Node candidate = (Node)candidateIt.next();
	  if(members.contains(candidate)){
	    System.err.println("Don't know how this is happening");
	    System.exit(-1);
	  }
	  double this_increase = calculatePartialPhysicalIncrease(candidate,members);	  
	  	  
	  if(this_increase>best_increase && this_increase > significant_increase){
	    bestCandidate = candidate;
	    best_increase = this_increase;
	    improved = true;
	  }

	}
	if(improved){
	  score += best_increase;
	  members.add(bestCandidate);
	  for(Iterator neighborIt = physicalNetwork.neighborsList(bestCandidate).iterator();neighborIt.hasNext();){
	    Node neighbor = (Node)neighborIt.next();
	    if(!members.contains(neighbor)){
	      neighbors.add(neighbor);
	    }
	  }
	}
      }
      
      /*
       * Here we calculate the number of potential
       * edges for each type of edges (physical vs genetic
       */
      results.add(new NetworkModel(progress,
				   members,
				   score));
      if(myMonitor.isCanceled()){
	//throw new RuntimeException("Search cancelled");
	break;
      }
      myMonitor.setProgress(progress++);
    }
    myMonitor.close();
    Collections.sort(results);
    results = prune(results);
  }

  public Vector getResults(){
    return results;
  }

  protected Vector prune(Vector old){
    Vector results = new Vector();
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),"Pruning results",null,0,100);
    myMonitor.setMillisToPopup(50);
    int update_interval = (int)Math.ceil(old.size()/100.0);
    int count = 0;
    HashSet foundNodes = new HashSet();
    for(Iterator modelIt = old.iterator();modelIt.hasNext();){
      if(myMonitor.isCanceled()){
	throw new RuntimeException("Search cancelled");
      }
      if(count%update_interval == 0){
	myMonitor.setProgress(count/update_interval);
      }
      NetworkModel current = (NetworkModel)modelIt.next();
      boolean model_added = false;
      if(current.one.size() < 2 || current.score < options.cutoff){
	continue;
      }
      for(Iterator nodeIt = current.one.iterator();nodeIt.hasNext();){
	Object node = nodeIt.next();
	if(!foundNodes.contains(node)){
	  foundNodes.add(node);
	  if(!model_added){
	    results.add(current);
	    model_added = true;
	  }
	}
      }
    }
    myMonitor.close();
    return results;
  }



  /*
   * I'm thinking about changes this so that *candidates only
   * includes those nodes that don't currently belong to the
   * respective set
   */
  protected double calculatePhysicalIncrease(List sourceCandidates, Set sourceMembers){
    double result = 0;
    for(Iterator sourceCandidateIt = sourceCandidates.iterator();sourceCandidateIt.hasNext();){
      Node sourceCandidate = (Node)sourceCandidateIt.next();
      result += calculatePartialPhysicalIncrease(sourceCandidate, sourceMembers);
    }
    /*
     * If both nodes are new,then we also have to consider the edge
     * (that must be present) between the two new additions to the sources
     */
    result += calculatePartialPhysicalIncrease(sourceCandidates);
    return result;
  }
  
  protected double calculatePartialPhysicalIncrease(List members){
    double result = 0.0;
    for(int idx=0;idx<members.size()-1;idx++){
      Node source = (Node)members.get(idx);
      if(physicalNetwork.containsNode(source)){
	for(int idy = idx+1;idy<members.size();idy++){
	  Node target = (Node)members.get(idy);
	  if(physicalNetwork.containsNode(target)){
	    int one,two;
	    one = Math.max(physicalNetwork.getIndex(source),physicalNetwork.getIndex(target)) - 1;
	    two = Math.min(physicalNetwork.getIndex(source),physicalNetwork.getIndex(target)) - 1;
	    if(physicalNetwork.isNeighbor(source,target)){
	      result += physical_logBeta;
	      result -= Math.log(physicalScores[one][two]);
	    }
	    else{
	      result += physical_logOneMinusBeta;
	      result -= Math.log(1-physicalScores[one][two]);
	    }
	  }
	  else{
	    result += physical_logOneMinusBeta;
	    result -= Math.log(1-absent_score);
	  }
	}
      }
      else{
	result += (members.size()-idx-1)*(physical_logOneMinusBeta - Math.log(1-absent_score));
      }
    }
    return result;
  }
  
  protected double calculatePartialPhysicalIncrease(Node candidate, Set members){
    double result = 0;
    boolean candidatePresent = physicalNetwork.containsNode(candidate);
    if(candidatePresent){
      int candidateIndex = physicalNetwork.getIndex(candidate)-1;
      for(Iterator memberIt = members.iterator();memberIt.hasNext();){
	Node member = (Node)memberIt.next();
	if(physicalNetwork.containsNode(member)){
	  int memberIndex = physicalNetwork.getIndex(member)-1;
	  int one = Math.max(candidateIndex,memberIndex);
	  int two = Math.min(candidateIndex,memberIndex);
	  if(physicalNetwork.isNeighbor(candidate,member)){
	    result += physical_logBeta;
	    result -= Math.log(physicalScores[one][two]);
	  }
	  else{
	    result += physical_logOneMinusBeta;
	    result -= Math.log(1-physicalScores[one][two]);
	  }  
	}
	else{
	  result += physical_logOneMinusBeta;
	  result -= Math.log(1-absent_score);
	}
      }
    }
    else{
      result += members.size()*(physical_logOneMinusBeta - Math.log(1-absent_score));
    }

    return result;
  }

  public double [][] getScores(File scoreFile, CyNetwork cyNetwork) throws IOException{
    double [][] result = new double[cyNetwork.getNodeCount()][];
    String [] names = new String [cyNetwork.getNodeCount()];
    for(int idx=result.length-1;idx>-1;idx--){
      result[idx] = new double[idx];
    }
    
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),"Loading scores for "+cyNetwork.getTitle(),null,0,100);
    myMonitor.setMillisToDecideToPopup(50);
    int updateInterval = (int)Math.ceil(cyNetwork.getNodeCount()/100.0);
    
    BufferedReader reader = null;
    try{
      reader = new BufferedReader(new FileReader(scoreFile));
    }catch(Exception e){
      throw new RuntimeException("Error loading score file for "+cyNetwork.getTitle());
    }
    int line_number = 0;
    int progress = 0;
    String iterationString = reader.readLine();
    double iterations = (new Integer(iterationString)).doubleValue();

    while(reader.ready()){
      String line = reader.readLine();
      String [] splat = line.split("\t");
      if(line_number%updateInterval == 0){
	if(myMonitor.isCanceled()){
	  throw new RuntimeException("Score loading cancelled");
	}
	myMonitor.setProgress(progress++);
      }
      names[line_number++] = splat[0];
      if(splat.length != line_number){
	throw new RuntimeException("Score file in incorrect format");
      }
      int one;
      try{
	one = cyNetwork.getIndex(Cytoscape.getCyNode(splat[0]));
      }catch(Exception e){
	throw new RuntimeException("Score file contains protein ("+splat[0]+") not present in the network");
      }
      for(int idx=1;idx<splat.length;idx++){
	int two;
	try{
	  two = cyNetwork.getIndex(Cytoscape.getCyNode(names[idx-1]));
	}catch(Exception e){
	  throw new RuntimeException("Score file contains proteins ("+names[idx-1]+") not present in the network");
	}
	double probability = Integer.parseInt(splat[idx])/iterations;
	if(one < two){
	  result[two-1][one-1] = probability; 
	}
	else{
	  result[one-1][two-1] = probability;
	}
	
      }
    }
    myMonitor.close();
    if(line_number != cyNetwork.getNodeCount()){
      throw new RuntimeException("The number of proteins in the network and score file do not match");
    }
    
    return result;
  }
}

