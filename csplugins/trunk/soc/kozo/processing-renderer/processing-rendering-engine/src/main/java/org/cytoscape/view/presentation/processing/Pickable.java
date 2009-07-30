package org.cytoscape.view.presentation.processing;

public interface Pickable {
	public void pick(float x, float y);
	public boolean isPicked();
}
