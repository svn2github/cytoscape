package org.cytoscape.cpathsquared.internal.task;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Properties;
import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import cpath.service.OutputFormat;

public class CPath2NetworkImportTask implements Task {

	private final String query;
	private final CPath2WebService client;
	private final OutputFormat format;
	private final CPath2Factory factory;

	public CPath2NetworkImportTask(String query, CPath2WebService client, OutputFormat format, CPath2Factory factory) {
		this.query = query;
		this.client = client;
		this.format = format;
		this.factory = factory;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
        String idStrs[] = query.split(" ");
        String ids[] = new String[idStrs.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = idStrs[i].trim();
        }

        //  Create the task
        ExecuteGetRecordByCPathIdTaskFactory taskFactory = factory
        	.createExecuteGetRecordByCPathIdTaskFactory(client, ids, format, CPath2Properties.serverName);
        TaskIterator iterator = taskFactory.createTaskIterator();
        while (iterator.hasNext()) {
        	Task task = iterator.next();
            task.run(taskMonitor);
        }
	}

	@Override
	public void cancel() {
	}
}
