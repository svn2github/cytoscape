package csplugins.ucsd.rmkelley.GeneticInteractions;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;


import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;

import cytoscape.AbstractPlugin;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;

/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class GeneticInteractions extends AbstractPlugin{
    
    CyWindow cyWindow;
    public static boolean DEBUG = true;
    public static int THREAD_COUNT = 2;
    public static int DISTANCE = 3;
    public static String LETHAL = "lethal";
    public static String GI = "gi";
    public static String YES="yes";
    public static String NO="no";
    
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public GeneticInteractions(CyWindow cyWindow) {
        this.cyWindow = cyWindow;
        cyWindow.getCyMenus().getOperationsMenu().add( new SamplePluginAction() );
    }
    /**
     * This class gets attached to the menu item.
     */
    public class SamplePluginAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public SamplePluginAction() {super("Find and Score Lethal CoPPs");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("Finds and scores lethal CoPPs");
            return sb.toString();
        }
        
	        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
       	    Thread t = new FindAndScoreLethalCoPPThread(cyWindow); 
	    t.start();
           
	}
    }
}
    	

class FindAndScoreLethalCoPPThread extends Thread{
    	CyWindow cyWindow;
	HashMap [] nodepair2count;
	public FindAndScoreLethalCoPPThread(CyWindow cyWindow){
		this.cyWindow = cyWindow;
	}
    	public void run(){
		GraphPerspective myPerspective = cyWindow.getView().getGraphPerspective();
		GraphObjAttributes edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();
		GraphObjAttributes nodeAttributes = cyWindow.getNetwork().getNodeAttributes(); 

		//create a set of all edges that represent genetic interactions
		if(GeneticInteractions.DEBUG){
			System.out.println("Finding all genetic interactions");
		}
		Vector geneticInteractions = getGeneticInteractions(cyWindow);	

		//create a set of all nodes involved in some genetic interaction
		if(GeneticInteractions.DEBUG){
			System.out.println("Creating set of all nodes involved in genetic interaction");
		}
		HashSet geneticInteractionNodes = getGeneticInteractionNodes(geneticInteractions);
		
		//for each node involved in a genetic interaction, create a hashmap that maps
		//from lethal nodes to a vector of paths to that lethal
		if(GeneticInteractions.DEBUG){
			System.out.println("Finding all lethals within "+GeneticInteractions.DISTANCE+" of a genetic interaction, using "+GeneticInteractions.THREAD_COUNT+" threads");
		}
		LethalPathFinder lpf = new LethalPathFinder(cyWindow,geneticInteractionNodes);
		HashMap giNode2HashMap = lpf.findLethalPaths(GeneticInteractions.DISTANCE,GeneticInteractions.THREAD_COUNT);
		
		if(GeneticInteractions.DEBUG){
			System.out.println("Printing information about identified lethal paths");
			Iterator giIt = geneticInteractionNodes.iterator();
			while(giIt.hasNext()){
				Node giNode = (Node)giIt.next();
				HashMap lethal2Vector = (HashMap)giNode2HashMap.get(giNode);
				Iterator lethalIt = lethal2Vector.keySet().iterator();
				while(lethalIt.hasNext()){
					Node lethal = (Node)lethalIt.next();
					Iterator pathIterator = ((Vector)lethal2Vector.get(lethal)).iterator();
					while(pathIterator.hasNext()){
						HashSet path = (HashSet)pathIterator.next();
						System.out.println("Found a path between gi "+giNode+" and lethal "+lethal);
						System.out.println("\t"+path);
						System.out.println();
					}
				}
			}
		}
		
		//for each genetic interaction, find the overlapping lethals
		if(GeneticInteractions.DEBUG){
			System.out.println("Determining lethal CoPPs by finding non-overlapping lethal paths");
		}
		LethalCoPPFinder lethalCoPPFinder = new LethalCoPPFinder(geneticInteractions,giNode2HashMap);
		Vector lethalCoPPs = lethalCoPPFinder.findLethalCoPPs(GeneticInteractions.THREAD_COUNT);

		if(GeneticInteractions.DEBUG){
			System.out.println("Printing information about identified lethal CoPPs");
			Iterator lethalCoPPIt = lethalCoPPs.iterator();
			while(lethalCoPPIt.hasNext()){
				System.out.println(lethalCoPPIt.next());
			}
		}

		//score each of the paired paths to genetic interactions

	}

