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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.cytoscape.io.internal.read.MarkSupportedInputStream;
import org.cytoscape.io.internal.read.datatable.CSVCyReaderFactory;
import org.cytoscape.io.internal.read.session.CyTableMetadataImpl.CyTableMetadataBuilder;
import org.cytoscape.io.internal.read.xgmml.XGMMLNetworkReader;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.CyPropertyReader;
import org.cytoscape.io.read.CyPropertyReaderManager;
import org.cytoscape.io.read.CySessionReader;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.io.read.VizmapReader;
import org.cytoscape.io.read.VizmapReaderManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableMetadata;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.session.Cysession;
import org.cytoscape.property.session.Edge;
import org.cytoscape.property.session.Network;
import org.cytoscape.property.session.Node;
import org.cytoscape.session.CySession;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 */
public class SessionReaderImpl extends AbstractTask implements CySessionReader {

	public static final String CYSESSION = "cysession.xml";
	public static final String VIZMAP_PROPS = "vizmap.props";
	public static final String VIZMAP_XML = "vizmap.xml";
	public static final String CY_PROPS = "cytoscape.props";
	public static final String XGMML_EXT = ".xgmml";
	public static final String BOOKMARKS_FILE = "session_bookmarks.xml";
	private static final String TABLE_EXT = ".table";
	public static final String NETWORK_ROOT = "Network Root";
	public static final int MAJOR_DOC_VERSION = 3;
	public static final Pattern NETWORK_PATTERN = Pattern.compile(".*/([^/]+)[.]xgmml");
	public static final Pattern NETWORK_TABLE_PATTERN = Pattern.compile(".*/([^/]+)/([^/]+)-([^/]+)-([^/]+)[.]table");
	public static final Pattern GLOBAL_TABLE_PATTERN = Pattern.compile(".*/(\\d+)-([^/]+)[.]table");

	private static final Logger logger = LoggerFactory.getLogger(SessionReaderImpl.class);

	private final Map<String, List<File>> pluginFileListMap = new HashMap<String, List<File>>();
	private final Map<String,CyNetworkView[]> networkViews = new HashMap<String,CyNetworkView[]>();
	private final Map<CyNetworkView,String> visualStyleMap = new HashMap<CyNetworkView,String>();
	private final Map<String,Set<CyTableMetadataBuilder>> networkTableMap = new HashMap<String,Set<CyTableMetadataBuilder>>();
	private final Set<CyTableMetadata> tableMetadata = new HashSet<CyTableMetadata>();

	private final InputStream sourceInputStream;
	private final CyNetworkReaderManager netviewReaderMgr; 
	private final CyPropertyReaderManager propertyReaderMgr;
	private final VizmapReaderManager vizmapReaderMgr;
	private final CSVCyReaderFactory csvCyReaderFactory;

	private Cysession cysession;
	private Bookmarks bookmarks;
	private TaskMonitor taskMonitor;
	private Properties cytoscapeProps;
	private Set<VisualStyle> visualStyles;
	private final CyProperty<Properties> properties;

