package  ucsd.rmkelley.WithinPathway;
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

class BetweenPathwayThread2 extends Thread{
  double absent_score = .00001;
  double physical_beta = 0.9;
  double genetic_beta = 0.9;
  double physical_logBeta = Math.log(physical_beta);
  double physical_logOneMinusBeta = Math.log(1-physical_beta);
  double genetic_logBeta = Math.log(genetic_beta);
  double genetic_logOneMinusBeta = Math.log(1-genetic_beta);
  double overlap_cutoff = 0.30;
  BetweenPathwayOptions options;
  double [][] physicalScores;
  double [][] geneticScores;
  CyNetwork physicalNetwork;
  CyNetwork geneticNetwork;
  Vector results;

  public BetweenPathwayThread2(BetweenPathwayOptions options){
    this.options = options;
  }

  public void setPhysicalNetwork(CyNetwork physicalNetwork){
    this.physicalNetwork = physicalNetwork;
  }

  public void setGeneticNetwork(CyNetwork geneticNetwork){
    this.geneticNetwork = geneticNetwork;
  }

  public void loadPhysicalScores(File physicalFile){
    //set up the array of physical scores
    try{
      physicalScores = getScores(physicalFile,physicalNetwork);
    }catch(IOException io){
      throw new RuntimeException("Error loading physical score file");
    }
  }

  public void loadGeneticScores(File geneticFile){
    try{
      geneticScores = getScores(geneticFile,geneticNetwork);
    }catch(IOException io){
      throw new RuntimeException("Error loading genetic score file");
    }
  }

  public void run(){
    //validate the user input
    if(physicalNetwork == null){
      throw new RuntimeException("No physical network");
    }
    if(geneticNetwork == null){
      throw new RuntimeException("No genetic network");
    }
    

    results = new Vector();

    //start a search from each node
    int progress = 0;
    Iterator geneticIt = null;
    
    Iterator it = Cytoscape.getRootGraph().nodesIterator();
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,Cytoscape.getRootGraph().getNodeCount());    
    myMonitor.setMillisToPopup(0);
    while(it.hasNext()){
      System.err.println(""+progress);
      Node seedNode = (Node)it.next();
      double physical_score = 0;
      double genetic_score = 0;
      double score = 0;
      
      Set members = new HashSet();
      members.add(seedNode);
      
      Set neighbors = new HashSet();
      if(physicalNetwork.containsNode(seedNode)){
	neighbors.addAll(physicalNetwork.neighborsList(seedNode));
      }
      if(geneticNetwork.containsNode(seedNode)){
	neighbors.addAll(geneticNetwork.neighborsList(seedNode));
      }
      neighbors.remove(seedNode);
      
      boolean improved = true;
      double significant_increase = 0;
      while(improved){
	improved = false;
	if(myMonitor.isCanceled()){
	  //throw new RuntimeException("Search cancelled");
	  break;
	}

	Node bestCandidate = null;
	double best_score = score;
	double best_genetic_score = Double.NEGATIVE_INFINITY;
	double best_physical_score = Double.NEGATIVE_INFINITY;
	
	for(Iterator candidateIt = neighbors.iterator();candidateIt.hasNext();){
	  Node candidate = (Node)candidateIt.next();
	  try{
	    double this_physical_score = physical_score + calculatePartialPhysicalIncrease(candidate,members);
	    double this_genetic_score = genetic_score + calculatePartialGeneticIncrease(candidate,members);
	  
	    //if(this_genetic_score < 0 || this_physical_score < 0){
	    //  continue;
	    //}
	    double this_score = this_genetic_score + this_physical_score;
	    if(this_score>best_score){
	      bestCandidate = candidate;
	      best_score = this_score;
	      best_physical_score = this_physical_score;
	      best_genetic_score = this_genetic_score;
	      improved = true;
	    }
	  }catch(Exception e){
	    e.printStackTrace();
	    System.err.println(candidate);
	    System.err.println(members);
	    System.exit(-1);
	  }

	}
	if(improved){
	  score = best_score;
	  genetic_score = best_genetic_score;
	  physical_score = best_physical_score;
	  members.add(bestCandidate);
	  List newNeighbors = physicalNetwork.neighborsList(bestCandidate);
	  if(newNeighbors != null){
	    neighbors.addAll(newNeighbors);
	  }
	  newNeighbors = geneticNetwork.neighborsList(bestCandidate);
	  if(newNeighbors != null){
	    neighbors.addAll(newNeighbors);
	  }
	  neighbors.removeAll(members);
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
      if(current.nodes.size() < 2 || current.score < options.cutoff){
	continue;
      }
      for(Iterator nodeIt = current.nodes.iterator();nodeIt.hasNext();){
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


  
  protected double calculatePartialGeneticIncrease(Node candidate, Set members){
    double result = 0;
    boolean candidatePresent = geneticNetwork.containsNode(candidate);
    if(candidatePresent){
      int candidateIndex = geneticNetwork.getIndex(candidate)-1;
      for(Iterator memberIt = members.iterator();memberIt.hasNext();){
	Node member = (Node)memberIt.next();
	if(geneticNetwork.containsNode(member)){
	  int memberIndex = geneticNetwork.getIndex(member)-1;
	  int one = Math.max(candidateIndex,memberIndex);
	  int two = Math.min(candidateIndex,memberIndex);
	  if(geneticNetwork.isNeighbor(candidate,member)){
	    result += genetic_logBeta;
	    result -= Math.log(geneticScores[one][two]);
	  }
	  else{
	    result += genetic_logOneMinusBeta;
	    result -= Math.log(1-geneticScores[one][two]);
	  }  
	}
	else{
	  result += genetic_logOneMinusBeta;
	  result -= Math.log(1-absent_score);
	}
      }
    }
    else{
      result += members.size()*(genetic_logOneMinusBeta - Math.log(1-absent_score));
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
    //if(cyNetwork == physicalNetwork){
    //  iterations /= DENSITY_FACTOR;
    //}
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

