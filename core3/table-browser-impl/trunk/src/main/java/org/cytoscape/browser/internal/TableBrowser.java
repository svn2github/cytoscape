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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.cytoscape.browser.ui.AttributeBrowserToolBar;
import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyTableRowUpdateService;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.model.events.TableDeletedEvent;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.model.events.TableAboutToBeDeletedListener;
import org.cytoscape.model.events.TableAboutToBeDeletedEvent;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.swing.GUITaskManager;


@SuppressWarnings("serial")
public class TableBrowser
	extends JPanel implements CytoPanelComponent, ActionListener, NetworkViewAddedListener, TableAboutToBeDeletedListener
{
	private final CyTableManager tableManager;
	private final CyServiceRegistrar serviceRegistrar;
	private final EqnCompiler compiler;
	private final BrowserTable browserTable;
	private final CyTableRowUpdateService tableRowUpdateService;
	private final AttributeBrowserToolBar attributeBrowserToolBar;
	private final TableChooser tableChooser;
	private BrowserTableModel browserTableModel;
	private CyTable currentTable;
	private final TableTaskFactory deleteTableTaskFactoryService;
	private final GUITaskManager guiTaskManagerServiceRef;
	
	TableBrowser(final CyTableManager tableManager, final CyServiceRegistrar serviceRegistrar,
		     final EqnCompiler compiler, final OpenBrowser openBrowser,
		     final CyNetworkManager networkManager,
		     final CyTableRowUpdateService tableRowUpdateService, 
		     final TableTaskFactory deleteTableTaskFactoryService,
		     final GUITaskManager guiTaskManagerServiceRef,
		     final PopupMenuHelper popupMenuHelper)
	{
		this.tableManager = tableManager;
		this.serviceRegistrar = serviceRegistrar;
		this.compiler = compiler;

		this.deleteTableTaskFactoryService = deleteTableTaskFactoryService;
		this.guiTaskManagerServiceRef = guiTaskManagerServiceRef;
		
		this.browserTable = new BrowserTable(openBrowser, compiler, popupMenuHelper);
		this.tableRowUpdateService = tableRowUpdateService;
		this.attributeBrowserToolBar = new AttributeBrowserToolBar(serviceRegistrar, compiler, 
				this.deleteTableTaskFactoryService, this.guiTaskManagerServiceRef);
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
		if (table == null && table != currentTable){
			if (browserTableModel != null) {
				browserTableModel.cleanup();
				serviceRegistrar.unregisterAllServices(browserTableModel);
			}
			currentTable = null;
			browserTableModel = null;
			browserTable.setModel(new DefaultTableModel());
			attributeBrowserToolBar.setBrowserTableModel(null);
		}
		
		if (table != null && table != currentTable) {
			if (browserTableModel != null) {
				browserTableModel.cleanup();
				serviceRegistrar.unregisterAllServices(browserTableModel);
			}

			currentTable = table;
			browserTableModel = new BrowserTableModel(browserTable, table, compiler,
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
	
	public void handleEvent(TableAboutToBeDeletedEvent e){
		final CyTable cyTable = e.getTable();
		final MyComboBoxModel comboBoxModel = (MyComboBoxModel)tableChooser.getModel();
		comboBoxModel.removeItem(cyTable);
	}
}