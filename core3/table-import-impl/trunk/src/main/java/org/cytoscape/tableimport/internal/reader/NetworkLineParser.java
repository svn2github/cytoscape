
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

package org.cytoscape.tableimport.internal.reader;

//import cytoscape.Cytoscape;

//import cytoscape.data.CyAttributes;
//import cytoscape.data.Semantics;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import java.util.ArrayList;
import java.util.List;


/**
 * Parse one line for network text table
 *
 * @author kono
 *
 */
public class NetworkLineParser {
	private final NetworkTableMappingParameters nmp;
	private final List<Integer> nodeList;
	private final List<Integer> edgeList;

	/**
	 * Creates a new NetworkLineParser object.
	 *
	 * @param nodeList  DOCUMENT ME!
	 * @param edgeList  DOCUMENT ME!
	 * @param nmp  DOCUMENT ME!
	 */
	public NetworkLineParser(List<Integer> nodeList, List<Integer> edgeList,
	                         final NetworkTableMappingParameters nmp) {
		this.nmp = nmp;
		this.nodeList = nodeList;
		this.edgeList = edgeList;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param parts DOCUMENT ME!
	 */
	public void parseEntry(String[] parts) {
		final CyEdge edge = addNodeAndEdge(parts);

		if (edge != null)
			addEdgeAttributes(edge, parts);
	}

	
	private CyEdge addNodeAndEdge(final String[] parts) {
		
		final CyNode source = createNode(parts, nmp.getSourceIndex());
		final CyNode target = createNode(parts, nmp.getTargetIndex());		
		
		// Single column nodes list.  Just add nodes.
		if(source == null || target == null)
			return null;

		final String interaction;

		if ((nmp.getInteractionIndex() == -1) || (nmp.getInteractionIndex() > (parts.length - 1))
		    || (parts[nmp.getInteractionIndex()] == null)) {
			interaction = nmp.getDefaultInteraction();
		} else
			interaction = parts[nmp.getInteractionIndex()];

		final CyEdge edge = null;
		//edge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, interaction, true, true);
		//edgeList.add(edge.getRootGraphIndex());
		
		return edge;
	}
	
	
	private CyNode createNode(final String[] parts, final Integer nodeIndex) {
		final CyNode node = null;
		if (nodeIndex.equals(-1) == false && (nodeIndex <= (parts.length - 1)) && (parts[nodeIndex] != null)) {
			//node = Cytoscape.getCyNode(parts[nodeIndex], true);
			//nodeList.add(node.getRootGraphIndex());
		}// else
		//	node = null;
		
		return node;
	}

	private void addEdgeAttributes(final CyEdge edge, final String[] parts) {
		for (int i = 0; i < parts.length; i++) {
			if ((i != nmp.getSourceIndex()) && (i != nmp.getTargetIndex())
			    && (i != nmp.getInteractionIndex()) && parts[i] != null ) {
				if ((nmp.getImportFlag().length > i) && (nmp.getImportFlag()[i] == true)) {
					//mapAttribute(edge.getIdentifier(), parts[i].trim(), i);
				}
			}
		}
	}

	/**
	 * Based on the attribute types, map the entry to CyAttributes.<br>
	 *
	 * @param key
	 * @param entry
	 * @param index
	 */
	private void mapAttribute(final String key, final String entry, final int index) {
		
		/*
		
		//Byte type = nmp.getAttributeTypes()[index];
		Class<?> type = nmp.getAttributeTypes()[index];
		
		if (entry == null || entry.length() == 0) {
			return;
		}

		switch (type) {
			case Boolean.class: //CyAttributes.TYPE_BOOLEAN:
				nmp.getAttributes()
				   .setAttribute(key, nmp.getAttributeNames()[index], new Boolean(entry));

				break;

			case Integer.class: //CyAttributes.TYPE_INTEGER:
				nmp.getAttributes()
				   .setAttribute(key, nmp.getAttributeNames()[index], new Integer(entry));

				break;

			case Double.class: //CyAttributes.TYPE_FLOATING:
				nmp.getAttributes()
				   .setAttribute(key, nmp.getAttributeNames()[index], new Double(entry));

				break;

			case String.class: //CyAttributes.TYPE_STRING:
				nmp.getAttributes().setAttribute(key, nmp.getAttributeNames()[index], entry);

				break;

			case List.class: //CyAttributes.TYPE_SIMPLE_LIST:

				/
				 * In case of list, not overwrite the attribute. Get the existing
				 * list, and add it to the list.
				 /
				List curList = nmp.getAttributes()
				                  .getListAttribute(key, nmp.getAttributeNames()[index]);

				if (curList == null) {
					curList = new ArrayList();
				}

				curList.addAll(buildList(entry));

				nmp.getAttributes().setListAttribute(key, nmp.getAttributeNames()[index], curList);

				break;

			default:
				nmp.getAttributes().setAttribute(key, nmp.getAttributeNames()[index], entry);
		}
		*/
	}

	/**
	 * If an entry is a list, split the string and create new List Attribute.
	 *
	 * @return
	 */
	private List buildList(final String entry) {
		if (entry == null) {
			return null;
		}

		final List<String> listAttr = new ArrayList<String>();

		final String[] parts = (entry.replace("\"", "")).split(nmp.getListDelimiter());

		for (String listItem : parts) {
			listAttr.add(listItem.trim());
		}

		return listAttr;
	}
}
