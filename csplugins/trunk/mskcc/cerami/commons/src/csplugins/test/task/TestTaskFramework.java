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
package csplugins.test.task;

import csplugins.task.SampleTask;
import csplugins.task.Task;
import junit.framework.TestCase;

/**
 * JUnit Tests for Task Framework.
 *
 * @author Ethan Cerami
 */
public class TestTaskFramework extends TestCase {
    private static final long INIT_DELAY = 1000;
    private static final long FINAL_DELAY = 1000;
    private static final long COUNT_DELAY = 50;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    /**
     * Tests the SampleTask and Task Framework.
     */
    public void testSampleTask() {
        // Create New Task;  Count to 100.
        Task task = new SampleTask(MAX_VALUE, INIT_DELAY, FINAL_DELAY,
                COUNT_DELAY);

        //  Verify Task is Indeterminate at start
        assertTrue(task.isIndeterminate());

        //  Start Task in New Thread.
        task.start();

        //  Give task a head start.
        try {
            Thread.sleep(INIT_DELAY * 2);
        } catch (InterruptedException e) {
        }

        //  Verify Task is now determinate
        assertTrue(!task.isIndeterminate());

        //  Verify Max Progress Value
        int maxProgress = task.getMaxProgressValue();
        assertEquals(100, maxProgress);

        //  Monitor Running Task
        int count = MIN_VALUE;
        int currentProgress;
        int lastProgress = task.getProgressValue();
        while (count < MAX_VALUE / 2) {
            currentProgress = task.getProgressValue();

            //  Verify Current Progress.
            assertTrue(currentProgress >= lastProgress
                    && currentProgress <= maxProgress);

            //  Verify Progress Message
            String msg = task.getProgressMessage();
            assertTrue(msg.startsWith("Counting"));

            //  Verify Time Remaining
            long timeRemaining = task.getEstimatedTimeRemaining();
            assertTrue(timeRemaining > 0 && timeRemaining < 10000);

            //  Sleep so that task can do some more work.
            try {
                Thread.sleep(COUNT_DELAY);
            } catch (InterruptedException e) {
            }
            count++;
            lastProgress = currentProgress;
        }

        //  Now interrupt task
        task.interrupt();

        //  Give task time to stop / clean-up.
        try {
            Thread.sleep(FINAL_DELAY * 2);
        } catch (InterruptedException e) {
        }

        //  Verify Task is now done, and has indeed been interrupted.
        assertTrue(task.isDone());

        //  Verify Task is not complete.
        assertTrue(task.getProgressValue() < task.getMaxProgressValue());

        //  Verify we can access the InterruptedException and the Human
        //  readable error message.
        assertTrue(task.errorOccurred());
        Throwable t = task.getInternalException();
        assertTrue(t instanceof InterruptedException);
        assertTrue(task.getHumanReadableErrorMessage().
                startsWith("I was rudely interrupted"));
    }
}