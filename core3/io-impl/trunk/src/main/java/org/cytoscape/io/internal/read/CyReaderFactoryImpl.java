package org.cytoscape.io.internal.read;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderFactory;
import org.cytoscape.io.util.StreamUtil;

public class CyReaderFactoryImpl implements CyReaderFactory {

	private CyFileFilter filter;
	private CyReader reader;
	private StreamUtil streamUtil;

	// This should be an OSGi service.
	private Proxy proxy;

	public CyReaderFactoryImpl(CyFileFilter filter, CyReader reader, StreamUtil streamUtil)
			throws IllegalArgumentException {
		this.filter = filter;
		this.reader = reader;

		if (this.reader == null) {
			throw new IllegalArgumentException("Reader cannot be null.");
		} else if (this.reader == null) {
			throw new IllegalArgumentException("CyFileFilter cannot be null.");
		}
		this.streamUtil = streamUtil;
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
		InputStream is = streamUtil.getInputStream(uri.toURL());
		return getReader(is);
	}

	public CyReader getReader(InputStream stream) throws IOException {
		reader.setInputStream(stream);
		return reader;
	}

	public CyFileFilter getCyFileFilter() {
		return filter;
	}

}
