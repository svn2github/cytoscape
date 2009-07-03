package org.cytoscape.view.presentation.processing;

public enum PresentationType {
	NODE("Node"), EDGE("Edge"), NETWORK("Network"), ANNOTATION("Annotation");
	
	private final String displayName;
	
	private PresentationType(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}
