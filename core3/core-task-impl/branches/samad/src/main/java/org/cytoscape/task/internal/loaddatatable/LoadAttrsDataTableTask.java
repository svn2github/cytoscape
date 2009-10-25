package org.cytoscape.task.internal.loaddatatable;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.cytoscape.io.CyIOFactoryManager;
import org.cytoscape.io.CyFileFilter;
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

public class LoadAttrsDataTableTask implements Task{

	@Tunable(description = "Data table file to load", flag={Param.attributes})
	public File file;

	final CyIOFactoryManager<CyDataTableReaderFactory> ioManager;
	final CyDataTableMapperTaskFactory mapperFactory;
	final CyNetworkManager networkManager;
	final CyDataTableFactory tableFactory;
	final String objType;

	Task superTask = null;

	public LoadAttrsDataTableTask(
		CyIOFactoryManager<CyDataTableReaderFactory> ioManager,
		CyDataTableMapperTaskFactory mapperFactory,
		CyNetworkManager networkManager,
		CyDataTableFactory tableFactory,
		String objType)
	{
		this.ioManager = ioManager;
		this.mapperFactory = mapperFactory;
		this.networkManager = networkManager;
		this.tableFactory = tableFactory;
		this.objType = objType;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		final CyDataTableReaderFactory readerFactory = ioManager.getFactoryFromURI(file.toURI());
		if (readerFactory == null)
			throw new Exception(makeReaderNotFoundMessage());
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
				CyDataTable targetTable = network.getCyDataTables(objType).get(CyNetwork.DEFAULT_ATTRS);
				if(targetTable == null)
					throw new IllegalStateException("Could not find target CyDataTable for " + objType);
				dumpTable(table);
				mapperFactory.setSource(table);
				mapperFactory.setSourceColumn("name");
				mapperFactory.setTarget(targetTable);
				mapperFactory.setTargetColumn("name");
				actualMapper = mapperFactory.getTask();
				actualMapper.run(monitor);
				actualMapper = null;

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

	String makeReaderNotFoundMessage()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><p>Could not open specified file \'" + file.getName() + "\' because the file type is not supported.</p>");
		Set<CyDataTableReaderFactory> factories = ioManager.getAllFactories();
		if (factories.size() == 0)
		{
			builder.append("<p>No readers for data tables could be found.</p>");
		}
		else
		{
			builder.append("<p>The following file types are supported:</p><p><ul>");
			for (CyDataTableReaderFactory factory : factories)
			{
				CyFileFilter fileFilter = factory.getCyFileFilter();
				builder.append("<li>");
				builder.append(fileFilter.getDescription());
				builder.append("</li>");
			}
			builder.append("</ul></p>");
		}
		builder.append("</html>");
		return builder.toString();
	}
}
