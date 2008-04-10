package cytoscape.tutorial07;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 
 */
public class Tutorial07 extends CytoscapePlugin {

	/**
	 * 
	 */
	public Tutorial07() {
		// Create an Action, add it to Cytoscape menu
		MyPluginAction myAction = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) myAction);
	
		TestClass myTestClass = new TestClass();
		
	}
		

	// Add a menu item "Tutorial07" under menu "Plugins"
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial07 myPlugin) {
			super("Tutorial07");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			String message = "Message from Tutorial07";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),message);	
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInToolBar() {
			return false;
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInMenuBar() {
			return true;
		}
	}

	
	public class TestClass implements  PropertyChangeListener {
		
		public TestClass() {
			// Register this class as a listener to listen Cytoscape events
			Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
			Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
		}
		
		// Handle PropertyChangeEvent
		public void propertyChange(PropertyChangeEvent e) {
			if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
			{
				System.out.println("Received an event -- Cytoscape.ATTRIBUTES_CHANGED!");
			}
			if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
			{	
				System.out.println("Received an event -- CytoscapeDesktop.NETWORK_VIEW_FOCUSED!");
			}
			
			
		}
	}

	
}
