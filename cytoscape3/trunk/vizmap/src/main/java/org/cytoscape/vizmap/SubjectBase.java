package org.cytoscape.vizmap;

import javax.swing.event.ChangeListener;

public interface SubjectBase {

	/**
	 * Add a ChangeListener. When the state underlying the calculator changes,
	 * all ChangeListeners will be notified.
	 *
	 * @param listener
	 *            ChangeListener to add
	 */
	public void addChangeListener(ChangeListener listener);

	/**
	 * Remove a ChangeListener from the calcaultor. When the state underlying
	 * the calculator changes, all ChangeListeners will be notified.
	 *
	 * @param listener
	 *            ChangeListener to add
	 */
	public void removeChangeListener(ChangeListener listener);

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireStateChanged();

}