	/**
	 */
	public SessionReaderImpl(final InputStream sourceInputStream, 
	                         final CyNetworkReaderManager netviewReaderMgr, 
	                         final CyPropertyReaderManager propertyReaderMgr,
	                         final VizmapReaderManager vizmapReaderMgr,
	                         final CSVCyReaderFactory csvCyReaderFactory,
	                         final CyProperty<Properties> properties) {

		if ( sourceInputStream == null )
			throw new NullPointerException("input stream is null!");
		this.sourceInputStream = sourceInputStream;

		if ( netviewReaderMgr == null )
			throw new NullPointerException("network view reader manager is null!");
		this.netviewReaderMgr = netviewReaderMgr;	

		if ( propertyReaderMgr == null )
			throw new NullPointerException("property reader manager is null!");
		this.propertyReaderMgr = propertyReaderMgr;
		
		if ( vizmapReaderMgr == null )
		    throw new NullPointerException("vizmap reader manager is null!");
		this.vizmapReaderMgr = vizmapReaderMgr;
		
		if ( csvCyReaderFactory == null )
			throw new NullPointerException("table reader manager is null!");
		this.csvCyReaderFactory = csvCyReaderFactory;
		
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
		mergeNetworkTables();
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
			.visualStyles( visualStyles )
			.bookmarks( bookmarks )
			.cysession( cysession )
			.pluginFileListMap( pluginFileListMap )
			.tables( tableMetadata )
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
				extractSessionState(tmpIs, entryName);
			} else if (entryName.endsWith(VIZMAP_XML) || entryName.endsWith(VIZMAP_PROPS)) {
				//System.out.println("   extracting vizmap");
				extractVizmap(tmpIs, entryName);
			} else if (entryName.endsWith(CY_PROPS)) {
				//System.out.println("   extracting cytoscape props");
				extractCytoscapeProps(tmpIs, entryName);
			} else if (entryName.endsWith(XGMML_EXT)) {
				//System.out.println("   extracting network");
				extractNetwork(tmpIs, entryName);
			} else if (entryName.endsWith(BOOKMARKS_FILE)) {
				extractBookmarks(tmpIs, entryName);
				//System.out.println("   extracting bookmarks");
			} else if (entryName.endsWith(TABLE_EXT)) {
				extractTable(tmpIs, entryName);
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

	private void mergeNetworkTables() {
		for (Entry<String, CyNetworkView[]> entry : networkViews.entrySet()) {
			String entryName = entry.getKey();
			Matcher matcher = NETWORK_PATTERN.matcher(entryName);
			if (!matcher.matches()) {
				continue;
			}
			String name = matcher.group(1);
			CyNetworkView view = entry.getValue()[0];
			CyNetwork network = view.getModel();
			Set<CyTableMetadataBuilder> builders = networkTableMap.get(name);
			if (builders == null) {
				continue;
			}
			for (CyTableMetadataBuilder builder : builders) {
				Set<CyNetwork> networks = new HashSet<CyNetwork>();
				networks.add(network);
				CyTableMetadata metadata = builder
					.setNetworks(networks)
					.build();
				// TODO: Merging serialized tables with respective networks is
				// currently disabled due to timing conflicts with Ding. 
				//mergeNetworkTable(network, metadata);
				tableMetadata.add(metadata);
			}
		}
	}

	private void mergeNetworkTable(CyNetwork network, CyTableMetadata metadata) {
		Class<?> type = metadata.getType();
		CyTableEntry entry = null;
		CyTable mapping = null;
		if (type.equals(CyNetwork.class)) {
			CyTable sourceTable = metadata.getCyTable();
			String keyName = sourceTable.getPrimaryKey().getName();
			// Network tables should only have one row.
			CyRow sourceRow = sourceTable.getAllRows().iterator().next();
			CyRow targetRow = network.getCyRow(metadata.getNamespace());
			mergeRow(keyName, sourceRow, targetRow);
			return;
		} else if (type.equals(CyNode.class)) {
			entry = network.getNodeList().iterator().next();
			mapping = network.getDefaultNodeTable();
		} else if (type.equals(CyEdge.class)) {
			entry = network.getEdgeList().iterator().next();
			mapping = network.getDefaultEdgeTable();
		}
		if (entry == null) {
			return;
		}
		CyRow row = entry.getCyRow(metadata.getNamespace());
		Map<Long, Long> mappings = createSUIDMappings(mapping);
		mergeTables(metadata.getCyTable(), row.getTable(), mappings);
	}

	private Map<Long, Long> createSUIDMappings(CyTable mapping) {
		if (mapping == null) {
			return Collections.emptyMap();
		}
		
		Map<Long, Long> mappings = new HashMap<Long, Long>();
		String key = mapping.getPrimaryKey().getName();
		for (CyRow row : mapping.getAllRows()) {
			Long oldSUID = row.get(XGMMLNetworkReader.ORIGINAL_ID_COLUMN, Long.class);
			Long newSUID = row.get(key, Long.class);
			mappings.put(oldSUID, newSUID);
		}
		return mappings;
	}


	private void mergeTables(CyTable source, CyTable target, Map<Long, Long> mappings) {
		CyColumn sourceKey = source.getPrimaryKey();
		CyColumn targetKey = target.getPrimaryKey();
		String keyName = sourceKey.getName();

		// Make sure keys match
		if (keyName != targetKey.getName()) {
			return;
		}
		
		for (CyRow sourceRow : source.getAllRows()) {
			Long key = sourceRow.get(keyName, Long.class);
			if (mappings != null) {
				key = mappings.get(key);
			}
			CyRow targetRow = target.getRow(key);
			mergeRow(keyName, sourceRow, targetRow);
		}
	}

	private void mergeRow(String keyName, CyRow sourceRow, CyRow targetRow) {
		for (CyColumn column : sourceRow.getTable().getColumns()) {
			String columnName = column.getName();
			if (columnName.equals(keyName)) {
				continue;
			}
			Class<?> type = column.getType();
			if (type.equals(List.class)) {
				Class<?> elementType = column.getListElementType();
				List<?> list = sourceRow.getList(columnName, elementType);
				targetRow.set(columnName, list);
			} else {
				Object value = sourceRow.get(columnName, type);
				targetRow.set(columnName, value);
			}
		}
	}


	private void extractTable(InputStream stream, String entryName) throws Exception {
		csvCyReaderFactory.setInputStream(stream, entryName);
		CyTableReader reader = (CyTableReader) csvCyReaderFactory.getTaskIterator().next();
		reader.run(taskMonitor);
		
		// Assume one table per entry
		CyTable table = reader.getCyTables()[0];
		
		Matcher matcher = NETWORK_TABLE_PATTERN.matcher(entryName);
		if (matcher.matches()) {
			String networkName = matcher.group(1);
			String namespace = matcher.group(2);
			Class<?> type = Class.forName(matcher.group(3));
			String title = URLDecoder.decode(matcher.group(3), "UTF-8");
			table.setTitle(title);
			CyTableMetadataBuilder builder = new CyTableMetadataBuilder()
				.setCyTable(table)
				.setNamespace(namespace)
				.setType(type);
			Set<CyTableMetadataBuilder> builders = networkTableMap.get(networkName);
			if (builders == null) {
				builders = new HashSet<CyTableMetadataBuilder>();
				networkTableMap.put(networkName, builders);
			}
			builders.add(builder);
			return;
		}
		
		matcher = GLOBAL_TABLE_PATTERN.matcher(entryName);
		if (matcher.matches()) {
			// table SUID is in group(1); we may need it when restoring
			// equations/virtual columns
			String title = URLDecoder.decode(matcher.group(2), "UTF-8");
			table.setTitle(title);
			Set<CyNetwork> networks = Collections.emptySet();
			CyTableMetadataBuilder builder = new CyTableMetadataBuilder()
				.setCyTable(table)
				.setNetworks(networks);
			tableMetadata.add(builder.build());
		}
	}

	private void extractNetwork(InputStream is, String entryName) throws Exception {
	    // Get the current state of the style builder switch
	    Properties prop = properties.getProperties();
	    String vsbSwitch = prop.getProperty("visualStyleBuilder");
	    // Since we're reading a session (which already has visual styles defined)
	    // force the vsbSwitch off
	    prop.setProperty("visualStyleBuilder", "off");
	    
		CyNetworkReader reader = netviewReaderMgr.getReader(is, entryName);
		reader.run(taskMonitor);
		CyNetwork[] networks = reader.getCyNetworks();
		CyNetworkView[] views = new CyNetworkView[networks.length];
		int i = 0;
		for(CyNetwork network: networks) {
			views[i] = reader.buildCyNetworkView(network);
			i++;
		}
		networkViews.put(entryName, views);
		
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
	
	private void extractVizmap(InputStream is, String entryName) throws Exception {
		VizmapReader reader = vizmapReaderMgr.getReader(is, entryName);
        reader.run(taskMonitor);
        visualStyles = reader.getVisualStyles();
	}

	private void extractCytoscapeProps(InputStream is, String entryName) throws Exception {
		CyPropertyReader reader = propertyReaderMgr.getReader(is, entryName);
		reader.run(taskMonitor);
		cytoscapeProps = (Properties) reader.getProperty(); 
	}

	private void extractBookmarks(InputStream is, String entryName) throws Exception {
		CyPropertyReader reader = propertyReaderMgr.getReader(is, entryName);
		reader.run(taskMonitor);
		bookmarks = (Bookmarks) reader.getProperty(); 
	}

	private void extractSessionState(InputStream is, String entryName) throws Exception {
		CyPropertyReader reader = propertyReaderMgr.getReader(is, entryName);
		reader.run(taskMonitor);
		cysession = (Cysession) reader.getProperty(); 
	}

	private void processNetworks() throws Exception {
		if (cysession.getNetworkTree() == null){
			return;
		}
		
		boolean isOldFormat = false;
		String docVersion = cysession.getDocumentVersion();
		
		if (docVersion != null) {
			String[] tokens = docVersion.split(".");
			
			if (tokens.length > 0) {
				String t0 = tokens[0];
				
				try {
					int majorVersion = Integer.parseInt(t0);
					isOldFormat = majorVersion < MAJOR_DOC_VERSION;
				} catch (NumberFormatException nfe) {
					logger.warn("The Cysession document version is invalid!");
				}
			}
		} else {
			logger.warn("The Cysession has no document version!");
		}
		
		for (Network net : cysession.getNetworkTree().getNetwork()) {
			// We no longer have the concept of a top-level network root,
			// so let's ignore a network with that name, but only if the Cysession doc version is old.
			if (isOldFormat && net.getId().equals(NETWORK_ROOT)) continue;
			
			CyNetworkView view = getNetworkView(net.getFilename());
			
			if (view == null) {
				logger.warn("Failed to find network in session: " + net.getFilename());
			} else {
    			String vsName = net.getVisualStyle();
    			if (vsName != null) visualStyleMap.put(view, vsName);
    
    			CyNetwork cyNet = view.getModel();
    
    			if (net.getHiddenNodes() != null)
    				setBooleanNodeAttr(cyNet, net.getHiddenNodes().getNode().iterator(), "hidden");
    			if (net.getHiddenEdges() != null)
    				setBooleanEdgeAttr(cyNet, net.getHiddenEdges().getEdge().iterator(), "hidden");
    			
    			if (isOldFormat) {
    				// From Cytoscape 3.0, the selection info is stored inside CyTables
        			if (net.getSelectedNodes() != null)
        				setBooleanNodeAttr(cyNet, net.getSelectedNodes().getNode().iterator(), CyNetwork.SELECTED);
        			if (net.getSelectedEdges() != null)
        				setBooleanEdgeAttr(cyNet, net.getSelectedEdges().getEdge().iterator(), CyNetwork.SELECTED);
    			}
			}
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

	private void setBooleanNodeAttr(final CyNetwork net, Iterator<?> it, final String attrName) {
		if (it == null)  return;

		// create an id map
		Map<String,CyNode> nodeMap = new HashMap<String,CyNode>();
		
		for ( CyNode n : net.getNodeList() ) 
			nodeMap.put(n.getCyRow().get("name",String.class), n);
		
		// set attr values based on ids
		while (it.hasNext()) {
			final Node nodeObject = (Node) it.next();
			// FIXME this fires too many events
			nodeMap.get(nodeObject.getId()).getCyRow().set(attrName,true);
		}
	}

	private void setBooleanEdgeAttr(final CyNetwork net, final Iterator<?> it, final String attrName) {
		if (it == null) return;

		// create an id map
		Map<String,CyEdge> edgeMap = new HashMap<String,CyEdge>();
		for ( CyEdge e : net.getEdgeList() ) 
			edgeMap.put(e.getCyRow().get("name",String.class), e);
		
		// set attr values based on ids
		while (it.hasNext()) {
			final Edge edgeObject = (Edge) it.next();
			// FIXME this fires too many events
			edgeMap.get(edgeObject.getId()).getCyRow().set(attrName,true);
		}
	}
}
