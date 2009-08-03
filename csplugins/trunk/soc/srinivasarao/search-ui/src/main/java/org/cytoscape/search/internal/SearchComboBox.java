package org.cytoscape.search.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;

public class SearchComboBox extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method initializes
	 * 
	 */
	public SearchComboBox() {
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
		Component editor = this.getEditor().getEditorComponent();
		editor.addKeyListener(new UserKeyListener(this));
	}
	
}

class UserKeyListener extends KeyAdapter {
	private SearchComboBox box;

	public UserKeyListener(SearchComboBox b) {
		this.box = b;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE
				&& e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
			System.out.println("Delete Key Pressed");
			box.removeItem(box.getSelectedItem());
		}
	}
}