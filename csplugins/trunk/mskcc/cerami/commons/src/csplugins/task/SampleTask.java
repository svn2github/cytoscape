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
 * A SampleTask, used for testing / illustration purposes only.
 * This task counts from 0..MaxValue, and includes various optional
 * delay parameters.  The task provides estimates for time remaining, and
 * can be safely interrupted by another thread.
 *
 * @author Ethan Cerami.
 */
public class SampleTask extends BaseTask {
    private static final int MIN_VALUE = 0;
    private int maxValue;
    private long initializationDelay;
    private long finalizationDelay;
    private long countDelay;

    /**
     * Constructor.
     *
     * @param max                 Max Count Value.
     * @param initializationDelay initialization delay in milliseconds.
     * @param finalizationDelay   finalization delay in milliseconds.
     * @param countDelay          delay between each count.
     */
    public SampleTask(int max, long initializationDelay, long finalizationDelay,
            long countDelay) {
        super("Counting from 0.." + max);
        this.maxValue = max;
        this.initializationDelay = initializationDelay;
        this.finalizationDelay = finalizationDelay;
        this.countDelay = countDelay;
    }

    /**
     * A Sample Dummy Task that counts from 0..MaxValue.
     *
     * @throws Exception All Exceptions.
     */
    public void executeTask() throws Exception {
        this.setMaxProgressValue(maxValue);
        this.setProgressValue(MIN_VALUE);

        try {
            //  At First, Task is indeterminate
            //  Used for simple testing purposes
            this.setProgressMessage("Initializing Counter Task...");
            this.setIndeterminate(true);
            Thread.sleep(initializationDelay);

            //  Set Initial Time Estimate
            this.setEstimatedTimeRemaining((maxValue - MIN_VALUE) * countDelay);

            //  Count from MIN_VALUE to MAX_VALUE with a TIME_DELAY
            //  Counting from 0..100 with a 50 ms delay should take ~5 seconds
            //  Make sure to check the interrupt flag.
            int i = MIN_VALUE;
            while (i++ < maxValue && !isInterrupted()) {
                this.setProgressValue(i);
                this.setProgressMessage("Counting:  " + i);

                //  Update Time Estimate
                this.setEstimatedTimeRemaining((maxValue - i) * countDelay);
                Thread.sleep(countDelay);
            }
        } catch (InterruptedException e) {
            setHumanReadableErrorMessage("I was rudely interrupted!");
            throw e;
        } finally {
            //  Task becomes indeterminate again
            //  Again, for simple testing purposes
            if (this.isInterrupted()) {
                this.setProgressMessage("Canceled by User.  Cleaning up...");
            } else {
                this.setProgressMessage("Finalizing Counter Task...");
            }
            this.setIndeterminate(true);
            Thread.sleep(finalizationDelay);
        }
    }
}