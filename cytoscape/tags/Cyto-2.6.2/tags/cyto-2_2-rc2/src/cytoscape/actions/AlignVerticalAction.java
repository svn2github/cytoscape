//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.*;

import giny.view.*;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class AlignVerticalAction extends AbstractAction {
    NetworkView networkView;
    
    public AlignVerticalAction(NetworkView networkView) {
        super("Vertical");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        GraphView view = networkView.getView();
        double avgXcoord=0;
        
        List selected_nodes = view.getSelectedNodes();
        Iterator node_iterator;
        
        node_iterator = selected_nodes.iterator();
        while( node_iterator.hasNext() ) {
          avgXcoord += ( ( NodeView )node_iterator.next() ).getXPosition();
        }

        node_iterator = selected_nodes.iterator();
        while( node_iterator.hasNext() ) {
          NodeView nv = ( NodeView )node_iterator.next();
          nv.setXPosition( avgXcoord ); 
        }
    }
}

