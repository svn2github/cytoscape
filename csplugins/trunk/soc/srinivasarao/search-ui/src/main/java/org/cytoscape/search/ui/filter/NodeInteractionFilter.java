package org.cytoscape.search.ui.filter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NodeInteractionFilter extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel filterLabel = null;
	private JPanel filterPanel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JCheckBox sourceBox = null;
	private JCheckBox targetBox = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JTextField queryField = null;

	/**
	 * This is the default constructor
	 */
	public NodeInteractionFilter() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.gridx = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		filterLabel = new JLabel();
		filterLabel.setText("NodeInteractionFilter");
		filterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (filterPanel.isVisible()) {
					filterPanel.setVisible(false);
				} else {
					filterPanel.setVisible(true);
				}
			}
		});
		this.setSize(425, 456);
		this.setLayout(new GridBagLayout());
		this.add(filterLabel, gridBagConstraints1);
		this.add(getFilterPanel(), gridBagConstraints2);
	}

	/**
	 * This method initializes filterPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.BOTH;
			gridBagConstraints12.gridy = 3;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.gridx = 2;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.gridwidth = 2;
			jLabel4 = new JLabel();
			jLabel4.setText("and which match the query");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 2;
			jLabel3 = new JLabel();
			jLabel3.setText("of atleast one edge");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 3;
			gridBagConstraints9.gridy = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 1;
			jLabel2 = new JLabel();
			jLabel2.setText("which are the");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jLabel1 = new JLabel();
			jLabel1.setText("Select nodes");
			filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			filterPanel.add(jLabel1, gridBagConstraints);
			filterPanel.add(jLabel2, gridBagConstraints7);
			filterPanel.add(getSourceBox(), gridBagConstraints8);
			filterPanel.add(getTargetBox(), gridBagConstraints9);
			filterPanel.add(jLabel3, gridBagConstraints10);
			filterPanel.add(jLabel4, gridBagConstraints11);
			filterPanel.add(getQueryField(), gridBagConstraints12);
		}
		return filterPanel;
	}

	/**
	 * This method initializes sourceBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSourceBox() {
		if (sourceBox == null) {
			sourceBox = new JCheckBox();
			sourceBox.setText("source");
		}
		return sourceBox;
	}

	/**
	 * This method initializes targetBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getTargetBox() {
		if (targetBox == null) {
			targetBox = new JCheckBox();
			targetBox.setText("target");
		}
		return targetBox;
	}

	/**
	 * This method initializes queryField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getQueryField() {
		if (queryField == null) {
			queryField = new JTextField();
			queryField.setPreferredSize(new Dimension(40, 10));
		}
		return queryField;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
