//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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


