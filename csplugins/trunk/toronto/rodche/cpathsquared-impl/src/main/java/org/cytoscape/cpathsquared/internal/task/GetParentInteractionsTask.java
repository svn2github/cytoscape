package org.cytoscape.cpathsquared.internal.task;

import javax.swing.JDialog;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPathException;
import org.cytoscape.cpathsquared.internal.CPathWebService;
import org.cytoscape.cpathsquared.internal.CPathWebServiceImpl;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.cpathsquared.internal.view.InteractionBundleModel;
import org.cytoscape.cpathsquared.internal.view.InteractionBundlePanel;
import org.cytoscape.cpathsquared.internal.view.RecordList;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cpath.service.jaxb.SearchResponse;

public class GetParentInteractionsTask implements Task {
    private String uri;
    private CPathWebService webApi = CPathWebServiceImpl.getInstance();
    private InteractionBundleModel interactionBundleModel;
	private final CyNetwork network;
    private final CyNode node;
	private final CPath2Factory factory;

    public GetParentInteractionsTask (CyNetwork network, CyNode node, CPath2Factory factory) {
        this.uri = network.getRow(node).get("uri", String.class);
        this.node = node;
        this.network = network;
        this.factory = factory;
    }

    @Override
    public void cancel() {
        webApi.abort();
    }
    
    public void run(TaskMonitor taskMonitor) throws Exception {
    	taskMonitor.setTitle("Getting neighbors...");
        try {
            taskMonitor.setStatusMessage("Retrieving neighborhood summary.");
            SearchResponse response = webApi.getParentSummaries(uri, taskMonitor);
            RecordList recordList = new RecordList(response);
            interactionBundleModel = new InteractionBundleModel();
            interactionBundleModel.setRecordList(recordList);
            interactionBundleModel.setPhysicalEntityName("Network Neighborhood");

            CySwingApplication application = factory.getCySwingApplication();
            JDialog dialog = new JDialog(application.getJFrame());

            String nodeLabel = network.getRow(node).get(CyNode.NAME, String.class);
            if (nodeLabel != null) {
                dialog.setTitle(nodeLabel);
            } else {
                dialog.setTitle("Neighborhood");
            }
            InteractionBundlePanel interactionBundlePanel = factory.createInteractionBundlePanel(interactionBundleModel, network, dialog);
            dialog.getContentPane().add(interactionBundlePanel);
            interactionBundleModel.setRecordList(recordList);
            interactionBundlePanel.expandAllNodes();
            dialog.pack();
            dialog.setLocationRelativeTo(application.getJFrame());
            dialog.setVisible(true);
        } catch (CPathException e) {
            if (e.getErrorCode() != CPathException.ERROR_CANCELED_BY_USER) {
            	throw e;
            }
        } catch (EmptySetException e) {
        	throw new Exception("No neighbors found for selected node.", e);
        }
    }
    
    public InteractionBundleModel getInteractionBundle() {
        return this.interactionBundleModel;
    }
}
