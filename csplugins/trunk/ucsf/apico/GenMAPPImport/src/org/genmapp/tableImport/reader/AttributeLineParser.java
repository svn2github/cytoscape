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
package org.genmapp.tableImport.reader;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.giny.CytoscapeRootGraph;


import giny.model.Edge;
import giny.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.genmapp.tableImport.reader.TextTableReader.ObjectType;


/**
 * Take a line of data, analyze it, and map to CyAttributes.
 *
 * @since Cytoscape 2.4
 * @version 0.8
 * @author Keiichiro Ono
 *
 */
public class AttributeLineParser {
	private AttributeMappingParameters amp;
	private Map<String, Object> invalid = new HashMap<String, Object>();

	/**
	 * Creates a new AttributeLineParser object.
	 *
	 * @param amp  DOCUMENT ME!
	 */
	public AttributeLineParser(AttributeMappingParameters amp) {
		this.amp = amp;
	}

	/**
	 *  Import everything regardless associated nodes/edges exist or not.
	 *
	 * @param parts entries in a line.
	 */
	public void parseAll(String[] parts) {
		// Get key
		final String primaryKey = parts[amp.getKeyIndex()].trim();
		final int partsLen = parts.length;

		for (int i = 0; i < partsLen; i++) {
			if ((i != amp.getKeyIndex()) && !amp.getAliasIndexList().contains(i)
			    && amp.getImportFlag()[i]) {
				if (parts[i] == null) {
					continue;
				} else if (amp.getObjectType() == ObjectType.NETWORK) {
					//mapAttribute(targetNetworkID, parts[i].trim(), i);
				} else {
					mapAttribute(primaryKey, parts[i].trim(), i);
				}
			}
		}
	}

	/**
	 * Takes an array of entries, which is one line of text file, and maps them
	 * to CyAttributes.
	 *
	 * @param parts
	 */
	public void parseEntry(String[] parts) {
		/*
		 * Split the line and extract values
		 */
		final String primaryKey = parts[amp.getKeyIndex()].trim();

		/*
		 * Set aliases In this case, "aliases" means alias entries in the TEXT
		 * TABLE, not the ones returned by Cytoscape.getNodeAliases()
		 *
		 * The variable aliasSet has non-redundant set of object names.
		 */
		final Set<String> aliasSet = new TreeSet<String>();

		if (amp.getAliasIndexList().size() != 0) {
			/*
			 * Alias column exists. Extract those keys.
			 */
			String aliasCell = null;

			for (int aliasIndex : amp.getAliasIndexList()) {
				if (parts.length > aliasIndex) {
					aliasCell = parts[aliasIndex];

					if ((aliasCell != null) && (aliasCell.trim().length() != 0)) {
						aliasSet.addAll(buildList(aliasCell, CyAttributes.TYPE_STRING));
					}
				}
			}
		}
		aliasSet.add(primaryKey);

		/*
		 * Case 1: use node ID as the key
		 */
		if (amp.getMappingAttribute().equals(amp.ID)) {
			transfer2cyattributes(primaryKey, aliasSet, parts);
		} else {
			/*
			 * Case 2: use an attribute as the key.
			 */
			List<String> objectIDs = null;

			for (String id : aliasSet) {
				// Normal Mapping.  Case sensitive.
				
				if (amp.getAttributeToIDMap().containsKey(id)) {
					objectIDs = amp.toID(id);

					for (String objectID : objectIDs) {
						amp.getAlias().add(objectID, new ArrayList<String>(aliasSet));
					}

					break;
				} else if (amp.getCaseSensitive() == false) {
					
					Set<String> keySet = amp.getAttributeToIDMap().keySet();

					String newKey = null;

					for (String key : keySet) {
						if (key.equalsIgnoreCase(id)) {
							newKey = key;
							
							break;
						}
					}

					if (newKey != null) {
						objectIDs = amp.toID(newKey);

						for (String objectID : objectIDs) {
							amp.getAlias().add(objectID, new ArrayList<String>(aliasSet));
						}

						break;
					}
				}
			}

			if (objectIDs != null) {
				for (String key : objectIDs) {
					transfer2cyattributes(key, aliasSet, parts);
				}
			}
		}
	}

