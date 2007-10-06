package org.genmapp.subgeneviewer.splice.controller;

import org.genmapp.subgeneviewer.controller.SubgeneController;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

public class SpliceController extends SubgeneController {

	
	/**
	 * @param nodeId
	 * @return
	 */
	public static SubgeneNetworkView buildSpliceViewer(String nodeId) {
		SpliceNetworkView spliceView = new SpliceNetworkView();
		spliceView.parseSplice(nodeId);
		spliceView.renderSplice(nodeId);
		return spliceView;

	}


}
