/*
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
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
package org.cytoscape.io.internal.read.session;


import org.cytoscape.io.internal.generated.*;

import org.cytoscape.io.read.CySessionReader;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.session.CySession;
import org.cytoscape.io.internal.read.MarkSupportedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 */
public class SessionReaderImpl extends AbstractTask implements CySessionReader {
	/**
	 *
	 */
	public static final String PACKAGE_NAME = "org.cytoscape.io.internal.generated";

	/**
	 *
	 */
	public static final String BOOKMARK_PACKAGE_NAME = "cytoscape.bookmarks";

	/**
	 *
	 */
	public static final String CYSESSION = "cysession.xml";

	/**
	 *
	 */
	public static final String VIZMAP_PROPS = "vizmap.props";

	/**
	 *
	 */
	public static final String CY_PROPS = "cytoscape.props";

	/**
	 *
	 */
	public static final String XGMML_EXT = ".xgmml";
	private static final String BOOKMARKS_FILE = "session_bookmarks.xml";
	private static final String NETWORK_ROOT = "Network Root";
	private static final Logger logger = LoggerFactory.getLogger(SessionReaderImpl.class);
	private final Map<String, List<File>> pluginFileListMap = new HashMap<String, List<File>>();
	private final Map<String,CyNetworkView[]> networkViews = new HashMap<String,CyNetworkView[]>();
	private final Map<CyNetworkView,String> visualStyleMap = new HashMap<CyNetworkView,String>();
	private final Map<String, Network> netMap = new HashMap<String, Network>();
	private final InputStream sourceInputStream;

	private final CyNetworkViewReaderManager netviewReaderMgr; 

	private Cysession session;
//	private Bookmarks bookmarks;
	private TaskMonitor taskMonitor;
	private Properties desktopProps;
	private Properties cytoscapeProps;
	private Properties vizmapProps;

	/**
	 */
	public SessionReaderImpl(final InputStream sourceInputStream, final CyNetworkViewReaderManager netviewReaderMgr) {

		if ( sourceInputStream == null )
			throw new NullPointerException("input stream is null!");
		this.sourceInputStream = sourceInputStream;

		if ( netviewReaderMgr == null )
			throw new NullPointerException("network view reader manager is null!");
		this.netviewReaderMgr = netviewReaderMgr;	
	}


	/**
	 * Read a session file.
	 *
	 * @throws IOException
	 * @throws JAXBException
	 */
	public void run(TaskMonitor tm) throws Exception {
		System.out.println("SESSION starting to read");
		taskMonitor = tm;

		extractEntries();
		processNetworks();


//		if (session.getSessionState().getDesktop() != null) {
//			restoreDesktopState();
//		}

//		if (session.getSessionState().getServer() != null) {
			// TODO 
			//restoreOntologyServerStatus();
//		}

		// Send signal to others
//		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
//		Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, null);

		// Send signal to plugins
//		Cytoscape.firePropertyChange(Cytoscape.RESTORE_PLUGIN_STATE, pluginFileListMap, null);
//		deleteTmpPluginFiles();

		// Send message with list of loaded networks.
//		Cytoscape.firePropertyChange(Cytoscape.SESSION_LOADED, null, networkList);

		// Restore listener for VizMapper.
//		if (Cytoscape.getDesktop() != null) {

			// Cleanup view
//			final GraphView curView = Cytoscape.getCurrentNetworkView();

//			if ((curView != null) && (curView.equals(Cytoscape.getNullNetworkView()) == false)) {
//				VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
//				VisualStyle lastVS = vmm.getVisualStyle(lastVSName);
//				vmm.setVisualStyleForView(curView,lastVS);
//				vmm.setNetworkView(curView);
//				vmm.setVisualStyle(lastVS);
//				Cytoscape.redrawGraph(curView);
//			}

//			Cytoscape.getDesktop().getVizMapperUI().enableListeners(true);
//		}
	}

