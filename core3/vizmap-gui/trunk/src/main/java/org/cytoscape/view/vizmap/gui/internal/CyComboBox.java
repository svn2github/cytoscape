package org.cytoscape.view.vizmap.gui.internal;

import javax.swing.JComboBox;

public class CyComboBox<T> extends JComboBox {

	public CyComboBox() {
		this.setModel(new CyComboBoxModel<T>());
	}
	
	@SuppressWarnings("unchecked")
	public CyComboBoxModel<T> getModel() {
		return (CyComboBoxModel<T>) this.dataModel;
	}
	
	public T getSelectedItem() {
		return this.getModel().getSelectedItem();
	}
}
