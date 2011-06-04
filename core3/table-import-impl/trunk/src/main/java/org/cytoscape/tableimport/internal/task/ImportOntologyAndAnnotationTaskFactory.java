package org.cytoscape.tableimport.internal.task;

import java.io.InputStream;

import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportOntologyAndAnnotationTaskFactory implements TaskFactory {
	
	private final InputStreamTaskFactory factory;
	private InputStream is;
	private String name;
	private final CyNetworkManager manager;
	
	public ImportOntologyAndAnnotationTaskFactory(final CyNetworkManager manager, final InputStreamTaskFactory factory, InputStream is, String name) {
		this.factory = factory;
		this.is = is;
		this.name = name;
		this.manager = manager;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ImportOntologyAndAnnotationTask(manager, factory, is, name));
	}
}
