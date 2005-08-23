// our package
package cytoscape.view.cytopanel;

// imports
import java.util.EventListener;
import cytoscape.view.cytopanel.CytoPanelState;

/**
 * This listener interface provides the
 * mechanism to respond to CytoPanel Events.
 *
 * @author Ben Gross
 */
public interface CytoPanelListener extends EventListener {

    /**
     * Notifies the listener on a change in the CytoPanel state.
     *
     * @param newState The new CytoPanel state - see CytoPanelConstants class.
     */
    public void onStateChange(CytoPanelState newState);

    /**
     * Notifies the listener when a new component on the CytoPanel is selected.
     *
	 * @param componentIndex The index of the component selected.
     */
    public void onComponentSelected(int componentIndex);
}
