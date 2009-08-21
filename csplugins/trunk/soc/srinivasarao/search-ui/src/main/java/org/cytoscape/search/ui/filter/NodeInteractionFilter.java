package org.cytoscape.search.ui.filter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.search.EnhancedSearch;
import org.cytoscape.search.EnhancedSearchIndex;
import org.cytoscape.search.EnhancedSearchQuery;
import org.cytoscape.search.internal.EnhancedSearchFactoryImpl;
import org.cytoscape.search.internal.EnhancedSearchIndexImpl;
import org.cytoscape.search.internal.EnhancedSearchQueryImpl;
import org.cytoscape.search.ui.tasks.SelectUtils;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;

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
	private JButton applyButton = null;
	private CyNetworkManager netmgr;

	/**
	 * This is the default constructor
	 */
	public NodeInteractionFilter(CyNetworkManager nm) {

		super();
		this.netmgr = nm;
		initialize();

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.insets = new Insets(5, 8, 5, 4);

		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.insets = new Insets(5, 8, 5, 4);
		gridBagConstraints1.weightx = 1.0;
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

			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridy = 4;
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints13.weightx = 0;
			gridBagConstraints13.insets = new Insets(8, 0, 0, 0);

			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.BOTH;
			gridBagConstraints12.gridy = 3;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.gridx = 3;

			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.gridwidth = 3;
			gridBagConstraints11.weightx = 1.0;
			jLabel4 = new JLabel();
			jLabel4.setText("and which match the query");

			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 2;
			gridBagConstraints10.weightx = 1.0;

			jLabel3 = new JLabel();
			jLabel3.setText("of atleast one edge");

			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 3;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.weightx = 1.0;

			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;

			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.weightx = 1.0;
			jLabel2 = new JLabel();
			jLabel2.setText("which are the");

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;

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
			filterPanel.add(getApplyButton(), gridBagConstraints13);
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
			queryField.setPreferredSize(new Dimension(40, 20));
		}
		return queryField;
	}

	/**
	 * This method initializes applyButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			applyButton = new JButton();
			applyButton.setText("Apply");
			// applyButton.setPreferredSize(new Dimension(100,25));
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean source = false;
					boolean target = false;
					if (sourceBox.isSelected())
						source = true;
					if (targetBox.isSelected())
						target = true;
					findNodes(source, target, queryField.getText());
				}
			});
		}
		return applyButton;
	}

	public void findNodes(boolean source, boolean target, String query) {
		System.out.println("Source:" + source);
		System.out.println("Target:" + target);
		System.out.println("Query:" + query);
		final EnhancedSearch es = new EnhancedSearchFactoryImpl()
				.getGlobalEnhancedSearchInstance();
		final CyNetwork network = netmgr.getCurrentNetwork();
		String status = es.getNetworkIndexStatus(network);
		RAMDirectory idx;
		
		CyNetworkView view = netmgr.getCurrentNetworkView();
		// To unselect all nodes and Edges
		SelectUtils.setSelectedNodes(network.getNodeList(), false);
		SelectUtils.setSelectedEdges(network.getEdgeList(), false);
		if (view != null)
			view.updateView();
		if(query==null || query.equals(""))
			return;
		
		if (status == EnhancedSearch.INDEX_SET) {
			idx = es.getNetworkIndex(network);
		} else {
			EnhancedSearchIndex indexHandler = new EnhancedSearchIndexImpl(
					network);
			idx = indexHandler.getIndex();
			es.setNetworkIndex(network, idx);

		}
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,
				network);
		queryHandler.executeQuery(query);
		ArrayList<CyEdge> edgelist = queryHandler.getEdgeHits();
		ArrayList<CyNode> nodes = new ArrayList<CyNode>();
		for (CyEdge e : edgelist) {
			if (e.isDirected()) {
				if (source) {
					nodes.add(e.getSource());
				}
				if (target) {
					nodes.add(e.getTarget());
				}
			} else {
				nodes.add(e.getSource());
				nodes.add(e.getTarget());
			}
		}
		
		
		
		SelectUtils.setSelectedNodes(nodes, true);
		
		if (view != null)
			view.updateView();
	}
}
