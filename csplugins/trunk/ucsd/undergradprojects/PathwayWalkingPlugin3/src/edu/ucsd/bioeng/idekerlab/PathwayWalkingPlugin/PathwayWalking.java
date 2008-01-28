package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.layout.Tunable;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.NodeCalculator;
import cytoscape.visual.mappings.PassThroughMapping;
import giny.model.Edge;
import giny.model.Node;
import giny.view.NodeView;
//import ding.*;

// Assume user has right-clicked, and called PathwayWalking (via Sam's Linkout NodeListener thing)
public class PathwayWalking {
	public static void main(String[]args) {
//		PopupMenu();
	}


	public void startGUI(NodeView node) {
		//System.out.println("linkout.addLinks called with node "+ ((NodeView) node).getLabel().getText());

		final JMenu top_menu = new JMenu("PathwayWalking");
		final NodeView mynode = (NodeView) node;

		// Get the set of attribute names for this node
		Node n = mynode.getNode();
		CyAttributes na = Cytoscape.getNodeAttributes();
		System.out.println("test");
	}


	private JMenuItem getMenuItem(String name, JMenu menu) {
		int count = menu.getMenuComponentCount();

		if (count == 0) {
			return null;
		}

		//Skip over all JMenu components that are not JMenu or JMenuItem
		for (int i = 0; i < count; i++) {
			if (!menu.getItem(i).getClass().getName().equals("javax.swing.JMenu")
			    && !menu.getItem(i).getClass().getName().equals("javax.swing.JMenuItem")) {
				continue;
			}

			JMenuItem jmi = menu.getItem(i);

			if (jmi.getText().equalsIgnoreCase(name)) {
				return jmi;
			}
		}

		return null;
	}
	
	
	

	
	
	
	
	private static void PopupMenu() {
		// Steve's Swing/GUI thing. :D
		
		// Methods we need to write (for the future)
//		String NodeID = getNodeID();
//		boolean database[] = getDatabase();
		
		
	}
//	public JMenuItem addLinks(NodeView nv){
//		return ;
//	}
}
