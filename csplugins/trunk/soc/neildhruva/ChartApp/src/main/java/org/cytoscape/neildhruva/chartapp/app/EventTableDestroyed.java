package org.cytoscape.neildhruva.chartapp.app;

import java.awt.GridLayout;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedListener;

public class EventTableDestroyed implements NetworkAboutToBeDestroyedListener, NetworkDestroyedListener{

	private MyCytoPanel myCytoPanel;
	private CyTableManager cyTableManager;
	
	public EventTableDestroyed(MyCytoPanel myCytoPanel, CyTableManager cyTableManager){
		this.myCytoPanel = myCytoPanel;
		this.cyTableManager = cyTableManager;
	}
	
	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		JLabel label = new JLabel("Please select/import a network");
		JPanel jpanel=new JPanel();
	    jpanel.setLayout(new GridLayout());
		jpanel.add(label);
		myCytoPanel.setJPanel(jpanel);
		
		Iterator<CyTable> iterator = cyTableManager.getAllTables(true).iterator();
		CyTable myTable;
		while(iterator.hasNext()) {
			myTable = iterator.next();
			if(myTable.getTitle().equals("CytoChart "+e.getNetwork().getDefaultNodeTable().getTitle())) {
				cyTableManager.deleteTable(myTable.getSUID());
			}
		}
		
		//TODO should I delete myCyTable for the deleted CyTable?
		
	}

	@Override
	public void handleEvent(NetworkDestroyedEvent e) {
		
		
	}
	
}
