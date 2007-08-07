// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.data.CyAttributes;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.CyNetwork;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * write out the current graph to the specified file, using the standard
 * interactions format:  nodeA edgeType nodeB.
 * for example: <code>
 * <p/>
 * YMR056C pp YLL013C
 * YCR107W pp YBR265W
 * <p/>
 * </code>
 */
public class SaveAsInteractionsAction extends CytoscapeAction {

    /**
     * Constructor.
     */
    public SaveAsInteractionsAction() {
        super("Network as Interactions...");
        setPreferredMenu("File.Save");
    }
    // MLC 09/19/05 BEGIN:
    /**
     * User-initiated action to save the current network in SIF format
     * to a user-specified file.  If successfully saved, fires a
     * PropertyChange event with property=Cytoscape.NETWORK_SAVED,
     * old_value=null, and new_value=a three element Object array containing:
     * <OL>
     * <LI>first element = CyNetwork saved
     * <LI>second element = URI of the location where saved
     * <LI>third element = an Integer representing the format in which the
     * Network was saved (e.g., Cytoscape.FILE_SIF).
     * </OL>
     * @param e ActionEvent Object.
     */
    // MLC 09/19/05 END.
    public void actionPerformed(ActionEvent e) {

        // get the file name
        File file = FileUtil.getFile("Save Network as Interactions",
                    FileUtil.SAVE, new CyFileFilter[]{});

        if (file != null) {
            String fileName = file.getAbsolutePath();
            if (!fileName.endsWith(".sif"))
                fileName = fileName + ".sif";

            //  Create LoadNetwork Task
            SaveAsSifTask task = new SaveAsSifTask(fileName);

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
 * Task to Save Graph Data to SIF Format.
 */
class SaveAsSifTask implements Task {
    private String fileName;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     * @param fileName          Filename to save to
     */
    SaveAsSifTask (String fileName) {
        this.fileName = fileName;
    }

    /**
     * Executes the Task.
     */
    public void run() {
        taskMonitor.setStatus("Saving Interactions...");
        try {
	    if (Cytoscape.getCurrentNetwork().getNodeCount() == 0) {
                throw new IllegalArgumentException ("Network is empty.");
            }
            saveInteractions();
            taskMonitor.setPercentCompleted (100);
            taskMonitor.setStatus("Network successfully saved to:  "
                    + fileName);
        } catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Network is Empty.  Cannot be saved.");
        } catch (IOException e) {
            taskMonitor.setException(e, "Unable to save network.");
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
        return new String ("Saving Network");
    }

    /**
     * Saves Interactions to File.
     * @throws IOException Error Writing to File.
     */
    private void saveInteractions() throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        String lineSep = System.getProperty("line.separator");
        CyNetwork network = Cytoscape.getCurrentNetwork();
        List nodeList = network.nodesList();

        if (nodeList.size() == 0) {
            throw new IOException ("Network is empty.");
        }

        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
        giny.model.Node[] nodes = 
        	(giny.model.Node[]) nodeList.toArray(new giny.model.Node[0]);
        
        for (int i = 0; i < nodes.length; i++) {

            //  Report on Progress
            double percent = ((double) i / nodes.length) * 100.0;
            taskMonitor.setPercentCompleted((int) percent);

            StringBuffer sb = new StringBuffer();
            giny.model.Node node = nodes[i];
            String canonicalName = node.getIdentifier();
            List edges = network.getAdjacentEdgesList(node, true, true, true);

            if (edges.size() == 0) {
                sb.append(canonicalName + lineSep);
            } else {
                Iterator it = edges.iterator();
                while (it.hasNext()) {
                    giny.model.Edge edge = (giny.model.Edge) it.next();
                    if (node == edge.getSource()) { //do only for outgoing edges
                        giny.model.Node target = edge.getTarget();
                        
                        String canonicalTargetName = target.getIdentifier();
                        
                        String edgeName = edge.getIdentifier();
                        
                        String interactionName =
                                edgeAtts.getStringAttribute(edge.getIdentifier(),Semantics.INTERACTION);
                        
                        if (interactionName == null) {
                            interactionName = "xx";
                        }
                        sb.append(canonicalName);
                        sb.append("\t");
                        sb.append(interactionName);
                        sb.append("\t");
                        sb.append(canonicalTargetName);
                        sb.append(lineSep);
                    }
                } // while
            } // else: this node has edges, write out one line for every
            // out edge (if any)
            fileWriter.write(sb.toString());
            //System.out.println(" WRITE: "+ sb.toString() );
        }  // for i
        fileWriter.close();
	// MLC: 09/19/05 BEGIN:
	//        // AJK: 09/14/05 BEGIN
	//        Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, network);
	//		// AJK: 09/14/05 END
	Object[] ret_val = new Object[3];
	ret_val[0] = network;
	ret_val[1] = new File (fileName).toURI();
	ret_val[2] = new Integer (Cytoscape.FILE_SIF);
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
	// MLC: 09/19/05 END.
    }
}
