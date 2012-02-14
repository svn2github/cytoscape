package org.cytoscape.tableimport.internal;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.work.TaskIterator;


public class ImportNetworkTableReaderFactory extends AbstractNetworkReaderFactory {
	private final static long serialVersionUID = 12023139869460154L;
	private final String fileFormat;
	private final CyTableManager tableManager;

	/**
	 * Creates a new ImportNetworkTableReaderFactory object.
	 */
	public ImportNetworkTableReaderFactory(final CyFileFilter filter,
	                                       final String fileFormat) {
		super(filter, CytoscapeServices.cyNetworkViewFactory, CytoscapeServices.cyNetworkFactory);

		this.tableManager = CytoscapeServices.cyTableManager;
		this.fileFormat = fileFormat;

	}

	public TaskIterator createTaskIterator(InputStreamTaskContext context) {
		return new TaskIterator(new ImportNetworkTableReaderTask(context.getInputStream(), fileFormat,
		                                                         context.getInputName(), tableManager));
	}
}
