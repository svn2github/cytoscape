package org.cytoscape.task.internal.loaddatatable;


import java.net.URL;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import java.util.Set;
import java.util.Iterator;


public class LoadAttributesURLTask extends AbstractLoadAttributesTask {
	@Tunable(description="Attribute Table URL", params="fileCategory=table;input=true")
	public URL url;

	public LoadAttributesURLTask(final CyTableReaderManager mgr, CyTableManager tableMgr) {
		super(mgr, tableMgr);
	}

	/**
	 * Executes Task.
	 */
	public void run(final TaskMonitor taskMonitor) throws Exception {
		loadTable(url.toString(), url.toURI(), taskMonitor);
	}
}

