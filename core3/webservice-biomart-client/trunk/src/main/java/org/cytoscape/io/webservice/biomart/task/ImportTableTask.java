package org.cytoscape.io.webservice.biomart.task;

import java.util.Set;

import org.cytoscape.io.webservice.biomart.BiomartClient;
import org.cytoscape.io.webservice.biomart.BiomartQuery;
import org.cytoscape.io.webservice.client.CyTableImportTask;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ImportTableTask extends AbstractTask implements CyTableImportTask {

	private final BiomartClient client;
	private final BiomartQuery query;
	
	private Set<CyTable> tables;
	
	public ImportTableTask(final BiomartClient client, final BiomartQuery query) {
		this.client = client;
		this.query = query;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if(query == null)
			throw new NullPointerException("Query is null");
		
		tables = client.importAttributes(query);
	}

	@Override
	public Set<CyTable> getCyTables() {
		return tables;
	}
}
