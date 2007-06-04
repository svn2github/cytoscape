// $Id: MapBioPaxToCytoscape.java,v 1.38 2006/10/09 20:48:20 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.mapping;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;

import cytoscape.task.TaskMonitor;

import giny.model.Edge;

import org.jdom.Element;
import org.jdom.JDOMException;

import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;
import org.mskcc.biopax_plugin.util.biopax.BioPaxCellularLocationMap;
import org.mskcc.biopax_plugin.util.biopax.BioPaxChemicalModificationMap;
import org.mskcc.biopax_plugin.util.biopax.BioPaxConstants;
import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;
import org.mskcc.biopax_plugin.util.rdf.RdfConstants;
import org.mskcc.biopax_plugin.util.rdf.RdfQuery;
import org.mskcc.biopax_plugin.util.rdf.RdfUtil;

import java.util.*;

import javax.swing.*;


/**
 * Maps a BioPAX Document to Cytoscape Nodes/Edges.
 *
 * @author Ethan Cerami.
 */
public class MapBioPaxToCytoscape {
	/**
	 * Cytoscape Attribute:  BioPAX Network.
	 * Stores boolean indicating this CyNetwork
	 * is a BioPAX network.
	 */
	public static final String BIOPAX_NETWORK = "BIOPAX_NETWORK";

	/**
	 * Cytoscape Attribute:  BioPAX Edge Type.
	 */
	public static final String BIOPAX_EDGE_TYPE = "BIOPAX_EDGE_TYPE";

	/**
	 * Cytoscape Edge Attribute:  RIGHT
	 */
	public static final String RIGHT = "RIGHT";

	/**
	 * Cytoscape Edge Attribute:  LEFT
	 */
	public static final String LEFT = "LEFT";

	/**
	 * Cytoscape Edge Attribute:  PARTICIPANT
	 */
	public static final String PARTICIPANT = "PARTICIPANT";

	/**
	 * Cytoscape Edge Attribute:  CONTROLLER
	 */
	public static final String CONTROLLER = "CONTROLLER";

	/**
	 * Cytoscape Edge Attribute:  CONTROLLED
	 */
	public static final String CONTROLLED = "CONTROLLED";

	/**
	 * Cytoscape Edge Attribute:  COFACTOR
	 */
	public static final String COFACTOR = "COFACTOR";

	/**
	 * Cytoscape Edge Attribute:  CONTAINS
	 */
	public static final String CONTAINS = "CONTAINS";
	private BioPaxUtil bpUtil;
	private ArrayList nodeList = new ArrayList();
	private ArrayList edgeList = new ArrayList();
	private BioPaxConstants bpConstants;
	private RdfQuery rdfQuery;
	private TaskMonitor taskMonitor;
	private ArrayList warningList = new ArrayList();
	private BioPaxCellularLocationMap cellularLocationAbbr = new BioPaxCellularLocationMap();
	private BioPaxChemicalModificationMap chemicalModificationAbbr = new BioPaxChemicalModificationMap();

	// created cynodes
	private Map<String,Set<String>> createdCyNodes; // cpath id is key, list of cynodes is value

	/**
	 * Inner class to store a given nodes'
	 * chemical modification(s) or cellular location(s)
	 * along with a string of abbreviations for the respective attribute
	 * (which is used in the construction of the node label).
	 */
	class NodeAttributesWrapper {
		// map of cellular location
		// or chemical modifications
		private Map attributesMap;

		// abbreviations string
		private String abbreviationString;

		// contructor
		NodeAttributesWrapper(Map attributesMap, String abbreviationString) {
			this.attributesMap = attributesMap;
			this.abbreviationString = abbreviationString;
		}

		// gets the attributes map
		Map getMap() {
			return attributesMap;
		}

		// gets the attributes map as list
		ArrayList getList() {
			return (attributesMap != null) ? new ArrayList(attributesMap.keySet()) : null;
		}

		// gets the abbrevation string (used in node label)
		String getAbbreviationString() {
			return abbreviationString;
		}
	}

	/**
	 * Constructor.
	 *
	 * @param bpUtil      BioPAX Utility Class.
	 * @param taskMonitor TaskMonitor Object.
	 */
	public MapBioPaxToCytoscape(BioPaxUtil bpUtil, TaskMonitor taskMonitor) {
		this(bpUtil);
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Constructor.
	 *
	 * @param bpUtil BioPAX Utility Class.
	 */
	public MapBioPaxToCytoscape(BioPaxUtil bpUtil) {
		this.bpUtil = bpUtil;
		this.bpConstants = new BioPaxConstants();
		this.rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());
		this.warningList = new ArrayList();
		this.createdCyNodes = new HashMap<String, Set<String>>();
	}

