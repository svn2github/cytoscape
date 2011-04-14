package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.util.Arrays;
import java.util.List;

import org.cytoscape.model.CyColumn;
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

public class SetQueryGeneTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(SetQueryGeneTask.class);

    private static final String QUERY_GENE_ATTR_NAME = "Gene Type";

    @Tunable(description = "Enter list of genes you are interested in (NCBI Entrez Gene ID)")
    public String queryGenes;

    private final SubnetworkBuilderUtil util;

    SetQueryGeneTask(final SubnetworkBuilderUtil util) {
	this.util = util;
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

	boolean found = false;
	final List<String> geneList = Arrays.asList(genes);
	try {
	    util.eventHelper.fireSynchronousEvent(new RowsAboutToChangeEvent(this, nodeTable));

	    List<CyNode> nodeList = target.getNodeList();
	    for (final CyNode node : nodeList) {
		if (geneList.contains(node.getCyRow().get(CyTableEntry.NAME, String.class))) {
		    node.getCyRow().set("selected", true);
		    node.getCyRow().set(QUERY_GENE_ATTR_NAME, "query");
		    logger.debug("!!! Found: " + node.getCyRow().get(CyTableEntry.NAME, String.class));
		    found = true;
		}
	    }
	} finally {
	    util.eventHelper.fireSynchronousEvent(new RowsFinishedChangingEvent(this, nodeTable));
	}
	
	if(!found) {
	    logger.debug("Not found in the interactome.");
	    return;
	}
	    
	Task createNetworkTask = util.getNewNetworkSelectedNodesOnlyTask(target);
	this.insertTasksAfterCurrentTask(createNetworkTask);

	SelectFirstNeighborsTask nextTask = new SelectFirstNeighborsTask(target, util.networkViewManager,
		util.eventHelper);
	this.insertTasksAfterCurrentTask(nextTask);

    }
}
