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

/**
 * Interface for long-running tasks.
 * Tasks usually take a while (several seconds to several minutes)
 * to process, and run in a separate thread.
 *
 * @author Ethan Cerami
 */
public interface Task extends Runnable {

    /**
     * Starts the task in a new thread.
     */
    void start();

    /**
     * Gets Human Readable Description of Task.
     * Used by UI Components.
     *
     * @return Human Readable Description of Task.
     */
    String getTaskDescription();

    /**
     * Gets current value of progress.
     * For example, if maxProgressValue is 10, and progressValue is 5,
     * the task is 50% complete.
     *
     * @return integer value.
     */
    int getProgressValue();

    /**
     * Gets maximum value of progress.
     * For example, if maxProgressValue is 10, and progressValue is 5,
     * the task is 50% complete.
     *
     * @return integer value.
     */
    int getMaxProgressValue();

    /**
     * Indicates that the progress of task is currently indeterminate.
     * The task should be set to indeterminate only if if it has no idea
     * how much time / progress is remaining.
     *
     * @return true of false.
     */
    boolean isIndeterminate();

    /**
     * Gets the current progress message.
     * This is a message to be be displayed to the end user.
     * For example, a long-running task that parses XML data might have a
     * progress message, such as, "Validating XML document against XML Schema.",
     * followed by a second message, such as "Validation Passed."
     *
     * @return progress message string.
     */
    String getProgressMessage();

    /**
     * Gets estimated amount of time remaining (in milli-seconds).
     * This value can be displayed to the end-user, but note that the value
     * is only an esimate.  A negative return value indicates that the
     * task has no idea how much time is remaining.
     *
     * @return time remaining (in milli-seconds) or negative value,
     *         indicating unknown time remaining.
     */
    long getEstimatedTimeRemaining();

    /**
     * Gets Approximate Time Elapsed Since Task Started.
     * If task has already completed, getTimeElapsed returns the total
     * execution time for the task.
     *
     * @return time (in milli-seconds).
     */
    long getTimeElapsed();

    /**
     * Determines if an error has occurred within the task.
     *
     * @return true or false.
     */
    boolean errorOccurred();

    /**
     * Gets Exception (if any) that has occurred within the task.
     * Since the task is running in a separate thread, we need a convenient
     * way to capture exceptions.  Any exception thrown by the task is available
     * via this method.
     *
     * @return Exception object.
     */
    Throwable getInternalException();

    /**
     * Gets human readable error message, for display to the end-user.
     *
     * @return human readable error message.
     */
    String getHumanReadableErrorMessage();

    /**
     * Interrupts the task.
     */
    void interrupt();

    /**
     * Determines if task has been interrupted.
     *
     * @return interrupt Flag.
     */
    boolean isInterrupted();

    /**
     * Determines if the task is done.
     *
     * @return true of false.
     */
    boolean isDone();
}