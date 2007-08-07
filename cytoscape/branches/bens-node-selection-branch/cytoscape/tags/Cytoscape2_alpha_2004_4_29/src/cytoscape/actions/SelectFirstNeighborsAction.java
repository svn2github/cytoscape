//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.Vector;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
/**
 *  select every first neighbor (directly connected nodes) of the currently
 *  selected nodes.
 */
public class SelectFirstNeighborsAction extends AbstractAction {
    NetworkView networkView;
    
    public SelectFirstNeighborsAction (NetworkView networkView) { 
        super ("First neighbors of selected nodes"); 
        this.networkView = networkView;
    }
    public void actionPerformed (ActionEvent e) {
        GinyUtils.selectFirstNeighbors(networkView.getView());
        networkView.redrawGraph(false, false);
    } // actionPerformed
} // SelectFirstNeighborsAction

