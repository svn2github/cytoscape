package org.cytoscape.task.internal.loaddatatable;


import static org.cytoscape.io.DataCategory.TABLE;

import java.io.File;
import java.util.Properties;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.io.read.CyDataTableReaderManager;
import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


public class LoadDataTableTask extends AbstractTask {
	@Tunable(description = "Data table file to load", flags = { Param.attributes })
	public File file;

	private CyDataTableReader reader;
	private final CyDataTableReaderManager mgr;

	public LoadDataTableTask(final CyDataTableReaderManager mgr) {
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

		insertTaskAfterCurrentTask(new FinalStatusMessageUpdateTask(reader));
		insertTaskAfterCurrentTask(reader);
	}

	@Override
	public void cancel() {
	}
}


class FinalStatusMessageUpdateTask extends AbstractTask {
	private final CyDataTableReader reader;

	FinalStatusMessageUpdateTask(final CyDataTableReader reader) {
		this.reader = reader;
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		for (CyDataTable table : reader.getCyDataTables())
			taskMonitor.setStatusMessage("Successfully loaded data table: " + table.getTitle());

		taskMonitor.setProgress(1.0);
	}

	@Override
	public void cancel() {
	}
}
