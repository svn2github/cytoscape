package org.cytoscape.app.internal.net.server;

import java.util.Map;

import org.cytoscape.app.internal.net.server.LocalHttpServer.Response;

/**
 * This class is responsible for handling GET requests received by the local HTTP server.
 */
public class LocalGetResponder implements LocalHttpServer.GetResponder{

	private static final String STATUS_QUERY_URL = "status";
	private static final String STATUS_QUERY_APP_NAME = "appname";
	
	private static final String INSTALL_QUERY_URL = "install";
	private static final String INSTALL_QUERY_APP_NAME = "appname";
	
	
	@Override
	public boolean canRespondTo(String url) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Response respond(String url) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Parses the parameters from an URL encoded in the application/x-www-form-urlencoded form, 
	 * which is the default form. This method uses a simple parsing method, only scanning text after the last '?' symbol
	 * and splitting with the '=' symbol. A more comprehensive (and possibly securer) parser can be found in the URLEncodedUtils 
	 * class of the Apache HttpClient library.
	 * 
	 * @param url
	 * @return
	 */
	private Map<String, String> parseEncodedUrl(String url) {
		int lastIndex = url.lastIndexOf("?");
		
		String paramSubstring = url.substring(lastIndex);
		
		return null;
	}
}
