package org.cytoscape.browser.internal;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import org.cytoscape.browser.ui.AttributeBrowserToolBar;
import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyTableRowUpdateService;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;


@SuppressWarnings("serial")
public class TableBrowser
	extends JPanel implements CytoPanelComponent, ActionListener, NetworkViewAddedListener
{
	private final CyTableManager tableManager;
	private final CyServiceRegistrar serviceRegistrar;
	private final CyEventHelper eventHelper;
	private final EqnCompiler compiler;
	private final BrowserTable browserTable;
	private final CyTableRowUpdateService tableRowUpdateService;
	private final AttributeBrowserToolBar attributeBrowserToolBar;
	private final TableChooser tableChooser;
	private BrowserTableModel browserTableModel;
	private CyTable currentTable;

	TableBrowser(final CyTableManager tableManager, final CyServiceRegistrar serviceRegistrar,
		     final CyEventHelper eventHelper, final EqnCompiler compiler,
		     final OpenBrowser openBrowser, final CyNetworkManager networkManager,
		     final CyTableRowUpdateService tableRowUpdateService)
	{
		this.tableManager = tableManager;
		this.serviceRegistrar = serviceRegistrar;
		this.eventHelper = eventHelper;
		this.compiler = compiler;
		this.browserTable = new BrowserTable(openBrowser, compiler);
		this.tableRowUpdateService = tableRowUpdateService;
		this.attributeBrowserToolBar = new AttributeBrowserToolBar(serviceRegistrar, compiler, tableManager);
		this.setLayout(new BorderLayout());

		browserTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tableChooser = new TableChooser(tableManager, networkManager);
		tableChooser.addActionListener(this);
		add(tableChooser, BorderLayout.SOUTH);
		browserTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
		add(new JScrollPane(browserTable), BorderLayout.CENTER);
		add(attributeBrowserToolBar, BorderLayout.NORTH);
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
	public String getTitle() { return "Attribute Browser"; }

	/**
	 * @return null
	 */
	public Icon getIcon() { return null; }

	public void actionPerformed(ActionEvent e) {
		final CyTable table = (CyTable)tableChooser.getSelectedItem();
		if (table != null && table != currentTable) {
			if (browserTableModel != null) {
				browserTableModel.cleanup();
				serviceRegistrar.unregisterAllServices(browserTableModel);
			}

			currentTable = table;
			browserTableModel = new BrowserTableModel(browserTable, eventHelper, table,
								  compiler, serviceRegistrar,
								  tableRowUpdateService);
			serviceRegistrar.registerAllServices(browserTableModel, new Properties());
			browserTable.setModel(browserTableModel);
			browserTable.setRowSorter(new TableRowSorter(browserTableModel));
			attributeBrowserToolBar.setBrowserTableModel(browserTableModel);
		}
	}

	public void handleEvent(NetworkViewAddedEvent e) {
		final CyTable nodeTable = e.getNetworkView().getModel().getDefaultNodeTable();
		final MyComboBoxModel comboBoxModel = (MyComboBoxModel)tableChooser.getModel();
		comboBoxModel.addAndSetSelectedItem(nodeTable);
	}
}