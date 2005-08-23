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
 * Menu item handler - CytoPanelSouth
 */
public class CytoPanelSouthAction extends CytoscapeAction{

	/**
	 * Maintains state of cytopanel just prior to being hidden
	 */
	private CytoPanelState cytoPanelSouthPrevState =  CytoPanelState.DOCK;
   
	/**
	 * Constructor - no args
	 */
	public CytoPanelSouthAction () {
		super("CytoPanel 3");
		setPreferredMenu( "CytoPanels" );
		//setAccelerator( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_3, 0 ) );
	}
  
	public void actionPerformed (ActionEvent e) {

		// get the cytopanel south menu item
		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		JMenu cytoPanelMenu = cyMenus.getCytoPanelMenu();
		// dont like that this is hardcoded, but oh well
		JCheckBoxMenuItem cytoPanelSouthItem = (JCheckBoxMenuItem)cytoPanelMenu.getItem(2);

		// get the south cytopanel
		CytoPanel cytoPanelSouth = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH);
		
		// dock or float or hide based on cytopanel and menu item state
		if (cytoPanelSouthItem.isSelected()){
			if (cytoPanelSouthPrevState == CytoPanelState.DOCK){
				cytoPanelSouth.setState(CytoPanelState.DOCK);
			}
			else{
				cytoPanelSouth.setState(CytoPanelState.FLOAT);
			}
		}
		else{
			cytoPanelSouthPrevState = cytoPanelSouth.getState();
			cytoPanelSouth.setState(CytoPanelState.HIDE);
		}
	}
}

