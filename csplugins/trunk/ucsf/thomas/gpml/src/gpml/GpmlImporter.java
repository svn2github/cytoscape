package gpml;

import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.Pathway;
import org.pathvisio.view.swing.PathwayTransferable;

import phoebe.PhoebeCanvasDropEvent;
import phoebe.PhoebeCanvasDropListener;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.util.CyFileFilter;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.InnerCanvas;



public class GpmlImporter extends CytoscapePlugin implements PhoebeCanvasDropListener, PropertyChangeListener {
	GpmlAttributeHandler gpmlHandler;
		
	public PluginInfo getPluginInfoObject() {
		PluginInfo info = new PluginInfo();
		info.setDescription("This plugin allows you to import pathways in the GPML format");
		info.setName("GPML importer");
		info.addAuthor("Thomas Kelder", "University of Maastricht, BiGCaT Bioinformatics");
		info.setProjectUrl("http://wikipathways.org");
		info.setPluginVersion(0.2);
		return info;
	}
		
    public GpmlImporter() {
    	gpmlHandler = new GpmlAttributeHandler();
    	
        Cytoscape.getImportHandler().addFilter(new GpmlFilter());
        
		// Listen for Network View Creation
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
		
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CyMenus menu = desktop.getCyMenus();
		menu.addCytoscapeAction(new PasteAction(this));
			
    }
       
    class GpmlFilter extends CyFileFilter {
    	public GpmlFilter() {
			super("gpml", "GPML file", ImportHandler.GRAPH_NATURE);
		}
    	
    	public GraphReader getReader(String fileName) {
    		return new GpmlReader(fileName, gpmlHandler);
    	}
    }
    
    static double mToV(double m) {
    	return m * 1.0/15; //Should be stored in the model somewhere (pathvisio)
    }

	public void itemDropped(PhoebeCanvasDropEvent e) {
		drop(e.getTransferable());
	}
	
	public void drop(Transferable transfer) {
			try {
				Pathway p = PathwayTransferable.pathwayFromTransferable(transfer);
				
				if(p == null) return; //No pathway in transferable
				
				GpmlConverter converter = new GpmlConverter(gpmlHandler, p);

				//Get the nodes/edges indexes
				int[] nodes = converter.getNodeIndicesArray();
				int[] edges = converter.getEdgeIndicesArray();
				
				//Get the current network, or create a new one, if none is available
				CyNetwork network = Cytoscape.getCurrentNetwork();
				if(network == null) {
					String title = converter.getPathway().getMappInfo().getMapInfoName();
					network = Cytoscape.createNetwork(title == null ? "new network" : title, false);
				}
				
				//Add all nodes and edges to the network
				for(int nd : nodes) network.addNode(nd);
				for(int ed : edges) network.addEdge(ed);
				
				CyNetworkView view = Cytoscape.createNetworkView(network);
				Cytoscape.setCurrentNetworkView(view.getIdentifier());
				converter.layout(view);
				view.updateView();
				
			} catch(Exception ex) {
				Logger.log.error("Unable to process pasted data", ex);
			}
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		//Register droplistener to new canvas
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
			DGraphView view = (DGraphView) e.getNewValue();
			((InnerCanvas)view.getCanvas()).addPhoebeCanvasDropListener(this);
		}
		super.propertyChange(e);
	}
}
