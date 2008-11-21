
package org.cytoscape.event;

public class StubCyEventListenerImpl implements StubCyEventListener {

	int called = 0;
	public void handleEvent(StubCyEvent e) {
		called++;
	}
	public int getNumCalls() {
		return called;
	}
}
