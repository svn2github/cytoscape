
package example;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.ding.DingNetworkView; 
import cytoscape.actions.CreateNetworkViewAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.visual.CalculatorIO;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.util.URLUtil;

import java.util.Properties;
import java.util.Random;
import java.io.InputStream;
import java.io.IOException;

public class PerformanceActions implements PropertyChangeListener {

	public void propertyChange(PropertyChangeEvent e) {
		if ( Cytoscape.CYTOSCAPE_INITIALIZED.equals(e.getPropertyName()) ) {
			performActions();	
		}
	}

	private void performActions() {
		loadNetworks();
		zoomInAndOut();
		selectAllNodes();
		applyLayout();
		loadVisualProperties();
		applyVizMap();
		saveSession();
		restoreSessionFromFile();

		System.exit(0);
	}

	private String rualIdentifier;
	private String bindIdentifier;
	private String galIdentifier;

	private void loadNetworks() {
		// this one also loads attrs
		Cytoscape.createNetworkFromURL(getClass().getResource("/galFiltered.xgmml"), true);
		galIdentifier = Cytoscape.getCurrentNetwork().getIdentifier();

		// just to test gml
		Cytoscape.createNetworkFromURL(getClass().getResource("/galFiltered.gml"), true);

		// a big network with separate view creation
		Cytoscape.createNetworkFromURL(getClass().getResource("/BINDhuman.sif"), true);
		bindIdentifier = Cytoscape.getCurrentNetwork().getIdentifier();
		CreateNetworkViewAction.createViewFromCurrentNetwork(Cytoscape.getNetwork(bindIdentifier));

		// a moderate network good for testing
		Cytoscape.createNetworkFromURL(getClass().getResource("/RUAL.subset.sif"), true);
		rualIdentifier = Cytoscape.getCurrentNetwork().getIdentifier();
	}

	private void zoomInAndOut() {
		Cytoscape.setCurrentNetworkView( rualIdentifier );
		CyNetwork network = Cytoscape.getNetwork(rualIdentifier);

		// select a random node
		CyNode randomNode = Cytoscape.getCyNode("6612");	
		network.setSelectedNodeState(randomNode,true);

		DingNetworkView view = (DingNetworkView)(Cytoscape.getCurrentNetworkView());

		// zoom out
		view.fitContent();

		// zoom in
		view.fitSelected();

		// zoom out
		view.fitContent();
	}

	private void selectAllNodes() {
		DingNetworkView view = (DingNetworkView)Cytoscape.getNetworkView(bindIdentifier);
		view.fitSelected();

		CyNetwork network = Cytoscape.getNetwork(bindIdentifier);
		network.selectAllNodes();
		view.updateView();
	}

	private void applyLayout() {
		CyNetworkView view = Cytoscape.getNetworkView(rualIdentifier);
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		layout.doLayout(view);
	}

	private void loadVisualProperties() {
		Properties vizmapProps = new Properties();
		try {
		InputStream is = URLUtil.getBasicInputStream(getClass().getResource("/test.vizmap.props"));
		vizmapProps.load(is);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		}
		CalculatorCatalog clog = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
		CalculatorIO.loadCalculators(vizmapProps, clog);
	}

	private void applyVizMap() {
		Cytoscape.setCurrentNetwork( galIdentifier );
		CyNetworkView view = Cytoscape.getNetworkView(galIdentifier);
		String styleName = "testStyle";
        Cytoscape.getVisualMappingManager().setVisualStyle(styleName);
		Cytoscape.getVisualMappingManager().setNetworkView(view);
		view.setVisualStyle(styleName);
		view.redrawGraph(true,true);
	}

	String sessionName;

	private void saveSession() {
		sessionName = Integer.toString(new Random().nextInt()) + "-session";
		Cytoscape.getDesktop().setTitle("new session");
		CytoscapeSessionWriter sw = new CytoscapeSessionWriter(sessionName);
        try {
            sw.writeSessionToDisk();
        } catch (Exception e) {
			e.printStackTrace();
		}
		Cytoscape.setCurrentSessionFileName(sessionName);
		Cytoscape.getDesktop().setTitle("temp session");
	}

	private void restoreSessionFromFile() {
		Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
		Cytoscape.createNewSession();
		Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
		try {
		CytoscapeSessionReader sr = new CytoscapeSessionReader(sessionName, null);
		sr.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Cytoscape.getDesktop().getVizMapperUI().initVizmapperGUI();
		Cytoscape.setCurrentSessionFileName(sessionName);
	}
}
