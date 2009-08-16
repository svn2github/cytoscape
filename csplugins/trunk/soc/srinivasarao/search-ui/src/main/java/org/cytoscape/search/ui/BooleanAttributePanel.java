package org.cytoscape.search.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.session.CyNetworkManager;

public class BooleanAttributePanel extends BasicDraggablePanel {

	private static final long serialVersionUID = 1L;
	private String attrName = null;
	private JLabel jLabel = null;
	private JCheckBox trueButton = null;
	private JCheckBox falseButton = null;
	private JPanel jPanel = null;
	private CyNetworkManager netmgr;
	private String type;
	
	/**
	 * This is the default constructor
	 */
	public BooleanAttributePanel(String attrName, CyNetworkManager nm,String attrType) {
		super();
		this.attrName = attrName;
		this.netmgr = nm;
		this.type = attrType;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 8, 5, 0);
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getLabel(), gridBagConstraints);
		this.add(getJPanel(), gridBagConstraints1);
		jPanel.setVisible(false);

	}

	private JLabel getLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			jLabel = new JLabel();
			if(type.equals("NODE")){
				jLabel.setText(attrName + " [N]");
			}else{
				jLabel.setText(attrName + " [E]");
			}
			//jLabel.setText(attrName);
			jLabel.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (jPanel.isVisible()) {
						jPanel.setVisible(false);
					} else {
						jPanel.setVisible(true);
					}
				}
			});
		}
		return jLabel;
	}

	private JCheckBox getTrueButton() {
		if (trueButton == null) {
			trueButton = new JCheckBox();
			trueButton.setText("True");
			trueButton.setActionCommand("True");
			trueButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SearchPanelFactory.getGlobalInstance(netmgr)
							.updateSearchField();
				}
			});
		}
		return trueButton;
	}

	private JCheckBox getFalseButton() {
		if (falseButton == null) {
			falseButton = new JCheckBox();
			falseButton.setText("False");
			falseButton.setActionCommand("False");
			falseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SearchPanelFactory.getGlobalInstance(netmgr)
							.updateSearchField();
				}
			});
		}
		return falseButton;
	}

	public String getCheckedValues() {
		String str = "(";
		String sel = null;
		boolean select1 = false;
		boolean select2 = false;
		if (trueButton.isSelected()) {
			select1 = true;
			str = str + attrName + ":" + "true)";
		}
		if (falseButton.isSelected()) {
			select2 = true;
			if (!select1) {
				str = str + attrName + ":" + "false)";
			} else {
				trueButton.setSelected(false);
				falseButton.setSelected(false);
			}
		}
		if ((select1 && !select2) || select2 && !select1)
			return str;
		else
			return null;
	}

	public void clearAll() {
		trueButton.setSelected(false);
		falseButton.setSelected(false);
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			GridBagConstraints gct = new GridBagConstraints();
			GridBagConstraints gcf = new GridBagConstraints();
			gct.fill = GridBagConstraints.HORIZONTAL;
			gct.anchor = GridBagConstraints.WEST;
			gct.insets = new Insets(0, 12, 4, 0);
			gct.gridx = 0;
			gct.gridy = 0;
			gct.weightx = 1.0;
			gcf.fill = GridBagConstraints.HORIZONTAL;
			gcf.anchor = GridBagConstraints.WEST;
			gcf.insets = new Insets(0, 12, 4, 0);
			gcf.gridx = 0;
			gcf.gridy = 1;
			gcf.weightx = 1.0;
			jPanel.add(getTrueButton(), gct);
			jPanel.add(getFalseButton(), gcf);
		}
		return jPanel;
	}
}
