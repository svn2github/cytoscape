package org.cytoscape.io.internal.write.datatable.csv;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.write.datatable.AbstractCyTableWriterFactory;
import org.cytoscape.io.write.CyWriter;

public class CSVTableWriterFactory extends AbstractCyTableWriterFactory {

	private final boolean writeSchema;

	public CSVTableWriterFactory(CyFileFilter fileFilter, boolean writeSchema) {
		super(fileFilter);
		this.writeSchema = writeSchema;
	}
	
	@Override
	public CyWriter getWriterTask() {
		return new CSVCyWriter(outputStream, table, writeSchema);
	}
}
