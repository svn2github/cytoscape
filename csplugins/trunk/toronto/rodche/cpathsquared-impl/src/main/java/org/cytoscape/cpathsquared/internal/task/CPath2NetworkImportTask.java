package org.cytoscape.cpathsquared.internal.task;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import cpath.service.OutputFormat;

public class CPath2NetworkImportTask implements Task {

	private final String query;
	private final OutputFormat format;

	public CPath2NetworkImportTask(String query, OutputFormat format) {
		this.query = query;
		this.format = format;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
        String idStrs[] = query.split(" ");
        String ids[] = new String[idStrs.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = idStrs[i].trim();
        }

        //  Create the task
        TaskFactory taskFactory = CPath2Factory.newTaskFactory(
        	new ExecuteGetRecordByCPathIdTask(ids, format, CPath2Factory.serverName));
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
