package csplugins.test.quickfind.test;

import cytoscape.task.TaskMonitor;

/**
 * Task Monitor Stub.
 *
 * @author Ethan Cerami
 */
public class TaskMonitorBase implements TaskMonitor {
    private String status;
    private int percentComplete;

    /**
     * Sets Percent Completed.
     *
     * @param percentComplete Percent Completed.
     * @throws IllegalThreadStateException Illegal Thread State.
     * @throws IllegalArgumentException    Illegal Argument.
     */
    public void setPercentCompleted(int percentComplete)
            throws IllegalThreadStateException,
            IllegalArgumentException {
        this.percentComplete = percentComplete;
    }

    /**
     * Sets estimated time remaining:  no-op.
     *
     * @param l time remaining.
     * @throws IllegalThreadStateException Illegal Thread State.
     */
    public void setEstimatedTimeRemaining(long l)
            throws IllegalThreadStateException {
    }

    /**
     * Sets Exception:  no-op.
     *
     * @param throwable Throwable Object.
     * @param string    Human readable error message.
     * @throws IllegalThreadStateException Illegal Thread State.
     */
    public void setException(Throwable throwable, String string)
            throws IllegalThreadStateException {
    }

    /**
     * Sets Status:  no-op.
     *
     * @param status Status Message.
     * @throws IllegalThreadStateException Illegal Thread State.
     * @throws NullPointerException        NullPointer Error.
     */
    public void setStatus(String status)
            throws IllegalThreadStateException, NullPointerException {
        this.status = status;
    }

    /**
     * Gets Status.
     *
     * @return Status Message.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets Percent Complete.
     *
     * @return percent complete.
     */
    public int getPercentComplete() {
        return percentComplete;
    }
}
