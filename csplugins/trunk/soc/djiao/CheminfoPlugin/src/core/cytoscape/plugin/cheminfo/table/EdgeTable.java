package cytoscape.plugin.cheminfo.table;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.giny.CytoscapeFingRootGraph;
import cytoscape.plugin.cheminfo.ChemInfoPlugin.AttriType;
import cytoscape.view.CyNetworkView;

public class EdgeTable extends ChemTable implements ChangeListener {
	private static String[] popupItems = {};
	
	public EdgeTable(ChemTableModel model, String networkID, String attribute, AttriType attrType) {
		super(model, networkID, attribute, attrType);
	}

	@Override
	protected void removeFromTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setupPopup() {
		// TODO Auto-generated method stub
		
	}

	public void onSelectEvent(SelectEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		
		DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e
				.getSource();

		CyNetworkView nview = Cytoscape.getNetworkView(this.networkID);
		CytoscapeFingRootGraph graph = (CytoscapeFingRootGraph)nview.getRootGraph();
		
		if (!selectionModel.getValueIsAdjusting()) {
			List values = ((ChemTableModel) getModel()).getRecords();
			List<CyEdge> updateEdges = new ArrayList<CyEdge>();
			List<CyNode> updateNodes = new ArrayList<CyNode>();
			List<CyEdge> highlightEdges = new ArrayList<CyEdge>();
			List<CyNode> highlightNodes = new ArrayList<CyNode>();
			int first = e.getFirstIndex();
			if (first != -1) {
				int last = e.getLastIndex();
				for (int i = first; i <= last; i++) {
					List record = (List)values.get(i);
					CyEdge edge = graph.getEdge((String)record.get(0));
					CyNode node1 = graph.getNode((String)record.get(1));
					CyNode node2 = graph.getNode((String)record.get(2));
					if (selectionModel.isSelectedIndex(i)) {
						highlightEdges.add(edge);
						if (!highlightNodes.contains(node1)) {
							highlightNodes.add(node1);
						}
						if (!highlightNodes.contains(node2)) {
							highlightNodes.add(node2);
						}
					}
					updateEdges.add(edge);
					if (!updateNodes.contains(node1)) {
						updateNodes.add(node1);
					}
					if (!updateNodes.contains(node2)) {
						updateNodes.add(node2);
					}					
				}
				highlightCytoscape(highlightEdges, updateEdges);
			}
		}
	}
	
	/**
	 * Select a group of nodes in Cytoscape. If a node is already selected,
	 * change the color to indicate a second-level selection.
	 * 
	 */
	public void highlightCytoscape(List<CyEdge> highlightEdges, List<CyEdge> updateEdges) {
		CyNetworkView networkView = Cytoscape.getNetworkView(networkID);
		
		CyNetwork network = Cytoscape.getNetwork(networkID);
		
		List highlightNodes = new ArrayList();
		//network.unselectAllNodes();
		for (CyEdge edge: updateEdges) {
			EdgeView edgeView = networkView.getEdgeView(edge);
			CyNode node1 = (CyNode)edge.getSource();
			CyNode node2 = (CyNode)edge.getTarget();
			NodeView nodeView1 = networkView.getNodeView(node1);
			NodeView nodeView2 = networkView.getNodeView(node2);
			
			if (highlightEdges.contains(edge)) {
				edgeView.setSelectedPaint(java.awt.Color.CYAN);
				nodeView1.setSelectedPaint(java.awt.Color.CYAN);
				nodeView2.setSelectedPaint(java.awt.Color.CYAN);
				if (!highlightNodes.contains(nodeView1.getNode().getIdentifier())) {
					highlightNodes.add(nodeView1.getNode().getIdentifier());
				}
				if (!highlightNodes.contains(nodeView2.getNode().getIdentifier())) {
					highlightNodes.add(nodeView2.getNode().getIdentifier());
				}

			} else {
				edgeView.setSelectedPaint(java.awt.Color.RED);
				if (!highlightNodes.contains(nodeView1.getNode().getIdentifier())) {
					nodeView1.setSelectedPaint(java.awt.Color.YELLOW);
				}
				if (!highlightNodes.contains(nodeView2.getNode().getIdentifier())) {
					nodeView2.setSelectedPaint(java.awt.Color.YELLOW);
				}
			}
		}

		networkView.updateView();
	}		
	
	@Override
	public void showTableDialog(String title)  {
		ChemTableDialog dialog = new ChemTableDialog();
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.setTitle(title);
		dialog.setModal(false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
        JScrollPane spane = new JScrollPane();
        spane.getViewport().add(this);
        dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(spane, BorderLayout.CENTER);
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(25);
		slider.setMinorTickSpacing(5);
		Hashtable labels = new Hashtable();
		for (int i = 0; i < 5; i++) {
			labels.put(i*25, new JLabel(String.valueOf(((double)i*25)/100.0)));
		}
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		
		dialog.getContentPane().add(slider, BorderLayout.NORTH);
		
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.pack();
		dialog.setVisible(true);
	}

	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        int fps = (int)source.getValue();
	        /*
	        if (fps == 0) {
	            if (!frozen) stopAnimation();
	        } else {
	            delay = 1000 / fps;
	            timer.setDelay(delay);
	            timer.setInitialDelay(delay * 10);
	            if (frozen) startAnimation();
	        }
	        */
	        filterTable(fps/100.0);
	    }

	}	
	
	public void filterTable(double threshold) {
		final ChemTableModel model = (ChemTableModel)this.getModel();
		List records = model.getRecords();
		List remove = new ArrayList();
		ListSelectionModel selection = this.getSelectionModel();
		selection.clearSelection();
		int i = 0; 
		
		for (Object object : records) {
			List record = (List)object;
			if ((Double)record.get(3) >= threshold) {
				selection.addSelectionInterval(i, i);
			}
			i++;
		}
	}

}
