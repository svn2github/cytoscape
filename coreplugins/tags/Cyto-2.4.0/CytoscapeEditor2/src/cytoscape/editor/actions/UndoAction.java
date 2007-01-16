/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import cytoscape.editor.CytoscapeEditorManager;

/**
 * action called when user invokes "undo" operation
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see RedoAction, UndoManager
 */
public class UndoAction extends AbstractAction {
	
	// MLC 09/14/06:
	private static final long serialVersionUID = -5223342344074921377L;
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
			undo.undo();
		} catch (CannotUndoException ex) {
			CytoscapeEditorManager.log("Unable to undo: " + ex);
		}
		
		update();
		redoAction.update();
	}

	/**
	 * enables and disables undo and redo operations, according to the last operation performed.
	 *
	 */
	public void update() {
		if (undo.canUndo()) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

	/**
	 * defines a redo action that corresponds with this UndoAction and is (or should be) the inverse of the 
	 * functionality of the undo operation.
	 * @param redoAction The redoAction to set.
	 */
	public void setRedoAction(RedoAction redoAction) {
		this.redoAction = redoAction;
	}
}