	/**
	 * Make a set containing all the nodes involved in genetic interactions.
	 * @param geneticInteractions  A vector containing edges which are known to be genetic interactions
	 * @return A HashSet containing all the nodes involved in those interactions
	 */
	private HashSet getGeneticInteractionNodes(Vector geneticInteractions){
		HashSet result = new HashSet();
		Iterator giIt = geneticInteractions.iterator();
		while(giIt.hasNext()){
			Edge currentEdge = (Edge)giIt.next();
			result.add(currentEdge.getSource());
			result.add(currentEdge.getTarget());
		}
		return result;
	}

	/**
	 * Find all edges annotated as a genetic interaction.
	 * @param cyWindow The window in which to look for edges
	 * @return A vector containing all the edges that are genetic interactions
	 */
	private Vector getGeneticInteractions(CyWindow cyWindow){
		Vector result = new Vector();
		GraphObjAttributes edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();	
		Iterator edgeIt = cyWindow.getView().getGraphPerspective().edgesIterator();
		while(edgeIt.hasNext()){
			Edge currentEdge = (Edge)edgeIt.next();
			String interaction = (String)edgeAttributes.get("interaction",edgeAttributes.getCanonicalName(currentEdge));
			if(interaction.equals(GeneticInteractions.GI)){
				result.add(currentEdge);
			}
		}
		return result;
	}
}

/**
 * Given genetic interactions, this class tries to use information about previously determined
 * lethal paths to come up with lethal CoPPs. (Convergent Pairs of paths which end in a lethal
 * deletion.
 */
class LethalCoPPFinder{
	/**
	 * See constructor
	 */
	private Vector geneticInteractions;
	/**
	 * See constructor
	 */
	private HashMap giNode2HashMap;
	/**
	 * Constructor
	 * @param geneticInteractions A vector containing all of the edges which represent genetic interactions
	 * @param giNode2HashMap The hashmap containing all of the lethal path information
	 */
	public LethalCoPPFinder(Vector geneticInteractions,HashMap giNode2HashMap){
		this.geneticInteractions = geneticInteractions;
		this.giNode2HashMap = giNode2HashMap;
	}

	/**
	 * Find the lethalCopps
	 * @param thread_count the number of threads to use in the search
	 * @return A LethalCoPP vector
	 */
	public Vector findLethalCoPPs(int thread_count){
		Vector result = new Vector();
		Thread[] threads = new Thread[thread_count];
		Iterator geneticInteractionsIt = geneticInteractions.iterator();
		for(int i=0;i<thread_count;i++){
			threads[i] = new LethalCoPPFinderThread(geneticInteractionsIt,giNode2HashMap,result);
		}
		for(int i=0;i<thread_count;i++){
			threads[i].start();
		}
		for(int i=0;i<thread_count;i++){
			try{
				threads[i].join();
			}catch(InterruptedException ie){
				ie.printStackTrace();
				System.exit(-1);
			}
		}
		return result;
	}
}
/**
 * This is an individual worker thread for identifying lethal CoPPs from
 * lethal paths
 */
class LethalCoPPFinderThread extends Thread{
	/**
	 * See constructor
	 */
	Iterator geneticInteractionsIt;
	/**
	 * See constructor
	 */
	HashMap giNode2HashMap;
	/**
	 * The vector into which results are stored
	 */
	Vector result;
	/**
	 * Constructor
	 * @param geneticInteractions A vector containing all of the edges which represent genetic interactions
	 * @param giNode2HashMap The hashmap containing all of the lethal path information
	 */
	public LethalCoPPFinderThread(Iterator geneticInteractionsIt, HashMap giNode2HashMap, Vector result){
		this.geneticInteractionsIt = geneticInteractionsIt;
		this.giNode2HashMap = giNode2HashMap;
		this.result = result;
	}

	/**
	 * This is the function responsible for doing the lethal CoPP finding
	 */
	public void run(){
		boolean done = true;
		Edge currentEdge = null;
		synchronized(geneticInteractionsIt){
			if(geneticInteractionsIt.hasNext()){
				currentEdge = (Edge)geneticInteractionsIt.next();
				done = false;
			}
		}
		while(!done){
			done = true;
			Node source = currentEdge.getSource();
			Node target = currentEdge.getTarget();
			//get the set of neighbor lethals for both source and target
			HashMap sourceHashMap = (HashMap)giNode2HashMap.get(source);
			HashMap targetHashMap = (HashMap)giNode2HashMap.get(target);
			Set sourceSet = sourceHashMap.keySet();
			Set targetSet = targetHashMap.keySet();
			sourceSet.retainAll(targetSet);
			//source now contains the union of the two sets
			Iterator unionIt = sourceSet.iterator();
			while(unionIt.hasNext()){
				Node lethal = (Node)unionIt.next();
				Iterator sourcePathVectorIt = ((Vector)sourceHashMap.get(lethal)).iterator();
				//now look at all combinations for paths that do not overlapp
				while(sourcePathVectorIt.hasNext()){
					Set sourcePath = (Set)sourcePathVectorIt.next();
					Iterator targetPathVectorIt = ((Vector)targetHashMap.get(lethal)).iterator();
					while(targetPathVectorIt.hasNext()){
						Set targetPath = (Set)targetPathVectorIt.next();
						boolean overlap = false;
						Iterator targetNodeIt = targetPath.iterator();
						while(targetNodeIt.hasNext() && !overlap){
							overlap = sourcePath.contains(targetNodeIt.next());
						}
						if(!overlap){
							//these are non-overlapping paths, therefore
							//this is a valid lethal copp add, it to the results
							LethalCoPP lethalCoPP = new LethalCoPP(lethal,sourcePath,targetPath);
							synchronized(result){
								result.add(lethalCoPP);
							}
						}
						

					}
				}
			}
			synchronized(geneticInteractionsIt){
				if(geneticInteractionsIt.hasNext()){
					currentEdge = (Edge)geneticInteractionsIt.next();
					done = false;
				}
			}
		}
	}
}

