package org.idekerlab.ModFindPlugin;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import org.idekerlab.ModFindPlugin.networks.SFNetwork;
import org.idekerlab.ModFindPlugin.networks.hashNetworks.FloatHashNetwork;
import java.util.List;

/**
 * Converts a single CyNetwork plus two edge attributes to two separate
 * SFNetworks.
 */
class ConvertCyNetworkToSFNetworks {
	private FloatHashNetwork physicalNetwork = null;
	private FloatHashNetwork geneticNetwork = null;

	/**
	 * Initialises the network converter.
	 * 
	 * @param inputNetwork
	 *            The single network that will be split apart into a physical
	 *            and a genetic network.
	 * @param physicalNetworkAttrName
	 *            The name of a floating point edge attribute that will be used
	 *            to induce edges in the physical network.
	 * @param geneticNetworkAttrName
	 *            The name of a floating point edge attribute that will be used
	 *            to induce edges in the genetic network.
	 * @throws IllegalArgumentException
	 *             Will be thrown if any argument is null, or if either
	 *             physicalNetworkAttrName or geneticNetworkAttrName do not
	 *             correspond to a floating point edge attribute.
	 * @throws ClassCastException
	 *             This should never be thrown and indicated an internal error!
	 */
	public ConvertCyNetworkToSFNetworks(final CyNetwork inputNetwork, final String physicalNetworkAttrName,
		                           final String geneticNetworkAttrName) throws IllegalArgumentException, ClassCastException
	{
		if (inputNetwork == null)
			throw new IllegalArgumentException(
					"input parameter inputNetwork must not be null!");
		if (physicalNetworkAttrName == null)
			throw new IllegalArgumentException(
					"input parameter physicalNetworkAttrName must not be null!");
		if (geneticNetworkAttrName == null)
			throw new IllegalArgumentException(
					"input parameter geneticNetworkAttrName must not be null!");

		final CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		final byte physEdgeAttribType = edgeAttributes.getType(physicalNetworkAttrName);
		if (physEdgeAttribType != CyAttributes.TYPE_FLOATING && physEdgeAttribType != CyAttributes.TYPE_INTEGER)
			throw new IllegalArgumentException("\"" + physicalNetworkAttrName
							   + "\" is not the name of a known floating point edge attribute!");
		final byte geneticEdgeAttribType = edgeAttributes.getType(geneticNetworkAttrName);
		if (geneticEdgeAttribType != CyAttributes.TYPE_FLOATING && geneticEdgeAttribType != CyAttributes.TYPE_INTEGER)
			throw new IllegalArgumentException("\"" + geneticNetworkAttrName
							  + "\" is not the name of a known floating point edge attribute!");

		physicalNetwork = new FloatHashNetwork(/* selfOk = */false, /* directed = */false, /* startsize = */1);
		geneticNetwork = new FloatHashNetwork(/* selfOk = */false, /* directed = */false, /* startsize = */1);

		@SuppressWarnings("unchecked") List<CyEdge> edges = (List<CyEdge>)inputNetwork.edgesList();
		for (final CyEdge edge : edges) {
			final String edgeID = edge.getIdentifier();

			final Double physicalAttrValue =
				edgeAttributes.getDoubleAttribute(edgeID, physicalNetworkAttrName);
			if (physicalAttrValue != null) {
				final float value = (physEdgeAttribType == CyAttributes.TYPE_FLOATING) ? physicalAttrValue.floatValue()
				                                                                       : physicalAttrValue.intValue();
				physicalNetwork.add(edge.getSource().getIdentifier(),
						    edge.getTarget().getIdentifier(),
						    value);
			}

			final Double geneticAttrValue =
				edgeAttributes.getDoubleAttribute(edgeID, geneticNetworkAttrName);
			if (geneticAttrValue != null) {
				final float value = (geneticEdgeAttribType == CyAttributes.TYPE_FLOATING) ? geneticAttrValue.floatValue()
				                                                                          : geneticAttrValue.intValue();
				geneticNetwork.add(edge.getSource().getIdentifier(),
						   edge.getTarget().getIdentifier(),
						   value);
			}
		}
	}

	SFNetwork getPhysicalNetwork() {
		return physicalNetwork;
	}

	SFNetwork getGeneticNetwork() {
		return geneticNetwork;
	}
}
