package org.cytoscape.BipartiteVisualiserPlugin;

import giny.view.EdgeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateBipartiteViewAction implements ActionListener {

	private final EdgeView edgeView;
	
	public CreateBipartiteViewAction(final EdgeView edgeView) {
		this.edgeView = edgeView;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("====== Do dual Layout for " + edgeView.getEdge().getIdentifier());
	}

}
