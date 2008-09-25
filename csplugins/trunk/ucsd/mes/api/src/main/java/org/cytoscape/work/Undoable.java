
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.work;

/** 
 * A Cytoscape undo interface.  This is meant to be implemented by {@link Command}s
 * such the operation of the command can be undone.  This is a simplified version of
 * {@link javax.swing.undo.UndoableEdit} suitable for non-Swing environments. 
 */
public interface Undoable {
	/**
	 * When executed this method undoes the {@link Command} that was executed.  The system
	 * state should be returned to what it was prior the execution of {@link Command#execute()}.
	 */
	public void undo();

	/**
	 * When executed this method undoes the {@link Command} that was executed.  This method
	 * should leave the system state the same as if only the  {@link Command#execute()} method had
	 * been called.
	 */
	public void redo();

	/**
	 * Indicates whether this {@link Command} can be undone.
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean canUndo();

	/**
	 * Indicates whether this {@link Command} can be redone.
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean canRedo();

	/**
	 * This method should return a string that is suitable for presentation in a user
	 * interface. The name should be short (appr. 1-5 words) and you should assume that the user
	 * interface will prepend the words  <i>UNDO:</i> and <i>REDO:</i> to the presentation name
	 * returned.  Choose wisely!<p>For example, if the {@link Command} is
	 * <code>ApplyLayout</code> then the presentation name might be "Apply Layout" such that the
	 * user interface will present the text:</p>
	 *  <p><b>UNDO: <i>Apply Layout</i></b></p>
	 *  <p>and</p>
	 *  <p><b>REDO: <i>Apply Layout</i></b></p>
	 *  <p>to the user.</p>
	 *
	 * @return A presentation name suitable for inclusion in user interfaces that will be prefixed
	 *         with the words <b>UNDO:</b> and <b>REDO:</b>.
	 */
	public String getPresentationName();
}
