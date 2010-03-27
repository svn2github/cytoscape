package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.visual.customgraphic.ui.CustomGraphicsManagerDialog;

public class ShowCustomGraphicsManagerAction extends CytoscapeAction {

	private static final long serialVersionUID = 5876533870116518191L;

	private static final String TITLE = "Open Custom Graphics Manager...";

	private CustomGraphicsManagerDialog manager;

	/**
	 * Creates a new SetVisualPropertiesAction object.
	 */
	public ShowCustomGraphicsManagerAction() {
		super(TITLE);
		setPreferredMenu("View");
		setEnabled(true);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// Add custom graphics manager

		if (manager == null)
			manager = new CustomGraphicsManagerDialog(Cytoscape.getDesktop(),
					false);

		manager.setLocationRelativeTo(Cytoscape.getDesktop());
		manager.setVisible(true);

	}
}
