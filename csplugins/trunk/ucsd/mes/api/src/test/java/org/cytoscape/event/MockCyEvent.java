
package org.cytoscape.event;

public class MockCyEvent implements CyEvent<String> {
	String source;
	MockCyEvent(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}
}
