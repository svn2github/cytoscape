
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

import org.cytoscape.session.CySession;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CySession {

	private final Set<CyNetworkView> netViews;
	private final Set<CyTable> tables;
	private final Map<CyNetworkView,String> vsMap;
	private final Properties cyProps;
	private final Properties vProps;
	private final Properties dProps;
	private final Map<String, List<File>> pluginFiles;

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

		if ( b.dProps == null )
			dProps = new Properties();
		else
			dProps = b.dProps;

		if ( b.pluginFiles == null )
			pluginFiles = new HashMap<String, List<File>>(); 
		else
			pluginFiles = b.pluginFiles;
	}

	public static class Builder {

		private Set<CyNetworkView> netViews; 
		private Set<CyTable> tables;
		private Map<CyNetworkView,String> vsMap; 
		private Properties cyProps;
		private Properties vProps; 
		private Properties dProps;
		private Map<String, List<File>> pluginFiles; 

		public CySession build() { return new CySession(this); }

		public Builder networkViews(final Set<CyNetworkView> views) { 
			netViews = views; 
			return this;
		}

    	public Builder tables(final Set<CyTable> t) { 
			tables = t; 
			return this;
		}

    	public Builder viewVisualStyleMap(final  Map<CyNetworkView,String> vs) { 
			vsMap = vs; 
			return this;
		}

    	public Builder cytoscapeProperties(final Properties p) { 
			cyProps = p; 
			return this;
		}

    	public Builder vizmapProperties(final Properties p) { 
			vProps = p; 
			return this;
		}

    	public Builder desktopProperties(final Properties p) { 
			dProps = p; 
			return this;
		}

		public Builder pluginFileListMap(final Map<String, List<File>> p) { 
			this.pluginFiles = p; 
			return this;
		}
	}

    public Set<CyNetworkView> getNetworkViews() { return netViews; }

    public Set<CyTable> getTables() { return tables; }

    public Map<CyNetworkView,String> getViewVisualStyleMap() { return vsMap; }

    public Properties getCytoscapeProperties() { return cyProps; }

    public Properties getVizmapProperties() { return vProps; }

    public Properties getDesktopProperties() { return dProps; }

	public Map<String, List<File>> getPluginFileListMap() { return pluginFiles; }
}


