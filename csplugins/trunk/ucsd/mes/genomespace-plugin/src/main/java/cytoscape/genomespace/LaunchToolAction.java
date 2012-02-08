
package cytoscape.genomespace;


import cytoscape.logger.CyLogger;
import cytoscape.Cytoscape;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import org.genomespace.sws.SimpleWebServer;
import org.genomespace.client.ui.BrowserLauncher;
import org.genomespace.atm.model.WebToolDescriptor;


public class LaunchToolAction extends AbstractAction {

	private static final CyLogger logger = CyLogger.getLogger(LaunchToolAction.class);

	private final WebToolDescriptor webTool;

	public LaunchToolAction(WebToolDescriptor webTool) {
		super(webTool.getName());
		this.webTool = webTool;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String launchUrl = SimpleWebServer.makeLocalLoadUrl(webTool.getReloadPort(), null);
			if (!SimpleWebServer.timedGetUrl(launchUrl)) {
				launchUrl = webTool.getBaseUrl();
				logger.info("Launch URL is: "+ launchUrl);
				BrowserLauncher.openURL(launchUrl);
			}
		} catch (Exception ex) {
			logger.error("Launch failed", ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}
}
