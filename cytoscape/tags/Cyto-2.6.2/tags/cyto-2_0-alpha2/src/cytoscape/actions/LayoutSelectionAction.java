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
public class LayoutSelectionAction extends AbstractAction {
    CyWindow cyWindow;
    
    public LayoutSelectionAction (CyWindow cyWindow) {
        super("Layout current selection");
        this.cyWindow = cyWindow;
    }

    public void actionPerformed (ActionEvent e) {
        cyWindow.applySelLayout();
        cyWindow.redrawGraph(false, false);
    }
}

