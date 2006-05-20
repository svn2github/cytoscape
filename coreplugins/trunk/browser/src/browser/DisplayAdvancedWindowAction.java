package browser;

import java.awt.event.ActionEvent;

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
 * 3/6/2005 KONO: This action is now under "View" menu.
 * 
 * @author kono
 * 
 */
public class DisplayAdvancedWindowAction extends CytoscapeAction {

	public DisplayAdvancedWindowAction() {
		super("Show/Hide advanced window");
		// setPreferredMenu("Data");
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
			
			// Case 2: Panel is in the Dock
		} else if (curState == CytoPanelState.DOCK) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			//menuItem.setSelected(false);
			// Case 3: Panel is FLOAT
		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			//menuItem.setSelected(false);
		}

	}// action performed
}
