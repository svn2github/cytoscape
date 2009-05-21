package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import giny.model.GraphPerspective;
import giny.model.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * This class is used to find components. I can't use the one in y.algo, because
 * I need to find components seeded off a certain set of nodes. This class extends
 * the Dfs class, so that we can use the super class functions to do most of the
 * grunt work in the depth first search to find the components (just override the
 * callback functions, like postVisit()
 */
public class ComponentFinder{
  /**
   *The graph which contains the nodes we are searching on
   */
  GraphPerspective graph;
  /**
   *A list of nodes that have been found in the current component search.
   */
  List current;
  /**
   *Quickly determine if we have seen a node in our search yet. I think there is
   *also some way to do this with the fields in the super class, but I didn't 
   *seem to quite work so I just did it this way.
   */
  HashSet reached;
  /**
   * A hashset of nodes which are valid for the current search
   */
  HashSet valid;
  /**
   * Make a new component finder with the specified graph
   * @param g the graph on which to search
   */
  public ComponentFinder(GraphPerspective g,HashSet valid){
    graph = g;
    this.valid = valid;
  }

  /**
   * Return a vector of components reachable from these nodes.
   * @param nodes The nodes which we base our search off of
   */
  public Vector getComponents(List nodes){
    Vector result = new Vector();
    boolean done = false;
    int start = 0;

    int initialSize = nodes.size();
    int finalSize = 0;

    //check to see if there is anything to do
    if(nodes.size() == 0){
      done = true;
    }
    //Iterator it = nodes.iterator();
    reached = new HashSet(2*nodes.size());
    //while there are nodes that we haven't seen in our search yet
    while(!done){
      //make a new list
      current = new Vector();
      //start searching at the current node, the start method will make a 
      //call back to postVisit after it is done searching any node. This is
      //where we will update the status of that node
      if(!valid.contains(nodes.get(start))){
	throw new RuntimeException("Starting node for a component search was not present in the graph, this is an ActiveModules bug."+((Node)nodes.get(start)).getIdentifier());
 				//System.err.println("Starting node for a component search was not present in the grpah, this is an ActiveModules bug."+((Node)nodes.get(start)).getIdentifier());
	}
						
      search((Node)nodes.get(start));
      result.add(new Component(current));
      finalSize += current.size();
      while(start<nodes.size() && reached.contains(nodes.get(start))){
	start++;
      }
      //check if we are out of nodes to examine
      if(start == nodes.size()){
	done = true;
      }
    }
    return result;
  }

  private void search(Node root){
    current.add(root);
    reached.add(root);
    Iterator nodeIt = graph.neighborsList(root).iterator();
    while(nodeIt.hasNext()){
      Node myNode = (Node)nodeIt.next();
      if(valid.contains(myNode) && !reached.contains(myNode)){
	search(myNode);
      }
    }
  }

}
