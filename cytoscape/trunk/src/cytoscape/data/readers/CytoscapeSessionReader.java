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

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.Semantics;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.generated.Child;
import cytoscape.generated.Cysession;
import cytoscape.generated.Edge;
import cytoscape.generated.HiddenEdges;
import cytoscape.generated.HiddenNodes;
import cytoscape.generated.Network;
import cytoscape.generated.NetworkTree;
import cytoscape.generated.Node;
import cytoscape.generated.SelectedEdges;
import cytoscape.generated.SelectedNodes;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;

/**
 * Reaser to load CYtoscape Session file (.cys).<br>
 * This class unzip cys file and read all files in the archive.
 * 
 * @version 1.0
 * @since Cytoscape 2.3
 * @see cytoscape.data.readers.XGMMLReader
 * @author kono
 * 
 */
public class CytoscapeSessionReader {

	private static final String PACKAGE_NAME = "cytoscape.generated";
	public static final String CYSESSION = "cysession.xml";

	private static final String XML_EXT = ".xml";
	private static final String XGMML_EXT = ".xgmml";

	private static final String FS = System.getProperty("file.separator");

	private String fileName;
	private Object sourceObject;

	private HashMap networkURLs = null;

	private URL cysessionFileURL = null;
	private URL vizmapFileURL = null;
	private URL cytoscapePropsURL = null;

	private HashMap netMap;
	private String sessionID;

	private Cysession session;

	private List networkList;

	/*
	 * Stores networkName as the key and value is visualStyleName associated
	 * with it.
	 */
	HashMap vsMap;
	HashMap vsMapByName;

	/**
	 * Constructor for local files.<br>
	 * 
	 * @param filename
	 *            Name of .cys file.
	 * 
	 * This constructor is for loading local session file.
	 */
	public CytoscapeSessionReader(String filename) {
		this.fileName = filename;
		this.sourceObject = fileName;
		this.networkList = new ArrayList();

		vsMap = new HashMap();
		vsMapByName = new HashMap();
	}

	/**
	 * Constructor for remote file (specified by an URL)<br>
	 * 
	 * @param sourceName
	 * @throws IOException
	 * 
	 * This is for remote session file (URL).
	 */
	public CytoscapeSessionReader(URL sourceName) throws IOException {
		this.sourceObject = sourceName;
		this.networkList = new ArrayList();

		vsMap = new HashMap();
		vsMapByName = new HashMap();

		extractEntry((URL) sourceObject);
	}

	/**
	 * Extract Zip entries in the remote file
	 * 
	 * @param sourceName
	 * @throws IOException
	 */
	private void extractEntry(final URL sourceName) throws IOException {
		ZipInputStream zis = null;
		zis = new ZipInputStream(sourceName.openStream());
		networkURLs = new HashMap();

		// Extract list of entries
		ZipEntry zen = null;
		String entryName = null;
		while ((zen = zis.getNextEntry()) != null) {

			entryName = zen.getName();
			if (entryName.endsWith(XML_EXT)) {
				cysessionFileURL = new URL("jar:" + sourceName.toString()
						+ "!/" + entryName);
			} else if (entryName.endsWith("vizmap.props")) {
				vizmapFileURL = new URL("jar:" + sourceName.toString() + "!/"
						+ entryName);
			} else if (entryName.endsWith("cytoscape.props")) {
				cytoscapePropsURL = new URL("jar:" + sourceName.toString()
						+ "!/" + entryName);
			} else if (entryName.endsWith(XGMML_EXT)) {
				URL networkURL = new URL("jar:" + sourceName.toString() + "!/"
						+ entryName);
				networkURLs.put(entryName, networkURL);
			}
		}
		zis.close();
	}

