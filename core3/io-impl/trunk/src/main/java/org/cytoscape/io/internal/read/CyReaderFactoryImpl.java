package org.cytoscape.io.internal.read;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;
import java.net.URLConnection;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderFactory;

public class CyReaderFactoryImpl implements CyReaderFactory {

	private CyFileFilter filter;
	private CyReader reader;

	// This should be an OSGi service.
	private Proxy proxy;

	public CyReaderFactoryImpl(CyFileFilter filter, CyReader reader)
			throws IllegalArgumentException {
		this.filter = filter;
		this.reader = reader;

		if (this.reader == null) {
			throw new IllegalArgumentException("Reader cannot be null.");
		} else if (this.reader == null) {
			throw new IllegalArgumentException("CyFileFilter cannot be null.");
		}
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * Gets Graph Reader.
	 * 
	 * @param fileName
	 *            File name.
	 * @return GraphReader Object.
	 * @throws IOException
	 */
	public CyReader getReader(URI uri) throws IOException {
		final URLConnection urlConn;

		// Proxy available
		if (proxy != null) {
			urlConn = uri.toURL().openConnection(proxy);
		} else {
			urlConn = uri.toURL().openConnection();
		}

		return getReader(urlConn.getInputStream());
	}

	public CyReader getReader(InputStream stream) throws IOException {
		reader.setInputStream(stream);
		return reader;
	}

	public CyFileFilter getCyFileFilter() {
		return filter;
	}

}
