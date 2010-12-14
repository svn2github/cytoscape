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


public class LoadAttributesTask extends AbstractTask {
	@Tunable(description="Atrtribute file", params="fileCategory=table;input=true")
	public File file;

	private CyTableReader reader;
	private final CyTableReaderManager mgr;
	private CyTableManager tableMgr;

	public LoadAttributesTask(final CyTableReaderManager mgr, CyTableManager tableMgr) {
		this.mgr = mgr;
		this.tableMgr = tableMgr;
	}

	/**
	 * Executes Task.
	 */
	public void run(final TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setStatusMessage("Finding Attribute Data Reader...");
		taskMonitor.setProgress(-1.0);
		reader = mgr.getReader(file.toURI());

		if (reader == null)
			throw new NullPointerException("Failed to find reader for specified file!");

		taskMonitor.setStatusMessage("Importing Data Table...");

		insertTasksAfterCurrentTask(reader, new FinalStatusMessageUpdateTask2(reader, tableMgr, file));
	}
}


class FinalStatusMessageUpdateTask2 extends AbstractTask {
	private final CyTableReader reader;
	private CyTableManager tableMgr;
	private File file;
	
	FinalStatusMessageUpdateTask2(final CyTableReader reader, CyTableManager tableMgr, File file) {
		this.reader = reader;
		this.tableMgr = tableMgr;
		this.file = file;
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		
		if (reader.getCyTables().length == 1){
			// If this is a global table, update its title
			String tableTitle = "ThisIsAGlobalTable";
			CyTable globalTable = getTableByTitle(tableMgr, tableTitle);
			if (globalTable != null){
				globalTable.setTitle(file.getName());
			}
		}
		
		for (CyTable table : reader.getCyTables())
			taskMonitor.setStatusMessage("Successfully loaded attribute table: " + table.getTitle());

		taskMonitor.setProgress(1.0);
	}
	

	private CyTable getTableByTitle(CyTableManager tableMgr, String tableTitle){
		CyTable retValue = null;

		Set<CyTable> tableSet = tableMgr.getAllTables(false);

		Iterator<CyTable> it = tableSet.iterator();
		while (it.hasNext()){
			CyTable tbl= it.next();
			if(tbl.getTitle().equalsIgnoreCase(tableTitle)){
				retValue = tbl;
				break;
			}
		}	

		return retValue;
	}
}
