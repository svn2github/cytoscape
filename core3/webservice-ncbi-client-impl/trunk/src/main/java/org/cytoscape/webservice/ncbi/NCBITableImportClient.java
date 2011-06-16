package org.cytoscape.webservice.ncbi;

import java.util.Set;

import org.cytoscape.io.webservice.TableImportWebServiceClient;
import org.cytoscape.io.webservice.client.AbstractWebServiceClient;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.webservice.ncbi.task.ImportTableFromNCBITask;
import org.cytoscape.work.TaskIterator;

public class NCBITableImportClient extends AbstractWebServiceClient implements TableImportWebServiceClient {

	private final CyTableFactory tableFactory;

	public NCBITableImportClient(String uri, String displayName, String description, final CyTableFactory tableFactory) {
		super(uri, displayName, description);
		this.tableFactory = tableFactory;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ImportTableFromNCBITask(tableFactory, ((NCBIQuery) this.currentQuery).getIds(),
				((NCBIQuery) this.currentQuery).getCategory()));
	}

	@Override
	public Set<CyTable> getTables() {
		return null;
	}

}
