

package org.cytoscape.work;

/** 
 * A Cytoscape undo interface.  This is meant to be implemented by {@link Command}s
 * such the operation of the command can be undone.  This is a simplified version of
 * {@link javas.swing.undo.UndoableEdit} suitable for non-Swing environments. 
 */
public interface Undoable {

	/**
	 * When executed this method undoes the {@link Command} that was executed. 
	 * The system state should be returned to what it was prior the execution of
	 * {@link Command#execute()}.
	 */
	public void undo();

	/**
	 * 
	 * When executed this method undoes the {@link Command} that was executed. 
	 * This method should leave the system state the same as if only the 
	 * {@link Command#execute()} method had been called.
	 */
	public void redo();

	/**
	 * Indicates whether this {@link Command} can be undone. 
	 */
	public boolean canUndo();

	/**
	 *
	 * Indicates whether this {@link Command} can be redone. 
	 */
	public boolean canRedo();

	/**
	 * This method should return a string that is suitable for presentation
	 * in a user interface. The name should be short (appr. 1-5 words) and
	 * you should assume that the user interface will prepend the words 
	 * <i>UNDO:</i> and <i>REDO:</i> to the presentation name returned. 
	 * Choose wisely!
	 * <p>
	 * For example, if the {@link Command} is <code>ApplyLayout</code> then
	 * the presentation name might be "Apply Layout" such that the user
	 * interface will present the text:
	 * <p>
	 * <b>UNDO: <i>Apply Layout</i></b>
	 * <p>
	 * and
	 * <p>
	 * <b>REDO: <i>Apply Layout</i></b>
	 * <p>
	 * to the user.
	 * @return A presentation name suitable for inclusion in user interfaces
	 * prefixed with the words <b>UNDO:</b> and <b>REDO:</b>.
	 */
	public String getPresentationName();	
}
