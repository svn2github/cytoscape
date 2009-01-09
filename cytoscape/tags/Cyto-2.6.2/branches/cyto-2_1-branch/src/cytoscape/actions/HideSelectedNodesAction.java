//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;
//-------------------------------------------------------------------------
public class HideSelectedNodesAction extends CytoscapeAction   {
    
    public HideSelectedNodesAction () {
        super("Hide selection");
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_H, ActionEvent.CTRL_MASK );
    }

  public HideSelectedNodesAction ( boolean label ) {
    super();
       
  }

    public void actionPerformed ( ActionEvent e ) {
      GinyUtils.hideSelectedNodes( Cytoscape.getCurrentNetworkView() );
    }

}
