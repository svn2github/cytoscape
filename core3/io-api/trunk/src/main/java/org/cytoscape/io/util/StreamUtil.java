package org.cytoscape.io.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A stateless utility class that provides special handling to support
 * InputStreams and URLConnections over the network. 
 */
public interface StreamUtil {

	// TODO what's the difference between these two methods?
	/**
	 * @param source The URL from which to generate the InputStream.
	 * @return An input stream from the specified URL.
	 */
	public InputStream getInputStream(URL source) throws IOException;

	/**
	 * @param source The URL from which to generate the InputStream.
	 * @return An input stream from the specified URL.
	 */
	public InputStream getBasicInputStream(URL source) throws IOException;

	/**
	 * @param source The URL from which to generate the URLConnection.
	 * @return An URLConnection from the specified URL.
	 */
	public URLConnection getURLConnection(URL source) throws IOException;

}
