package edu.ucsd.bioeng.idekerlab.ncbiclient;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
import cytoscape.data.webservice.WebServiceClientImpl;

public class NCBIClient extends WebServiceClientImpl {
	private static final String DISPLAY_NAME = "NCBI Entrez Utilities Web Service Client";
	private static final String SERVICE_NAME = "ncbi_entrez";

	public NCBIClient() throws Exception {
		super(SERVICE_NAME, DISPLAY_NAME);
		EUtilsServiceLocator service = new EUtilsServiceLocator();
		stub = service.geteUtilsServiceSoap();
	}		

}
