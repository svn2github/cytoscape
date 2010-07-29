
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
	    throws IllegalThreadStateException, IllegalArgumentException {
		this.percentComplete = percentComplete;
	}

	/**
	 * Sets estimated time remaining:  no-op.
	 *
	 * @param l time remaining.
	 * @throws IllegalThreadStateException Illegal Thread State.
	 */
	public void setEstimatedTimeRemaining(long l) throws IllegalThreadStateException {
	}

	/**
	 * Sets Exception:  no-op.
	 *
	 * @param throwable Throwable Object.
	 * @param string    Human readable error message.
	 * @throws IllegalThreadStateException Illegal Thread State.
	 */
	public void setException(Throwable throwable, String string) throws IllegalThreadStateException {
	}

	/**	
	* Sets Exception:  no-op.
	*
	* @param throwable Throwable Object.
	* @param str1 Human readable error message.
 	* @param str2 Recovery Tip.
	*/
	public void setException(Throwable throwable, String str1, String str2)
		throws IllegalThreadStateException {
	}

	/**
	 * Sets Status:  no-op.
	 *
	 * @param status Status Message.
	 * @throws IllegalThreadStateException Illegal Thread State.
	 * @throws NullPointerException        NullPointer Error.
	 */
	public void setStatus(String status) throws IllegalThreadStateException, NullPointerException {
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
