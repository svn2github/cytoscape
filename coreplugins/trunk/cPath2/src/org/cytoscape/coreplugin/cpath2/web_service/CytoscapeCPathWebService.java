package org.cytoscape.coreplugin.cpath2.web_service;

import cytoscape.data.webservice.*;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.layout.Tunable;
import cytoscape.task.TaskMonitor;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.SearchResponseType;
import org.cytoscape.coreplugin.cpath2.util.NullTaskMonitor;

/**
 * CPath Web Service, integrated into the Cytoscape Web Services Framework.
 *
 * 
 */
public class CytoscapeCPathWebService extends WebServiceClientImpl{
	// Display name of this client.
    // TODO:  Get Display Name for CPathProperties Class
    private static final String DISPLAY_NAME = "Pathway Commons Web Service Client";

	// Client ID. This should be unique.
    // TODO:  Generated Client ID via CPathProperties Class
    private static final String CLIENT_ID = "pathway_commons";

	// Instance of this client.  This is a singleton.
	private static final WebServiceClient client = new CytoscapeCPathWebService();

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

        //  TODO:  Can we add Tunable Lists? e.g. for pull-down menus
        props.add(new Tunable("NCI Taxonomy ID", "NCBI Taxonomy ID", Tunable.INTEGER,
		                      new Integer(9606)));
    }

    public void executeService(CyWebServiceEvent e) throws Exception {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(CyWebServiceEvent.WSEventType.IMPORT_NETWORK)) {
                CPathWebService webApi = CPathWebServiceImpl.getInstance();
                //webApi.getRecordsByIds(ids, format, new NullTaskMonitor());
            } else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.EXPAND_NETWORK)) {
			} else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.SEARCH_DATABASE)) {
                String q = e.getParameter().toString();
                CPathWebService webApi = CPathWebServiceImpl.getInstance();
                SearchResponseType response = webApi.searchPhysicalEntities(q, -1,
                        new NullTaskMonitor());
                System.out.println("Total Number of hits:  " + response.getTotalNumHits());
            }
		}
    }
}

