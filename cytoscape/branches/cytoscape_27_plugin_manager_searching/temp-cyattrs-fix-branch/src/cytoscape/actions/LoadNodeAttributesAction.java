// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.servers.BioDataServer;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;
import java.io.File;

/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of node / edge attributes from the GUI
 */

public class LoadNodeAttributesAction extends CytoscapeAction {

    /**
     * Constructor.
     */
    public LoadNodeAttributesAction() {
        super("Node Attributes...");
        setPreferredMenu("File.Load");
    }

    /**
     * User Initiated Request.
     *
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e) {

        //  Use a Default CyFileFilter:  enables user to select any file type.
        CyFileFilter nf = new CyFileFilter();

        // get the file name
        File file = FileUtil.getFile("Load Node Attributes",
                    FileUtil.LOAD, new CyFileFilter[]{nf});

        if (file != null) {

            //  Create Load Attributes Task
            LoadAttributesTask task = 
            	new LoadAttributesTask (file, LoadAttributesTask.NODE_ATTRIBUTES);

            //  Configure JTask Dialog Pop-Up Box
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.displayCloseButton(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(false);

            //  Execute Task in New Thread;  pop open JTask Dialog Box.
            TaskManager.executeTask(task, jTaskConfig);

        }
    }
}

/**
 * Task to Load New Node/Edge Attributes Data.
 */
class LoadAttributesTask implements Task {
    private TaskMonitor taskMonitor;
    private File file;
    private int type;
    static final int NODE_ATTRIBUTES = 0;
    static final int EDGE_ATTRIBUTES = 1;

    /**
     * Constructor.
     * @param file File Object.
     * @param type NODE_ATTRIBUTES or EDGE_ATTRIBUTES
     */
    LoadAttributesTask (File file, int type) {
        this.file = file;
        this.type = type;
    }

    /**
     * Executes Task.
     */
    public void run() {
        try {
            //  Get Defaults.
            BioDataServer bioDataServer = Cytoscape.getBioDataServer();
            String speciesName = CytoscapeInit.getDefaultSpeciesName();
            boolean canonicalize = !CytoscapeInit.noCanonicalization();

            //  Read in Data
            
            // track progress. CyAttributes has separation between
            // reading attributes and storing them
            // so we need to find a different way of monitoring this task:
            // attributes.setTaskMonitor(taskMonitor);
          
            if ( type == NODE_ATTRIBUTES ) 
                Cytoscape.loadAttributes( new String[] { file.getAbsolutePath() },
                                          new String[] {},
                                          canonicalize,
                                          bioDataServer,
                                          speciesName );
            else if ( type == EDGE_ATTRIBUTES ) 
                Cytoscape.loadAttributes( new String[] {},
	                                      new String[] { file.getAbsolutePath() },
                                          canonicalize,
                                          bioDataServer,
                                          speciesName );
            else
                throw new Exception("Unknown attribute type: " + Integer.toString(type) );

            //  Inform others via property change event.
            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null );

        } catch (Exception e) {
            taskMonitor.setException(e, e.getMessage());
        }
    }

    /**
     * Halts the Task:  Not Currently Implemented.
     */
    public void halt() {
        //   Task can not currently be halted.
    }

    /**
     * Sets the Task Monitor Object.
     * @param taskMonitor
     * @throws IllegalThreadStateException
     */
    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return Task Title.
     */
    public String getTitle() {
        if (type == NODE_ATTRIBUTES) {
            return new String ("Loading Node Attributes");
        } else {
            return new String ("Loading Edge Attributes");
        }
    }
}
