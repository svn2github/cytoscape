package browser;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * 
 * This class is enabled only when browser plugin is loaded.
 * 3/6/2005 KONO: This action is now under "View" menu.
 * 
 * @author kono
 * 
 */
public class DisplayAdvancedWindowAction extends CytoscapeAction {

	public DisplayAdvancedWindowAction() {
		super("Show/Hide advanced window");
	}

	public void actionPerformed(ActionEvent ev) {

		// Check the state of the browser Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.EAST).getState();

		int targetIndex = 0;

		// Case 1: Panel is disabled
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.FLOAT);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
					.setSelectedIndex(targetIndex);
			// Need to sync. Desktop menu item
			syncCheckbox(true, 3);
			
		// Case 2: Panel is in the Dock
		} else if (curState == CytoPanelState.DOCK) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			syncCheckbox(false, 3);
			
		// Case 3: Panel is FLOAT
		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			syncCheckbox(false, 3);
		}
		
		

	}// action performed
	
	/**
	 * Find menu item, and sync check box.
	 * 
	 * @param on
	 * @param menuItem
	 */
	protected static void syncCheckbox(boolean on, int cytopanelIndex) {
		JCheckBoxMenuItem targetCheckbox = null;
		JMenu targetMenu = Cytoscape.getDesktop().getCyMenus().getViewMenu();
		int menuCount = targetMenu.getMenuComponentCount();
		
		// Find the location of menu item
		for(int i=0; i<menuCount; i++) {
			
			Object component = targetMenu.getMenuComponent(i);
			
			if(component.getClass().equals(JMenu.class)) {
				if(((JMenu)component).getText().equals("Desktop")) {
					targetCheckbox = (JCheckBoxMenuItem) ((JMenu)component).getMenuComponent(cytopanelIndex-1);
				}
			}
		}
		
		if(targetCheckbox == null) {
			return;
		}
		
		if(on == true) {
			targetCheckbox.setSelected(true);
		} else {
			targetCheckbox.setSelected(false);
		}
	}
}
