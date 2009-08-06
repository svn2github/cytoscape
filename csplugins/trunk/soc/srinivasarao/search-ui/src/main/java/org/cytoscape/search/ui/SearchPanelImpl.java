package org.cytoscape.search.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.EnhancedSearch;
import org.cytoscape.search.internal.EnhancedSearchFactoryImpl;
import org.cytoscape.search.util.AttributeTypes;
import org.cytoscape.session.CyNetworkManager;

import cytoscape.Cytoscape;

public class SearchPanelImpl extends SearchPanel implements
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private CyNetworkManager netmgr = null;
	private MainPanel mp;
	private RootPanel attrPanel = null;
	private JScrollPane jsp = null;
	private JSplitPane split = null;
	private String[] nodeattrList;
	private String[] edgeattrList;

	/**
	 * This is the default constructor
	 */
	public SearchPanelImpl(CyNetworkManager nm) {
		super();
		this.netmgr = nm;
		initialize();
		initListeners();
	}

	private void initListeners() {
		System.out.println("I am init listeners");
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		NetworkModifiedListener nml = new NetworkModifiedListener();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(nml);
		System.out.println("Listeners are initiated");
		if (Cytoscape.getSwingPropertyChangeSupport()
				.getPropertyChangeListeners() != null) {
			System.out.println("Number of listeners for Network Creation:"
					+ Cytoscape.getSwingPropertyChangeSupport()
							.getPropertyChangeListeners().length);
			if (Cytoscape.getSwingPropertyChangeSupport()
					.getPropertyChangeListeners()[0].equals(this)) {
				System.out.println("Yeah");
			}
			if (Cytoscape.getPropertyChangeSupport()
					.getPropertyChangeListeners()[0].equals(nml)) {
				System.out.println("YahooooooooooOO!!!!!!!!!!!!");
			}
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(333, 415);

		this.setLayout(new GridBagLayout());
		GridBagConstraints g1 = new GridBagConstraints();
		g1.fill = GridBagConstraints.BOTH;
		g1.gridx = 0;
		g1.gridy = 0;
		g1.weightx = 1.0;
		g1.weighty = 1.0;
		g1.anchor = GridBagConstraints.NORTHWEST;
		mp = new MainPanel(netmgr);
		attrPanel = new RootPanel(netmgr);
		CyNetwork net = netmgr.getCurrentNetwork();
		if (net != null) {
			CyDataTable nodetable = net.getCyDataTables("NODE").get(
					CyNetwork.DEFAULT_ATTRS);
			Map<String, Class<?>> nodetypemap = nodetable.getColumnTypeMap();
			Set<String> keys = nodetypemap.keySet();
			Object[] arr = keys.toArray();
			nodeattrList = new String[arr.length];
			for (int i = 0; i < arr.length; i++) {
				nodeattrList[i] = arr[i].toString();
			}
			Arrays.sort(nodeattrList);
			for (int i = 0; i < nodeattrList.length; i++) {
				if (!nodeattrList[i].equals("name")) {
					if (nodetypemap.get(nodeattrList[i]).getName() == AttributeTypes.TYPE_STRING) {
						StringAttributePanel temp = new StringAttributePanel(
								netmgr, nodeattrList[i], "NODE");
						attrPanel.addPanel(temp);
					} else if (nodetypemap.get(nodeattrList[i]).getName() == AttributeTypes.TYPE_INTEGER
							|| nodetypemap.get(nodeattrList[i]).getName() == AttributeTypes.TYPE_DOUBLE) {
						NumericAttributePanel temp = new NumericAttributePanel(
								netmgr, nodeattrList[i], "NODE", nodetypemap
										.get(nodeattrList[i]).getName());
						attrPanel.addPanel(temp);
					}
				}
			}
			CyDataTable edgetable = net.getCyDataTables("EDGE").get(
					CyNetwork.DEFAULT_ATTRS);
			Map<String, Class<?>> edgetypemap = edgetable.getColumnTypeMap();
			keys = edgetypemap.keySet();
			Object[] edgearr = keys.toArray();
			edgeattrList = new String[edgearr.length];
			for (int i = 0; i < edgearr.length; i++) {
				edgeattrList[i] = edgearr[i].toString();
			}
			Arrays.sort(edgeattrList);
			for (int i = 0; i < edgeattrList.length; i++) {
				if (!edgeattrList[i].equals("name")) {
					if (edgetypemap.get(edgeattrList[i]).getName() == AttributeTypes.TYPE_STRING) {
						StringAttributePanel temp = new StringAttributePanel(
								netmgr, edgeattrList[i], "EDGE");
						attrPanel.addPanel(temp);
					} else if (edgetypemap.get(edgeattrList[i]).getName() == AttributeTypes.TYPE_INTEGER
							|| edgetypemap.get(edgeattrList[i]).getName() == AttributeTypes.TYPE_DOUBLE) {
						NumericAttributePanel temp = new NumericAttributePanel(
								netmgr, edgeattrList[i], "EDGE", edgetypemap
										.get(edgeattrList[i]).getName());
						attrPanel.addPanel(temp);
					}
				}
			}
		}

		jsp = new JScrollPane(attrPanel);
		split = new JSplitPane();
		split.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(mp);
		split.setBottomComponent(jsp);
		split.setMinimumSize(new Dimension(280, 200));
		this.add(split, g1);

	}

	public void performSearch(boolean reindex) {
		String query = mp.getQuery();
		System.out.println(query);
		if (query.length() > 0) {
			final CyNetwork currNetwork = netmgr.getCurrentNetwork();
			// Mark the network for reindexing, if requested
			if (reindex) {
				final EnhancedSearch enhancedSearch = new EnhancedSearchFactoryImpl()
						.getGlobalEnhancedSearchInstance();
				enhancedSearch.setNetworkIndexStatus(currNetwork,
						EnhancedSearch.REINDEX);
			}

			// Define a new IndexAndSearchTask
			IndexAndSearchTaskImpl task = new IndexAndSearchTaskImpl(
					currNetwork, query);
			task.run();
			// Execute the task via the task manager
			// tm.execute(task);
		}
	}

	public void updateSearchField() {
		String query = null;
		String operator = mp.getOperator();
		List<BasicDraggablePanel> list = attrPanel.getPanelList();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof StringAttributePanel) {
				StringAttributePanel sp = (StringAttributePanel) list.get(i);
				if (sp != null) {
					if (query == null) {
						if (sp.getCheckedValues() != null)
							query = sp.getCheckedValues();

					} else {
						if (sp.getCheckedValues() != null) {
							query = query + " " + operator + " "
									+ sp.getCheckedValues();
						}
					}
				}
			}

			else if (list.get(i) instanceof NumericAttributePanel) {
				NumericAttributePanel np = (NumericAttributePanel) list.get(i);
				if (np != null) {
					if (query == null) {
						if (np.getQueryFromBox() != null)
							query = np.getQueryFromBox();
						else if (np.getQueryFromBox() == null) {
							if (np.rangeQuery() != null)
								query = np.rangeQuery();
						}

					} else {
						if (np.getQueryFromBox() != null) {
							query = query + " " + operator + " "
									+ np.getQueryFromBox();
						} else if (np.getQueryFromBox() == null) {
							if (np.rangeQuery() != null)
								query = query + " " + operator + " "
										+ np.rangeQuery();
						}
					}
				}
			}
		}
		System.out.println(query);
		mp.setSearchText(query);
	}

	public void clearAll() {
		List<BasicDraggablePanel> list = attrPanel.getPanelList();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof StringAttributePanel) {
				StringAttributePanel sp = (StringAttributePanel) list.get(i);
				if (sp != null) {
					sp.clearCheckBoxes();
				}
			}

			else if (list.get(i) instanceof NumericAttributePanel) {
				NumericAttributePanel np = (NumericAttributePanel) list.get(i);
				if (np != null) {
					np.clearAll();
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		System.out.println("I am in property change listener");
		if (event.getPropertyName() != null) {
			String propertyname = event.getPropertyName();
			if (propertyname.equals(Cytoscape.NETWORK_CREATED)) {
				System.out.println("Network Created");
			} else if (propertyname.equals(Cytoscape.NETWORK_LOADED)) {
				System.out.println("Network Loaded");
			} else if (propertyname.equals(Cytoscape.NETWORK_MODIFIED)) {
				System.out.println("Network Modified");
			}
		}
	}
}

class NetworkModifiedListener implements PropertyChangeListener {

	public NetworkModifiedListener() {
		System.out.println("I am in Network Modified Listener Initialization");
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		System.out.println("I am in Network Modified Listener");
	}

}
