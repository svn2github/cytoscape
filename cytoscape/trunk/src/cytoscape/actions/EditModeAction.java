//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.CyWindow;
//-------------------------------------------------------------------------
public class EditModeAction extends AbstractAction {
    CyWindow cyWindow;
    
    public EditModeAction(CyWindow cyWindow) {
        super("Edit Mode for Nodes and Edges");
        this.cyWindow = cyWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cyWindow.switchToEditMode();
    }
}

