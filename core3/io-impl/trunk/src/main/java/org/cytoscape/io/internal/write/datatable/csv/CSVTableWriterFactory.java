package org.cytoscape.io.internal.write.datatable.csv;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.write.datatable.AbstractCyTableWriterFactory;
import org.cytoscape.io.write.CyWriter;

public class CSVTableWriterFactory extends AbstractCyTableWriterFactory {

	public CSVTableWriterFactory(CyFileFilter fileFilter) {
		super(fileFilter);
	}
	
	@Override
	public CyWriter getWriterTask() {
		return new CSVCyWriter(outputStream, table);
	}
}
