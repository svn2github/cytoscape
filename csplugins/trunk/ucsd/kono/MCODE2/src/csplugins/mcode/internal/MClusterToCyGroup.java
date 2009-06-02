package csplugins.mcode.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

public class MClusterToCyGroup {

	private static final String DEF_CLUSTER_NAME_PREFIX = "-Rank ";

	private static MClusterToCyGroup converter = new MClusterToCyGroup();

	private CyGroupViewer viewer;

	private MClusterToCyGroup() {
		final Collection<CyGroupViewer> viewers = CyGroupManager
				.getGroupViewers();
		if (viewers == null || viewers.size() == 0) {
			throw new IllegalStateException("Group Viewer is not available.");
		} else {
			viewer = viewers.iterator().next();
		}
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
				converter.viewer.getViewerName());
	}

}
