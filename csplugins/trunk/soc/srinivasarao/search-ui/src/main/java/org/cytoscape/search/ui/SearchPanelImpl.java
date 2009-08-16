package org.cytoscape.search.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.EnhancedSearch;
import org.cytoscape.search.ReindexTask;
import org.cytoscape.search.internal.EnhancedSearchFactoryImpl;
import org.cytoscape.search.ui.tasks.IndexAndSearchTaskImpl;
import org.cytoscape.search.ui.tasks.ReindexTaskImpl;
import org.cytoscape.search.util.AttributeTypes;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.TaskManager;

public class SearchPanelImpl extends SearchPanel {

	private static final long serialVersionUID = 1L;
	private CyNetworkManager netmgr = null;
	private MainPanel mp;
	private RootPanel attrPanel = null;
	private JScrollPane jsp = null;
	private JSplitPane split = null;
	private String[] nodeattrList;
	private String[] edgeattrList;
	private TaskManager taskmanager;

	/**
	 * This is the default constructor
	 */
	public SearchPanelImpl(CyNetworkManager nm) {
		super();
		this.netmgr = nm;
		initialize();
	}

	public void setTaskManager(TaskManager tm) {
		this.taskmanager = tm;
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
		initattrPanel();
		jsp = new JScrollPane(attrPanel);
		split = new JSplitPane();
		split.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(mp);
		split.setBottomComponent(jsp);
		split.setMinimumSize(new Dimension(280, 200));
		this.add(split, g1);

	}

	public void initattrPanel() {

		CyNetwork net = netmgr.getCurrentNetwork();

		if (net != null) {

			List<CyEdge> edgelist = net.getEdgeList();
			for (CyEdge e : edgelist) {
				System.out.println("In SearchPanelImpl:"
						+ e.attrs().get("interaction", String.class));
				Map<String, Object> all = e.attrs().getAllValues();
				Iterator<Entry<String, Object>> it = all.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Object> en = it.next();
					System.out.println(en.getKey() + ":" + en.getValue());
				}

			}

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

				if (nodetypemap.get(nodeattrList[i]).getName() == AttributeTypes.TYPE_STRING) {
					StringAttributePanel temp = new StringAttributePanel(
							netmgr, nodeattrList[i], "NODE");
					attrPanel.addPanel(temp);
				} else if (nodetypemap.get(nodeattrList[i]).getName() == AttributeTypes.TYPE_INTEGER
						|| nodetypemap.get(nodeattrList[i]).getName() == AttributeTypes.TYPE_DOUBLE) {
					NumericAttributePanel temp = new NumericAttributePanel(
							netmgr, nodeattrList[i], "NODE", nodetypemap.get(
									nodeattrList[i]).getName());
					attrPanel.addPanel(temp);
				} else if (nodetypemap.get(nodeattrList[i]).getName() == AttributeTypes.TYPE_BOOLEAN) {
					BooleanAttributePanel temp = new BooleanAttributePanel(
							nodeattrList[i], netmgr, "NODE");
					attrPanel.addPanel(temp);
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

				if (edgetypemap.get(edgeattrList[i]).getName() == AttributeTypes.TYPE_STRING) {
					StringAttributePanel temp = new StringAttributePanel(
							netmgr, edgeattrList[i], "EDGE");
					attrPanel.addPanel(temp);
				} else if (edgetypemap.get(edgeattrList[i]).getName() == AttributeTypes.TYPE_INTEGER
						|| edgetypemap.get(edgeattrList[i]).getName() == AttributeTypes.TYPE_DOUBLE) {
					NumericAttributePanel temp = new NumericAttributePanel(
							netmgr, edgeattrList[i], "EDGE", edgetypemap.get(
									edgeattrList[i]).getName());
					attrPanel.addPanel(temp);
				} else if (edgetypemap.get(edgeattrList[i]).getName() == AttributeTypes.TYPE_BOOLEAN) {
					BooleanAttributePanel temp = new BooleanAttributePanel(
							edgeattrList[i], netmgr, "EDGE");
					attrPanel.addPanel(temp);
				}
			}
		}
	}

	public RootPanel getattrPanel() {
		return attrPanel;
	}

	public MainPanel getmainPanel() {
		return mp;
	}

	public void performSearch(boolean reindex) {
		String query = mp.getQuery();
		System.out.println("In Perform Search before changing" + query);

		// Handling queries of type #1 and #2 where 1 and 2 are queries from
		// history
		SearchComboBox box = mp.getSearchBox();
		Pattern p = Pattern.compile("#[0-9]+");
		Matcher m = p.matcher(query);
		while (m.find()) {
			String match = m.group();
			int num = new Integer(match.substring(1)).intValue();
			//System.out.println("Match:" + match);
			//System.out.println("Num:"+num);
			//System.out.println("History:" + box.getQueryAt(num));
			query = query.replaceAll(match, box.getQueryAt(num));
		}
		System.out.println("In Perform Search after changing" + query);

		if (query.length() > 0) {
			final CyNetwork currNetwork = netmgr.getCurrentNetwork();
			// Mark the network for reindexing, if requested
			if (reindex) {
				final EnhancedSearch enhancedSearch = new EnhancedSearchFactoryImpl()
						.getGlobalEnhancedSearchInstance();
				enhancedSearch.setNetworkIndexStatus(currNetwork,
						EnhancedSearch.REINDEX);
				ReindexTask task = new ReindexTaskImpl(netmgr);
				taskmanager.execute(task);

			}

			// Define a new IndexAndSearchTask
			IndexAndSearchTaskImpl task = new IndexAndSearchTaskImpl(netmgr,
					query);
			taskmanager.execute(task);
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

			else if (list.get(i) instanceof BooleanAttributePanel) {
				BooleanAttributePanel bp = (BooleanAttributePanel) list.get(i);
				if (bp != null) {
					if (query == null) {
						if (bp.getCheckedValues() != null) {
							query = bp.getCheckedValues();
						}
					} else {
						if (bp.getCheckedValues() != null) {
							query = query + " " + operator + " "
									+ bp.getCheckedValues();
						}
					}

				}
			}
		}
		System.out.println(query);
		mp.setSearchText(query);
	}

	public void clearAll() {
		
		mp.clearAll();
		List<BasicDraggablePanel> list = attrPanel.getPanelList();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof StringAttributePanel) {
				StringAttributePanel sp = (StringAttributePanel) list.get(i);
				if (sp != null) {
					sp.clearAll();
				}
			} else if (list.get(i) instanceof NumericAttributePanel) {
				NumericAttributePanel np = (NumericAttributePanel) list.get(i);
				if (np != null) {
					np.clearAll();
				}
			} else if (list.get(i) instanceof BooleanAttributePanel) {
				BooleanAttributePanel bp = (BooleanAttributePanel) list.get(i);
				if (bp != null) {
					bp.clearAll();
				}
			}
		}
	}

}
