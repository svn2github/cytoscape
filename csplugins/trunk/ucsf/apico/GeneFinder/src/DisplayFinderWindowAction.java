package browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * 
 * This class is enabled only when browser plugin is loaded. User can on/off
 * browser panel (CytoPanel3) by using f5 key.
 * 
 * @author kono
 * 
 */
public class DisplayFinderWindowAction extends CytoscapeAction {

	public DisplayFinderWindowAction() {
		super("Show/Hide finder window");
//		setPreferredMenu("Data");
	}

	public void actionPerformed(ActionEvent ev) {

		// Check the state of the browser Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.EAST).getState();
		
		JCheckBoxMenuItem menuItem = null;
		menuItem =  (JCheckBoxMenuItem) Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("CytoPanels").getItem(2);

		int targetIndex = 0;

		// Case 1: Panel is disabled
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.FLOAT);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
					.setSelectedIndex(targetIndex);
			menuItem.setSelected(true);
			
			//System.out.println("Counter = " + menuItem.toString());

			// Case 2: Panel is in the Dock
		} else if (curState == CytoPanelState.DOCK) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			menuItem.setSelected(false);
			// Case 3: Panel is FLOAT
		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			menuItem.setSelected(false);
		}

	}// action performed
}
