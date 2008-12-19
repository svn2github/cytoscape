package dual;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.servers.BioDataServer;
import cytoscape.giny.CytoscapeRootGraph;
import cytoscape.CyNetwork;
import phoebe.PGraphView;
import java.util.Iterator;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import phoebe.PGraphView;
import giny.view.NodeView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.init.CyPropertiesReader;
import cytoscape.visual.VisualStyle;
import java.util.*;

public class DualLayout {

	public static void create(String[] args, String[] noas, String style) {
		
		// The point here is to create a cynetwork
		// created from an on disk representation
		// without actually instantiating an instance
		// of cytoscape

		CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();
		for( int idx = 0; idx < args.length; idx++ ) {

			InteractionsReader reader = null;
			try {
				reader = new InteractionsReader(Cytoscape.loadBioDataServer(null), CytoscapeInit.getDefaultSpeciesName(), args[idx]);
				reader.read();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			CyNetwork cyNetwork  = rootGraph.createNetwork(reader.getNodeIndicesArray(),reader.getEdgeIndicesArray());
			CyNetwork splitGraph = rootGraph.createNetwork(new int[]{},new int[]{});
			DualLayoutTask task = new DualLayoutTask(cyNetwork);
			task.splitNetwork(splitGraph);
			PhoebeNetworkView view = new PhoebeNetworkView(splitGraph,"Title");
			task.layoutNetwork(view);
	
			// Read NoA files
			// Read EdgeAttributeFiles
			String[] eaf = {};
			Cytoscape.loadAttributes(noas, eaf, true, Cytoscape.getBioDataServer(), CytoscapeInit.getDefaultSpeciesName());
			System.out.println("def spec " + CytoscapeInit.getDefaultSpeciesName());

			
			// CHANGE STYLE HERE
			VisualMappingManager manager = new VisualMappingManager(view);
			VisualStyle chosen = manager.getCalculatorCatalog().getVisualStyle(style);
			manager.setVisualStyle(chosen);
			manager.applyNodeAppearances();
			manager.applyEdgeAppearances();
			try {
				ImageIO.write((BufferedImage)view.getCanvas().getLayer().toImage(),"png",new File(args[idx]+".png"));  
			} catch ( Exception e) {
				e.printStackTrace();
			}
		}
	}
}
