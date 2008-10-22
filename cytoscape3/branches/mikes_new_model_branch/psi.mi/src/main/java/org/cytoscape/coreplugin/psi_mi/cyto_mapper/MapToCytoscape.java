/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.coreplugin.psi_mi.cyto_mapper;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.coreplugin.psi_mi.data_mapper.Mapper;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapperException;
import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.Interaction;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.vocab.CommonVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;
import org.cytoscape.coreplugin.psi_mi.util.AttributeUtil;
import org.cytoscape.coreplugin.psi_mi.util.ListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Maps Interaction objects to Cytoscape Node/Edge Objects.
 * This data_mapper will work on a new empty GraphPerspective, or an existing GraphPerspective
 * with pre-existing data.  If the GraphPerspective has pre-existing nodes/edges,
 * the data_mapper will automatically check for duplicates when new interactions
 * are added.
 *
 * @author Ethan Cerami
 * @author Nisha Vinod
 */
public class MapToCytoscape implements Mapper {
	/**
	 * Spoke View.
	 */
	public static final int SPOKE_VIEW = 1;

	/**
	 * Matrix View.
	 */
	public static final int MATRIX_VIEW = 2;

	/**
	 * ROOT_GRAPH_INDEXES Attribute Name.
	 */
	public static final String ROOT_GRAPH_INDEXES = "ROOT_GRAPH_INDEXES";

	/**
	 * Data Service Interactor Reference
	 */
	public static final String DS_INTERACTOR = "DS_INTERACTOR";

	/**
	 * Data Service Interaction Reference
	 */
	public static final String DS_INTERACTION = "DS_INTERACTION";

	/**
	 * Node List
	 */
	private ArrayList nodeList = new ArrayList();

	/**
	 * Edge List
	 */
	private ArrayList edgeList = new ArrayList();

	/**
	 * CyMap Object.
	 */
	private HashMap cyMap;

	/**
	 * ArrayList of Interaction Objects.
	 */
	private ArrayList interactions;

	/**
	 * Graph Type, e.g. SPOKE_VIEW or MATRIX_VIEW.
	 */
	private int graphType;

	/**
	 * List of Warnings.
	 */
	private ArrayList warnings = new ArrayList();

	/**
	 * If Number of Intearctors <= MATRIX_CUT_OFF then
	 * do Matrix View.  Otherwise, report a warning.
	 */
	private static final int MATRIX_CUT_OFF = 5;

	/**
	 * Open Paren Constant.
	 */
	protected static final String OPEN_PAREN = " (";

	/**
	 * Close Paren Constant.
	 */
	protected static final String CLOSE_PAREN = ") ";
	private Map intMap;
	private static final boolean DEBUG = false;

	/**
	 * Constructor.
	 * The graphType parameter determines the method of drawing interactions
	 * when the number of interactors > 2.
	 * <p/>
	 * For example, consider we have an interaction defined for (A, B, C).
	 * <p/>
	 * If graphType is set to SPOKE_VIEW and A is the "bait" interactor, the
	 * data_mapper will draw the following graph:
	 * <p/>
	 * A <--> B
	 * A <--> C
	 * <p/>
	 * This looks like a "spoke", with A at the center of the spoke.  Note that
	 * the data_mapper will not draw an edge between B and C.  In order to properly
	 * draw a spoke view, one of the interactors must be designated as "bait".
	 * If graphType is set to SPOKE_VIEW, but there is no "bait" interactor,
	 * a MapperException will be thrown.
	 * Modified the code such that if there is no bait, bait is determined from the
	 * names sorted alphanumerically, and the first one is selected as bait.
	 * <p/>
	 * If graphType is set to MATRIX_VIEW, the data_mapper will draw the following
	 * graph:
	 * <p/>
	 * A <--> B
	 * A <--> C
	 * B <--> C
	 * <p/>
	 * In the matrix view, each node interacts with all other nodes, and
	 * therefore there is now an edge between B and C.  The matrix view does
	 * not require a "bait" interactor.
	 *
	 * @param interactionList interactionList ArrayList of Interaction objects.
	 * @param graphType       graphType (SPOKE_VIEW or MATRIX_VIEW).
	 */
	public MapToCytoscape(ArrayList interactionList, int graphType) {
		if ((graphType < SPOKE_VIEW) || (graphType > MATRIX_VIEW)) {
			throw new IllegalArgumentException("Illegal GraphType Parameter.");
		}

		this.cyMap = new HashMap();
		this.interactions = interactionList;
		this.graphType = graphType;
	}

