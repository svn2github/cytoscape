package org.cytoscape.browser.internal;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Icon;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;


@SuppressWarnings("serial")
	public class TableBrowser extends JPanel implements CytoPanelComponent {
	private final Dimension PANEL_SIZE = new Dimension(400, 300);

	public TableBrowser() {
		super();

		setLayout(new BorderLayout());
		add(new JLabel("Jello mold!"));
                setPreferredSize(PANEL_SIZE);
		setBorder(null);
	}

	/**
	 * Returns the Component to be added to the CytoPanel. 
	 * @return The Component to be added to the CytoPanel. 
	 */
	@Override
	public Component getComponent() { return this; }

	/**
	 * Returns the name of the CytoPanel that this component should be added to.
	 * @return the name of the CytoPanel that this component should be added to.
	 */
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/**
	 * Returns the title of the tab within the CytoPanel for this component.
	 * @return the title of the tab within the CytoPanel for this component.
	 */
	@Override
	public String getTitle() { return "Table Browser"; }

	/**
	 * @return null
	 */
	@Override
	public Icon getIcon() { return null; }
}