package org.cytoscape.internal.actions.welcomescreen;

import java.io.File;

import javax.swing.JComboBox;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class DownloadBiogridDataTaskFactory implements TaskFactory<DownloadBiogridDataTaskContext> {

	private final JComboBox list;
	
	private final File settingFile;
	
	DownloadBiogridDataTaskFactory(final JComboBox list, final CyApplicationConfiguration config) {
		settingFile = config.getConfigurationDirectoryLocation();
		this.list = list;
	}
	
	@Override
	public DownloadBiogridDataTaskContext createTaskContext() {
		return new DownloadBiogridDataTaskContext();
	}
	
	@Override
	public TaskIterator createTaskIterator(DownloadBiogridDataTaskContext context) {
		DownloadBiogridDataTask task = new DownloadBiogridDataTask(context, settingFile, list);
		return new TaskIterator(task);
	}
}
