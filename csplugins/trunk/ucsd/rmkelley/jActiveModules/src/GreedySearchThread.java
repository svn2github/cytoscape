package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import java.io.*;
import java.util.*;
import giny.model.*;
import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.data.readers.*;
//import cytoscape.undo.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;
//import cytoscape.vizmap.*;
//import cytoscape.layout.*;
import cytoscape.*;
import javax.swing.*;

public class GreedySearchThread extends Thread{
  int max_depth,search_depth;
  Iterator nodeIterator;
  MyProgressMonitor pm;
  HashMap node2BestComponent;
  /**
   * Track the best component generated from the current
   * starting point
   */
  Component currentBestComponent;

  /**
   * Lets us know if we need to repeat the greedy search from a new
   * starting point
   */
  boolean greedyDone;
  /**
   * Determines which nodes are within max depth
   * of the starting point
   */
  HashSet withinMaxDepth;    
  Node [] nodes;
  GraphPerspective graph;
  public GreedySearchThread(GraphPerspective graph, int md, int sd, List nodeList,MyProgressMonitor tpm, HashMap temp_hash, Node[] node_array){
    max_depth = md-1;
    search_depth = sd-1;
    this.nodeIterator = nodeList.iterator();
    pm = tpm;
    node2BestComponent = temp_hash;
    nodes = node_array;
    this.graph = graph;
  }
  /**
   * Recursively find the nodes within a max depth
   */
  private void initializeMaxDepth(Node current, int depth){
    withinMaxDepth.add(current);
    if(depth>0){
      Iterator listIt = graph.neighborsList(current).iterator();
      while(listIt.hasNext()){
	Node myNode = (Node)listIt.next();
	if(!withinMaxDepth.contains(myNode)){
	  initializeMaxDepth(myNode,depth-1);
	}
      }
    }
  }

  /**
   * Runs the greedy search algorithm. This function will run a greedy
   * search iteratively using each node of the graph as a starting point
   */
  public void run(){
    boolean done = false;
    Node seed = null;
    synchronized (nodeIterator){
      if(nodeIterator.hasNext()){
	seed = (Node)nodeIterator.next();
      }
      else{
	done = true;
      }
    }
    while(!done){

      //determine which nodes are within max-depth
      //of this starting node and add them to a hash set
      //so we can easily identify them
      withinMaxDepth = new HashSet();
      //if the user doesn't wish to limit the maximum
      //depth, just add every node into the max depth
      //hash, thus all nodes are accepted as possible
      //additions
      if(max_depth < 1){
	for(int j=0;j<nodes.length;j++){
	  withinMaxDepth.add(nodes[j]);
	}
      }
      else{
	//recursively find the nodes within a max depth
	initializeMaxDepth(seed,max_depth);
      }
	    
      //set the neighborhood of nodes to initially be only
      //the single node we are starting the search from
      currentBestComponent = new Component();
      currentBestComponent.addNode(seed);
	    
	    
      //we are done when the recursive call yields no better
      //scoring components, the done 
      //System.out.println("Starting greedy search, seeding from node "+seedCursor.node());
      do{
	greedyDone = true;
	Component component = new Component(currentBestComponent.getNodes());
	//HashSet neighborhood = (HashSet)bestNeighborhood.clone();
	HashSet neighborhood = new HashSet();
	Iterator it = component.getNodes().iterator();
	while(it.hasNext()){
	  Node compNode = (Node)it.next();
	  Iterator nodeIt = graph.neighborsList(compNode).iterator();
	  while(nodeIt.hasNext()){
	    Node myNode = (Node)nodeIt.next();
	    if(withinMaxDepth.contains(myNode)&&!component.contains(myNode)){
	      neighborhood.add(myNode);
	    }
	  }
	}
	runGreedySearchRecursive(search_depth,component,neighborhood);
      }while(!greedyDone);
	    
      Iterator it = currentBestComponent.getNodes().iterator();
	    
      synchronized (node2BestComponent){
	while(it.hasNext()){
	  Node current = (Node)it.next();
	  Component oldBest = (Component)node2BestComponent.get(current);
	  if(oldBest == null || oldBest.getScore() < currentBestComponent.getScore()){
	    node2BestComponent.put(current,currentBestComponent);
	  }
	}
      }
      if (pm != null) {
	synchronized (pm){
	  pm.update();
	}
      }
	    
      synchronized (nodeIterator){
	if(nodeIterator.hasNext()){
	  seed = (Node)nodeIterator.next();
	}
	else{
	  done = true;
	}
      }
    }
  }
    