	/**
	 * Execute the Mapping.
	 *
	 * @throws JDOMException Error Parsing XML via JDOM.
	 */
	public void doMapping() throws JDOMException {
		// note:
		// all complex and interaction members are
		// created within respective map*Edges() routines

		//  map interactions
		mapInteractionNodes();
		mapInteractionEdges();

		// map complex
		mapComplexes();

		// map attributes
		MapNodeAttributes.doMapping(bpUtil, nodeList);
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
	 * Gets the Warning List.
	 *
	 * @return ArrayList of String Objects.
	 */
	public ArrayList getWarningList() {
		return warningList;
	}

	/**
	 * Repairs Canonical Name;  temporary fix for bug:  1001.
	 * By setting Canonical name to BIOPAX_NODE_LABEL, users can search for
	 * nodes via the Select Nodes --> By Name feature.
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	public static void repairCanonicalName(CyNetwork cyNetwork) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		Iterator iter = cyNetwork.nodesIterator();

		while (iter.hasNext()) {
			CyNode node = (CyNode) iter.next();
			String label = nodeAttributes.getStringAttribute(node.getIdentifier(),
			                                                 BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);

			if (label != null) {
				nodeAttributes.setAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME, label);
			}
		}
	}

	/**
	 * Repairs Network Name.  Temporary fix to automatically set network
	 * name to match BioPAX Pathway name.
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	public static void repairNetworkName(final CyNetwork cyNetwork) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		Iterator iter = cyNetwork.nodesIterator();
		CyNode node = (CyNode) iter.next();

		if (node != null) {
			String pathwayName = nodeAttributes.getStringAttribute(node.getIdentifier(),
			                                                       MapNodeAttributes.BIOPAX_PATHWAY_NAME);

			if (pathwayName != null) {
				cyNetwork.setTitle(pathwayName);

				//  Update UI.  Must be done via SwingUtilities,
				// or it won't work.
				SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Cytoscape.getDesktop().getNetworkPanel().updateTitle(cyNetwork);
						}
					});
			}
		}
	}

	/**
	 * Maps Select Interactions to Cytoscape Nodes.
	 */
	private void mapInteractionNodes() throws JDOMException {
		//  Extract the List of all Interactions
		List interactionList = bpUtil.getInteractionList();

		if (taskMonitor != null) {
			taskMonitor.setStatus("Adding Interactions");
			taskMonitor.setPercentCompleted(0);
		}

		for (int i = 0; i < interactionList.size(); i++) {
			Element interactionElement = (Element) interactionList.get(i);
			String id = BioPaxUtil.extractRdfId(interactionElement);

			// have we already created this interaction ?
			if (createdCyNodesContainsValue(id)) {
				continue;
			}

			//  Create node symbolizing the interaction
			CyNode interactionNode = Cytoscape.getCyNode(id, true);

			//  Add New Interaction Node to Network
			nodeList.add(interactionNode);

			//  Set Node Identifier
			interactionNode.setIdentifier(id);

			//  Extract Name
			String name = interactionElement.getName();
			name = getNodeName(interactionElement);

			//  set node attributes
			setNodeAttributes(interactionNode, name, interactionElement.getName(), id, null);

			// update our map
			putCreatedCyNodes(id, id);

			if (taskMonitor != null) {
				double perc = (double) i / interactionList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}

	private void mapInteractionEdges() throws JDOMException {
		//  Extract the List of all Interactions
		List interactionList = bpUtil.getInteractionList();

		if (taskMonitor != null) {
			taskMonitor.setStatus("Creating BioPAX Links");
			taskMonitor.setPercentCompleted(0);
		}

		for (int i = 0; i < interactionList.size(); i++) {
			Element interactionElement = (Element) interactionList.get(i);
			String id = BioPaxUtil.extractRdfId(interactionElement);

			//  Get the node symbolizing the interaction
			String name = interactionElement.getName();
			CyNode interactionNode = Cytoscape.getCyNode(id, true);

			if (bpConstants.isConversionInteraction(name)) {
				addConversionInteraction(interactionNode, interactionElement);
			} else if (bpConstants.isControlInteraction(name)) {
				addControlInteraction(interactionElement);
			} else if (name.equals(BioPaxConstants.PHYSICAL_INTERACTION)) {
				addPhysicalInteraction(interactionNode, interactionElement);
			}

			if (taskMonitor != null) {
				double perc = (double) i / interactionList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}

	private String getNodeName(Element e) {
		String nodeName = null;
		List nameList = null;
		Element nameElement = null;

		// short name
		nameList = rdfQuery.getNodes(e, "SHORT-NAME");

		if ((nameList != null) && (nameList.size() > 0)) {
			nameElement = (Element) nameList.get(0);
			nodeName = nameElement.getTextNormalize();
		}

		if ((nodeName != null) && (nodeName.length() > 0)) {
			return nodeName;
		}

		// name
		nameList = rdfQuery.getNodes(e, "NAME");

		if ((nameList != null) & (nameList.size() > 0)) {
			nameElement = (Element) nameList.get(0);
			nodeName = nameElement.getTextNormalize();
		}

		if ((nodeName != null) && (nodeName.length() > 0)) {
			return nodeName;
		}

		// shortest synonym
		int shortestSynonymIndex = -1;
		nameList = rdfQuery.getNodes(e, "SYNONYMS");

		if ((nameList != null) && (nameList.size() > 0)) {
			int minLength = -1;

			for (int lc = 0; lc < nameList.size(); lc++) {
				nameElement = (Element) nameList.get(lc);

				String curNodeName = nameElement.getTextNormalize();

				if ((minLength == -1) || (curNodeName.length() < minLength)) {
					minLength = curNodeName.length();
					nodeName = curNodeName;
				}
			}

			if (shortestSynonymIndex > -1) {
				return nodeName;
			}
		}

		// made it this far, outta here
		return null;
	}

	private String truncateLongStr(String str) {
		if (str.length() > 25) {
			str = str.substring(0, 25) + "...";
		}

		return str;
	}

	private void mapComplexes() {

		// maintain list to keep track of complexes (id) processed
		// we do not want to create a free standing complex if
		// it is a member of an interaction or a complex
		List<Element> alreadyProcessedList = new ArrayList<Element>();

		// list contains all complexes and members
		// in which the complex is a participant of an interaction
		List<Element> interactionParticipantMembers = new ArrayList<Element>();

		//  extract list of physical entities
		List<Element> physicalEntityList = bpUtil.getPhysicalEntityList();

		// edge attributes
		CyAttributes attributes = Cytoscape.getEdgeAttributes();

		for (Element physicalEntity : physicalEntityList) {

			// only processing complexes
			if (!physicalEntity.getName().equals(BioPaxConstants.COMPLEX)) continue;

			// get id
			String complexID = BioPaxUtil.extractRdfId(physicalEntity);

			// get all components of complex
			List<Element> flatComplexMemberList = new ArrayList<Element>();
			List complexMembersList = getComplexMemberList(flatComplexMemberList, physicalEntity);

			// if this complex is in cyNodesCreated map, it is part of interaction
			boolean isInteractionParticipant = createdCyNodes.containsKey(complexID);

			if (isInteractionParticipant) {
				for (String sourceID : createdCyNodes.get(complexID)) {
					CyNode sourceNode = Cytoscape.getCyNode(sourceID);
					mapComplex(sourceNode, sourceID, physicalEntity, complexMembersList);
				}
			}
			else {
				// complex does not appear in an interaction
				if (!alreadyProcessedList.contains(physicalEntity)) {
					mapComplex(null, null, physicalEntity, complexMembersList);
				}
			}
			// prevent "free standing" complexes from being constructed
			alreadyProcessedList.addAll(flatComplexMemberList);
		}
	}

	/**
	 * Method which returns a List of members for the given complex.  If a member
	 * is a complex, the list entry is a map whose key is the complex element and whose
	 * value is another list.
	 *
	 * @param flatlist List<String> - list past by reference to store complex member id's in flat structure
	 * @param physicalEntity element - a complex whose member list we construct
	 * @return List - element or map objects
	 */
	private List getComplexMemberList(List<Element> flatList, Element physicalEntity) {

		List toReturn = new ArrayList();

		flatList.add(physicalEntity);
		List<Element> complexMembersList = rdfQuery.getNodes(physicalEntity, "COMPONENTS/*/PHYSICAL-ENTITY/*");
		for (Element pe : complexMembersList) {
			if (pe.getName().equals(BioPaxConstants.COMPLEX)) {
				List memberComplexMemberList = getComplexMemberList(flatList, pe);
				Map memberMap = new HashMap();
				memberMap.put(pe, memberComplexMemberList);
				toReturn.add(memberMap);
			}
			else {
				toReturn.add(pe);
				flatList.add(pe);
			}
		}

		// outta here
		return toReturn;
	}

	private CyNode mapComplex(CyNode parentNode, String idSuffix, Element bindingElement, List complexMembersList) {

		// edge attributes
		CyAttributes attributes = Cytoscape.getEdgeAttributes();

		// source node
		CyNode sourceNode;
		String sourceID = BioPaxUtil.extractRdfId(bindingElement);
		if (parentNode != null) {
			sourceNode = parentNode;
		}
		else {
			String preCookedSourceID = sourceID;
			sourceID = (idSuffix != null) ? sourceID + "-" + idSuffix : sourceID;
			String name = sourceID;
			name = getNodeName(bindingElement);
			sourceNode = Cytoscape.getCyNode(sourceID, true);
			nodeList.add(sourceNode);
			sourceNode.setIdentifier(sourceID);
			setNodeAttributes(sourceNode, name, bindingElement.getName(), preCookedSourceID, null);
			putCreatedCyNodes(sourceID, sourceID);
		}
		
		for (Object member : complexMembersList) {

			CyNode targetNode = null;
			if (member instanceof Element) {
				String targetId = BioPaxUtil.extractRdfId((Element)member);
				targetNode = getCyNode(bindingElement, (Element)member,
									   BioPaxConstants.COMPLEX, sourceNode.getIdentifier());
			}
			else if (member instanceof Map) {
				// key is binding element
				for (Object key : ((Map)member).keySet()) {
					targetNode = mapComplex(null, sourceNode.getIdentifier(), (Element)key, (List)(((Map)member).get(key)));
				}
			}

			// create the edge
			Edge edge = Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, CONTAINS, true);
			attributes.setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, CONTAINS);
			edgeList.add(edge);
		}

		// outta here
		return sourceNode;
	}

	/**
	 * Adds a Physical Interaction, such as a binding interaction between
	 * two proteins.
	 */
	private void addPhysicalInteraction(CyNode interactionNode, Element interactionElement) {
		//  Add all Participants
		//  There can be 0 or more PARTICIPANT Elements
		List participantElements = rdfQuery.getNodes(interactionElement, "PARTICIPANTS");

		for (int i = 0; i < participantElements.size(); i++) {
			Element participantElement = (Element) participantElements.get(i);
			linkNodes(interactionElement, interactionNode, participantElement, PARTICIPANT);
		}
	}

	/**
	 * Adds a Conversion Interaction.
	 */
	private void addConversionInteraction(CyNode interactionNode, Element interactionElement) {
		//  Add Left Side of Reaction
		//  There can be 0 or more LEFT Elements
		List leftSideElements = rdfQuery.getNodes(interactionElement, LEFT);

		for (int i = 0; i < leftSideElements.size(); i++) {
			Element leftElement = (Element) leftSideElements.get(i);
			linkNodes(interactionElement, interactionNode, leftElement, LEFT);
		}

		//  Add Right Side of Reaction
		//  There can be 0 or more RIGHT Elements
		List rightSideElements = rdfQuery.getNodes(interactionElement, RIGHT);

		for (int i = 0; i < rightSideElements.size(); i++) {
			Element rightElement = (Element) rightSideElements.get(i);
			linkNodes(interactionElement, interactionNode, rightElement, RIGHT);
		}
	}

	/**
	 * Add Edges Between Interaction Node and Physical Entity Nodes.
	 */
	private void linkNodes(Element interactionElement, CyNode nodeA, Element participantElement,
	                       String type) {
		//  Get all Physical Entities.  There can be 0 or more
		List physicalEntityList = rdfQuery.getNodes(participantElement, "*/PHYSICAL-ENTITY/*");

		// edge attributes
		CyAttributes attributes = Cytoscape.getEdgeAttributes();

		for (int i = 0; i < physicalEntityList.size(); i++) {
			Element physicalEntity = (Element) physicalEntityList.get(i);
			CyNode nodeB = getCyNode(interactionElement, physicalEntity, type, null);

			if (nodeB == null) {
				return;
			}

			CyEdge edge = null;

			if (type.equals(RIGHT) || type.equals(COFACTOR) || type.equals(PARTICIPANT)) {
				edge = Cytoscape.getCyEdge(nodeA, nodeB, Semantics.INTERACTION, type, true);
			} else {
				edge = Cytoscape.getCyEdge(nodeB, nodeA, Semantics.INTERACTION, type, true);
			}

			//Cytoscape.setEdgeAttributeValue(edge, BIOPAX_EDGE_TYPE, type);
			attributes.setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, type);
			edgeList.add(edge);
		}
	}

	/**
	 * Adds a BioPAX Control Interaction.
	 */
	private void addControlInteraction(Element interactionElement) {
		//  Get the Interaction Node represented by this Interaction Element
		String interactionId = BioPaxUtil.extractRdfId(interactionElement);
		CyNode interactionNode = Cytoscape.getCyNode(interactionId);

		//  Get the Controlled Element
		//  For now, we assume there is only 1 controlled element
		List controlledList = rdfQuery.getNodes(interactionElement, "CONTROLLED/*");

		if (controlledList.size() == 1) {
			Element controlledElement = (Element) controlledList.get(0);
			String controlledId = BioPaxUtil.extractRdfId(controlledElement);
			CyNode controlledNode = Cytoscape.getCyNode(controlledId);

			if (controlledNode == null) {
				this.warningList.add(new String("Warning!  Cannot find:  " + controlledId));
			} else {
				//  Determine the BioPAX Edge Type
				String typeStr = CONTROLLED;
				List controlType = rdfQuery.getNodes(interactionElement, "CONTROL-TYPE");

				if ((controlType != null) && (controlType.size() > 0)) {
					Element controlTypeElement = (Element) controlType.get(0);
					typeStr = controlTypeElement.getTextNormalize();
				}

				//  Create Edge from Control Interaction Node to the
				//  Controlled Node
				Edge edge = Cytoscape.getCyEdge(interactionNode, controlledNode,
				                                Semantics.INTERACTION, typeStr, true);
				//Cytoscape.setEdgeAttributeValue(edge, BIOPAX_EDGE_TYPE, typeStr);
				Cytoscape.getEdgeAttributes()
				         .setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, typeStr);
				edgeList.add(edge);

				//  Create Edges from the Controller(s) to the
				//  Control Interaction
				List controllerList = rdfQuery.getNodes(interactionElement, "CONTROLLER");

				for (int i = 0; i < controllerList.size(); i++) {
					Element controllerElement = (Element) controllerList.get(i);
					linkNodes(interactionElement, interactionNode, controllerElement, CONTROLLER);
				}

				mapCoFactors(interactionElement);
			}
		} else {
			warningList.add("Warning!  Control Interaction: " + interactionId
			                + "has more than one CONTROLLED " + "Element.");
		}
	}

	/**
	 * Map All Co-Factors.
	 */
	private void mapCoFactors(Element interactionElement) {
		String interactionId = BioPaxUtil.extractRdfId(interactionElement);
		List coFactorList = rdfQuery.getNodes(interactionElement, "COFACTOR");

		if (coFactorList.size() == 1) {
			Element coFactorElement = (Element) coFactorList.get(0);

			List coFactorPhysEntityList = rdfQuery.getNodes(coFactorElement,
			                                                "physicalEntityParticipant/PHYSICAL-ENTITY/*");

			if (coFactorPhysEntityList.size() == 1) {
				Element physicalEntity = (Element) coFactorPhysEntityList.get(0);
				String coFactorId = BioPaxUtil.extractRdfId(physicalEntity);
				CyNode coFactorNode = Cytoscape.getCyNode(coFactorId);

				if (coFactorNode == null) {
					coFactorNode = getCyNode(interactionElement, physicalEntity,
					                         BioPaxConstants.CONTROL, null);
				}

				//  Create Edges from the CoFactors to the Controllers
				List controllerList = rdfQuery.getNodes(interactionElement, "CONTROLLER");

				for (int i = 0; i < controllerList.size(); i++) {
					Element controllerElement = (Element) controllerList.get(i);
					linkNodes(interactionElement, coFactorNode, controllerElement, COFACTOR);
				}
			} else if (coFactorPhysEntityList.size() > 1) {
				warningList.add("Warning!  Control Interaction:  " + interactionId
				                + " has a COFACTOR Element with "
				                + "more than one physicalEntity.  I am not yet "
				                + "equipped to handle this.");
			}
		} else if (coFactorList.size() > 1) {
			warningList.add("Warning!  Control Interaction:  " + interactionId
			                + " has more than one COFACTOR Element.  " + "I am not yet equipped "
			                + "to handle this.");
		}
	}

	/**
	 * Creates required CyNodes given a binding element (complex or interaction).
	 *
	 * @param bindingElement Element
	 * @param physicalEntity Element
	 * @param type           String
	 * @param suffix         String - added to arg list to support duplication of complexes within interactions
	 * @return CyNode
	 */
	private CyNode getCyNode(Element bindingElement, Element physicalEntity, String type, String suffix) {
		// we handle complexes & interactions differently than proteins
		BioPaxConstants bpConstants = new BioPaxConstants();
		boolean isComplex = (physicalEntity.getName().equals(BioPaxConstants.COMPLEX) ||
							 physicalEntity.getName().equals(BioPaxConstants.COMPLEX_ASSEMBLY));
		boolean isInteraction = bpConstants.isInteraction(physicalEntity.getName());

		// extract id
		String id = BioPaxUtil.extractRdfId(physicalEntity);

		if ((id == null) || (id.length() == 0)) {
			return null;
		}

		// if we have an interaction, we can short circuit
		if (isInteraction) {
			if (createdCyNodesContainsValue(id)) {
				return Cytoscape.getCyNode(id);
			}
		}

		//  get node name
		String nodeName = id;
		nodeName = getNodeName(physicalEntity);

		// create a node label & cynode id
		String nodeLabel = new String(truncateLongStr(nodeName));
		String cyNodeId = new String(id);
		NodeAttributesWrapper chemicalModificationsWrapper = null;
		NodeAttributesWrapper cellularLocationsWrapper = null;

		if (!isComplex && !isInteraction) {
			// add chemical modification & cellular location to label
			chemicalModificationsWrapper = getInteractionChemicalModifications(bindingElement,
			                                                                   physicalEntity, type);

			String chemicalModification = (chemicalModificationsWrapper != null)
			                              ? chemicalModificationsWrapper.getAbbreviationString()
			                              : null;
			cellularLocationsWrapper = getInteractionCellularLocations(bindingElement,
			                                                           physicalEntity, type);

			String cellularLocation = (cellularLocationsWrapper != null)
			                          ? cellularLocationsWrapper.getAbbreviationString() : null;
			nodeLabel += (((chemicalModification != null) && (chemicalModification.length() > 0))
			              ? chemicalModification : "");
			nodeLabel += (((cellularLocation != null) && (cellularLocation.length() > 0))
			              ? ("\n(" + cellularLocation + ")") : "");

			// add modifications to cynode id
			cyNodeId += (((chemicalModification != null) && (chemicalModification.length() > 0))
			             ? chemicalModification : "");
			cyNodeId += (((cellularLocation != null) && (cellularLocation.length() > 0))
			             ? ("(" + cellularLocation + ")") : "");
		}

		// if binding element is complex, lets also tack on complex id
		if (type == BioPaxConstants.COMPLEX ||
			(isComplex && (type == RIGHT || type == LEFT))) {
			String bindingId = (suffix != null) ? suffix : BioPaxUtil.extractRdfId(bindingElement);
			cyNodeId += ("-" + bindingId);
		}

		// have we seen this node before
		if (createdCyNodesContainsValue(cyNodeId)) {
			return Cytoscape.getCyNode(cyNodeId);
		}

		// haven't seen this node before, lets create a new one
		CyNode node = Cytoscape.getCyNode(cyNodeId, true);
		nodeList.add(node);
		node.setIdentifier(cyNodeId);

		//  set node attributes
		setNodeAttributes(node, nodeName, physicalEntity.getName(), id,
		                  (isComplex || isInteraction) ? null : nodeLabel);

		CyAttributes attributes = Cytoscape.getNodeAttributes();
		Map modificationsMap = (chemicalModificationsWrapper != null)
		                       ? chemicalModificationsWrapper.getMap() : null;

		if (modificationsMap != null) {
			//  As discussed with Ben on August 29, 2006:
			//  We will now store chemical modifications in two places:
			//  1.  a regular list of strings (to be used by the view details panel,
			//  node attribute browser, and Quick Find.
			//  2.  a multihashmap, of the following form:
			//  chemical_modification --> modification(s) --> # of modifications.
			//  this cannot be represented as a SimpleMap, and must be represented as
			//  a multi-hashmap.  This second form is used primarily by the custom
			//  rendering engine for, e.g. drawing number of phosphorylation sies.

			//  Store List of Chemical Modifications Only
			Set keySet = modificationsMap.keySet();
			List list = new ArrayList();
			list.addAll(keySet);
			attributes.setListAttribute(cyNodeId,
			                            MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_LIST, list);

			//  Store Complete Map of Chemical Modifications --> # of Modifications
			setMultiHashMap(cyNodeId, attributes,
			                MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, modificationsMap);

			if (modificationsMap.containsKey(BioPaxConstants.PHOSPHORYLATION_SITE)) {
				attributes.setAttribute(cyNodeId, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
				                        BioPaxConstants.PROTEIN_PHOSPHORYLATED);
			}
		}

		List cellularLocationsList = (cellularLocationsWrapper != null)
		                             ? cellularLocationsWrapper.getList() : null;

		if (cellularLocationsList != null) {
			attributes.setListAttribute(cyNodeId, MapNodeAttributes.BIOPAX_CELLULAR_LOCATIONS,
			                            cellularLocationsList);
		}

		// update our map -
		putCreatedCyNodes(id, cyNodeId);

		// outta here
		return node;
	}

	/**
	 * Given a binding element (complex or interaction)
	 * and type (like left or right),
	 * returns chemical modification (abbreviated form).
	 *
	 * @param bindingElement  Element
	 * @param physicalElement Element
	 * @param type            String
	 * @return NodeAttributesWrapper
	 */
	private NodeAttributesWrapper getInteractionChemicalModifications(Element bindingElement,
	                                                                  Element physicalElement,
	                                                                  String type) {
		// both of these objects will be used to contruct
		// the NodeAttributesWrapper which gets returned
		Map chemicalModificationsMap = null;
		String chemicalModifications = null;

		// if we are dealing with PARTICIPANTS (physical interactions
		// or complexes), we have to through the participants to get the
		// proper chemical modifications
		List chemicalModificationList = null;

		if ((type == PARTICIPANT) || (type == BioPaxConstants.COMPLEX)) {
			String query = (type == PARTICIPANT) ? "PARTICIPANTS/*" : "COMPONENTS/*";
			Element participantElement = getParticipantElement(bindingElement, physicalElement,
			                                                   query);

			if (participantElement != null) {
				chemicalModificationList = rdfQuery.getNodes(participantElement,
				                                             "SEQUENCE-FEATURE-LIST/*/FEATURE-TYPE/*/TERM");
			}
		} else {
			chemicalModificationList = rdfQuery.getNodes(bindingElement,
			                                             type
			                                             + "/*/SEQUENCE-FEATURE-LIST/*/FEATURE-TYPE/*/TERM");
		}

		if (chemicalModificationList == null) {
			return null;
		}

		// interate through the list returned from the query
		for (int lc = 0; lc < chemicalModificationList.size(); lc++) {
			// get next modification from list
			Element chemicalModificationElement = (Element) chemicalModificationList.get(lc);
			String modification = chemicalModificationElement.getTextNormalize();

			// do we have a modification
			if ((modification != null) && (modification.length() > 0)) {
				// initialize chemicalModifications string if necessary
				chemicalModifications = (chemicalModifications == null) ? "-" : chemicalModifications;

				// initialize chemicalModifications hashmap if necessary
				chemicalModificationsMap = (chemicalModificationsMap == null) ? new HashMap()
				                                                              : chemicalModificationsMap;

				// is this a new type of modification ?
				if (!chemicalModificationsMap.containsKey(modification)) {
					// determine abbreviation
					String abbr = (String) chemicalModificationAbbr.get(modification);
					abbr = ((abbr != null) && (abbr.length() > 0)) ? abbr : modification;

					// add abreviation to modifications string
					// (the string "-P...")
					chemicalModifications += abbr;

					// update our map - modification, count
					chemicalModificationsMap.put(modification, new Integer(1));
				} else {
					// we've seen this modification before, just update the count
					int count = ((Integer) chemicalModificationsMap.get(modification)).intValue();
					chemicalModificationsMap.put(modification, new Integer(count + 1));
				}
			}
		}

		// outta here
		return new NodeAttributesWrapper(chemicalModificationsMap, chemicalModifications);
	}

	/**
	 * Given a binding element (complex or interaction)
	 * and type (like left or right),
	 * returns cellular location (abbreviated form).
	 *
	 * @param bindingElement  Element
	 * @param physicalElement Element
	 * @param type            String
	 * @return NodeAttributesWrapper
	 */
	private NodeAttributesWrapper getInteractionCellularLocations(Element bindingElement,
	                                                              Element physicalElement,
	                                                              String type) {
		// both of these objects will be used to contruct
		// the NodeAttributesWrapper which gets returned
		String cellularLocation = null;
		Map nodeAttributes = new HashMap();

		// if we are dealing with PARTICIPANTS (physical interactions
		// or complexes), we have to through the participants to get the
		// proper cellular location
		List cellularLocationList = null;

		if ((type == PARTICIPANT) || (type == BioPaxConstants.COMPLEX)) {
			String query = (type == PARTICIPANT) ? "PARTICIPANTS/*" : "COMPONENTS/*";
			Element participantElement = getParticipantElement(bindingElement, physicalElement,
			                                                   query);

			if (participantElement != null) {
				cellularLocationList = rdfQuery.getNodes(participantElement,
				                                         "CELLULAR-LOCATION/*/TERM");
			}
		} else {
			cellularLocationList = rdfQuery.getNodes(bindingElement,
			                                         type + "/*/CELLULAR-LOCATION/*/TERM");
		}

		// ok, we should have cellular location list now, lets process it
		if ((cellularLocationList != null) && (cellularLocationList.size() == 1)) {
			Element cellularLocationElement = (Element) cellularLocationList.get(0);
			String location = cellularLocationElement.getTextNormalize();

			if ((location != null) && (location.length() > 0)) {
				cellularLocation = (String) cellularLocationAbbr.get(location);

				if (cellularLocation == null) {
					cellularLocation = location;
				}

				// add location to attributes list (we dont care about key)
				nodeAttributes.put(location, location);
			}
		} else {
			String id = BioPaxUtil.extractRdfId(bindingElement);

			if ((id != null) && (id.length() > 0)) {
				String warningSuffix = (cellularLocationList == null) ? " has no CELLULAR_LOCATION"
				                                                      : " has more than one CELLULAR-LOCATION";
				warningList.add("Warning!  Interaction: " + id + warningSuffix);
			}
		}

		// outta here
		return new NodeAttributesWrapper(nodeAttributes, cellularLocation);
	}

	/**
	 * Returns an Element ref to an interaction or complex participant
	 * given both a binding element and a physical element to match.
	 *
	 * @param bindingElement  Element
	 * @param physicalElement Element
	 * @param query           String
	 * @return Element
	 */
	private Element getParticipantElement(Element bindingElement, Element physicalElement,
	                                      String query) {
		// we're gonna need this id below
		String physicalElementId = BioPaxUtil.extractRdfId(physicalElement);

		// lets interate through the participants
		List participantList = rdfQuery.getNodes(bindingElement, query);

		if (participantList.size() > 0) {
			for (int lc = 0; lc < participantList.size(); lc++) {
				// ok, we have a participant, lets get its physical entity
				Element participantElement = (Element) participantList.get(lc);
				List physicalEntityList = rdfQuery.getNodes(participantElement, "PHYSICAL-ENTITY");

				if (physicalEntityList.size() == 1) {
					Element physicalEntityElement = (Element) physicalEntityList.get(0);
					String id = physicalEntityElement.getAttributeValue(RdfConstants.RESOURCE_ATTRIBUTE,
					                                                    RdfConstants.RDF_NAMESPACE);

					if ((id == null) || (id.length() == 0)) {
						continue;
					}

					id = RdfUtil.removeHashMark(id);

					// compare physical entity id with id passed into
					// the routine, if we have a match , we're outta here
					if (id.equals(physicalElementId)) {
						return participantElement;
					}
				} else {
					String id = BioPaxUtil.extractRdfId(participantElement);

					if ((id != null) && (id.length() > 0)) {
						warningList.add("Warning!  Participant: " + id
						                + "has more than one PHYSICAL-ENTITY");
					}
				}
			}
		} else {
			String id = BioPaxUtil.extractRdfId(bindingElement);

			if ((id != null) && (id.length() > 0)) {
				warningList.add("Warning!  Complex or Interaction: " + id + "has no participants");
			}
		}

		// outta here
		return null;
	}

	/**
	 * A helper function to set node attributes.
	 */
	private void setNodeAttributes(CyNode node, String name, String type, String id, String label) {
		String nodeID = node.getIdentifier();
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		//  Must set the Canonical Name;  otherwise the select node by
		// name feature will not work.
		if ((name != null) && (name.length() > 0)) {
			attributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, name);
		}

		if ((name != null) && (name.length() > 0)) {
			attributes.setAttribute(nodeID, MapNodeAttributes.BIOPAX_NAME, name);
		}

		if ((type != null) && (type.length() > 0)) {
			attributes.setAttribute(nodeID, MapNodeAttributes.BIOPAX_ENTITY_TYPE, type);
		}

		if ((id != null) && (id.length() > 0)) {
			attributes.setAttribute(nodeID, MapNodeAttributes.BIOPAX_RDF_ID, id);
		}

		if ((label != null) && (label.length() > 0)) {
			attributes.setAttribute(nodeID, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL, label);
		}
	}

