
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

package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader.ObjectType;

import giny.model.Edge;
import giny.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 */
public class OntologyAndAnnotationLineParser {
	private final AttributeAndOntologyMappingParameters mapping;

	/**
	 * Creates a new OntologyAndAnnotationLineParser object.
	 *
	 * @param mapping  DOCUMENT ME!
	 */
	public OntologyAndAnnotationLineParser(AttributeAndOntologyMappingParameters mapping) {
		this.mapping = mapping;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param parts DOCUMENT ME!
	 */
	public void parseEntry(String[] parts) {
		/*
		 * Split the line and extract values
		 */
		final String primaryKey = parts[mapping.getKeyIndex()].trim();

		/*
		 * Set aliases In this case, "aliases" means alias entries in the TEXT
		 * TABLE, not the ones returned by Cytoscape.getNodeAliases()
		 *
		 * The variable aliasSet has non-redundant set of object names.
		 */
		final Set<String> aliasSet = new TreeSet<String>();

		if (mapping.getAliasIndexList().size() != 0) {
			/*
			 * Alias column exists. Extract those keys.
			 */
			String aliasCell = null;

			for (int aliasIndex : mapping.getAliasIndexList()) {
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
		if (mapping.getMappingAttribute().equals(mapping.ID)) {
			transfer2cyattributes(primaryKey, aliasSet, parts);
		} else {
			/*
			 * Case 2: use an attribute as the key.
			 */
			List<String> objectIDs = null;

			for (String id : aliasSet) {
				if (mapping.getAttributeToIDMap().containsKey(id)) {
					objectIDs = mapping.toID(id);

					for (String objectID : objectIDs) {
						mapping.getAlias().add(objectID, new ArrayList<String>(aliasSet));
					}

					break;
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
		switch (mapping.getObjectType()) {
			case NODE:

				Node node = Cytoscape.getCyNode(primaryKey);

				if (node == null) {
					for (String alias : aliasSet) {
						node = Cytoscape.getCyNode(alias);

						if (node != null) {
							altKey = alias;

							break;
						}
					}

					if (node == null) {
						return;
					}
				} else {
					break;
				}

				break;

			case EDGE:

				Edge edge = Cytoscape.getRootGraph().getEdge(primaryKey);

				if (edge == null) {
					for (String alias : aliasSet) {
						edge = Cytoscape.getRootGraph().getEdge(alias);

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
				if (mapping.getnetworkTitleMap().containsKey(primaryKey)) {
					targetNetworkID = mapping.getnetworkTitleMap().get(primaryKey);

					break;
				}

				if (targetNetworkID == null) {
					for (String alias : aliasSet) {
						if (mapping.getnetworkTitleMap().containsKey(alias)) {
							targetNetworkID = mapping.getnetworkTitleMap().get(alias);

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
			if ((i != mapping.getKeyIndex()) && !mapping.getAliasIndexList().contains(i)
			    && mapping.getImportFlag()[i]) {
				if (parts[i] == null) {
					// Do nothing
				} else if (mapping.getObjectType() == ObjectType.NETWORK) {
					mapAttribute(targetNetworkID, parts[i].trim(), i);
				}
				/*
				 * Frist, check the node exists or not with the primary key
				 */
				else if (altKey == null) {
					mapAttribute(primaryKey, parts[i].trim(), i);
				} else {
					mapAttribute(altKey, parts[i].trim(), i);
				}
			}
		}

		/*
		 * Finally, add aliases
		 */
		if (altKey == null) {
			mapping.getAlias().add(primaryKey, new ArrayList<String>(aliasSet));
		} else {
			mapping.getAlias().add(altKey, new ArrayList<String>(aliasSet));
		}
	}

	private void mapAttribute(final String key, final String entry, final int index) {
		final Byte type;

		if (index == mapping.getOntologyIndex()) {
			type = CyAttributes.TYPE_SIMPLE_LIST;
		} else {
			type = mapping.getAttributeTypes()[index];
		}

		switch (type) {
			case CyAttributes.TYPE_BOOLEAN:
				mapping.getAttributes()
				       .setAttribute(key, mapping.getAttributeNames()[index], new Boolean(entry));

				break;

			case CyAttributes.TYPE_INTEGER:
				mapping.getAttributes()
				       .setAttribute(key, mapping.getAttributeNames()[index], new Integer(entry));

				break;

			case CyAttributes.TYPE_FLOATING:
				mapping.getAttributes()
				       .setAttribute(key, mapping.getAttributeNames()[index], new Double(entry));

				break;

			case CyAttributes.TYPE_STRING:
				mapping.getAttributes().setAttribute(key, mapping.getAttributeNames()[index], entry);

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
				final Byte[] listTypes = mapping.getListAttributeTypes();
				final Byte listType;

				if (index == mapping.getOntologyIndex()) {
					listType = CyAttributes.TYPE_STRING;
				} else if (listTypes != null) {
					listType = listTypes[index];
				} else {
					listType = CyAttributes.TYPE_STRING;
				}

				List curList = mapping.getAttributes()
				                      .getListAttribute(key, mapping.getAttributeNames()[index]);

				if (curList == null) {
					curList = new ArrayList();
				}

				curList.addAll(buildList(entry, listType));
				mapping.getAttributes()
				       .setListAttribute(key, mapping.getAttributeNames()[index], curList);

				break;

			default:
				mapping.getAttributes().setAttribute(key, mapping.getAttributeNames()[index], entry);
		}
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

		final String[] parts = (entry.replace("\"", "")).split(mapping.getListDelimiter());

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
