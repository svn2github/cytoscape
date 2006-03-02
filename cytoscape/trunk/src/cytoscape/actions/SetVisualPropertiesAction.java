//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.actions;
//------------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;

//------------------------------------------------------------------------------
public class SetVisualPropertiesAction extends CytoscapeAction   {

   
  public SetVisualPropertiesAction ( ImageIcon icon ) {
    super("Set VizMapper\u2122", icon );
    setPreferredMenu( "View" );
  }
  
  /** The constructor that takes a boolean shows no label,
   *  no matter what the value of the boolean actually is.
   *  This makes is appropriate for an icon, but inappropriate
   *  for the pulldown menu system. */
  public SetVisualPropertiesAction ( boolean showLabel) {
    super();
  }
    
  public void actionPerformed (ActionEvent e) {
    //TODO: ack! this should be using the global VizMapper
    Cytoscape.getDesktop().getVizMapUI().refreshUI();
    Cytoscape.getDesktop().getVizMapUI().getStyleSelector().show();
  }
}