	/**
	 * Read a session file.
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	public void read() throws IOException, JAXBException {

		if (sourceObject.getClass() == String.class) {
			unzipSessionFromFile();
		} else if (sourceObject.getClass() == URL.class) {
			unzipSessionFromURL();
		}

		// Send message with list of loaded networks.
		Cytoscape.firePropertyChange(Cytoscape.SESSION_LOADED, null,
				networkList);
		// Send signal to others
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, null);
	}

	/**
	 * Decompress session file
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	private boolean unzipSessionFromURL() throws IOException, JAXBException {

		// restore vizmap.props
		Cytoscape.firePropertyChange(Cytoscape.VIZMAP_RESTORED, null,
				vizmapFileURL);

		// restore cytoscape properties
		CytoscapeInit.getProperties().load(cytoscapePropsURL.openStream());

		loadCySession(cysessionFileURL);

		return true;
	}

	/**
	 * Decompress session file
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	private boolean unzipSessionFromFile() throws IOException, JAXBException {

		// Create zipfile object to be extracted
		final ZipFile cysFile = new ZipFile(fileName);

		String cysessionFileName = null;

		for (final Enumeration e = cysFile.entries(); e.hasMoreElements();) {
			final ZipEntry zent = (ZipEntry) e.nextElement();
			final String curEntry = zent.toString();
			if (curEntry.endsWith("xml")) {
				cysessionFileName = curEntry;

			} else if (curEntry.endsWith("vizmap.props")) {
				// We should restore VIZMAP first since it will be used by
				// Graph Layout.
				Cytoscape.firePropertyChange(Cytoscape.VIZMAP_RESTORED, null,
						fileName);
			} else if (curEntry.endsWith("cytoscape.props")) {
				CytoscapeInit.getProperties()
						.load(cysFile.getInputStream(zent));
			}
		}

		cysFile.close();
		loadCySession(cysessionFileName);

		return true;
	}

	private boolean loadCySession(final Object cysessionSource)
			throws JAXBException, IOException {

		InputStream is = null;
		ZipFile sessionFile = null;

		// Prepare input stream based on the
		if (cysessionSource.getClass() == URL.class) {
			JarURLConnection jarConnection = null;
			jarConnection = (JarURLConnection) ((URL) cysessionSource)
					.openConnection();
			is = (InputStream) jarConnection.getContent();

		} else if (cysessionSource.getClass() == String.class) {
			sessionFile = new ZipFile(fileName);
			is = sessionFile.getInputStream(sessionFile
					.getEntry((String) cysessionSource));
		}

		final JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE_NAME,
				this.getClass().getClassLoader());
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		session = (Cysession) unmarshaller.unmarshal(is);
		if (is != null) {
			is.close();
		}

		/*
		 * Session ID is the name of folder which contains everything for this
		 * session.
		 */
		sessionID = session.getId();

		// Extract tree structure
		final NetworkTree networks = (NetworkTree) session.getNetworkTree();

		// Convert it to map
		final Iterator it = networks.getNetwork().iterator();
		netMap = new HashMap();
		while (it.hasNext()) {
			final Network curNet = (Network) it.next();
			netMap.put(curNet.getId(), curNet);
		}

		// Extract root network
		// ("root network" is a network which belongs directly to
		// the Network Root.)

		final Network root = (Network) netMap.get("Network Root");
		final Iterator rootIt = root.getChild().iterator();

