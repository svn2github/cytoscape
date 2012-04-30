
package cytoscape.genomespace;

import java.util.Map;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import cytoscape.cytable.CyTableReaderPlugin;
import cytoscape.logger.CyLogger;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import org.genomespace.sws.GSLoadEventListener;
import org.genomespace.sws.GSLoadEvent;

public class LoadCyTableFromURL implements GSLoadEventListener {
	private static final CyLogger logger = CyLogger.getLogger(LoadCyTableFromURL.class);

	private final CyAttributes attrs;
	private final String key;

	public LoadCyTableFromURL(String key, CyAttributes attrs) {
		this.key = key;
		this.attrs = attrs;
	}

	public void onLoadEvent(GSLoadEvent event) {
		Map<String,String> params = event.getParameters();

		String url = params.get(key);
		
		if ( url == null )
			return;

		try {
			InputStream is = GSUtils.getSession().getDataManagerClient().getInputStream(new URL(url));
			InputStreamReader reader = new InputStreamReader(is);
			CyTableReaderPlugin.loadCyTable( reader, attrs );
		} catch ( Exception e ) { e.printStackTrace(); }
	}
}
