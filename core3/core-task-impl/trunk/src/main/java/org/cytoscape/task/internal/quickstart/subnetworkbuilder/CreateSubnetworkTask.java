package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.util.Arrays;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.events.RowsAboutToChangeEvent;
import org.cytoscape.model.events.RowsFinishedChangingEvent;
import org.cytoscape.task.internal.select.SelectFirstNeighborsTask;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSubnetworkTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(CreateSubnetworkTask.class);

    static final String QUERY_GENE_ATTR_NAME = "Gene Type";
    static final String SEARCH_GENE_ATTR_NAME = "Search Term";

    @Tunable(description = "Enter list of genes you are interested in (NCBI Entrez Gene ID)")
    public String queryGenes;

    private final SubnetworkBuilderUtil util;
    private final SubnetworkBuilderState state;

    CreateSubnetworkTask(final SubnetworkBuilderUtil util, final SubnetworkBuilderState state) {
	this.util = util;
	this.state = state;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

	final String[] genes = queryGenes.split("\\s+");
	logger.debug("Got gene list: " + genes.length);
	for (final String gene : genes) {
	    logger.debug("Gene: " + gene);
	}

	selectGenes(genes);

    }

    private void selectGenes(final String[] genes) {
	final CyNetwork target = util.appManager.getCurrentNetwork();
	final CyTable nodeTable = target.getDefaultNodeTable();

	if (nodeTable.getColumn(QUERY_GENE_ATTR_NAME) == null)
	    nodeTable.createColumn(QUERY_GENE_ATTR_NAME, String.class, false);
	if (nodeTable.getColumn(SEARCH_GENE_ATTR_NAME) == null)
	    nodeTable.createColumn(SEARCH_GENE_ATTR_NAME, String.class, false);

	boolean found = false;
	final List<String> geneList = Arrays.asList(genes);
	try {
	    util.eventHelper.fireSynchronousEvent(new RowsAboutToChangeEvent(this, nodeTable));

	    List<CyNode> nodeList = target.getNodeList();
	    for (final CyNode node : nodeList) {
		final String nodeName = node.getCyRow().get(CyTableEntry.NAME, String.class);
		
		if(geneList.contains(nodeName) && state.getDiseaseGenes().contains(nodeName)) {
		    node.getCyRow().set(CyNetwork.SELECTED, true);
		    node.getCyRow().set(QUERY_GENE_ATTR_NAME, "query and disease");
		    node.getCyRow().set(SEARCH_GENE_ATTR_NAME, state.getSearchTerms());
		    found = true;
		} else if (geneList.contains(nodeName)) {
		    node.getCyRow().set(CyNetwork.SELECTED, true);
		    node.getCyRow().set(QUERY_GENE_ATTR_NAME, "query");
		    found = true;
		} else if (state.getDiseaseGenes().contains(nodeName)) {
		    node.getCyRow().set(CyNetwork.SELECTED, true);
		    node.getCyRow().set(QUERY_GENE_ATTR_NAME, "disease");
		    node.getCyRow().set(SEARCH_GENE_ATTR_NAME, state.getSearchTerms());
		}
	    }
	} finally {
	    util.eventHelper.fireSynchronousEvent(new RowsFinishedChangingEvent(this, nodeTable));
	}

	if (!found) {
	    logger.error("Query genes were not found in the interactome.");
	    return;
	}

	this.insertTasksAfterCurrentTask(new BuildVisualStyleTask(util));
	
	Task createNetworkTask = util.getNewNetworkSelectedNodesOnlyTask(target);
	this.insertTasksAfterCurrentTask(createNetworkTask);

	SelectFirstNeighborsTask nextTask = new SelectFirstNeighborsTask(target, util.networkViewManager,
		util.eventHelper);
	this.insertTasksAfterCurrentTask(nextTask);
    }
}
