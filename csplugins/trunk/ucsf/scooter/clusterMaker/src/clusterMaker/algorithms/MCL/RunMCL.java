package clusterMaker.algorithms.MCL;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

public class RunMCL {

	private double inflationParameter; /*density parameter */
	private int number_iterations; /*number of inflation/expansion cycles*/
	private double clusteringThresh; /*Threshold used to remove weak edges between distinct clusters*/
	private List<CyNode> nodes;
	private Hashtable<String, Double> normalizedSimilaritiesForGivenEdges = new Hashtable<String, Double>(); // key: s#t
	private String nodeClusterAttributeName;
	final static String GROUP_ATTRIBUTE = "__MCLGroups";
	
	public RunMCL(String nodeClusterAttributeName, List<CyNode> nodes, Hashtable<String, Double> normalizedSimilaritiesForGivenEdges, double inflationParameter, int num_iterations, double clusteringThresh)
	{
		
		this.nodeClusterAttributeName = nodeClusterAttributeName;
		this.nodes = nodes;
		this.normalizedSimilaritiesForGivenEdges = normalizedSimilaritiesForGivenEdges;
		this.inflationParameter = inflationParameter;
		this.number_iterations = num_iterations;
		this.clusteringThresh = clusteringThresh;
		
	}
	
	public void run()
	{
		double[][] graph = new double[this.nodes.size()][this.nodes.size()];
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		double[] clusters;
		Matrix matrix;
		double numClusters;
		
		
		for (int i = 0; i < this.nodes.size(); i++) {
			String source = this.nodes.get(i).getIdentifier();
			for (int j = i; j < nodes.size(); j++) {
				String target = this.nodes.get(j).getIdentifier();
				String key = source + "#" + target;
				String keyI = target + "#" + source;
				if(this.normalizedSimilaritiesForGivenEdges.containsKey(key)){
					graph[i][j] = this.normalizedSimilaritiesForGivenEdges.get(key);
				} else if(this.normalizedSimilaritiesForGivenEdges.containsKey(keyI)){
					graph[i][j] = this.normalizedSimilaritiesForGivenEdges.get(keyI);
				} else {
					graph[i][j] = 0;
				}
				graph[j][i]=graph[i][j];
				
			}
		}
		
		//Create Matrix and cluster nodes
		matrix = new Matrix(graph,inflationParameter,number_iterations,clusteringThresh);
		clusters = matrix.cluster();
		numClusters = matrix.numClusters;
		
/*
		for(int i=0; i< numClusters; i++)
		{
			String name = (new Integer(i)).toString();
			CyGroupManager.createGroup(nodeClusterAttributeName + "_" + name,"metaNode");
		}
*/

		HashMap<Integer,List<CyNode>>clusterMap = new HashMap();
		
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
		
		// Now, create the groups
		List<String>groupList = new ArrayList(); // keep track of the groups we create
		for (Integer clusterNumber: clusterMap.keySet()) {
			String groupName = nodeClusterAttributeName+"_"+clusterNumber.toString();
			List<CyNode>nodeList = clusterMap.get(clusterNumber);
			// Create the group
			CyGroup newgroup = CyGroupManager.createGroup(groupName, nodeList, null);
			if (newgroup != null) {
				// Now tell the metanode viewer about it
				CyGroupManager.setGroupViewer(newgroup, "metaNode", Cytoscape.getCurrentNetworkView(), true);
				groupList.add(groupName);
			}
		}

		// Save the network attribute so we remember which groups are ours
		netAttributes.setListAttribute(networkID, GROUP_ATTRIBUTE, groupList);
	}	
}

