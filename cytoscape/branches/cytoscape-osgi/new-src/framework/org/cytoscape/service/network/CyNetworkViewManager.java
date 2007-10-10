
package org.cytoscape.service.network-view;

public interface CyNetworkViewManager {
	public Map<CyNetwork,CyNetworkView> getNetworkViewMap();
	public CyNetworkView getNetworkView(CyNetwork net);
	public CyNetworkView getNullNetworkView();
	public List<CyNetworkView> getCurrentNetworkViews();
	public boolean setCurrentNetworkViews(List<CyNetwork> nets);
	public CyNetworkView createNetworkView(CyNetwork network);
	public void destroyNetworkView(CyNetworkView view);
}


