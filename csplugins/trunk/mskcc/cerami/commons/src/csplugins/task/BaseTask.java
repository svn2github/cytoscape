/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.task;

import java.util.Date;

/**
 * Base Implementation of the Task Interface.
 *
 * @author Ethan Cerami
 */
public abstract class BaseTask extends Thread implements Task {
    /**
     * Task is Complete.
     * By default, we assume task is not yet done.
     */
    private boolean taskIsDone = false;

    /**
     * Indicates if the task is currently indeterminate.
     * By default, we assume the task is indeterminate.
     */
    private boolean indeterminate = true;

    /**
     * Amount of Progress the Task has Already Completed.
     * By default, we assume 0 progress.
     */
    private int progressValue = 0;

    /**
     * Amount of Progress until the Task is Complete.
     * By default, we assume Integer.MAX_VALUE.
     */
    private int maxProgressValue = Integer.MAX_VALUE;

    /**
     * Current Progress Message.
     */
    private String progressMessage = "Starting...";

    /**
     * Human Readable Error Message.
     */
    private String humanErrorMessage;

    /**
     * Estimated Time Remaining until Task Completes.
     * By default, we have no idea how much time is remaining.
     * This is indicated by using a negative value.
     */
    private long estimatedTimeRemaining = -1;

    /**
     * Exception Generated within Task Thread.
     */
    private Throwable internalException;

    /**
     * Interrupt Flag.
     * By default, we assume interruptFlag is false.
     */
    private boolean interruptFlag = false;

    /**
     * Error Flag.
     * By default, we assume errorFlag is false.
     */
    private boolean errorFlag = false;

    /**
     * Human Readable Task Description.
     */
    private String taskTitle;

    /**
     * Time Task Started.
     */
    private Date startTime;

    /**
     * Time Task Ended.
     */
    private Date stopTime;

    /**
     * Constructor.
     *
     * @param taskTitle Human Readable Title of Task.
     */
    public BaseTask(String taskTitle) {
        this.taskTitle = taskTitle;
        this.startTime = new Date();
    }

    /**
     * Gets Human Readable Title of Task.
     * Used by UI Components.
     *
     * @return Human Readable Title of Task.
     */
    public String getTaskTitle() {
        return this.taskTitle;
    }

    /**
     * Indicates that the progress of task is currently indeterminate.
     * The task should be set to indeterminate only if if it has no idea
     * how much time / progress is remaining.
     *
     * @return true of false.
     */
    public boolean isIndeterminate() {
        return indeterminate;
    }

    /**
     * Determines if the task is Done.
     *
     * @return true of false.
     */
    public boolean isDone() {
        return taskIsDone;
    }

    /**
     * Gets current value of progress.
     * For example, if maxProgressValue is 10, and progressValue is 5,
     * the task is 50% complete.
     *
     * @return integer value.
     */
    public int getProgressValue() {
        return progressValue;
    }

    /**
     * Gets maximum value of progress.
     * For example, if maxProgressValue is 10, and progressValue is 5,
     * the task is 50% complete.
     *
     * @return integer value.
     */
    public int getMaxProgressValue() {
        return maxProgressValue;
    }

    /**
     * Gets the current progress message.
     * This is a message to be be displayed to the end user.
     * For example, a long-running task that parses XML data might have a
     * progress message, such as, "Validating XML document against XML Schema.",
     * followed by a second message, such as "Validation Passed."
     *
     * @return progress message string.
     */
    public String getProgressMessage() {
        return progressMessage;
    }

    /**
     * Gets estimated amount of time remaining (in milli-seconds).
     * This value can be displayed to the end-user, but note that the value
     * is only an esimate.  A negative return value indicates that the
     * task has no idea how much time is remaining.
     *
     * @return time remaining (in milli-seconds).
     */
    public long getEstimatedTimeRemaining() {
        return estimatedTimeRemaining;
    }

    /**
     * Gets Approximate Time Elapsed Since Task Started.
     * If task has already completed, getTimeElapsed returns the total
     * execution time for the task.
     *
     * @return time (in milli-seconds).
     */
    public long getTimeElapsed() {
        if (stopTime != null) {
            return stopTime.getTime() - startTime.getTime();
        } else {
            Date currentTime = new Date();
            return currentTime.getTime() - startTime.getTime();
        }
    }

