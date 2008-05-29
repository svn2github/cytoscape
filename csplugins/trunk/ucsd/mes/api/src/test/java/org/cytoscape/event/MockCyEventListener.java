
package org.cytoscape.event;

public class MockCyEventListener implements CyEventListener<MockCyEvent> {

	int called = 0;
	public void handleEvent(MockCyEvent e) {
		called++;
	}
	public int getNumCalls() {
		return called;
	}
}
