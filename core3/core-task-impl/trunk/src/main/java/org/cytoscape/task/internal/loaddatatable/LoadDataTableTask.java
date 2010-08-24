package org.cytoscape.task.internal.loaddatatable;

import static org.cytoscape.io.DataCategory.TABLE;

import java.io.File;
import java.util.Properties;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.io.read.CyDataTableReaderManager;
import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.task.AbstractTask;

public class LoadDataTableTask extends AbstractTask {

	@Tunable(description = "Data table file to load", flags = { Param.attributes })
	public File file;

	private CyDataTableReader reader;
	private CyDataTableReaderManager mgr;

	public LoadDataTableTask(CyDataTableReaderManager mgr) {
		this.mgr = mgr;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {

        taskMonitor.setStatusMessage("Finding Data Table Reader...");
        taskMonitor.setProgress(-1.0);
		reader = mgr.getReader(file.toURI());

		if ( reader == null )
			throw new NullPointerException("Failed to find reader for specified file!");
		
        taskMonitor.setStatusMessage("Importing Data Table...");

        reader.run(taskMonitor);

        for ( CyDataTable table : reader.getCyDataTables() ) 
            taskMonitor.setStatusMessage("Successfully loaded data table: " + table.getTitle());

        taskMonitor.setProgress(1.0);
	}

    public void cancel() {
        super.cancel();
        if (reader != null) {
            reader.cancel();
        }
    }
}
