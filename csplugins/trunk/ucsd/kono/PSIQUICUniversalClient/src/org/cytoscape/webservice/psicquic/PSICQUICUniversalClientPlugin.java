package org.cytoscape.webservice.psicquic;

import java.awt.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;

/**
 * PSICQUIC Universal Client class.
 * 
 * <p>
 * This class simply register itself to the Web Service Client framework.
 * 
 * @author kono
 * @since Cytoscape 2.7
 * 
 */
public class PSICQUICUniversalClientPlugin extends CytoscapePlugin {
	
	private static final String TARGET_MENU = "Network from Web Services...";

	private static final CyLogger logger = CyLogger.getLogger();

	public PSICQUICUniversalClientPlugin() {
		
		// Disable menu until initialization process ends.
		
		final JMenu importMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Import");
		final Component[] menues = importMenu.getMenuComponents();
		JMenuItem webServiceMenu = null;
		
		for(Component menu: menues) {
			if(menu instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) menu;
				if(menuItem.getText().contains(TARGET_MENU)) {
					webServiceMenu = menuItem;
					break;
				}
			}
		}
		
		if(webServiceMenu != null) {
			webServiceMenu.setEnabled(false);
			webServiceMenu.setToolTipText("Initializing web service clients.  Please wait...");
		}

		final ExecutorService ex = Executors.newSingleThreadExecutor();

		try {
			logger.info("Initializatin process start in separate thread for PSICQUIC.");
			ex.execute(new InitTask(webServiceMenu));
		} catch (Exception e) {
			logger.error("Failed to initialize PSICQUIC Universal Client.", e);
			webServiceMenu.setEnabled(true);
			webServiceMenu.setToolTipText(null);
		}
	}

	
	class InitTask implements Runnable {
		
		private final JMenuItem menuItem;
		
		InitTask(JMenuItem menu) {
			this.menuItem = menu;
		}

		public void run() {
			WebServiceClientManager.registerClient(PSICQUICUniversalClient
					.getClient());
			menuItem.setEnabled(true);
			menuItem.setToolTipText(null);
		}
	}
}
