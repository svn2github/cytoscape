package cytoscape.task.sample;

import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.*;

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
        JFrame frame = createJFrame();

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

            //  For Case 2:  throw an exception when we get to XX.
            //  Used to illustrate exception handling / error display.
        } else {
            task = new SampleTask(100, 100, 10);
        }

        //  Configure the JTask UI Component
        JTaskConfig config = new JTaskConfig();
        if (option == 1) {
            config.setMillisToPopup(2000);
        }
        config.setOwner(frame);
        configureJTask(option, config);

        //  Execute Task via TaskManager Utility
        //  Automatically pops up a JTask UI Component for visually
        //  monitoring the task
        boolean success = TaskManager.executeTask(task, config);

        if (success) {
            System.out.println("Task completed successfully");
        } else {
            System.out.println
                    ("Task aborted due to user request or task error.");
        }
    }

    /**
     * Creates a Dummy JFrame, to illustrate modality of JTask.
     *
     * @return JFrame Object.
     */
    private static JFrame createJFrame() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel label = new JLabel("A Dummy Cytoscape Desktop");
        panel.add(label);
        frame.getContentPane().add(panel);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private static void configureJTask(int option, JTaskConfig config) {
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
                config.displayCancelButton(true);
                config.displayCloseButton(true);
                config.setAutoDispose(false);

                System.out.println("This demo illustrates a customized "
                        + "JTask PopUp.");
                System.out.println("-- All time fields are displayed.");
                System.out.println("-- Description Field is displayed.");
                System.out.println("-- Status Field is displayed.");
                System.out.println("-- Task can be cancelled");
                System.out.println("-- Dialog box popups up after delay");
                break;

                //  Case 2 displays user buttons
            case 2:
                config.displayCancelButton(true);
                config.displayCloseButton(true);
                System.out.println("This demo illustrates exception handling.");
                System.out.println("--  This task will end prematurely "
                        + "with an error.");
                System.out.println("--  This task can be canceled");
        }
    }
}
