package cytoscape.filters.util;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.filters.CompositeFilter;
import cytoscape.filters.AtomicFilter;
import ViolinStrings.Strings;
import giny.model.Edge;
import giny.model.Node;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import cytoscape.filters.StringFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.filters.view.FilterMainPanel;
import csplugins.quickfind.util.QuickFind;
import cytoscape.util.CytoscapeAction;
import cytoscape.data.CyAttributes;
import cytoscape.filters.AdvancedSetting;
import cytoscape.filters.FilterPlugin;

public class FilterUtil {
	
	// For test only
	public static Vector<CompositeFilter> getTestFilterVect() {
		
		Vector<CompositeFilter> retVect = new Vector<CompositeFilter>();
		return retVect;
	}
	
	// do selection on given network
	public static void doSelection(CompositeFilter pFilter) {
		//System.out.println("Entering FilterUtil.doSelection() ...");
		
		//return;
		
		pFilter.apply();
		
		CyNetwork network = Cytoscape.getCurrentNetwork(); 

		network.unselectAllNodes();
		network.unselectAllEdges();
		
		final List<Node> nodes_list = network.nodesList();
		final List<Edge> edges_list = network.edgesList();

		if (pFilter.getAdvancedSetting().isNodeChecked()&& (pFilter.getNodeBits() != null)) {
			// Select nodes
			final List<Node> passedNodes = new ArrayList<Node>();

			Node node = null;

			for (int i=0; i< pFilter.getNodeBits().length(); i++) {
				int next_set_bit = pFilter.getNodeBits().nextSetBit(i);
				
				node = nodes_list.get(next_set_bit);
								
				passedNodes.add(node);
				i = next_set_bit;
			}
			network.setSelectedNodeState(passedNodes, true);
		}
		if (pFilter.getAdvancedSetting().isEdgeChecked()&& (pFilter.getEdgeBits() != null)) {
			// Select edges
			final List<Edge> passedEdges = new ArrayList<Edge>();

			Edge edge = null;
			for (int i=0; i< edges_list.size(); i++) {
				int next_set_bit = pFilter.getEdgeBits().nextSetBit(i);
				if (next_set_bit == -1) {
					break;
				}
				edge = edges_list.get(next_set_bit);
				passedEdges.add(edge);
				i = next_set_bit;
			}
			network.setSelectedEdgeState(passedEdges, true);
		}

		Cytoscape.getCurrentNetworkView().updateView();
		
	}
	
	
	public static void applyFilter(CompositeFilter pFilter) {
		ApplyFilterThread applyFilterThread = new ApplyFilterThread(pFilter);
		applyFilterThread.start();
	}
	
	public static CyAttributes getCyAttributes(String pIndexType) {
		CyAttributes attributes = null;

		if (pIndexType.equalsIgnoreCase("node")) {
			attributes = Cytoscape.getNodeAttributes();
		} else if (pIndexType.equalsIgnoreCase("edge")) {
			attributes = Cytoscape.getEdgeAttributes();
		}
		else { //QuickFind.INDEX_ALL_ATTRIBUTES
			attributes = null;
		}

		return attributes;
	}
	
	public static boolean isFilterNameDuplicated(String pFilterName) {
		if (FilterPlugin.getAllFilterVect() == null || FilterPlugin.getAllFilterVect().size() == 0)
			return false;
		
		for (int i=0; i<FilterPlugin.getAllFilterVect().size(); i++) {
			CompositeFilter theFilter = (CompositeFilter) FilterPlugin.getAllFilterVect().elementAt(i);
			if (pFilterName.equalsIgnoreCase(theFilter.getName().trim())) {
				return true;
			}
		}
		return false;
	}
}