	private void transfer2cyattributes(String primaryKey, Set<String> aliasSet, String[] parts) {
		String altKey = null;
		String targetNetworkID = null;

		/*
		 * Search the key
		 */
		switch (amp.getObjectType()) {
			case NODE:

				Node node = Cytoscape.getCyNode(primaryKey);

				if ((amp.getCaseSensitive() == false) && (node == null)) {
					// This is extremely slow, but we have no choice.
					final CytoscapeRootGraph rg = Cytoscape.getRootGraph();
					int[] nodes = Cytoscape.getRootGraph().getNodeIndicesArray();
					int nodeCount = nodes.length;

					for (int i = 0; i < nodeCount; i++) {
						if (rg.getNode(nodes[i]).getIdentifier().equalsIgnoreCase(primaryKey)) {
							node = rg.getNode(nodes[i]);
							primaryKey = node.getIdentifier();

							break;
						}
					}
				}

				if (node == null) {
					for (String alias : aliasSet) {
						node = Cytoscape.getCyNode(alias);

						if ((amp.getCaseSensitive() == false) && (node == null)) {
							// This is extremely slow, but we have no choice.
							final CytoscapeRootGraph rg = Cytoscape.getRootGraph();
							int[] nodes = Cytoscape.getRootGraph().getNodeIndicesArray();
							int nodeCount = nodes.length;

							for (int i = 0; i < nodeCount; i++) {
								if (rg.getNode(nodes[i]).getIdentifier().equalsIgnoreCase(alias)) {
									node = rg.getNode(nodes[i]);
									alias = node.getIdentifier();

									break;
								}
							}
						}

						if (node != null) {
							altKey = alias;

							break;
						}
					}

					if (node == null) {
						return;
					}
				}

				break;

			case EDGE:

				Edge edge = Cytoscape.getRootGraph().getEdge(primaryKey);

				if ((amp.getCaseSensitive() == false) && (edge == null)) {
					// This is extremely slow, but we have no choice.
					final CytoscapeRootGraph rg = Cytoscape.getRootGraph();
					int[] edges = Cytoscape.getRootGraph().getEdgeIndicesArray();
					int edgeCount = edges.length;

					for (int i = 0; i < edgeCount; i++) {
						if (rg.getEdge(edges[i]).getIdentifier().equalsIgnoreCase(primaryKey)) {
							edge = rg.getEdge(edges[i]);
							primaryKey = edge.getIdentifier();

							break;
						}
					}
				}

				if (edge == null) {
					for (String alias : aliasSet) {
						edge = Cytoscape.getRootGraph().getEdge(alias);

						if ((amp.getCaseSensitive() == false) && (edge == null)) {
							// This is extremely slow, but we have no choice.
							final CytoscapeRootGraph rg = Cytoscape.getRootGraph();
							int[] edges = Cytoscape.getRootGraph().getEdgeIndicesArray();
							int edgeCount = edges.length;

							for (int i = 0; i < edgeCount; i++) {
								if (rg.getEdge(edges[i]).getIdentifier().equalsIgnoreCase(alias)) {
									edge = rg.getEdge(edges[i]);
									alias = edge.getIdentifier();

									break;
								}
							}
						}

						if (edge != null) {
							altKey = alias;

							break;
						}
					}

					if (edge == null) {
						return;
					}
				} else {
					break;
				}

				break;

			case NETWORK:

				/*
				 * This is a special case: Since network IDs are only integers and
				 * not always the same, we need to use title instead of ID.
				 */
				if (amp.getnetworkTitleMap().containsKey(primaryKey)) {
					targetNetworkID = amp.getnetworkTitleMap().get(primaryKey);

					break;
				}

				if (targetNetworkID == null) {
					for (String alias : aliasSet) {
						if (amp.getnetworkTitleMap().containsKey(alias)) {
							targetNetworkID = amp.getnetworkTitleMap().get(alias);

							break;
						}
					}
				}

				if (targetNetworkID == null) {
					/*
					 * Network not found: just ignore this line.
					 */
					return;
				}

				break;

			default:
		}

		/*
		 * Now, transfer entries into CyAttributes.
		 */
		for (int i = 0; i < parts.length; i++) {
			if ((i != amp.getKeyIndex()) && !amp.getAliasIndexList().contains(i)
			    && amp.getImportFlag()[i]) {
				if (parts[i] == null) {
					// Do nothing
				} else if (amp.getObjectType() == ObjectType.NETWORK) {
					mapAttribute(targetNetworkID, parts[i].trim(), i);
				}
				/*
				 * First, check the node exists or not with the primary key
				 */
				else if (altKey == null) {
					mapAttribute(primaryKey, parts[i].trim(), i);
				} else {
					mapAttribute(altKey, parts[i].trim(), i);
				}
			}
		}

		/*
		 * Finally, add aliases and primary key.
		 */
		amp.getAlias().add(primaryKey, new ArrayList<String>(aliasSet));
		amp.getAttributes()
		       .setAttribute(primaryKey, amp.getAttributeNames()[amp.getKeyIndex()],
		                     parts[amp.getKeyIndex()]);

		/*
		 * Add primary key as an attribute
		 */
	}

