package org.cytoscape.io.internal.read;

import org.cytoscape.io.read.CyDataTableProducer;
import org.cytoscape.io.read.CyDataTableProducerFactory;
import org.cytoscape.io.read.CyDataTableProducerManager;
import org.cytoscape.io.DataCategory;

public class CyDataTableProducerManagerImpl 
	extends GenericProducerManager<CyDataTableProducerFactory,CyDataTableProducer> 
	implements CyDataTableProducerManager {

	public CyDataTableProducerManagerImpl() {
		super(DataCategory.TABLE);
	}
}
