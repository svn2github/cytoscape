// our package
package cytoscape.actions;

// imports
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.AbstractAction;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyMenus;
import cytoscape.view.cytopanel.CytoPanel;
import cytoscape.view.cytopanel.CytoPanelState;

/**
 * Menu item handler - CytoPanelWest
 */
public class CytoPanelWestAction extends CytoscapeAction{

	/**
	 * Maintains state of cytopanel just prior to being hidden
	 */
	private CytoPanelState cytoPanelWestPrevState =  CytoPanelState.DOCK;
   
	/**
	 * Constructor - no args
	 */
	public CytoPanelWestAction () {
		super("CytoPanel 1");
		setPreferredMenu( "CytoPanels" );
		//setAccelerator( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_1, 0 ) );
	}
  
	public void actionPerformed (ActionEvent e) {

		// get the cytopanel west menu item
		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		JMenu cytoPanelMenu = cyMenus.getCytoPanelMenu();
		// dont like that this is hardcoded, but oh well
		JCheckBoxMenuItem cytoPanelWestItem = (JCheckBoxMenuItem)cytoPanelMenu.getItem(0);

		// get the west cytopanel
		CytoPanel cytoPanelWest = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
		
		// dock or float or hide based on cytopanel and menu item state
		if (cytoPanelWestItem.isSelected()){
			if (cytoPanelWestPrevState == CytoPanelState.DOCK){
				cytoPanelWest.setState(CytoPanelState.DOCK);
			}
			else{
				cytoPanelWest.setState(CytoPanelState.FLOAT);
			}
		}
		else{
			cytoPanelWestPrevState = cytoPanelWest.getState();
			cytoPanelWest.setState(CytoPanelState.HIDE);
		}
	}
}

