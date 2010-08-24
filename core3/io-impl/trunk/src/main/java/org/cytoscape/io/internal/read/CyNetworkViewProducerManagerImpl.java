package org.cytoscape.io.internal.read;

import org.cytoscape.io.read.CyNetworkViewProducer;
import org.cytoscape.io.read.CyNetworkViewProducerFactory;
import org.cytoscape.io.read.CyNetworkViewProducerManager;
import org.cytoscape.io.DataCategory;

public class CyNetworkViewProducerManagerImpl 
	extends GenericProducerManager<CyNetworkViewProducerFactory,CyNetworkViewProducer> 
	implements CyNetworkViewProducerManager {

	public CyNetworkViewProducerManagerImpl() {
		super(DataCategory.NETWORK);
	}
}
