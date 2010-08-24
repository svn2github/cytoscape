package org.cytoscape.io.internal.read.datatable;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.io.internal.read.AbstractDataTableReaderFactory;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;


public class TextDataTableReaderFactory extends AbstractDataTableReaderFactory {

	public TextDataTableReaderFactory(CyFileFilter filter, CyDataTableFactory factory) {
		super(filter, factory);
	}

	public CyDataTableReader getTask() {
		return new TextDataTableReader(inputStream, tableFactory );
	}
}
