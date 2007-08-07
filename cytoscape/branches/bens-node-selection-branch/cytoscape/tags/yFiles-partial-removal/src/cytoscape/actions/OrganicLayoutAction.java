//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.organic.OrganicLayouter;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class OrganicLayoutAction extends AbstractAction {
    NetworkView networkView;
    
    public OrganicLayoutAction (NetworkView networkView) {
        super("Organic");
        this.networkView = networkView;
    }
    
    public void actionPerformed (ActionEvent e) {
        OrganicLayouter ol = new OrganicLayouter();
        ol.setActivateDeterministicMode(true);
        ol.setPreferredEdgeLength(80);
        networkView.setLayouter(ol);
    }
}

