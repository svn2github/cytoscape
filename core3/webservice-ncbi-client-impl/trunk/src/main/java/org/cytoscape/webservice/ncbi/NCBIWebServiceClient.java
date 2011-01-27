package org.cytoscape.webservice.ncbi;

import java.util.Set;

import org.cytoscape.io.webservice.NetworkImportWebServiceClient;
import org.cytoscape.io.webservice.SearchWebServiceClient;
import org.cytoscape.io.webservice.TableImportWebServiceClient;
import org.cytoscape.io.webservice.client.AbstractWebServiceClient;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.TaskIterator;

public class NCBIWebServiceClient extends AbstractWebServiceClient implements
		TableImportWebServiceClient, NetworkImportWebServiceClient,
		SearchWebServiceClient<Object> {

	public NCBIWebServiceClient(String uri, String displayName,
			String description) {
		super(uri, displayName, description);
	}

	@Override
	public TaskIterator getTaskIterator() {

		return null;
	}

	@Override
	public Set<CyNetwork> getNetworks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<CyTable> getTables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSearchResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
