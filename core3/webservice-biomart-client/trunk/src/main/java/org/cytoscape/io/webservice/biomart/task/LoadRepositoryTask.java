package org.cytoscape.io.webservice.biomart.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class LoadRepositoryTask extends AbstractTask {

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
	private final List<String> dsList = new ArrayList<String>();
	
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
		// TODO Auto-generated method stub
		reg = client.getRegistry();

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
					dsList.add(dispName + " - " + datasources.get(key));
					datasourceMap.put(dispName + " - " + datasources.get(key),
							key);
				}
			}

			percentCompleted += increment;
			taskMonitor.setProgress(percentCompleted);
		}
		
		Collections.sort(dsList);

	}

}
