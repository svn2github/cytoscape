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
    //number of physical interactions allowed between pathways
    int cross_count_limit = 1;
    //get the two networks which will be used for the search

    //validate the user input
    if(physicalNetwork == null){
      throw new RuntimeException("No physical network");
    }
    if(geneticNetwork == null){
      throw new RuntimeException("No genetic network");
    }
    

    /*
     * Create a map which will map from each node in the genetic network
     * to a path of length one which contains only this nodes, saves
     * us from having to make multiple copies of this
     */
    HashMap node2Path = new HashMap(geneticNetwork.getNodeCount());
    for(Iterator nodeIt = geneticNetwork.nodesIterator();nodeIt.hasNext();){
      Node node = (Node)nodeIt.next();
      Vector path = new Vector(1);
      path.add(node);
      node2Path.put(node,path);
    }
    /*
     * Create a map which will map from each edge 
     * to a list of adjacent edges and another list
     * We consider an edge adjacent if it shares a path of lenght <=4
     * which goes through both the genetic and physical networks.
     */
    HashMap edgeNeighborMap = new HashMap(geneticNetwork.getEdgeCount());
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Determining edge adjacencies",0,geneticNetwork.getEdgeCount());
    myMonitor.setMillisToPopup(1);
    int progress = 0;
    for(Iterator it = geneticNetwork.edgesIterator();it.hasNext();){
      if(myMonitor.isCanceled()){
	throw new RuntimeException("Search cancelled");
      }
      myMonitor.setProgress(progress++);
      Vector neighbors = new Vector();
      Edge edge = (Edge)it.next();
      Node source = edge.getSource();
      Node target = edge.getTarget();
      if(source == target){
	continue;
      }
      int max_depth = 2;

            
      /*
       * The find all paths function will recursively
       * fill the paths array will all paths of the
       * length corresponding to the index. We only keep
       * those paths that are present in the genetic
       * network.
       */
      
      /*
       * So first we will find all paths that are within a certain depth
       * of the source (and are present in the genetic network), We will add them
       * to a hash that will map from node => path
       */
      
      HashMap nodeNeighbor2Paths = new HashMap();
      Stack nodeStack = new Stack();
      nodeStack.push(source);
      findSourcePaths(nodeStack,max_depth,target,nodeNeighbor2Paths);
      nodeStack.pop();
      nodeStack.push(target);
      findNeighborEdges(nodeStack,max_depth,source,nodeNeighbor2Paths,neighbors);
      nodeStack.pop();
      neighbors.trimToSize();
      edgeNeighborMap.put(edge,neighbors);
    }
    myMonitor.close();
    results = new Vector();
    //start a search from each individual genetic interactions
    progress = 0;
    Iterator geneticIt = null;
    if(options.selectedSearch){
      geneticIt = geneticNetwork.getFlaggedEdges().iterator();
      myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,geneticNetwork.getFlaggedEdges().size());
    }
    else{
      geneticIt = geneticNetwork.edgesIterator();
      myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,geneticNetwork.getEdgeCount());    
    }
    myMonitor.setMillisToPopup(0);
    while(geneticIt.hasNext()){
      System.err.println(""+progress);
      Edge seedInteraction = (Edge)geneticIt.next();
      int cross_count_total = 0;
      double physical_source_score = 0;
      double physical_target_score = 0;
      /*
       * I need to correct this, the model starts
       * with one edge, and I should edit this
       * initial genetic score to reflect this
       * Don't need to check for the edge
       * because it already has to be there
       */
      double genetic_score = 0;
      double score = Double.NEGATIVE_INFINITY;
      
      /*
       * performs a sanity check, shouldn't have a 
       * genetic interaction between a gene and itself
       */
      if(seedInteraction.getSource().getRootGraphIndex() == seedInteraction.getTarget().getRootGraphIndex()){
	continue;
      }
      
      //initialize the sets that represent the two pathways
      Set sourceMembers = new HashSet();
      Set targetMembers = new HashSet();
      sourceMembers.add(seedInteraction.getSource());
      targetMembers.add(seedInteraction.getTarget());
      
      //initialize the set that represents the neighboring interactions in the 
      //genetic network
      Set neighbors = new HashSet((Vector)edgeNeighborMap.get(seedInteraction));
      //initialize the sets that represent the neighbors in the physical network
      if(physicalNetwork.edgeExists(seedInteraction.getSource(),seedInteraction.getTarget())){
	cross_count_total++;
      }
            
      /*
       * Start searching through neighbor interactions to improve
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

	NeighborEdge bestCandidate = null;
	double best_score = score;
	double best_genetic_score = Double.NEGATIVE_INFINITY;
	double best_physical_source_score = Double.NEGATIVE_INFINITY;
	double best_physical_target_score = Double.NEGATIVE_INFINITY;
	int best_cross_count = 0;
	for(Iterator candidateIt = neighbors.iterator();candidateIt.hasNext();){
	  NeighborEdge candidate = (NeighborEdge)candidateIt.next();
	  List sourceCandidates = candidate.reverse ? candidate.targetCandidates : candidate.sourceCandidates;
	  List targetCandidates = candidate.reverse ? candidate.sourceCandidates : candidate.targetCandidates;
	  List newSources = new Vector(sourceCandidates.size());
	  List newTargets = new Vector(targetCandidates.size());

	  /*
	   * Determine which candidates are not already present in the 
	   * source set
	   */
	  for(Iterator sourceCandidateIt = sourceCandidates.iterator();sourceCandidateIt.hasNext();){
	    Object sourceCandidate = sourceCandidateIt.next();
	    if(!sourceMembers.contains(sourceCandidate)){
	      newSources.add(sourceCandidate);
	    }
	  }
	  /*
	   * Do the same for the target candidates
	   */
	  for(Iterator targetCandidateIt = targetCandidates.iterator();targetCandidateIt.hasNext();){
	    Object targetCandidate = targetCandidateIt.next();
	    if(!targetMembers.contains(targetCandidate)){
	      newTargets.add(targetCandidate);
	    }
	  }


	  /*
	   * Check to see if all possible candidates have
	   * already been added to the current result. If so
	   * we want ot ignore this edge in future passes
	   */
	  if(newSources.size() == 0 && newTargets.size() == 0){
	    candidateIt.remove();
	    continue;
	  }

	  /*
	   * Check to see if adding this edges will make the two sets overlapping
	   * If so, we never want to add this edge, so we can safely
	   * ignore it from this point on.
	   */
	  boolean cont = false;
	  for(Iterator sourceCandidateIt = newSources.iterator();sourceCandidateIt.hasNext();){
	    if(targetMembers.contains(sourceCandidateIt.next())){
	      candidateIt.remove();
	      cont = true;
	      break;
	    }
	  }
	  if(cont){
	    continue;
	  }

	  for(Iterator targetCandidateIt = newTargets.iterator();targetCandidateIt.hasNext();){
	    if(sourceMembers.contains(targetCandidateIt.next())){
	      candidateIt.remove();
	      cont = true;
	      break;
	    }
	  }
	  if(cont){
	    continue;
	  }

	  
	  int remaining_cross_count = cross_count_limit-cross_count_total+1;
	  int cross_count = crossPhysicalInteraction(newSources,newTargets,sourceMembers,targetMembers,remaining_cross_count);
	  /*
	   * Check to see if adding this edge will exceed our cross
	   * count limit
	   */
	  if(cross_count == remaining_cross_count){
	    candidateIt.remove();
	    continue;
	  }

	  
	  double this_physical_source_score = physical_source_score + calculatePhysicalIncrease(newSources,sourceMembers);
	  double this_physical_target_score = physical_target_score + calculatePhysicalIncrease(newTargets,targetMembers);
	  int source_size = newSources.size()+sourceMembers.size();
	  int target_size = newTargets.size()+targetMembers.size();
	  double sourceMax = source_size*7.5;
	  double targetMax = target_size*7.5;

	  double temp1 = Math.max(0,Math.min(sourceMax,this_physical_source_score));
	  double temp2 = Math.max(0,Math.min(targetMax,this_physical_target_score));
	  //double source_FDR = score2SuccessRate(this_physical_source_score);
	  //double target_FDR = score2SuccessRate(this_physical_target_score);
	  double this_genetic_score = genetic_score + calculateGeneticIncrease(newSources,newTargets,sourceMembers,targetMembers);
	  if(this_genetic_score < 0){
	    continue;
	  }
	  //double this_score = this_genetic_score*source_FDR*target_FDR;
	  //double this_score = this_genetic_score*(temp1/sourceMax)*(temp2/targetMax);
	  double this_score = this_genetic_score + this_physical_source_score + this_physical_target_score;
	  if(this_score>best_score){
	    bestCandidate = candidate;
	    best_score = this_score;
	    best_physical_source_score = this_physical_source_score;
	    best_physical_target_score = this_physical_target_score;
	    best_genetic_score = this_genetic_score;
	    best_cross_count = cross_count;
	    improved = true;
	  }

	}
	if(improved){
	  cross_count_total += best_cross_count;
	  score = best_score;
	  genetic_score = best_genetic_score;
	  physical_source_score = best_physical_source_score;
	  physical_target_score = best_physical_target_score;
	  sourceMembers.addAll(bestCandidate.reverse ? bestCandidate.targetCandidates : bestCandidate.sourceCandidates);
	  targetMembers.addAll(bestCandidate.reverse ? bestCandidate.sourceCandidates : bestCandidate.targetCandidates);
	  if(bestCandidate.reverse){
	    for(Iterator it = ((Vector)edgeNeighborMap.get(bestCandidate.edge)).iterator();it.hasNext();){
	      NeighborEdge newNeighbor = (NeighborEdge)it.next();
	      neighbors.add(newNeighbor.reverseEdge());
	    }
	  }
	  else{
	    neighbors.addAll((Vector)edgeNeighborMap.get(bestCandidate.edge));
	  }
	}
      }
      
      /*
       * Here we calculate the number of potential
       * edges for each type of edges (physical vs genetic
       */
      results.add(new NetworkModel(progress,
				   sourceMembers,
				   targetMembers,
				   score,
				   physical_source_score,
				   physical_target_score,
				   genetic_score));
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