	/**
	 * Based on the attribute types, map the entry to CyAttributes.<br>
	 *
	 * @param key
	 * @param entry
	 * @param index
	 */
	private void mapAttribute(final String key, final String entry, final int index) {
		final Byte type = amp.getAttributeTypes()[index];

		//		System.out.println("Index = " + mapping.getAttributeNames()[index] + ", " + key + " = "
		//		                   + entry);
		switch (type) {
			case CyAttributes.TYPE_BOOLEAN:

				Boolean newBool;

				try {
					newBool = new Boolean(entry);
					amp.getAttributes()
					       .setAttribute(key, amp.getAttributeNames()[index], newBool);
				} catch (Exception e) {
					invalid.put(key, entry);
				}

				break;

			case CyAttributes.TYPE_INTEGER:

				Integer newInt;

				try {
					newInt = new Integer(entry);
					amp.getAttributes()
					       .setAttribute(key, amp.getAttributeNames()[index], newInt);
				} catch (Exception e) {
					invalid.put(key, entry);
				}

				break;

			case CyAttributes.TYPE_FLOATING:

				Double newDouble;

				try {
					newDouble = new Double(entry);
					amp.getAttributes()
					       .setAttribute(key, amp.getAttributeNames()[index], newDouble);
				} catch (Exception e) {
					invalid.put(key, entry);
				}

				break;

			case CyAttributes.TYPE_STRING:
				try {
					amp.getAttributes().setAttribute(key, amp.getAttributeNames()[index], entry);
				} catch (Exception e) {
					invalid.put(key, entry);
				}

				break;

			case CyAttributes.TYPE_SIMPLE_LIST:

				/*
				 * In case of list, not overwrite the attribute. Get the existing
				 * list, and add it to the list.
				 *
				 * Since list has data types for their data types, so we need to
				 * extract it first.
				 *
				 */
				final Byte[] listTypes = amp.getListAttributeTypes();
				final Byte listType;

				if (listTypes != null) {
					listType = listTypes[index];
				} else {
					listType = CyAttributes.TYPE_STRING;
				}

				List curList = amp.getAttributes()
				                      .getListAttribute(key, amp.getAttributeNames()[index]);

				if (curList == null) {
					curList = new ArrayList();
				}

				curList.addAll(buildList(entry, listType));
				try {
					amp.getAttributes()
					       .setListAttribute(key, amp.getAttributeNames()[index], curList);
				} catch (Exception e) {
					invalid.put(key, entry);
				}

				break;

			default:
				try {
					amp.getAttributes().setAttribute(key, amp.getAttributeNames()[index], entry);
				} catch (Exception e) {
					invalid.put(key, entry);
				}
		}
	}

	protected Map getInvalidMap() {
		return invalid;
	}

	/**
	 * If an entry is a list, split the string and create new List Attribute.
	 *
	 * @return
	 */
	private List buildList(final String entry, final Byte dataType) {
		if (entry == null) {
			return null;
		}

		final String[] parts = (entry.replace("\"", "")).split(amp.getListDelimiter());

		final List listAttr = new ArrayList();

		for (String listItem : parts) {
			switch (dataType) {
				case CyAttributes.TYPE_BOOLEAN:
					listAttr.add(Boolean.parseBoolean(listItem.trim()));

					break;

				case CyAttributes.TYPE_INTEGER:
					listAttr.add(Integer.parseInt(listItem.trim()));

					break;

				case CyAttributes.TYPE_FLOATING:
					listAttr.add(Double.parseDouble(listItem.trim()));

					break;

				case CyAttributes.TYPE_STRING:
					listAttr.add(listItem.trim());

					break;

				default:
					break;
			}
		}

		return listAttr;
	}
}
