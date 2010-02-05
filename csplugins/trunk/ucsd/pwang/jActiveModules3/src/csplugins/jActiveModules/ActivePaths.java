// ActivePaths.java:  a plugin for CytoscapeWindow,
// which uses VERA & SAM expression data
// to propose active gene regulatory paths
//------------------------------------------------------------------------------
// $Revision: 11526 $
// $Date: 2007-09-05 14:14:24 -0700 (Wed, 05 Sep 2007) $
// $Author: rmkelley $
//------------------------------------------------------------------------------
package csplugins.jActiveModules;
//------------------------------------------------------------------------------
import giny.model.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import csplugins.jActiveModules.Component;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectFilter;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.PropUtil;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import java.util.HashSet;
import cytoscape.data.Semantics;

//-----------------------------------------------------------------------------------
public class ActivePaths implements ActivePathViewer, Runnable {

	protected boolean showTable = true;
	protected boolean hideOthers = true;
	protected boolean randomize = false;

	protected JMenuBar menubar;
	protected JMenu expressionConditionsMenu;
	protected String currentCondition = "none";
	protected Component[] activePaths;
	protected String[] attrNames;
	protected static boolean activePathsFindingIsAvailable;
	protected JButton activePathToolbarButton;
	protected JFrame mainFrame;
	protected CyNetwork cyNetwork;
	protected String titleForCurrentSelection;
	protected ActivePathFinderParameters apfParams;
	protected static double MIN_SIG = 0.0000000000001;
	protected static double MAX_SIG = 1 - MIN_SIG;

	protected static int resultsCount = 1;
	protected ActiveModulesUI parentUI;
	
	private static int MAX_NETWORK_VIEWS = PropUtil.getInt(CytoscapeInit.getProperties(), "moduleNetworkViewCreationThreshold", 0);
	private static int pathCount = 0;	
	private static int runCount = 0;	
	
	//public static String groupViewerName = "moduleFinderViewer";
	
	// ----------------------------------------------------------------
	public ActivePaths(CyNetwork cyNetwork, ActivePathFinderParameters apfParams, ActiveModulesUI parentUI) {
		this.apfParams = apfParams;
		if (cyNetwork == null || cyNetwork.getNodeCount() == 0) {
			throw new IllegalArgumentException("Please select a network");
		}

		attrNames = (String[])apfParams.getExpressionAttributes().toArray(new String[0]);
		Arrays.sort(attrNames);
		if (attrNames.length == 0) {
			throw new RuntimeException("No expression data selected!");
		}
		menubar = Cytoscape.getDesktop().getCyMenus().getMenuBar();
		mainFrame = Cytoscape.getDesktop();
		this.cyNetwork = cyNetwork;
		this.parentUI = parentUI;
		
	} // ctor

