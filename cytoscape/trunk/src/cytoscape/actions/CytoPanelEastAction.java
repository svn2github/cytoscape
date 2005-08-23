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
 * Menu item handler - CytoPanelEast
 */
public class CytoPanelEastAction extends CytoscapeAction{

	/**
	 * Maintains state of cytopanel just prior to being hidden
	 */
	private CytoPanelState cytoPanelEastPrevState =  CytoPanelState.DOCK;
   
	/**
	 * Constructor - no args
	 */
	public CytoPanelEastAction () {
		super("CytoPanel 2");
		setPreferredMenu( "CytoPanels" );
		//setAccelerator( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_2, 0 ) );
	}
  
	public void actionPerformed (ActionEvent e) {

		// get the cytopanel east menu item
		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		JMenu cytoPanelMenu = cyMenus.getCytoPanelMenu();
		// dont like that this is hardcoded, but oh well
		JCheckBoxMenuItem cytoPanelEastItem = (JCheckBoxMenuItem)cytoPanelMenu.getItem(1);

		// get the east cytopanel
		CytoPanel cytoPanelEast = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		
		// dock or float or hide based on cytopanel and menu item state
		if (cytoPanelEastItem.isSelected()){
			if (cytoPanelEastPrevState == CytoPanelState.DOCK){
				cytoPanelEast.setState(CytoPanelState.DOCK);
			}
			else{
				cytoPanelEast.setState(CytoPanelState.FLOAT);
			}
		}
		else{
			cytoPanelEastPrevState = cytoPanelEast.getState();
			cytoPanelEast.setState(CytoPanelState.HIDE);
		}
	}
}

