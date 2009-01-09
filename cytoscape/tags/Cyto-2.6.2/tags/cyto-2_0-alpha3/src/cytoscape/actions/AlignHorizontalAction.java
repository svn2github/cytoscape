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
public class AlignHorizontalAction extends AbstractAction {
    NetworkView networkView;
    
    public AlignHorizontalAction(NetworkView networkView) {
        super("Horizontal");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        GraphView view = networkView.getView();
        double avgYcoord=0;
        
        List selected_nodes = view.getSelectedNodes();
        Iterator node_iterator;
        
        node_iterator = selected_nodes.iterator();
        while( node_iterator.hasNext() ) {
          avgYcoord += ( ( NodeView )node_iterator.next() ).getYPosition();
        }

        node_iterator = selected_nodes.iterator();
        while( node_iterator.hasNext() ) {
          ( ( NodeView )node_iterator.next() ).setYPosition( avgYcoord );
        }
    }
}

