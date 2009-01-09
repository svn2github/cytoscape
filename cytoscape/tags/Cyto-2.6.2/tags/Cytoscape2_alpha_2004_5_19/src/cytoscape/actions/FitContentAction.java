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
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class FitContentAction extends AbstractAction {
    NetworkView networkView;
    
    public FitContentAction(NetworkView networkView) {
        super();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
      //we have to do it this way because 
      //networkView.getView().fitContent();
        //currently appears to do nothing -AM 12-17-2003
        PGraphView view =(PGraphView) networkView.getView();
        view.getCanvas().getCamera().animateViewToCenterBounds( view.getCanvas().getLayer().getFullBounds(), true, 50l );
 

   }
}