    /**
     * Determines if an error has occurred within the task.
     *
     * @return true or false.
     */
    public boolean errorOccurred() {
        return this.errorFlag;
    }

    /**
     * Gets human readable error message, for display to the end-user.
     *
     * @return human readable error message.
     */
    public String getHumanReadableErrorMessage() {
        return humanErrorMessage;
    }

    /**
     * Gets Exception / Error (if any have occurred).
     * Since the task is running in a separate thread, we need a convenient
     * way to capture exceptions.  Any exception thrown by the task is available
     * via this method.
     *
     * @return Exception object.
     */
    public Throwable getInternalException() {
        return internalException;
    }

    /**
     * Interrupts task.
     */
    public void interrupt() {
        this.interruptFlag = true;
        super.interrupt();
    }

    /**
     * Determines if task has been interrupted.
     *
     * @return interrupt Flag.
     */
    public boolean isInterrupted() {
        return interruptFlag;
    }

    /**
     * Sets the Done Flag to True.
     * This method is protected, and may only be called by the task itself.
     */
    protected void setDone() {
        this.taskIsDone = true;
    }

    /**
     * Sets the indeterminate flag.
     * Set this to true if you have no idea how long the task will take
     * to complete.
     * This method is protected, and may only be called by the task itself.
     *
     * @param indeterminate Indeterminate Flag.
     */
    protected void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    /**
     * Sets current value of progress.
     * For example, if maxProgressValue is 10, and progressValue is 5,
     * the task is 50% complete.
     * This method is protected, and may only be called by the task itself.
     *
     * @param progressValue Current Progress Value.
     */
    protected void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
        this.setIndeterminate(false);
    }

    /**
     * Sets maxium value of progress.
     * For example, if maxProgressValue is 10, and progressValue is 5,
     * the task is 50% complete.
     * This method is protected, and may only be called by the task itself.
     *
     * @param maxProgressValue Maximum Progress Value.
     */
    protected void setMaxProgressValue(int maxProgressValue) {
        this.maxProgressValue = maxProgressValue;
        this.setIndeterminate(false);
    }

    /**
     * Sets the current progress message.
     * This is a message to be be displayed to the end user.
     * For example, a long-running task that parses XML data might have a
     * progress message, such as, "Validating XML document against XML Schema.",
     * followed by a second message, such as "Validation Passed."
     * This method is protected, and may only be called by the task itself.
     *
     * @param progressMessage Progress Message String.
     */
    protected void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    /**
     * Sets the Title of the Task.
     *
     * @param taskTitle Human Readable Task Title.
     */
    protected void setTaskTitle (String taskTitle) {
        this.taskTitle = taskTitle;
    }

    /**
     * Sets estimated amount of time remaining (in milli-seconds).
     * This value can be displayed to the end-user, but note that the value
     * is only an esimate.
     * This method is protected, and may only be called by the task itself.
     *
     * @param estimatedTimeRemaining Estimated Time Remaining.
     */
    protected void setEstimatedTimeRemaining(long estimatedTimeRemaining) {
        this.estimatedTimeRemaining = estimatedTimeRemaining;
    }

    /**
     * Sets the error flag.
     * This method is protected, and may only be called by the task itself.
     *
     * @param flag Error Flag.
     */
    protected void setErrorOccured(boolean flag) {
        this.errorFlag = flag;
    }

    /**
     * Sets a human readable error message.
     *
     * @param errorMsg Human Readable Error Message.
     */
    protected void setHumanReadableErrorMessage(String errorMsg) {
        this.humanErrorMessage = errorMsg;
    }

    /**
     * Sets Exception / Error (if any have occurred).
     * Since the task is running in a separate thread, we need a convenient
     * way to capture exceptions.  Any exception thrown by the task is
     * available via this method.
     * This method is protected, and may only be called by the task itself.
     *
     * @param internalException Throwable Object.
     */
    protected void setException(Throwable internalException) {
        this.internalException = internalException;
    }

    /**
     * Executes task.
     * Captures all internal exceptions.
     */
    public final void run() {
        try {
            this.executeTask();
        } catch (Throwable t) {
            this.setErrorOccured(true);
            this.setException(t);
        } finally {
            this.stopTime = new Date();
            this.setDone();
        }
    }

    /**
     * Task specific code must be implemented by the subclass.
     *
     * @throws Exception All Exceptions.
     */
    public abstract void executeTask() throws Exception;
}