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
public class CloseWindowAction extends AbstractAction {
    CyWindow cyWindow;
    
    public CloseWindowAction(CyWindow cyWindow) {
        super("Close");
        this.cyWindow = cyWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        cyWindow.getMainFrame().dispose();
    }
}

