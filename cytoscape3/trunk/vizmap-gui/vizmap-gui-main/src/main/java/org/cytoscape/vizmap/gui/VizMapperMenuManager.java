package org.cytoscape.vizmap.gui;

import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.cytoscape.vizmap.gui.theme.IconManager;


public class VizMapperMenuManager {

	private JPopupMenu mainMenu;
	private JPopupMenu rightClickMenu;
	private JMenu generateValues;

	private IconManager iconManager;

	// Injected from resource file.
	private String generateMenuLabel;
	private String generateIconId;

	private String modifyMenuLabel;
	private String modifyIconId;

	private JMenu modifyValues;
	
	public void setIconManager(IconManager iconManager) {
		this.iconManager = iconManager;
	}
	
	public void setGenerateMenuLabel(String generateMenuLabel) {
		this.generateMenuLabel = generateMenuLabel;
	}
	
	public void setGenerateIconId(String generateIconId) {
		this.generateIconId = generateIconId;
	}

	public VizMapperMenuManager() {

		// Will be shown under the button next to Visual Style Name
		mainMenu = new JPopupMenu();

		// Context menu
		rightClickMenu = new JPopupMenu();


		//modifyValues = new JMenu(modifyMenuLabel);
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
	public void onBind(VizMapperUIAction action, Map properties) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@ binding service:");
		if(generateValues == null && iconManager != null) {
			// for value generators.
			generateValues = new JMenu(generateMenuLabel);
			generateValues.setIcon(iconManager.getIcon(generateIconId));
			rightClickMenu.add(generateValues);
		}
		
		for(Object key: properties.keySet()){
			System.out.println(key + " ==============> " + properties.get(key)); 
		}
		
		final Object serviceType = properties.get("service.type");
		if( serviceType != null && serviceType.toString().equals("vizmapUI.contextMenu")) {
			rightClickMenu.add(action.getMenu());
			
			System.out.println("@@@@@@@@@@@@@@@@@@@@ Contyext menu service: " + action.getMenu().getText());
			
		} else {
			mainMenu.add(action.getMenu());
		}
	}

	public void onUnbind(VizMapperUIAction service, Map properties) {
	}
}
