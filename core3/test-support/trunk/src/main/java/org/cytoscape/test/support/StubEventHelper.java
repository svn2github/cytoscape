
package org.cytoscape.test.support;

import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyListener;

public class StubEventHelper implements CyEventHelper {
	public <E extends CyEvent, L extends CyListener> void fireSynchronousEvent(final E event,
				final Class<L> listener) {}
	public <E extends CyEvent, L extends CyListener> void fireAsynchronousEvent(final E event,
				final Class<L> listener) {}
}


