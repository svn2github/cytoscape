
/*
  File: CytoscapeSessionReader.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.data.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import cytoscape.ding.DingNetworkView;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.actions.FitContentAction;
import cytoscape.data.servers.BioDataServer;
import cytoscape.generated.Child;
import cytoscape.generated.Cysession;
import cytoscape.generated.Network;
import cytoscape.generated.NetworkTree;
import cytoscape.generated.Node;
import cytoscape.generated.SelectedNodes;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.ding.DingNetworkView;
import cytoscape.init.CyPropertiesReader;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Cytoscape Session Reader. This class expands cys file and read all files in
 * the archive.
 * 
 * @author kono
 * 
 */
public class CytoscapeSessionReader {

	private static final int NUM_STATE_FILES = 3;
	private final String PACKAGE_NAME = "cytoscape.generated";
	private static final String PROP_EXT = ".props";
	private static final String XML_EXT = ".xml";
	private static final String XGMML_EXT = ".xgmml";
	private String fileName;

	public static final String CYSESSION = "cysession.xml";

	private final String FS = System.getProperty("file.separator");

	private String[] networkFiles;

	private File tempDir;

	private TaskMonitor taskMonitor;

	HashMap netMap;

	String sessionID;

	// for testing
	int cnt;

	Cysession session;
	
	// Stores networkName as the key and value is visualStyleName associated with it.
	HashMap vsMap;
	HashMap vsMapByName;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            Name of .cys file.
	 * @throws JAXBException
	 * @throws Exception
	 */
	public CytoscapeSessionReader(String filename) {
		this.fileName = filename;
		vsMap = new HashMap();
		vsMapByName = new HashMap();
	}

	public void read() throws IOException, JAXBException {
		unzipSession();
	}

