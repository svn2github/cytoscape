package cytoscape.genomespace;

import java.io.File;
import java.util.Map;
import java.net.URL;

import cytoscape.Cytoscape;
import cytoscape.util.URLUtil;
import cytoscape.logger.CyLogger;

import org.genomespace.sws.GSLoadEventListener;
import org.genomespace.sws.GSLoadEvent;

public class LoadSessionFromURL implements GSLoadEventListener {
	private static final CyLogger logger = CyLogger.getLogger(LoadNetworkFromURL.class);

	public void onLoadEvent(GSLoadEvent event) {
		Map<String,String> params = event.getParameters();
		String sessionURL = params.get("session");
		loadSession(sessionURL);
	}

	public void loadSession(String sessionURL) {
		if ( sessionURL == null )
			return;
        if (!SessionLoader.destroyCurrentSession(Cytoscape.getDesktop()))
            return;

		try {
			File tempFile = GSUtils.downloadToTempFile(sessionURL); 
			SessionLoader.loadSession(tempFile, sessionURL );
		} catch ( Exception e ) { e.printStackTrace(); }
	}
}