	// --------------------------------------------------------------
	protected void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}
	protected void clearActivePaths() {
		this.activePaths = null;
	}

	public void run() {
	    System.gc();
		//long start = System.currentTimeMillis();
		HashMap expressionMap = generateExpressionMap();
		// run the path finding algorithm
		ActivePathsFinder apf = null;
		if(randomize){
		    apf = new ActivePathsFinder(expressionMap, attrNames,
						cyNetwork, apfParams, null, parentUI);
		}else{
		    apf = new ActivePathsFinder(expressionMap, attrNames,
						cyNetwork, apfParams, mainFrame, parentUI);
		}
		activePaths = apf.findActivePaths();

		// create nested networks
		//1 . create subnetwork for each path
		CyNetwork[] subnetworks = createSubnetworks();
		
		//2. create an overview network for all nested network
		
		Set<CyNode>  path_nodes = new HashSet<CyNode>();
		for (int i=0; i< subnetworks.length; i++){
			CyNode newNode =Cytoscape.getCyNode(subnetworks[i].getTitle(), true); 
			path_nodes.add(newNode);
			newNode.setNestedNetwork(subnetworks[i]);
			// create an attribute for this new node
			Cytoscape.getNodeAttributes().setAttribute(newNode.getIdentifier(), "activepath_score", new Double(activePaths[i].getScore()));
		}
		
		//Edges indicate that nodes in nested networks exist in both nested networks
		Set<CyEdge>  path_edges = getPathEdges(path_nodes); //new HashSet<CyEdge>();
		
		Cytoscape.createNetwork(path_nodes, path_edges, "Overview"+ "_"+ runCount++, cyNetwork);;
	}

	private Set<CyEdge> getPathEdges(Set path_nodes) {
		HashSet<CyEdge> edgeSet = new HashSet<CyEdge>();
		
		Object[] nodes = path_nodes.toArray();
		
		HashSet[] hashSet = new HashSet[nodes.length];
		for (int i=0; i< nodes.length; i++){
			hashSet[i] = new HashSet<CyNode>(((CyNode)nodes[i]).getNestedNetwork().nodesList());
		}
		
		for (int i=0; i< nodes.length-1; i++){
			for (int j=i+1; j<nodes.length; j++){
				// determine if there are overlap between nested networks
				if (hasTwoSetOverlap(hashSet[i], hashSet[j])){
					CyEdge edge = Cytoscape.getCyEdge((CyNode)nodes[i], (CyNode)nodes[j], Semantics.INTERACTION, "overlap", true);
					edgeSet.add(edge);
				}
			}
		}
		
		return edgeSet;
	}
	
	
	private boolean hasTwoSetOverlap(HashSet<CyNode> set1, HashSet<CyNode> set2) {
		Iterator<CyNode> it = set1.iterator();
		while (it.hasNext()){
			if (set2.contains(it.next())){
				return true;
			}
		}		
		return false;
	}
	
	
	private CyNetwork[] createSubnetworks() {
		CyNetwork[] subnetworks = new CyNetwork[activePaths.length];

		for (int i = 0; i < activePaths.length; i++) {
			Component thePath = activePaths[i];
			String pathName = "Path_" + runCount + "_" + (i + 1);
			
			// get nodes for this path
			Vector nodeVect = (Vector) thePath.getDisplayNodes();
			Set<CyNode> nodeSet = new HashSet<CyNode>();
			for (int j = 0; j < nodeVect.size(); j++) {
				CyNode oneNode = (CyNode) nodeVect.elementAt(j);
				if (oneNode != null)
					nodeSet.add(oneNode);
			}
			
			// get edges for this path
			Set edgeSet = new HashSet();
			Iterator iterator = cyNetwork.edgesIterator();
			while (iterator.hasNext()) {
				CyEdge edge = (CyEdge) iterator.next();
				if (nodeSet.contains(edge.getSource()) && nodeSet.contains(edge.getTarget()))
					edgeSet.add(edge);
			}
			
			final boolean createView = i < MAX_NETWORK_VIEWS;
			subnetworks[i] = Cytoscape.createNetwork(nodeSet, edgeSet, pathName, cyNetwork, createView);
		}
		
		return subnetworks;
	}
	
	/**
	 * Returns the best scoring path from the last run. This is mostly used by
	 * the score distribution when calculating the distribution
	 */
	protected Component getHighScoringPath() {
		System.err.println("High Scoring Path:");
		System.err.println(activePaths);
		System.err.println("Score: " + activePaths[0].getScore());
		int size = activePaths[0].getNodes().size();
		System.err.println("Size: " + size);
		System.err.println("Raw score: "
				+ activePaths[0].calculateSimpleScore());
		System.err.println("Mean: "
				+ Component.pStats.getMean(size));
		System.err.println("Std: "
				+ Component.pStats.getStd(size));
		return activePaths[0];
	}

	protected HashMap generateExpressionMap() {
		// set up the HashMap which is used to map from nodes
		// to z values. At this point, we are mapping from the
		// p values for expression to z values
		System.err.println("Processing Expression Data into Hash");
		HashMap tempHash = new HashMap();
		System.err.println("Do some testing of the ExpressionData object");
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Create two identical lists of genes
		List<Node> geneList = new ArrayList<Node>();
		List<Node> shuffledList = new ArrayList<Node>();
		for (Iterator nodeIt = cyNetwork.nodesIterator(); nodeIt.hasNext();) {
			Node n = (Node) nodeIt.next();
			geneList.add(n);
			shuffledList.add(n);
		}

		// If randomize, permute the second list of genes
		if ( randomize ) 
			Collections.shuffle(shuffledList);

		for (int i = 0; i < geneList.size(); i++) {
		
			// If not randomizing these will be identical.
			Node current = geneList.get(i); 
			Node shuffle = shuffledList.get(i);

			// If randomizing, you'll get p-values for a different gene. 
			String canonicalName = shuffle.getIdentifier();

			double[] tempArray = new double[attrNames.length];
			for (int j = 0; j < attrNames.length; j++) {
				Double d = nodeAttributes.getDoubleAttribute(canonicalName,attrNames[j]);

				if (d == null) {
					tempArray[j] = ZStatistics.oneMinusNormalCDFInverse(.5);
				} else {
					double sigValue = d.doubleValue();
					if (sigValue < MIN_SIG) {
						sigValue = MIN_SIG;
						System.err.println("Warning: value for " + current.getIdentifier() + 
						                   " (" + canonicalName + ") adjusted to " + MIN_SIG);
					} 
					if (sigValue > MAX_SIG) {
						sigValue = MAX_SIG;
						System.err.println("Warning: value for " + current.getIdentifier() + 
						                   " (" + canonicalName + ") adjusted to " + MAX_SIG);
					} 

					// transform the p-value into a z-value and store it in the
					// array of z scores for this particular node
					tempArray[j] = ZStatistics.oneMinusNormalCDFInverse(sigValue);
				}
			}
			tempHash.put(current, tempArray);
		}
		System.err.println("Done processing into Hash");
		return tempHash;
	}

	/**
	 * Scores the currently selected nodes in the graph, and pops up a window
	 * with the result
	 */
	protected void scoreActivePath() {
		String callerID = "jActiveModules";
		ActivePathsFinder apf = new ActivePathsFinder(generateExpressionMap(),
				attrNames, cyNetwork, apfParams, mainFrame, parentUI);

		long start = System.currentTimeMillis();
		Vector result = new Vector();
		Iterator it = cyNetwork.getSelectedNodes().iterator();
		while (it.hasNext()) {
			result.add(it.next());
		}

		double score = apf.scoreList(result);
		long duration = System.currentTimeMillis() - start;
		System.err.println("-------------- back from score: " + duration
				+ " msecs");
		System.err.println("-------------- score: " + score + " \n");
		JOptionPane.showMessageDialog(mainFrame, "Score: " + score);
	} // scoreActivePath

	/*
	protected class ActivePathControllerLauncherAction extends AbstractAction {
		ActivePathControllerLauncherAction() {
			super("Active Modules");
		} // ctor
		public void actionPerformed(ActionEvent e) {
			//showConditionsVsPathwaysTable();
		}
	}
	*/

	/**
	 * find all of the unique node names in the full set of active paths. there
	 * may be duplicates since some nodes may appear in several paths
	 */
	protected Vector combinePaths(Component[] activePaths) {
		HashSet set = new HashSet();
		for (int i = 0; i < activePaths.length; i++) {
			set.addAll(activePaths[i].getNodes());
		} // for i
		return new Vector(set);

	}

	protected void addActivePathToolbarButton() {
		// if (activePathToolbarButton != null)
		// cytoscapeWindow.getCyMenus().getToolBar().remove
		// (activePathToolbarButton);

		// activePathToolbarButton =
		// cytoscapeWindow.getCyMenus().getToolBar().add (new
		// ActivePathControllerLauncherAction ());

	} // addActivePathToolbarButton
	// ------------------------------------------------------------------------------
	public void displayPath(Component activePath, boolean clearOthersFirst, String pathTitle) {
		titleForCurrentSelection = pathTitle;
		SelectFilter filter = cyNetwork.getSelectFilter();
		// cytoscapeWindow.selectNodesByName (activePath.getNodes (),
		// clearOthersFirst);
		if (clearOthersFirst) {
			// cyNetwork.unFlagAllNodes();
			filter.unselectAllNodes();
		}
		filter.setSelectedNodes(activePath.getDisplayNodesGeneric(), true);
		// cyNetwork.setFlaggedNodes(activePath.getNodes(),true);
		  Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
	}
	// ------------------------------------------------------------------------------
	public void displayPath(Component activePath,
			String pathTitle) {
		displayPath(activePath, true, pathTitle);
	}
	// ------------------------------------------------------------------------------

} // class ActivePaths (a CytoscapeWindow plugin)
