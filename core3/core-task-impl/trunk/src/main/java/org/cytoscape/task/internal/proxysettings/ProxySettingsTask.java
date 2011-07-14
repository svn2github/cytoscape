package org.cytoscape.task.internal.proxysettings;


import org.cytoscape.io.util.StreamUtil;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.work.util.ListSingleSelection;

import java.net.URL;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;


/**
 * Dialog for assigning proxy settings.
 * @author Pasteur
 */
public class ProxySettingsTask extends AbstractTask {

	final TaskManager taskManager;
	final StreamUtil streamUtil;
	
	public ProxySettingsTask(final TaskManager taskManager, final StreamUtil streamUtil) {
		this.taskManager = taskManager;
		this.streamUtil = streamUtil;
	}
	
	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setProgress(0.01);
		taskMonitor.setTitle("Set proxy server");
		taskMonitor.setStatusMessage("Setting proxy server...");
	
		// We run ProxySeting in another task, because TunableValidator is used. If we run
		// it in the same task, Cytoscape will be frozen during validating process
		ProxySettingsTask2 task = new ProxySettingsTask2(this.taskManager, this.streamUtil);
		
		this.insertTasksAfterCurrentTask(task);

		taskMonitor.setProgress(0.05);
	}
}

