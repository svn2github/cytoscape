//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.layout.EmbeddedLayouter;

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class EmbeddedLayoutAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public EmbeddedLayoutAction(CytoscapeWindow cytoscapeWindow) {
        super("Embedded");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.setLayouter( new EmbeddedLayouter() );
    }
}

