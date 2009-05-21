package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import giny.model.GraphPerspective;
import giny.model.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import csplugins.jActiveModules.data.ActivePathFinderParameters;

public class QuenchingSearchThread extends SearchThread{
    public QuenchingSearchThread(GraphPerspective graph, Vector resultPaths, Node [] nodes, ActivePathFinderParameters apfParams, SortedVector oldPaths){
	super(graph,resultPaths,nodes,apfParams);
	super.oldPaths = oldPaths;
	HashSet temp = new HashSet();
	for(Iterator pathIt = oldPaths.iterator();pathIt.hasNext();){
	  for(Iterator nodeIt = ((Component)pathIt.next()).getNodes().iterator();nodeIt.hasNext();){
	    temp.add(nodeIt.next());
	  }
	}
	super.nodeSet = temp;
    }
    
    /**
     *This is the quenching function. It is used to run a greedy
     *search after running simulatedAnnealing. If you use enough
     *iterations in the simulatedAnnealing, this shouldn't be
     *necessary, as the quenching is like simulatedAnnealing except
     *the temperature is zero and we iteratively toggleNodes
     *instead of selecting them at random. In fact, this could probably
     *be implemented so that simulated annealing and quenching share
     *pretty much the same code. Just change the annealing temperature
     *before you run and pass in an object that implements an interface
     *(something like node chooser). The quenching is seeded of the 
     * components currently in oldPaths (components found in the simulated
     * annealing run)
     */
    public void run(){
	//are we still quenching?
	boolean quench = true;
	//have we made a positive change in this cycle?
	boolean improved = false;
	//what node are we now toggling
	int count = 0;
		
	//here we are creating the hashmap which maps from nodes to their
	//respective components. It is important (and sometimes tricky) to 
	//keep this hash map up to date after we change which components
	//are in the graph.
	Iterator compIt = oldPaths.iterator();
	node2component = new HashMap();
	while(compIt.hasNext()){
	    Component comp = (Component)compIt.next();
	    Vector compNodes = comp.getNodes();
	    for(int i=0;i<compNodes.size();i++){
		node2component.put(compNodes.get(i),comp);
	    }
	}

	while(quench){
	    
	    //select a node
	    Node current_node = nodes[count++];
	    toggleNode(current_node);
	    Vector newComps = updatePaths(current_node);
	    
	    if(newPaths.size() >= oldPaths.size() || newPaths.size() >=  apfParams.getNumberOfPaths()){
		
		//compare the new score to the old scores
		boolean decision = false;
		boolean keep = false;
		int i = 0;
		Iterator oldIt = oldPaths.iterator();
		Iterator newIt = newPaths.iterator();
		while(!decision && (newIt.hasNext() && oldIt.hasNext())){
		    double delta = ((Component)newIt.next()).getScore()-((Component)oldIt.next()).getScore();
		    if(delta > .001){
			keep = true;
			decision = true;
		    }
		    else{
			if(delta < 0){
			    keep = false;
			    decision = true;
			}    
		    }
		    i++;
		}
		if(keep){
		    oldPaths = newPaths;
		    //need to update the node2component
		    //has to reflect the newly
		    //generated components
		    Iterator it = newComps.iterator();
		    improved = true;
		    while(it.hasNext()){
			Component currComp = ((Component)it.next());
			Iterator nodeIt = currComp.getNodes().iterator();
			while(nodeIt.hasNext()){
			    node2component.put(nodeIt.next(),currComp);
			}
		    }
		}
		else{
		    //toggle the node back    
		    toggleNode(current_node);
		}
	    }
	    else{
		toggleNode(current_node);		
	    }
	    //check to see if we have made a complete cycle through
	    //all of the nodes
	    if(count == nodes.length){
		//check to see if there was any improvement in this
		//cycle
		if(!improved){
		    quench = false;
		}
		else{
		    //we are going to do another round
		    //so reset the counter and the flag
		    //which indicates whether we have found
		    //an improved score
		    improved = false;
		    count = 0;
		}
		
	    }
	    
	}
	resultPaths.addAll(oldPaths);
	
    }
}
