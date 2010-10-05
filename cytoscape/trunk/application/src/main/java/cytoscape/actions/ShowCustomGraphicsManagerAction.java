package cytoscape.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.visual.customgraphic.RestoreImageTask;
import cytoscape.visual.customgraphic.ui.CustomGraphicsManagerDialog;

public class ShowCustomGraphicsManagerAction extends CytoscapeAction {

	private static final long serialVersionUID = 5876533870116518191L;

	private static final String TITLE = "Open Custom Graphics Manager";
	private static final String ICON_LOCATION = "images/ximian/stock_symbol-selection-16.png";
	private static final Icon ICON =  new ImageIcon(Cytoscape.class.getResource(ICON_LOCATION));
	
	private static final String MENU_NAME = "View";

	private CustomGraphicsManagerDialog manager;

	/**
	 * Creates a new SetVisualPropertiesAction object.
	 */
	public ShowCustomGraphicsManagerAction() {
		super(TITLE, ICON);
		setPreferredMenu(MENU_NAME);
		setEnabled(true);
	}

	/**
	 * Display custom graphics manager
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// Add custom graphics manager

		if (manager == null) {
			// Create Task
			final RestoreImageTask task = new RestoreImageTask();

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();

			jTaskConfig.displayCancelButton(false);
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(false);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);

			manager = new CustomGraphicsManagerDialog(Cytoscape.getDesktop(), false);
		}

		manager.setLocationRelativeTo(Cytoscape.getDesktop());
		manager.setVisible(true);
	}
}
