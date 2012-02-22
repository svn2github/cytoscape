package org.cytoscape.cpathsquared.internal.task;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.cpathsquared.internal.CPath2WebServiceImpl;
import org.cytoscape.cpathsquared.internal.view.ResultsModel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;


//TODO needs much re-factoring...
public class GetNodeNeighborsTask implements Task {
    private String uri;
    private CPath2WebService webApi = CPath2WebServiceImpl.getInstance();
    private ResultsModel resultsModel;
	private final CyNetwork network;
	private final CPath2Factory factory;

    public GetNodeNeighborsTask (CyNetwork network, CyNode node, CPath2Factory factory) {
        this.uri = network.getRow(node).get("uri", String.class);
        this.network = network;
        this.factory = factory;
    }

    @Override
    public void cancel() {
        webApi.abort();
    }
    
    //TODO
    public void run(TaskMonitor taskMonitor) throws Exception {
    	taskMonitor.setTitle("Getting...");
        taskMonitor.setStatusMessage("Retrieving...");
    }
    
}
