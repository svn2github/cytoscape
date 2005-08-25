//     
// $Id$
//------------------------------------------------------------------------------

// our package
package cytoscape.actions;

// imports
import java.awt.event.ActionEvent;
import javax.swing.JCheckBoxMenuItem;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.view.cytopanels.CytoPanelListener;

/**
 * Menu item handler for CytoPanels
 */
public class CytoPanelAction extends CytoscapeAction implements CytoPanelListener {

	/**
	 * Reference to our CytoPanel.
	 */
	CytoPanel cytoPanel;

	/**
	 * Maintains state of CytoPanel just prior to being hidden
	 */
	private CytoPanelState cytoPanelPrevState =  CytoPanelState.DOCK;

	/**
	 * Reference to our parent menu.
	 */
	JCheckBoxMenuItem menuItem;
   
	/**
	 * Constructor.
	 */
	public CytoPanelAction (JCheckBoxMenuItem menuItem, CytoPanel cytoPanel) {
		// call our parent constructor
		super(cytoPanel.getTitle());
		setPreferredMenu( "CytoPanels" );

		// save reference to CytoPanel
		this.cytoPanel = cytoPanel;

		// save reference to menu
		this.menuItem = menuItem;

		// register as a CytoPanel listener
		cytoPanel.addCytoPanelListener(this);
	}

	/**
	 * Menu item select/deselect handler.
	 */
	public void actionPerformed (ActionEvent e) {

		// dock or float or hide based on cytopanel and menu item state
		if (menuItem.isSelected()){
			if (cytoPanelPrevState == CytoPanelState.DOCK){
				cytoPanel.setState(CytoPanelState.DOCK);
			}
			else{
				cytoPanel.setState(CytoPanelState.FLOAT);
			}
		}
		else{
			cytoPanelPrevState = cytoPanel.getState();
			cytoPanel.setState(CytoPanelState.HIDE);
		}
	}

    /**
     * Notifies the listener when a component is added to the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel.
     */
    public void onComponentAdded(int count){
		// no way to check if item is already enabled, so lets just enable it
		menuItem.setEnabled(true);
	}

    /**
     * Notifies the listener when a component is removed from the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel.
     */
    public void onComponentRemoved(int count){

		// if no more components on cytopanel, disable menu item
		if (count == 0){
			menuItem.setEnabled(false);
			menuItem.setSelected(false);
		}
	}

    /**
     * Notifies the listener on a change in the CytoPanel state.
     *
     * @param newState The new CytoPanel state - see CytoPanelState class.
     */
    public void onStateChange(CytoPanelState newState){
		if (newState == CytoPanelState.DOCK ||
			newState == CytoPanelState.FLOAT){
			menuItem.setSelected(true);
		}
	}

    /**
     * Notifies the listener when a new component on the CytoPanel is selected.
     *
	 * @param componentIndex The index of the component selected.
     */
    public void onComponentSelected(int componentIndex){
	}
}