//   protected void findAllPaths(Stack nodeStack, int max_depth, Node otherNode, List [] sourcePaths){
//     Node currentNode = (Node)nodeStack.peek();
//     List currentNodeNeighbors = physicalNetwork.neighborsList(currentNode);
//     if(currentNodeNeighbors != null){
//       for(Iterator neighborIt = currentNodeNeighbors.iterator();neighborIt.hasNext();){
// 	Node neighbor = (Node)neighborIt.next();
//   	/*
//   	 * I want the path to consist only
//   	 * of unique nodes
//   	 */
//   	if(!nodeStack.contains(neighbor)){
//   	  nodeStack.push(neighbor);
//   	  if(geneticNetwork.containsNode(neighbor)){
//   	    Vector path = new Vector(nodeStack);
//   	    sourcePaths[sourcePaths.length-max_depth].add(path);
//   	  }
//   	  if(max_depth > 1){
//   	    findAllPaths(nodeStack,max_depth-1,sourcePaths);
//   	  }
//   	  nodeStack.pop();
//   	}
//       }
//     }
//   }
  

    protected void findSourcePaths(Stack nodeStack, int max_depth, Node otherNode, HashMap node2Paths){
      Node currentNode = (Node)nodeStack.peek();
      if(geneticNetwork.containsNode(currentNode)){
	Vector path = new Vector(nodeStack);
	Vector pathList = (Vector)node2Paths.get(nodeStack.peek());
	if(pathList == null){
	  pathList = new Vector();
	  node2Paths.put(nodeStack.peek(),pathList);
	}
	pathList.add(path);
      }
      if(max_depth > 0){
	List currentNodeNeighbors = physicalNetwork.neighborsList(currentNode);
	if(currentNodeNeighbors != null){
	  for(Iterator neighborIt = currentNodeNeighbors.iterator();neighborIt.hasNext();){
	    Node neighbor = (Node)neighborIt.next();
	    /*
	     * I want the path to consist only
	     * of unique nodes
	     */
	    if(!nodeStack.contains(neighbor) && neighbor != otherNode){
	      nodeStack.push(neighbor);
	      findSourcePaths(nodeStack,max_depth-1,otherNode,node2Paths);
	      nodeStack.pop();
	    }
	  }
	}
      }
    }
  
  protected void findNeighborEdges(Stack nodeStack, int max_depth, Node otherNode, HashMap node2Paths, Vector results){
    Node currentNode = (Node)nodeStack.peek();
    if(geneticNetwork.containsNode(currentNode)){
      for(Iterator geneticIt = geneticNetwork.getAdjacentEdgesList(currentNode,true,true,true).iterator();geneticIt.hasNext();){
	Edge edge = (Edge)geneticIt.next();
	if(edge.getSource() == currentNode){
	  if(node2Paths.containsKey(edge.getTarget())){
	    Vector targetPath = new Vector(nodeStack);
	    for(Iterator pathIt = ((List)node2Paths.get(edge.getTarget())).iterator();pathIt.hasNext();){
	      results.add(new NeighborEdge(edge,targetPath,(List)pathIt.next(),true));
	    }
	  }
	}
	else{
	  if(node2Paths.containsKey(edge.getSource())){
	    Vector targetPath = new Vector(nodeStack);
	    for(Iterator pathIt = ((List)node2Paths.get(edge.getSource())).iterator();pathIt.hasNext();){
	      results.add(new NeighborEdge(edge,(List)pathIt.next(),targetPath,false));
	    }
	  }
	}
      }
      if(max_depth > 0){
	List currentNodeNeighbors = physicalNetwork.neighborsList(currentNode);
	if(currentNodeNeighbors != null){
	  for(Iterator neighborIt = currentNodeNeighbors.iterator();neighborIt.hasNext();){
	    Node neighbor = (Node)neighborIt.next();
	    /*
	     * I want the path to consist only
	     * of unique nodes
	     */
	    if(!nodeStack.contains(neighbor) && neighbor != otherNode){
	      nodeStack.push(neighbor);
	      findNeighborEdges(nodeStack,max_depth-1,otherNode,node2Paths,results);
	      nodeStack.pop();
	    }
	  }
	}
      }
    }
  }
  /**
   * The purpose of this function is to map from a physical
   * score to an FDR rate associated with that particular score
   * This is used to fudge with the genetic score for a bi-partite
   * thingy, Right now I am kind of guesstimating, but eventually
   * I plan on using learned data
   */
  protected double score2SuccessRate(double score){
    double MAX_SCORE = 15.0;
    double adjusted_score = Math.min(MAX_SCORE,score);
    adjusted_score = Math.max(0,adjusted_score);
    return adjusted_score/MAX_SCORE;
  }

