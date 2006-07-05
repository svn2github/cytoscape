import junit.framework.TestCase;
import junit.framework.TestSuite;
import giny.model.Edge;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.actions.SaveSessionAction;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesImpl;
import cytoscape.data.ExpressionData;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.readers.XGMMLReader;
import cytoscape.data.servers.BioDataServer;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.giny.CytoscapeFingRootGraph;
import cytoscape.giny.CytoscapeRootGraph;
import cytoscape.init.CyInitParams;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.VisualMappingManager;
import cytoscape.*;

public class CytoscapeTest extends TestCase {

	public void setUp() throws Exception {
	}
	
	public void tearDown() throws Exception {
	}

	public void testGetImportHandler() throws Exception {
		//ImportHandler importHandler = Cytoscape.getImportHandler();
		//assertEquals(importHandler.getClass(), ImportHandler.class);
	}
	
	//public void test
	//try getting network attributes
	
	//try gettting the null network
	
	//try creating a network
	
	
}