	public CySession getCySession() {
		CySessionImpl ret = new CySessionImpl();

		HashSet<CyNetworkView> views = new HashSet<CyNetworkView>();
		for ( CyNetworkView[] va : networkViews.values() )
			for ( CyNetworkView v : va )
				views.add( v );

		ret.setNetworkViews( views );
		ret.setViewVisualStyleMap( visualStyleMap );
		ret.setCytoscapeProperties( cytoscapeProps );
		ret.setVizmapProperties( vizmapProps );
		ret.setDesktopProperties( desktopProps );

		return ret;
	}

	/**
	 * Extract Zip entries in the remote file
	 *
	 * @param sourceName
	 * @throws IOException
	 */
	private void extractEntries() throws Exception {
		ZipInputStream zis = new ZipInputStream(sourceInputStream);

		try {

		// Extract list of entries
		ZipEntry zen = null;
		String entryName = null;

		while ((zen = zis.getNextEntry()) != null) {
			entryName = zen.getName();
			System.out.println("SESSION entry name: " + entryName);
			InputStream tmpIs = new MarkSupportedInputStream(zis);

			try {

			if (entryName.contains("/plugins/")) {
				extractPluginEntry(tmpIs, entryName);
				System.out.println("   extracting plugin entry");
			} else if (entryName.endsWith(CYSESSION)) {
				extractSessionState(tmpIs);
				System.out.println("   extracting session file");
			} else if (entryName.endsWith(VIZMAP_PROPS)) {
				extractVizmapProps(tmpIs);
				System.out.println("   extracting vizmap props");
			} else if (entryName.endsWith(CY_PROPS)) {
				extractCytoscapeProps(tmpIs);
				System.out.println("   extracting cytoscape props");
			} else if (entryName.endsWith(XGMML_EXT)) {
				extractNetwork(tmpIs, entryName);
				System.out.println("   extracting network");
			} else if (entryName.endsWith(BOOKMARKS_FILE)) {
				System.out.println("   extracting bookmarks");
// TODO
//				extractBookmarks(tmpIs);
			} else {
				System.out.println("Unknown entry found in session zip file!\n" + entryName);
			}

		
			} finally {
				if ( tmpIs != null )
					tmpIs.close();
				tmpIs = null;
			}

			zis.closeEntry();
		} 

		} finally {
			if (zis != null)
				zis.close();
			zis = null;
		}
	}

	private void extractNetwork(InputStream is, String entryName) throws Exception {
		CyNetworkViewReader reader = netviewReaderMgr.getReader(is);
		reader.run(taskMonitor);
		networkViews.put(entryName, reader.getNetworkViews());
	}

	private void extractPluginEntry(InputStream is, String entryName) {
		String[] items = entryName.split("/");

		if (items.length < 3) {
			// It's a directory name, not a file name
			return;
		}

		String pluginName = items[2];

		File theFile = new File(entryName);

		try {
			// Write input stream into tmp file
			BufferedWriter out = null;
			BufferedReader in = null;

			in = new BufferedReader(new InputStreamReader(is));
			out = new BufferedWriter(new FileWriter(theFile));

			// Write to tmp file
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				out.write(inputLine);
				out.newLine();
			}

			in.close();
			out.close();
		} catch (IOException e) {
			logger.warn("Error: read from zip: " + entryName);
			return;
		}

		// Put the file into pluginFileListMap
		if (!pluginFileListMap.containsKey(pluginName)) 
			pluginFileListMap.put(pluginName, new ArrayList<File>());

		List<File> fileList = pluginFileListMap.get(pluginName);
		fileList.add(theFile);

	}

	private void extractVizmapProps(InputStream is) throws Exception {
		vizmapProps = new Properties();
		vizmapProps.load( is );
	}

	private void extractCytoscapeProps(InputStream is) throws Exception {
		cytoscapeProps = new Properties();
		cytoscapeProps.load( is );
	}
