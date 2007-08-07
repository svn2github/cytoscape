package cytoscape.layout;
import giny.view.NodeView;

import java.util.Collection;
import java.util.Iterator;

import cytoscape.*;
import cytoscape.view.CyNetworkView;

/**
 * A class with easy to call static methods for layout purposes.
 * 
 * @author Iliana Avila-Campillo
 * @since 2.3
 */
public class LayoutUtils {
	
	/**
	 * Layouts a collection of nodes in a "stack" (one node on top of the other, vertically)
	 * 
	 * @param network_view  the CyNetworkView that should be modified
	 * @param nodes the nodes whose position will be modified
	 * @param x_position the x position for the nodes
	 * @param y_start_position the y starting position for the stack (where the first node will be positioned, all other nodes below it)
	 */
    public static void layoutNodesInAStack (CyNetworkView network_view, Collection nodes, double x_position, double y_start_position){
        
        Iterator it = nodes.iterator();
        double yPosition = y_start_position;
        while(it.hasNext()){
            CyNode node = (CyNode)it.next();
            NodeView nodeView = network_view.getNodeView(node);
            nodeView.setXPosition(x_position);
            nodeView.setYPosition(yPosition);
            yPosition += nodeView.getHeight() * 2;
        }
        
    }
	
}