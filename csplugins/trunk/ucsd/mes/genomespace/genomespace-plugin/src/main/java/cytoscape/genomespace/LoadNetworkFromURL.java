package cytoscape.genomespace;

import java.net.URL;
import java.util.Map;
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import org.genomespace.sws.GSLoadEventListener;
import org.genomespace.sws.GSLoadEvent;

public class LoadNetworkFromURL implements GSLoadEventListener {
	private static final CyLogger logger = CyLogger.getLogger(LoadNetworkFromURL.class);

	public void onLoadEvent(GSLoadEvent event) {
		Map<String,String> params = event.getParameters();
		String netURL = params.get("network");
		loadNetwork(netURL);
	}

	public void loadNetwork(String netURL) {
		if ( netURL == null )
			return;

		try {
			File tmp = GSUtils.downloadToTempFile(netURL);
			Cytoscape.createNetworkFromFile(tmp.getPath()).setTitle(netURL);
		} catch ( Exception e ) { e.printStackTrace(); }
	}
}
