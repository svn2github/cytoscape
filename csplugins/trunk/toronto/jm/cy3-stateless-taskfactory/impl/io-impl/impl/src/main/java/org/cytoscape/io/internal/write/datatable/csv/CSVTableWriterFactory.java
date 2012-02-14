package org.cytoscape.io.internal.write.datatable.csv;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.write.datatable.AbstractCyTableWriterFactory;
import org.cytoscape.io.write.CyTableWriterContext;
import org.cytoscape.io.write.CyTableWriterContextImpl;
import org.cytoscape.io.write.CyWriter;


public class CSVTableWriterFactory extends AbstractCyTableWriterFactory {
	private final boolean writeSchema;
	private final boolean handleEquations;

	public CSVTableWriterFactory(final CyFileFilter fileFilter, final boolean writeSchema,
				     final boolean handleEquations)
	{
		super(fileFilter);
		this.writeSchema     = writeSchema;
		this.handleEquations = handleEquations;
	}
	
	@Override
	public CyTableWriterContext createTaskContext() {
		return new CyTableWriterContextImpl();
	}
	
	@Override
	public CyWriter createWriterTask(CyTableWriterContext context) {
		return new CSVCyWriter(context.getOutputStream(), context.getTable(), writeSchema, handleEquations);
	}
}
