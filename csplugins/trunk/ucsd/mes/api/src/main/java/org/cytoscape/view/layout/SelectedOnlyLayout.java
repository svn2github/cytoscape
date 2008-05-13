package org.cytoscape.view.layout;

import org.cytoscape.view.CyNodeView;

public interface SelectedOnlyLayout {

	public void layoutSelectedOnly(boolean selectedOnly);
	public void lockNode(CyNodeView v);
	public void unlockNode(CyNodeView v);
}


