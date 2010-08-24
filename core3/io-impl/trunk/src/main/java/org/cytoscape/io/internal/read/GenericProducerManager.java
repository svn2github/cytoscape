package org.cytoscape.io.internal.read;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.work.Task;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class GenericProducerManager<T extends InputStreamTaskFactory, S extends Task>  {

	protected final DataCategory category; 
	protected final Set<T> factories;
	private static final Logger logger = LoggerFactory.getLogger( GenericProducerManager.class ); 

	public GenericProducerManager(DataCategory category) {
		this.category = category;
		factories = new HashSet<T>();
	}
	

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void addInputStreamTaskFactory(T factory, Map props) {
		if ( factory != null && factory.getCyFileFilter().getDataCategory() == category ) {
			logger.info("adding IO taskFactory ");
			factories.add(factory);
		} else
			logger.warn("Specified factory is null or has wrong DataCategory (" + category + ")");
	}

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void removeInputStreamTaskFactory(T factory, Map props) {
		factories.remove(factory);
	}

	/**
	 * Gets the GraphReader that is capable of reading the specified file.
	 * 
	 * @param fileName
	 *            File name or null if no reader is capable of reading the file.
	 * @return GraphReader capable of reading the specified file.
	 */
	public S getProducer(URI uri) {

		for (T factory : factories) {
			logger.info("trying factory: " + factory);
			
			CyFileFilter cff = factory.getCyFileFilter();

			if (cff.accept(uri, category) && uri != null ) {
				try {
					factory.setInputStream( uri.toURL().openStream() );
					return (S)(factory.getTask());
				} catch (IOException e) {
					logger.warn("Error opening stream to URI: " + uri.toString(), e);
				}
			}
		}

	 	return null;	
	}

	public S getProducer(InputStream stream) {

		for (T factory : factories) {
			
			CyFileFilter cff = factory.getCyFileFilter();

			if (cff.accept(stream, category)) {
				factory.setInputStream(stream);
				return (S)(factory.getTask());	
			}
		}

	 	return null;	
	}
}
