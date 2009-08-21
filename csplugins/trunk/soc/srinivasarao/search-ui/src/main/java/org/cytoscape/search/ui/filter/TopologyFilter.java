package org.cytoscape.search.ui.filter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
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
	private JButton applyButton = null;

	private CyNetworkManager netmgr;

	/**
	 * This is the default constructor
	 */
	public TopologyFilter(CyNetworkManager nm) {
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
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1.0;
		gc.insets = new Insets(5, 8, 5, 4);
		GridBagConstraints gj = new GridBagConstraints();
		gj.gridx = 0;
		gj.gridy = 1;
		gj.weightx = 1.0;
		gj.fill = GridBagConstraints.HORIZONTAL;
		gj.insets = new Insets(5, 8, 0, 4);
		filterLabel = new JLabel();
		filterLabel.setText("TopologyFilter");

		filterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (jPanel.isVisible()) {
					jPanel.setVisible(false);
				} else {
					jPanel.setVisible(true);
				}
			}
		});
		this.setSize(311, 399);
		this.setLayout(new GridBagLayout());
		this.add(filterLabel, gc);
		this.add(getJPanel(), gj);
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.weightx = 0;
			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints.insets = new Insets(8, 0, 0, 0);
			// gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 3;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;

			jLabel4 = new JLabel();
			jLabel4.setText("neighbours");

			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridx = 2;
			gridBagConstraints7.insets = new Insets(0, 0, 3, 6);

			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.insets = new Insets(0, 0, 3, 6);

			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 3;
			gridBagConstraints5.weightx = 1.0;

			jLabel3 = new JLabel();
			jLabel3.setText("and matching the query");

			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(0, 0, 3, 0);
			jLabel2 = new JLabel();
			jLabel2.setText("within distance");

			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(0, 0, 3, 6);

			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.insets = new Insets(0, 0, 3, 0);
			jLabel1 = new JLabel();
			jLabel1.setText("with atleast");

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(0, 0, 3, 0);
			// gridBagConstraints1.weightx = 1.0;

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
			jPanel.add(getApplyButton(), gridBagConstraints);
			jPanel.setVisible(false);
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
			neighbourField.setPreferredSize(new Dimension(15, 20));
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
			queryField.setPreferredSize(new Dimension(40, 20));
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
			distanceField.setPreferredSize(new Dimension(15, 20));
		}
		return distanceField;
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
					performTopologySearch(neighbourField.getText(),
							distanceField.getText(), queryField.getText());
				}
			});
		}
		return applyButton;
	}

	public void performTopologySearch(String numOfNeighbours,
			String withinDist, String query) {
		System.out.println("Neighbours:" + numOfNeighbours);
		System.out.println("Distance:" + withinDist);
		System.out.println("Query:" + query);
		final CyNetwork network = netmgr.getCurrentNetwork();
		// To Unselect all Nodes and Edges
		SelectUtils.setSelectedNodes(network.getNodeList(), false);
		SelectUtils.setSelectedEdges(network.getEdgeList(), false);
		CyNetworkView view = netmgr.getCurrentNetworkView();
		if (view != null) {
			view.updateView();
		}
		if (query == null || numOfNeighbours == null || withinDist == null) {
			return;
		}
		if (query.equals("") || numOfNeighbours.equals("")
				|| withinDist.equals("")) {
			return;
		}

		// HashSet neighbourSet = new HashSet();
		ArrayList<CyNode> neighbourList = new ArrayList<CyNode>();
		int noOfNeighbours = Integer.parseInt(numOfNeighbours);
		int distance = Integer.parseInt(withinDist);
		EnhancedSearchFactoryImpl esf = new EnhancedSearchFactoryImpl();
		EnhancedSearch es = esf.getGlobalEnhancedSearchInstance();

		String status = es.getNetworkIndexStatus(network);
		RAMDirectory idx = null;

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

		// ArrayList<CyEdge> edgelist = queryHandler.getEdgeHits();
		ArrayList<CyNode> resultNodes = queryHandler.getNodeHits();
		for (CyNode n : resultNodes) {
			getNeighbours(n, neighbourList, distance, network);

			if (neighbourList.contains(n)) {
				int index = neighbourList.indexOf(n);
				neighbourList.remove(index);
			}
			// System.out.println(neighbourList.size());
			if (neighbourList.size() >= noOfNeighbours) {
				// SelectUtils.setSelectedNodes(network.getNodeList(), false);
				// SelectUtils.setSelectedEdges(network.getEdgeList(), false);

				SelectUtils.setSelectedNodes(neighbourList, true);
				if (view != null)
					view.updateView();
			}
		}

	}

	private void getNeighbours(CyNode n, ArrayList<CyNode> neighbourList,
			int distance, CyNetwork network) {
		if (distance == 0) {
			if (!neighbourList.contains(n)) {
				neighbourList.add(n);
			}
			return;
		}

		List<CyNode> neighbours = network.getNeighborList(n, CyEdge.Type.ANY);
		Iterator<CyNode> nodeIt = neighbours.iterator();
		while (nodeIt.hasNext()) {
			CyNode nextNode = (CyNode) nodeIt.next();
			if (!neighbourList.contains(nextNode)) {
				neighbourList.add(nextNode);
			}
			getNeighbours(nextNode, neighbourList, (distance - 1), network);
		}
	}

}
