package org.cytoscape.io.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public interface StreamUtil {

	public InputStream getInputStream(URL source) throws IOException;

	public InputStream getBasicInputStream(URL source) throws IOException;

	public URLConnection getURLConnection(URL source) throws IOException;

}
