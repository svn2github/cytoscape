package org.cytoscape.io.webservice.biomart.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadRepositoryTask extends AbstractTask {

	private static final Logger logger = LoggerFactory
			.getLogger(LoadRepositoryTask.class);

	private final BiomartRestClient client;

	private Map<String, Map<String, String>> reg;
	
	

	// These databases are not compatible with this UI.
	private static final List<String> databaseFilter = new ArrayList<String>();

	static {
		// Database on this list will not appear on the list.
		databaseFilter.add("compara_mart_pairwise_ga_47");
		databaseFilter.add("compara_mart_multiple_ga_47");
		databaseFilter.add("dicty");
		databaseFilter.add("Pancreatic_Expression");
	}

	private Map<String, String> datasourceMap;
	private List<String> dsList;

	public LoadRepositoryTask(final BiomartRestClient client) {
		this.client = client;
	}

	public Map<String, String> getDatasourceMap() {
		return this.datasourceMap;
	}

	public List<String> getSortedDataSourceList() {
		return this.dsList;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		taskMonitor.setProgress(0.0);
		taskMonitor.setStatusMessage("Loading list of available marts...");
		
		dsList = new ArrayList<String>();
		datasourceMap = new HashMap<String, String>();

		logger.debug("Loading Repository...");
		reg = client.getRegistry();
		logger.debug("Loading Repository Done: Reg size = " + reg.size());

		final int registryCount = reg.size();
		int increment = 90 / registryCount;
		int percentCompleted = 10;

		Map<String, String> datasources;

		for (String databaseName : reg.keySet()) {

			Map<String, String> detail = reg.get(databaseName);

			// Add the datasource if its visible
			if (detail.get("visible").equals("1")
					&& (databaseFilter.contains(databaseName) == false)) {
				String dispName = detail.get("displayName");
				try {
					datasources = client.getAvailableDatasets(databaseName);
				} catch (IOException e) {
					// If timeout/connection error is found, skip the source.
					percentCompleted += increment;
					continue;
				}

				for (String key : datasources.keySet()) {
					final String dataSource = dispName + " - " + datasources.get(key);
					dsList.add(dataSource);
					datasourceMap.put(dataSource, key);
					logger.info("Data Source: " + dataSource);
				}
			}

			percentCompleted += increment;
			taskMonitor.setProgress(percentCompleted);
		}

		Collections.sort(dsList);
		
		taskMonitor.setProgress(1.0);
		taskMonitor.setStatusMessage("Finished: " + dsList.size());
	}

}
