package cytoscape.task.sample;

import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.ui.JTask;

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
        Task task = new SampleTask (100, 100);

        //  Configure JTask
        JTaskConfig config = new JTaskConfig();

        //  Show Cancel/Close Buttons
        config.displayUserButtons(true);

        //  Execute Task via TaskManager
        //  This automatically pops-open a JTask Dialog Box
        JTask jTask = TaskManager.executeTask(task, config);
    }
}