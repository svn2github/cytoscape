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
public class ExitAction extends AbstractAction {
    CyWindow cyWindow;
    
    public ExitAction(CyWindow cyWindow) {
        super("Exit");
        this.cyWindow = cyWindow;
    }
    
    public void actionPerformed (ActionEvent e) {
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        cyWindow.getCytoscapeObj().getParentApp().exit(0);
    }
}

