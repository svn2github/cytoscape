// $Revision$
// $Date$
// $Author$

// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.GMLParser;
import cytoscape.data.readers.GMLReader2;
import cytoscape.data.readers.GMLWriter;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Vector;

public class SaveAsGMLAction extends CytoscapeAction {

    public SaveAsGMLAction() {
        super("Graph as GML...");
        setPreferredMenu("File.Save");
    }

    public SaveAsGMLAction(boolean label) {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        String name;
        try {
            name = FileUtil.getFile("Save Graph as GML",
                    FileUtil.SAVE,
                    new CyFileFilter[]{}).toString();
        } catch (Exception exp) {
            // this is because the selection was canceled
            return;
        }

        if (!name.endsWith(".gml"))
            name = name + ".gml";

        //  Get Current Network and View
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyNetworkView view = Cytoscape.getNetworkView
                (network.getIdentifier());

        //  Create Task
        SaveAsGMLTask task = new SaveAsGMLTask(name, network, view);

        //  Configure JTask Dialog Pop-Up Box
        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayCloseButton(true);
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(false);

        //  Execute Task in New Thread;  pop open JTask Dialog Box.
        TaskManager.executeTask(task, jTaskConfig);
    }
} // SaveAsGMLAction

/**
 * Task to Save Graph Data to GML Format.
 */
class SaveAsGMLTask implements Task {
    private String fileName;
    private CyNetwork network;
    private CyNetworkView view;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     *
     * @param network Network Object.
     * @param view    Network View Object.
     */
    SaveAsGMLTask(String fileName, CyNetwork network, CyNetworkView view) {
        this.fileName = fileName;
        this.network = network;
        this.view = view;
    }

    /**
     * Executes Task
     */
    public void run() {
        taskMonitor.setStatus("Saving Graph...");
        taskMonitor.setPercentCompleted(-1);
        try {
            List nodeList = network.nodesList();
            if (nodeList.size() == 0) {
                throw new IllegalArgumentException("Graph is empty.");
            }
            saveGraph();
            taskMonitor.setPercentCompleted (100);
            taskMonitor.setStatus("Graph successfully saved to:  "
                    + fileName);
        } catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Graph is Empty.  Cannot be saved.");
        } catch (IOException e) {
            taskMonitor.setException(e, "Unable to save graph.");
        }
    }

    /**
     * Halts the Task:  Not Currently Implemented.
     */
    public void halt() {
        //   Task can not currently be halted.
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
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
        return new String("Saving Graph");
    }

    /**
     * Saves Graph to File.
     *
     * @throws IOException Error Writing to File.
     */
    private void saveGraph() throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        List list = null;
        GMLReader2 reader = (GMLReader2) network.getClientData("GML");
        if (reader != null) {
            list = reader.getList();
        } else {
            list = new Vector();
        }
        GMLWriter gmlWriter = new GMLWriter();
        gmlWriter.writeGML(network, view, list);
        GMLParser.printList(list, fileWriter);
        fileWriter.close();
    }
}