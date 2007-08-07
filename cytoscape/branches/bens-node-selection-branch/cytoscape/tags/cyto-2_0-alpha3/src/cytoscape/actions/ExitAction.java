//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

//-------------------------------------------------------------------------
public class ExitAction extends CytoscapeAction {
       
  public ExitAction () {
    super("Exit");
    setPreferredMenu( "File" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_Q, ActionEvent.CTRL_MASK );
  }
    
  public void actionPerformed (ActionEvent e) {
    //TODO: Other Exit stuff....
    Cytoscape.getCytoscapeObj().saveCalculatorCatalog();
    Cytoscape.getCytoscapeObj().getParentApp().exit(0);
  }
}

