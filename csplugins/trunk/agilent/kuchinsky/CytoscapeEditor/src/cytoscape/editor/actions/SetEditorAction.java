/*
 * Created on Jul 30, 2005
 */
package cytoscape.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.undo.UndoManager;

import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/**
 * 
 * Assigns an editor for all NetworkViews in the Cytoscape environment.  Defines a "SetEditor" menu item.
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
	 * Defines a menu item for an editor and identifies the CytoscapeEditorFactory object that will be used to
	 * build the editor when it is invoked.  This routine is called when an editor is registered with the 
	 * CytoscapeEditorManager.
	 * @param editorName name of the editor
	 * @param factory the factory object that will be used to build the editor when the menu item is chosen
	 * @see CytoscapeEditorManager
	 */
	public SetEditorAction(String editorName, CytoscapeEditorFactory factory) {
		super(editorName);
		this.editorName = editorName;
		this.factory = factory;
		setPreferredMenu("File.SetEditor");
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

		CytoscapeEditor oldEditor = CytoscapeEditorManager.getCurrentEditor();
		if (oldEditor != null) {
			oldEditor.disableControls(null);
		}
		try {

			// setup a new editor
			CytoscapeEditor cyEditor = factory.getEditor(editorName);

			// AJK: 09/19/05 BEGIN
			//     setup visual style for this editor
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
				if (existingStyle != null) {
					manager.setVisualStyle(existingStyle);
				}
			}

			// AJK: 09/19/05 END

			CytoscapeEditorManager.setCurrentEditor(cyEditor);
			//			System.out.println ("Set current editor to: " +
			// CytoscapeEditorManager.getCurrentEditor());
			//			System.out.println ("for editor name: " + editorName);
			cyEditor.initializeControls(null);

			CytoscapeEditorManager.resetEventHandlerForExistingViews();
			
			// AJK: 09/29/05 BEGIN
			//   setup and undo manager for this network view
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			Object undoObj = CytoscapeEditorManager.getUndoManagerForView(view);
			if (undoObj instanceof UndoManager)
			{
				CytoscapeEditorManager.setCurrentUndoManager ((UndoManager) undoObj);
			}
			else
			{
				UndoManager newUndo = new UndoManager();
				CytoscapeEditorManager.setUndoManagerForView (view, newUndo);
				CytoscapeEditorManager.setCurrentUndoManager (newUndo);
			}
			// AJK: 09/29/05 END

		} catch (InvalidEditorException ex) {
			// TODO: put some error handling here
			ex.printStackTrace();
		}
	}
}	