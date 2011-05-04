package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.util.Set;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchRelatedGenesTask extends AbstractTask {

	private static final Logger logger = LoggerFactory.getLogger(SearchRelatedGenesTask.class);

	@Tunable(description = "Gene Ontology")
	public String go;

	@Tunable(description = "Disease/Phynotype")
	public String phynotype;

	private final SubnetworkBuilderState state;
	private final SubnetworkBuilderUtil util;

	SearchRelatedGenesTask(final SubnetworkBuilderUtil util, final SubnetworkBuilderState state) {
		this.state = state;
		this.util = util;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		tm.setStatusMessage("Searching NCBI Gene Database...");

		tm.setProgress(0.0);

		insertTasksAfterCurrentTask(new CreateSubnetworkTask(util, state));

		final NCBISearchClient client = new NCBISearchClient();

		final Set<String> idSet = client.search(phynotype, go);
		state.setDiseaseGenes(idSet);
		state.setSearchTerms(phynotype + "," + go);

		logger.info("NCBI Gene database returns " + idSet.size() + " gene IDs.");
	}
}
