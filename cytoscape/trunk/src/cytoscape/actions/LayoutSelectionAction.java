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
      if ( cyWindow.getCytoscapeObj().getConfiguration().isYFiles() ) {  			  
	      String callerID = "LayoutSelectionAction.actionPerformed";
	      cyWindow.getNetwork().beginActivity(callerID);
	      cyWindow.applyLayoutSelection();
	      cyWindow.redrawGraph(false, false);
	      cyWindow.getNetwork().endActivity(callerID);
      }
      else {
	       cyWindow.applySelLayout();
      }
    }
}