/**
 * This class represents a lethal CoPP. This is a pair of paths form a genetic interaction
 * which both end in the same lethal deletion. These paths are assumed to be non-overlapping
 * (in terms of nodes)
 */
class LethalCoPP{
	/**
	 * See constructor
	 */
	Node lethal;
	/**
	 * See constructor
	 */
	Set one;
	/**
	 * See constructor
	 */
	Set two;
	/**
	 * Constructor
	 * @param lethal The convergent lethal node of this lethal CoPP
	 * @param one The first path of this pair of paths (represnted as a set)
	 * @param two The second path of this pair of paths (repersented as a set);
	 */
	public LethalCoPP(Node lethal,Set one,Set two){
		this.lethal = lethal;
		this.one = one;
		this.two = two;
	}

	/**
	 * Return string repersentation of this object.
	 * Basically just prints out the two sets
	 */
	public String toString(){
		return ""+lethal+" , "+one+" , "+two;
	}
}
/**
 *This class is responsible for finding paths from nodes involved in genetic
 *nodes with a lethal annotation.
 */
class LethalPathFinder{
	/**
	 * The window in which we are looking for paths
	 */
	private CyWindow cyWindow;
	/**
	 * Contains all nodes known to be involved in genetic interactions
	 */
	private HashSet geneticInteractionNodes;
	
	/**
	 * Constructor.
	 * @param cyWindow The window in which we are looking for paths
	 * @param geneticInteractionNodes a HashSet containing all nodes involved in a genetic interaction.
	 */
	public LethalPathFinder(CyWindow cyWindow, HashSet geneticInteractionNodes){
		this.cyWindow = cyWindow;
		this.geneticInteractionNodes = geneticInteractionNodes;
	}

	/**
	 * Function responsible for finding and return the paths. It will return the paths
	 * in a hashmap.
	 * @param distance The maximum distance to look for lethals
	 * @param thread_count The number of threads to use in the search.
	 * @return a HashMap containing the determined paths. The keys of this hashmap
	 * are the nodes involved in genetic interactions and the values are another HashMap. This hashmap maps
	 * from lethal nodes to a vector of paths (which are represented as HashSets).
	 */
	public HashMap findLethalPaths(int distance, int thread_count){
		HashMap result = new HashMap();
		Thread [] threads = new Thread [thread_count];
		//create a hashmap that maps from each node to a set of physicall interacting nodes
		HashMap node2Set = new HashMap();
		GraphPerspective myPerspective = cyWindow.getView().getGraphPerspective();
		Iterator nodeIt = myPerspective.nodesIterator();
		GraphObjAttributes edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();
		while(nodeIt.hasNext()){
			Node current = (Node)nodeIt.next();
			Set currentSet = new HashSet();
			node2Set.put(current,currentSet);
			Iterator edgeIt = myPerspective.getAdjacentEdgesList(current, true, true, true).iterator();
			while(edgeIt.hasNext()){
				Edge currentEdge = (Edge)edgeIt.next();
				String interaction = (String)edgeAttributes.get("interaction",edgeAttributes.getCanonicalName(currentEdge));
				if(interaction.equals("pp") || interaction.equals("pd")){
					if(currentEdge.getSource() == current){
						currentSet.add(currentEdge.getTarget());
					}
					else{
						currentSet.add(currentEdge.getSource());
					}
				}
			}
		}

		
		for(int i=0;i<thread_count;i++){
			threads[i] = new LethalPathFinderThread(cyWindow,node2Set,geneticInteractionNodes.iterator(),result,distance);
		}
		for(int i=0;i<thread_count;i++){
			threads[i].start();
		}

		for(int i=0;i<thread_count;i++){
			try{
				threads[i].join();
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
				System.exit(-1);
			}
		}
		return result;
	}
}

