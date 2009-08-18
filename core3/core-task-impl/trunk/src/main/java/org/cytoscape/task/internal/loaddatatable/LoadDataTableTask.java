package org.cytoscape.task.internal.loaddatatable;

import static org.cytoscape.io.DataCategory.TABLE;

import java.io.File;
import java.util.Properties;

import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;

public class LoadDataTableTask extends AbstractLoadDataTableTask {

	@Tunable(description = "Data table file to load", flag = { Param.attributes })
	public File file;

	public LoadDataTableTask(CyReaderManager mgr,
			Properties props) {
		super(mgr, props);
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;

		reader = mgr.getReader(file.toURI(), TABLE);
		
		uri = file.toURI();
		name = file.getName();

		if (reader == null) {
			uri = null;
		}

		System.out.println("\n\nData table " + file.getAbsolutePath()
				+ " will be loaded !!!\n\n");
		loadTable(reader);
		System.out.println("\n\nData table " + file.getAbsolutePath()
				+ " is LOADED !!!\n\n");
	}
}