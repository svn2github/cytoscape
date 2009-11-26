package giny.model;

import giny.view.GraphView;

/**
 * This event will be fired when nested network is created/removed.
 * 
 * @author kono
 *
 */
public interface NestedNetworkChangeEvent {
	/**
	 * 
	 * @return Node points to the nested network.
	 */
	public Node getNestedNode();
	
	/**
	 * Nested network view pointed by a node in other network.
	 * This can be null because network view may not be available at this point.
	 * 
	 * @return nested network.
	 */
	public GraphView getNestedNetworkView();

}
