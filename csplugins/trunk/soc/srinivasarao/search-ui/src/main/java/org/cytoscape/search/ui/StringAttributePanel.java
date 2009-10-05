package org.cytoscape.search.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.util.EnhancedSearchUtils;
import org.cytoscape.session.CyNetworkManager;

import cytoscape.Cytoscape;

public class StringAttributePanel extends BasicDraggablePanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private ArrayList<JTextField> fields;
	private CyNetworkManager netmgr = null;
	private String attrName = null;
	private JCheckBox[] boxes = null;
	private String type = null;
	private JPanel attrPanel;
	private int limit = 10;
	private ArrayList<String> attrValues = new ArrayList<String>();
	private ImageIcon delIcon;
	private ImageIcon addIcon;
	private JLabel addLabel;
	private HashMap<JTextField, JLabel> map;

	// private String attrQuery = "";

	/**
	 * This is the default constructor
	 */
	public StringAttributePanel(CyNetworkManager nm, String attrname,
			String type) {
		super();
		this.netmgr = nm;
		this.attrName = attrname;
		this.type = type;
		fields = new ArrayList<JTextField>();
		map = new HashMap<JTextField, JLabel>();
		// delIcon = new
		// ImageIcon(getClass().getResource("/images/stock_delete-16.png"));
		delIcon = new ImageIcon(Cytoscape.class
				.getResource("/images/ximian/stock_delete-16.png"));
		addIcon = new ImageIcon(this.getClass().getResource("/icons/add.gif"));

		if (addIcon == null) {
			System.out.println("It is null");
		} else {
			System.out.println(addIcon);
		}

		getattrValues();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 8, 5, 0);
		this.setLayout(new GridBagLayout());
		this.add(getLabel(), gridBagConstraints);

		GridBagConstraints gg = new GridBagConstraints();
		gg.fill = GridBagConstraints.HORIZONTAL;
		gg.gridwidth = GridBagConstraints.REMAINDER;
		gg.weightx = 1.0;
		this.add(getAttrPanel(), gg);
	}

	private JPanel getAttrPanel() {
		if (attrPanel == null) {
			attrPanel = new JPanel();
			attrPanel.setLayout(new GridBagLayout());
			attrPanel.setVisible(false);
			if (attrValues.size() <= limit) {
				createCheckBoxes();
				for (int i = 0; i < boxes.length; i++) {
					GridBagConstraints gc = new GridBagConstraints();
					gc.fill = GridBagConstraints.HORIZONTAL;
					gc.anchor = GridBagConstraints.WEST;
					gc.insets = new Insets(0, 12, 4, 0);
					gc.gridx = 0;
					gc.gridy = i;
					gc.weightx = 1.0;
					attrPanel.add(boxes[i], gc);
				}
			} else {
				JTextField jf = getstringField();
				fields.add(jf);
				map.put(jf, getRemoveLabel());
				relayout();

				/*
				 * GridBagConstraints gc = new GridBagConstraints(); gc.anchor =
				 * GridBagConstraints.WEST; gc.insets = new Insets(0, 12, 4, 0);
				 * gc.gridx = 0; gc.gridy = 1; gc.weightx = 0; //JTextField jf =
				 * getstringField(); //fields.add(jf); attrPanel.add(jf, gc);
				 * gc.gridx = 1; gc.weightx = 1.0; gc.fill =
				 * GridBagConstraints.HORIZONTAL;
				 * attrPanel.add(Box.createHorizontalStrut(0), gc);
				 */
			}
		}
		return attrPanel;
	}

	private JLabel getAddLabel() {
		addLabel = new JLabel();
		// addLabel.setText("add");
		addLabel.setIcon(addIcon);
		addLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JTextField jf = getstringField();
				fields.add(jf);
				map.put(jf, getRemoveLabel());
				relayout();
			}
		});
		return addLabel;
	}

	private JLabel getRemoveLabel() {
		JLabel delLabel = new JLabel();
		delLabel.setIcon(delIcon);
		delLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JLabel del = (JLabel) e.getSource();
				for (JTextField jf : map.keySet()) {
					if (map.get(jf).equals(del)) {
						attrPanel.remove(jf);
						map.remove(jf);
						fields.remove(jf);
						relayout();
						SearchPanelFactory.getGlobalInstance(netmgr)
								.updateSearchField();
						break;
					}
				}
			}
		});

		return delLabel;
	}

	private void relayout() {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 0;
		gc.anchor = GridBagConstraints.PAGE_START;
		attrPanel.removeAll();
		int row = 0;
		for (int i = 0; i < fields.size(); i++) {
			if (i == 0) {
				gc.insets = new Insets(0, 15, 4, 0);
			} else {
				gc.insets = new Insets(0, 15, 4, 0);
			}
			gc.gridy = row;
			JTextField b = fields.get(i);
			attrPanel.add(b, gc);
			if (i == 0) {
				gc.gridx = 1;
				gc.insets = new Insets(0, 12, 4, 10);
				attrPanel.add(getAddLabel(), gc);
				/*
				 * To maintain the x alignment of rest of the fields
				 */
				gc.gridx = 0;
			} else {
				gc.gridx = 1;
				gc.insets = new Insets(0, 12, 4, 10);
				// attrPanel.add(getRemoveLabel(), gc);
				attrPanel.add(map.get(b), gc);
				/*
				 * To maintain the x alignment of rest of the fields
				 */
				gc.gridx = 0;
			}
			row++;
		}
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx = 0;
		gc.gridy = row;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		attrPanel.add(Box.createRigidArea(null), gc);
		attrPanel.validate();
		this.setVisible(false);
		this.setVisible(true);
	}

	private JLabel getLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			if (type.equals("NODE")) {
				jLabel.setText(attrName + " [N]");
			} else {
				jLabel.setText(attrName + " [E]");
			}
			// jLabel.setText(attrName);
			jLabel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (attrPanel.isVisible()) {
						attrPanel.setVisible(false);
					} else {
						attrPanel.setVisible(true);
					}
				}
			});
		}
		return jLabel;
	}

	private JTextField getstringField() {
		JTextField stringField = new JTextField();
		stringField.setMinimumSize(new Dimension(150, 20));
		stringField.setPreferredSize(new Dimension(170, 20));
		stringField.setText("");
		stringField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		/*
		 * stringField.addActionListener(new java.awt.event.ActionListener() {
		 * public void actionPerformed(java.awt.event.ActionEvent e) { //
		 * JTextField jf = (JTextField) e.getSource();
		 * SearchPanelFactory.getGlobalInstance(netmgr) .updateSearchField(); }
		 * });
		 * 
		 * stringField.addFocusListener(new java.awt.event.FocusAdapter() {
		 * public void focusLost(java.awt.event.FocusEvent e) {
		 * SearchPanelFactory.getGlobalInstance(netmgr) .updateSearchField(); }
		 * });
		 */
		stringField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				SearchPanelFactory.getGlobalInstance(netmgr)
						.updateSearchField();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				SearchPanelFactory.getGlobalInstance(netmgr)
						.updateSearchField();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				SearchPanelFactory.getGlobalInstance(netmgr)
						.updateSearchField();
			}
		});

		createPopupMenu(stringField);

		return stringField;
	}

	public void getattrValues() {
		final CyNetwork network = netmgr.getCurrentNetwork();
		CyDataTable datatable = network.getCyDataTables(type).get(
				CyNetwork.DEFAULT_ATTRS);
		List<String> nonuniquevalues = datatable.getColumnValues(attrName,
				String.class);
		for (String str : nonuniquevalues) {
			if (!attrValues.contains(str)) {
				attrValues.add(str);
			}
		}
	}

	public String getCheckedValues() {
		String res = "(";
		if (attrValues.size() <= limit) {
			int num = 0;
			for (int i = 0; i < boxes.length; i++) {
				if (boxes[i].isSelected()) {
					num = num + 1;
					res = res + boxes[i].getActionCommand();
					res = res + " OR ";
				}
			}
			if (num == 0)
				return null;
			else {
				int k = res.lastIndexOf("OR");
				res = res.substring(0, k - 1);
				res = res + ")";
				return res;
			}
		} else {
			boolean ret = false;
			StringBuffer query = new StringBuffer("(");
			for (JTextField jf : fields) {
				String str = jf.getText();
				String attr = EnhancedSearchUtils.replaceWhitespace(attrName);
				if (str != null && !str.equals("")) {
					if (query.toString().equals("(")) {
						query.append(attr + ":" + str);
					} else {
						query.append(" OR " + attr + ":" + str);
					}
					ret = true;
				}
			}
			query.append(")");
			if (ret) {
				return query.toString();
			} else {
				return null;
			}
		}
	}

	public void clearAll() {
		if (attrValues.size() <= limit) {
			for (int i = 0; i < boxes.length; i++) {
				boxes[i].setSelected(false);
			}
		} else {
			// TODO Need to clear all the string fields in fields arraylist
			for (JTextField jf : fields) {
				jf.setText("");
			}
			// stringField.setText("");
			// attrQuery = "";
		}
	}

	public void createCheckBoxes() {
		boxes = new JCheckBox[attrValues.size()];
		for (int i = 0; i < attrValues.size(); i++) {
			String text = attrValues.get(i);
			boxes[i] = new JCheckBox();
			if (text.indexOf(" ") == -1 && text.indexOf("\t") == -1) {
				boxes[i].setActionCommand(EnhancedSearchUtils
						.replaceWhitespace(attrName)
						+ ":" + text);
			} else {
				boxes[i].setActionCommand(EnhancedSearchUtils
						.replaceWhitespace(attrName)
						+ ":" + "\"" + text + "\"");
			}
			boxes[i].setText(text);
			boxes[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SearchPanelFactory.getGlobalInstance(netmgr)
							.updateSearchField();
				}
			});
		}
	}

	private void createPopupMenu(JTextField stringField) {
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		menuItem = new JMenuItem("Clear");
		menuItem.addActionListener(new StringFieldListener(stringField));
		popup.add(menuItem);
		// Add 'Clear All' menu item

		// Add listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);
		stringField.addMouseListener(popupListener);
	}

	/**
	 * Displays the popup menu on mouse right-click if search field is enabled
	 */
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	class StringFieldListener implements ActionListener {

		JTextField jf;

		public StringFieldListener(JTextField stringField) {
			this.jf = stringField;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			jf.setText(null);
		}

	}
}
