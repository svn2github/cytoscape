package org.cytoscape.tableimport.internal.task;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportOntologyAndAnnotationTaskFactory implements TaskFactory {

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ImportOntologyAndAnnotationTask());
	}

}
