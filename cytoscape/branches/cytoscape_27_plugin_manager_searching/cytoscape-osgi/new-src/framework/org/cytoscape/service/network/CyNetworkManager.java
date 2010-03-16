
package org.cytoscape.service.network;

public interface CyNetworkManager {
	public Set<CyNetwork> getAllNetworks();
	public CyNetwork getNetwork(String id);
	public CyNetwork getNullNetwork();
	public List<CyNetwork> getCurrentNetworks();
	public void setCurrentNetworks(List<CyNetwork> nets);
	public void destroyNetwork(CyNetwork network);
	public CyNetwork createNetwork(String title);
	public List<CyNode> getCyNodes();
	public List<CyEdge> getCyEdges();
	public CyNode getCyNode(String alias);
	public List<CyEdge> getCyEdge(Node node_1, Node node_2);
}
