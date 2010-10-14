package org.cytoscape.task.internal.loaddatatable;


import static org.cytoscape.io.DataCategory.TABLE;

import java.io.File;
import java.util.Properties;

import org.cytoscape.model.CyTable;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


public class LoadDataTableTask extends AbstractTask {
	@Tunable(description="Data table file to load", params="fileCategory=attribute")
	public File file;

	private CyTableReader reader;
	private final CyTableReaderManager mgr;

	public LoadDataTableTask(final CyTableReaderManager mgr) {
		this.mgr = mgr;
	}

	/**
	 * Executes Task.
	 */
	public void run(final TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setStatusMessage("Finding Data Table Reader...");
		taskMonitor.setProgress(-1.0);
		reader = mgr.getReader(file.toURI());

		if (reader == null)
			throw new NullPointerException("Failed to find reader for specified file!");

		taskMonitor.setStatusMessage("Importing Data Table...");

		insertTasksAfterCurrentTask(reader, new FinalStatusMessageUpdateTask(reader));
	}
}


class FinalStatusMessageUpdateTask extends AbstractTask {
	private final CyTableReader reader;

	FinalStatusMessageUpdateTask(final CyTableReader reader) {
		this.reader = reader;
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		for (CyTable table : reader.getCyDataTables())
			taskMonitor.setStatusMessage("Successfully loaded data table: " + table.getTitle());

		taskMonitor.setProgress(1.0);
	}
}
