package cytoscape.task.util;

import cytoscape.task.Task;
import cytoscape.task.ui.JTask;
import cytoscape.task.ui.JTaskConfig;

import java.util.TimerTask;

/**
 * Utility class used to execute tasks and visually monitor their progress.
 */
public class TaskManager {

    /**
     * Executes the specified task in a new thread, and automatically
     * pops open a JTask UI Component for visually monitoring the task.
     * <P>
     * This method will block until the JTask UI Component is disposed.
     * Disposal will occur automatically if JTaskConfig is set to
     * setAutoDispose(true).  Otherwise, disposal will occur when the user
     * manually closes the JTask UI Dialog Box.  
     *
     * @param task   the task to execute.
     * @param config Configuration options for the JTask UI Component.
     * @return true value indicates that task completed successfully.
     *         false value indicates that task was halted by user or task
     *         encountered an error. 
     */
    public synchronized static boolean executeTask(Task task,
            JTaskConfig config) {

        //  Validate incoming task parameter.
        if (task == null) {
            throw new NullPointerException("Task is null");
        }

        //  If JTaskConfig is null, use the bare bones configuration options.
        if (config == null) {
            config = new JTaskConfig();
        }

        //  Instantiate a new JTask UI Component
        JTask jTask = new JTask(task, config);

        //  Tell task to report progress to JTask
        task.setTaskMonitor(jTask);

        //  Create a Task Wrapper
        TaskWrapper taskThread = new TaskWrapper(task, jTask);

        //  Start the Task
        taskThread.start();

        //  Show the JTask Dialog Box.
        Object popUpLock = new Object();
        startPopupTimer(config, popUpLock);

        //  Wait for notification of pop-up
        try {
            synchronized(popUpLock) {
                //  This locks the current thread until notification of pop-up.
                popUpLock.wait();
                if (taskThread.isAlive()) {
                    //  Thread will now block until modal dialog is
                    //  disposed automatically or by user.
                    jTask.setVisible(true);
                }
            }
        } catch (InterruptedException e) {
        }

        //  If all went well, return true.  Otherwise, return false.
        if (jTask.errorOccurred() == false
                && jTask.haltRequested() == false) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shows JTask Dialog Box after XX milliseconds of delay.
     * @param config    JTaskConfig Object.
     */
    private static void startPopupTimer(final JTaskConfig config,
            final Object popUpLock) {
        //  Create Timer in new thread.
        java.util.Timer timer = new java.util.Timer();
        TimerTask task = new TimerTask (){
            public void run() {
                synchronized(popUpLock) {
                    popUpLock.notify();
                }
            }
        };
        timer.schedule(task, config.getMillisToPopup());
    }
}

/**
 * Used to Wrap an Existing Task.
 */
class TaskWrapper extends Thread {
    private Task task;
    private JTask jTask;

    /**
     * Constructor.
     *
     * @param task  Task Object.
     * @param jTask JTask Object.
     */
    TaskWrapper(Task task, JTask jTask) {
        this.task = task;
        this.jTask = jTask;
    }


    /**
     * Executes the Task.
     */
    public void run() {
        try {
            //  Run the actual task
            task.run();
        } finally {
            //  Inform the UI Component that the task is now done
            jTask.setDone();
        }
    }
}
