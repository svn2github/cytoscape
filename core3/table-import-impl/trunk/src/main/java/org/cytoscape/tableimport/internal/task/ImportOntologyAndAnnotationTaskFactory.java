package org.cytoscape.tableimport.internal.task;

import java.io.InputStream;

import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportOntologyAndAnnotationTaskFactory implements TaskFactory {

	private final InputStreamTaskFactory factory;
	private InputStream is;
	private String name;
	private final CyNetworkManager manager;
	
	final CyTableFactory tableFactory;
	final InputStream gaStream;
	private String tableName;

	public ImportOntologyAndAnnotationTaskFactory(final CyNetworkManager manager, final InputStreamTaskFactory factory,
			InputStream is, String name, final CyTableFactory tableFactory,
			final InputStream gaStream, final String tableName) {
		this.factory = factory;
		this.is = is;
		this.name = name;
		this.manager = manager;
		
		this.tableFactory = tableFactory;
		this.gaStream = gaStream;
		this.tableName = tableName;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ImportOntologyAndAnnotationTask(manager, factory, is, name, tableFactory, gaStream, tableName));
	}
}
