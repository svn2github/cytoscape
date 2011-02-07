/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.model.internal; 


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;


/**
 * An interface describing a factory used for managing 
 * {@link CyTable} objects.  This class will be
 * provided as a service through Spring/OSGi.
 */
public class CyTableManagerImpl implements CyTableManager {
	private final Map<Class<?>, Map<CyNetwork, Map<String,CyTable>>> networkTableMap;
	private final Map<Long,CyTable> tables;

	public CyTableManagerImpl() {
		networkTableMap = new HashMap<Class<?>, Map<CyNetwork, Map<String,CyTable>>>();	
		networkTableMap.put( CyNetwork.class, new HashMap<CyNetwork, Map<String,CyTable>>() );
		networkTableMap.put( CyNode.class, new HashMap<CyNetwork, Map<String,CyTable>>() );
		networkTableMap.put( CyEdge.class, new HashMap<CyNetwork, Map<String,CyTable>>() );

		tables = new HashMap<Long,CyTable>();
	}
	
	@Override
	public synchronized void reset() {
		networkTableMap.clear();
		tables.clear();
	}

	@Override
	public synchronized Map<String, CyTable> getTableMap(final Class<?> graphObjectType,
							     final CyNetwork network)
	{
		if ( network == null || graphObjectType == null )
			return null;

		Map<CyNetwork, Map<String,CyTable>> tmap = networkTableMap.get(graphObjectType);

		if ( tmap == null )
			throw new IllegalArgumentException("no data tables of type: " + graphObjectType + " exist");

		return networkTableMap.get(graphObjectType).get(network);
	}

	public synchronized void setTableMap(final Class<?> graphObjectType, final CyNetwork network,
					     final Map<String,CyTable> tm)
	{
		if ( network == null )
			throw new NullPointerException("CyNetwork is null");
		if ( graphObjectType == null )
			throw new NullPointerException("Type is null");

		if ( !networkTableMap.containsKey(graphObjectType) )
			networkTableMap.put(graphObjectType, new HashMap<CyNetwork, Map<String,CyTable>>());

		Map<CyNetwork, Map<String,CyTable>> tmap = networkTableMap.get(graphObjectType);

		if ( tm == null )
			tmap.remove(network);
		else
			tmap.put(network,tm);
	}

	public synchronized void addTable(final CyTable t) {
		if (t == null)
			throw new NullPointerException("added table is null");
		tables.put( t.getSUID(), t );
	}

	@Override
	public synchronized Set<CyTable> getAllTables(final boolean includePrivate) {
		Set<CyTable> res = new HashSet<CyTable>();
		
		for ( Long key : tables.keySet() ) {	
			if ( includePrivate )
				res.add(tables.get(key));
			else if ( tables.get(key).isPublic() )
				res.add(tables.get(key));
		}
		return res;
	}

	@Override
	public synchronized CyTable getTable(final long suid) {
		return tables.get(suid);
	}

	@Override
	public synchronized void deleteTable(final long suid) {
		final CyTableImpl table = (CyTableImpl)tables.get(suid);
		if (table == null)
			return;

		if (table.isImmutable())
			throw new IllegalArgumentException("can't delete an immutable table!");

		if (!table.holdsNoVirtColumnReferences())
			throw new IllegalArgumentException("can't delete a table that still has virtual column references!");

		table.removeAllVirtColumns();
		tables.remove(suid);
	}
}
