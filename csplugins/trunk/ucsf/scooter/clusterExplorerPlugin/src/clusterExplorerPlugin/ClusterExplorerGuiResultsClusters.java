package clusterExplorerPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.actions.GinyUtils;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;


public class ClusterExplorerGuiResultsClusters extends JPanel implements ListSelectionListener, ActionListener {
	
	private JTable table;
	
	private Vector<ClusterElementSimilarity> ces;
	private Vector<ClusterClusterSimilarity> ccs;
	
	private Clusters clusters;
	
	private boolean ceResults = true;
	
	public ClusterExplorerGuiResultsClusters(Clusters clusters, Vector v, boolean ceResults) {
		
		this.ceResults = ceResults;
		
		if (ceResults) {
			this.ces = v;
		} else {
			this.ccs = v;
		}
		
		this.clusters = clusters;
		
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		JButton clearButton = new JButton("Destroy results");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);
		JPanel clearDummyPanel = new JPanel();
		clearDummyPanel.add(clearButton);
		
		createTable();
		
		JScrollPane scrollpane = new JScrollPane(table);
			
		this.add(scrollpane);
		this.add(clearDummyPanel);
		
	}

	private void createTable() {
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Cluster");
		columnNames.add("Mean weight");
		
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		
		if (this.ceResults) {
			for (int i = 0; i < this.ces.size(); i++) {
				
				ClusterElementSimilarity entry = ces.get(i);
				
				Vector<String> row = makeRow(entry);
				data.add(row);
				
			}
		} else {
			for (int i = 0; i < this.ccs.size(); i++) {
				
				ClusterClusterSimilarity entry = ccs.get(i);
				
				Vector<String> row = makeRow(entry);
				data.add(row);
				
			}
		}
		
		
		this.table = new JTable(data, columnNames);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		ListSelectionModel m = this.table.getSelectionModel();
        m.addListSelectionListener(this);
        
	}
	
	private Vector<String> makeRow(ClusterElementSimilarity entry) {
		
		String sim = ""+entry.sim;
		String clusterID = entry.cluster.getID();
		
		Vector<String> row = new Vector<String>();
		row.add(clusterID);
		row.add(sim);
		
		return row;
	}
	
	private Vector<String> makeRow(ClusterClusterSimilarity entry) {
		
		String sim = ""+entry.sim;
		String clusterID = entry.targetCluster.getID();
		
		Vector<String> row = new Vector<String>();
		row.add(clusterID);
		row.add(sim);
		
		return row;
	}

	private void gotoSelectedNode(int index) {
		
		Cluster c;
		if (this.ceResults) {
			ClusterElementSimilarity element = ces.get(index);
			c = element.cluster;
		} else {
			ClusterClusterSimilarity element = ccs.get(index);
			c = element.targetCluster;
		}
		
		Vector<Integer> ids = c.getElements();
		Vector<CyNode> nodes = new Vector<CyNode>();
		
		for (int i = 0; i < ids.size(); i++) {
			String nodeID = c.getMapping().getID(ids.get(i));
			CyNode node = Cytoscape.getCyNode(nodeID, false);
			nodes.add(node);
		}
		
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		GinyUtils.deselectAllEdges(networkView);
		GinyUtils.deselectAllNodes(networkView);
		
		network.setSelectedNodeState(nodes, true);
		
	}
	
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		int selectedRow = lsm.getMinSelectionIndex();
		gotoSelectedNode(selectedRow);
	}

	public void actionPerformed(ActionEvent e) {
		
		String c = e.getActionCommand();
		
		if (c.equalsIgnoreCase("clear")) {
			
			CytoscapeDesktop desktop = Cytoscape.getDesktop();
            CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
            
            cytoPanel.remove(this);
            if (cytoPanel.getCytoPanelComponentCount() == 0) {
            	cytoPanel.setState(CytoPanelState.HIDE);
            }
			
		}
		
	}
	
}
