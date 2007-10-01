
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.task.sample;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import java.io.IOException;


/**
 * Sample Task, used to illustrate the Task Framework.
 * This tasks counts from 0 to maxValue.
 */
public class SampleTask implements Task {
	private static final int MIN_VALUE = 0;
	private int maxValue;
	private long countDelay;
	private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;
	private int exceptionIndex = Integer.MAX_VALUE;

	/**
	 * Constructor.
	 *
	 * @param max        Max Count Value.
	 * @param countDelay Delay between each count (in milliseconds).
	 */
	public SampleTask(int max, long countDelay) {
		this.maxValue = max;
		this.countDelay = countDelay;
	}

	/**
	 * Constructor.
	 * Provides a test of Exception Handling.
	 * The Task will throw a NullPointerException when it reaches the
	 * exceptionIndex value.
	 *
	 * @param max            Max Count Value.
	 * @param countDelay     Delay between each count (in milliseconds).
	 * @param exceptionIndex The Task will throw a NullPointerException
	 *                       when it reaches the exceptionIndex
	 *                       value.
	 */
	public SampleTask(int max, long countDelay, int exceptionIndex) {
		this.maxValue = max;
		this.countDelay = countDelay;
		this.exceptionIndex = exceptionIndex;
	}

	/**
	 * Run the Task.
	 */
	public void run() {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}

		try {
			//  Count from 0 to maxValue with a countDelay
			//  Counting from 0..100 with a 50 ms delay should take ~5 seconds

			//  Make sure to check the interrupt flag.
			int i = MIN_VALUE;

			while ((i <= maxValue) && !interrupted) {
				// Calculate Percentage.  This must be a value between 0..100.
				int percentComplete = (int) (((double) i / maxValue) * 100);

				//  Estimate Time Remaining
				long totalTime = maxValue * countDelay;
				long timeElapsed = i * countDelay;
				long timeRemaining = totalTime - timeElapsed;

				//  Update the Task Monitor.
				//  This automatically updates the UI Component w/ progress bar.
				if (taskMonitor != null) {
					taskMonitor.setPercentCompleted(percentComplete);
					taskMonitor.setStatus("Counting:  " + i);
					taskMonitor.setEstimatedTimeRemaining(timeRemaining);
				}

				//  Illustrates how to Handle/Report Exceptions within a Task.
				//  When this IOException is thrown, the task will stop
				//  execution and report the error to the Task Monitor.
				//  This causes the UI Component to automatically display
				//  an error dialog box to the end-user.
				if (i == this.exceptionIndex) {
					throw new IOException("This is a Fake IO Exception");
				}

				// Artificial Delay
				Thread.sleep(countDelay);
				i++;
			}

			taskMonitor.setStatus("Task is now complete."
			                      + "\n\nThis is a long message used to demonstrate that status "
			                      + "messages can be long, and can span multiple lines.");
		} catch (InterruptedException e) {
			taskMonitor.setException(e, "Counting interrupted");
		} catch (IOException e) {
			taskMonitor.setException(e, "Counting aborted by fake exception");
		}
	}

	/**
	 * Non-blocking call to interrupt the task.
	 */
	public void halt() {
		this.interrupted = true;
	}

	/**
	 * Sets the Task Monitor.
	 *
	 * @param taskMonitor TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) {
		if (this.taskMonitor != null) {
			throw new IllegalStateException("Task Monitor is already set.");
		}

		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 *
	 * @return human readable task title.
	 */
	public String getTitle() {
		return new String("Counting Task");
	}
}