	/**
	 * Gets a Map of all Interactors/Interactions indexed by Node/Edge ID.
	 *
	 * @return HashMap Object.
	 */
	public HashMap getCyMap() {
		return this.cyMap;
	}

	/**
	 * Perform Mapping.
	 *
	 * @throws MapperException Indicates Error in mapping.
	 */
	public final void doMapping() throws MapperException {
		HashMap nodeMap = new HashMap();
		HashMap edgeMap = new HashMap();
		intMap = new HashMap();
		//  Validate Interaction Data
		validateInteractions();

		//  First pass, add all new nodes.
		addNewNodes(nodeMap);

		//  Second pass, add all new interactions.
		addNewEdges(nodeMap, edgeMap);
	}

	/**
	 * Gets all node indices.
	 *
	 * @return array of root graph indices.
	 */
	public int[] getNodeIndices() {
		int[] nodeIndices = new int[nodeList.size()];

		for (int i = 0; i < nodeList.size(); i++) {
			CyNode node = (CyNode) nodeList.get(i);
			nodeIndices[i] = node.getRootGraphIndex();
		}

		return nodeIndices;
	}

	/**
	 * Gets all edge indices.
	 *
	 * @return array of root graph indices.
	 */
	public int[] getEdgeIndices() {
		int[] edgeIndices = new int[edgeList.size()];

		for (int i = 0; i < edgeList.size(); i++) {
			CyEdge edge = (CyEdge) edgeList.get(i);
			edgeIndices[i] = edge.getRootGraphIndex();
		}

		return edgeIndices;
	}

	/**
	 * Gets Mapping Warnings.
	 *
	 * @return Mapping Warnings.
	 */
	public ArrayList getWarnings() {
		return this.warnings;
	}

