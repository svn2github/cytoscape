package org.cytoscape.view.vizmap.gui.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cytoscape.view.vizmap.gui.action.VizMapUIAction;
import org.cytoscape.view.vizmap.gui.internal.theme.IconManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manager for all Vizmap-local tasks (commands).
 * 
 * @author kono
 *
 */
public class VizMapperMenuManager {
	
	private static final Logger logger = LoggerFactory.getLogger(VizMapperMenuManager.class);
	
	// Metadata
	private static final String METADATA_MENU_KEY = "menu";
	private static final String METADATA_TITLE_KEY = "title";
	
	private static final String MAIN_MENU = "main";
	private static final String CONTEXT_MENU = "context";

	// Menu items under the tool button
	private JPopupMenu mainMenu;
	
	// Context menu
	private JPopupMenu rightClickMenu;
	
	private JMenu generateValues;

	private IconManager iconManager;

	// Injected from resource file.
	private String generateMenuLabel;
	private String generateIconId;

	private String modifyMenuLabel;
	private String modifyIconId;

	private JMenu modifyValues;
	
	
	private final TaskManager taskManager;
	
	public VizMapperMenuManager(final TaskManager taskManager) {
		this.taskManager = taskManager;

		// Will be shown under the button next to Visual Style Name
		mainMenu = new JPopupMenu();

		// Context menu
		rightClickMenu = new JPopupMenu();


		//modifyValues = new JMenu(modifyMenuLabel);
	}
	
	
	
	public void setIconManager(IconManager iconManager) {
		this.iconManager = iconManager;
	}
	
	public void setGenerateMenuLabel(String generateMenuLabel) {
		this.generateMenuLabel = generateMenuLabel;
	}
	
	public void setGenerateIconId(String generateIconId) {
		this.generateIconId = generateIconId;
	}

	

	public JPopupMenu getMainMenu() {
		return mainMenu;
	}

	public JPopupMenu getContextMenu() {
		return rightClickMenu;
	}

	
	/*
	 * Custom listener for dynamic menu management
	 * 
	 * (non-Javadoc)
	 * @see cytoscape.view.ServiceListener#onBind(java.lang.Object, java.util.Map)
	 */
	public void onBind(VizMapUIAction action, Map properties) {
		if(generateValues == null && iconManager != null) {
			// for value generators.
			generateValues = new JMenu(generateMenuLabel);
			generateValues.setIcon(iconManager.getIcon(generateIconId));
			rightClickMenu.add(generateValues);
		}

		
		final Object serviceType = properties.get("service.type");
		if( serviceType != null && serviceType.toString().equals("vizmapUI.contextMenu")) {
			rightClickMenu.add(action.getMenu());			
		} else {
			mainMenu.add(action.getMenu());
		}
	}

	public void onUnbind(VizMapUIAction service, Map properties) {
	}
	
	
	/**
	 * 
	 * @param taskFactory
	 * @param properties
	 */
	public void addTaskFactory(final TaskFactory taskFactory, Map properties) {
		final Object serviceType = properties.get(METADATA_MENU_KEY);
		if( serviceType != null && serviceType.toString().equals(MAIN_MENU)) {
			// This is a menu item for Main Command Button.
			final Object title = properties.get(METADATA_TITLE_KEY);
			if(title == null)
				throw new NullPointerException("Title metadata is missing.");
			
			
			// Add new menu to the pull-down
			final JMenuItem menuItem = new JMenuItem(title.toString());
			//menuItem.setIcon(iconManager.getIcon(iconId));
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					taskManager.execute(taskFactory);
				}
			});
			
			mainMenu.add(menuItem);
		}
	}
	
	public void removeTaskFactory(final TaskFactory taskFactory, Map properties) {
		
	}
}
