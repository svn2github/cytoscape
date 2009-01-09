// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;
import java.io.File;

/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of edge / edge attributes from the GUI
 */

public class LoadEdgeAttributesAction extends CytoscapeAction {

    /**
     * Constructor.
     */
    public LoadEdgeAttributesAction() {
        super("Edge Attributes...");
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
        File file = FileUtil.getFile("Load Edge Attributes",
                    FileUtil.LOAD, new CyFileFilter[]{nf});

        if (file != null) {

            //  Create Load Attributes Task
            LoadAttributesTask task = new LoadAttributesTask
                    (file,
                    LoadAttributesTask.EDGE_ATTRIBUTES);

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