	/**
	 * Validates Interactions.
	 *
	 * @throws MapperException Mapping Exception.
	 */
	private void validateInteractions() throws MapperException {
		String errorMsg = "In order to correctly graph your interactions, "
		                  + "each interaction must specify exactly " + "one bait value.";

		if (graphType == SPOKE_VIEW) {
			for (int i = 0; i < interactions.size(); i++) {
				Interaction interaction = (Interaction) interactions.get(i);
				ArrayList interactors = interaction.getInteractors();

				if (interactors.size() > 2) {
					HashMap baitMap = (HashMap) interaction.getAttribute(InteractionVocab.BAIT_MAP);

					if (baitMap == null) {
						throw new MapperException(errorMsg);
					} else {
						Interactor bait = determineBait(interactors, baitMap);

						if (bait == null) {
							throw new MapperException(errorMsg);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds New Nodes to Network.
	 *
	 * @param nodeMap HashMap of current nodes.
	 */
	private void addNewNodes(HashMap nodeMap) {
		for (int i = 0; i < interactions.size(); i++) {
			Interaction interaction = (Interaction) interactions.get(i);
			ArrayList interactors = interaction.getInteractors();

			for (int j = 0; j < interactors.size(); j++) {
				Interactor interactor = (Interactor) interactors.get(j);
				addNode(interactor, nodeMap);
			}
		}
	}

	/**
	 * Adds New edges to Network.
	 *
	 * @param nodeMap Current Nodes.
	 * @param edgeMap Current Edges.
	 */
	private void addNewEdges(HashMap nodeMap, HashMap edgeMap) {
		for (int i = 0; i < interactions.size(); i++) {
			Interaction interaction = (Interaction) interactions.get(i);
			ArrayList interactors = interaction.getInteractors();

			if (graphType == MATRIX_VIEW) {
				doMatrixView(interactors, nodeMap, interaction, edgeMap);
			} else {
				doSpokeView(interactors, nodeMap, interaction, edgeMap);
			}
		}
	}

	/**
	 * Map to MATRIX_VIEW Graph Type
	 */
	private void doMatrixView(ArrayList interactors, HashMap nodeMap, Interaction interaction,
	                          HashMap edgeMap) {
		if (interactors.size() <= MATRIX_CUT_OFF) {
			for (int j = 0; j < interactors.size(); j++) {
				for (int k = j + 1; k < interactors.size(); k++) {
					//  Get Interactors
					Interactor interactor1 = (Interactor) interactors.get(j);
					Interactor interactor2 = (Interactor) interactors.get(k);

					//  Conditionally Create Edge
					createEdge(interactor1, interactor2, interaction, nodeMap, edgeMap);
				}
			}
		} else {
			ExternalReference[] refs = interaction.getExternalRefs();
			StringBuffer refList = new StringBuffer();

			if ((refs != null) && (refs.length > 0)) {
				for (int i = 0; i < refs.length; i++) {
					String db = refs[i].getDatabase();
					String id = refs[i].getId();
					refList.append("[" + db + ":" + id + "] ");
				}
			} else {
				refList.append("[No Ids available]");
			}

			String warningMsg = new String("Interaction contains more" + " than " + MATRIX_CUT_OFF
			                               + " interactors.  The interaction will not be mapped to "
			                               + " any Cytoscape edges.  The offending interaction is"
			                               + " identified with the following identifiers:  "
			                               + refList);
			warnings.add(warningMsg);
		}
	}

	/**
	 * Map to SPOKE_VIEW Graph Type
	 */
	private void doSpokeView(ArrayList interactors, HashMap nodeMap, Interaction interaction,
	                         HashMap edgeMap) {
		HashMap baitMap = (HashMap) interaction.getAttribute(InteractionVocab.BAIT_MAP);

		if (interactors.size() > 2) {
			//  Determine bait interactor
			Interactor bait = determineBait(interactors, baitMap);

			//  Create Edges between Bait and all other interactors.
			for (int i = 0; i < interactors.size(); i++) {
				Interactor interactor = (Interactor) interactors.get(i);

				String role = (String) baitMap.get(interactor.getName());
				int eliminateInteractorflag = 0;

				if ((role == null) || (!(role.equalsIgnoreCase("bait")))) {
					if ((role != null) && !role.equalsIgnoreCase("prey")) {
						if (!(bait.getName().equalsIgnoreCase(interactor.getName()))) {
							createEdge(bait, interactor, interaction, nodeMap, edgeMap);
						} else {
							if (eliminateInteractorflag == 1) {
								createEdge(bait, interactor, interaction, nodeMap, edgeMap);
							} else if (eliminateInteractorflag == 0) {
								eliminateInteractorflag = 1;
							}
						}
					} else {
						createEdge(bait, interactor, interaction, nodeMap, edgeMap);
					}
				}
			}
		} else if (interactors.size() == 2) {
			Interactor interactor0 = (Interactor) interactors.get(0);
			Interactor interactor1 = (Interactor) interactors.get(1);

			if ((interactor0 != null) && (interactor1 != null)) {
				createEdge(interactor0, interactor1, interaction, nodeMap, edgeMap);
			}
		}

		ListUtil.setInteractionMap(intMap);
	}

	/*
	* Determines a bait
	*/
	private Interactor determineBait(ArrayList interactors, HashMap baitMap) {
		Interactor bait = null;

		for (int i = 0; i < interactors.size(); i++) {
			Interactor interactor = (Interactor) interactors.get(i);
			String name = interactor.getName();

			String role = (String) baitMap.get(interactor.getName());

			if ((role != null) && role.equalsIgnoreCase("bait")) {
				bait = interactor;
				AttributeUtil.setbaitStatus(0);

				break;
			}
		}

		// If a bait is not found, get a bait by sorting its name and gets the
		//first one as bait
		if (bait == null) {
			bait = determineBaitByName(interactors);
			AttributeUtil.setbaitStatus(1);
		}

		return bait;
	}

	/**
	 * If there is no bait defined, then it is determined by
	 * interactor name sorted alphanumerically and then the first in the
	 * list is selected as bait.
	 */
	private Interactor determineBaitByName(ArrayList interactors) {
		for (int i = 0; i < interactors.size(); i++) {
			Interactor temp;

			for (int j = 0; j < (interactors.size() - 1); j++) {
				Interactor interactor1 = (Interactor) interactors.get(j);
				Interactor interactor2 = (Interactor) interactors.get(j + 1);

				if (interactor1.getName().compareTo(interactor2.getName()) > 0) {
					temp = interactor1;
					interactor1 = interactor2;
					interactor2 = temp;
				}
			}
		}

		return (Interactor) interactors.get(0);
	}

	/**
	 * Creates Edge Between Node1 and Node2.
	 */
	private void createEdge(Interactor interactor1, Interactor interactor2,
	                        Interaction interaction, HashMap nodeMap, HashMap edgeMap) {
		//  Get Matching Nodes
		CyNode node1 = (CyNode) nodeMap.get(interactor1.getName());
		CyNode node2 = (CyNode) nodeMap.get(interactor2.getName());

		//  Create node1 --> node2 edge key
		String key = this.createEdgeKey(node1, node2, interaction);
		log("Creating edge:  " + key);

		//  Create Edge between node1 and node2.
		CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, key, true);

		//  Now get the new edge and set the edge identifier.
		edge.setIdentifier(key);

		// If no edge exists then create a new one
		if ((!edgeExists(node1, node2, interaction, edgeMap))) {
			edgeList.add(edge);
			mapEdgeAttributes(interaction, edge);

			int edgeRootGraphIndex = edge.getRootGraphIndex();
			ArrayList indexes = (ArrayList) interaction.getAttribute(ROOT_GRAPH_INDEXES);

			if (indexes == null) {
				indexes = new ArrayList();
				interaction.addAttribute(ROOT_GRAPH_INDEXES, indexes);
			}

			indexes.add(Integer.valueOf(edgeRootGraphIndex));

			//  Add to CyMap
			cyMap.put(key, interaction);

			//  Add to Edge Map
			edgeMap.put(key, edge);
			intMap.put(Integer.valueOf(interaction.getInteractionId()), interaction);
		}
	}

	/**
	 * Determines if an edge already exists between the two nodes.
	 */
	private boolean edgeExists(CyNode node1, CyNode node2, Interaction interaction, HashMap edgeMap) {
		//  Create node1 --> node2 edge key
		String key1 = this.createEdgeKey(node1, node2, interaction);

		//  Create node2 --> node2 edge key
		String key2 = this.createEdgeKey(node2, node1, interaction);

		//  Check to see if either key already exists
		boolean exists1 = edgeMap.containsKey(key1);
		boolean exists2 = edgeMap.containsKey(key2);
		boolean exists3 = exists1 | exists2;

		return exists3;
	}

	/**
	 * Conditionally adds new node to graph.
	 *
	 * @param interactor Interactor object.
	 * @param map        HashMap of current nodes.
	 */
	private void addNode(Interactor interactor, HashMap map) {
		String name = interactor.getName();
		boolean inGraph = map.containsKey(name);

		//HashMap nodemap;
		if (!inGraph) {
			//  Create New Node via getCyNode Method.
			CyNode node = Cytoscape.getCyNode(name, true);
			//  Add New Node to Network
			nodeList.add(node);

			//  Set Node Identifier, Canonical Name, and Common Name.
			node.setIdentifier(name);

			//  Add to CyMap
			cyMap.put(name, interactor);

			//  Map Node Attributes, e.g. species, xrefs, and/or sequence.
			mapNodeAttributes(interactor, node);

			//  Add Node to Node Map.
			map.put(name, node);
		}
	}

	/**
	 * Maps Node Attributes to Cytoscape GraphObj Attributes.
	 * Can be subclassed.
	 *
	 * @param interactor Interactor object.
	 * @param cyNode     Node.
	 */
	protected void mapNodeAttributes(Interactor interactor, CyNode cyNode) {
		//  Map All Interactor Attributes
		HashMap attributeMap = interactor.getAllAttributes();
		Iterator iterator = attributeMap.keySet().iterator();

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = (String) attributeMap.get(key);

			if (value != null) {
				Cytoscape.getNodeAttributes().setAttribute(cyNode.getIdentifier(), key, value);
			}
		}

		//  Map All External References
		ExternalReference[] refs = interactor.getExternalRefs();

		if (refs != null) {
			List dbsList = new ArrayList(refs.length);
			List idsList = new ArrayList(refs.length);

			for (int i = 0; i < refs.length; i++) {
				ExternalReference ref = refs[i];
				dbsList.add(ref.getDatabase());
				idsList.add(ref.getId());
			}

			if ((dbsList != null) && (dbsList.size() != 0)) {
				Cytoscape.getNodeAttributes()
				         .setListAttribute(cyNode.getIdentifier(), CommonVocab.XREF_DB_NAME, dbsList);
			}

			if ((idsList != null) && (idsList.size() != 0)) {
				Cytoscape.getNodeAttributes()
				         .setListAttribute(cyNode.getIdentifier(), CommonVocab.XREF_DB_ID, idsList);
			}
		}
	}

	/**
	 * Maps Edge Attributes to Cytoscape Attributes.
	 * Can be subclassed.
	 *
	 * @param interaction Interaction object.
	 * @param cyEdge      Edge object.
	 */
	protected void mapEdgeAttributes(Interaction interaction, CyEdge cyEdge) {
		HashMap attributeMap = interaction.getAllAttributes();
		Iterator iterator = attributeMap.keySet().iterator();

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object attrObject = attributeMap.get(key);

			if (attrObject instanceof String) {
				String str = (String) attrObject;
				Object object = Cytoscape.getEdgeAttributes()
				                         .getStringAttribute(key, cyEdge.getIdentifier());

				if (object != null) {
					String[] values = AttributeUtil.appendString(object, str);

					if ((values != null) && (values.toString().length() != 0)) {
						Cytoscape.getEdgeAttributes()
						         .setAttribute(cyEdge.getIdentifier(), key, values.toString());
					}
				} else {
					if ((str != null) && (str.length() != 0)) {
						Cytoscape.getEdgeAttributes().setAttribute(cyEdge.getIdentifier(), key, str);
					}
				}
			}
		}

		//  Map All External References
		ExternalReference[] refs = interaction.getExternalRefs();

		if (refs != null) {
			List dbsList = new ArrayList(refs.length);
			List idsList = new ArrayList(refs.length);

			for (int i = 0; i < refs.length; i++) {
				ExternalReference ref = refs[i];
				dbsList.add(ref.getDatabase());
				idsList.add(ref.getId());
			}

			if ((dbsList != null) && (dbsList.size() != 0)) {
				Cytoscape.getEdgeAttributes()
				         .setListAttribute(cyEdge.getIdentifier(), CommonVocab.XREF_DB_NAME, dbsList);
			}

			if ((idsList != null) && (idsList.size() != 0)) {
				Cytoscape.getEdgeAttributes()
				         .setListAttribute(cyEdge.getIdentifier(), CommonVocab.XREF_DB_ID, idsList);
			}
		}
	}

	/**
	 * Create Canonical name for Interaction type.
	 * Can be subclassed.
	 *
	 * @param interaction Interaction to be named.
	 * @return canonical name of interaction type.
	 */
	protected String getInteractionTypeId(Interaction interaction) {
		StringBuffer key = new StringBuffer(OPEN_PAREN);
		String expType = (String) interaction.getAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
		String shortName = (String) interaction.getAttribute(InteractionVocab.INTERACTION_SHORT_NAME);
		String pmid = (String) interaction.getAttribute(InteractionVocab.PUB_MED_ID);

		if (expType == null) {
			key.append(" <--> ");
		} else {
			key.append(expType);
		}

		if (shortName != null) {
			key.append(":" + shortName);
		}

		if (pmid != null) {
			key.append(":" + pmid);
		}

		key.append(CLOSE_PAREN);

		return key.toString();
	}

	/**
	 * Create Hashkey for Edges.
	 *
	 * @param node1 First node.
	 * @param node2 Second node.
	 * @return HashKey.
	 */
	private String createEdgeKey(CyNode node1, CyNode node2, Interaction interaction) {
		String node1Ident = node1.getIdentifier();
		String node2Ident = node2.getIdentifier();
		String interactionType = getInteractionTypeId(interaction);

		return new String(node1Ident + interactionType + node2Ident);
	}

	private void log(String msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
}
