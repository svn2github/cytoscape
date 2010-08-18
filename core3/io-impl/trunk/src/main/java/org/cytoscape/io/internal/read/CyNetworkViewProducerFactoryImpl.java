package org.cytoscape.io.internal.read;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyNetworkViewProducer;
import org.cytoscape.io.read.CyNetworkViewProducerFactory;
import org.cytoscape.io.util.StreamUtil;

public class CyNetworkViewProducerFactoryImpl implements CyNetworkViewProducerFactory {

	private CyFileFilter filter;
	private CyNetworkViewProducer producer;
	private StreamUtil streamUtil;
	private InputStream is;
	
	// This should be an OSGi service.
	private Proxy proxy;

	public CyNetworkViewProducerFactoryImpl(CyFileFilter filter, CyNetworkViewProducer producer, StreamUtil streamUtil)
			throws IllegalArgumentException {
		this.filter = filter;
		this.producer = producer;

		if (this.producer == null) {
			throw new IllegalArgumentException("Reader cannot be null.");
		} else if (this.producer == null) {
			throw new IllegalArgumentException("CyFileFilter cannot be null.");
		}
		this.streamUtil = streamUtil;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public CyNetworkViewProducer getTask(){
		///?????
		return null;
	}
	public void setInputStream(InputStream is) {
		//????
		this.is = is;
	}

	
	/**
	 * Gets Graph Reader.
	 * 
	 * @param fileName
	 *            File name.
	 * @return GraphReader Object.
	 * @throws IOException
	 */
	public CyNetworkViewProducer getNetworkViewProducer(URI uri) throws IOException {
		InputStream is = streamUtil.getInputStream(uri.toURL());
		return getNetworkViewProducer(is);
	}

	public CyNetworkViewProducer getNetworkViewProducer(InputStream stream) throws IOException {
		producer.setInputStream(stream);
		return producer;
	}

	public CyFileFilter getCyFileFilter() {
		return filter;
	}
}
