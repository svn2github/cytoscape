
package cytoscape.events;

import org.cytoscape.event.AbstractCyEvent;

/**
 * An event fired immediately before Cytoscape will be shutdown. 
 */
public final class CytoscapeShutdownEvent extends AbstractCyEvent<Object> {

	private String reason;

	public CytoscapeShutdownEvent(final Object source) {
		super(source, CytoscapeShutdownListener.class);
		reason = null;
	}

	/**
	 * A callback to the firing class that allows a listener to
	 * abort the shutdown.  This can cause conflicts if abused.
	 * @param why A user comprehensible message describing why the shutdown
	 * was aborted.
	 */
	public void abortShutdown(final String why) {
		if ( why == null || why.equals("") )
			return;

		reason = why;
	}

	public String whyNot() {
		return reason;
	}

	public boolean actuallyShutdown() {
		return (reason == null || reason.length() <= 0);
	}
}