/*
	private void extractBookmarks(InputStream is) {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(BOOKMARK_PACKAGE_NAME,
			                                                        this.getClass().getClassLoader());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			bookmarks = (Bookmarks) unmarshaller.unmarshal(is);

		} catch (Exception e1) {
			logger.warn("bookmarks not read", e1);
		}
	}
	*/

	private void extractSessionState(InputStream is) throws Exception {
		final JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE_NAME,
		                                                        this.getClass().getClassLoader());
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		session = (Cysession) unmarshaller.unmarshal(is);

		// extract the desktop state
		desktopProps = new Properties();

		desktopProps.setProperty("desktop.width",session.getSessionState().getDesktop().getDesktopSize().getWidth().toString());
		desktopProps.setProperty("desktop.height",session.getSessionState().getDesktop().getDesktopSize().getHeight().toString());


		final List<NetworkFrame> frames = session.getSessionState().getDesktop().getNetworkFrames()
		                                                           .getNetworkFrame();
		for(NetworkFrame netFrame: frames) {
			String id = netFrame.getFrameID();
			desktopProps.setProperty("network.width." + id, netFrame.getWidth().toString());
			desktopProps.setProperty("network.height." + id, netFrame.getHeight().toString());
			desktopProps.setProperty("network.xpos." + id, netFrame.getX().toString());
			desktopProps.setProperty("network.ypos." + id, netFrame.getY().toString());
		}

		// TODO consider tracking cytopanel state here too!
	}

	private void processNetworks() throws JAXBException, IOException, Exception {

		for (Network curNet : session.getNetworkTree().getNetwork()) 
			netMap.put(curNet.getId(), curNet);

		walkTree(netMap.get(NETWORK_ROOT), null);
	}

	/**
	 * Load the root network and then create its children.<br>
	 *
	 * @param currentNetwork
	 * @param parent
	 * @throws JAXBException
	 * @throws IOException
	 */
	private void walkTree(final Network currentNetwork, final CyNetwork parent) throws JAXBException, IOException {

		for (Child child : currentNetwork.getChild() ) {

			Network childNet = netMap.get(child.getId());

			CyNetworkView view = getNetworkView(childNet.getFilename()); 
			if ( view == null ) {
				logger.warn("failed to find network in session: " + childNet.getFilename());
				return;
			}

			String vsName = childNet.getVisualStyle();
			if (vsName == null)
				vsName = "default";

			visualStyleMap.put( view, vsName );

			CyNetwork new_network = view.getModel();

			if ( childNet.getHiddenNodes() != null )
				setBooleanNodeAttr(new_network, childNet.getHiddenNodes().getNode().iterator(), "hidden");
			if ( childNet.getHiddenEdges() != null )
				setBooleanEdgeAttr(new_network, childNet.getHiddenEdges().getEdge().iterator(), "hidden");
			if ( childNet.getSelectedNodes() != null )
				setBooleanNodeAttr(new_network, childNet.getSelectedNodes().getNode().iterator(), "selected");
			if ( childNet.getSelectedEdges() != null )
				setBooleanEdgeAttr(new_network, childNet.getSelectedEdges().getEdge().iterator(), "selected");

			// Load child networks
			if (childNet.getChild().size() != 0)
				// child 2
				walkTree(childNet, new_network);
		}
	}

	private CyNetworkView getNetworkView(String name) {
		for ( String s : networkViews.keySet() ) {
			if ( s.endsWith( "/" + name ) ) {
				// this is OK since XGMML only ever reads one network
				return networkViews.get(s)[0];
			}
		}
		return null;
	}

	private void setBooleanNodeAttr(final CyNetwork net, Iterator it, final String attrName) {
		if (it == null) 
			return;

		// create an id map
		Map<String,CyNode> nodeMap = new HashMap<String,CyNode>();
		for ( CyNode n : net.getNodeList() ) 
			nodeMap.put(n.attrs().get("name",String.class), n);
		
		// set attr values based on ids
		while (it.hasNext()) {
			final Node nodeObject = (Node) it.next();
			nodeMap.get(nodeObject.getId()).attrs().set(attrName,true);
		}
	}

	private void setBooleanEdgeAttr(final CyNetwork net, final Iterator it, final String attrName) {
		if (it == null) 
			return;

		// create an id map
		Map<String,CyEdge> edgeMap = new HashMap<String,CyEdge>();
		for ( CyEdge e : net.getEdgeList() ) 
			edgeMap.put(e.attrs().get("name",String.class), e);
		
		// set attr values based on ids
		while (it.hasNext()) {
			final Edge edgeObject = (Edge) it.next();
			edgeMap.get(edgeObject.getId()).attrs().set(attrName,true);
		}
	}
}