		while (rootIt.hasNext()) {
			final Child curNet = (Child) rootIt.next();
			final String curNetName = curNet.getId();
			final Network targetRoot = (Network) netMap.get(curNetName);
			vsMapByName.put(targetRoot.getId(), targetRoot.getVisualStyle());

			// Create a root network here.
			// String targetNetwork = sessionID + FS + targetRoot.getFilename();
			String targetNetwork = sessionID + "/" + targetRoot.getFilename();

			InputStream networkStream = null;

			if (cysessionSource.getClass() == URL.class) {
				// Problem: targetNetwork improperly uses the file separator
				// FS for URLs and one Windows machine FS is '\'
				// Fix: for URLs, replace '\' with '/'
				targetNetwork = targetNetwork.replace('\\', '/');
				final URL targetNetworkURL = (URL) networkURLs
						.get(targetNetwork);
				JarURLConnection jarConnection = (JarURLConnection) (targetNetworkURL)
						.openConnection();
				networkStream = (InputStream) jarConnection.getContent();
			} else if (cysessionSource.getClass() == String.class) {
				networkStream = sessionFile.getInputStream(sessionFile
						.getEntry(targetNetwork));
			}

			/*
			 * Finally, create CyNetwork.
			 */
			final CyNetwork rootNetwork = this.createNetwork(null,
					networkStream, targetRoot.isViewAvailable());

			/*
			 * Extract network view id available
			 */
			final CyNetworkView curNetView = Cytoscape.getNetworkView(rootNetwork
					.getIdentifier());

			if (networkStream != null) {
				networkStream.close();
			}

			/*
			 * Restore association between view and VS.
			 */
			String vsName = targetRoot.getVisualStyle();
			if (vsName == null) {
				vsName = "default";
			}
			vsMap.put(rootNetwork.getIdentifier(), vsName);

			networkList.add(rootNetwork.getIdentifier());

			/*
			 * Hide hidden nodes
			 */
			final HiddenNodes hNodes = (HiddenNodes) targetRoot
					.getHiddenNodes();
			if (curNetView != Cytoscape.getNullNetworkView() && hNodes != null) {
				setHiddenNodes(curNetView, hNodes);
			}

			/*
			 * Hide hidden edges
			 */
			final HiddenEdges hEdges = (HiddenEdges) targetRoot
					.getHiddenEdges();
			if (curNetView != Cytoscape.getNullNetworkView() && hEdges != null) {
				setHiddenEdges(curNetView, hEdges);
			}

			/*
			 * Restore selected nodes
			 */
			final SelectedNodes sNodes = (SelectedNodes) targetRoot
					.getSelectedNodes();
			if (sNodes != null) {
				setSelectedNodes(rootNetwork, sNodes);
			}

			/*
			 * Restore selected edges
			 */
			final SelectedEdges sEdges = (SelectedEdges) targetRoot
					.getSelectedEdges();
			if (sEdges != null) {
				setSelectedEdges(rootNetwork, sEdges);
			}

			/*
			 * Move to the next network in the tree.
			 */
			if (cysessionSource.getClass() == URL.class) {
				walkTree(targetRoot, rootNetwork, cysessionSource);
			} else if (cysessionSource.getClass() == String.class) {
				walkTree(targetRoot, rootNetwork, sessionFile);
			}
		}

		if (sessionFile != null) {
			sessionFile.close();
		}