  /**
   * Recursive greedy search function. Called from runGreedySearch()
   * to a recursive set of calls to greedily identify high scoring
   * networks. The idea for this search is that we make a recursive
   * call for each addition of a node from the neighborhood. At each
   * stage we check to see if we have found a higher scoring network,
   * and if so, store it in one of the global variables. You know how
   * in the Wonder Twins, one of them turned into an elephant and the
   * other turned into a bucket of water? This function is like the
   * elephant.
   * @param depth The remaining depth allowed for this greed search
   * @param component The current component we are branching from
   * @param neighborhood The set of all nodes in the neighborhood of component (distance 1)
   */
  private void runGreedySearchRecursive(int depth, Component component, HashSet neighborhood){
	
    //score this component, check and see if the global top scores should
    //be updated, if we have found a better score, then return true
    if(component.getScore() > currentBestComponent.getScore()){
      currentBestComponent = new Component(component.getNodes());
      greedyDone = false;
    }

    if(depth > 0){
      //if depth > 0, otherwise we are out of depth and the recursive calls will end
      //get an iterator for the neighborhood
	    
      //foreach member of the neighborhood, add it to the current component
      //make the recursive call, and then remove it from the current component
      //we also have to add the appropriate nodes to the neighborhood, and 
      //remove them when we are done
      Vector neighborVector = new Vector(neighborhood);
      Iterator it = neighborVector.iterator();
      while(it.hasNext()){
	Node nextNeighbor = (Node)it.next();
	//add to the component and remove from the neighborhood
	
	component.addNode(nextNeighbor);
	neighborhood.remove(nextNeighbor);
	//for each neighbor, see if it
	//is a new neighbor
	Iterator nodeIt = graph.neighborsList(nextNeighbor).iterator();
	Vector newNeighbors = new Vector();
	while(nodeIt.hasNext()){
	  Node newNeighbor = (Node)nodeIt.next(); 
	  //this node is only a new neighbor if it is not currently
	  //in either the component or the neighborhood, we don't want
	  //to see it twice, we need (ed., that's a weird ass sentence). This component contains() call
	  //needs to be backed up by a hash (done), or I could add an additional
	  //hash which keeps track up the members of the component.
	  if(withinMaxDepth.contains(newNeighbor) && !component.contains(newNeighbor)&&!neighborhood.contains(newNeighbor)){
	    neighborhood.add(newNeighbor);
	    //we also need to keep track of the nodes we added in so
	    //they can be removed afterwards
	    newNeighbors.add(newNeighbor);
	  }
	}
		
	//now that we have updated the component to contain the nextNeighbor
	//and updated neighbors to contain its new neighbors, we can make
	//the recursive call
	runGreedySearchRecursive(depth-1,component,neighborhood);
		
	//after the recursive call we need to clean up after ourselves by
	//fixing up the component and the neighbors hash to undo the changes
	//we made
	component.removeNode(nextNeighbor);
		
	//don't add the neighbor back in yet, otherwise
	//all the the subsequent recursive calls will duplicate
	//some of the work we just did
	//neighborhood.add(nextNeighbor);
		
	Iterator newNeighborIt = newNeighbors.iterator();
	while(newNeighborIt.hasNext()){
	  neighborhood.remove(newNeighborIt.next());
	}
      }
	
      //add all the neighbors back in at the end so they are now
      //available to subsequent recursive calls
      it = neighborVector.iterator();
      while(it.hasNext()){
	neighborhood.add(it.next());
      }
    }
  }
}
