package clusterMaker.algorithms.MCL;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.logger.CyLogger;

public class RunMCL {

	private double inflationParameter; /*density parameter */
	private int number_iterations; /*number of inflation/expansion cycles*/
	private double clusteringThresh; /*Threshold used to remove weak edges between distinct clusters*/
	private List<CyNode> nodes;
	private List<CyEdge> edges;
	private String nodeClusterAttributeName;
	private String edgeAttributeName;
	private boolean takeNegLOG;
	private CyLogger logger;
	final static String GROUP_ATTRIBUTE = "__MCLGroups";
	
	public RunMCL(String nodeClusterAttributeName, String edgeAttributeName, 
	              double inflationParameter, int num_iterations, 
	              double clusteringThresh, boolean takeNegLOG, CyLogger logger )
	{
		
		this.nodeClusterAttributeName = nodeClusterAttributeName;
		this.edgeAttributeName = edgeAttributeName;
		this.inflationParameter = inflationParameter;
		this.number_iterations = num_iterations;
		this.clusteringThresh = clusteringThresh;
		this.takeNegLOG = takeNegLOG;
		this.logger = logger;
		// logger.info("InflationParameter = "+inflationParameter);
		// logger.info("Iterations = "+num_iterations);
		// logger.info("Clustering Threshold = "+clusteringThresh);
	}
	
	public void run()
	{
		edges = Cytoscape.getCurrentNetwork().edgesList();
		nodes = Cytoscape.getCurrentNetwork().nodesList();;
		double[][] graph = new double[this.nodes.size()][this.nodes.size()];
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		double[] clusters;
		Matrix matrix;
		double numClusters;
		double edgeWeight;
		double minEdgeWeight = 0;
		int sourceIndex;
		int targetIndex;

		for(int i = 0; i < graph.length; i++)
			for(int j = 0; j < graph.length; j++)
				graph[i][j] = 0;

		// logger.info("Getting edge weights from network");
		//Get Edge Weights From Network
		for(CyEdge edge: edges) {
			String id = edge.getIdentifier();

			if(!edgeAttributes.hasAttribute(id,edgeAttributeName))
				continue;
		
			if(edgeAttributes.getType(edgeAttributeName) == edgeAttributes.TYPE_FLOATING)
				edgeWeight = edgeAttributes.getDoubleAttribute(id,edgeAttributeName).doubleValue();
		
			else if(edgeAttributes.getType(edgeAttributeName) == edgeAttributes.TYPE_INTEGER)
				edgeWeight = edgeAttributes.getIntegerAttribute(id,edgeAttributeName).doubleValue();
		
			else
				continue;
		    
		  /*Take -LOG of edge weight (E-Value) if so specified*/
			if(takeNegLOG)
				if(edgeWeight != 0.0)
					edgeWeight = -1*Math.log(edgeWeight);

			if(edgeWeight < minEdgeWeight)
				minEdgeWeight = edgeWeight;
		      
			/*Add edge to graph*/
			sourceIndex = nodes.indexOf(edge.getSource());
			targetIndex = nodes.indexOf(edge.getTarget());
			graph[targetIndex][sourceIndex] = edgeWeight;

			if(!edge.isDirected())
				graph[sourceIndex][targetIndex] = edgeWeight;
		}

		//make sure all edge values are positive
		if(minEdgeWeight < 0)
			for(int i = 0; i < graph.length; i++)
				for(int j = 0; j < graph.length; j++)
			    graph[i][j] += -1*minEdgeWeight;

		// logger.info("Calculating clusters");

		//Create Matrix and cluster nodes
		matrix = new Matrix(graph,inflationParameter,number_iterations,clusteringThresh);
		clusters = matrix.cluster();
		numClusters = matrix.numClusters;
		// logger.info("Found "+numClusters+" clusters");

		HashMap<Integer,List<CyNode>>clusterMap = new HashMap();

		// logger.info("Updating node attributes");
		
		//Update node attributes in network to include clusters. Create cygroups from clustered nodes
		for (int i=0; i<clusters.length; i++)
		{
			CyNode node = this.nodes.get(i);
			nodeAttributes.setAttribute(node.getIdentifier(),nodeClusterAttributeName,new Double(clusters[i]));
			if (clusterMap.containsKey(Integer.valueOf((int)clusters[i]))) {
				clusterMap.get(Integer.valueOf((int)clusters[i])).add(node);
			} else {
				ArrayList<CyNode>nodeList = new ArrayList();
				nodeList.add(node);
				clusterMap.put(Integer.valueOf((int)clusters[i]), nodeList);
			}
		}

		// logger.info("Removing groups");

		// See if we already have groups defined (from a previous run?)
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		String networkID = Cytoscape.getCurrentNetwork().getIdentifier();
		if (netAttributes.hasAttribute(networkID, GROUP_ATTRIBUTE)) {
			List<String> groupList = (List<String>)netAttributes.getListAttribute(networkID, GROUP_ATTRIBUTE);
			for (String groupName: groupList) {
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group != null)
					CyGroupManager.removeGroup(group);
			}
		}

		// logger.info("Creating groups");
		
		// Now, create the groups
		List<String>groupList = new ArrayList(); // keep track of the groups we create
		for (Integer clusterNumber: clusterMap.keySet()) {
			String groupName = nodeClusterAttributeName+"_"+clusterNumber.toString();
			List<CyNode>nodeList = clusterMap.get(clusterNumber);
			// logger.info("Group: "+clusterNumber+": "+groupName);
			// Create the group
			CyGroup newgroup = CyGroupManager.createGroup(groupName, nodeList, null);
			if (newgroup != null) {
				// Now tell the metanode viewer about it
				CyGroupManager.setGroupViewer(newgroup, "metaNode", Cytoscape.getCurrentNetworkView(), false);
				groupList.add(groupName);
			}
		}

		// Now notify the metanode viewer
		CyGroup group = CyGroupManager.findGroup(groupList.get(0));
		CyGroupManager.setGroupViewer(group, "metaNode", Cytoscape.getCurrentNetworkView(), true);

		// Save the network attribute so we remember which groups are ours
		netAttributes.setListAttribute(networkID, GROUP_ATTRIBUTE, groupList);
	}	
}

