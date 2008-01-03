package org.cytoscape.model;

public interface CyModelObjectChangeListener {
	enum ChangeType {ADD, REMOVE, MODIFY};

	/**
	 * This method gets called by a CyNetwork when a change occurs
	 *
	 * @param object the changed object
	 * @param change the specific change that happened
	 */
	public void valueChanged(CyModelObject object, ChangeType change);
}
