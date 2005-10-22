/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.impl.ShapePalette;

/**
 * action called when user invokes "undo" operation
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see RedoAction, UndoManager
 */
public class UndoAction extends AbstractAction {
	
	UndoManager undo;
	RedoAction redoAction;
	
	
	/**
	 * defines the method that is invoked when the user performs an "undo delete" operation.
	 * @param undo
	 */
	public UndoAction(UndoManager undo) {
		
		super("");
		this.undo = undo;
		setEnabled(false);
	}
	

	/**
	 * 
	 * method that is executed when the user performed an "undo delete" operation.
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			// AJK: 09/05/05 BEGIN
			// accommodate one UndoManager per NetworkView
//			undo.undo();
//			UndoManager undoMgr = CytoscapeEditorManager.getCurrentUndoManager();
			UndoManager undoMgr =
				CytoscapeEditorManager.getUndoManagerForView(Cytoscape.getCurrentNetworkView());
			undoMgr.undo();
//			undoMgr.end();
			// AJK: 09/05/05 END
		} catch (CannotUndoException ex) {
			System.out.println("Unable to undo: " + ex);
//			ex.printStackTrace();
		}
		
		update();
		System.out.println ("updating redoAction for" + this + " = "+ redoAction);
//		 AJK: 10/21/05 try setting true of false on update 
		redoAction.update();
//		redoAction.update(true);
	}

	/**
	 * enables and disables undo and redo operations, according to the last operation performed.
	 *
	 */
	public void update() {
		// AJK: 09/05/05
		// accommodate one UndoManager per NetworkView
//		if (undo.canUndo()) {
		UndoManager undoMgr = CytoscapeEditorManager.getUndoManagerForView(
				Cytoscape.getCurrentNetworkView());
		ShapePalette palette = CytoscapeEditorManager.getShapePaletteForView(
				Cytoscape.getCurrentNetworkView());
		if (undoMgr.canUndo()) {
		// AJK: 09/05/05 END		
			setEnabled(true);
			// AJK: 10/21/05 No name, just use button
//			putValue(Action.NAME, undo.getUndoPresentationName());
			if (palette != null)
			{
				palette.getUndoButton().setEnabled(true);
			}
		} else {
			setEnabled(false);
			// AJK: 10/21/05 No name, just use button
//			putValue(Action.NAME, null);
			if (palette != null)
			{
				palette.getUndoButton().setEnabled(false);
			}
		}
	}
	/**
	 * defines a redo action that corresponds with this UndoAction and is (or should be) the inverse of the 
	 * functionality of the undo operation.
	 * @param redoAction The redoAction to set.
	 */
	public void setRedoAction(RedoAction redoAction) {
		System.out.println ("Setting redo action for undo action: " + this + " = " + redoAction);
		this.redoAction = redoAction;
	}
}

