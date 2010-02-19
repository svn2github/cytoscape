package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import giny.model.GraphPerspective;
import giny.model.Node;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.CyNetwork;
import cytoscape.logger.CyLogger;

/**
 * This class contains the main logic for finding Active Paths The important
 * function is findActivePaths() which calls the simulated annealing subroutine
 * to find the active paths
 */
public class ActivePathsFinder {

	private static CyLogger logger = CyLogger.getLogger( ActivePathsFinder.class );

	/**
	 * See constructor
	 */
	private String[] attrNames;
	/**
	 * See constructor
	 */
	private CyNetwork cyNetwork;
	/**
	 * parameters for path finding
	 */
	private ActivePathFinderParameters apfParams;
	/**
	 * an array containing all of the nodes initially in the graph
	 */
	private Node[] nodes;
	/**
	 * This is a hashmap which maps from nodes to an array of edges (Edge []).
	 * This is used to determine which edges belonged to which nodes before any
	 * changes were made to the graph. This hash map is then used to recover
	 * edges when reinserting nodes into a graph it is initialized in
	 * setupScoring() and used in toggleNode()
	 */
	// HashMap node2edges;
	// Global Variables for the Greedy Search
	/**
	 * Maps from a node to the best component found for that node
	 */
	HashMap node2BestComponent;
	/**
	 * The neighborhood for the current best component
	 */
	// HashSet bestNeighborhood;
	HashMap expressionMap;
	JFrame parentFrame;
	ActiveModulesUI parentUI;
	protected static int DISPLAY_STEP = 50;

	/**
	 * This is the only constructor for ActivePathsFinder. In order to find the
	 * paths, we need certain information.
	 * 
	 * @param attrNames
	 *            The names of hte attributes which correspond to significance
	 * @param cyNetwork
	 *            The cyNetwork which contains our graph, should divorce this
	 *            from the window before running
	 * @param apfp
	 *            The object specifying the parameters for this run
	 * @param parentFrame
	 *            The JFrame which is our parent window, if this is null, then
	 *            we won't display any progress information
	 */
	public ActivePathsFinder(HashMap expressionMap, String[] attrNames,
			CyNetwork cyNetwork, ActivePathFinderParameters apfp,
			JFrame parentFrame, ActiveModulesUI parentUI) {
		this.expressionMap = expressionMap;
		this.parentFrame = parentFrame;
		this.attrNames = attrNames;
		this.cyNetwork = cyNetwork;
		this.parentUI = parentUI;
		apfParams = apfp;

	}

	/**
	 * This function will determine a score for the nodes currently selected in
	 * the graph. It does not try to determine components from these selected
	 * nodes, merely assuming they are all in the same connected component.
	 * 
	 * @param nodeList
	 *            the node list
	 * @return The score for the selected nodes
	 */
	public double scoreList(List nodeList) {
		setupScoring();
		// Vector result = new Vector();
		// Iterator it =
		// cytoscapeWindow.getView().getSelectedNodes().iterator();
		// while(it.hasNext()){
		// result.add(((NodeView)it.next()).getNode());
		// }
		Component selected = new Component(nodeList);
		return selected.getScore();
	}

