package org.cytoscape.tableimport.internal.task;

import java.io.InputStream;

import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportOntologyAndAnnotationTask extends AbstractTask {
	
	private static final Logger logger = LoggerFactory.getLogger(ImportOntologyAndAnnotationTask.class);
	
	private final InputStreamTaskFactory factory;
	private final CyNetworkManager manager;
	private final String name;
	
	ImportOntologyAndAnnotationTask(final CyNetworkManager manager, final InputStreamTaskFactory factory, InputStream is, String name) {
		this.factory = factory;
		this.manager = manager;
		this.name = name;
		
		factory.setInputStream(is, name);
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		logger.debug("Start");
		Task loadOBOTask = factory.getTaskIterator().next();
		
		insertTasksAfterCurrentTask(new RegisterOntologyTask((CyNetworkReader) loadOBOTask, manager, name));
		insertTasksAfterCurrentTask(loadOBOTask);
		
	}
}