/**
 * This is a worker thread that will find paths from genetic interactions
 * to lethal nodes.
 */
class LethalPathFinderThread extends Thread{
	/**
	 * Maps from a node to a set of physical neighbors 
	 */
	private HashMap node2Set;

	/**
	 * See constructor
	 */
	private Iterator giIt;

	/**
	 * See constructor
	 */
	private HashMap result;
	/**
	 * See constructor
	 */
	private int depth;
	/**
	 * Keeps track of the nodes seen already in the search
	 */
	private HashSet seenNodes;
	/**
	 * HashSet of all the lethal nodes, hopefully faster than trying to do the lookup
	 * in the reviled GraphObjAttributes every time
	 */
	private HashSet lethalNodes;
	/**
	 * Node that we are currently searching from, need this to be able to correctly associate
	 * results
	 */
	private Node currentSearch; 
	/**
	 * Constructor
	 * @param cyWindow The window in which to look for paths
	 * @param giIt An iterator over nodes involved in genetic interactions, make sure access to this is synchronized
	 * @param result The HashMap where the results will be added
	 * @param depth The depth of the grah search
	 */
	public LethalPathFinderThread(CyWindow cyWindow,HashMap node2Set,Iterator giIt, HashMap result,int depth){
		this.node2Set = node2Set; 
		this.giIt = giIt;
		this.result = result;
		this.depth = depth;
		//create the hashset of lethal nodes, each worker thread
		//makes it's own copy so there is not competition for access.
		lethalNodes = new HashSet();
		GraphObjAttributes nodeAttributes = cyWindow.getNetwork().getNodeAttributes();
		Iterator nodeIt = cyWindow.getView().getGraphPerspective().nodesIterator();
		while(nodeIt.hasNext()){
			Node current = (Node)nodeIt.next();
			String lethal = (String)nodeAttributes.get(GeneticInteractions.LETHAL,nodeAttributes.getCanonicalName(current));
			if(lethal == null){
				throw new RuntimeException("Unable to lookup value for "+GeneticInteractions.LETHAL);
			}
			else{
				if(lethal.toLowerCase().equals(GeneticInteractions.YES)){
					lethalNodes.add(current);
				}
				else if(!lethal.toLowerCase().equals(GeneticInteractions.NO)){
					throw new RuntimeException("Expected "+GeneticInteractions.YES+" or "+GeneticInteractions.NO+" for attribute "+GeneticInteractions.LETHAL);
				}
			}
		}
	}

	/**
	 * This will start the pathfinding procedure and store any results
	 * found into "result";
	 */
	public void run(){
		boolean done = true;
		Node current = null;
		synchronized(giIt){
			if(giIt.hasNext()){
				current = (Node)giIt.next();
				done = false;
			}
		}
		while(!done){
			done = true;
			//search from current and store the results
			seenNodes = new HashSet();
			currentSearch = current;
			synchronized(result){
				result.put(currentSearch, new HashMap());
			}
			findPaths(current,depth);
			//check to see if there is more work to be done
			synchronized(giIt){
				if(giIt.hasNext()){
					current = (Node)giIt.next();
					done = false;
				}
			}			
		}
	}
	/**
	 * Recursive function to find lethal nodes
	 * @param current The current location of the search
	 * @param distance The remaining depth in the search
	 */
	private void findPaths(Node current, int distance){
		//base case, no more depth left in search
		if(distance > 0){	
			//check to see if the current node is a lethal
			if(lethalNodes.contains(current)){
				//add the current path to the results
				HashMap lethalNode2Vector;
				synchronized(result){
					lethalNode2Vector = (HashMap)result.get(currentSearch);
				}
				//we are the only thread that will try to access this hashmap
				Vector pathVector = (Vector)lethalNode2Vector.get(current);
				if(pathVector == null){
					pathVector = new Vector();
					lethalNode2Vector.put(current,pathVector);
				}
				//create a copy of the current path so far
				HashSet currentPath = new HashSet(seenNodes);
				pathVector.add(currentPath);
			}
			seenNodes.add(current);
			//get the list of next nodes and initiate a
			//search from each of those, if they have not 
			//already been seen
			Iterator neighborIt = ((Set)node2Set.get(current)).iterator();
			while(neighborIt.hasNext()){
				Node neighbor = (Node)neighborIt.next();
				if(!seenNodes.contains(neighbor)){
					findPaths(neighbor,distance-1);
				}
			}
			//remove the current
			seenNodes.remove(current);
		}
	}
}
