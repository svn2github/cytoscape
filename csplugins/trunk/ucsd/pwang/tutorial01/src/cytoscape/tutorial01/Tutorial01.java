
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
	 * Creates a new Plugin object.
	 * 
	 * @param icon
	 *            DOCUMENT ME!
	 * @param csfilter
	 *            DOCUMENT ME!
	 */
	public Tutorial01() {

		CytoPanelImp ctrlPanel = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST); 
		MyPanel myPanel = new MyPanel(); 
		ctrlPanel.add("myPanelName", myPanel);

		int indexInCytoPanel = ctrlPanel.indexOfComponent("myPanelName");
		ctrlPanel.setSelectedIndex(indexInCytoPanel);						
	}
	
	class MyPanel extends JPanel {
		public MyPanel() {
		}
	}

}
