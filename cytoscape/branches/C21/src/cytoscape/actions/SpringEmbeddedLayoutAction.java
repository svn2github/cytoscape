//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.util.SpringEmbeddedLayouter;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

public class SpringEmbeddedLayoutAction extends CytoscapeAction {
    
  public SpringEmbeddedLayoutAction () {
    super("Apply Spring Embedded Layout");
    setPreferredMenu( "Layout" );
  }
    
  public void actionPerformed ( ActionEvent e ) {
    SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter( Cytoscape.getCurrentNetworkView() );
    lay.doLayout();
  }
}

