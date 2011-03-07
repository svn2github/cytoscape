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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.cytoscape.io.internal.read.MarkSupportedInputStream;
import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.read.CyPropertyReader;
import org.cytoscape.io.read.CyPropertyReaderManager;
import org.cytoscape.io.read.CySessionReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.session.Child;
import org.cytoscape.property.session.Cysession;
import org.cytoscape.property.session.Edge;
import org.cytoscape.property.session.Network;
import org.cytoscape.property.session.Node;
import org.cytoscape.session.CySession;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 */
public class SessionReaderImpl extends AbstractTask implements CySessionReader {

	public static final String CYSESSION = "cysession.xml";
	public static final String VIZMAP_PROPS = "vizmap.props";
	public static final String CY_PROPS = "cytoscape.props";
	public static final String XGMML_EXT = ".xgmml";
	public  static final String BOOKMARKS_FILE = "session_bookmarks.xml";
	public  static final String NETWORK_ROOT = "Network Root";


	private static final Logger logger = LoggerFactory.getLogger(SessionReaderImpl.class);

	private final Map<String, List<File>> pluginFileListMap = new HashMap<String, List<File>>();
	private final Map<String,CyNetworkView[]> networkViews = new HashMap<String,CyNetworkView[]>();
	private final Map<CyNetworkView,String> visualStyleMap = new HashMap<CyNetworkView,String>();
	private final Map<String, Network> netMap = new HashMap<String, Network>();

	private final InputStream sourceInputStream;
	private final CyNetworkViewReaderManager netviewReaderMgr; 
	private final CyPropertyReaderManager propertyReaderMgr; 

	private Cysession cysession;
	private Bookmarks bookmarks;
	private TaskMonitor taskMonitor;
	private Properties cytoscapeProps;
	private Properties vizmapProps;
	private final CyProperty<Properties> properties;

	/**
	 */
	public SessionReaderImpl(final InputStream sourceInputStream, 
	                         final CyNetworkViewReaderManager netviewReaderMgr, 
	                         CyPropertyReaderManager propertyReaderMgr,
	                         CyProperty<Properties> properties) {

		if ( sourceInputStream == null )
			throw new NullPointerException("input stream is null!");
		this.sourceInputStream = sourceInputStream;

		if ( netviewReaderMgr == null )
			throw new NullPointerException("network view reader manager is null!");
		this.netviewReaderMgr = netviewReaderMgr;	

		if ( propertyReaderMgr == null )
			throw new NullPointerException("property reader manager is null!");
		this.propertyReaderMgr = propertyReaderMgr;
		
		if ( properties == null )
            throw new NullPointerException("properties is null!");
		this.properties = properties;
	}


	/**
	 * Read a session file.
	 */
	public void run(TaskMonitor tm) throws Exception {
		taskMonitor = tm;

		extractEntries();
		processNetworks();
	}

