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
public class ReadOnlyModeAction extends AbstractAction {
    CyWindow cyWindow;
    
    public ReadOnlyModeAction(CyWindow cyWindow) {
        super("Read only Mode");
        this.cyWindow = cyWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cyWindow.switchToReadOnlyMode();
    }
}

