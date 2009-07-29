package org.cytoscape.search.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;

public class SampleComboBox extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method initializes
	 * 
	 */
	public SampleComboBox() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(new Dimension(174, 24));
		setEditable(true);
		setMaximumRowCount(5);
		this.addItem("Srinivas");
		this.addItem("Nithin");
		this.addItem("Hanuma");
		this.addItem("Aditya");
		this.addItem("Sai Krishna");
		this.addItem("Kiran");
		this.addItem("Kranthi");
		this.addItem("Ganesh");
		this.addItem("Harsha");
		Component editor = this.getEditor().getEditorComponent();
		editor.addKeyListener(new UserKeyListener(this));
	}
}

class UserKeyListener extends KeyAdapter {
	private SampleComboBox box;

	public UserKeyListener(SampleComboBox b) {
		this.box = b;
	}

	public void keyPressed(KeyEvent e) {
		// System.out.println("I am in key listener");
		if (e.getKeyCode() == KeyEvent.VK_DELETE
				&& e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
			System.out.println("Delete Key Pressed");
			box.removeItem(box.getSelectedItem());
		}
	}
}