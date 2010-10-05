
/*
 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.session;

import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.session.Cysession;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A session is an immutable snapshot of the data contents of Cytoscape.
 * Sessions are only meant for saving and restoring the state of Cytoscape
 * and are not meant to be used interactively for anything besides 
 * writing, reading, and restoring from session files.
 * <br/>
 * Using the data returned from the various methods in a CySession object
 * should be sufficient to recreate all aspects of Cytoscape at the time
 * the session was created.
 * <br/>
 * Creating an instance of CySession is done following the builder pattern.
 * For example, the following code creates a session that only includes
 * a list of networkViews and cytoscape properties, but nothing else.  
 * <br/>
 * <pre>
 * CySession session = new CySession.Builder().networkViews(viewList).cytoscapeProperties(cyProps).build();
 * </pre>
 * <br/>
 */
public final class CySession {

	private final Set<CyNetworkView> netViews;
	private final Set<CyTable> tables;
	private final Map<CyNetworkView,String> vsMap;
	private final Properties cyProps;
	private final Properties vProps;
	private final Map<String, List<File>> pluginFiles;
	private final Bookmarks bookmarks; 
	private final Cysession cysession; 

	private static final Logger logger = LoggerFactory.getLogger(CySession.class);

	private CySession(Builder b) {
		// TODO consider making defensive copies of objects...

		if ( b.netViews == null )
			netViews = new HashSet<CyNetworkView>();
		else
			netViews = b.netViews;

		if ( b.tables == null )
			tables = new HashSet<CyTable>();
		else 
			tables = b.tables;

		if ( b.vsMap == null )
			vsMap = new HashMap<CyNetworkView,String>();
		else
			vsMap = b.vsMap;

		if ( b.cyProps == null )
			cyProps = new Properties();
		else
			cyProps = b.cyProps;

		if ( b.vProps == null )
			vProps = new Properties();
		else
			vProps = b.vProps;

		if ( b.pluginFiles == null )
			pluginFiles = new HashMap<String, List<File>>(); 
		else
			pluginFiles = b.pluginFiles;

		if ( b.bookmarks == null )
			bookmarks = new Bookmarks(); 
		else
			bookmarks = b.bookmarks;

		if ( b.cysession == null )
			cysession = new Cysession(); 
		else
			cysession = b.cysession;
	}

	public static class Builder {

		private Set<CyNetworkView> netViews; 
		private Set<CyTable> tables;
		private Map<CyNetworkView,String> vsMap; 
		private Properties cyProps;
		private Properties vProps; 
		private Map<String, List<File>> pluginFiles; 
		private Bookmarks bookmarks; 
		private Cysession cysession; 

		/**
		 * Returns a complete instance of CySession based upon the methods
		 * called on this instance of Builder.
		 * @return A fully configured instanced of CySession. 
		 */
		public CySession build() { return new CySession(this); }

		/**
		 * @param views A Set of CyNetworkView objects, presumably all networks
		 * that exist in this instance of Cytoscape.
		 * @return An instance of Builder that has at least been configured
		 * with the specified network views.
		 */
		public Builder networkViews(final Set<CyNetworkView> views) { 
			netViews = views; 
			return this;
		}

		/**
		 * @param t A Set of CyTable objects, presumably all tables
		 * that exist in this instance of Cytoscape.
		 * @return An instance of Builder that has at least been configured
		 * with the specified tables.
		 */
    	public Builder tables(final Set<CyTable> t) { 
			tables = t; 
			return this;
		}

		/**
		 * @param vs A map of CyNetworkViews to the names of the VisualStyle
		 * currently applied to that network view, for presumably all network views
		 * that exist in this instance of Cytoscape.
		 * @return An instance of Builder that has at least been configured
		 * with the specified network view visual style name map.
		 */
    	public Builder viewVisualStyleMap(final  Map<CyNetworkView,String> vs) { 
			vsMap = vs; 
			return this;
		}

		/**
		 * @param p A Properties object that contains the current Cytoscape 
		 * properties.
		 * @return An instance of Builder that has at least been configured
		 * with the specified properties.
		 */
    	public Builder cytoscapeProperties(final Properties p) { 
			cyProps = p; 
			return this;
		}

		/**
		 * @param p A Properties object that contains the current VizMap 
		 * properties for all VisualStyles in this instance of Cytoscape.
		 * @return An instance of Builder that has at least been configured
		 * with the specified properties.
		 */
    	public Builder vizmapProperties(final Properties p) { 
			vProps = p; 
			return this;
		}

		/**
		 * @param p A map of plugin names to a list of File objects that the
		 * given plugin wants stored in the session file.
		 * @return An instance of Builder that has at least been configured
		 * with the specified plugin file list map.
		 */
		public Builder pluginFileListMap(final Map<String, List<File>> p) { 
			this.pluginFiles = p; 
			return this;
		}

		/**
		 * @param b A Bookmarks object containing all bookmarks defined
		 * for this session.
		 * @return An instance of Builder that has at least been configured
		 * with the specified bookmarks. 
		 */
		public Builder bookmarks(final Bookmarks b) { 
			this.bookmarks = b; 
			return this;
		}

		/**
		 * @param s A {@link Cysession} object containing the session descriptor
		 * for this session.
		 * @return An instance of Builder that has at least been configured
		 * with the specified session descriptor. 
		 */
		public Builder cysession(final Cysession s) { 
			this.cysession = s; 
			return this;
		}
	}

	/**
	 * @return A set of all CyNetworkView objects contained in this Session. 
	 */
    public Set<CyNetworkView> getNetworkViews() { return netViews; }

	/**
	 * @return A set of all CyTable objects contained in this Session. 
	 */
    public Set<CyTable> getTables() { return tables; }

	/**
	 * @return A map of CyNetworkViews to the names of the VisualStyle
	 * applied to that network view in this session.
	 */
    public Map<CyNetworkView,String> getViewVisualStyleMap() { return vsMap; }

	/**
	 * @return A Propeties object containing all Cytoscape properties 
	 * defined for this session. 
	 */
    public Properties getCytoscapeProperties() { return cyProps; }

	/**
	 * @return A Propeties object containing all VisualStyles defined
	 * for this session.
	 */
    public Properties getVizmapProperties() { return vProps; }

	/**
	 * @return A {@link Bookmarks} object containing all bookmarks for this session.
	 */
    public Bookmarks getBookmarks() { return bookmarks; }

	/**
	 * @return A {@link Cysession} object containing a description of this session. 
	 */
    public Cysession getCysession() { return cysession; }

	/**
	 * @return A map of plugin names to lists of File objects that are stored
	 * as part of the session for the specified plugin.
	 */
	public Map<String, List<File>> getPluginFileListMap() { return pluginFiles; }
}


