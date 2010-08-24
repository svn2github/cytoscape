package org.cytoscape.io.internal.read.datatable;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyDataTableProducer;
import org.cytoscape.io.internal.read.AbstractDataTableProducerFactory;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;


public class TextDataTableReaderFactory extends AbstractDataTableProducerFactory {

	public TextDataTableReaderFactory(CyFileFilter filter, CyDataTableFactory factory) {
		super(filter, factory);
	}

	public CyDataTableProducer getTask() {
		return new TextDataTableReader(inputStream, tableFactory );
	}
}
