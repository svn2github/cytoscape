package org.cytoscape.search.internal;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.EnhancedSearch;
import org.cytoscape.search.util.AttributeTypes;
import org.cytoscape.session.CyNetworkManager;

public class SearchPanelImpl extends SearchPanel {

	private static final long serialVersionUID = 1L;
	private CyNetworkManager netmgr = null;
	private MainPanel mp;
	private HashMap<String, Object> attrMap = new HashMap<String, Object>(); // @jve:decl-index=0:
	Map<String, Class<?>> typemap;
	private JPanel attrPanel = null;
	private JScrollPane jsp = null;
	private JSplitPane split = null;
	private String[] attrList;

	/**
	 * This is the default constructor
	 */
	public SearchPanelImpl(CyNetworkManager nm) {
		super();
		this.netmgr = nm;
		if (netmgr.getCurrentNetwork() != null)
			initialize();
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
		attrPanel = new JPanel();
		attrPanel.setLayout(new GridBagLayout());
		CyNetwork net = netmgr.getCurrentNetwork();
		CyDataTable nodetable = net.getCyDataTables("NODE").get(
				CyNetwork.DEFAULT_ATTRS);
		typemap = nodetable.getColumnTypeMap();
		Set<String> keys = typemap.keySet();
		Object[] arr = keys.toArray();
		attrList = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			attrList[i] = arr[i].toString();
		}
		Arrays.sort(attrList);
		for (int i = 0; i < attrList.length; i++) {

			GridBagConstraints gc = new GridBagConstraints();
			gc.gridx = 0;
			gc.gridy = i;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.weightx = 1.0;
			gc.insets = new Insets(0, 0, 10, 0);
			gc.anchor = GridBagConstraints.FIRST_LINE_START;
			if (typemap.get(attrList[i]).getName() == AttributeTypes.TYPE_STRING
					&& !attrList[i].equals("name")) {
				StringAttributePanel temp = new StringAttributePanel(netmgr,
						attrList[i], "NODE");
				attrMap.put(attrList[i], temp);
				attrPanel.add(temp, gc);
			}
		}
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = attrList.length;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		attrPanel.add(Box.createRigidArea(null), gc);
		// attrPanel.setBorder(new LineBorder(Color.BLUE));
		jsp = new JScrollPane(attrPanel);
		// jsp.setBorder(new LineBorder(Color.RED));
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mp, jsp);
		split.setMinimumSize(new Dimension(280, 200));
		this.add(split, g1);
		// this.add(split);
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
		for (int i = 0; i < attrList.length; i++) {
			if (typemap.get(attrList[i]).getName() == AttributeTypes.TYPE_STRING) {
				StringAttributePanel sp = (StringAttributePanel) attrMap
						.get(attrList[i]);
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
		}
		System.out.println(query);
		mp.setSearchText(query);
	}
	
	public void clearAll(){
		for (int i = 0; i < attrList.length; i++) {
			if (typemap.get(attrList[i]).getName() == AttributeTypes.TYPE_STRING) {
				StringAttributePanel sp = (StringAttributePanel) attrMap
						.get(attrList[i]);
				if (sp != null) {
						sp.clearCheckBoxes();
				}
			}
		}
	}
	
}
