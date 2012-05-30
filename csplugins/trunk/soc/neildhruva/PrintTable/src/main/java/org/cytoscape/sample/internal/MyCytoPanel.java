package org.cytoscape.sample.internal;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

public class MyCytoPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;

	public MyCytoPanel() {
		
		this.setVisible(true);	
	}


	public Component getComponent() {
		return this;
	}

	/**
	 * @return CytoPanelName Location of the CytoPanel
	 */
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/**
	 * @return String Title of the CytoPanel
	 */
	public String getTitle() {
		return "Table View";
	}

	public Icon getIcon() {
		return null;
	}
}


