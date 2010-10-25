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

import org.springframework.core.InfrastructureProxy;


public class GenericReaderManager<T extends InputStreamTaskFactory, R extends Task>  {
	protected final DataCategory category; 
	protected final Set<T> factories;
	private static final Logger logger = LoggerFactory.getLogger( GenericReaderManager.class ); 

	public GenericReaderManager(DataCategory category) {
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
		if (factory == null)
			logger.warn("Specified factory is null!");
		else if (factory.getCyFileFilter().getDataCategory() == category) {
			final Object f;
			if (factory instanceof InfrastructureProxy)
				f = ((InfrastructureProxy)factory).getWrappedObject();
			else
				f = factory;

			logger.info("adding IO taskFactory (factory = " + f + ", category = " + category + ")");
			factories.add(factory);
		}
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
	public R getReader(URI uri) {
		for (T factory : factories) {
			logger.info("trying factory: " + factory);
			
			CyFileFilter cff = factory.getCyFileFilter();

			if (cff.accepts(uri, category) && uri != null ) {
				try {
					factory.setInputStream( uri.toURL().openStream() );
					return (R)factory.getTaskIterator().next();
				} catch (IOException e) {
					logger.warn("Error opening stream to URI: " + uri.toString(), e);
				}
			}
		}

		logger.info("No reader found for uri: " + uri.toString());
	 	return null;	
	}

	public R getReader(InputStream stream) {
		try {

			if ( !stream.markSupported() )
				stream = new MarkSupportedInputStream(stream);

			for (T factory : factories) {
				CyFileFilter cff = factory.getCyFileFilter();

				// Because we don't know who will provide the file filter or
				// what they might do with the InputStream, we provide a copy
				// of the first 2KB rather than the stream itself. 
				if (cff.accepts(CopyInputStream.copyKBytes(stream,2), category)) {
					factory.setInputStream(stream);
					return (R)factory.getTaskIterator().next();	
				}
			}
		} catch (IOException ioe) {
			logger.warn("Error setting input stream", ioe);
		}

		logger.info("No reader found for input stream");
	 	return null;	
	}
}
