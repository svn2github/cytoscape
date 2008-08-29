package org.cytoscape.StringClient;

import cytoscape.layout.Tunable;
import cytoscape.util.ModuleProperties;
import cytoscape.util.ModulePropertiesImpl;

/**
 * String Web Service Stub.<br>
 *
 * @author pwang
 * @version 0.1
 * @since Cytoscape 2.6
 */

public class StringStub {
	// Base URL of the String Web Service API
	private String baseURL = "http://string.embl.de/api/psi-mi/interactions?";
	private String URLStr= "";
	public StringStub() {
	}
		
	public void setQueryParam(final ModuleProperties pProps, String pQuery){
		
		ModulePropertiesImpl props = (ModulePropertiesImpl) pProps;
		
		// Construct queryParam
		String queryParameter = ""; //"identifier=PGH1_HUMAN&species=auto_detect";
		
		int additional_network_nodes = ((Integer) props.get("additional_network_nodes").getValue()).intValue();
		int limit = ((Integer) props.get("limit").getValue()).intValue();
		int network_depath = ((Integer) props.get("network_depth").getValue()).intValue();
		int required_score = ((Integer) props.get("required_score").getValue()).intValue();
		String species = ((String) props.get("species").getValue()).toString();

		String[] keywords = pQuery.split("[ |\n]");
		
		queryParameter = "identifier="; 
		for (int i=0; i<keywords.length; i++) {
			queryParameter += keywords[i] + "%20";			
		}
		queryParameter += "&";

		// Construct URLstr -- baseURL + queryParam
		
		URLStr = baseURL + queryParameter; //"http://string.embl.de/api/psi-mi/interactions?identifier=PGH1_HUMAN&species=auto_detect";
		URLStr = "http://string.embl.de/api/psi-mi/interactions?identifier=PGH1_HUMAN&species=auto_detect";
	}

	public String getURLstr(){
		//Sample URL: http://string.embl.de/api/psi-mi/interactions?identifier=PGH1_HUMAN&species=auto_detect
		return URLStr;
	}
	
	public static void main(String[] argv ) {
		StringStub anInstance = new StringStub();
		
		ModulePropertiesImpl props = new ModulePropertiesImpl("String", "wsc");

		props.add(new Tunable("additional_network_nodes", "Additional network nodes", Tunable.INTEGER,
		                      new Integer(0)));
		props.add(new Tunable("limit", "limit", Tunable.INTEGER, new Integer(10)));		
		props.add(new Tunable("network_depth", "Network depth", Tunable.INTEGER, new Integer(1)));
		props.add(new Tunable("required_score", "Required score", Tunable.INTEGER, new Integer(400)));
		props.add(new Tunable("species", "species", Tunable.STRING, new String("auto_detect")));

		anInstance.setQueryParam(props, "Ppotein1");
		
		String URLstr = anInstance.getURLstr();
		System.out.println("URLstr = " + URLstr + "\n");
	}
}
