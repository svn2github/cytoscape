
package cytoscape.tutorial01;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.CytoPanelImp;
import javax.swing.SwingConstants;
import javax.swing.JPanel;

/**
 * A sample plugin to show how to add a tabbed Panel to Cytoscape
 * Control panel. Deploy this plugin (tutorial01.jar) to the plugins
 * directory. A new tabbed panel "MyPanel" will appear at the 
 * control panel of Cytoscape.
 */
public class Tutorial01 extends CytoscapePlugin {

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
