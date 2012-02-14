package org.cytoscape.view.layout;

import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.NetworkViewTaskContext;
import org.cytoscape.view.model.View;

public interface LayoutContext extends NetworkViewTaskContext {
	/**
	 * Sets the "selectedOnly" flag
	 *
	 * @param selectedOnly boolean value that tells the layout algorithm whether to
	 * only layout the selected nodes
	 */
	void setSelectedOnly(boolean selectedOnly);

	/**
	 * Sets the attribute to use for node- or edge- based attribute layouts
	 *
	 * @param attributeName String with the name of the attribute to use
	 */
	void setLayoutAttribute(String attributeName);

	/**
	 * This returns a (possibly empty) List of Strings that is used for
	 * the attribute list in the menu for attribute-based layouts.  This
	 * allows layout algorithms to provide "special" attributes.  For example,
	 * a force directed layout might want to set the list to ["(unweighted)"]
	 * to allow the user to perform an unweighted layout.  Note that this value
	 * will be set using setLayoutAttribute() just like other attributes, so the
	 * layout algorithm will need to check for it.
	 *
	 * @return List of column names (i.e. attributes) used for attribute-based layouts.
	 */
	List<String> getInitialAttributeList();

	boolean getSelectedOnly();

	Set<View<CyNode>> getStaticNodes();
}
