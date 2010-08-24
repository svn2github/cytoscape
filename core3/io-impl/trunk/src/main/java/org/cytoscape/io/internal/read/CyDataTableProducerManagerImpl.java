package org.cytoscape.io.internal.read;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

import org.cytoscape.io.read.CyDataTableProducer;
import org.cytoscape.io.read.CyDataTableProducerFactory;
import org.cytoscape.io.read.CyDataTableProducerManager;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CyDataTableProducerManagerImpl implements CyDataTableProducerManager{

	private final DataCategory category = DataCategory.TABLE;
	private final Set<CyDataTableProducerFactory> factories = 
		new HashSet<CyDataTableProducerFactory>();
	private final Logger logger = LoggerFactory.getLogger(CyDataTableProducerManagerImpl.class); 

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void addDataTableProducerFactory(CyDataTableProducerFactory factory, Map props) {
		factories.add(factory);
	}

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void removeDataTableProducerFactory(CyDataTableProducerFactory factory, Map props) {
		factories.remove(factory);
	}

	/**
	 * Gets the GraphReader that is capable of reading the specified file.
	 * 
	 * @param fileName
	 *            File name or null if no reader is capable of reading the file.
	 * @return GraphReader capable of reading the specified file.
	 */
	public CyDataTableProducer getProducer(URI uri) {
		for (CyDataTableProducerFactory factory : factories) {
			
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

	public CyDataTableProducer getProducer(InputStream stream) {

		for (CyDataTableProducerFactory factory : factories) {
			
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
