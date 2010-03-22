package org.cytoscape.coreplugin.cpath2.test;

import junit.framework.TestCase;
import cytoscape.data.webservice.*;
import cytoscape.util.ModuleProperties;
import cytoscape.layout.Tunable;
import cytoscape.Cytoscape;
import cytoscape.CyMain;
import org.cytoscape.coreplugin.cpath2.web_service.*;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.SearchResponseType;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.SummaryResponseType;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.BasicRecordType;

import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Test Harness for the CytosacpeCPathWebService.
 *
 * NB:  The test below connect to the live instance of Pathway Commons.
 * As such, the data is likely to change, and these tests are likely to break.
 * 
 */
public class TestCytoscapeCPathWebService extends TestCase {

    public void testWebService() throws EmptySetException, CPathException {
        try {
            CyMain.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //  First, create the client and register it.
        WebServiceClient wsClient = CytoscapeCPathWebService.getClient();
        WebServiceClientManager.registerClient(wsClient);

        //  Get the client back from the manager
        wsClient = WebServiceClientManager.getClient(wsClient.getClientID());
        //validateStub(wsClient);

        CyWebServiceEventSupport eventManager =
                WebServiceClientManager.getCyWebServiceEventSupport();
        validateSearchEvent(wsClient, eventManager);
        validateImportEvent(eventManager);

        System.out.println("Press <ENTER> to exit: ");
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        try {
            in.readLine();
        } catch (IOException e) {
        }
    }

    private void validateImportEvent(CyWebServiceEventSupport eventManager) {
        CyWebServiceEvent wsEvent = new CyWebServiceEvent
                (CPathProperties.getInstance().getWebServicesId(),
                        CyWebServiceEvent.WSEventType.IMPORT_NETWORK, "1");
        try {
            eventManager.fireCyWebServiceEvent(wsEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateSearchEvent(WebServiceClient wsClient, CyWebServiceEventSupport eventManager) {
        //  Set Organism Filter
        ModuleProperties props = wsClient.getProps();
        props.add(new Tunable(CytoscapeCPathWebService.NCBI_TAXONOMY_ID_FILTER,
                "Filter by Organism - NCBI Taxonomy ID",
                Tunable.INTEGER, new Integer(9606)));
        CyWebServiceEvent wsEvent = new CyWebServiceEvent
                (CPathProperties.getInstance().getWebServicesId(),
                        CyWebServiceEvent.WSEventType.SEARCH_DATABASE, "brca1");

        // TODO:  Recmd:  fire should throw something narrower than Exception
        Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(new MiniListener());
        try {
            eventManager.fireCyWebServiceEvent(wsEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateStub(WebServiceClient wsClient) throws CPathException, EmptySetException {
        //  1.  try getting the stub and executing calls that way.
        CPathWebService webApi = (CPathWebService) wsClient.getClientStub();

        //  a.  Search Physical Entities
        SearchResponseType responseType =
                webApi.searchPhysicalEntities("brca1", -1, null);
        assertTrue (responseType.getTotalNumHits() > 0);

        //  b.  Get Records by ID
        long ids[] = new long[1];
        ids[0] = 1;
        String response = webApi.getRecordsByIds(ids, CPathResponseFormat.BIOPAX, null);
        assertTrue (response.length() > 100);
        response = webApi.getRecordsByIds(ids, CPathResponseFormat.BINARY_SIF, null);
        assertTrue (response.length() > 100);

        //  c. Get Organism List, which is not (yet) implemented.
        try {
            webApi.getOrganismList();
            fail ("UnsupportedOperationException should have been thrown.");
        } catch (UnsupportedOperationException e) {
        }

        //  d.  Get Parent Summaries.
        SummaryResponseType summaryResponseType = webApi.getParentSummaries(100, null);
        List<BasicRecordType> list = summaryResponseType.getRecord();
        assertTrue (list.size() > 1) ;
    }
}

class MiniListener implements PropertyChangeListener {

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("SEARCH_RESULT")) {
            DatabaseSearchResult result = (DatabaseSearchResult) propertyChangeEvent.getNewValue();
            System.out.println(result.getResultSize());
        }
    }
}