		/*
		 * Set VS for each network view
		 */
		String currentNetworkID = null;
		final Iterator viewIt = vsMap.keySet().iterator();
		while (viewIt.hasNext()) {
			currentNetworkID = (String) viewIt.next();
			final CyNetworkView targetView = Cytoscape
					.getNetworkView(currentNetworkID);
			if (targetView != Cytoscape.getNullNetworkView()) {
				targetView.setVisualStyle((String) vsMap.get(currentNetworkID));
				targetView.applyVizmapper(targetView.getVisualStyle());
			}
		}
		return false;
	}

	private void setSelectedNodes(final CyNetwork network,
			final SelectedNodes selected) {

		final List selectedNodeList = new ArrayList();
		final Iterator it = selected.getNode().iterator();

		while (it.hasNext()) {
			final Node selectedNode = (Node) it.next();
			selectedNodeList.add(Cytoscape.getCyNode(selectedNode.getId(),
					false));
		}
		network.setSelectedNodeState(selectedNodeList, true);
	}

	private void setHiddenNodes(final CyNetworkView view,
			final HiddenNodes hidden) {
		final Iterator it = hidden.getNode().iterator();
		while (it.hasNext()) {
			final Node hiddenNodeObject = (Node) it.next();
			final CyNode hiddenNode = Cytoscape.getCyNode(hiddenNodeObject
					.getId(), false);
			view.hideGraphObject(view.getNodeView(hiddenNode));
		}
	}

	private void setHiddenEdges(final CyNetworkView view,
			final HiddenEdges hidden) {
		final Iterator it = hidden.getEdge().iterator();
		while (it.hasNext()) {
			final Edge hiddenEdgeObject = (Edge) it.next();
			final CyEdge hiddenEdge = getEdgeFromID(hiddenEdgeObject.getId());
			if (hiddenEdge != null) {
				view.hideGraphObject(view.getEdgeView(hiddenEdge));
			}
		}
	}

	private void setSelectedEdges(final CyNetwork network,
			final SelectedEdges selected) {

		CyEdge targetEdge = null;
		final List selectedEdgeList = new ArrayList();
		final Iterator it = selected.getEdge().iterator();
		while (it.hasNext()) {

			final cytoscape.generated.Edge selectedEdge = (cytoscape.generated.Edge) it
					.next();
			targetEdge = getEdgeFromID(selectedEdge.getId());
			if (targetEdge != null) {
				selectedEdgeList.add(targetEdge);
			}
		}
		network.setSelectedEdgeState(selectedEdgeList, true);
	}

	private CyEdge getEdgeFromID(final String edgeID) {
		CyEdge targetEdge = null;
		final String[] parts = edgeID.split(" ");
		if (parts.length == 3) {
			final CyNode source = Cytoscape.getCyNode(parts[0], false);
			final CyNode target = Cytoscape.getCyNode(parts[2], false);
			final String interaction = parts[1].substring(1,
					parts[1].length() - 1);
			targetEdge = Cytoscape.getCyEdge(source, target,
					Semantics.INTERACTION, interaction, false);
		}
		return targetEdge;
	}

	/**
	 * Load the root network and then create its children.<br>
	 * 
	 * @param currentNetwork
	 * @param parent
	 * @param sessionSource
	 * @throws JAXBException
	 * @throws IOException
	 */
	private void walkTree(final Network currentNetwork, final CyNetwork parent,
			final Object sessionSource) throws JAXBException, IOException {

		// Get the list of children under this root
		final List children = currentNetwork.getChild();

		// Traverse using recursive call
		for (int i = 0; i < children.size(); i++) {

			final Child child = (Child) children.get(i);
			final Network childNet = (Network) netMap.get(child.getId());
			String vsName = childNet.getVisualStyle();
			if (vsName == null) {
				vsName = "default";
			}

			vsMapByName.put(child.getId(), vsName);

			final String childFile = sessionID + "/" + childNet.getFilename();

			InputStream networkStream = null;
			if (sessionSource.getClass() == ZipFile.class) {
				final ZipFile zipSourceFile = (ZipFile) sessionSource;
				networkStream = zipSourceFile.getInputStream(zipSourceFile
						.getEntry(childFile));
			} else if (sessionSource.getClass() == URL.class) {
				final URL targetNetworkURL = (URL) networkURLs.get(childFile);
				final JarURLConnection jarConnection = (JarURLConnection) (targetNetworkURL)
						.openConnection();
				networkStream = (InputStream) jarConnection.getContent();
			}

			final CyNetwork new_network = this.createNetwork(parent,
					networkStream, childNet.isViewAvailable());

			/*
			 * Extract network view id available
			 */
			final CyNetworkView curNetView = Cytoscape
					.getNetworkView(new_network.getIdentifier());

			if (networkStream != null) {
				networkStream.close();
			}
			vsMap.put(new_network.getIdentifier(), vsName);
			networkList.add(new_network.getIdentifier());

			/*
			 * Hide hidden nodes
			 */
			final HiddenNodes hNodes = (HiddenNodes) childNet.getHiddenNodes();
			if (curNetView != Cytoscape.getNullNetworkView() && hNodes != null) {
				setHiddenNodes(curNetView, hNodes);
			}

			/*
			 * Hide hidden edges
			 */
			final HiddenEdges hEdges = (HiddenEdges) childNet.getHiddenEdges();
			if (curNetView != Cytoscape.getNullNetworkView() && hEdges != null) {
				setHiddenEdges(curNetView, hEdges);
			}

			final SelectedNodes sNodes = (SelectedNodes) childNet
					.getSelectedNodes();
			if (sNodes != null) {
				setSelectedNodes(new_network, sNodes);
			}

			final SelectedEdges sEdges = (SelectedEdges) childNet
					.getSelectedEdges();
			if (sEdges != null) {
				setSelectedEdges(new_network, sEdges);
			}

			if (childNet.getChild().size() != 0) {
				walkTree(childNet, new_network, sessionSource);
			}
		}
	}

	private CyNetwork createNetwork(final CyNetwork parent, final InputStream is,
			final boolean viewAvailable) throws IOException, JAXBException {

		// Create reader and read an XGMML file
		XGMMLReader reader;
		reader = new XGMMLReader(is);
		reader.read();

		// Get the RootGraph indices of the nodes and
		// Edges that were just created
		final int[] nodes = reader.getNodeIndicesArray();
		final int[] edges = reader.getEdgeIndicesArray();

		// Create the CyNetwork
		// First, set the view threshold to 0. By doing so, we can disable
		// the auto-creating of the CyNetworkView.

		final int realThreshold = Integer.valueOf(
				CytoscapeInit.getProperties().getProperty("viewThreshold",
						"5000")).intValue();

		CytoscapeInit.setViewThreshold(0);

		CyNetwork network = null;
		if (parent == null) {
			network = Cytoscape.createNetwork(nodes, edges, reader
					.getNetworkID());

		} else {
			network = Cytoscape.createNetwork(nodes, edges, reader
					.getNetworkID(), parent);
		}

		// Set network Attributes here, not in the read() method in XGMMLReader!
		// Otherwise, ID mismatch may happen.
		reader.setNetworkAttributes(network);

		// network.putClientData(
		// "metaNodeViewer.model.GPMetaNodeFactory.metaNodeRindices",
		// reader.getMetanodeList());

		// Reset back to the real View Threshold
		CytoscapeInit.setViewThreshold(realThreshold);

		// Conditionally, Create the CyNetworkView

		if (viewAvailable) {
			createCyNetworkView(network);

			if (Cytoscape.getNetworkView(network.getIdentifier()) != Cytoscape
					.getNullNetworkView()) {
				reader
						.layout(Cytoscape.getNetworkView(network
								.getIdentifier()));
			}

			// Lastly, make the GraphView Canvas Visible.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					final DingNetworkView view = (DingNetworkView) Cytoscape
							.getCurrentNetworkView();
					view.setGraphLOD(new CyGraphLOD());
				}
			});

			final String curVS = (String) vsMapByName.get(network.getTitle());
			final CyNetworkView curView = Cytoscape.getNetworkView(network
					.getIdentifier());
			if (curVS != null) {
				curView.setVisualStyle(curVS);
				Cytoscape.getDesktop().getVizMapUI().getStyleSelector()
						.resetStyles(curVS);
				Cytoscape.getDesktop().getVizMapUI().visualStyleChanged();
				Cytoscape.getVisualMappingManager().setVisualStyle(curVS);

			} else {
				curView.setVisualStyle(Cytoscape.getVisualMappingManager()
						.getVisualStyle().getName());
			}

			// set view zoom
			final Double zoomLevel = reader.getGraphViewZoomLevel();
			if (zoomLevel != null) {
				curView.setZoom(zoomLevel.doubleValue());
			}
			// set view center
			final Point2D center = reader.getGraphViewCenter();
			if (center != null) {
				((DGraphView) curView).setCenter(center.getX(), center.getY());
			}
		}
		return network;
	}

	// same as other loaders
	//
	private void createCyNetworkView(final CyNetwork cyNetwork) {
		final DingNetworkView view = new DingNetworkView(cyNetwork, cyNetwork
				.getTitle());

		view.setIdentifier(cyNetwork.getIdentifier());
		Cytoscape.getNetworkViewMap().put(cyNetwork.getIdentifier(), view);
		view.setTitle(cyNetwork.getTitle());

		// set the selection mode on the view
		Cytoscape.setSelectionMode(Cytoscape.getSelectionMode(), view);

		Cytoscape.firePropertyChange(
				cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, null,
				view);
	}
}
