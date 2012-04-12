package org.cytoscape.app.internal.net;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is responsible for querying the Cytoscape App Store web service to obtain
 * information about available apps and app updates.
 */
public class WebQuerier {
	private static final String APP_STORE_URL = "http://nrnb.org/cyappstore/";
	
	private URL appStoreURL;
	
	public WebQuerier() {
		
		// Parse default string URL as an URL object
		try {
			appStoreURL = new URL(APP_STORE_URL);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed store URL: " + APP_STORE_URL, e);
		}
	}
	
	public void getAvailableApps() {
		
		// Open a connection to the app store website
		
		// Query the app store
		
		// Obtain the JSON-formatted result from the query
		String JSONresult;
	}
}
