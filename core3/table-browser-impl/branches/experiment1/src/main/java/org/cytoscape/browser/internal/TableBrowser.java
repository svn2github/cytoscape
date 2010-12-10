package org.cytoscape.browser.internal;


import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;


@SuppressWarnings("serial")
public class TableBrowser extends JPanel implements CytoPanelComponent, ActionListener {
	private final CyTableManager tableManager;
	private final CyServiceRegistrar serviceRegistrar;
	private final BrowserTable browserTable;
	private BrowserTableModel browserTableModel;
	private CyTable currentTable;

	TableBrowser(final CyTableManager tableManager, final CyServiceRegistrar serviceRegistrar) {
		this.tableManager = tableManager;
		this.serviceRegistrar = serviceRegistrar;
		this.browserTable = new BrowserTable();
		browserTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		final TableChooser tableChooser = new TableChooser(tableManager);
		tableChooser.addActionListener(this);
		add(tableChooser);
		add(new JScrollPane(browserTable));
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
	public String getTitle() { return null; }

	/**
	 * @return null
	 */
	public Icon getIcon() { return null; }
		
	public void actionPerformed(ActionEvent e) {
		final TableChooser tableChooser = (TableChooser)e.getSource();
		final CyTable table = (CyTable)tableChooser.getSelectedItem();
		if (table != null && table != currentTable) {
			currentTable = table;
			if (browserTableModel != null)
				serviceRegistrar.unregisterAllServices(browserTableModel);
			browserTableModel = new BrowserTableModel(table);
			serviceRegistrar.registerAllServices(browserTableModel, new Properties());
			browserTable.setModel(browserTableModel);
		}
	}
}