package org.cytoscape.cpathsquared.internal;

import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTaskFactory;
import org.cytoscape.cpathsquared.internal.webservice.CPathProperties;
import org.cytoscape.cpathsquared.internal.webservice.CPathResponseFormat;
import org.cytoscape.cpathsquared.internal.webservice.CPathWebService;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class CPathNetworkImportTask implements Task {

	private final String query;
	private final CPathWebService client;
	private final CPathResponseFormat format;
	private final CPath2Factory factory;

	public CPathNetworkImportTask(String query, CPathWebService client, CPathResponseFormat format, CPath2Factory factory) {
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

//        ModuleProperties properties = this.getProps();
//        Tunable tunable = properties.get(RESPONSE_FORMAT);
//        CPathResponseFormat format = CPathResponseFormat.BINARY_SIF;
//        if (tunable != null) {
//            format = CPathResponseFormat.getResponseFormat((String) tunable.getValue());
//        }

        //  Create the task
        ExecuteGetRecordByCPathIdTaskFactory taskFactory = factory.createExecuteGetRecordByCPathIdTaskFactory(client, ids, format, CPathProperties.serverName);
        TaskIterator iterator = taskFactory.getTaskIterator();
        while (iterator.hasNext()) {
        	Task task = iterator.next();
            task.run(taskMonitor);
        }
	}

	@Override
	public void cancel() {
	}
}
