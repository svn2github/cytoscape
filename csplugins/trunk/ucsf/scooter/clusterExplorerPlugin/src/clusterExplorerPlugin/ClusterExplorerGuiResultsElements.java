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


public class ClusterExplorerGuiResultsElements extends JPanel implements ListSelectionListener, ActionListener {
	
	private JTable table;
	
	private Vector<ClusterElementSimilarity> ces;
	private Vector<ElementElementSimilarity> ees;
	
	private boolean ceResults = true;
	
	private Clusters clusters;
	
	public ClusterExplorerGuiResultsElements(Clusters clusters, Vector v, boolean ceResults) {
		
		this.ceResults = ceResults;
		
		if (ceResults) {
			this.ces = v;
		} else {
			this.ees = v;
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
		columnNames.add("Element");
		columnNames.add("Mean weight");
		columnNames.add("Cluster");
		
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		
		if (this.ceResults) {
			for (int i = 0; i < this.ces.size(); i++) {
				
				ClusterElementSimilarity entry = ces.get(i);
				
				Vector<String> row = makeRow(entry);
				data.add(row);
				
			}
		} else {
			for (int i = 0; i < this.ees.size(); i++) {
				
				ElementElementSimilarity entry = ees.get(i);
				
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
		
		String elementID = this.clusters.getMapping().getID(entry.element);
		String sim = ""+entry.sim;
		String clusterID = entry.cluster.getID();
		
		Vector<String> row = new Vector<String>();
		row.add(elementID);
		row.add(sim);
		row.add(clusterID);
		
		return row;
	}
	
	private Vector<String> makeRow(ElementElementSimilarity entry) {
		
		String elementID = this.clusters.getMapping().getID(entry.targetElement);
		String sim = ""+entry.sim;
		String clusterID = entry.targetCluster.getID();
		
		Vector<String> row = new Vector<String>();
		row.add(elementID);
		row.add(sim);
		row.add(clusterID);
		
		return row;
	}

	private void gotoSelectedNode(int index) {
		
		String nodeID;
		if (this.ceResults) {
			ClusterElementSimilarity element = ces.get(index);
			nodeID = this.clusters.getMapping().getID(element.element);
		} else {
			ElementElementSimilarity element = ees.get(index);
			nodeID = this.clusters.getMapping().getID(element.targetElement);
		}
		
		CyNode node = Cytoscape.getCyNode(nodeID, false);
		
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		GinyUtils.deselectAllEdges(networkView);
		GinyUtils.deselectAllNodes(networkView);
		
		network.setSelectedNodeState(node, true);
		
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
