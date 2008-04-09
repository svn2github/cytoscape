
package cytoscape.tutorial01;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.CytoPanelImp;
import javax.swing.SwingConstants;
import javax.swing.JPanel;

/**
 * 
 */
public class Tutorial01 extends CytoscapePlugin {

	/**
	 * Add a tabbed panel to the control panel
	 */
	public Tutorial01() {

		// Three steps
		
		// (1) Get the handler to cytoPanel west
		CytoPanelImp ctrlPanel = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST); 
		
		// (2) Create a JPanel object 
		MyPanel myPanel = new MyPanel(); 
		
		// (3) Add the JPanel object to the cytoPanel west
		ctrlPanel.add("myPanel", myPanel);


		// Select the panel after the plugin is initialized
		int indexInCytoPanel = ctrlPanel.indexOfComponent("myPanel");
		ctrlPanel.setSelectedIndex(indexInCytoPanel);						
	}
	
	class MyPanel extends JPanel {
		public MyPanel() {
		}
	}

}
