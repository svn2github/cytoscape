/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import cytoscape.editor.CytoscapeEditorManager;


/**
 * redo an operation that has been undone
 * @author Allan Kuchinsky, Agilent Technologies
 *
 */
public class RedoAction extends AbstractAction {
    // MLC 09/14/06:
    private static final long serialVersionUID = -6492270531850001461L;
    UndoManager               undo;
    UndoAction                undoAction;

    /**
     * defines the method that is invoked when the user performs a "redo" of an undone operation.
     * @param undo
     */
    public RedoAction(UndoManager undo) {
        super("");
        this.undo = undo;
        setEnabled(false);
    }

    /**
     *
     * method that is executed when the user performed an "redo delete" operation.
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        try {
            undo.redo();
        } catch (CannotRedoException ex) {
            CytoscapeEditorManager.log("Unable to redo: " + ex);
        }

        update();
        undoAction.update();
    }

    /**
     * enables and disables undo and redo operations, according to the last operation performed.
     *
     */
    public void update() {
        if (undo.canRedo()) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    public void update(boolean redoFlag) {
        if ((undo.canRedo() || (redoFlag))) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    /**
     * defines an undo action that corresponds with this RedoAction and is (or should be) the inverse of the
     * functionality of the redo operation.
     * @param undoAction The UndoAction to set.
     */
    public void setUndoAction(UndoAction undoAction) {
        this.undoAction = undoAction;
    }
}
