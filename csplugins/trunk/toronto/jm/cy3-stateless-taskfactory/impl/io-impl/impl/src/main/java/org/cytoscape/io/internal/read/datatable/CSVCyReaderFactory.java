package org.cytoscape.io.internal.read.datatable;


import org.cytoscape.equations.EquationCompiler;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskContextImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.work.TaskIterator;


public class CSVCyReaderFactory implements InputStreamTaskFactory<InputStreamTaskContext> {
	private final CyFileFilter filter;
	private final boolean readSchema;
	private final boolean handleEquations;
	private final CyTableFactory tableFactory;
	private final EquationCompiler compiler;
	private final CyTableManager tableManager;

	public CSVCyReaderFactory(final CyFileFilter filter, final boolean readSchema,
				  final boolean handleEquations, final CyTableFactory tableFactory,
				  final EquationCompiler compiler, final CyTableManager tableManager)
	{
		this.filter          = filter;
		this.readSchema      = readSchema;
		this.handleEquations = handleEquations;
		this.tableFactory    = tableFactory;
		this.compiler        = compiler;
		this.tableManager    = tableManager;
	}
	
	@Override
	public TaskIterator createTaskIterator(InputStreamTaskContext context) {
		return new TaskIterator(new CSVCyReader(context, readSchema, handleEquations,
							tableFactory, compiler, tableManager));
	}

	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}
	
	@Override
	public InputStreamTaskContext createTaskContext() {
		return new InputStreamTaskContextImpl();
	}
}
