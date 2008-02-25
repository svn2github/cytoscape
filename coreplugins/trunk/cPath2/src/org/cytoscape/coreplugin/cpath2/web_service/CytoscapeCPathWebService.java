package org.cytoscape.coreplugin.cpath2.web_service;

import cytoscape.data.webservice.*;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.util.ModuleProperties;
import cytoscape.layout.Tunable;
import cytoscape.Cytoscape;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.SearchResponseType;
import org.cytoscape.coreplugin.cpath2.util.NullTaskMonitor;
import org.cytoscape.coreplugin.cpath2.task.ExecuteGetRecordByCPathId;

/**
 * CPath Web Service, integrated into the Cytoscape Web Services Framework.
 *
 * 
 */
public class CytoscapeCPathWebService extends WebServiceClientImpl{
	// Display name of this client.
    private static final String DISPLAY_NAME = CPathProperties.getInstance().getCPathServerName() +
                " Web Service Client";

	// Client ID. This should be unique.
    private static final String CLIENT_ID = CPathProperties.getInstance().getWebServicesId();

	// Instance of this client.  This is a singleton.
	private static final WebServiceClient client = new CytoscapeCPathWebService();

    /**
     * NCBI Taxonomy ID Filter.
     */
    public static final String NCBI_TAXONOMY_ID_FILTER = "ncbi_taxonomy_id_filter";

    /**
     * Response Format.
     */
    public static final String RESPONSE_FORMAT = "response_format";

    /**
	 * Return instance of this client.
	 * @return WebServiceClient Object.
	 */
	public static WebServiceClient getClient() {
		return client;
	}

    /**
	 * Creates a new IntactClient object.
	 */
	private CytoscapeCPathWebService() {
		super(CLIENT_ID, DISPLAY_NAME, new WebServiceClientManager.ClientType[]
                { WebServiceClientManager.ClientType.NETWORK });
        // Set properties for this client.
        stub = CPathWebServiceImpl.getInstance();
        setProperty();
	}

	/**
	 * Set props for this client.
	 */
	private void setProperty() {
		props = new ModulePropertiesImpl(clientID, "wsc");

        //  TODO:  Can we add Tunable Lists? e.g. enums 
        props.add(new Tunable(NCBI_TAXONOMY_ID_FILTER, "Filter by Organism - NCBI Taxonomy ID",
                Tunable.INTEGER, new Integer(-1)));
        props.add(new Tunable(RESPONSE_FORMAT, "Response Format",
                Tunable.INTEGER, CPathResponseFormat.BINARY_SIF.getFormatString()));
    }

    public void executeService(CyWebServiceEvent e) throws Exception {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(CyWebServiceEvent.WSEventType.IMPORT_NETWORK)) {
                importNetwork(e);
            } else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.EXPAND_NETWORK)) {
			} else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.SEARCH_DATABASE)) {
                searchDatabase(e);
            }
        }
    }

    private void importNetwork(CyWebServiceEvent e) {
        CPathWebService webApi = CPathWebServiceImpl.getInstance();
        String q = e.getParameter().toString();

        //  TODO:  Is there a convention for specifying multiple Ids / keywords?
        String idStrs[] = q.split(" ");
        long ids[] = new long [idStrs.length];
        for (int i=0; i<ids.length; i++) {
            ids[i] = Long.parseLong(idStrs[i]);
        }

        ModuleProperties properties = this.getProps();
        Tunable tunable = properties.get(RESPONSE_FORMAT);
        CPathResponseFormat format = CPathResponseFormat.BINARY_SIF;
        if (tunable != null) {
            format = CPathResponseFormat.getResponseFormat((String) tunable.getValue());
        }

        //  Create the task
        ExecuteGetRecordByCPathId task = new ExecuteGetRecordByCPathId(webApi,
                ids, format, CPathProperties.getInstance().getCPathServerName());
        //  Run right here in this thread.
        //  TODO:  Should the web client create a new thread?
        task.run();
    }

    private void searchDatabase(CyWebServiceEvent e) throws CPathException, EmptySetException {
        String q = e.getParameter().toString();
        CPathWebService webApi = CPathWebServiceImpl.getInstance();
        ModuleProperties properties = this.getProps();
        Tunable tunable = properties.get(NCBI_TAXONOMY_ID_FILTER);
        Integer taxonomyId = -1;
        if (tunable != null) {
            taxonomyId = (Integer) tunable.getValue();
        }
        SearchResponseType response = webApi.searchPhysicalEntities(q, taxonomyId,
                new NullTaskMonitor());
        Integer totalNumHits = response.getTotalNumHits().intValue();

        //  Fire appropriate events.
        //  TODO:  "SEARCH_RESULT" should probably be a constant somewhere.
        if (e.getNextMove() != null) {
            Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
                 new DatabaseSearchResult(totalNumHits, response,
                    e.getNextMove()));
        } else {
            Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
                 new DatabaseSearchResult(totalNumHits, response,
                      CyWebServiceEvent.WSEventType.IMPORT_NETWORK));
        }
    }
}