	/**
	 * Decompress session file
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	private boolean unzipSession() throws IOException, JAXBException {

		
		// Create zipfile object to be extracted
		ZipFile cysFile = new ZipFile(fileName);

		String cysessionFileName = null;
		List networks = new ArrayList();

		for (Enumeration e = cysFile.entries(); e.hasMoreElements();) {
			String curEntry = e.nextElement().toString();
			if (curEntry.endsWith("xml")) {
				cysessionFileName = curEntry;
				
			} else if (curEntry.endsWith("vizmap.props")) {
				// We should restore VIZMAP first since it will be used by 
				// Graph Layout.
				restoreVizmap(curEntry);
			} else {
				networks.add(curEntry);
			}
		}

		cysFile.close();
		loadCySession2(cysessionFileName, networks);
		
		
		// Set VS for each network view
		Set networkIDSet = vsMap.keySet();
		String currentNetworkID = null;
		Iterator it = networkIDSet.iterator();
		while(it.hasNext()) {
			currentNetworkID = (String) it.next();
			Cytoscape.getNetworkView(currentNetworkID).setVisualStyle((String) vsMap.get(currentNetworkID));
		}
		
		return true;
	}
	
	private void restoreVizmap(String vizmapName) throws IOException {
		// Tell the target file name to CalculatorCatalog
		Cytoscape.firePropertyChange(Cytoscape.SESSION_LOADED, null, fileName);
	}
	
	
	private boolean readPropFiles( String propFileName ) throws FileNotFoundException, IOException {

		CytoscapeInit.getProperties().load(new FileInputStream( propFileName ));
		Properties prop = CytoscapeInit.getProperties();
		
		//propertiesLocation = propReader.getPropertiesLocation();
		
		setVariablesFromProperties(prop);
		
		return true;
	}
	
	private void setVariablesFromProperties(Properties properties) {

		// plugins
		if (properties.getProperty("plugins") != null) {
			String[] pargs = properties.getProperty("plugins").split(",");
			for (int i = 0; i < pargs.length; i++) {
				String plugin = pargs[i];
				URL url;
				try {
					if (plugin.startsWith("http")) {
						plugin = "jar:" + plugin + "!/";
						url = new URL(plugin);
					} else {
						url = new URL("file", "", plugin);
					}
					CytoscapeInit.getPluginURLs().add(url);
				} catch (Exception ue) {
					System.err.println("Jar: " + pargs[i]
							+ "was not a valid URL");
				}
			}
		}

		// Data variables
		CytoscapeInit.setDefaultSpeciesName();
		//CytoscapeInit.getBioDataServer();
		//bioDataServer = properties.getProperty("bioDataServer", "unknown");

		// Configuration variables
		CytoscapeInit.setViewThreshold((new Integer(properties.getProperty("viewThreshold","500"))).intValue());
		CytoscapeInit.setSecondaryViewThreshold((new Integer(properties.getProperty(
				"secondaryViewThreshold", "2000"))).intValue());
		
//		viewType = properties.getProperty("viewType", "internal");
//
//		// View Only Variables
//		defaultVisualStyle = properties.getProperty("defaultVisualStyle",
//				"default");
		CytoscapeInit.setMRUD(new File(properties.getProperty("mrud", System
				.getProperty("user.dir"))));
		
		
		
	}

	private boolean loadCySession2(String cysessionFileName, List networkList)
			throws JAXBException, IOException {

		ZipFile sessionFile = new ZipFile(fileName);
		ZipEntry csxml = sessionFile.getEntry(cysessionFileName);

		InputStream is = sessionFile.getInputStream(csxml);
		JAXBContext jc = JAXBContext.newInstance(PACKAGE_NAME);

		Unmarshaller u = jc.createUnmarshaller();

		// u.setValidating(true);

		session = (Cysession) u.unmarshal(is);

		// Session ID is the name of folder which contains everything
		// for this session.
		sessionID = session.getId();

		// Extract tree structure
		NetworkTree networks = (NetworkTree) session.getNetworkTree();

		List netList = networks.getNetwork();
		// Convert it to map
		Iterator it = netList.iterator();
		netMap = new HashMap();
		while (it.hasNext()) {
			Network curNet = (Network) it.next();
			netMap.put(curNet.getId(), curNet);
			
		}

		// Extract root network
		// ("root network" is a network which belongs directly to
		// the Network Root.)

		Network root = (Network) netMap.get("Network Root");
		List rootTrees = root.getChild();
		Iterator rootIt = rootTrees.iterator();

		cnt = 0;
		while (rootIt.hasNext()) {
			Child curNet = (Child) rootIt.next();
			String curNetName = curNet.getId();

			Network targetRoot = (Network) netMap.get(curNetName);
			vsMapByName.put(targetRoot.getId(), targetRoot.getVisualStyle());
			
			// Create a root network here.
			String targetNetwork = sessionID + FS + targetRoot.getFilename();
			
			
			ZipEntry zen = sessionFile.getEntry(targetNetwork);

			InputStream networkStream = sessionFile.getInputStream(sessionFile
					.getEntry(targetNetwork));

			CyNetwork rootNetwork = this.createNetwork(null, targetNetwork,
					networkStream, Cytoscape.FILE_XGMML, Cytoscape
							.getBioDataServer(), CytoscapeInit
							.getDefaultSpeciesName());
			
			String vsName = targetRoot.getVisualStyle();
			if(vsName  == null) {
				vsName = "default";
			}
			vsMap.put(rootNetwork.getIdentifier(), vsName);
			
			
			// Set selected nodes & edges
			SelectedNodes sNodes = (SelectedNodes) targetRoot
					.getSelectedNodes();

			// Create metanode structures

			if (sNodes != null) {
				setSelectedNodes(rootNetwork, sNodes);
			}
			walkTree(targetRoot, rootNetwork, sessionFile);
		}

		sessionFile.close();

		// Traverse network tree

		return false;
	}

	private void setSelectedNodes(CyNetwork network, SelectedNodes selected) {

		HashMap nodeMap = new HashMap();

		Iterator it = selected.getNode().iterator();
		while (it.hasNext()) {
			Node selectedNode = (Node) it.next();
			String nodeID = selectedNode.getId();
			Iterator nodeIt = network.nodesIterator();

			while (nodeIt.hasNext()) {
				CyNode node = (CyNode) nodeIt.next();

				if (node.getIdentifier().equals(nodeID)) {
					//System.out.println("Selected nodes found! " + nodeID);
					network.setFlagged(node, true);
				}
			}

		}

	}

	// Load the root network and then create its children.
	private void walkTree(Network currentNetwork, CyNetwork parent,
			ZipFile sessionFile) throws JAXBException, IOException {

		cnt++;

		CyNetwork network = parent;

		// Get the list of children under this root
		List children = currentNetwork.getChild();

		// Traverse using recursive call
		for (int i = 0; i < children.size(); i++) {

			Child child = (Child) children.get(i);
			Network childNet = (Network) netMap.get(child.getId());
			String vsName = childNet.getVisualStyle();
			if(vsName == null) {
				vsName = "default";
			}
			
			vsMapByName.put(child.getId(), vsName);
			
			String childFile = sessionID + FS + childNet.getFilename();

			InputStream networkStream = sessionFile.getInputStream(sessionFile
					.getEntry(childFile));

			CyNetwork new_network = this.createNetwork(parent, childFile,
					networkStream, Cytoscape.FILE_XGMML, Cytoscape
							.getBioDataServer(), CytoscapeInit
							.getDefaultSpeciesName());
			
			vsMap.put(new_network.getIdentifier(), vsName);
//			System.out.println("***Network is: " + childNet.getId());
//			System.out.println("***VS is: " + vsMap.get(childNet.getId()).toString());
			
			

			//
			// Set selected/hidden nodes & edges
			//
			SelectedNodes sNodes = (SelectedNodes) childNet.getSelectedNodes();
			if (sNodes != null) {
				setSelectedNodes(new_network, sNodes);
			}

			// Re-create

			// network = createChildNetwork(childNet, network);
			if (childNet.getChild().size() == 0) {
				//System.out.println("!!!!!!!!!leaf");
			} else {
				walkTree(childNet, new_network, sessionFile);
			}

		}
	}

	private CyNetwork createNetwork(CyNetwork parent, String location,
			InputStream is, int file_type, BioDataServer biodataserver,
			String species) throws IOException, JAXBException {

		XGMMLReader reader;
		reader = new XGMMLReader(is);

		// Have the GraphReader read the given file
		reader.read();

		// Get the RootGraph indices of the nodes and
		// Edges that were just created
		final int[] nodes = reader.getNodeIndicesArray();
		final int[] edges = reader.getEdgeIndicesArray();

		File file = new File(location);
		// final String title = file.getName();
		// final String id = reader.getNetworkID();

		// Create the CyNetwork
		// First, set the view threshold to 0. By doing so, we can disable
		// the auto-creating of the CyNetworkView.
		int realThreshold = CytoscapeInit.getViewThreshold();
		CytoscapeInit.setViewThreshold(0);

		CyNetwork network = null;
		if (parent == null) {
			network = Cytoscape.createNetwork(nodes, edges, reader
					.getNetworkID());

		} else {
			network = Cytoscape.createNetwork(nodes, edges, reader
					.getNetworkID(), parent);
		}
		
		// Store network Metadata
		network.putClientData("RDF", reader.getNetworkMetadata());
		network.putClientData(
				"metaNodeViewer.model.GPMetaNodeFactory.metaNodeRindices",
				reader.getMetanodeList());

		// Reset back to the real View Threshold
		CytoscapeInit.setViewThreshold(realThreshold);

		// Store "network property" in the clientdata data structure
		//network.putClientData("XGMML", reader);

		Object[] ret_val = new Object[3];
		ret_val[0] = network;
		ret_val[1] = file.toURI();
		ret_val[2] = new Integer(file_type);
		// Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null,
		// ret_val);

		// Conditionally, Create the CyNetworkView
		if (network.getNodeCount() < CytoscapeInit.getViewThreshold()) {
			createCyNetworkView(network);

			if (Cytoscape.getNetworkView(network.getIdentifier()) != null) {
				reader
						.layout(Cytoscape.getNetworkView(network
								.getIdentifier()));
			}

			// Lastly, make the GraphView Canvas Visible.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DingNetworkView view = (DingNetworkView) Cytoscape
							.getCurrentNetworkView();
// 					PCanvas pCanvas = view.getCanvas();
// 					pCanvas.setVisible(true);
				}
			});
			
			
			 
			
			String curVS = (String) vsMapByName.get(network.getTitle());
			CyNetworkView curView = Cytoscape.getNetworkView(network
					.getIdentifier());
			if(curVS != null ) {
				curView.setVisualStyle(curVS);
				Cytoscape.getDesktop().getVizMapUI().getStyleSelector().resetStyles(curVS);
				Cytoscape.getDesktop().getVizMapUI().visualStyleChanged();

			} else
				curView.setVisualStyle(Cytoscape.getDesktop().getVizMapManager().getVisualStyle().getName());
		}

		return network;
	}

	// same as other loaders
	//
	private void createCyNetworkView(CyNetwork cyNetwork) {
		final DingNetworkView view = new DingNetworkView(cyNetwork,
				cyNetwork.getTitle());

		// Start of Hack: Hide the View
// 		PCanvas pCanvas = view.getCanvas();
// 		pCanvas.setVisible(false);
		// End of Hack

		view.setIdentifier(cyNetwork.getIdentifier());
		Cytoscape.getNetworkViewMap().put(cyNetwork.getIdentifier(), view);
		view.setTitle(cyNetwork.getTitle());
		
		// if Squiggle function enabled, enable squiggling on the created view
// 		if (Cytoscape.isSquiggleEnabled()) {
// 			view.getSquiggleHandler().beginSquiggling();
// 		}

		// set the selection mode on the view
		Cytoscape.setSelectionMode(Cytoscape.getSelectionMode(), view);

		Cytoscape.firePropertyChange(
				cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, null,
				view);

// 		PLayer layer = view.getCanvas().getLayer();
// 		PBounds pb = layer.getFullBounds();
// 		if (!pb.isEmpty()) {
// 			view.getCanvas().getCamera().animateViewToCenterBounds(pb, true,
// 					500);
// 		}

		// Fit the network
		DingNetworkView currentGraphView = (DingNetworkView) Cytoscape
				.getNetworkView(cyNetwork.getIdentifier());
		FitContentAction fca = new FitContentAction();

		if ((cyNetwork.getNodeCount() > 0) && (cyNetwork.getNodeCount() < 200)) {
                  currentGraphView.fitContent();
		} else {
// 			view.getCanvas().getCamera().animateViewToCenterBounds(
// 					view.getCanvas().getLayer().getFullBounds(), true, 50l);
		}
	}

}
