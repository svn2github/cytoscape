package org.cytoscape.sample.internal;

import java.awt.GridLayout;

import javax.swing.JLabel;

import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class TableDestroyedEvent implements NetworkAboutToBeDestroyedListener{

	private MyCytoPanel myCytoPanel;
	
	
	TableDestroyedEvent(MyCytoPanel myCytoPanel){
		
		this.myCytoPanel = myCytoPanel;
		
	}
	
	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		//Clear the Table View Panel
		myCytoPanel.removeAll();
		JLabel label = new JLabel("Please select/import a network");
		myCytoPanel.setLayout(new GridLayout());
		myCytoPanel.add(label);
		myCytoPanel.revalidate();
		
		//Set networkDestroyed to true in order to keep from implementing the code in TableAddedEvent.java
		TableAddedEvent.networkDestroyed = true;
		
	}
}
