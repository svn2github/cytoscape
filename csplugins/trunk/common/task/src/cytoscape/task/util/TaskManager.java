package cytoscape.task.util;

import cytoscape.task.Task;
import cytoscape.task.ui.JTask;
import cytoscape.task.ui.JTaskConfig;

/**
 * Utility class used to execute tasks and visually monitor their progress.
 */
public class TaskManager {

    /**
     * Executes the specified task in a new thread, and automatically
     * pops open a JTask UI Component for visually monitoring the task.
     *
     * @param task   the task to execute.
     * @param config Configuration options for the JTask UI Component.
     * @return JTask UI Component, can be null.
     */
    public static JTask executeTask(Task task, JTaskConfig config) {

        //  Validate incoming task parameter.
        if (task == null) {
            throw new NullPointerException("Task is null");
        }

        //  If JTaskConfig is null, use the bare bones configuration options.
        if (config == null) {
            config = new JTaskConfig();
        }

        //  Instantiate a new JTask UI Component
        JTask jTask = new JTask(task.getTitle(), config);

        //  Tell task to report progress to JTask
        task.setTaskMonitor(jTask);

        //  Create a Task Wrapper
        TaskWrapper taskThread = new TaskWrapper(task, jTask);

        //  Start the Task
        taskThread.start();

        //  Return a JTask UI Component
        return jTask;
    }
}

/**
 * Used to Wrap an Existing Task.
 */
class TaskWrapper extends Thread {
    private Task task;
    private JTask jTask;
    private final Object lock = new Object();
    private boolean ran = false;
    private boolean running = false;
    private boolean stop = false;

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
     * This method is guaranteed to run at most once - that is, it will call
     * the underlying <code>Task.run()</code> at most once.
     * If this method is invoked a second time, it will throw an
     * <code>IllegalStateException</code>.
     */
    public void run() {
        synchronized (lock) {
            if (ran) {
                throw new IllegalStateException
                        ("Task already running or ran");
            }
            ran = true;
        }

        // Guaranteed to get to this line of code at most once.
        synchronized (lock) {
            if (stop) {
                return;
            }
            running = true;
        }
        try {
            //  Run the actual task
            task.run();
        } finally {

            //  Inform the UI Component that the task is now done
            jTask.setDone();

            synchronized (lock) {
                running = false;
                lock.notifyAll();
            }
        }
    }
}