package org.cytoscape.webservice.ncbi;

import java.util.Set;

import org.cytoscape.io.webservice.TableImportWebServiceClient;
import org.cytoscape.io.webservice.client.AbstractWebServiceClient;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.TaskIterator;

public class NCBITableImportClient extends AbstractWebServiceClient implements TableImportWebServiceClient {

	public NCBITableImportClient(String uri, String displayName, String description) {
		super(uri, displayName, description);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TaskIterator getTaskIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<CyTable> getTables() {
		// TODO Auto-generated method stub
		return null;
	}

}
