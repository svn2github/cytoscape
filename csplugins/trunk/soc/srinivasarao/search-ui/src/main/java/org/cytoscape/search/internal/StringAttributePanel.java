package org.cytoscape.search.internal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.util.EnhancedSearchUtils;
import org.cytoscape.session.CyNetworkManager;

public class StringAttributePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private CyNetworkManager netmgr = null;
	private String attrName = null;
	private JCheckBox[] boxes = null;
	private String type = null;

	/**
	 * This is the default constructor
	 */
	public StringAttributePanel(CyNetworkManager nm, String attrname,
			String type) {
		super();
		this.netmgr = nm;
		this.attrName = attrname;
		this.type = type;
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
		gridBagConstraints.insets = new Insets(0, 5, 8, 0);
		// this.setSize(300, 200);
		// this.setBorder(new LineBorder(Color.RED));
		this.setLayout(new GridBagLayout());
		this.add(getLabel(), gridBagConstraints);
		createCheckBoxes();
		for (int i = 0; i < boxes.length; i++) {
			GridBagConstraints gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.anchor = GridBagConstraints.WEST;
			// gc.gridwidth = GridBagConstraints.REMAINDER;
			gc.insets = new Insets(0, 5, 4, 0);
			gc.gridx = 0;
			gc.gridy = i + 1;
			gc.weightx = 1.0;
			// gc.weighty=1.0;
			this.add(boxes[i], gc);

		}
		/*
		 * GridBagConstraints gc = new GridBagConstraints(); gc.fill =
		 * GridBagConstraints.HORIZONTAL; gc.anchor = GridBagConstraints.WEST;
		 * //gc.gridwidth = GridBagConstraints.REMAINDER; gc.gridx = 0;
		 * gc.weightx=1.0; gc.gridy = boxes.length+1; this.add(new
		 * JTextField(),gc);
		 */
	}

	private JLabel getLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			jLabel.setText(attrName);
			// jLabel.setBorder(new LineBorder(Color.BLUE));
			// jLabel.setFont(new Font(null,Font.BOLD, 15));
		}
		return jLabel;
	}

	public List<String> getattrValues() {
		final CyNetwork network = netmgr.getCurrentNetwork();
		CyDataTable datatable = network.getCyDataTables(type).get(
				CyNetwork.DEFAULT_ATTRS);
		List<String> l = datatable.getColumnValues(attrName, String.class);
		return l;
	}

	public String getCheckedValues() {
		String res = "(";
		int num = 0;
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isSelected()) {
				num = num + 1;
				res = res + boxes[i].getActionCommand();
				res = res + " OR ";
				// System.out.println(boxes[i].getActionCommand());
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
	}

	public void clearCheckBoxes() {
		for (int i = 0; i < boxes.length; i++) {
			boxes[i].setSelected(false);
		}
	}

	public void createCheckBoxes() {
		List<String> l = getattrValues();
		boxes = new JCheckBox[l.size()];
		for (int i = 0; i < l.size(); i++) {
			String text = l.get(i);
			boxes[i] = new JCheckBox();
			boxes[i].setActionCommand(EnhancedSearchUtils
					.replaceWhitespace(attrName)
					+ ":" + text);
			boxes[i].setText(text);
			boxes[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AbstractButton button = (AbstractButton) e.getSource();
					boolean b = button.getModel().isSelected();
					if (b) {
						SearchPanelFactory.getGlobalInstance(netmgr)
								.updateSearchField();
					}
				}
			});
		}
	}
}
