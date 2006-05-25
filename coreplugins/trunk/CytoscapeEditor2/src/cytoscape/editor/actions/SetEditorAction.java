/*
 * Created on Jul 30, 2005
 */
package cytoscape.editor.actions;

import java.awt.event.ActionEvent;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/**
 * 
 * Assigns an editor for all NetworkViews in the Cytoscape environment. Defines
 * a "SetEditor" menu item.
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see CytoscapeEditorManager
 * 
 */

public class SetEditorAction extends CytoscapeAction {

	private String editorName;

	private CytoscapeEditorFactory factory;

	/**
	 * Defines a menu item for an editor and identifies the
	 * CytoscapeEditorFactory object that will be used to build the editor when
	 * it is invoked. This routine is called when an editor is registered with
	 * the CytoscapeEditorManager.
	 * 
	 * @param editorName
	 *            name of the editor
	 * @param factory
	 *            the factory object that will be used to build the editor when
	 *            the menu item is chosen
	 * @see CytoscapeEditorManager
	 */
	public SetEditorAction(String editorName, CytoscapeEditorFactory factory) {
		super(editorName);
		this.editorName = editorName;
		this.factory = factory;
		setPreferredMenu("File.SetEditor");
		System.out.println("Set editor action for editor name: " + editorName);
	}

	/**
	 * 
	 * sets up the selected editor from the File -> SetEditor menu. Disables
	 * controls for any previously assigned editors. Initializes controls for
	 * the new editor. Goes through all existing Network views and resets their
	 * NetworkEditEventHandlers to the handler associated with the new editor.
	 * 
	 * @param e
	 *            ActionEvent fired by the selection of the editor from File ->
	 *            SetEditor menu item.
	 */
	public void actionPerformed(ActionEvent e) {

		System.out.println("isSettingUpEditor, onEntry = " + CytoscapeEditorManager.isSettingUpEditor());

		CytoscapeEditorManager.setEditingEnabled(true);

		CytoscapeEditor oldEditor = CytoscapeEditorManager.getCurrentEditor();
		if (oldEditor != null) {
			oldEditor.disableControls(null);
		}
		try {

			CytoscapeEditorManager.setSettingUpEditor(true);
			// setup a new editor
			CytoscapeEditor cyEditor = factory.getEditor(editorName);

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			String viewTitle = "null";
			if (view != null) {
				viewTitle = view.getTitle();
			}

			// AJK: 10/05/05 BEGIN
			// create a new network if there are no networks yet
			if (viewTitle.equals("null")) {

				// CyNetwork newNet = Cytoscape.getCurrentNetwork();
				// System.out.println ("Got current network: " + newNet);
				CyNetwork newNet = Cytoscape.createNetwork("Net:"
						+ CytoscapeEditorManager.getNetworkNameCounter());
				CytoscapeEditorManager.incrementNetworkNameCounter();
				view = Cytoscape.createNetworkView(newNet);

				CytoscapeEditorManager.setEditorForNetwork(newNet, cyEditor);
				CytoscapeEditorManager.setEditorForView(view, cyEditor);
			}

			// setup visual style for this editor

			String visualStyleName = CytoscapeEditorManager
					.getVisualStyleForEditorType(editorName);
			System.out.println("getting visual style for: " + visualStyleName);
			if ((visualStyleName != null)
					&& (!(visualStyleName
							.equals(CytoscapeEditorManager.ANY_VISUAL_STYLE)))) {
				VisualMappingManager manager = Cytoscape.getDesktop()
						.getVizMapManager();
				CalculatorCatalog catalog = manager.getCalculatorCatalog();
				VisualStyle existingStyle = catalog
						.getVisualStyle(visualStyleName);
				System.out.println("Got visual style: " + existingStyle);
				System.out.println("getting visual style for editor: "
						+ editorName);
				if (existingStyle != null) {
					manager.setVisualStyle(existingStyle);
					// AJK: 10/15/05 set editor for visual style
					CytoscapeEditorManager.setEditorForVisualStyle(
							existingStyle, cyEditor);
				}
			}

			// AJK: 09/19/05 END

			CytoscapeEditorManager.setCurrentEditor(cyEditor);

			// System.out.println ("Set current editor to: " +
			// CytoscapeEditorManager.getCurrentEditor());
			// System.out.println ("for editor name: " + editorName);
			cyEditor.initializeControls(null);

			// set the buttons on the shapePalette to undo, redo actions
			ShapePalette palette = CytoscapeEditorManager
					.getShapePaletteForView(view);
			if (palette != null) {
				// AJK: 10/24/05 comment this out, undo functionality moving to
				// Cytoscape
				// palette.getUndoButton().setAction(CytoscapeEditorManager.getUndoActionForView(view));
				// palette.getRedoButton().setAction(CytoscapeEditorManager.getRedoActionForView(view));
			}

			CytoscapeEditorManager.setEventHandlerForView(view);
			CytoscapeEditorManager.setSettingUpEditor(false);

			// enable the "File.New" submenu
			Cytoscape.getDesktop().getCyMenus().getMenuBar()
					.getMenu("File.New").setEnabled(true);

		} catch (InvalidEditorException ex) {
			// TODO: put some error handling here
			CytoscapeEditorManager.setSettingUpEditor(false);
			ex.printStackTrace();
		}
		// AJK: 04/02/06 BEGIN
		// Cytoscape.getDesktop().redrawGraph();
		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		System.out.println("isSettingUpEditor, onExit = " + CytoscapeEditorManager.isSettingUpEditor());
		// AJK: 04/02/06 END
	}
}