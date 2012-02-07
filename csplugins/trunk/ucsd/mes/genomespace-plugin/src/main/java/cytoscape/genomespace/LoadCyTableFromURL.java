
package cytoscape.genomespace;

import java.util.Map;

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

			CyTableReaderPlugin.loadCyTable( url, attrs );

		} catch ( Exception e ) { e.printStackTrace(); }
	}
}
