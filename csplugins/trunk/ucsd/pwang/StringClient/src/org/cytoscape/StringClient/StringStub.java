package org.cytoscape.StringClient;

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
	//private String baseURL = "http://stitch.embl.de/api/psi-mi/interactions?";
	
	private String URLStr= "";
	public StringStub() {
	}
		
	public void setQueryParam(final ModuleProperties pProps, String pQuery){
				
		ModulePropertiesImpl props = (ModulePropertiesImpl) pProps;
		
		// Construct queryParam
		String queryParameter = "";

		String[] keywords = pQuery.split("[ \n\t]+");
		queryParameter = "identifier="; 
		for (int i=0; i<keywords.length; i++) {
			queryParameter += keywords[i];
			if (i < (keywords.length-1)) {
				queryParameter +=  "%20";
			}
		}

		int additional_network_nodes = ((Integer) props.get("additional_network_nodes").getValue()).intValue();
		int limit = ((Integer) props.get("limit").getValue()).intValue();
		int network_depth = ((Integer) props.get("network_depth").getValue()).intValue();
		int required_score = ((Integer) props.get("required_score").getValue()).intValue();
		String species = ((String) props.get("species").getValue()).toString();

		queryParameter += "&additional_network_nodes="+ additional_network_nodes;
		queryParameter += "&limit="+ limit;
		queryParameter += "&network_depth="+ network_depth;
		queryParameter += "&required_score="+ required_score;
		queryParameter += "&species="+ species;

		// Construct URLstr
		URLStr = baseURL + queryParameter;
	}

	public String getURLstr(){
		//Sample URL: http://string.embl.de/api/psi-mi/interactions?identifier=PGH1_HUMAN&species=auto_detect
		return URLStr;
	}	
}
