package org.cytoscape.task.internal.loaddatatable;


import java.io.File;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import java.util.Set;
import java.util.Iterator;


public class LoadAttributesFileTask extends AbstractLoadAttributesTask {
	@Tunable(description="Attribute Table file", params="fileCategory=table;input=true")
	public File file;

	public LoadAttributesFileTask(final CyTableReaderManager mgr, CyTableManager tableMgr) {
		super(mgr, tableMgr);
	}

	/**
	 * Executes Task.
	 */
	public void run(final TaskMonitor taskMonitor) throws Exception {
		loadTable(file.getName(), file.toURI(), taskMonitor);
	}
}

