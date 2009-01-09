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

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class EmbeddedLayoutAction extends AbstractAction {
    NetworkView networkView;
    
    public EmbeddedLayoutAction(NetworkView networkView) {
        super("Embedded");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {
        networkView.setLayouter( new EmbeddedLayouter() );
    }
}

