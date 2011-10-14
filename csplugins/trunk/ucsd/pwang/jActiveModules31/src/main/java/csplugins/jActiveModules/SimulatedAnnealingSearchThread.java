package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import giny.model.GraphPerspective;
import giny.model.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.logger.CyLogger;

public class SimulatedAnnealingSearchThread extends SearchThread{
    MyProgressMonitor progress;
    public SimulatedAnnealingSearchThread(GraphPerspective graph, Vector resultPaths, Node [] nodes, ActivePathFinderParameters apfParams, MyProgressMonitor progress){
	super(graph,resultPaths,nodes,apfParams);
	this.progress = progress;
	super.nodeSet = new HashSet(graph.nodesList());
    }

    /**
     *This runs the simulated annealing algorithm
     *and returns an array of ActivePaths which represent
     *the determined active paths. After the simulated annealing run
     *the activePaths are found in oldPaths
     */
    public void run(){
	int timeout = 0;//current number of iterations
	double T = apfParams.getInitialTemperature();
	double temp_step = 1 - Math.pow((apfParams.getFinalTemperature()/apfParams.getInitialTemperature()),(1.0/apfParams.getTotalIterations()));
	Random rand = new Random(apfParams.getRandomSeed());
	//This vector will contain Component objects which refer
	//to the old ActivePaths
	oldPaths = new SortedVector();
	
       
	boolean hubFinding = apfParams.getMinHubSize() > 0;
	if(hubFinding){
	    CyLogger.getLogger( SimulatedAnnealingSearchThread.class).info("Using hub finding: "+apfParams.getMinHubSize());
	}

	

	//NodeList [] components = GraphConnectivity.connectedComponents(graph);
	ComponentFinder cf = new ComponentFinder(graph,nodeSet);
	//Vector components = cf.getComponents(new Vector(graph.nodesList()));

	//why is a new vector being creater here?
	//Iterator compIt = cf.getComponents(new Vector(graph.nodesList())).iterator();
	Iterator compIt = cf.getComponents(graph.nodesList()).iterator();
	while(compIt.hasNext()){
	    //Component tempComponent = new Component((Vector)compIt.next());
	    oldPaths.sortedAdd((Component)compIt.next());
	} 
	
	
	//here we are creating the hashmap which maps from nodes to their
	//respective components. It is important (and sometimes tricky) to 
	//keep this hash map up to date after we change which components
	//are in the graph.
	Iterator it = oldPaths.iterator();
	node2component = new HashMap();
	while(it.hasNext()){
	    Component comp = (Component)it.next();
	    Vector compNodes = comp.getNodes();
	    for(int i=0;i<compNodes.size();i++){
		node2component.put(compNodes.get(i),comp);
	    }
	}
	
	//this starts the simulated annealing loop. The temperature
	//step is set so that we will get to the final temperature
	//after the total number of iterations.
	
	//int display_step = Math.max(1,apfParams.getTotalIterations()/ActivePathsFinder.UPDATE_COUNT);
	int display_step = ActivePathsFinder.DISPLAY_STEP;
	while(timeout < apfParams.getTotalIterations()){
	    timeout++;
	    if(progress != null && timeout%display_step == 0){
		progress.update();
	    }
	    T *= 1 - temp_step;
	    //when using hub finding, don't accidentally specifiy nodes
	    //for removal.
	    
	    hiddenNodes.clear();
	    
	    //select a node
	    Node current_node = nodes[rand.nextInt(nodes.length)];
	    //toggle the state of that node, it we are doing hubfinding, this may
	    //also involve toggling the state of the surrrounding nodes
					if(hubFinding){
		toggleNodeWithHiding(current_node);
	    }
	    else{
		toggleNode(current_node);
	    }

	    //get a vector of the new components created by toggling
	    //the current node, in this call, we also update the status of 
	    //newPaths so that it contains a complete list of the components
	    //that would be present in the graph if we made this move
	
	    Vector newComps = updatePaths(current_node);
	    Iterator tempIt = oldPaths.iterator();
	
	    //here we decide if we want to keep the move we made
	    //the first criteria is that the number of paths
	    //cannot fall belong the minimum number of paths specified
	    //by Mr. user.
	    if(newPaths.size() >= oldPaths.size() || newPaths.size() >=  apfParams.getNumberOfPaths()){
		boolean decision = false;
		boolean keep = true;
		int i = 0;
		Iterator oldIt = oldPaths.iterator();
		Iterator newIt = newPaths.iterator();
		//compare the top scoring old and new paths against each other in order. If we find a 
		//better scoring component, we automatically except the move. Otherwise, use the temperature
		//to reject the move with a certain probability.
		//Note that newIt may be larger, but can not be smaller than oldIt, here we will just compare the
		//scores of oldIt versus the matching elements of the new paths
		while(!decision && (newIt.hasNext() && oldIt.hasNext())){
		    double delta = ((Component)newIt.next()).getScore()-((Component)oldIt.next()).getScore();
		    if(delta > .001){
			keep = true;
			decision = true;
		    }
		    else{
			if(rand.nextDouble() > Math.exp(delta/T)){
			    keep = false;
			    decision = true;
			}    
		    }
		    i++;
		}
		if(keep){
		    //we want to keep the move, update the status
		    //of all the necessary data structures. An important update
		    //is the hashmap from nodes to components. We need to vector
		    //of new components so that we can quickly update the hash (without
		    //rehashing everything in newPaths.
		    oldPaths = newPaths;
		    it = newComps.iterator();
		    while(it.hasNext()){
			Component currComp = ((Component)it.next());
			Iterator nodeIt = currComp.getNodes().iterator();
			while(nodeIt.hasNext()){
			    node2component.put(nodeIt.next(),currComp);
			}
		    }
		}
		else{
		    //undo hte current move, if we are dong hubfinding, may need
		    //to restore the hidden nodes as well.
		    toggleNode(current_node);
		    if(hubFinding){
			it = hiddenNodes.iterator();
			while(it.hasNext()){
			    toggleNode((Node)it.next());
			}
		    }
		}
	    }
	    else{
		toggleNode(current_node);		
		if(hubFinding){
		    it = hiddenNodes.iterator();
		    while(it.hasNext()){
			toggleNode((Node)it.next());
		    }
		}
	    }
	    
	}
	resultPaths.addAll(oldPaths);
	
    }
}
