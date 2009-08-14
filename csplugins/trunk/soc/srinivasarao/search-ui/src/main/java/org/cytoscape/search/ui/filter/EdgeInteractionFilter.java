package org.cytoscape.search.ui.filter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EdgeInteractionFilter extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel filterLabel = null;
	private JPanel filterPanel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JCheckBox sourceBox = null;
	private JCheckBox targetBox = null;
	private JLabel jLabel3 = null;
	private JTextField queryField = null;

	/**
	 * This is the default constructor
	 */
	public EdgeInteractionFilter() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridy = 1;
		gridBagConstraints7.gridx = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		filterLabel = new JLabel();
		filterLabel.setText("Edge Interaction Filter");
		filterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (filterPanel.isVisible()) {
					filterPanel.setVisible(false);
				} else {
					filterPanel.setVisible(true);
				}
			}
		});
		this.setSize(373, 239);
		this.setLayout(new GridBagLayout());
		this.add(filterLabel, gridBagConstraints);
		this.add(getFilterPanel(), gridBagConstraints7);
	}

	/**
	 * This method initializes filterPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridx = 2;

			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.gridwidth = 2;
			jLabel3 = new JLabel();
			jLabel3.setText("which match the query");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 3;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 1;
			jLabel2 = new JLabel();
			jLabel2.setText("with a node");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			jLabel1 = new JLabel();
			jLabel1.setText("Select nodes");
			filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			filterPanel.add(jLabel1, gridBagConstraints1);
			filterPanel.add(jLabel2, gridBagConstraints2);
			filterPanel.add(getSourceBox(), gridBagConstraints3);
			filterPanel.add(getTargetBox(), gridBagConstraints4);
			filterPanel.add(jLabel3, gridBagConstraints5);
			filterPanel.add(getQueryField(), gridBagConstraints6);
			filterPanel.setVisible(false);
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