	/**
	 * In order to score the components, we need to set up certain data
	 * structures first. Mainly this includes seting up the z scores table and
	 * the monte carlo correction. Chris sez it shouldn't be called a monte
	 * carlo correction, and I tend to agree, but this has some historical
	 * inertia behind it (ie, it would involve changing maybe 6 lines of code,
	 * which is simply unthinkable) These data structures are initialized as
	 * static data structures in the Component class, where the scoring is
	 * actually done
	 */
	private void setupScoring() {
		GraphPerspective perspective = cyNetwork;
		// Here we initialize the z table. We use this data structure when we
		// want to get an adjusted z score
		// based on how many conditions we are looking at.
		logger.info("Initializing Z Table");
		Component.zStats = new ZStatistics(attrNames.length);
		logger.info("Done initializing Z Table");

		nodes = new Node[1];
		nodes = (Node[]) (perspective.nodesList().toArray(nodes));

		// Edge [] e_array;
		// This has is used to store all the edges that were connected
		// to a particular node
		// node2edges = new HashMap();
		// for(int i = 0;i<nodes.length;i++){
		// Edge [] temp = new Edge[0];
		// node2edges.put(nodes[i],perspective.getAdjacentEdgesList(nodes[i],true,true,true).toArray(temp));
		// }

		// Component.node2edges = node2edges;
		Component.graph = perspective;
		// Component needs the condition names to return which conditions
		// yield significant scores
		Component.attrNames = attrNames;
		// Determine whether or not we want to correct for the size
		// of active paths
		Component.monteCorrection = apfParams.getMCboolean();
		Component.regionScoring = apfParams.getRegionalBoolean();
		Component.exHash = expressionMap;
		// Initialize the param statistics object. The pStats object uses
		// randomized methods ot determine the
		// mean and standard deviation for networks of size 1 through n.
		Component.pStats = new ParamStatistics(new Random(apfParams
				.getRandomSeed()), Component.zStats);
		// The statistics object is required fro the component scoring function
		// We want to use a monte carlo correction
		if (apfParams.getMCboolean()) {
			boolean failed = false;
			// and we want to load the state from a file
			if (apfParams.getToUseMCFile()) {
				// read in the monte carlo file, it is stored as a serialized
				// ParamStatistics object
				logger.info("Trying to read monte carlo file");
				try {
					FileInputStream fis = new FileInputStream(apfParams
							.getMcFileName());
					ObjectInputStream ois = new ObjectInputStream(fis);
					Component.pStats = (ParamStatistics) ois.readObject();
					ois.close();
					if (Component.pStats.getNodeNumber() != nodes.length) {
						// whoops, the file we loaded doesn't look like it
						// contains the correct information for the set
						// of nodes we are dealing with, user specified a bad
						// file, I hope he feels shame
						logger.info("Monte Carlo file calculated for incorrect number of nodes. Using correct file?");
						failed = true;
						throw new Exception("wrong number of nodes");
					}
				} catch (Exception e) {
					logger.warn("Loading monte carlo file failed", e);
					failed = true;
				}
			}

			if (failed || !apfParams.getToUseMCFile()) {
				logger.info("Initializing monte carlo state");
				MyProgressMonitor progressMonitor = null;
				if (parentFrame != null) {
					progressMonitor = new MyProgressMonitor(parentFrame,
							"Sampling Mean and Standard Deviation", "", 0,
							ParamStatistics.DEFAULT_ITERATIONS);
				} // end of if ()

				// Component.pStats.calculateMeanAndStd(nodes,ParamStatistics.DEFAULT_ITERATIONS,apfParams.getMaxThreads(),
				// new MyProgressMonitor(cytoscapeWindow, "Sampling Mean and
				// Standard
				// Deviation","",0,ParamStatistics.DEFAULT_ITERATIONS));
				Component.pStats.calculateMeanAndStd(nodes,
						ParamStatistics.DEFAULT_ITERATIONS, apfParams
								.getMaxThreads(), progressMonitor);
				logger.info("Finished initializing monte carlo state");

				logger.info("Trying to save monte carlo state");
				try {
					FileOutputStream fos = new FileOutputStream("last.mc");
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(Component.pStats);
					oos.close();
					logger.info("Saved monte carlo state to last.mc");
				} catch (Exception e) {
					logger.error("Failed to save monte carlo state", e);
				}
			}
		}

	}

