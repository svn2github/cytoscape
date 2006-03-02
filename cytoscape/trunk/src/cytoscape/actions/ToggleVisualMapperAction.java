//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

/**
 * This class implements two menu items that allow enabling and disabling the
 * visual mapper attached the the CyWindow argument.
 */
public class ToggleVisualMapperAction extends CytoscapeAction {

	public ToggleVisualMapperAction() {
		super("Lock VizMapper\u2122");
		setPreferredMenu("View");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_M, ActionEvent.ALT_MASK);
	}

	public void actionPerformed(ActionEvent e) {
		// TODO: this is state information that should saved
		if (Cytoscape.getCurrentNetworkView() != null) {
			Cytoscape.getCurrentNetworkView().toggleVisualMapperEnabled();
			Cytoscape.getDesktop().getCyMenus().setVisualMapperItemsEnabled(
					Cytoscape.getCurrentNetworkView().getVisualMapperEnabled());
		}

	}
}
