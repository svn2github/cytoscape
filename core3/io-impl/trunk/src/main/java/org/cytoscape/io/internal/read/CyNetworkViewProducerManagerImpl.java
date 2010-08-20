package org.cytoscape.io.internal.read;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

import org.cytoscape.io.read.CyNetworkViewProducer;
import org.cytoscape.io.read.CyNetworkViewProducerFactory;
import org.cytoscape.io.read.CyNetworkViewProducerManager;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CyNetworkViewProducerManagerImpl implements CyNetworkViewProducerManager{

	private final DataCategory category = DataCategory.NETWORK;
	private final Set<CyNetworkViewProducerFactory> factories = 
		new HashSet<CyNetworkViewProducerFactory>();
	private final Logger logger = LoggerFactory.getLogger(CyNetworkViewProducerManagerImpl.class); 

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void addNetworkViewProducerFactory(CyNetworkViewProducerFactory factory, Map props) {
		factories.add(factory);
	}

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void removeNetworkViewProducerFactory(CyNetworkViewProducerFactory factory, Map props) {
		factories.remove(factory);
	}

	/**
	 * Gets the GraphReader that is capable of reading the specified file.
	 * 
	 * @param fileName
	 *            File name or null if no reader is capable of reading the file.
	 * @return GraphReader capable of reading the specified file.
	 */
	public CyNetworkViewProducer getProducer(URI uri) {
		for (CyNetworkViewProducerFactory factory : factories) {
			
			CyFileFilter cff = factory.getCyFileFilter();

			if (uri != null) {
				
				boolean accept = false;
				try {
					accept = cff.accept(uri, category);
				} catch (IOException e) {
					logger.warn("Failed to check file's contents: " + uri.toString(), e);
				}

				if (accept) {
					try {
						factory.setInputStream( uri.toURL().openStream() );
						return factory.getTask();
					} catch (IOException e) {
						logger.error("Could not get proper reader for the file: " + uri.toString(), e);
					}
				}
			}
		}

		return null;
	}

	public CyNetworkViewProducer getProducer(InputStream stream) {

		for (CyNetworkViewProducerFactory factory : factories) {
			
			CyFileFilter cff = factory.getCyFileFilter();

			try {
				if (cff.accept(stream, category)) {
					factory.setInputStream(stream);
					return factory.getTask();	
				}
			} catch (IOException e) {
				logger.warn("Failed to check streams's contents: ", e);
			}
		}

		return null;
	}
}
