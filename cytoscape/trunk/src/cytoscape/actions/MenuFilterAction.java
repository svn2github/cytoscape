//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class MenuFilterAction extends MainFilterDialogAction {
    NetworkView networkView;
    
    public MenuFilterAction(NetworkView networkView) {
        super(networkView, "Using filters..."); 
    }
}
