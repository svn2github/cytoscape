
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

package org.cytoscape.io.internal.reader.table;

import cytoscape.Cytoscape;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

import java.util.ArrayList;
import java.util.List;


/**
 * Parse one line for network text table
 *
 * @author kono
 *
 */
public class NetworkLineParser {
	
	private final CyNetworkFactory networkFactory;
	
	private final NetworkTableMappingParameters nmp;
	private final List<Integer> nodeList;
	private final List<Integer> edgeList;
	private final boolean directed_edges;

	/**
	 * Creates a new NetworkLineParser object, which will create directed edges
	 *
	 * @param nodeList  DOCUMENT ME!
	 * @param edgeList  DOCUMENT ME!
	 * @param nmp  DOCUMENT ME!
	 */
	public NetworkLineParser(List<Integer> nodeList, List<Integer> edgeList,
	                         final NetworkTableMappingParameters nmp, CyNetworkFactory factory) {
		this(nodeList, edgeList, nmp, true, factory);
	}
	/**
	 * Creates a new NetworkLineParser object, which will create edges of the given directionality
	 *
	 * @param nodeList  DOCUMENT ME!
	 * @param edgeList  DOCUMENT ME!
	 * @param nmp  DOCUMENT ME!
	 * @param directed_edges if true, create directed edges, otherwise create undirected ones
	 */	
	public NetworkLineParser(List<Integer> nodeList, List<Integer> edgeList,
            final NetworkTableMappingParameters nmp, final boolean directed_edges, CyNetworkFactory factory) {
		this.nmp = nmp;
		this.nodeList = nodeList;
		this.edgeList = edgeList;
		this.directed_edges = directed_edges;
		
		this.networkFactory = factory;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param parts DOCUMENT ME!
	 */
	public void parseEntry(String[] parts) {
		final CyEdge edge = addNodeAndEdge(parts);

		if (edge != null) {
			addEdgeAttributes(edge, parts);
		}
	}

	private CyEdge addNodeAndEdge(final String[] parts) {
		final CyNode source;
		
		if (nmp.getSourceIndex().equals(-1) == false && (nmp.getSourceIndex() <= (parts.length - 1)) && (parts[nmp.getSourceIndex()] != null)) {
			//TODO: needs a network. Use factory?
			source = Cytoscape.getCyNode(parts[nmp.getSourceIndex()].trim(), true);
			nodeList.add(source.getIndex());
		} else {
			source = null;
		}
		
		final CyNode target;

		if (nmp.getTargetIndex().equals(-1) == false && (nmp.getTargetIndex() <= (parts.length - 1)) && (parts[nmp.getTargetIndex()] != null)) {
			target = Cytoscape.getCyNode(parts[nmp.getTargetIndex()].trim(), true);
			nodeList.add(target.getIndex());
		} else {
			target = null;
		}
		
		// Single column nodes list.  Just add nodes.
		if(source == null || target == null) {
			// do not create edge.
			return null;
		}

		final String interaction;

		if ((nmp.getInteractionIndex() == -1) || (nmp.getInteractionIndex() > (parts.length - 1))
		    || (parts[nmp.getInteractionIndex()] == null)) {
			interaction = nmp.getDefaultInteraction();
		} else {
			interaction = parts[nmp.getInteractionIndex()].trim();
		}

		final CyEdge edge;
		edge = Cytoscape.getCyEdge(source, target, "pp", interaction, true, directed_edges);
		edgeList.add(edge.getIndex());
		
		return edge;
	}

	private void addEdgeAttributes(final CyEdge edge, final String[] parts) {
		for (int i = 0; i < parts.length; i++) {
			if ((i != nmp.getSourceIndex()) && (i != nmp.getTargetIndex())
			    && (i != nmp.getInteractionIndex()) && parts[i] != null ) {
				if ((nmp.getImportFlag().length > i) && (nmp.getImportFlag()[i] == true)) {
					mapAttribute(edge.getSUID(), parts[i].trim(), i);
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
	private void mapAttribute(final long key, final String entry, final int index) {
		Class<?> type = nmp.getAttributeTypes()[index];

		if (type == Boolean.class || type == boolean.class){
			nmp.getAttributes()
			   .setAttribute(key, nmp.getAttributeNames()[index], new Boolean(entry));
		} else if ( type == Integer.class || type == int.class){
			nmp.getAttributes()
			   .setAttribute(key, nmp.getAttributeNames()[index], Integer.valueOf(entry));
		} else if (type == Double.class || type == double.class) {
			nmp.getAttributes()
			   .setAttribute(key, nmp.getAttributeNames()[index], new Double(entry));
		} else if (type == String.class) {
			nmp.getAttributes().setAttribute(key, nmp.getAttributeNames()[index], entry);
		} else if (type == ArrayList.class){
			/*
			 * In case of list, not overwrite the attribute. Get the existing
			 * list, and add it to the list.
			 */
			List curList = nmp.getAttributes()
			                  .getListAttribute(key, nmp.getAttributeNames()[index]);

			if (curList == null) {
				curList = new ArrayList();
			}

			curList.addAll(buildList(entry));

			nmp.getAttributes().setListAttribute(key, nmp.getAttributeNames()[index], curList);
		} else {
			nmp.getAttributes().setAttribute(key, nmp.getAttributeNames()[index], entry);
		}
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
