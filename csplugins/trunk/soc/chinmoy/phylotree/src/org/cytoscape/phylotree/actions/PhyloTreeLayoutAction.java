package org.cytoscape.phylotree.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.layout.AbstractLayout;
import cytoscape.util.CytoscapeAction;

import cytoscape.layout.CyLayouts;

public class PhyloTreeLayoutAction extends CytoscapeAction{
	
	String layout_name;
	
	public PhyloTreeLayoutAction(AbstractLayout layout) {
		
		super(layout.toString());
		layout_name = layout.getName();
		CyLayouts.addLayout(layout, null);
		if(layout_name.equals("rectangular_cladogram")|| layout_name.equals("slanted_cladogram")
				|| layout_name.equals("radial_cladogram")
				|| layout_name.equals("circular_cladogram"))
			setPreferredMenu("Layout.Phylotree Layouts.Cladogram Layouts");
		else
			setPreferredMenu("Layout.Phylotree Layouts.Phylogram Layouts");
		
	}

	
	public void actionPerformed(ActionEvent e) {
		CyLayouts.getLayout(layout_name).doLayout();
	}

}