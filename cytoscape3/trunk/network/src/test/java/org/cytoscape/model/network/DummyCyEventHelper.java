package org.cytoscape.model.network;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventListener;

public class DummyCyEventHelper implements CyEventHelper {
		
	public <E extends CyEvent, L extends CyEventListener> void fireSynchronousEvent( final E event, final Class<L> listener ) {};

	public <E extends CyEvent, L extends CyEventListener> void fireAsynchronousEvent( final E event, final Class<L> listener ) {}; 

}
