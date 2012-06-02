package csplugins.jActiveModules.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyTable;


public class SelectUtil {

	public static Set<CyNode> getSelectedNodes(CyNetwork cyNetwork) {
		return getSelected(cyNetwork.getNodeList(), cyNetwork);
	}
	
	static <T extends CyIdentifiable> Set<T> getSelected(Collection<T> items, CyNetwork network) {
		Set<T> entries = new HashSet<T>();
		for (T item : items) {
			//CyRow row = item.getCyRow();
			CyRow row = network.getRow(item);
			if (row.get(CyNetwork.SELECTED, Boolean.class)) {
				entries.add(item);
			}
		}
		return entries;
	}
}
