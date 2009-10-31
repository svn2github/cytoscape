/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.ontology.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.GraphObject;
import org.cytoscape.ontology.Alias;

/**
 * 
 * Simpler version of Thesaurus.<br>
 * 
 * <p>
 * Purpose of this class is providing simple aliasing without biological
 * context. No species, commonname, nor canonical name are available. Just
 * maintains keys (IDs) and their aliases in CyAttributes.<br>
 * This will be used for importing attribute data.
 * </p>
 * 
 * <p>
 * This class has no redundant data structure: everything will be stored in
 * CyAttributes, and uses immutable special attributes called Alias
 * </p>
 * 
 * @since Cytoscape 2.4
 * @version 0.6
 * 
 * @author kono
 * 
 */
public class AliasImpl implements Alias {

	// Name of Alias attributes
	public static final String ALIAS = "cytoscape.alias.list";

	private final GraphObject obj;
	private final String key;

	public AliasImpl(GraphObject obj) {
		this.obj = obj;
		this.key = this.obj.attrs().get("name", String.class);
		if (obj.attrs().getDataTable().getUniqueColumns().contains(ALIAS) == false) {
			obj.attrs().getDataTable().createColumn(ALIAS, List.class, false);
		}
	}

	/**
	 * Add new alias for an object.
	 * 
	 * @param key
	 *            ID of the object.
	 * @param alias
	 *            New alias to be added.
	 */
	@Override
	public void add(String alias) {
		List<String> aliasList = obj.attrs().get(ALIAS, List.class);

		if (aliasList == null) {
			aliasList = new ArrayList<String>();
			aliasList.add(alias);
		} else if (aliasList.contains(alias) == false) {
			aliasList.add(alias);
		}

		obj.attrs().set(ALIAS, aliasList);
	}

	/**
	 * Add list of aliases to the existing alias lists.<br>
	 * 
	 * @param key
	 * @param aliaseList
	 */
	@Override
	public void add(Set<String> aliases) {
		List<String> aliasList = obj.attrs().get(ALIAS, List.class);

		if (aliasList == null) {
			aliasList = new ArrayList<String>(aliases);
		} else {
			Set<String> aliasSet = new HashSet<String>(aliasList);
			aliasSet.addAll(aliases);
			aliasList = new ArrayList<String>(aliasSet);
		}

		obj.attrs().set(ALIAS, aliasList);
	}

	/**
	 * Remove an alias.<br>
	 * 
	 * @param key
	 *            ID of the object.
	 * @param alias
	 *            Alias to be removed.
	 */
	public void remove(String alias) {
		List<String> curAliasList = obj.attrs().get(ALIAS, List.class);

		if (curAliasList != null)
			obj.attrs().get(ALIAS, List.class).remove(alias);
	}

	/**
	 * 
	 * Returns true if the object with the alias wxists in the memory
	 * (rootGraph).
	 * 
	 * This is an O(n) operation, which is expensive!
	 * 
	 * @param key
	 * @param alias
	 * @return Key for this alias. If it does not exist, return null.
	 */
	public String getKey() {
		return this.key;
	}

	@Override
	public Set<String> getAliasSet() {
		List<String> aliasSet = obj.attrs().get(ALIAS, List.class);
		if (aliasSet != null) {
			return new HashSet<String>(aliasSet);
		} else
			return null;
	}
}
