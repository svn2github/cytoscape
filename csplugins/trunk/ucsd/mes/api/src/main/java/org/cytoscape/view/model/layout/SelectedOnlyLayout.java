package org.cytoscape.view.model.layout;

import org.cytoscape.view.model.CyNodeView;

/**
 * To simplify the Layout interface we provide this supplemental
 * interface that layout authors can implement if they want their
 * layout algorithm to just draw selected nodes.
 */
public interface SelectedOnlyLayout extends Layout {

	public void layoutSelectedOnly(boolean selectedOnly);
	public void lockNode(CyNodeView v);
	public void unlockNode(CyNodeView v);
}


