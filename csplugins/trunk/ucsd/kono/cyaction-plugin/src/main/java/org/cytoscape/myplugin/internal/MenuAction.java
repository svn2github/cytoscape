package org.cytoscape.myplugin.internal;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;


/**
 * Creates a new menu item under Plugins menu section.
 *
 */
public class MenuAction extends AbstractCyAction {

	public MenuAction(final CyApplicationManager applicationManager, final String menuTitle) {
		super(menuTitle, applicationManager);
		setPreferredMenu("Plugins");
	}

	public void actionPerformed(ActionEvent e) {

		// Write your own function here.
		JOptionPane.showMessageDialog(null, "Hello Cytoscape 3 World!");
		
	}
}
