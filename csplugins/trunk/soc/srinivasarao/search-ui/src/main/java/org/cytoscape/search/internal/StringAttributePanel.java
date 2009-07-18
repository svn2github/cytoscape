package org.cytoscape.search.internal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;

public class StringAttributePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private CyNetwork network = null;
	private String attrName = null;
	private JCheckBox[] boxes = null;
	private String type = null;

	/**
	 * This is the default constructor
	 */
	public StringAttributePanel(CyNetwork net, String attrname, String type) {
		super();
		this.network = net;
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
		gridBagConstraints.insets = new Insets(0,5,8,0);
		//this.setSize(300, 200);
		//this.setBorder(new LineBorder(Color.RED));
		this.setLayout(new GridBagLayout());
		this.add(getLabel(), gridBagConstraints);
		createCheckBoxes();
		for (int i = 0; i < boxes.length; i++) {
			GridBagConstraints gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.anchor = GridBagConstraints.WEST;
			//gc.gridwidth = GridBagConstraints.REMAINDER;
			gc.insets = new Insets(0,5,4,0);
			gc.gridx = 0;
			gc.gridy = i+1;
			gc.weightx=1.0;
			//gc.weighty=1.0;
			this.add(boxes[i],gc);
			
		}
		/*
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.WEST;
		//gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.gridx = 0;
		gc.weightx=1.0;
		gc.gridy = boxes.length+1;
		this.add(new JTextField(),gc);
		*/
	}

	private JLabel getLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			jLabel.setText(attrName);
			//jLabel.setBorder(new LineBorder(Color.BLUE));
			//jLabel.setFont(new Font(null,Font.BOLD, 15));
		}
		return jLabel;
	}

	public List<String> getattrValues() {

		CyDataTable datatable = network.getCyDataTables(type).get(
				CyNetwork.DEFAULT_ATTRS);
		List<String> l = datatable.getColumnValues(attrName, String.class);
		return l;
	}

	public void getCheckedValues() {
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isSelected())
				System.out.println(boxes[i].getActionCommand());
		}
	}

	public void createCheckBoxes() {
		List<String> l = getattrValues();
		boxes = new JCheckBox[l.size()];
		for (int i = 0; i < l.size(); i++) {
			String text = l.get(i);
			boxes[i] = new JCheckBox();
			boxes[i].setActionCommand(text);
			boxes[i].setText(text);
			//boxes[i].setHorizontalAlignment(SwingConstants.LEFT);
			//boxes[i].setBorder(new LineBorder(Color.RED));
			//System.out.println(boxes[i].getFont());
			//boxes[i].setFont(new Font(null, Font.BOLD, 12));
		}
	}
}
