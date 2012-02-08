

package cytoscape.genomespace;


import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;
import org.genomespace.atm.model.WebToolDescriptor;
import org.genomespace.client.GsSession;
import cytoscape.logger.CyLogger;


public class LaunchToolMenu extends JMenu implements MenuListener {

	private static final CyLogger logger = CyLogger.getLogger(LaunchToolMenu.class);

	public LaunchToolMenu(JMenu parent) {
		super("Launch");
		parent.addMenuListener(this);
	}

	public void menuCanceled(MenuEvent e) { 
		removeAll();
	}

	public void menuDeselected(MenuEvent e) { 
		removeAll();
	} 

	public void menuSelected(MenuEvent e) { 
		if ( GSUtils.loggedInToGS() ) {
			setEnabled(true);
			try {
				GsSession session = GSUtils.getSession();
				for ( WebToolDescriptor webTool : session.getAnalysisToolManagerClient().getWebTools() ) {
					if ( webTool.getName().equalsIgnoreCase("cytoscape") )
						continue;
					LaunchToolAction action = new LaunchToolAction(webTool);
					add(new JMenuItem(action));
				}
			} catch (Exception ex) { 
				logger.warn("problem finding web tools", ex); 
			}
		} else {
			setEnabled(false);
		}
	}
}
