package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import giny.model.GraphPerspective;
import giny.model.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import csplugins.jActiveModules.data.ActivePathFinderParameters;

public abstract class SearchThread extends Thread{
  protected GraphPerspective graph;
  protected SortedVector oldPaths,newPaths;
  protected Vector resultPaths;
  protected Vector hiddenNodes;
  protected HashSet nodeSet;
  //protected HashMap node2edges;
  protected Node [] nodes;
  protected HashMap node2component;
  protected ActivePathFinderParameters apfParams;
  public SearchThread(GraphPerspective graph, Vector resultPaths,Node [] nodes, ActivePathFinderParameters apfParams){
    this.graph = graph;
    this.resultPaths = resultPaths;
    //this.node2edges = node2edges;
    this.nodes = nodes;
    this.apfParams = apfParams;
    hiddenNodes = new Vector();
  }

  public abstract void run();

  /**
   * This function will update the status of newPaths
   * so that it will contain the components that would
   * be present if we toggled the state of Node current
   * @return A vector of new Components created by applying this change
   */
  protected Vector updatePaths(Node current){
    //first make a (shallow) copy of oldPaths as our newPaths
    //this copy will be update in upate_remove and update_add
    newPaths = (SortedVector)((SortedVector)oldPaths).clone();
    //depending on whether the node was removed or added, call
    //update_remove or update_add. These methods contain specific
    //optimizations to speed the update in their respective cases
    //(mostly the optimization are in update_add()
    if(!nodeSet.contains(current)){
      return update_remove(current);
    }
    else{
      Vector result = update_add(current);
      if(!hiddenNodes.isEmpty()){
	result.addAll(update_remove(hiddenNodes));
      }
      return result;
    }
  }
	
  /**
   *returns a vector containing the new componet
   *created by adding current to the graph. Also
   *update the status of newPaths with this new
   *component and removes invalid old paths from
   *newPaths
   * @param current The node which was just added
   * @return A vector of newly generated components
   */
  protected Vector update_add(Node current){
    //get a list of components
    //for the neighboring nodes
    //first get the list of neighboring nodes

    //now build the list of neighbor components
    Set nComponents = new HashSet();
    //Iterator it = graph.nodesIterator();
				//CHANGE HERE
				Iterator it = graph.neighborsList(current).iterator();
				while(it.hasNext()){
      //check for self loops
      Node myNode = (Node)it.next();
      if(current != myNode && nodeSet.contains(myNode)){
	nComponents.add(node2component.get(myNode));
      }
    }
	
    //Vector vnComponents = new Vector(nComponents);
    it = nComponents.iterator();
    //remove those components from newPaths
    while(it.hasNext()){
      newPaths.remove(it.next());
    }
	
	
    //combine all those paths into a new
    //component and add all of their scores
    //together
    Component newComponent = new Component(nComponents,current);

    //add this component to newPatbs
    newPaths.sortedAdd(newComponent);
    Vector result = new Vector();
    result.add(newComponent);
	
    return result;

	
  }

  /**
   * A helper functin to call update_remove
   * if we only have one node to remove at a
   * time
   * @param removed The node which was removed
   * @return A vector of newly created components
   */
  protected Vector update_remove(Node removed){
    Vector temp = new Vector();
    temp.add(removed);
    return update_remove(temp);
  }
    
