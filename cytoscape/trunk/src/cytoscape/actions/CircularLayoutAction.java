//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.circular.CircularLayouter;

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class CircularLayoutAction extends AbstractAction   {
    CytoscapeWindow cytoscapeWindow;
    
    public CircularLayoutAction (CytoscapeWindow cytoscapeWindow) {
        super("Circular");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.setLayouter( new CircularLayouter() );
    }
}

