package org.genmapp.subgeneviewer.splice.controller;

import org.genmapp.subgeneviewer.controller.SubgeneController;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.text.Example_Exon_Structure_GenMAPP_CS;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

public class SpliceController extends SubgeneController {

	
	/**
	 * @param nodeId
	 * @return
	 */
	public static SubgeneNetworkView buildSpliceViewer(String nodeId) {
		// todo: make this a Mediator method, so that we can transition to
		//       embedded database and remote server
		SpliceNetworkView spliceView = new SpliceNetworkView();		
		spliceView.parseSplice(nodeId);
		
		Example_Exon_Structure_GenMAPP_CS.dumpSpliceNetworkView(spliceView); 
		spliceView.renderSplice(nodeId);
		return spliceView;

	}


}
