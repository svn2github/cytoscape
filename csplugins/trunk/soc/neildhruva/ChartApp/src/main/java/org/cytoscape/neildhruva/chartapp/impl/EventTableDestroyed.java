package org.cytoscape.neildhruva.chartapp.impl;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class EventTableDestroyed implements NetworkAboutToBeDestroyedListener{

	private MyCytoPanel myCytoPanel;
	
	public EventTableDestroyed(MyCytoPanel myCytoPanel){
		this.myCytoPanel = myCytoPanel;
	}
	
	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		JLabel label = new JLabel("Please select/import a network");
		JPanel jpanel=new JPanel();
	    jpanel.setLayout(new GridLayout());
		jpanel.add(label);
		myCytoPanel.setJPanel(jpanel);
		
	}
}
