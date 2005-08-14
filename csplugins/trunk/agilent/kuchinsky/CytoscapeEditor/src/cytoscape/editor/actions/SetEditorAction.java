/*
 * Created on Jul 30, 2005
 */
package cytoscape.editor.actions;

import java.awt.event.ActionEvent;

import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.util.CytoscapeAction;

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
	 * sets up the selected editor from the File -> SetEditor menu.  Disables controls for any previously assigned 
	 * editors.  Initializes controls for the new editor.  Goes through all existing Network views and resets 
	 * their NetworkEditEventHandlers to the handler associated with the new editor.
	 * @param e ActionEvent fired by the selection of the editor from File -> SetEditor menu item.
	 */
	public void actionPerformed(ActionEvent e) {
		
		CytoscapeEditor oldEditor = CytoscapeEditorManager.getCurrentEditor();
		if (oldEditor != null)
		{
			oldEditor.disableControls(null);
		}
		try
		{
			
			// setup a new editor
			CytoscapeEditor cyEditor = factory.getEditor(editorName);
			CytoscapeEditorManager.setCurrentEditor(cyEditor);
//			System.out.println ("Set current editor to: " + CytoscapeEditorManager.getCurrentEditor());
//			System.out.println ("for editor name: " + editorName);
			cyEditor.initializeControls(null);
			
			CytoscapeEditorManager.resetEventHandlerForExistingViews();
			
		}
		catch (InvalidEditorException ex)
		{
		    // TODO: put some error handling here	
		}
	}
}	