package giny.model;

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
	 * Nested network pointed by a node in other network.
	 * 
	 * @return nested network.
	 */
	public GraphPerspective getNestedNetwork();

}
