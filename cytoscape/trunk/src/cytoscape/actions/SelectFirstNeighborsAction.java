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

import y.base.*;
import y.view.Graph2D;
import y.view.NodeRealizer;

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
        Graph2D graph = networkView.getNetwork().getGraph ();
        NodeCursor nc = graph.selectedNodes (); 
        Vector newNodes = new Vector ();
        
        // for all selected nodes
        for (nc.toFirst (); nc.ok (); nc.next ()) {
            Node node = nc.node ();
            EdgeCursor ec = node.edges ();
            
            for (ec.toFirst (); ec.ok (); ec.next ()) {
                Edge edge = ec.edge ();
                Node source = edge.source ();
                if (!newNodes.contains (source))
                    newNodes.add (source);
                    Node target = edge.target ();
                    if (!newNodes.contains (target))
                        newNodes.add (target);
            } // for edges
        } // for selected nodes
        
        for (int i=0; i < newNodes.size (); i++) {
            Node node = (Node) newNodes.elementAt (i);
            NodeRealizer realizer = graph.getRealizer (node);
            realizer.setSelected (true);
        }
        
        networkView.redrawGraph (false, false);
    } // actionPerformed
    
} // SelectFirstNeighborsAction
