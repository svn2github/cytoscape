//
// UndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

/**
 * Public interface of undoable actions.
 */
public interface UndoItem {

    /**
     * Apply the state corresponding saved by this UndoItem at
     * construction time.  Implementing classes are responsible for
     * ensuring that the state change FULLY returns the current state
     * to the one recorded by the instance.  Should return true for
     * success, false for failure.
     */
    boolean undo();


    /**
     * This function is responsible for undoing the undo.  It will
     * never be called unless there has been a corresponding call to
     * undo().  The implementing class is responsbile for ensuring
     * that the state FULLY returns to the state of the system before
     * the undo() was applied.
     *
     * One common way of implementing this is as follows: at the start
     * of the undo() function, create a new UndoItem of the same type
     * corresponding to the then-current state of the graph.  Then,
     * use that instance's undo() function to "undo the undo," thereby
     * returning the graph to its original state.
     */
    boolean redo();
}
