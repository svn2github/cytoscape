package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import giny.model.Node;
import giny.view.NodeView;
import java.awt.Color;
//import ding.*;

// Assume user has right-clicked, and called PathwayWalking (via Sam's Linkout NodeListener thing)
public class PathwayWalking {
	public PathwayWalking(){
		
	}


	public JMenuItem addLinks(NodeView node) {
		System.out.println("popupmenu.addLinks called with node "+ ((NodeView) node).getLabel().getText());

		final JMenu top_menu = new JMenu("PathwayWalking");
		final NodeView mynode = (NodeView) node;

		// Get the set of attribute names for this node
		Node n = mynode.getNode();
		CyAttributes na = Cytoscape.getNodeAttributes();
		final JMenuItem source = new JMenuItem("Get Neighbors");
		source.setBackground(Color.red);
		source.setEnabled(true);
		top_menu.add(source);
		HelloWorld hw =  new HelloWorld(na);
		return top_menu;
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
