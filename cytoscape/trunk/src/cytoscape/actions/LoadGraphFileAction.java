// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.readers.GMLReader2;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.servers.BioDataServer;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.NetworkViewManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * User has requested loading of an Expression Matrix File.
 * Could be SIF File or GML File.
 */
public class LoadGraphFileAction extends CytoscapeAction {
    protected CyMenus windowMenu;

    /**
     * Constructor.
     *
     * @param windowMenu WindowMenu Object.
     */
    public LoadGraphFileAction(CyMenus windowMenu) {
        super("Graph...");
        setPreferredMenu("File.Load");
        setAcceleratorCombo(java.awt.event.KeyEvent.VK_L,
                ActionEvent.CTRL_MASK);
        this.windowMenu = windowMenu;
    }

    /**
     * Constructor.
     *
     * @param windowMenu WindowMenu Object.
     * @param label      boolean label.
     */
    public LoadGraphFileAction(CyMenus windowMenu, boolean label) {
        super();
        this.windowMenu = windowMenu;
    }

    /**
     * User Initiated Request.
     *
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e) {

        // Create FileFilters
        CyFileFilter intFilter = new CyFileFilter();
        CyFileFilter gmlFilter = new CyFileFilter();
        CyFileFilter graphFilter = new CyFileFilter();

        // Add accepted File Extensions
        gmlFilter.addExtension("gml");
        gmlFilter.setDescription("GML files");
        intFilter.addExtension("sif");
        intFilter.setDescription("Interaction files");
        graphFilter.addExtension("sif");
        graphFilter.addExtension("gml");
        graphFilter.setDescription("All graph files");

        // Get the file name
        File  file = FileUtil.getFile("Load Graph File",
            FileUtil.LOAD, new CyFileFilter[]
            {graphFilter, intFilter, gmlFilter});

        // if the name is not null, then load
        if (file != null) {
            int fileType = Cytoscape.FILE_SIF;

            //  long enough to have a "gml" extension
            if (file.getName().length() > 4) {
                String name = file.getName();
                String extension = name.substring(name.length() - 3);
                if (extension.equalsIgnoreCase("gml"))
                    fileType = Cytoscape.FILE_GML;
            }

            //  Create LoadNetwork Task
            LoadNetworkTask task = new LoadNetworkTask(file, fileType);

            //  Configure JTask Dialog Pop-Up Box
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.displayCloseButton(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(false);

            //  Execute Task in New Thread;  pops open JTask Dialog Box.
            boolean success = TaskManager.executeTask(task, jTaskConfig);
        }
    }
}

/**
 * Task to Load New Network Data.
 */
class LoadNetworkTask implements Task {
    private File file;
    private int fileType;
    private CyNetwork cyNetwork;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     *
     * @param file         File.
     * @param fileType     FileType, e.g. Cytoscape.FILE_SIF or
     *                     Cytoscape.FILE_GML.
     */
    public LoadNetworkTask(File file, int fileType) {
        this.file = file;
        this.fileType = fileType;
    }

    /**
     * Executes Task.
     */
    public void run() {
        taskMonitor.setStatus("Reading in Graph Data...");

        try {
            cyNetwork = this.createNetwork(file.getAbsolutePath(),
                    fileType, Cytoscape.getBioDataServer(),
                    CytoscapeInit.getDefaultSpeciesName());

            if (cyNetwork != null) {
                informUserOfGraphStats(cyNetwork);
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append("Could not read graph from file: " + file.getName());
                sb.append("\nThis file may not be a valid GML or SIF file.");
                taskMonitor.setException(new IOException(sb.toString()),
                        sb.toString());
            }
            taskMonitor.setPercentCompleted(100);
        } catch (IOException e) {
            taskMonitor.setException(e, "Unable to load graph file.");
        }
    }

    /**
     * Inform User of Graph Stats.
     */
    private void informUserOfGraphStats(CyNetwork newNetwork) {
        NumberFormat formatter = new DecimalFormat("#,###,###");
        StringBuffer sb = new StringBuffer();

        //  Give the user some confirmation
        sb.append("Succesfully loaded graph from:  " + file.getName());
        sb.append("\n\nGraph contains " + formatter.format
                (newNetwork.getNodeCount()));
        sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
        sb.append(" edges.\n\n");

        if (newNetwork.getNodeCount() < CytoscapeInit.getViewThreshold()) {
            sb.append("Graph is under "
                    + CytoscapeInit.getViewThreshold()
                    + " nodes.  A view will be automatically created.");
        } else {
            sb.append("Graph is over "
                    + CytoscapeInit.getViewThreshold()
                    + " nodes.  A view will not been created."
                    + "  If you wish to view this graph, use "
                    + "\"Create View\" from the \"Edit\" menu.");
        }
        taskMonitor.setStatus(sb.toString());
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
        return new String("Loading Graph");
    }

    /**
     * Creates a cytoscape.data.CyNetwork from a file.
     * The passed variable determines the
     * type of file, i.e. GML, SIF, etc.<p>
     * This operation may take a long time to complete.
     *
     * @param location      the location of the file
     * @param file_type     the type of file GML, SIF, SBML, etc.
     * @param biodataserver provides the name conversion service
     * @param species       the species used by the BioDataServer
     */
    private CyNetwork createNetwork(String location, int file_type,
            BioDataServer biodataserver, String species) throws IOException {

        GraphReader reader;
        taskMonitor.setStatus("Analyzing Graph Data File...");
        taskMonitor.setPercentCompleted(5);

        //  Set the reader according to what file type was passed.
        if (file_type == Cytoscape.FILE_SIF) {
            reader = new InteractionsReader(biodataserver, species, location,
                    taskMonitor);
        } else if (file_type == Cytoscape.FILE_GML) {
            reader = new GMLReader2(location, taskMonitor);
        } else {
            throw new IOException("File Type not Supported.");
        }

        // Have the GraphReader read the given file
        reader.read();

        //  Get the RootGraph indices of the nodes and
        //  Edges that were just created
        final int[] nodes = reader.getNodeIndicesArray();
        final int[] edges = reader.getEdgeIndicesArray();

        File file = new File (location);
        final String title = file.getName();

        // Create a new cytoscape.data.CyNetwork from these nodes and edges
        taskMonitor.setStatus("Creating Cytoscape Network...");
        taskMonitor.setPercentCompleted(-1);
        final CyNetwork network[] = new CyNetwork[1];

        //  This call will trigger the creation of the CyNetworkView
        network[0] = Cytoscape.createNetwork(nodes, edges,
            CyNetworkNaming.getSuggestedNetworkTitle (title));

        //  Store GML Data as a Network Attribute
        if (file_type == Cytoscape.FILE_GML) {
            network[0].putClientData("GML", reader);
        }

        //  Layout Network
        if (Cytoscape.getNetworkView(network[0].getIdentifier()) != null) {
            reader.layout(Cytoscape.getNetworkView
                    (network[0].getIdentifier()));
        }
        return network[0];
    }
}