package org.cytoscape.search.ui.filter;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import javax.swing.JTextField;

public class TopologyFilter extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel filterLabel = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JTextField neighbourField = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField queryField = null;
	private JTextField distanceField = null;
	private JLabel jLabel4 = null;

	/**
	 * This is the default constructor
	 */
	public TopologyFilter() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		filterLabel = new JLabel();
		filterLabel.setText("TopologyFilter");
		filterLabel.setBounds(new Rectangle(17, 22, 257, 47));
		this.setSize(462, 383);
		this.setLayout(null);
		this.add(filterLabel, null);
		this.add(getJPanel(), null);
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 3;
			gridBagConstraints8.gridy = 1;
			jLabel4 = new JLabel();
			jLabel4.setText("neighbours");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridx = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridx = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("and which match the query");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 2;
			jLabel2 = new JLabel();
			jLabel2.setText("within distance");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("with atleast");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Select nodes");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBounds(new Rectangle(18, 82, 439, 265));
			jPanel.add(jLabel, gridBagConstraints1);
			jPanel.add(jLabel1, gridBagConstraints2);
			jPanel.add(getNeighbourField(), gridBagConstraints3);
			jPanel.add(jLabel2, gridBagConstraints4);
			jPanel.add(jLabel3, gridBagConstraints5);
			jPanel.add(getQueryField(), gridBagConstraints6);
			jPanel.add(getDistanceField(), gridBagConstraints7);
			jPanel.add(jLabel4, gridBagConstraints8);
		}
		return jPanel;
	}

	/**
	 * This method initializes neighbourField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNeighbourField() {
		if (neighbourField == null) {
			neighbourField = new JTextField();
		}
		return neighbourField;
	}

	/**
	 * This method initializes queryField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getQueryField() {
		if (queryField == null) {
			queryField = new JTextField();
		}
		return queryField;
	}

	/**
	 * This method initializes distanceField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDistanceField() {
		if (distanceField == null) {
			distanceField = new JTextField();
		}
		return distanceField;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
