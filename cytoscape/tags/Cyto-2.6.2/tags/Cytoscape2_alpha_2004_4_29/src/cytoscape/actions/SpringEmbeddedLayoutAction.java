//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.util.SpringEmbeddedLayouter;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class SpringEmbeddedLayoutAction extends AbstractAction {
    
    NetworkView networkView;
    
    public SpringEmbeddedLayoutAction(NetworkView networkView) {
        super("Apply Spring Embedded Layout");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(networkView.getView());
	lay.doLayout();
    }
}

