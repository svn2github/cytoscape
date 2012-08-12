package org.cytoscape.neildhruva.chartapp.app;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedListener;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;

public class EventTableDestroyed implements NetworkAboutToBeDestroyedListener, NetworkDestroyedListener{

	private MyCytoPanel myCytoPanel;
	private ChartAppFactory chartappFactory;
	
	public EventTableDestroyed(MyCytoPanel myCytoPanel, ChartAppFactory chartAppFactory){
		this.myCytoPanel = myCytoPanel;
		this.chartappFactory = chartAppFactory;
	}
	
	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		JLabel label = new JLabel("Please select/import a network");
		JPanel jpanel=new JPanel();
	    jpanel.setLayout(new GridLayout());
		jpanel.add(label);
		myCytoPanel.setJPanel(jpanel);
		
		chartappFactory.deleteCytoChart("", e.getNetwork().getDefaultNodeTable());
		
	}

	@Override
	public void handleEvent(NetworkDestroyedEvent e) {
		
		
	}
	
}
