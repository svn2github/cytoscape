
package org.cytoscape.plugin.internal;


import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Task;


public class PluginLoaderTaskFactory implements TaskFactory {

	CyPluginAdapter adapter;

	PluginLoaderTaskFactory(CyPluginAdapter adapter) {
		this.adapter = adapter;
	}

	public Task getTask() {
		return new PluginLoaderTask(adapter);
	}
}