//   protected void findAllPaths(Stack nodeStack, int max_depth, List otherPath, boolean searchFromSource, List results){
//     Node currentNode = (Node)nodeStack.peek();
//     List currentNodeNeighbors = physicalNetwork.neighborsList(currentNode);
//     if(currentNodeNeighbors != null){
//       for(Iterator neighborIt = currentNodeNeighbors.iterator();neighborIt.hasNext();){
// 	Node neighbor = (Node)neighborIt.next();
// 	/*
// 	 * I want the path to consist only
// 	 * of unique nodes and I don't want the unique
// 	 * path to go through the edge oppostive
// 	 */
// 	if(!nodeStack.contains(neighbor) && neighbor != otherPath.get(0)){
// 	  nodeStack.push(neighbor);
// 	  if(geneticNetwork.containsNode(neighbor)){
// 	    Vector path = new Vector(nodeStack);
// 	    for(Iterator geneticIt = geneticNetwork.getAdjacentEdgesList(neighbor,true,true,true).iterator();geneticIt.hasNext();){
// 	      Edge edge = (Edge)geneticIt.next();
// 	      if(edge.getSource() != edge.getTarget()){
// 		if(edge.getSource() == neighbor ^ searchFromSource){
// 		  results.add(new NeighborEdge(edge,path,otherPath,false));
// 		}
// 		else{
// 		  results.add(new NeighborEdge(edge,otherPath,path,true));
// 		}
// 	      }
// 	    }
// 	  }
// 	  if(max_depth > 1){
// 	    findAllPaths(nodeStack,max_depth-1,otherPath,searchFromSource,results);
// 	  }
// 	  nodeStack.pop();
// 	}
//       }
//     }
//   }

  public Vector getResults(){
    return results;
  }

