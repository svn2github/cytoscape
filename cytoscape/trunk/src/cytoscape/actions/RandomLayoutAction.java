//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.random.RandomLayouter;

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class RandomLayoutAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public RandomLayoutAction(CytoscapeWindow cytoscapeWindow) {
        super("Random");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.setLayouter( new RandomLayouter() );
    }
}

