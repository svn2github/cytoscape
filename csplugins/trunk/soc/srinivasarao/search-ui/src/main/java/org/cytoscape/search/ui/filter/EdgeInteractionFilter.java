package org.cytoscape.search.ui.filter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

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
import org.cytoscape.search.ui.SearchComboBox;
import org.cytoscape.search.ui.tasks.SelectUtils;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;

public class EdgeInteractionFilter extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel filterLabel = null;
	private JPanel filterPanel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JCheckBox sourceBox = null;
	private JCheckBox targetBox = null;
	private JLabel jLabel3 = null;
	private SearchComboBox queryBox = null;
	private JTextField queryField = null;
	private JButton applyButton = null;
	private CyNetworkManager netmgr;

	/**
	 * This is the default constructor
	 */
	public EdgeInteractionFilter(CyNetworkManager nm) {

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
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridy = 1;
		gridBagConstraints7.gridx = 0;
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.insets = new Insets(5, 8, 5, 4);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 8, 5, 4);
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
		filterPanel.setVisible(false);
	}

	/**
	 * This method initializes filterPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 3;
			gridBagConstraints8.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints8.insets = new Insets(8, 0, 0, 0);

			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints6.insets = new Insets(0, 5, 0, 4);

			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.weightx = 1.0;
			jLabel3 = new JLabel();
			jLabel3.setText("which match the query");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 3;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;

			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			// gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridwidth = 2;
			// gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			jLabel2 = new JLabel();
			jLabel2.setText("with a node");

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;

			jLabel1 = new JLabel();
			jLabel1.setText("Select edges");
			filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			filterPanel.add(jLabel1, gridBagConstraints1);
			filterPanel.add(jLabel2, gridBagConstraints2);
			filterPanel.add(getSourceBox(), gridBagConstraints3);
			filterPanel.add(getTargetBox(), gridBagConstraints4);
			filterPanel.add(jLabel3, gridBagConstraints5);
			filterPanel.add(getQueryBox(), gridBagConstraints6);
			filterPanel.add(getApplyButton(), gridBagConstraints8);

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
	private SearchComboBox getQueryBox() {
		if (queryBox == null) {
			queryBox = new SearchComboBox(netmgr);
			queryField = (JTextField) queryBox.getEditor().getEditorComponent();
			/*
			 * queryField = new JTextField();
			 */
			queryField.setPreferredSize(new Dimension(50, 20));
		}
		return queryBox;
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
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean source = false;
					boolean target = false;
					if (sourceBox.isSelected())
						source = true;
					if (targetBox.isSelected())
						target = true;
					if (queryField.getText() != null
							&& queryField.getText() != "") {
						queryBox.addMenuItem(queryField.getText());
					}
					findEdges(source, target, queryField.getText());
				}
			});
		}
		return applyButton;
	}

	public void findEdges(boolean source, boolean target, String query) {
		System.out.println("Source:" + source);
		System.out.println("Target:" + target);
		System.out.println("Query:" + query);

		final EnhancedSearch es = new EnhancedSearchFactoryImpl()
				.getGlobalEnhancedSearchInstance();
		final CyNetwork network = netmgr.getCurrentNetwork();
		String status = es.getNetworkIndexStatus(network);
		RAMDirectory idx;

		CyNetworkView view = netmgr.getCurrentNetworkView();
		// To Unselect all Nodes and Edges
		SelectUtils.setSelectedNodes(network.getNodeList(), false);
		SelectUtils.setSelectedEdges(network.getEdgeList(), false);
		if (view != null) {
			view.updateView();
		}
		if (query == null || query.equals("")) {
			return;
		}

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

		ArrayList<CyNode> resultnodes = queryHandler.getNodeHits();
		List<CyEdge> edges = network.getEdgeList();
		ArrayList<CyEdge> resultedges = new ArrayList<CyEdge>();
		for (CyEdge e : edges) {
			CyNode src = e.getSource();
			CyNode tar = e.getTarget();
			if (e.isDirected()) {
				if (source) {
					if (resultnodes.contains(src)) {
						resultedges.add(e);
					}
				}
				if (target) {
					if (resultnodes.contains(tar)) {
						resultedges.add(e);
					}
				}
			} else {
				if (resultnodes.contains(src)) {
					resultedges.add(e);
				}
				if (resultnodes.contains(tar)) {
					resultedges.add(e);
				}
			}

		}

		SelectUtils.setSelectedEdges(resultedges, true);

		if (view != null) {
			view.updateView();
		}

	}

}
