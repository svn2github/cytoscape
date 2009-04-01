package org.cytoscape.vizmap.gui;

import java.awt.Component;

import javax.swing.JPanel;

public interface DefaultViewEditor {

	public JPanel showEditor(Component parent);
	
	public JPanel getDefaultView(String vsName);

}