//   protected Vector prune(Vector old){
//     Vector results = new Vector();
//     ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),"Pruning results",null,0,100);
//     myMonitor.setMillisToPopup(50);
//     int update_interval = (int)Math.ceil(old.size()/100.0);
//     int count = 0;
//     for(Iterator modelIt = old.iterator();modelIt.hasNext();){
//       if(myMonitor.isCanceled()){
// 	throw new RuntimeException("Search cancelled");
//       }
//       if(count%update_interval == 0){
// 	myMonitor.setProgress(count/update_interval);
//       }
//       boolean overlap = false;
//       NetworkModel current = (NetworkModel)modelIt.next();
//       if(current.one.size() < 2 || current.two.size() < 2 || current.score < options.cutoff){
// 	overlap = true;
//       }
//       else{
// 	for(Iterator oldModelIt = results.iterator();oldModelIt.hasNext();){
// 	  NetworkModel oldModel = (NetworkModel)oldModelIt.next();
// 	  if(overlap(oldModel,current)){
// 	    overlap = true;
// 	    break;
// 	  }
// 	}
//       }
//       if(!overlap){
// 	results.add(current);
//       }
//     }
//     myMonitor.close();
//     return results;
//   }

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
      if(current.one.size() < 2 || current.two.size() < 2 || current.score < options.cutoff){
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
      for(Iterator nodeIt = current.two.iterator();nodeIt.hasNext();){
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

  public boolean overlap(NetworkModel one, NetworkModel two){
    return (intersection(one.one,two.one)>overlap_cutoff && intersection(one.two,two.two)>overlap_cutoff) || (intersection(one.one,two.two) > overlap_cutoff && intersection(one.two,two.one) > overlap_cutoff); 
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
      
  /**
   * Calculate how many new cross physical interactions are implied
   * by adding in these nodes to the source and target sets
   * A max is provided to short-circuit the evaluation
   */
  protected int crossPhysicalInteraction(List sourceCandidates, List targetCandidates, Set sourceMembers, Set targetMembers, int maximum){
    int cross_count = 0;
    for(Iterator sourceCandidateIt = sourceCandidates.iterator(); sourceCandidateIt.hasNext();){
      Node sourceCandidate = (Node)sourceCandidateIt.next();
      for(Iterator it = targetMembers.iterator();it.hasNext();){
	if(physicalNetwork.isNeighbor(sourceCandidate,(Node)it.next())){
	  cross_count += 1;
	  if(cross_count >= maximum){
	    return cross_count;
	  }
	}
      }
      for(Iterator targetCandidateIt = targetCandidates.iterator(); targetCandidateIt.hasNext();){
	if(physicalNetwork.isNeighbor(sourceCandidate,(Node)targetCandidateIt.next())){
	  cross_count += 1;
	  if(cross_count >= maximum){
	    return cross_count;
	  }
	}
      }
    }
    for(Iterator targetCandidateIt = targetCandidates.iterator(); targetCandidateIt.hasNext();){
      Node targetCandidate = (Node)targetCandidateIt.next();
      for(Iterator it = sourceMembers.iterator();it.hasNext();){
	if(physicalNetwork.isNeighbor(targetCandidate,(Node)it.next())){
	  cross_count += 1;
	  if(cross_count >= maximum){
	    return cross_count;
	  }
	}
      }
      
    }
    return cross_count;
  }

  protected double calculateGeneticIncrease(List sourceCandidates, List targetCandidates, Set sourceMembers, Set targetMembers){
    double result = 0;
    for(Iterator sourceCandidateIt = sourceCandidates.iterator();sourceCandidateIt.hasNext();){
      Node sourceCandidate = (Node)sourceCandidateIt.next();
      result += calculatePartialGeneticIncrease(targetMembers,sourceCandidate);
    }
    for(Iterator targetCandidateIt = targetCandidates.iterator();targetCandidateIt.hasNext();){
      Node targetCandidate = (Node)targetCandidateIt.next();
      result += calculatePartialGeneticIncrease(sourceMembers,targetCandidate);
    }

    for(Iterator newSourceIt = sourceCandidates.iterator();newSourceIt.hasNext();){
      Node sourceCandidate = (Node)newSourceIt.next();
      if(geneticNetwork.containsNode(sourceCandidate)){
	int sourceCandidateIndex = geneticNetwork.getIndex(sourceCandidate);
	for(Iterator newTargetIt = targetCandidates.iterator();newTargetIt.hasNext();){
	  Node targetCandidate = (Node)newTargetIt.next();
	  if(geneticNetwork.containsNode(targetCandidate)){
	    int targetCandidateIndex = geneticNetwork.getIndex(targetCandidate);
	    int one,two;
	    one = Math.max(sourceCandidateIndex,targetCandidateIndex)-1;
	    two = Math.min(sourceCandidateIndex,targetCandidateIndex)-1;
	    if(geneticNetwork.isNeighbor(sourceCandidate,targetCandidate)){
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
	result += targetCandidates.size()*(genetic_logOneMinusBeta - Math.log(1-absent_score));
      }
    }
    return result;
  }

  
  protected double calculatePartialGeneticIncrease(Set memberSet, Node candidate){
    double result = 0;
    if(geneticNetwork.containsNode(candidate)){
      int candidate_index = geneticNetwork.getIndex(candidate);
      for(Iterator memberIt = memberSet.iterator();memberIt.hasNext();){
	Node member = (Node)memberIt.next();
	if(geneticNetwork.containsNode(member)){
	  int member_index = geneticNetwork.getIndex(member);
	  int one,two;
	  if(candidate_index < member_index){
	    one = member_index-1;
	    two = candidate_index-1;
	  }
	  else{
	    one = candidate_index-1;
	    two = member_index-1;
	  }
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
      result += memberSet.size()*(genetic_logOneMinusBeta - Math.log(1-absent_score));
    }
    
    return result;
	
  }

  /*
   * I'm thinking about changes this so that *candidates only
   * includes those nodes that don't currently belong to the
   * respective set
   */
  // protected double calculatePhysicalIncrease(List sourceCandidates, List targetCandidates, Set sourceMembers, Set targetMembers){
//     double result = 0;
//     for(Iterator sourceCandidateIt = sourceCandidates.iterator();sourceCandidateIt.hasNext();){
//       Node sourceCandidate = (Node)sourceCandidateIt.next();
//       result += calculatePartialPhysicalIncrease(sourceCandidate, sourceMembers, targetMembers);
//     }
//     /*
//      * If both nodes are new,then we also have to consider the edge
//      * (that must be present) between the two new additions to the sources
//      */
//     result += calculatePartialPhysicalIncrease(sourceCandidates);
        
    
//     for(Iterator targetCandidateIt = targetCandidates.iterator();targetCandidateIt.hasNext();){
//       Node targetCandidate = (Node)targetCandidateIt.next();
//       result += calculatePartialPhysicalIncrease(targetCandidate, targetMembers, sourceMembers);
//     }
//     result += calculatePartialPhysicalIncrease(targetCandidates);
    
//     return result;
//   }

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
    //get genetic interactios involving candidate
    //for each genetic interaction with a member of opposite set
    //    for each genetic interaction that member has with members
    //       check for a protein interaction, and update score
    /*
     * Update this function to ignore the distance restrictions
     */
    /*
      for(Iterator it = geneticNetwork.neighborsList(candidate).iterator();it.hasNext();){
      Node candidateNeighbor = (Node)it.next();
      if(otherMembers.contains(candidateNeighbor)){
      for(Iterator dist2It = geneticNetwork.neighborsList(candidateNeighbor).iterator();dist2It.hasNext();){
      Node dist2Neighbor = (Node)dist2It.next();
      if(members.contains(dist2Neighbor)){
      if(candidatePresent && physicalNetwork.containsNode(dist2Neighbor)){
      int one = Math.max(physicalNetwork.getIndex(dist2Neighbor),candidateIndex) -1;
      int two = Math.min(physicalNetwork.getIndex(dist2Neighbor),candidateIndex) -1;
      if(physicalNetwork.isNeighbor(candidate,dist2Neighbor)){
      result += logBeta;
      result -= Math.log(physicalScores[one][two]);
      }
      else{
      result += logOneMinusBeta;
      result -= Math.log(1-physicalScores[one][two]);
      }
      }
      else{
      result += logOneMinusBeta;
      result -= Math.log(1-absent_score);
      }
      }
      }
      }
      }
      return result;
    */
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

class NeighborEdge{
  /**
   * Keeps track of the root graph indices
   * of the neighbors of a particular
   * node
   */
  public Edge edge;
  public List sourceCandidates;
  public List targetCandidates;
  /**
   * Whether we should reverse the source
   * and target to make the two edges
   * line up source to source
   */
  public boolean reverse;
  public NeighborEdge(Edge edge, List sourceCandidates, List targetCandidates, boolean reverse){
    this.edge = edge;
    this.reverse = reverse;
    this.sourceCandidates = sourceCandidates;
    this.targetCandidates = targetCandidates;
  }
  
  public boolean equals(Object o){
    NeighborEdge other = (NeighborEdge)o;
    if(other.edge != this.edge || other.reverse != reverse){
      return false;
    }
    if(sourceCandidates.size() != other.sourceCandidates.size()){
      return false;
    }
    if(targetCandidates.size() != other.targetCandidates.size()){
      return false;
    }
    for(int idx=0;idx<sourceCandidates.size();idx++){
      if(sourceCandidates.get(idx) != other.sourceCandidates.get(idx)){
	return false;
      }
    }
    for(int idx=0;idx<targetCandidates.size();idx++){
      if(targetCandidates.get(idx) != other.targetCandidates.get(idx)){
	return false;
      }
    }
    return true;
  }
  
  public int hashCode(){
    return edge.hashCode();
  }

  public NeighborEdge reverseEdge(){
    return new NeighborEdge(edge,sourceCandidates,targetCandidates,!reverse);
  }
}
