package org.cytoscape.extras.event_tracker.internal;

public class EventData {

	private String fullName;
	private String shortName;
	
	private int count;
	private int payloadCount;

	public EventData(Class<?> c) {
		fullName = c.getName();
		shortName = c.getSimpleName();
	}
	
	public String getFullName() {
		return fullName;
	}

	public String getShortName() {
		return shortName;
	}
	
	public int getCount() {
		return count;
	}

	public void increment() {
		count++;
	}

	public void addPayload(int count) {
		payloadCount += count;
	}
	
	public void reset() {
		count = 0;
		payloadCount = 0;
	}

	public int getPayloadCount() {
		return payloadCount;
	}
}
