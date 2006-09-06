package structureViz.ui;

import edu.umd.cs.piccolo.PNode;
import javax.swing.JMenu;

public class AbstractStructureVizMenu {

		public static JMenu getAbstractStructureVizMenu (Object [] args, PNode pnode) {
			// Create all of the menu entries here
			JMenu menu = new JMenu("Structure Visualization");
			menu.add("Display Structure");
			menu.add("Align Structures");
			return menu;
		}
}
