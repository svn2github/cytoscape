package org.cytoscape.paperwing.internal;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.session.CyApplicationManager;

public class SampleAction extends AbstractCyAction {
	public SampleAction(Map<String, String> properties, CyApplicationManager applicationManager) {
		super(properties, applicationManager);
	}

	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, "Hello, world!");
	}
}
