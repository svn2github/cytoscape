package cytoscape.task.sample;

import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * Illustrates how to execute a sample task via the TaskManager.
 */
public class RunBareBones {

    /**
     * Executes the Sample Task.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {

        //  Create a Sample Task
        Task task = new SampleTask(100, 100);

        //  Configure JTask
        JTaskConfig config = new JTaskConfig();

        //  Show Cancel/Close Buttons
        config.displayCancelButton(true);

        //  Execute Task via TaskManager
        //  This automatically pops-open a JTask Dialog Box.
        //  This method will block until the JTask Dialog Box is disposed.
        boolean success = TaskManager.executeTask(task, config);
    }
}