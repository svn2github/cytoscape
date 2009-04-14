package browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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
public class DisplayNetworkTreeAction extends CytoscapeAction {

	public DisplayNetworkTreeAction() {
		super("Show/Hide network tree viewer");
//		setPreferredMenu("Data");
		

	}

	public void actionPerformed(ActionEvent ev) {

		// Check the state of the browser Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST).getState();

		int targetIndex = 0;

		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(
					CytoPanelState.DOCK);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
					.setSelectedIndex(targetIndex);

		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(
					CytoPanelState.HIDE);
		}

	}// action performed
}
