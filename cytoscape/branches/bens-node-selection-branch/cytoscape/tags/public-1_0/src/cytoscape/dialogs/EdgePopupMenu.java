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

// EdgePopupMenu.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.dialogs;

import cytoscape.*;
import javax.swing.*;
import y.base.*;
import y.view.*;
import java.awt.event.*;


/**
 * The JPopupMenu that pops up when the user right-clicks an edge.
 */
public class EdgePopupMenu extends JPopupMenu {

    CytoscapeWindow window;
    Edge edge;

    public EdgePopupMenu (CytoscapeWindow window, Edge edge) {
	this.window = window;
	this.edge = edge;
	
	setLabel(window.getGraph().getLabelText(edge));
	add(new EdgeAttributesAction());
    }


    protected class EdgeAttributesAction extends AbstractAction {
	EdgeAttributesAction () { super("Edge Attributes"); }

	public void actionPerformed (ActionEvent e) {
	    String name = window.getGraph().getLabelText(edge);
	    if (name.length() == 0)
		name = window.getEdgeAttributes().getCanonicalName(edge);
	    
	    JDialog dialog = new EdgeAttributesPopupDetails
		(window.getMainFrame(), name, window.getEdgeAttributes());

	    dialog.pack();
	    dialog.setLocationRelativeTo(window.getMainFrame());
	    dialog.setVisible(true);
	}
    }
}


