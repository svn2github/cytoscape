package csplugins.ucsd.rmkelley.GeneticInteractions;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedList;

import giny.model.Node;
import giny.model.Edge;

public class PathFinder{
  
  public static int MAX_DISTANCE = 10;
  HashMap bait2EdgeList;
  HashMap physicalInteractions;
  public PathFinder(HashMap bait2EdgeList, HashMap physicalInteractions){
    this.bait2EdgeList = bait2EdgeList;
    this.physicalInteractions = physicalInteractions;
  }

  public int [] getDistanceCounts(){
    Set baits = bait2EdgeList.keySet();
    int [] result = new int[MAX_DISTANCE];
    for (Iterator baitIt = baits.iterator();baitIt.hasNext();) {
      Node bait = (Node)baitIt.next();
      if (GeneticInteractions.DEBUG) {
	System.out.println("Finding paths from "+bait);
      } // end of if ()
      
      //determine the set of nodes which are interaction partners with
      //this bait
      List edgeList = (List)bait2EdgeList.get(bait);
      HashSet preys = new HashSet();
      for (Iterator edgeIt = edgeList.iterator();edgeIt.hasNext();) {
	Edge current = (Edge)edgeIt.next();
	if (current.getSource().equals(bait)) {
	  preys.add(current.getTarget());
	  	  
	} // end of if ()
	else {
	  if (!current.getTarget().equals(bait)) {
	    throw new RuntimeException("Bait isn't source or target, somebody needs to be fired");
	  } // end of if ()
	  else {
	    preys.add(current.getSource());
	  } // end of else
	} // end of else
      } // end of for ()
      
      //start a search from the bait to look for preys, when
      //one is found, remove it from the set of preys, and update
      //the distance count array, based on the search, we are guarenteed
      //to find them in the correct order
   
      HashMap distances = new HashMap();
      LinkedList queue = new LinkedList();
      queue.add(bait);
      distances.put(bait,new Integer(0));
      while (!preys.isEmpty()) {
	if (queue.isEmpty()) {
	  result[MAX_DISTANCE-1] += preys.size();
	  if (GeneticInteractions.DEBUG) {
	    for ( Iterator preyIt = preys.iterator();preyIt.hasNext();) {
	    	      System.out.println(bait+" gl "+preyIt.next()+" has no convergent path");
	    }
	  }
	  break;
	} // end of if ()
	
	Node current = (Node)queue.removeFirst();
	int distance = ((Integer)distances.get(current)).intValue();
	if ( preys.contains(current)) {
	  preys.remove(current);
	  result[distance]++;
	} // end of if ()
	Set neighbors = (Set)physicalInteractions.get(current);
	for (Iterator neighborIt = neighbors.iterator();neighborIt.hasNext();) {
	  Node neighbor = (Node)neighborIt.next();
	  if (!distances.keySet().contains(neighbor)) {
	    distances.put(neighbor,new Integer(distance+1));
	    queue.add(neighbor);
	  } // end of if ()
	} // end of for ()
      }
    }
    return result;
  }
}
