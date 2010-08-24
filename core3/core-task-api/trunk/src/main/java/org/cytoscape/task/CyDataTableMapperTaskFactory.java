package org.cytoscape.task;


import org.cytoscape.model.CyDataTable;
import org.cytoscape.work.TaskFactory;


public interface CyDataTableMapperTaskFactory  extends TaskFactory {
	public void setSourceAndTarget(CyDataTable source, String sourceColumn, CyDataTable target, String targetColumn);
}
