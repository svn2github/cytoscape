// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.data.ExpressionData;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * User has requested loading of an Expression Matrix File.
 */
public class LoadExpressionMatrixAction extends CytoscapeAction {

    /**
     * Constructor.
     */
    public LoadExpressionMatrixAction() {
        super("Expression Matrix File...");
        setPreferredMenu("File.Load");
        setAcceleratorCombo(KeyEvent.VK_E, ActionEvent.CTRL_MASK);
    }

    /**
     * User Initiated Request.
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e) {

        CyFileFilter filter = new CyFileFilter();
        filter.addExtension("mrna");
        filter.addExtension("mRNA");
        filter.addExtension("pvals");
        filter.setDescription("Expression Matrix files");

        // get the file name
        final String name;
        try {
            name = FileUtil.getFile("Load Expression Matrix File",
                    FileUtil.LOAD,
                    new CyFileFilter[]{filter}).toString();
        } catch (Exception exp) {
            // this is because the selection was canceled
            return;
        }

        //  Create the LoadExpressionTask
        LoadExpressionDataTask task = new LoadExpressionDataTask(name);
        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayCloseButton(true);
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(false);

        //  Start Loading in a new Thread;  and pop-open JTask Dialog Box.
        TaskManager.executeTask(task, jTaskConfig);
    }
}

/**
 * Task to Load New Expression Data File.
 */
class LoadExpressionDataTask implements Task {
    private TaskMonitor taskMonitor;
    private String fileName;

    /**
     * Constructor.
     * @param fileName File name containing expression data.
     */
    public LoadExpressionDataTask(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Executes the Task.
     */
    public void run() {
        taskMonitor.setStatus("Analyzing Expression Data File...");
        try {
            //  Read in Expression Data File
            ExpressionData expressionData = new ExpressionData(fileName,
                    taskMonitor);
            Cytoscape.setExpressionData(expressionData);

            //  Copy Expression Data to Attributes
            taskMonitor.setStatus("Mapping Expression Data to "
                    + "Node Attributes...");
            expressionData.copyToAttribs(Cytoscape.getNodeAttributes(),
                    taskMonitor);
            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED,
                    null, null);
            Cytoscape.firePropertyChange(Cytoscape.EXPRESSION_DATA_LOADED,
                    null, expressionData);

            //  We are done;  inform user of expression data details.
            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus(expressionData.getDescription());
        } catch (Exception e) {
            taskMonitor.setException(e,
                    "Unable to load expression matrix file.");
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
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     * @return Task Title.
     */
    public String getTitle() {
        return new String("Loading Gene Expression Data");
    }
}
