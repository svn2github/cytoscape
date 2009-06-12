package csplugins.mcode.internal;

import java.util.ArrayList;
import java.util.List;

import csplugins.mcode.MCODEPlugin;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

public class MClusterToCyGroup {

	private static final String DEF_CLUSTER_NAME_PREFIX = "-Rank ";

	private static String viewer;

	private MClusterToCyGroup() {
		viewer = MCODEPlugin.DEFAULT_VIEWER_NAME;
	}

	public static CyGroup convertToGroup(MCODECluster cluster) {

		String clusterName = cluster.getParentNetwork().getTitle() + "."
				+ cluster.getResultTitle() + DEF_CLUSTER_NAME_PREFIX
				+ cluster.getRank();
		final List<CyNode> nodeList = new ArrayList<CyNode>();

		for (Integer nodeIndex : cluster.getALCluster()) {
			CyNode node = (CyNode) Cytoscape.getRootGraph().getNode(nodeIndex);
			nodeList.add(node);
		}

		return CyGroupManager.createGroup(clusterName, nodeList,
				viewer);
	}

}
