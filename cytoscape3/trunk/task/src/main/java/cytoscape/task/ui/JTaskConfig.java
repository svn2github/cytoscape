
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

package cytoscape.task.ui;

import java.awt.*;


/**
 * Used to configure the JTask UI component.
 */
public class JTaskConfig {
	/**
	 * Display the Status Field.
	 */
	private boolean statusFlag = false;

	/**
	 * Display the Time Elapsed Field.
	 */
	private boolean timeElapsedFlag = false;

	/**
	 * Display the Estimated Time Remaining Field.
	 */
	private boolean timeRemainingFlag = false;

	/**
	 * Display User Cancel Button.
	 */
	private boolean cancelButtonFlag = false;

	/**
	 * Display Close Button.
	 */
	private boolean closeButtonFlag = false;

	/**
	 * Disposes of Component When Task Completes.
	 */
	private boolean autoDispose = true;

	/**
	 * Defines Modality.
	 */
	private boolean modal = true;

	/**
	 * Owner, such as a JFrame.
	 * Primarily used to center the JTask component relative to the owner.
	 */
	private Frame ownerFrame;
	private Dialog ownerDialog;
	private boolean useFrame;

	/**
	 * Milliseconds until popup.
	 */
	private int millisToPopup = 100;

	/**
	 * Enables/Disables Display of the Status Field.
	 * <P>By default, this value is set to false.
	 *
	 * @param flag boolean value.
	 */
	public void displayStatus(boolean flag) {
		this.statusFlag = flag;
	}

	/**
	 * Enables/Disables Display of the Time Elapsed Field.
	 * <P>By default, this value is set to false.
	 *
	 * @param flag boolean value.
	 */
	public void displayTimeElapsed(boolean flag) {
		this.timeElapsedFlag = flag;
	}

	/**
	 * Enables/Disables Display of the Time Remaining Field.
	 * <P>By default, this value is set to false.
	 *
	 * @param flag boolean value.
	 */
	public void displayTimeRemaining(boolean flag) {
		this.timeRemainingFlag = flag;
	}

	/**
	 * Enables/Disables Display of the Cancel Button.
	 * <P>By default, this value is set to false.
	 *
	 * @param flag boolean value.
	 */
	public void displayCancelButton(boolean flag) {
		this.cancelButtonFlag = flag;
	}

	/**
	 * Enables/Disables Display of the Close Button.
	 * <P>By default, this value is set to false.
	 *
	 * @param flag boolean value.
	 */
	public void displayCloseButton(boolean flag) {
		this.closeButtonFlag = flag;
	}

	/**
	 * Enables/Disables Auto-Dispose Feature.
	 * When set to true, the JTask dialog box will automatically disappear
	 * when the task is complete.
	 * When set to false, the JTask dialog box will persist until the user
	 * dismissed it.
	 * <P>By default, this value is set to true.
	 *
	 * @param flag boolean value.
	 */
	public void setAutoDispose(boolean flag) {
		this.autoDispose = flag;
	}

	/**
	 * Specifies the Owner of the JTask.
	 * <P>
	 * Primarily used to center the JTask component relative to an owner,
	 * such as a JFrame object.
	 * <P>
	 * If set to null, the JTask component will be centered relative to the
	 * user's screen.
	 *
	 * @param owner Owner container.  May NOT be null.
	 */
	// TODO Why is this a Container?  It should be a Component.
	public void setOwner(Container owner) {
		setOwnerComponent((Component)owner);
	}

	public void setOwnerComponent(Component owner) {
        if (owner == null)
            throw new NullPointerException("component is null");

        if (owner instanceof Frame) {
			ownerFrame = (Frame)owner;
			useFrame = true;
			return;
		} else if ( owner instanceof Dialog) {
			ownerDialog = (Dialog)owner;
			useFrame = false;
			return;
		} else {
            setOwnerComponent(owner.getParent());
		}
	}

	public boolean isOwnerFrame() {
		return useFrame;
	}

	public Frame getOwnerFrame() {
		return ownerFrame;
	}

	public Dialog getOwnerDialog() {
		return ownerDialog;
	}


	/**
	 * Enables modality of the JTask Dialog box.
	 * <P>
	 * When set to true, the JTask Dialog box is modal, and the user cannot
	 * interact with any other UI components on the screen.
	 * <P>
	 * When set to false, the JTask Dialog box is not modal, and the user may
	 * interact with other UI components on the screen.
	 * <P>
	 * By default, this value is set to true.
	 * <P>
	 *
	 * @param modal modality flag.
	 */
	public void setModal(boolean modal) {
		this.modal = modal;
	}

	/**
	 * Sets Milliseconds until JTask Dialog Box is Displayed.
	 * <P>Default is set to 100 ms.
	 *
	 * @param millisToPopup long value.
	 */
	public void setMillisToPopup(int millisToPopup) {
		this.millisToPopup = millisToPopup;
	}

	/**
	 * Gets Millieconds until JTask Dialog Box is Displayed.
	 * <P>Default is set to 100 ms.
	 *
	 * @return millisToPopup long value.
	 */
	public int getMillisToPopup() {
		return this.millisToPopup;
	}

	/**
	 * Gets Display Options for Status Field.
	 *
	 * @return boolean value.
	 */
	boolean getStatusFlag() {
		return this.statusFlag;
	}

	/**
	 * Gets Display Options for Time Elapsed Field.
	 *
	 * @return boolean value.
	 */
	boolean getTimeElapsedFlag() {
		return this.timeElapsedFlag;
	}

	/**
	 * Gets Display Options for Time Remaining Field.
	 *
	 * @return boolean value.
	 */
	boolean getTimeRemainingFlag() {
		return this.timeRemainingFlag;
	}

	/**
	 * Gets Display Options for Cancel Button Field.
	 *
	 * @return boolean value.
	 */
	boolean getCancelButtonFlag() {
		return this.cancelButtonFlag;
	}

	/**
	 * Gets Display Options for Close Button Field.
	 *
	 * @return boolean value.
	 */
	boolean getCloseButtonFlag() {
		return this.closeButtonFlag;
	}

	/**
	 * Gets AutoDispose Flag.
	 *
	 * @return boolean value.
	 */
	boolean getAutoDispose() {
		return autoDispose;
	}

	/**
	 * Gets JTask Owner object.
	 *
	 * @return Component owner object.
	 */
	Component getOwner() {
		return useFrame ? ownerFrame : ownerDialog;
	}

	/**
	 * Gets Modality flag.
	 *
	 * @return boolean value.
	 */
	boolean getModal() {
		return this.modal;
	}
}