	/**
	 * A helper function to set a multihashmap consisting of name - value pairs.
	 */
	private void setMultiHashMap(String cyNodeId, CyAttributes attributes, String attributeName,
	                             Map map) {
		// our key format
		final byte[] mhmKeyFormat = new byte[] { MultiHashMapDefinition.TYPE_STRING };

		// define multihashmap if necessary
		MultiHashMapDefinition mmapDefinition = attributes.getMultiHashMapDefinition();

		try {
			byte[] vals = mmapDefinition.getAttributeKeyspaceDimensionTypes(attributeName);
		} catch (IllegalStateException e) {
			// define the multihashmap attribute
			mmapDefinition.defineAttribute(attributeName, MultiHashMapDefinition.TYPE_STRING,
			                               mhmKeyFormat);
		}

		// add the map attributes
		MultiHashMap mhmap = attributes.getMultiHashMap();
		Set entrySet = map.entrySet();

		for (Iterator i = entrySet.iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			Object[] key = { (String) me.getKey() };
			Integer value = (Integer) me.getValue();
			mhmap.setAttributeValue(cyNodeId, attributeName, value.toString(), key);
		}
	}

	/**
	 * Convenience function to add values to createdCyNodes map.
	 *
	 * @param key String
	 * @param value String
	 */
	private void putCreatedCyNodes(String key, String value) {

		// get ref to value set or create new set
		Set<String> values = (createdCyNodes.containsKey(key)) ?
			createdCyNodes.get(key) : new HashSet<String>();

		// add new value to set
		values.add(value);

		// put set back into map
		createdCyNodes.put(key, values);
	}

	/**
	 * Convenience function to check for value existence
	 *
	 * @param value String
	 */
	private boolean createdCyNodesContainsValue(String value) {
		
		for (String key : createdCyNodes.keySet()) {
			Set<String> values = createdCyNodes.get(key);
			if (values == null) return false;
			if (values.contains(value)) return true;
		}

		// outta here
		return false;
	}
}
