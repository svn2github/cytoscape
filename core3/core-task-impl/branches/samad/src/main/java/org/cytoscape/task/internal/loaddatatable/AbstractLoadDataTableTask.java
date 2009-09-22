package org.cytoscape.task.internal.loaddatatable;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public abstract class AbstractLoadDataTableTask implements Task {

	protected CyReader reader;
	protected URI uri;
	protected TaskMonitor taskMonitor;
	protected String name;
	protected Thread myThread = null;
	protected boolean interrupted = false;
	protected CyReaderManager mgr;
	protected Properties props;

	protected CyNetworkNaming namingUtil;

	public AbstractLoadDataTableTask(CyReaderManager mgr,
			Properties props) {
		this.mgr = mgr;
		this.props = props;
	}

	protected void loadTable(CyReader reader) throws Exception {
		if (reader == null)
			throw new Exception("Could not read file: file reader was null");

		try {
			myThread = Thread.currentThread();
			taskMonitor.setStatusMessage("Reading in Data Table...");

			taskMonitor.setProgress(-1.0);

			taskMonitor.setStatusMessage("Importing Data Table...");

			final Map<Class<?>, Object> readData = reader.read();

			CyDataTable table = (CyDataTable) readData.get(CyDataTable.class);

			if (table != null) {
				informUserOfTableStats(table);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not read table from: ");
				sb.append(name);
				sb.append("\nThis file may not be a valid file format.");
				throw new IOException(sb.toString());
			}

			taskMonitor.setProgress(1.0);

		} finally {
			reader = null;
		}
	}

	abstract public void run(TaskMonitor taskMonitor) throws Exception;

	/**
	 * Inform User of Network Stats.
	 */
	private void informUserOfTableStats(CyDataTable table) {
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Successfully loaded data table from:  ");
		sb.append(name);

		taskMonitor.setStatusMessage(sb.toString());
	}

	public void cancel() {
		if (reader != null) {
			reader.cancel();
		}
	}
}
