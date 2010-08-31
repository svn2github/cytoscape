package org.cytoscape.io.internal.read.datatable;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.io.internal.read.AbstractDataTableReaderFactory;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.work.TaskIterator;


public class TextDataTableReaderFactory extends AbstractDataTableReaderFactory {
	public TextDataTableReaderFactory(CyFileFilter filter, CyDataTableFactory factory) {
		super(filter, factory);
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new TextDataTableReader(inputStream, tableFactory));
	}
}
