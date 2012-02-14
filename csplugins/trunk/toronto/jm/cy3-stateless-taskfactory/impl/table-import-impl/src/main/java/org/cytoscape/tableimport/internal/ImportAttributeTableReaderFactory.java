package org.cytoscape.tableimport.internal;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.work.TaskIterator;


public class ImportAttributeTableReaderFactory extends AbstractTableReaderFactory {
	private final static long serialVersionUID = 12023139869460898L;
	private final String fileFormat;

	/**
	 * Creates a new ImportAttributeTableReaderFactory object.
	 */
	public ImportAttributeTableReaderFactory(CyFileFilter filter, String fileFormat)
	{
		super(filter, CytoscapeServices.cyTableFactory);
		this.fileFormat = fileFormat;
	}

	public TaskIterator createTaskIterator(InputStreamTaskContext context) {
		return new TaskIterator(
			new ImportAttributeTableReaderTask(context.getInputStream(), fileFormat, CytoscapeServices.cyTableManager));
	}
}
