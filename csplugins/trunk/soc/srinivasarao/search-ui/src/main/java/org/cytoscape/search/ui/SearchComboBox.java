package org.cytoscape.search.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.cytoscape.session.CyNetworkManager;

public class SearchComboBox extends JComboBox {

	private Vector<String> queries;
	private JTextField searchField;
	private CyNetworkManager netmgr;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method initializes
	 * 
	 */
	public SearchComboBox(CyNetworkManager mgr) {
		super();
		this.netmgr = mgr;
		queries = new Vector<String>();
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
		editor.addKeyListener(new UserKeyListener(this, netmgr));
		initializeSearchField();
	}

	/**
	 * This method initializes searchField
	 * 
	 * @return javax.swing.JTextField
	 */
	private void initializeSearchField() {

		searchField = (JTextField) this.getEditor().getEditorComponent();
		searchField.setMinimumSize(new Dimension(15, 20));
		searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

	}

	public void addMenuItem(String o) {
		if (!queries.contains(o)) {
			queries.insertElementAt(o, 0);
			// this.addItem(o);
			this.insertItemAt(o, 0);
		}
	}

	public void removeMenuItem(int index) {

		this.removeItemAt(index);
		queries.removeElementAt(index);
		System.out.println("-------------------------------------------");
		for (int i = 0; i < queries.size(); i++) {
			System.out.println(queries.get(i));
		}
		System.out.println("-------------------------------------------");

	}

	public void clearAllItems() {
		queries.clear();
		this.removeAllItems();
	}

	public Vector<String> getHistory() {
		return queries;
	}

	public String getQueryAt(int index) {
		return queries.get(index - 1);
	}
}

class UserKeyListener extends KeyAdapter {
	private SearchComboBox box;
	private CyNetworkManager netmgr;

	public UserKeyListener(SearchComboBox b, CyNetworkManager nm) {
		this.box = b;
		this.netmgr = nm;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE
				&& e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
			box.removeMenuItem(box.getSelectedIndex());
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE
				|| e.getKeyCode() == KeyEvent.VK_BACK_SPACE
				|| (e.getKeyCode() == KeyEvent.VK_X && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK)) {
			//System.out.println("BackSpace Key released");

			JTextField jf = (JTextField) box.getEditor().getEditorComponent();
			System.out.println("Text:" + jf.getText());
			if (jf.getText().length() == 0) {
				SearchPanelFactory.getGlobalInstance(netmgr).clearAll();
			}
		}
	}

}