package cytoscape.data.readers;

import java.util.ArrayList;
import java.util.List;

import giny.model.Edge;
import giny.model.Node;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

/**
 * Parse one line for network text table
 * 
 * @author kono
 *
 */
public class NetworkLineParser {

	private final NetworkTableMappingParameters nmp;
	private final CyNetwork network;

	public NetworkLineParser(CyNetwork network,
			final NetworkTableMappingParameters nmp) {
		this.nmp = nmp;
		this.network = network;
	}

	public void parseEntry(String[] parts) {
		final Edge edge = addNodeAndEdge(parts);
		addEdgeAttributes(edge, parts);
	}

	private Edge addNodeAndEdge(final String[] parts) {
		final Node source = Cytoscape.getCyNode(parts[nmp.getSourceIndex()]
				.trim(), true);
		final Node target = Cytoscape.getCyNode(parts[nmp.getTargetIndex()]
				.trim(), true);
		final String interaction = parts[nmp.getInteractionIndex()].trim();

		final Edge edge = Cytoscape.getCyEdge(source, target,
				Semantics.INTERACTION, interaction, true);

		network.addNode(source);
		network.addNode(target);
		network.addEdge(edge);
		
		return edge;
	}

	private void addEdgeAttributes(final Edge edge, final String[] parts) {
		for(int i=0; i<parts.length; i++) {
			if(i != nmp.getSourceIndex() && i != nmp.getTargetIndex() && i != nmp.getInteractionIndex()) {
				
				mapAttribute(edge.getIdentifier(), parts[i].trim(), i);
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
			List curList = nmp.getAttributes().getAttributeList(key,
					nmp.getAttributeNames()[index]);
			if (curList == null) {
				curList = new ArrayList();
			}
			curList.addAll(buildList(entry));
			nmp.getAttributes().setAttributeList(key,
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
