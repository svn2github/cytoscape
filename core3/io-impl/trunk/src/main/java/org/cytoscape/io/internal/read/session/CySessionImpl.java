
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
package org.cytoscape.io.internal.read.session;


import org.cytoscape.session.CySession;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;


class CySessionImpl implements CySession {

	CySessionImpl() {
	}

	private Set<CyNetworkView> netViews;
    public Set<CyNetworkView> getNetworkViews() {
		return netViews;
	}
	void setNetworkViews(Set<CyNetworkView> views) {
		System.out.println("SESSION setting network views");
//		if ( views == null ) {
//		//	System.out.println(" NULL!!!");
//			return;
//		}
//		for ( CyNetworkView tt : views )
//			System.out.println("   " + tt);
		netViews = views;
	}


    private Set<CyTable> tables;
    public Set<CyTable> getTables() {
		return tables;
	}
    void setTables( Set<CyTable> t) {
		System.out.println("SESSION setting tables");
//		if ( t == null ) {
//			//System.out.println(" NULL!!!");
//			return;
//		}
//		for ( CyTable tt : t )
			//System.out.println("   " + tt);
		tables = t;
	}


    private Map<CyNetworkView,String> vsMap;
    public Map<CyNetworkView,String> getViewVisualStyleMap() {
		return vsMap;
	}
    void setViewVisualStyleMap( Map<CyNetworkView,String> vs) {
		System.out.println("SESSION setting view visual styles");
//		if ( vs == null ) {
//			System.out.println(" NULL!!!");
//			return;
//		}
//		for ( Map.Entry<CyNetworkView,String> e : vs.entrySet() )
//			System.out.println("   " + e.getKey() + " => " + e.getValue());
		vsMap = vs;
	}


	private Properties cyProps;
    public Properties getCytoscapeProperties() {
		return cyProps;
	}
    void setCytoscapeProperties(Properties p) {
//		System.out.println("SESSION setting cytoscape properties");
//		if ( p == null ) {
//			System.out.println(" NULL!!!");
//			return;
//		}
//		for ( Map.Entry<Object,Object> e : p.entrySet() )
//			System.out.println("   " + e.getKey() + " => " + e.getValue());
		cyProps = p;
	}

	private Properties vProps;
    public Properties getVizmapProperties() {
		return vProps;
	}
    void setVizmapProperties(Properties p) {
		System.out.println("SESSION setting vizmap properties");
//		if ( p == null ) {
//			System.out.println(" NULL!!!");
//			return;
//		}
//		for ( Map.Entry<Object,Object> e : p.entrySet() )
//			System.out.println("   " + e.getKey() + " => " + e.getValue());
		vProps = p;
	}

	private Properties dProps;
    public Properties getDesktopProperties() {
		return dProps;
	}
    void setDesktopProperties(Properties p) {
		System.out.println("SESSION setting desktop properties");
//		if ( p == null ) {
//			System.out.println(" NULL!!!");
//			return;
//		}
//		for ( Map.Entry<Object,Object> e : p.entrySet() )
//			System.out.println("   " + e.getKey() + " => " + e.getValue());
		dProps = p;
	}
}


