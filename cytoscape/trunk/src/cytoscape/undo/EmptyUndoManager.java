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

// EmptyUndoManager.java
//
// $Revision$
// $Date$
// $Author$
//
package cytoscape.undo;

import cytoscape.CytoscapeWindow;
import y.view.Graph2D;
import y.base.GraphEvent;
/**
 * This class serves as a do-nothing replacement for CytoscapeUndoManager.
 * An instance of this class is installed in CytoscapeWindow when the
 * undo feature should be disabled as much as possible. 
 */
public class EmptyUndoManager extends CytoscapeUndoManager {
    
    /* CytoscapeUndoManager requires the following constructor */
    public EmptyUndoManager(CytoscapeWindow cytoscapeWindow, Graph2D graph) {
        super(cytoscapeWindow, graph);
    }
    
    /* methods defined in UndoManager */
    public boolean undo() {return false;}
    public boolean redo() {return false;}
    public int undoLength() {return 0;}
    public int redoLength() {return 0;}
    public void saveState() {}
    public void clearHistory() {}
    public void pause() {}
    public void resume() {}
    public void beginUndoItemList() {}
    public void endUndoItemList() {}
    
    /* methods defined in CytoscapeUndoManager */
    public void onGraphEvent(GraphEvent e) {}
    public void saveRealizerState() {}
}

