package org.cytoscape.plugin.example;


import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.plugin.CyPluginAdapter;


public class MenuAction extends AbstractCyAction {
	public MenuAction(CyPluginAdapter adapter) {
		super("Hide unconnected nodes", adapter.getCyApplicationManager());
		setPreferredMenu("Select");
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("Jello mol;d!");
	}
}
