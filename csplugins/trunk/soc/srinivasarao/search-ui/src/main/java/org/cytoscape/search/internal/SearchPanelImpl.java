package org.cytoscape.search.internal;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.EnhancedSearch;
import org.cytoscape.session.CyNetworkManager;

public class SearchPanelImpl extends SearchPanel {

	private static final long serialVersionUID = 1L;
	private CyNetworkManager netmgr = null;
	private MainPanel mp;
	private ArrayList<StringAttributePanel> sap = null;
	private JPanel attrPanel = null;
	private JScrollPane jsp = null;
	private JSplitPane split = null;
	
	
	/**
	 * This is the default constructor
	 */
	public SearchPanelImpl(CyNetworkManager nm) {
		super();
		this.netmgr = nm;
		if(netmgr.getCurrentNetwork()!=null)
			initialize();	
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(333, 415);
		
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setLayout(new GridBagLayout());
		GridBagConstraints g1 = new GridBagConstraints();
		g1.fill=GridBagConstraints.BOTH;
		g1.gridx=0;
		g1.gridy=0;
		g1.weightx = 1.0;
		g1.weighty = 1.0;
		g1.anchor = GridBagConstraints.NORTHWEST;
		
		mp = new MainPanel(netmgr);
		attrPanel = new JPanel();
		//attrPanel.setLayout(new BoxLayout(attrPanel, BoxLayout.Y_AXIS));
		attrPanel.setLayout(new GridBagLayout());
		CyNetwork net = netmgr.getCurrentNetwork();
		CyDataTable nodetable = net.getCyDataTables("NODE").get(CyNetwork.DEFAULT_ATTRS);
		List<String> l = nodetable.getUniqueColumns();
		sap = new ArrayList<StringAttributePanel>();
		for(int i=0;i<l.size();i++){
			StringAttributePanel temp = new StringAttributePanel(net,l.get(i),"NODE");
			sap.add(temp);
			GridBagConstraints gc= new GridBagConstraints();
			gc.gridx=0;
			gc.gridy=i;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.weightx = 1.0;
			gc.insets = new Insets(0,0,10,0);
			//gc.weighty = 1.0;
			//if(i==0)
				gc.anchor = GridBagConstraints.FIRST_LINE_START;
			//else
				//gc.anchor = GridBagConstraints.WEST;
			attrPanel.add(temp,gc);
			
		}
		GridBagConstraints gc= new GridBagConstraints();
		gc.gridx=0;
		gc.gridy=l.size();
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		attrPanel.add(Box.createRigidArea(null),gc);
		//attrPanel.setBorder(new LineBorder(Color.BLUE));
		jsp = new JScrollPane(attrPanel);
		//jsp.setBorder(new LineBorder(Color.RED));
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,mp,jsp);
		split.setMinimumSize(new Dimension(280,200));
		this.add(split,g1);
		//this.add(split);
	}

	public void performSearch(boolean reindex) {
		System.out.println("I am in performSearch");
		String query = mp.getQuery();
		System.out.println("I am in performSearch");
		System.out.println(query);
		System.out.println("I am in performSearch");
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
}
