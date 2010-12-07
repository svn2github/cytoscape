package org.cytoscape.browser.internal;


import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.Icon;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;


@SuppressWarnings("serial")
public class TableBrowser extends JPanel implements CytoPanelComponent, ActionListener {
	private final CyTableManager tableManager;

	TableBrowser(final CyTableManager tableManager) {
		this.tableManager = tableManager;

		final TableChooser tableChooser = new TableChooser(tableManager);
		tableChooser.addActionListener(this);
		add(tableChooser);
	}

	/**
	 * Returns the Component to be added to the CytoPanel. 
	 * @return The Component to be added to the CytoPanel. 
	 */
	public Component getComponent() { return this; }

	/**
	 * Returns the name of the CytoPanel that this component should be added to.
	 * @return the name of the CytoPanel that this component should be added to.
	 */
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/**
	 * Returns the title of the tab within the CytoPanel for this component.
	 * @return the title of the tab within the CytoPanel for this component.
	 */
	public String getTitle() { return "Table Browser"; }

	/**
	 * @return null
	 */
	public Icon getIcon() { return null; }
		
	public void actionPerformed(ActionEvent e) {
		final TableChooser tableChooser = (TableChooser)e.getSource();
		final CyTable table = (CyTable)tableChooser.getSelectedItem();
		if (table != null)
			System.err.println("**************************************************** selected table = "+table.getTitle());
	}
}