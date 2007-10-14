package org.genmapp.subgeneviewer;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

public interface SubgeneNetworkViewMediator {
	
	/**
	 * 
	 * @param nodeId  identifier for the parent node
	 * @param importMethod  method by which data for view is imported: parse local file, embedded database, remote service 
	 * @return
	 */
	public SubgeneNetworkView getView (String nodeId, String importMethod);

}
