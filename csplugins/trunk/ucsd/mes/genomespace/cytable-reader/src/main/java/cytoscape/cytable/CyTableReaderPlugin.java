package cytoscape.cytable;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.CyAttributes;
import cytoscape.util.FileUtil;

import java.net.URL;
import java.io.File;
import java.io.Reader;

public class CyTableReaderPlugin extends CytoscapePlugin {

	public CyTableReaderPlugin() {
		Cytoscape.getDesktop().getCyMenus().addAction( new CyTableReaderAction("Node"));
		Cytoscape.getDesktop().getCyMenus().addAction( new CyTableReaderAction("Edge"));

		loadCyTable(CytoscapeInit.getProperties().getProperty("node.cytable"),Cytoscape.getNodeAttributes());
		loadCyTable(CytoscapeInit.getProperties().getProperty("edge.cytable"),Cytoscape.getNodeAttributes());
	}

	public static void loadCyTable(Object loadNow, CyAttributes attrs) {
		if ( loadNow == null )
			return;

		try {
			if ( loadNow instanceof Reader ) {
					new CyTableReader( (Reader)loadNow, attrs ).read();
			} else {
				String name = loadNow.toString();

				if ( name.matches(FileUtil.urlPattern) )
					new CyTableReader( new URL(name), attrs ).read();
				else
					new CyTableReader( new File(name).toURL(), attrs ).read();
			}

		} catch (Exception e) {
			CyLogger.getLogger(CyTableReaderPlugin.class).warn("failed to load table: " + loadNow);
		}
	}
}	


