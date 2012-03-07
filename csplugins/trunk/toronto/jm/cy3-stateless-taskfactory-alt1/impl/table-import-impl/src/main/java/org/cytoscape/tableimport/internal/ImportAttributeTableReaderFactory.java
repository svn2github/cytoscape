package org.cytoscape.tableimport.internal;


import java.io.InputStream;

import org.cytoscape.io.CyFileFilter;
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

	public TaskIterator createTaskIterator(InputStream inputStream, String inputName) {
		return new TaskIterator(
			new ImportAttributeTableReaderTask(inputStream, fileFormat, CytoscapeServices.cyTableManager));
	}
}
