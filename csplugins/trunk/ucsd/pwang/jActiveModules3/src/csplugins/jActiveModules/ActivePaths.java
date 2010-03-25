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

import java.net.URL;
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

import csplugins.jActiveModules.Component;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectFilter;
import cytoscape.util.PropUtil;
import cytoscape.view.CyNetworkView;

import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.logger.CyLogger;

//-----------------------------------------------------------------------------------
public class ActivePaths implements ActivePathViewer, Runnable {

	private static CyLogger logger = CyLogger.getLogger(ActivePaths.class);

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
	
	public static URL url1 = ActiveModulesUI.class.getResource("/JACTIVEMODULE_OVERVIEW_VS.props");
	public static URL url2 = ActiveModulesUI.class.getResource("/JACTIVEMODULE_MODULE_VS.props");
	private static String VS_OVERVIEW_NAME = "jActiveModules";
	private static String VS_MODULE_NAME = "jActiveModules_module";
	private static VisualStyle vs_overview = null;
	private static VisualStyle vs_module = null;

	static {
		
		// Create visualStyles based on the definition in property files
		Set<String> names = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyleNames();
		if (!names.contains(VS_OVERVIEW_NAME)){
			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,url1);
		}
		if (!names.contains(VS_MODULE_NAME)){
			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,url2);
		}
	}
	
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

		// Find the visualStyles for overview and module 
		Object[] styles = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyles().toArray();
		for (int i=0; i< styles.length; i++){
			VisualStyle vs = (VisualStyle) styles[i];
			if (vs.getName().equalsIgnoreCase(VS_OVERVIEW_NAME)){
				vs_overview = vs;
			}
			else if (vs.getName().equalsIgnoreCase(VS_MODULE_NAME)){
				vs_module = vs;
			}
		}

		
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
		
		CyNetwork overview = Cytoscape.createNetwork(path_nodes, path_edges, "Overview"+ "_"+ runCount++, cyNetwork);
		
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");

		Cytoscape.createNetworkView(overview, overview.getIdentifier(), layout, null);
		applyVisualStyle(overview,vs_overview);
		
		// Create an edge attribute "overlapScore", which is defined as NumberOfSharedNodes/min(two network sizes)
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		Iterator it = path_edges.iterator();
		while(it.hasNext()){
			CyEdge aEdge = (CyEdge) it.next();
			int NumberOfSharedNodes = getNumberOfSharedNodes((CyNetwork)aEdge.getSource().getNestedNetwork(), 
					(CyNetwork)aEdge.getTarget().getNestedNetwork());
			
			int minNodeCount = Math.min(aEdge.getSource().getNestedNetwork().getNodeCount(), 
								aEdge.getTarget().getNestedNetwork().getNodeCount());
			
			double overlapScore = (double)NumberOfSharedNodes/minNodeCount;
			cyEdgeAttrs.setAttribute(aEdge.getIdentifier(), "overlapScore", overlapScore);			
		}
	}


	private static int getNumberOfSharedNodes(CyNetwork networkA, CyNetwork networkB){
		
		int[] nodeIndicesA = networkA.getNodeIndicesArray();
		int[] nodeIndicesB = networkB.getNodeIndicesArray();
		
		
		HashSet<Integer> hashSet = new HashSet<Integer>();
		for (int i=0; i< nodeIndicesA.length; i++){
			hashSet.add( new Integer(nodeIndicesA[i]));
		}

		int sharedNodeCount =0;
		for (int i=0; i< nodeIndicesB.length; i++){
			if (hashSet.contains(new Integer(nodeIndicesB[i]))){
				sharedNodeCount++;
			}
		}
		
		return sharedNodeCount;
	}
	
	
	private static void applyVisualStyle(CyNetwork net, VisualStyle vs) {
		CyNetworkView view = Cytoscape.getNetworkView( net.getIdentifier() );
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		view.setVisualStyle(vs.getName());
		vmm.setNetworkView(view);
		vmm.setVisualStyle(vs);
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
		logger.info("High Scoring Path: " + activePaths[0].toString());
		logger.info("Score: " + activePaths[0].getScore());
		int size = activePaths[0].getNodes().size();
		logger.info("Size: " + size);
		logger.info("Raw score: " + activePaths[0].calculateSimpleScore());
		logger.info("Mean: " + Component.pStats.getMean(size));
		logger.info("Std: " + Component.pStats.getStd(size));
		return activePaths[0];
	}

	protected HashMap generateExpressionMap() {
		// set up the HashMap which is used to map from nodes
		// to z values. At this point, we are mapping from the
		// p values for expression to z values
		logger.info("Processing Expression Data into Hash");
		HashMap tempHash = new HashMap();
		logger.info("Do some testing of the ExpressionData object");
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
						logger.warn("Warning: value for " + current.getIdentifier() + 
						                   " (" + canonicalName + ") adjusted to " + MIN_SIG);
					} 
					if (sigValue > MAX_SIG) {
						sigValue = MAX_SIG;
						logger.warn("Warning: value for " + current.getIdentifier() + 
						                   " (" + canonicalName + ") adjusted to " + MAX_SIG);
					} 

					// transform the p-value into a z-value and store it in the
					// array of z scores for this particular node
					tempArray[j] = ZStatistics.oneMinusNormalCDFInverse(sigValue);
				}
			}
			tempHash.put(current, tempArray);
		}
		logger.info("Done processing into Hash");
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
		logger.info("-------------- back from score: " + duration
				+ " msecs");
		logger.info("-------------- score: " + score + " \n");
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
