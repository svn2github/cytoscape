//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import phoebe.PGraphView;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
//-------------------------------------------------------------------------
public class FitContentAction extends CytoscapeAction {
      
    public FitContentAction () {
        super();
    }
    
    public void actionPerformed(ActionEvent e) {
      //we have to do it this way because 
      //networkView.getView().fitContent();
        //currently appears to do nothing -AM 12-17-2003
      PGraphView view =(PGraphView) Cytoscape.getCurrentNetworkView();
      view.getCanvas().getCamera().animateViewToCenterBounds( view.getCanvas().getLayer().getFullBounds(), true, 50l );
 

   }
}

