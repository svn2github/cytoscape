package cytoscape.task.sample;

import cytoscape.task.Task;
import cytoscape.task.ui.JTask;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * Runs the Sample Task and demonstrate various UI options.
 */
public class RunSampleTask {

    /**
     * Main Method, used to testing purposes only.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        System.out.println("Running Task Demo");
        System.out.println("Press CTRL-C to end...");

        int option = 0;
        Task task = null;

        //  Get Command Line Option, e.g. 0..5
        if (args.length != 0) {
            option = Integer.parseInt(args[0]);
        }

        //  By default, create a sample task, count from 0..100
        if (option != 2) {
            task = new SampleTask(100, 100);

            //  For Case 2:  throw an exception when we get to 10.
            //  Used to illustrate exception handling / error display.
        } else {
            task = new SampleTask(100, 100, 10);
        }

        //  Configure the JTask UI Component
        JTaskConfig config = new JTaskConfig();

        switch (option) {
            //  Case 0 is the bare bones version.
            case 0:
                System.out.println("This demo illustrates a Bare Bones "
                        + "JTask PopUp.");
                System.out.println("--  Description and progress are "
                    + "displayed");
                System.out.println("--  Task cannot be cancelled.");
                break;

                //  Case 1 is the "bells and whistles" version.
            case 1:
                config.displayStatus(true);
                config.displayTimeElapsed(true);
                config.displayTimeRemaining(true);
                config.displayUserButtons(true);
                config.setAutoDispose(false);

                //  Wait 1 second before displaying UI component
                config.setMillisToDecideToPopup(1000);

                System.out.println("This demo illustrates a customized "
                        + "JTask PopUp.");
                System.out.println("-- JTask will wait 1 second before " +
                        "popping up.");
                System.out.println("-- All time fields are displayed.");
                System.out.println("-- Description Field is displayed.");
                System.out.println("-- Status Field is displayed.");
                System.out.println("-- Task can be cancelled");
                break;

                //  Case 2 displays user buttons
            case 2:
                config.displayUserButtons(true);
                System.out.println("This demo illustrates exception handling.");
                System.out.println("--  This task will end prematurely "
                        + "with an error.");
        }

        //  Execute Task via TaskManager Utility
        //  Automatically pops up a JTask UI Component for visually
        //  monitoring the task
        JTask jTask = TaskManager.executeTask(task, config);
    }
}