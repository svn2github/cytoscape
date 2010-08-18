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

public class CyNetworkViewProducerManagerImpl implements CyNetworkViewProducerManager{

	private DataCategory category = DataCategory.NETWORK;
	
	/*
	 * Not generic because Spring does not support it now.
	 */
	@SuppressWarnings("unchecked")
	public void addInputStreamFactory(CyNetworkViewProducerFactory factory, Map props){
		
	}

	@SuppressWarnings("unchecked")
	public void removeInputStreamFactory(CyNetworkViewProducerFactory factory, Map props){
		
	}

	
	//////////////////

	private Set<CyNetworkViewProducerFactory> factories;

	/**
	 * Constructor.
	 */
	public CyNetworkViewProducerManagerImpl() {
		factories = new HashSet<CyNetworkViewProducerFactory>();
	}

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
	public CyNetworkViewProducer getProducer(URI fileLocation)
			throws IllegalArgumentException {
		return getProducer(fileLocation, null);
	}

	public CyNetworkViewProducer getProducer(InputStream stream)
			throws IllegalArgumentException {
		return getProducer(null, stream);
	}

	private CyNetworkViewProducer getProducer(URI uri, InputStream stream) {

		CyFileFilter cff;
		CyNetworkViewProducer networkViewProducer = null;


		for (CyNetworkViewProducerFactory factory : factories) {
			
			System.out.println("Checking factory ################# " + factory.getCyFileFilter().getDescription());
			
			
			cff = factory.getCyFileFilter();

			if (uri != null) {
				
				boolean accept = false;
				try
				{
					accept = cff.accept(uri, category);
				}
				catch (IOException e)
				{
					throw new IllegalArgumentException("Failed to check file's contents: " + uri.toString(), e);
				}
				if (accept)
				{
					try
					{
						networkViewProducer = factory.getNetworkViewProducer(uri);
					}
					catch (IOException e)
					{
						throw new IllegalArgumentException("Could not get proper reader for the file.", e);
					}
				}
			} else {
				System.out.println("################# " + cff.getClass());
				//if (cff.accept(stream, category))
				try
				{
					networkViewProducer = factory.getNetworkViewProducer(stream);
				}
				catch (IOException e)
				{
					throw new IllegalArgumentException("Could not get proper reader for the file.", e);
				}
			}
		}

		if (networkViewProducer == null) {
			throw new IllegalArgumentException("File type is not supported.");
		}
		
		System.out.println("### Producer found: " + networkViewProducer);
		return networkViewProducer;
	}

	
	
	
	
	
	
	
	
}
