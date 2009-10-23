package org.cytoscape.task.internal.loaddatatable;

import java.io.File;
import java.io.FileInputStream;

import org.cytoscape.io.CyIOFactoryManager;
import org.cytoscape.io.read.CyDataTableReaderFactory;
import org.cytoscape.task.CyDataTableMapperTaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Task;
import org.cytoscape.work.SuperTask;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class LoadDataTableTask implements Task{

	@Tunable(description = "Data table file to load")
	public File file;

	final CyDataTableReaderFactory readerFactory;
	final CyDataTableMapperTaskFactory mapperFactory;
	final CyNetworkManager networkManager;
	final CyDataTableFactory tableFactory;

	Task superTask = null;

	public LoadDataTableTask(
		CyDataTableReaderFactory readerFactory,
		CyDataTableMapperTaskFactory mapperFactory,
		CyNetworkManager networkManager,
		CyDataTableFactory tableFactory)
	{
		this.readerFactory = readerFactory;
		this.mapperFactory = mapperFactory;
		this.networkManager = networkManager;
		this.tableFactory = tableFactory;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {

		final CyDataTable table = tableFactory.createTable("tmp", false);
		final Task readerTask = readerFactory.getReader(new FileInputStream(file), table);

		final Task mapperTask = new Task()
		{
			Task actualMapper = null;
			public void run(TaskMonitor monitor) throws Exception
			{
				CyNetwork network = networkManager.getCurrentNetwork();
				if( network == null)
					throw new IllegalStateException("Could not find current network.");
				CyDataTable targetTable = network.getCyDataTables("NODE").get(CyNetwork.DEFAULT_ATTRS);
				if(targetTable == null)
					throw new IllegalStateException("Could not find target CyDataTable for " + "NODE");
				dumpTable(table);
				mapperFactory.setSource(table);
				mapperFactory.setSourceColumn("name");
				mapperFactory.setTarget(targetTable);
				mapperFactory.setTargetColumn("name");
				actualMapper = mapperFactory.getTask();
				actualMapper.run(monitor);

				for(CyNode node: network.getNodeList()) {
					System.out.println("Attr for " + node.getSUID() + " =========> " + node.attrs().toString());
				}
			}

			public void cancel()
			{
				if (actualMapper != null)
					actualMapper.cancel();
			}
		};

		superTask = new SuperTask("Opening text data table", readerTask, mapperTask);
		superTask.run(taskMonitor);
	}

	public void cancel()
	{
		if (superTask != null)
			superTask.cancel();
	}

	void dumpTable(CyDataTable table)
	{
		System.out.println("Columns: " + table.getUniqueColumns());
		for (org.cytoscape.model.CyRow row : table.getAllRows())
		{
			System.out.println(row.toString());
		}
	}
}
