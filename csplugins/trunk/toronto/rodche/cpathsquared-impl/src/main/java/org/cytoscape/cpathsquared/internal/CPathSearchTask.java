package org.cytoscape.cpathsquared.internal;

import org.cytoscape.cpath.service.jaxb.SearchResponseType;
import org.cytoscape.cpathsquared.internal.util.NullTaskMonitor;
import org.cytoscape.cpathsquared.internal.web_service.CPathWebService;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class CPathSearchTask implements Task {

	private final CPathWebService client;
	private final String query;
	private final int taxonomyId;

	public CPathSearchTask(String query, CPathWebService client, int taxonomyId) {
		this.query = query;
		this.client = client;
		this.taxonomyId = taxonomyId;
	}
	
    /**
     * NCBI Taxonomy ID Filter.
     */
    public static final String NCBI_TAXONOMY_ID_FILTER = "ncbi_taxonomy_id_filter";

    @Override
	public void run(TaskMonitor taskMonitor) throws Exception {
        SearchResponseType response = client.searchPhysicalEntities(query, taxonomyId,
                new NullTaskMonitor());
        Integer totalNumHits = response.getTotalNumHits().intValue();
	}

	@Override
	public void cancel() {
	}
}
