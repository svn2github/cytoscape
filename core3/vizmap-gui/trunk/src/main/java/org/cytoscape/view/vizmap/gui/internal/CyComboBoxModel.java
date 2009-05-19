package org.cytoscape.view.vizmap.gui.internal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

class CyComboBoxModel<T> extends AbstractListModel implements ComboBoxModel {

	private List<T> data;

	private T selection = null;
	
	public CyComboBoxModel() {
		data = new ArrayList<T>();
	}

	public T getElementAt(int index) {
		return data.get(index);
	}

	public int getSize() {
		return data.size();
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem(Object anItem) {
		try {
			selection = (T) anItem;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(anItem
					+ " is not compatible data type for this model.", e);
		}
	}

	public T getSelectedItem() {
		return selection; // to add the selection to the combo box
	}
	
	public boolean contains(T value) {
		return data.contains(value);
	}

}