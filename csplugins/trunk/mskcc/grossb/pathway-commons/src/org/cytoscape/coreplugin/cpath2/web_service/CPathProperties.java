package org.cytoscape.coreplugin.cpath2.web_service;

import cytoscape.CytoscapeInit;
import java.util.Properties;

/**
 * Contains cPath Specific Properties
 *
 * @author Ethan Cerami.
 */
public class CPathProperties {
	/**
	 * Property:  CPath Read Location.
	 */
	private static final String CPATH_URL = new String("cpath.url");

	/**
	 * Gets URL for cPath Web Service API.
	 *
	 * @return cPath URL.
	 */
	public static String getCPathUrl() {
		Properties properties = CytoscapeInit.getProperties();
		String url = properties.getProperty(CPATH_URL);

		if (url != null) {
			return url;
		} else {
			//  hard-coded default
			return "http://localhost:8080/cpath/webservice.do";
           //return "http://awabi.cbio.mskcc.org/pc-demo/webservice.do";
        }
	}
}
