package org.cytoscape.search.ui.filter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JPanel;

import org.cytoscape.session.CyNetworkManager;

public class TopologyPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private TopologyFilter tf;
	private NodeInteractionFilter nif;
	private EdgeInteractionFilter eif;
	private CyNetworkManager netmgr;

	/**
	 * This is the default constructor
	 */
	public TopologyPanel(CyNetworkManager nm) {
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
		this.setSize(309, 538);
		this.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1.0;
		gc.insets = new Insets(8, 0, 10, 0);
		tf = new TopologyFilter(netmgr);
		this.add(tf, gc);
		gc.insets = new Insets(0, 0, 10, 0);
		gc.gridy = 1;
		nif = new NodeInteractionFilter(netmgr);
		this.add(nif, gc);
		gc.gridy = 2;
		eif = new EdgeInteractionFilter(netmgr);
		this.add(eif, gc);
		gc.gridy = 3;
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		this.add(Box.createRigidArea(null), gc);

	}

}
