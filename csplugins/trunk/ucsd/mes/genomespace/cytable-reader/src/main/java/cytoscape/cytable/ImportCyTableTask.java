
package cytoscape.cytable;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.data.servers.BioDataServer;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;

import java.io.File;


public class ImportCyTableTask implements Task {
	private TaskMonitor taskMonitor;
	private File[] files;
	private String type;

	public ImportCyTableTask(File[] files, String type) {
		this.files = files;
		this.type = type;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Reading CyTable");

			for (int i = 0; i < files.length; ++i) {
				taskMonitor.setPercentCompleted((100 * i) / files.length);

				if (type.equals("Node") )
					new CyTableReader(files[i].toURI().toURL(), Cytoscape.getNodeAttributes() ).read();
				else if (type.equals("Edge") )
					new CyTableReader(files[i].toURI().toURL(), Cytoscape.getEdgeAttributes() ).read();
				else
					throw new RuntimeException("Unknown table type: " + type);
			}

			taskMonitor.setPercentCompleted(100);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
			taskMonitor.setStatus("Done");
		} catch (Exception e) {
			taskMonitor.setException(e, e.getMessage());
		}
	}

	public void halt() {
		//   Task can not currently be halted.
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "Loading " + type + " Attributes CyTable";
	}

}
