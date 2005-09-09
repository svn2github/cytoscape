/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import phoebe.PGraphView;
import phoebe.PhoebeCanvas;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.event.NetworkEditEventAdapter;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

/**
 * provides non-static methods needed by the CytoscapeEditorManager, in
 * particular those methods associated with the PropertyChangeListener class
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see CytoscapeEditorManager
 * 
 */
public class CytoscapeEditorManagerSupport implements PropertyChangeListener {

	/**
	 * register interest in NETWORK_VIEW_FOCUSED and NETWORK_VIEW_CREATED events
	 *
	 */
	public CytoscapeEditorManagerSupport() {
		super();

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
	}



	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED))
		{
		}
		else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED))
		{
			// implement ATTRIBUTES_CHANGED handler
			System.out.println ("Property changed: " + e.getPropertyName());
			System.out.println ("Old value = " + e.getOldValue());
			System.out.println ("New value = " + e.getNewValue());
			
		}
		else if (e.getPropertyName().equals(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CytoscapeEditor cyEditor = CytoscapeEditorManager
					.getEditorForView(view);
			
			if (cyEditor == null)
			{
				cyEditor = CytoscapeEditorManager.getCurrentEditor();
			}
			
			if (cyEditor == null)
			{
				// this would be because no editor has been set yet.  Just return
				return;
			}
			
			// at this point there is an editor but it is not assigned to this view
			// this is probably the case if we are loading a network, rather than creating a new one
			// in this case, we need to setup the network view, which sets all the event handler, etc.
			CytoscapeEditorManager.setupNewNetworkView(view);
		}
	}
}