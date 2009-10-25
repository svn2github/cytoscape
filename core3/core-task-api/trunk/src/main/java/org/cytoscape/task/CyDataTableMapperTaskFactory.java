package org.cytoscape.task;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.work.TaskFactory;

public interface CyDataTableMapperTaskFactory  extends TaskFactory
{
	public void setSource(CyDataTable source);
	public void setSourceColumn(String sourceColumn);
	public void setTarget(CyDataTable target);
	public void setTargetColumn(String targetColumn);
}
