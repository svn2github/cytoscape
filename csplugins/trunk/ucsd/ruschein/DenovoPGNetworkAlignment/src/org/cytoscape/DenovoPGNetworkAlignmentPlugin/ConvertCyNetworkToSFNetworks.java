package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import networks.SFNetwork;
import networks.hashNetworks.FloatHashNetwork;
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
	public ConvertCyNetworkToSFNetworks(final CyNetwork inputNetwork,
			final String physicalNetworkAttrName,
			final String geneticNetworkAttrName)
			throws IllegalArgumentException, ClassCastException {
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
		if (edgeAttributes.getType(physicalNetworkAttrName) != CyAttributes.TYPE_FLOATING)
			throw new IllegalArgumentException(
					"\""
							+ physicalNetworkAttrName
							+ "\" is not the name of a known floating point edge attribute!");
		if (edgeAttributes.getType(geneticNetworkAttrName) != CyAttributes.TYPE_FLOATING)
			throw new IllegalArgumentException(
					"\""
							+ geneticNetworkAttrName
							+ "\" is not the name of a known floating point edge attribute!");

		physicalNetwork = new FloatHashNetwork(/* selfOk = */false, /*
																	 * directed
																	 * =
																	 */false, /*
																			 * startsize
																			 * =
																			 */
				1);
		geneticNetwork = new FloatHashNetwork(/* selfOk = */false, /* directed = */
				false, /* startsize = */1);

		for (final CyEdge edge : (List<CyEdge>) inputNetwork.edgesList()) {
			final String edgeID = edge.getIdentifier();

			final Double physicalAttrValue = edgeAttributes.getDoubleAttribute(
					edgeID, physicalNetworkAttrName);
			if (physicalAttrValue != null)
				physicalNetwork.add(edge.getSource().getIdentifier(), edge
						.getTarget().getIdentifier(), physicalAttrValue
						.floatValue());

			final Double geneticAttrValue = edgeAttributes.getDoubleAttribute(
					edgeID, geneticNetworkAttrName);
			if (geneticAttrValue != null)
				geneticNetwork.add(edge.getSource().getIdentifier(), edge
						.getTarget().getIdentifier(), geneticAttrValue
						.floatValue());
		}
	}

	SFNetwork getPhysicalNetwork() {
		return physicalNetwork;
	}

	SFNetwork getGeneticNetwork() {
		return geneticNetwork;
	}
}
