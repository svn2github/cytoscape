//
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
