package cytoscape.tutorial13;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.www.soap.eutils.einfo.EInfoRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.einfo.EInfoResult;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import javax.swing.JOptionPane;


public class My_NCBIClient extends WebServiceClientImpl {
	private static final String DISPLAY_NAME = "tutorial13 web service client";
	private static final String CLIENT_ID = "tutorial13";
	private static My_NCBIClient client;
	
	// Client should be a singleton
	static {
		try {
			client = new My_NCBIClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static WebServiceClient<EUtilsServiceSoap> getClient() {
		return client;
	}

	
	private EUtilsServiceSoap utils;
	
	/**
	 * Creates a new NCBIClient object.
	 * @throws CyWebServiceException 
	 *
	 * @throws Exception  DOCUMENT ME!
	 */
	public My_NCBIClient() throws CyWebServiceException {
		super(CLIENT_ID, "Tutorial13 web service client");
		
		System.out.println("Initializing web service client (tutorial13)...");

        try
        {
            EUtilsServiceLocator service = new EUtilsServiceLocator();
            utils = service.geteUtilsServiceSoap();
            setClientStub(utils);
        }
        catch(Exception ex) {
        	ex.printStackTrace(); 
        }

	}


	public void executeService(CyWebServiceEvent e) throws CyWebServiceException {
		
		if (e.getSource().equals(CLIENT_ID)) {

			if (e.getEventType().equals(CyWebServiceEvent.WSEventType.IMPORT_NETWORK)) {
				//
			}
			else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.IMPORT_ATTRIBUTE)) {
				//
			}
			else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.SEARCH_DATABASE)) {
				//
				System.out.println("Received an event: CyWebServiceEvent.WSEventType.SEARCH_DATABASE!");
			}
			else {
				//
			}
		}
	}
}
