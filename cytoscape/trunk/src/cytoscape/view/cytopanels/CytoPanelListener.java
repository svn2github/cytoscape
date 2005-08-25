//     
// $Id$
//------------------------------------------------------------------------------

// our package
package cytoscape.view.cytopanels;

// imports
import java.util.EventListener;

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
     * @param newState The new CytoPanel state - see CytoPanelState class.
     */
    public void onStateChange(CytoPanelState newState);

    /**
     * Notifies the listener when a new component on the CytoPanel is selected.
     *
	 * @param componentIndex The index of the component selected.
     */
    public void onComponentSelected(int componentIndex);

    /**
     * Notifies the listener when a component is added to the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel after the add.
     */
    public void onComponentAdded(int count);

    /**
     * Notifies the listener when a component is removed from the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel after the remove.
     */
    public void onComponentRemoved(int count);
}
