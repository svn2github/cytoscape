package org.cytoscape.io.internal.read.datatable;

import java.io.InputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.work.TaskIterator;

public class CSVCyReaderFactory implements InputStreamTaskFactory {

	private final CyFileFilter filter;
	private InputStream stream;
	private boolean readSchema;
	private CyTableFactory tableFactory;

	public CSVCyReaderFactory(CyFileFilter filter, boolean readSchema, CyTableFactory tableFactory) {
		this.filter = filter;
		this.readSchema = readSchema;
		this.tableFactory = tableFactory;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new CSVCyReader(stream, readSchema, tableFactory));
	}

	@Override
	public CyFileFilter getCyFileFilter() {
		return filter;
	}

	@Override
	public void setInputStream(InputStream stream, String inputName) {
		this.stream = stream;
	}

}
