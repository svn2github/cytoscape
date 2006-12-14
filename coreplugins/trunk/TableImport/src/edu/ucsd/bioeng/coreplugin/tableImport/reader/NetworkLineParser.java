package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.util.ArrayList;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import giny.model.Edge;
import giny.model.Node;

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

	public NetworkLineParser(List<Integer> nodeList, List<Integer> edgeList,
			final NetworkTableMappingParameters nmp) {
		this.nmp = nmp;
		this.nodeList = nodeList;
		this.edgeList = edgeList;
	}

	public void parseEntry(String[] parts) {
		final Edge edge = addNodeAndEdge(parts);
		if (edge != null) {
			addEdgeAttributes(edge, parts);
		}
	}

	private Edge addNodeAndEdge(final String[] parts) {
		final Node source;
		if (nmp.getSourceIndex() <= parts.length - 1
				&& parts[nmp.getSourceIndex()] != null) {
			source = Cytoscape.getCyNode(parts[nmp.getSourceIndex()].trim(),
					true);
			nodeList.add(source.getRootGraphIndex());
		} else {
			source = null;
		}

		final Node target;
		if (nmp.getTargetIndex() <= parts.length - 1
				&& parts[nmp.getTargetIndex()] != null) {
			target = Cytoscape.getCyNode(parts[nmp.getTargetIndex()].trim(),
					true);
			nodeList.add(target.getRootGraphIndex());
		} else {
			target = null;

		}

		final String interaction;
		if (nmp.getInteractionIndex() == -1
				|| nmp.getInteractionIndex() > parts.length - 1
				|| parts[nmp.getInteractionIndex()] == null) {
			interaction = nmp.getDefaultInteraction();
		} else {
			interaction = parts[nmp.getInteractionIndex()].trim();
		}

		final Edge edge;
		if (source != null && target != null) {
			edge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION,
					interaction, true);
			edgeList.add(edge.getRootGraphIndex());
		} else {
			edge = null;
		}

		return edge;
	}

	private void addEdgeAttributes(final Edge edge, final String[] parts) {

		for (int i = 0; i < parts.length; i++) {
			if (i != nmp.getSourceIndex() && i != nmp.getTargetIndex()
					&& i != nmp.getInteractionIndex()) {
				if (nmp.getImportFlag().length > i
						&& nmp.getImportFlag()[i] == true) {
					mapAttribute(edge.getIdentifier(), parts[i].trim(), i);
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
	private void mapAttribute(final String key, final String entry,
			final int index) {
		Byte type = nmp.getAttributeTypes()[index];

		switch (type) {
		case CyAttributes.TYPE_BOOLEAN:
			nmp.getAttributes().setAttribute(key,
					nmp.getAttributeNames()[index], new Boolean(entry));
			break;
		case CyAttributes.TYPE_INTEGER:
			nmp.getAttributes().setAttribute(key,
					nmp.getAttributeNames()[index], new Integer(entry));
			break;
		case CyAttributes.TYPE_FLOATING:
			nmp.getAttributes().setAttribute(key,
					nmp.getAttributeNames()[index], new Double(entry));
			break;
		case CyAttributes.TYPE_STRING:
			nmp.getAttributes().setAttribute(key,
					nmp.getAttributeNames()[index], entry);
			break;
		case CyAttributes.TYPE_SIMPLE_LIST:
			/*
			 * In case of list, not overwrite the attribute. Get the existing
			 * list, and add it to the list.
			 */

			List curList = nmp.getAttributes().getListAttribute(key,
					nmp.getAttributeNames()[index]);
			if (curList == null) {
				curList = new ArrayList();
			}
			curList.addAll(buildList(entry));

			nmp.getAttributes().setListAttribute(key,
					nmp.getAttributeNames()[index], curList);
			break;
		default:
			nmp.getAttributes().setAttribute(key,
					nmp.getAttributeNames()[index], entry);
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

		final String[] parts = (entry.replace("\"", "")).split(nmp
				.getListDelimiter());
		for (String listItem : parts) {
			listAttr.add(listItem.trim());
		}
		return listAttr;
	}

}