  /**
   *this function updates the state of newPaths
   *to reflect the removal of current, it returns
   *the a vector of newComponents that have been
   *created. It also removes the component that
   *is no longer valid from newPaths.
   * @param removed vector of nodes that were just removed
   * @return A vector of newly created components
   */
  protected Vector update_remove(Vector removed){
	
    //find the nodes neighboring this node
    //the hash hasn't been update yet, so I can
    //look up the nodes in the hash, and read
    //of the list of nodes
	
    HashSet oldComponents = new HashSet();
    Iterator it = removed.iterator();
    while(it.hasNext()){
      oldComponents.add(node2component.get(it.next()));
    }

    //remove those components from newPaths and get all the nodes
    //in that vector
    Vector oldNodes = new Vector();
    it = oldComponents.iterator();
    while(it.hasNext()){
      Component comp = (Component)it.next();
      newPaths.remove(comp);
      oldNodes.addAll(comp.getNodes());
    }
	
    //remove all the nodes that are now hidden
    it = removed.iterator();
    while(it.hasNext()){
      oldNodes.remove(it.next());
    }
	
    //now we have a vector of just the nodes that are
    //in the graph now. We want to find the components
    //in this set of nodes, keep in mind the special
    //case that this could be empty
	
    ComponentFinder cf = new ComponentFinder(graph,nodeSet);
    Vector newComponents = cf.getComponents(oldNodes);
	
	
    it =  newComponents.iterator();
    int sum = 0;
    while(it.hasNext()){
      newPaths.sortedAdd(it.next());
    }
    return newComponents;

	
  }

  /**
   *The following function will toggle the current state of a node
   *This can either be removal or reinsertion, depending on the 
   *current state of the node. If the node is to be reinserted,
   *it will also reinsert any possible associated edges
   * @param toggle The node to be toggled.
   */
  protected void toggleNode(Node toggle){
    //If the graph contains the node, remove it
    //this will also automatically remove any
    //associated edges
    //if(graph.containsNode(toggle,false)){
    //  graph.hideNode(toggle);	    
    //}
    if(nodeSet.contains(toggle)){
      nodeSet.remove(toggle);
    }
    else{
      //If the graph does not contain the node, add it back in.
      //Note that the associated edges have to be reinserted manually
      //This where th node2edges hash comes in handy.
      //graph.restoreNode(toggle);
      nodeSet.add(toggle);
      //Edge [] e_array = (Edge[])node2edges.get(toggle);
      //for(int j=0;j<e_array.length;j++){
      //Edge e = e_array[j];
      //if(graph.containsNode(e.getSource(),false) && graph.containsNode(e.getTarget(),false)){
      //  graph.restoreEdge(e);
      //}
      //}
    }
  }
  
  /**
   *The purpose of this function is to implement the hubfinding
   *correction. If the toggled node being reinserted is of high degree,
   *it will hide all adjacent nodes that belong to a low scoring component.
   *It sets a global vector of nodes that were hidden
   * @param toggle The node to be toggled
   */
  protected void toggleNodeWithHiding(Node toggle){
    //If the graph contains the node, we dont' have to do
    //anything special
    if(nodeSet.contains(toggle)){
      nodeSet.remove(toggle);
    }
    else{
      nodeSet.add(toggle);
      List neighborList = graph.neighborsList(toggle);
      //check if it is a hub according ot the user's 
      //parameters
      if(neighborList.size() >= apfParams.getMinHubSize()){
						//calculate the minimum score of the components
	     //being tracked, this is a pretty inefficient way
	     //to do it, but hubs are usually rare and the number
	     //of paths is small, so I don't think it really 
	     //matters.
	Iterator it = oldPaths.iterator();
	int i = 0;
	while(i<apfParams.getNumberOfPaths()-1){
	  i++;
	  it.next();
	}
	//I guess this is sorted now, so I don't know why this the iterator
	//is being used to find the min score
	//for each neighboring node get its component, if the components
	//score is low, then that node gets that axe.
	double min_score = ((Component)it.next()).getScore();
	for(Iterator neighborIt = neighborList.iterator();neighborIt.hasNext();){
	  Node neighbor = (Node)neighborIt.next();
	  //make sure to include a check for self edges here
	  if(!neighbor.equals(toggle) && nodeSet.contains(neighbor)){
	    //get the component that this node belongs to
	    Component nComponent = (Component)node2component.get(neighbor);
	    if(nComponent.getScore() < min_score){
	      hiddenNodes.add(neighbor);
	      nodeSet.remove(neighbor);
	    }
	  }
	}
}
else{
	hiddenNodes.clear();
}
	    
}
}



}
