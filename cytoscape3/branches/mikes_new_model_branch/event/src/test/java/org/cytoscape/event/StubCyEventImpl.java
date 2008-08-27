
package org.cytoscape.event;

public class StubCyEventImpl implements StubCyEvent {
	String source;
	StubCyEventImpl(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}
}
