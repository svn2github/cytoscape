package org.idekerlab.PanGIAPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.CyNetwork;
import cytoscape.CyNetworkTitleChange;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

public class PanGIANetworkListener implements PropertyChangeListener
{
	public void propertyChange(PropertyChangeEvent event) {
        if (CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(event.getPropertyName()))
        {
                final CyNetworkView view = (CyNetworkView) event.getNewValue();

                // Node right-click menu
                final PanGIANodeContextMenuListener nodeMenuListener = new PanGIANodeContextMenuListener(view);
                Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(nodeMenuListener);
                
                // Edge right-click menu
                final PanGIAEdgeContextMenuListener edgeMenuListener = new PanGIAEdgeContextMenuListener(view);
                Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(edgeMenuListener);
        }
        
        if (Cytoscape.NETWORK_DESTROYED.equals(event.getPropertyName()))
        {
        	CyNetwork net = Cytoscape.getNetwork((String)event.getNewValue());
        	
        	if (PanGIAPlugin.output.containsKey(net.getTitle())) System.out.println("Removing PanGIA result: "+net.getTitle());
        	PanGIAPlugin.output.remove(net.getIdentifier());
        }
        
        if (Cytoscape.NETWORK_TITLE_MODIFIED.equals(event.getPropertyName()))
        {
        	CyNetworkTitleChange ctc = (CyNetworkTitleChange)event.getNewValue();
        	String newName = Cytoscape.getNetwork(ctc.getNetworkIdentifier()).getTitle();
        	
        	for (CyNetwork net : Cytoscape.getNetworkSet())
        		if (net.getTitle().equals(newName) && !net.getIdentifier().equals(ctc.getNetworkIdentifier()) && (PanGIAPlugin.output.containsKey(net.getIdentifier()) || PanGIAPlugin.output.containsKey(ctc.getNetworkIdentifier())))
        			System.out.println("PanGIA WARNING: Two overview networks have the same name!");
        		
        		
        	//CyNetworkTitleChange ctc = (CyNetworkTitleChange)event.getNewValue();
        	//ctc.getNetworkTitle()
        	
        	//String oldName = Cytoscape.getNetwork(ctc.getNetworkIdentifier()).getTitle();
        	//String newName = Cytoscape.getNetwork(ctc.getNetworkIdentifier()).getTitle();
        	
        	/*
        	String oldName = ((CyNetworkTitleChange)event.getOldValue()).getNetworkTitle();
        	String newName = ((CyNetworkTitleChange)event.getNewValue()).getNetworkTitle();
        	System.out.println("Changing: "+oldName+" to "+newName);
        	
        	*/
        }
        	
}

}
