package  ucsd.rmkelley.BetweenPathway;
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

class BetweenPathwayThread extends Thread{
  double absent_score = .00001;
  double logBeta = Math.log(0.9);
  double logOneMinusBeta = Math.log(0.1);
  BetweenPathwayOptions options;

  public BetweenPathwayThread(BetweenPathwayOptions options){
    this.options = options;
  }

  public void run(){
    System.err.println("Between Pathway Thread started");
    //number of physical interactions allowed between pathways
    int cross_count_limit = 1;
    //get the two networks which will be used for the search
    CyNetwork physicalNetwork = options.physicalNetwork;
    CyNetwork geneticNetwork = options.geneticNetwork;

    //validate the user input
    if(physicalNetwork == null){
      throw new RuntimeException("No physical network");
    }
    if(geneticNetwork == null){
      throw new RuntimeException("No genetic network");
    }


    //set up the array of genetic scores
    double [][] geneticScores = null,physicalScores = null;
    try{
      geneticScores = getScores(options.geneticScores,geneticNetwork);
    }catch(IOException io){
      throw new RuntimeException("Error loading genetic score file");
    }
    try{
      physicalScores = getScores(options.physicalScores,physicalNetwork);
    }catch(IOException io){
      throw new RuntimeException("Error loading physical score file");
    }
    Vector results = new Vector();
    //start a search from each individual genetic interactions
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,geneticNetwork.getEdgeCount());
    int progress = 0;
    Iterator geneticIt = null;
    if(options.selectedSearch){
      geneticIt = geneticNetwork.getFlaggedEdges().iterator();
    }
    else{
      geneticIt = geneticNetwork.edgesIterator();
    }
    while(geneticIt.hasNext()){
      if(myMonitor.isCanceled()){
										throw new RuntimeException("Search cancelled");
      }
      myMonitor.setProgress(progress++);
      Edge seedInteraction = (Edge)geneticIt.next();
      int cross_count_total = 0;
      int score = 0;
      //performs a sanity check, shouldn't have a genetic interaction between a gene and itself
      if(seedInteraction.getSource().getRootGraphIndex() == seedInteraction.getTarget().getRootGraphIndex()){
	break;
      }
      
      //initialize the sets that represent the two pathways
      Set [] members = new Set[2];
      members[0] = new HashSet();
      members[1] = new HashSet();
      members[0].add(seedInteraction.getSource());
      members[1].add(seedInteraction.getTarget());
      
      //initialize the sets that represent the neighbors in the physical network
      Set [] neighbors = new Set[2];
      for(int idx = 0;idx<neighbors.length;idx++){
	Node member = (Node)members[idx].iterator().next();
	if(physicalNetwork.containsNode(member)){
	  neighbors[idx] = new HashSet(physicalNetwork.neighborsList(member));
	}
	else{
	  neighbors[idx] = new HashSet();
	}
      }
      if(neighbors[1].contains(members[0].iterator().next())){
	cross_count_total++;
      }
            
      boolean improved = true;
      double significant_increase = 0;
      while(improved){
	improved = false;
	for(int idx=0;idx<2;idx++){
	  int opposite = 1-idx;
	  Node bestCandidate = null;
	  double best_increase = significant_increase;
	  int best_cross_count = 0;
	  
	  //keeps track of nodes which will have to be removed (from where?)
	  Vector remove = new Vector();
	  //iterate through neighbors in the physical network and calculate a score for each
	  for(Iterator candidateIt = neighbors[idx].iterator();candidateIt.hasNext();){
	    double increase = 0;
	    Node candidate = (Node)candidateIt.next();

	    //this neighbor is already the member of a pathway, we don't want to consider it further as a neigbhor
	    if(members[0].contains(candidate) || members[1].contains(candidate)){
	      remove.add(candidate);
	      continue;
	    }
	    
	    //determine how many cross pathway interactions adding in this neighbor would induce,
	    //we want to make sure that we do not exceed the limit
	    int cross_count = cross_count_total;
	    for(Iterator candidateNeighborIt = physicalNetwork.neighborsList(candidate).iterator();candidateNeighborIt.hasNext();){
	      if(members[opposite].contains(candidateNeighborIt.next())){
		cross_count++;
		if(cross_count >= cross_count_limit){
		  break;
		}
	      }
	    }
	    
	    if(cross_count >= cross_count_limit){
	      continue;
	    }
	    
	    increase += calculateIncrease(physicalNetwork,physicalScores,members[idx],candidate);
	    increase += calculateIncrease(geneticNetwork,geneticScores,members[opposite],candidate);
	    //if necessary, update the information for the best candidate
	    //found so far
	    if(increase > best_increase){
	      bestCandidate = candidate;
	      best_increase = increase;
	      best_cross_count = cross_count;
	    }
	  }
	  neighbors[idx].removeAll(remove);
	  if(best_increase > significant_increase){
	    improved = true;
	    score += best_increase;
	    cross_count_total = best_cross_count;
	    members[idx].add(bestCandidate);
	    neighbors[idx].addAll(physicalNetwork.neighborsList(bestCandidate));
	    neighbors[idx].remove(bestCandidate);
	  }
	}  
      }
      results.add(new NetworkModel(progress,members[0],members[1],score));
    }
    myMonitor.close();
    Collections.sort(results);
    results = prune(results);
    JDialog betweenPathwayDialog = new BetweenPathwayResultDialog(geneticNetwork, physicalNetwork, results);
    betweenPathwayDialog.show();
  }


  /**
   * Check whether the thread results in an error condition
   */
  public boolean isError(){
    return exception == null;
  }

  public Vector prune(Vector old){
    Vector results = new Vector();
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),"Pruning results",null,0,100);
    int update_interval = (int)Math.ceil(old.size()/100.0);
    int count = 0;
    for(Iterator modelIt = old.iterator();modelIt.hasNext();){
      if(count%update_interval == 0){
	myMonitor.setProgress(count/update_interval);
      }
      boolean overlap = false;
      NetworkModel current = (NetworkModel)modelIt.next();
      if(current.one.size() < 2 || current.two.size() < 2){
	overlap = true;
      }
      else{
	for(Iterator oldModelIt = results.iterator();oldModelIt.hasNext();){
	  NetworkModel oldModel = (NetworkModel)oldModelIt.next();
	  if(overlap(oldModel,current)){
	    overlap = true;
	    break;
	  }
	}
      }
      if(!overlap){
	results.add(current);
      }
    }
    myMonitor.close();
    return results;
  }

  public boolean overlap(NetworkModel one, NetworkModel two){
    return (intersection(one.one,two.one)>0.33 && intersection(one.two,two.two)>0.33) || (intersection(one.one,two.two) > 0.33 && intersection(one.two,two.one) > 0.33); 
  }


  public double intersection(Set one, Set two){
    int size = one.size() + two.size();
    int count = 0;
    for(Iterator nodeIt = one.iterator() ; nodeIt.hasNext() ;){
      if(two.contains(nodeIt.next())){
	count++;
      }
    }
    return count/(double)(size-count);
  }
      


  public double calculateIncrease(CyNetwork cyNetwork, double[][] scores, Set memberSet, Node candidate){
    
    double result = 0;
    if(cyNetwork.containsNode(candidate)){
      int candidate_index = cyNetwork.getIndex(candidate);
      for(Iterator memberIt = memberSet.iterator();memberIt.hasNext();){
	Node member = (Node)memberIt.next();
	if(cyNetwork.containsNode(member)){
	  int member_index = cyNetwork.getIndex(member);
	  int one,two;
	  if(candidate_index < member_index){
	    one = member_index-1;
	    two = candidate_index-1;
	  }
	  else{
	    one = candidate_index-1;
	    two = member_index-1;
	  }
	  if(cyNetwork.isNeighbor(candidate,member)){
	    result += logBeta;
	    result -= Math.log(scores[one][two]);
	  }
	  else{
	    result += logOneMinusBeta;
	    result -= Math.log(1-scores[one][two]);
	  }
	}
	else{
	  result += logOneMinusBeta;
	  result -= Math.log(1-absent_score);
	}
      }
    }
    else{
      result =  memberSet.size()*(logOneMinusBeta-Math.log(1-absent_score));
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
	throw new RuntimeException("Score file contains proteins not present in the network");
      }
      for(int idx=1;idx<splat.length;idx++){
	int two;
	try{
	  two = cyNetwork.getIndex(Cytoscape.getCyNode(names[idx-1]));
	}catch(Exception e){
	  throw new RuntimeException("Score file contains proteins not present in the network");
	}
	if(one < two){
	  result[two-1][one-1] = (new Integer(splat[idx])).intValue()/iterations;
	}
	else{
	  result[one-1][two-1] = (new Integer(splat[idx])).intValue()/iterations;
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

