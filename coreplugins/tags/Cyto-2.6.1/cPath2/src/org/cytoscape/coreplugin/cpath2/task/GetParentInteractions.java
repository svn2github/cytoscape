package org.cytoscape.coreplugin.cpath2.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.web_service.CPathException;
import org.cytoscape.coreplugin.cpath2.web_service.EmptySetException;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebServiceImpl;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.SummaryResponseType;
import org.cytoscape.coreplugin.cpath2.view.model.InteractionBundleModel;
import org.cytoscape.coreplugin.cpath2.view.model.RecordList;
import org.cytoscape.coreplugin.cpath2.view.InteractionBundlePanel;
import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;

import javax.swing.*;

public class GetParentInteractions implements Task {
    private long cpathId;
    private TaskMonitor taskMonitor;
    private CPathWebService webApi = CPathWebServiceImpl.getInstance();
    private InteractionBundleModel interactionBundleModel;
    private CyNode node;

    public GetParentInteractions (CyNode node) {
        this.cpathId = Long.parseLong(node.getIdentifier());
        this.node = node;
    }

    public void run() {
        try {
            taskMonitor.setStatus("Retrieving neighborhood summary.");
            SummaryResponseType response = webApi.getParentSummaries(cpathId, taskMonitor);
            RecordList recordList = new RecordList(response);
            interactionBundleModel = new InteractionBundleModel();
            interactionBundleModel.setRecordList(recordList);
            interactionBundleModel.setPhysicalEntityName("Network Neighborhood");

            JDialog dialog = new JDialog(Cytoscape.getDesktop());

            CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
            String nodeLabel = nodeAttributes.getStringAttribute(node.getIdentifier(),
                    BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
            if (nodeLabel != null) {
                dialog.setTitle(nodeLabel);
            } else {
                dialog.setTitle("Neighborhood");
            }
            InteractionBundlePanel interactionBundlePanel =
                    new InteractionBundlePanel(interactionBundleModel,
                            Cytoscape.getCurrentNetwork(), dialog);
            dialog.getContentPane().add(interactionBundlePanel);
            interactionBundleModel.setRecordList(recordList);
            interactionBundlePanel.expandAllNodes();
            dialog.pack();
            dialog.setLocationRelativeTo(Cytoscape.getDesktop());
            dialog.setVisible(true);
        } catch (CPathException e) {
            if (e.getErrorCode() != CPathException.ERROR_CANCELED_BY_USER) {
                taskMonitor.setException(e, e.getMessage(), e.getRecoveryTip());
            }
        } catch (EmptySetException e) {
            taskMonitor.setException(e, "No neighbors found for selected node.");
        }
    }

    public void halt() {
        webApi.abort();
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Getting neighbors...";
    }

    public InteractionBundleModel getInteractionBundle() {
        return this.interactionBundleModel;
    }
}