	/**
	 * This is hte method called to determine the activePaths. Its operation
	 * depends on the parameters specified in hte activePathsFinderParameters
	 * object passed into the constructor.
	 */
	public Component[] findActivePaths() {
		setupScoring();
		Vector comps;
		if(apfParams.getGreedySearch()){
		//if (apfParams.getSearchDepth() > 0) {
			// this will read the parameters out of apfParams and
			// store the result into bestComponnet
			logger.info("Starting greedy search");
			runGreedySearch();
			logger.info("Greedy search finished");

			// after the call to run greedy search, each node is associated
			// with the best scoring component to which it belongs. Need to
			// take the values from this hashmap and put them into a vector
			// so that there are no duplicates.
			comps = new Vector(new HashSet(node2BestComponent.values()));

		} else {
			logger.info("Starting simulated annealing");
			Vector resultPaths = new Vector();
			MyProgressMonitor progress = null;
			if (parentFrame != null) {
				progress = new MyProgressMonitor(parentFrame,
						"Running Simulated Annealing", "", 0, (int) Math
								.ceil(apfParams.getTotalIterations()
										/ (double) DISPLAY_STEP));
			} // end of if ()
			Thread thread = new SimulatedAnnealingSearchThread(cyNetwork,
					resultPaths, nodes, apfParams,
					progress);
			thread.start();
			try {
				thread.join();
			} catch (Exception e) {
				logger.error("Failed to rejoin simulated annealing search thread",e);
				return new Component[0];	
			}
			if (progress != null) {
				progress.close();
			} // end of if ()
			logger.info("Finished simulated annealing run");
			if (apfParams.getToQuench()) {
				logger.info("Starting quenching run");
				SortedVector oldPaths = new SortedVector(resultPaths);
				resultPaths = new Vector();
				thread = new QuenchingSearchThread(cyNetwork,
						resultPaths, nodes, apfParams,
						oldPaths);
				thread.start();
				try {
					thread.join();
				} catch (Exception e) {
					logger.error("Failed to rejoin Quenching Search Thread",e);
					return new Component[0];	
				}
				logger.info("Quenching run finished");

			}
			comps = new Vector(resultPaths);
			// restoreNodes();
		}

		Collections.sort(comps);
		comps = filterResults(comps);

		/*
		 * Finalize the display information
		 */
		//for(Iterator compIt = comps.iterator();compIt.hasNext();){
		//	((Component)compIt.next()).finalizeDisplay();
		//}
		
		Component [] temp = new Component[0];
		int size = Math.min(comps.size(), apfParams.getNumberOfPaths());
		temp = (Component[]) comps.subList(0, size).toArray(temp);
		return temp;
	}
	
	protected Vector filterResults(Vector unfiltered){
		Vector result = new Vector();
		UNFILTERED_LOOP:
		for(Iterator unfilteredIt = unfiltered.iterator();unfilteredIt.hasNext();){
			Component component = (Component)unfilteredIt.next();
			component.finalizeDisplay();
			for(Iterator resultIt = result.iterator();resultIt.hasNext();){
				Component prevComponent  = (Component)resultIt.next();
				if(overlap(component,prevComponent) > apfParams.getOverlapThreshold()){
				    continue UNFILTERED_LOOP;
				}				
			}
			result.add(component);
			if(result.size() >= apfParams.getNumberOfPaths()){
			    break;
			}
		}
		return result;
	}

	private double overlap(Component component, Component prevComponent) {
	    HashSet nodeSet = new HashSet(prevComponent.getDisplayNodes());
	    int intersection = 0;
	    for(Iterator nodeIt = component.getDisplayNodes().iterator();nodeIt.hasNext();){
		if(nodeSet.contains(nodeIt.next())){
		    intersection++;
		}
	    }
	    return intersection/(double)(component.getDisplayNodes().size());
	}

	/**
	 * Runs the greedy search algorithm. This function will run a greedy search
	 * iteratively using each node of the graph as a starting point
	 */
	private void runGreedySearch() {
		if (apfParams.getSearchFromNodes()) {
			runGreedySearch(cyNetwork.getSelectedNodes());
		} else {
			runGreedySearch(cyNetwork.nodesList());
		}
	}

	private void runGreedySearch(Collection seedList) {
		// initialize global best score
		node2BestComponent = new HashMap();

		// List seedList = null;
		// initialize the list of nodes we will start searching from
		// if(apfParams.getSearchFromNodes()){
		// search from a subset of nodes that the user has selected
		// seedList = cytoscapeWindow.getView().getSelectedNodes();
		// }
		// else{
		// seedList =
		// cytoscapeWindow.getView().getGraphPerspective().nodesList();
		// }

		// run a greedy search using each node in our starting
		// list in a starting point
		MyProgressMonitor progressMonitor = null;
		if (parentFrame != null) {
			progressMonitor = new MyProgressMonitor(parentFrame,
					"Performing Greedy Search", "", 0, seedList.size());
		}
		int number_threads = apfParams.getMaxThreads();
		Vector threadVector = new Vector();
		for (int i = 0; i < number_threads; i++) {
			GreedySearchThread gst = new GreedySearchThread(cyNetwork,
					apfParams, seedList, progressMonitor,
					node2BestComponent, nodes);
			gst.start();
			threadVector.add(gst);
		}

		// wait for the threads to finish
		Iterator it = threadVector.iterator();
		while (it.hasNext()) {
			try {
				((Thread) it.next()).join();
			} catch (Exception e) {
				logger.error("Failed to join thread",e);
				return;	
			}
		}
		if (progressMonitor != null) {
			progressMonitor.close();
		}
	}

}
