package giny.model;

/**
 * Listener for nested network change events.
 * <p>
 * This can handle both creation and deletion of nested networks.
 * 
 * @author kono
 *
 */
public interface NestedNetworkChangeEventListener {
	
	public void nestedNetworkChanged(NestedNetworkChangeEvent evt);

}
