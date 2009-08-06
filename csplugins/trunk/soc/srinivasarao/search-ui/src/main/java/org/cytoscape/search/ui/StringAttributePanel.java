package org.cytoscape.search.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.util.EnhancedSearchUtils;
import org.cytoscape.session.CyNetworkManager;

public class StringAttributePanel extends BasicDraggablePanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private JTextField stringField = null;
	private CyNetworkManager netmgr = null;
	private String attrName = null;
	private JCheckBox[] boxes = null;
	private String type = null;
	private JPanel attrPanel;
	private int limit = 10;
	private List<String> attrValues; // @jve:decl-index=0:
	private String attrQuery = null;

	/**
	 * This is the default constructor
	 */
	public StringAttributePanel(CyNetworkManager nm, String attrname,
			String type) {
		super();
		this.netmgr = nm;
		this.attrName = attrname;
		this.type = type;
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
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.WEST;
			gc.insets = new Insets(0, 12, 4, 0);
			gc.gridx = 0;
			gc.gridy = 1;
			gc.weightx = 0;
			attrPanel.add(getstringField(), gc);
			gc.gridx = 1;
			gc.weightx = 1.0;
			gc.fill = GridBagConstraints.HORIZONTAL;
			attrPanel.add(Box.createHorizontalStrut(0), gc);
		}

		GridBagConstraints gg = new GridBagConstraints();
		gg.fill = GridBagConstraints.HORIZONTAL;
		gg.gridwidth = GridBagConstraints.REMAINDER;
		gg.weightx = 1.0;
		this.add(attrPanel, gg);
	}

	private JLabel getLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			jLabel.setText(attrName);
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
		if (stringField == null) {
			stringField = new JTextField();
			stringField.setMinimumSize(new Dimension(150, 20));
			stringField.setPreferredSize(new Dimension(170, 20));
			stringField.setText(null);
			stringField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
			stringField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String term = attrName + ":" + stringField.getText();
					if (attrQuery == null) {
						attrQuery = term;
					} else {
						attrQuery = attrQuery + " OR " + term;

					}
					SearchPanelFactory.getGlobalInstance(netmgr)
							.updateSearchField();
					stringField.setText(null);
				}
			});
		}
		return stringField;
	}

	public void getattrValues() {
		final CyNetwork network = netmgr.getCurrentNetwork();
		CyDataTable datatable = network.getCyDataTables(type).get(
				CyNetwork.DEFAULT_ATTRS);
		attrValues = datatable.getColumnValues(attrName, String.class);

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
			if (attrQuery != null) {
				res = res + attrQuery + ")";
				return res;
			} else
				return null;
		}

	}

	public void clearCheckBoxes() {
		if (attrValues.size() <= limit) {
			for (int i = 0; i < boxes.length; i++) {
				boxes[i].setSelected(false);
			}
		} else {
			stringField.setText(null);
			attrQuery= null;
		}
	}

	public void createCheckBoxes() {
		boxes = new JCheckBox[attrValues.size()];
		for (int i = 0; i < attrValues.size(); i++) {
			String text = attrValues.get(i);
			boxes[i] = new JCheckBox();
			boxes[i].setActionCommand(EnhancedSearchUtils
					.replaceWhitespace(attrName)
					+ ":" + text);
			boxes[i].setText(text);
			boxes[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SearchPanelFactory.getGlobalInstance(netmgr)
							.updateSearchField();
				}
			});
		}
	}
}
