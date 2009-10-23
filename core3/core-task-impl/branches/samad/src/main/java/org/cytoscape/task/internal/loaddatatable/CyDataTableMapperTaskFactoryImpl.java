package org.cytoscape.task.internal.loaddatatable;

import java.util.List;
import java.util.Map;
import org.cytoscape.task.CyDataTableMapperTaskFactory;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskFactory;

public class CyDataTableMapperTaskFactoryImpl implements CyDataTableMapperTaskFactory 
{
	CyDataTable source = null;
	String sourceColumn = null;
	CyDataTable target = null;
	String targetColumn = null;

	public CyDataTableMapperTaskFactoryImpl()
	{
	}

	public void setSource(CyDataTable source)
	{
		this.source = source;
	}

	public void setSourceColumn(String sourceColumn)
	{
		this.sourceColumn = sourceColumn;
	}
	public void setTarget(CyDataTable target)
	{
		this.target = target;
	}
	
	public void setTargetColumn(String targetColumn)
	{
		this.targetColumn = targetColumn;
	}
	public Task getTask()
	{
		return new Task()
		{
			boolean cancel = false;

			public void run(TaskMonitor monitor)
			{
				// Init columns in target
				monitor.setStatusMessage("Creating columns in target data table");
				Map<String,Class<?>> sourceColumnTypes = source.getColumnTypeMap();
				for (String sourceColumnName : sourceColumnTypes.keySet())
				{
					if (sourceColumnName.equals(sourceColumn))
						continue;

					target.createColumn(sourceColumnName, sourceColumnTypes.get(sourceColumnName), true);
				}

				// Copy contents
				monitor.setStatusMessage("Mapping contents between tables");
				List<CyRow> sourceRows = source.getAllRows();
				for (int i = 0; i < sourceRows.size(); i++)
				{
					monitor.setProgress(i / ((double) sourceRows.size()));
					List<CyRow> targetRows = target.getAllRows();
					CyRow sourceRow = sourceRows.get(i);
					for (int j = 0; j < targetRows.size(); j++)
					{
						CyRow targetRow = targetRows.get(j);
						processRow(sourceRow, targetRow);
					}
				}
			}

			public void cancel()
			{
				cancel = true;
			}

			private void processRow(CyRow sourceRow, CyRow targetRow)
			{
				if (!sourceRow.get(sourceColumn, Object.class).equals(targetRow.get(targetColumn, Object.class)))
					return;
				Map<String,Object> sourceValues = sourceRow.getAllValues();
				for (String sourceColumnName : sourceValues.keySet())
				{
					if (sourceColumnName.equals(sourceColumn))
						continue;
					targetRow.set(sourceColumnName, sourceValues.get(sourceColumnName));
				}
			}
		};
	}
}
