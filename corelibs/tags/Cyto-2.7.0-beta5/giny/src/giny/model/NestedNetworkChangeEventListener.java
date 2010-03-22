package giny.model;

import java.util.EventListener;

/**
 * Listener for nested network change events.
 * <p>
 * This can handle both creation and deletion of nested networks.
 * 
 * @author kono
 *
 */
public interface NestedNetworkChangeEventListener extends EventListener {
	
	public void nestedNetworkChanged(NestedNetworkChangeEvent evt);

}