	public CySession getCySession() {

		HashSet<CyNetworkView> views = new HashSet<CyNetworkView>();
		for ( CyNetworkView[] va : networkViews.values() )
			for ( CyNetworkView v : va )
				views.add( v );

		CySession ret = new CySession.Builder()
			.networkViews( views )
			.viewVisualStyleMap( visualStyleMap )
			.cytoscapeProperties( cytoscapeProps )
			.vizmapProperties( vizmapProps )
			.bookmarks( bookmarks )
			.cysession( cysession )
			.pluginFileListMap( pluginFileListMap )
			.build();

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
			//System.out.println("SESSION entry name: " + entryName);
			InputStream tmpIs = new MarkSupportedInputStream(zis);

			try {

			if (entryName.contains("/plugins/")) {
				//System.out.println("   extracting plugin entry");
				extractPluginEntry(tmpIs, entryName);
			} else if (entryName.endsWith(CYSESSION)) {
				//System.out.println("   extracting session file");
				extractSessionState(tmpIs);
			} else if (entryName.endsWith(VIZMAP_PROPS)) {
				//System.out.println("   extracting vizmap props");
				extractVizmapProps(tmpIs);
			} else if (entryName.endsWith(CY_PROPS)) {
				//System.out.println("   extracting cytoscape props");
				extractCytoscapeProps(tmpIs);
			} else if (entryName.endsWith(XGMML_EXT)) {
				//System.out.println("   extracting network");
				extractNetwork(tmpIs, entryName);
			} else if (entryName.endsWith(BOOKMARKS_FILE)) {
				extractBookmarks(tmpIs);
				//System.out.println("   extracting bookmarks");
			} else {
				logger.warn("Unknown entry found in session zip file!\n" + entryName);
			}

			} catch (Exception e) {
				logger.warn("Failed reading session entry: " + entryName, e); 
		
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
	    // Get the current state of the style builder switch
	    Properties prop = properties.getProperties();
	    String vsbSwitch = prop.getProperty("visualStyleBuilder");
	    // Since we're reading a session (which already has visual styles defined)
	    // force the vsbSwitch off
	    prop.setProperty("visualStyleBuilder", "off");
	    
		CyNetworkViewReader reader = netviewReaderMgr.getReader(is);
		reader.run(taskMonitor);
		networkViews.put(entryName, reader.getNetworkViews());
		
		// Restore the original state of the style builder switch
        if (vsbSwitch != null) prop.setProperty("visualStyleBuilder", vsbSwitch);
        else prop.remove("visualStyleBuilder");
	}

	private void extractPluginEntry(InputStream is, String entryName) {
		String[] items = entryName.split("/");

		if (items.length < 3) {
			// It's a directory name, not a file name
			return;
		}

		String pluginName = items[2];
		String fileName = items[items.length-1];
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		File theFile = new File(tmpDir, fileName);

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
			logger.warn("Error: read from zip: " + entryName, e);
			return;
		}

		// Put the file into pluginFileListMap
		if (!pluginFileListMap.containsKey(pluginName)) 
			pluginFileListMap.put(pluginName, new ArrayList<File>());

		List<File> fileList = pluginFileListMap.get(pluginName);
		fileList.add(theFile);

	}

	private void extractVizmapProps(InputStream is) throws Exception {
		
		CyPropertyReader reader = propertyReaderMgr.getReader(is);
		if(reader == null){
			return;
		}
		reader.run(taskMonitor);
		vizmapProps = (Properties) reader.getProperty(); 
	}

	private void extractCytoscapeProps(InputStream is) throws Exception {
		CyPropertyReader reader = propertyReaderMgr.getReader(is);
		reader.run(taskMonitor);
		cytoscapeProps = (Properties) reader.getProperty(); 
	}

	private void extractBookmarks(InputStream is) throws Exception {
		CyPropertyReader reader = propertyReaderMgr.getReader(is);
		reader.run(taskMonitor);
		bookmarks = (Bookmarks) reader.getProperty(); 
	}

	private void extractSessionState(InputStream is) throws Exception {
		CyPropertyReader reader = propertyReaderMgr.getReader(is);
		reader.run(taskMonitor);
		cysession = (Cysession) reader.getProperty(); 
	}

	private void processNetworks() throws Exception {
		
		if (cysession.getNetworkTree() == null){
			return;
		}
		
		for (Network curNet : cysession.getNetworkTree().getNetwork()) 
			netMap.put(curNet.getId(), curNet);

		walkTree(netMap.get(NETWORK_ROOT), null);
	}

	/**
	 * Load the root network and then create its children.<br>
	 *
	 * @param currentNetwork
	 * @param parent
	 * @throws IOException
	 */
	private void walkTree(final Network currentNetwork, final CyNetwork parent) throws Exception {

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
			nodeMap.put(n.getCyRow().get("name",String.class), n);
		
		// set attr values based on ids
		while (it.hasNext()) {
			final Node nodeObject = (Node) it.next();
			nodeMap.get(nodeObject.getId()).getCyRow().set(attrName,true);
		}
	}

	private void setBooleanEdgeAttr(final CyNetwork net, final Iterator it, final String attrName) {
		if (it == null) 
			return;

		// create an id map
		Map<String,CyEdge> edgeMap = new HashMap<String,CyEdge>();
		for ( CyEdge e : net.getEdgeList() ) 
			edgeMap.put(e.getCyRow().get("name",String.class), e);
		
		// set attr values based on ids
		while (it.hasNext()) {
			final Edge edgeObject = (Edge) it.next();
			edgeMap.get(edgeObject.getId()).getCyRow().set(attrName,true);
		}
	}
}
