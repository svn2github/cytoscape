package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.foo.GraphConverter;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.graph.layout.impl.SpringEmbeddedLayouter2;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Menu Options for Running the SpringEmbedded Layout.
 */
public class SpringEmbeddedLayoutMenu extends JMenu {
    private final JMenuItem m_wholeGraph;
    private final JMenuItem m_selectedNodesOnly;

    /**
     * Constructor.
     */
    public SpringEmbeddedLayoutMenu() {
        super("Apply Spring Embedded Layout");
        m_wholeGraph = new JMenuItem(new SpringEmbeddedLayoutAction(false));
        add(m_wholeGraph);
        m_selectedNodesOnly = new JMenuItem
                (new SpringEmbeddedLayoutAction(true));
        add(m_selectedNodesOnly);
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
                if (graphView.getSelectedNodeIndices().length == 0)
                    m_selectedNodesOnly.setEnabled(false);
                else
                    m_selectedNodesOnly.setEnabled(true);
            }
        });
    }

    /**
    * User has Requested that the Spring Embedded Layout Algorithm be executed
    * on the current graph.
    */
    private static class SpringEmbeddedLayoutAction extends AbstractAction {

        private final boolean m_selectedNodesOnly;

        /**
         * Constructor.
         * @param selectedNodesOnly Flag for Selected Nodes Only.
         */
        public SpringEmbeddedLayoutAction(boolean selectedNodesOnly) {
            super((selectedNodesOnly ? "Selected Nodes Only" :
                    "All Nodes"));
            m_selectedNodesOnly = selectedNodesOnly;
        }

        /**
         * User-Initiated Action:  Do Layout.
         *
         * @param e ActionEvent Object.
         */
        public void actionPerformed(ActionEvent e) {
            // Convert to NativeGraph Intermediate Data Structure
            MutablePolyEdgeGraphLayout nativeGraph =
                    GraphConverter.getGraphCopy
                    (0.0d, false, m_selectedNodesOnly);

            //  Instantiate Layout Task
            LayoutTaskWrapper task = new LayoutTaskWrapper(nativeGraph);

            //  Configure JTask Dialog Pop-Up Box
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.setMillisToDecideToPopup(200);
            jTaskConfig.displayCancelButton(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(true);

            //  Execute Task in New Thread;  pop open JTask Dialog Box.
            TaskManager.executeTask(task, jTaskConfig);
        }
    }
}

/**
 * Wrapper for Running the Spring Embedded Layout Algorithm.
 */
class LayoutTaskWrapper implements Task {
    private TaskMonitor taskMonitor;
    private SpringEmbeddedLayouter2 childTask;
    private MutablePolyEdgeGraphLayout nativeGraph;

    /**
     * Constructor.
     * @param nativeGraph MutablePolyEdgeGraphLayout Object.
     */
    LayoutTaskWrapper(MutablePolyEdgeGraphLayout nativeGraph) {
        this.nativeGraph = nativeGraph;
        childTask = new SpringEmbeddedLayouter2(nativeGraph);
    }

    /**
     * Executes Layout.
     * This is a two-part process:
     * First, run layout off-screen using internal data structures;
     * Second, update the screen.
     */
    public void run() {
        //  Run Layout on Internal Data Structures
        childTask.setTaskMonitor(taskMonitor);
        childTask.run();

        //  Then, update the screen
        taskMonitor.setStatus("Displaying graph...");
        taskMonitor.setPercentCompleted(-1);
        GraphConverter.updateCytoscapeLayout(nativeGraph);
        taskMonitor.setPercentCompleted(100);
    }

    /**
     * Halts Task.
     */
    public void halt() {
        childTask.halt();
    }

    /**
     * Sets Task Monitor.
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets Task Title.
     * @return Task Title.
     */
    public String getTitle() {
        return childTask.getTitle();
    }
}