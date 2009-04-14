package org.genmapp.genefinder;

import giny.filter.Filter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.view.CytoscapeDesktop;

public class FinderPanel extends JPanel implements PropertyChangeListener,
		ActionListener, SelectEventListener {
	public static int NODES = 0;
	public static int EDGES = 1;
	int graphObjectType;
	JComboBox networkBox;
	JComboBox filterBox;
	DataTableModel tableModel;
	Map titleIdMap;
	JCheckBox mirrorSelection;

	CyNetwork current_network;

	public FinderPanel(DataTableModel tableModel, int graphObjectType) {

		this.tableModel = tableModel;
		this.graphObjectType = graphObjectType;

		titleIdMap = new HashMap();
		networkBox = getNetworkBox();
		networkBox.setMaximumSize(new Dimension(15, (int) networkBox
				.getPreferredSize().getHeight()));

		// Filter is disabled for now...
		filterBox = new JComboBox(FilterManager.defaultManager()
				.getComboBoxModel());
		// filterBox = new JComboBox();

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);

		mirrorSelection = new JCheckBox();
		mirrorSelection.setSelected(true);

		setBorder(new TitledBorder("Finder"));
		add(new JLabel("Filter: "));
		add(filterBox);
		add(new JLabel("Network: "));
		add(networkBox);
		add(new JLabel("Mirror Network Selection"));
		add(mirrorSelection);

		filterBox.addActionListener(this);
		networkBox.addActionListener(this);

	}

	public void onFlagEvent(SelectEvent event) {
		if (mirrorSelection.isSelected()) {
			if (graphObjectType == NODES
					&& (event.getTargetType() == SelectEvent.SINGLE_NODE || event
							.getTargetType() == SelectEvent.NODE_SET)) {
				// node selection
				tableModel.setTableDataObjects(new ArrayList(Cytoscape
						.getCurrentNetwork().getFlaggedNodes()));
			} else if (graphObjectType == EDGES
					&& (event.getTargetType() == SelectEvent.SINGLE_EDGE || event
							.getTargetType() == SelectEvent.EDGE_SET)) {
				// edge selection
				tableModel.setTableDataObjects(new ArrayList(Cytoscape
						.getCurrentNetwork().getFlaggedEdges()));
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == filterBox) {
			Filter filter = (Filter) filterBox.getSelectedItem();
			System.out.println("Showing all that Pass Filter: " + filter);
			List list = new ArrayList(getGraphObjectCount());
			Iterator objs = getGraphObjectIterator();
			while (objs.hasNext()) {
				Object obj = objs.next();
				if (filter.passesFilter(obj))
					list.add(obj);
			}
			tableModel.setTableDataObjects(list);
		}

		if (e.getSource() == networkBox) {
			String network_id = (String) titleIdMap.get(networkBox
					.getSelectedItem());
			CyNetwork network = Cytoscape.getNetwork(network_id);
			System.out.println("Showing all that Pass Network: " + network);
			tableModel.setTableDataObjects(getGraphObjectList(network));
		}
	}

	private List getGraphObjectList(CyNetwork network) {
		if (graphObjectType == NODES)
			return network.nodesList();
		else
			return network.edgesList();
	}

	private Iterator getGraphObjectIterator() {
		if (graphObjectType == NODES)
			return Cytoscape.getRootGraph().nodesIterator();
		else
			return Cytoscape.getRootGraph().edgesIterator();
	}

	private int getGraphObjectCount() {
		if (graphObjectType == NODES)
			return Cytoscape.getRootGraph().getNodeCount();
		else
			return Cytoscape.getRootGraph().getEdgeCount();
	}

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equals(Cytoscape.NETWORK_CREATED)
				|| e.getPropertyName().equals(Cytoscape.NETWORK_DESTROYED)) {
			updateNetworkBox();
			tableModel.setTableDataObjects(new ArrayList());
		}

		else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED
				|| e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED
				|| e.getPropertyName() == Cytoscape.NETWORK_LOADED) {

			if (current_network != null) {
				current_network.removeFlagEventListener(this);
			}
			current_network = Cytoscape.getCurrentNetwork();
			if(current_network != null ) {
				current_network.addFlagEventListener(this);
			}
		
			if (graphObjectType == NODES) {
				// node selection
				tableModel.setTableDataObjects(new ArrayList(Cytoscape
						.getCurrentNetwork().getFlaggedNodes()));
			} else if (graphObjectType == EDGES) {
				// edge selection
				tableModel.setTableDataObjects(new ArrayList(Cytoscape
						.getCurrentNetwork().getFlaggedEdges()));
			}
		
		}

	}

	protected void updateNetworkBox() {
		Iterator i = Cytoscape.getNetworkSet().iterator();
		Vector vector = new Vector();
		while (i.hasNext()) {
			// System.out.println( i.next().getClass() );
			CyNetwork net = (CyNetwork) i.next();
			titleIdMap.put(net.getTitle(), net.getIdentifier());
			vector.add(net.getTitle());
		}
		DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
		networkBox.setModel(model);
	}

	protected JComboBox getNetworkBox() {
		Iterator i = Cytoscape.getNetworkSet().iterator();
		Vector vector = new Vector();
		while (i.hasNext()) {
			CyNetwork net = (CyNetwork) i.next();
			titleIdMap.put(net.getTitle(), net.getIdentifier());
			vector.add(net.getTitle());
		}
		DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
		return new JComboBox(model);
	}

}
