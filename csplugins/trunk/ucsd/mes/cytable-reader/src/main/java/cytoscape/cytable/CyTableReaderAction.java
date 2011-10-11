package cytoscape.cytable;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;

import java.io.File;


public class CyTableReaderAction extends CytoscapeAction {

	private final String type;

	public CyTableReaderAction(String type) {
		super(type + " Attribute CyTable");
		setPreferredMenu("File.Import");
		this.type = type;
	}

    public void actionPerformed(ActionEvent e) {
    
        CyFileFilter nf = new CyFileFilter("cytable");

        File[] files = FileUtil.getFiles("Import " + type + " Attribute CyTable", FileUtil.LOAD,
                                         new CyFileFilter[] { nf });

        if (files != null) {
            ImportCyTableTask task = new ImportCyTableTask(files, type);
            
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.displayCloseButton(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(false);

            TaskManager.executeTask(task, jTaskConfig);
        }
    }
}	


