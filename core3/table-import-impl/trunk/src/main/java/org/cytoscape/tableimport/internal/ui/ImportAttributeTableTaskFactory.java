package org.cytoscape.tableimport.internal.ui;


import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportAttributeTableTaskFactory implements TaskFactory
{
	ImportAttributeTableTask task;
	
	public ImportAttributeTableTaskFactory(ImportAttributeTableTask task){
		this.task = task;
	}
	public TaskIterator getTaskIterator() {
		return new TaskIterator(task);
	}
	
}
