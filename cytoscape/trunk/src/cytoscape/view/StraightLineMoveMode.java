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
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// StraightLineMoveMode.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.view;

import y.view.*;
import java.awt.event.*;


/**
 * Move mode for moving nodes and edges along a straight line if the
 * shift key modifier is pressed.
 */
public class StraightLineMoveMode extends MoveSelectionMode {
    /**
     * The origin of the drag, for straight line-ing.
     */
    double moveOriginX, moveOriginY;

    /**
     * Mouse coordinate orgin of the drag
     */
    int mouseOriginX, mouseOriginY;


    /**
     * Takes one of the following values: NO_DRAG, X_DRAG, Y_DRAG
     */
    int moveMode;

    /**
     * Neither X nor Y has yet been determined to be the primary
     * dragging direction.
     */
    static final int NO_DRAG = 0;

    /**
     * X has been determined to be the primary dragging direction.
     */
    static final int DRAG_X = 1;

    /**
     * Y has been determined to be the primary dragging direction.
     */
    static final int DRAG_Y = 2;

    /**
     * Default constructor
     */
    public StraightLineMoveMode () { }

    /**
     * Save the x and y coordinates of mouse-downs
     */
    public void mousePressed (MouseEvent e) {
	moveMode = NO_DRAG;
	mouseOriginX = e.getX();
	mouseOriginY = e.getY();
	moveOriginX = translateX(mouseOriginX);
	moveOriginY = translateY(mouseOriginY);

	super.mousePressed(e);
    }

    /**
     * Grabs mouse-drag events initiated with a left-click.
     *
     * Depending on the status of moveMode, holding down shift will
     * move the mouse along a straight vertical or horizontal line
     * going through the origin of the move.
     */
    public void mouseDraggedLeft (double x, double y) {
	if (lastDragEvent.isShiftDown()) {
	    double mouseDeltaX = Math.abs(lastDragEvent.getX() - mouseOriginX);
	    double mouseDeltaY = Math.abs(lastDragEvent.getY() - mouseOriginY);

	    // if we don't know which way to drag, so try to figure it out
	    if (moveMode == NO_DRAG) {
		double deltax = Math.abs(x - moveOriginX);
		double deltay = Math.abs(y - moveOriginY);

		// require that you move at least 4 pixels in
		// one direction or the other.

		if ((mouseDeltaX > 4) || (mouseDeltaY > 4)) {
		    if (deltax > 2 * deltay)
			moveMode = DRAG_X;

		    else if (deltay > 2 * deltax)
			moveMode = DRAG_Y;
		}
	    }

	    // dragging along x
	    if (moveMode == DRAG_X) {
		super.mouseDraggedLeft(x, moveOriginY);

		// user probably wants to DRAG_Y instead
		if (mouseDeltaY > 20+2*mouseDeltaX)
		    moveMode = DRAG_Y;
	    }

	    // dragging along y
	    else if (moveMode == DRAG_Y) {
		super.mouseDraggedLeft(moveOriginX, y);

		// user probably wants to DRAG_Y instead
		if (mouseDeltaX > 20+2*mouseDeltaY)
		    moveMode = DRAG_X;
	    }
	}

	// unshifted - move as normal
	else
	    super.mouseDraggedLeft(x, y);
    }
}



