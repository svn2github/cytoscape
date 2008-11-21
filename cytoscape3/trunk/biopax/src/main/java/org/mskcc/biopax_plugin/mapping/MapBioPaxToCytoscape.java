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

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.task.TaskMonitor;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.attributes.MultiHashMap;
import org.cytoscape.attributes.MultiHashMapDefinition;
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

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Maps a BioPAX Document to Cytoscape Nodes/Edges.
 *
 * @author Ethan Cerami.
 */
public class MapBioPaxToCytoscape {
	/**
	 * Cytoscape Attribute:  BioPAX Network.
	 * Stores boolean indicating this GraphPerspective
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
    private BioPaxNameUtil bpNameUtil;

    // created cynodes - cyNodeId is key, cpath id is value
	private Map<String, String> createdNodes;
	
	// complex cellular location wrapper - cyNodeId (of complex is key, NodeAttributesWrapper is value)
	private Map<String, NodeAttributesWrapper> complexCellularLocationWrapperMap;

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
		this.createdNodes = new HashMap<String,String>();
		this.complexCellularLocationWrapperMap = new HashMap<String, NodeAttributesWrapper>();
        this.bpNameUtil = new BioPaxNameUtil(rdfQuery);
    }

	/**
	 * Execute the Mapping.
	 *
	 * @throws JDOMException Error Parsing XML via JDOM.
	 */
	public void doMapping() throws JDOMException {

		// map interactions
		// note: this will now map complex nodes that participate in interactions.
		mapInteractionNodes();
		mapInteractionEdges();

		// process all complexes
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
	 * @param cyNetwork GraphPerspective Object.
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
	 * @param cyNetwork GraphPerspective Object.
	 */
	public static void repairNetworkName(final CyNetwork cyNetwork) {

		try {
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
		catch (java.util.NoSuchElementException e) {
			// network is empty, do nothing
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
			if (createdNodes.containsKey(id)) {
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
			name = bpNameUtil.getNodeName(name, interactionElement);

			//  set node attributes
			setNodeAttributes(interactionNode, name, interactionElement.getName(), id, null);

			// update our map
			createdNodes.put(id, id);

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
			} else if (name.equals(BioPaxConstants.PHYSICAL_INTERACTION) ||
					   name.equals(BioPaxConstants.INTERACTION)) {
				addPhysicalInteraction(interactionNode, interactionElement);
			}

			if (taskMonitor != null) {
				double perc = (double) i / interactionList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}

	/**
	 * Maps complex nodes to Cytoscape nodes.
	 */
	private void mapComplexNodes() throws JDOMException {
		//  Extract the List of all Physical Entities
		List physicalEntityList = bpUtil.getPhysicalEntityList();

		if (taskMonitor != null) {
			taskMonitor.setStatus("Adding Complexes");
			taskMonitor.setPercentCompleted(0);
		}

		//  Iterate through all Physical Entities
		for (int i = 0; i < physicalEntityList.size(); i++) {
			Element e = (Element) physicalEntityList.get(i);

			// we only map complexes
			if (!e.getName().equals(BioPaxConstants.COMPLEX)) {
				continue;
			}

			//  Extract ID
			String id = BioPaxUtil.extractRdfId(e);

			// have we already created this complex ?
			if (createdNodes.containsValue(id)) {
				continue;
			}

			//  Extract Name
			String name = id;
			name = bpNameUtil.getNodeName(name, e);

			//  Create New Node via getCyNode Method
			CyNode node = Cytoscape.getCyNode(id, true);

			//  Add New Node to Network
			nodeList.add(node);

			//  Set Node Identifier
			node.setIdentifier(id);

			//  Set BioPAX Name, Type, ID
			setNodeAttributes(node, name, e.getName(), id, null);

			// update our map
			createdNodes.put(id, id);

			if (taskMonitor != null) {
				double perc = (double) i / physicalEntityList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}

	private String truncateLongStr(String str) {
		if (str.length() > 25) {
			str = str.substring(0, 25) + "...";
		}

		return str;
	}

	/**
	 * Creates complexs nodes (for complexs outside of interactions).  
	 *  Maps complex edges for all complexes (attach members),
	 */
	private void mapComplexes() {

		// create complex edges/attach members for complexes that are part of interactions
		// (nodes created in mapInteractionNodes)
		ArrayList<Element> complexElementList = getComplexElementList();
		if (complexElementList.size() > 0) {
			do {
				complexElementList = mapComplexEdges(complexElementList);
			} while (complexElementList.size() > 0);
		}

		// now we need to process complexes that are not part of interactions
		complexElementList = getComplexElementList();
		ArrayList<Element> complexElementListClone = (ArrayList<Element>)(complexElementList.clone());
		Map<String, String> localCreatedNodes = (Map<String,String>)(((HashMap)createdNodes).clone());
		for (Element complexElement : complexElementListClone) {
			String complexCPathId = BioPaxUtil.extractRdfId(complexElement);
			if (localCreatedNodes.containsValue(complexCPathId)) {
				// a cynode for this complex has already een created, remove from complex element list
				complexElementList.remove(complexElement);
			}
			else {
				// a cynode has not been created for this complex, do it now
				getCyNode(complexElement, complexElement, BioPaxConstants.COMPLEX);
			}
		}
		if (complexElementList.size() > 0) {
			do {
				complexElementList = mapComplexEdges(complexElementList);
			} while (complexElementList.size() > 0);
		}
	}

	/**
	 * Interates over complexElementList and connects members.  This routine will
	 * modify and then return the complexElementList argument.  It removes complexes
	 * that get processed during this call, and adds members which are complexes themselves.
	 *
	 * @param complexElementList ArrayList<Element>
	 * @return ArrayList<Element>
	 */
	private ArrayList<Element> mapComplexEdges(ArrayList<Element> complexElementList) {

		// ref to node/edge attributes
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// need to clone the createdNodes map
		Map<String, String> localCreatedNodes = (Map<String,String>)(((HashMap)createdNodes).clone());

		// need to clone the complex ElementList
		ArrayList<Element> complexElementListClone = (ArrayList<Element>)(complexElementList.clone());

		// interate through all pe's
		for (Element complexElement : complexElementListClone) {

			// get source id
			String complexCPathId = BioPaxUtil.extractRdfId(complexElement);

			// iterate through all created nodes
			// note: a complex can occur multiple times in createdNodes map
			for (String cyNodeId : (Set<String>)localCreatedNodes.keySet()) {

				// is this a complex that maps to the current complex (complexElement) ?
				if (localCreatedNodes.get(cyNodeId).equals(complexCPathId)) {

					// get Cynode for this complexElement
					CyNode complexNode = Cytoscape.getCyNode(cyNodeId);
					//  get all components.  There can be 0 or more
					for (Element complexMemberElement : (List<Element>)rdfQuery.getNodes(complexElement, "COMPONENTS/*/PHYSICAL-ENTITY/*")) {
						CyNode complexMemberNode = getComplexNode(complexElement, complexNode.getIdentifier(), complexMemberElement);
						if (complexMemberNode != null) {
							// create edge, set attributes
							CyEdge edge = Cytoscape.getCyEdge(complexNode, complexMemberNode, Semantics.INTERACTION,
															CONTAINS, true);
							edgeAttributes.setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, CONTAINS);
							edgeList.add(edge);
							// if there was a complex, add it to complexElementList for processing next time
							if (complexMemberElement.getName().equals(BioPaxConstants.COMPLEX)) {
								complexElementList.add(complexMemberElement);
							}
						}
					}
				}
			}
			// remove the complex element we just processed
			complexElementList.remove(complexElement);
		}

		// outta here
		return complexElementList;
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
			CyNode nodeB = getCyNode(interactionElement, physicalEntity, type);

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
				CyEdge edge = Cytoscape.getCyEdge(interactionNode, controlledNode,
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
					                         BioPaxConstants.CONTROL);
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
	 * Returns a list of complex elements, derived from bpUtil.getPhysicalEntityList().
	 */
	private ArrayList<Element> getComplexElementList() {

		// list to return
		ArrayList<Element> toReturn = new ArrayList<Element>();

		for (Element complexElement : (List<Element>)bpUtil.getPhysicalEntityList()) {

			// only interested in complexes
			if (complexElement.getName().equals(BioPaxConstants.COMPLEX)) {
				toReturn.add(complexElement);
			}
		}

		// outta here
		return toReturn;
	}

	/**
	 * Creates required Nodes given a binding element (complex or interaction).
	 *
	 * @param bindingElement Element
	 * @param physicalEntity Element
	 * @param type           String
	 * @return Node
	 */
	private CyNode getCyNode(Element bindingElement, Element physicalEntity, String type) {

		// setup a few booleans used later on
		boolean isComplex = physicalEntity.getName().equals(BioPaxConstants.COMPLEX);
		boolean isInteraction = (physicalEntity.getName().equals(BioPaxConstants.COMPLEX_ASSEMBLY) ||
		                         bpConstants.isInteraction(physicalEntity.getName()));

		// extract id
		String id = BioPaxUtil.extractRdfId(physicalEntity);
		if ((id == null) || (id.length() == 0)) return null;

		// if we have an interaction, see if it was already created
		if (isInteraction && createdNodes.containsKey(id)) {
			return Cytoscape.getCyNode(id);
		}

		//  get node name
		String nodeName = bpNameUtil.getNodeName(id, physicalEntity);
		nodeName = (nodeName == null) ? id : nodeName;

		// create a node label & Node id
		String cyNodeId = new String(id);
		String cyNodeLabel = new String(truncateLongStr(nodeName));
		NodeAttributesWrapper cellularLocationsWrapper = null;
		NodeAttributesWrapper chemicalModificationsWrapper = null;

		if (!isInteraction) {

			// get chemical modification & cellular location attributes
			chemicalModificationsWrapper = getInteractionChemicalModifications(bindingElement,
			                                                                   physicalEntity, type);
			cellularLocationsWrapper = getInteractionCellularLocations(bindingElement,
			                                                           physicalEntity, type);

			// add modifications to id & label
			String modificationsString = getModificationsString(chemicalModificationsWrapper);
			cyNodeId += modificationsString;
			cyNodeLabel += modificationsString;

			// add cellular location str to node id & label
			String cellularLocationString = getCellularLocationString(cellularLocationsWrapper);
			cyNodeId += cellularLocationString;
			cyNodeLabel += (cellularLocationString.length() > 0) ? ("\n" + cellularLocationString) : "";
		}

		// have we seen this node before
		if (createdNodes.containsKey(cyNodeId)) {
			return Cytoscape.getCyNode(cyNodeId);
		}

		// haven't seen this node before, lets create a new one
		CyNode node = Cytoscape.getCyNode(cyNodeId, true);
		nodeList.add(node);
		node.setIdentifier(cyNodeId);

		//  set node attributes
		setNodeAttributes(node, nodeName, physicalEntity.getName(), id,
		                  (isInteraction || isComplex) ? null : cyNodeLabel);
		setChemicalModificationAttributes(cyNodeId, chemicalModificationsWrapper);
		setCellularLocationAttributes(cyNodeId, cellularLocationsWrapper);

		// if complex, save its cellular location wrapper -
		// may be inherited by complex members later on
		if (isComplex && cellularLocationsWrapper != null) {
			complexCellularLocationWrapperMap.put(cyNodeId, cellularLocationsWrapper);
		}

		// update our created nodes map
		createdNodes.put(cyNodeId, id);

		// outta here
		return node;
	}

	/**
	 * Gets complex member node.
	 *
	 * @param complexElement Element
	 * @param complexNodeId String
	 * @param complexMemberElement Element
	 * @return Node
	 */
	private CyNode getComplexNode(Element complexElement, String complexNodeId, Element complexMemberElement) {

		// extract id
		String complexMemberId = BioPaxUtil.extractRdfId(complexMemberElement);
		if ((complexMemberId == null) || (complexMemberId.length() == 0)) return null;

		// get node attributes
		NodeAttributesWrapper cellularLocationsWrapper =
			getInteractionCellularLocations(complexElement, complexMemberElement, BioPaxConstants.COMPLEX);
		NodeAttributesWrapper chemicalModificationsWrapper =
			getInteractionChemicalModifications(complexElement, complexMemberElement, BioPaxConstants.COMPLEX);

		// get node name
		String complexMemberNodeName = bpNameUtil.getNodeName(complexMemberId, complexMemberElement);
		complexMemberNodeName = (complexMemberNodeName == null) ? complexMemberId : complexMemberNodeName;

		// create node id & label strings
		String complexMemberNodeId = new String(complexMemberId);
		String complexMemberNodeLabel = new String(truncateLongStr(complexMemberNodeName));

		// add modifications to id & label
		// note: modifications do not get set on a complex, so if modifications string
		// is null, we do not try to inherit complex modifications
		String modificationsString = getModificationsString(chemicalModificationsWrapper);
		complexMemberNodeId += modificationsString;
		complexMemberNodeLabel += modificationsString;

		// add cellular location str to node id & label
		// if member cellular location string is null, attempt to inherit from complex
		String cellularLocationString = getCellularLocationString(cellularLocationsWrapper);
		if (cellularLocationString.length() == 0) {
			cellularLocationsWrapper = complexCellularLocationWrapperMap.get(complexNodeId);
			if (cellularLocationsWrapper != null) {
				cellularLocationString = getCellularLocationString(cellularLocationsWrapper);
			}
		}
		if (cellularLocationString.length() > 0) {
			complexMemberNodeId += cellularLocationString;
			complexMemberNodeLabel += "\n" + cellularLocationString;
		}

		// tack on complex id
		complexMemberNodeId += ("-" + complexNodeId);

		// have we seen this node before - this should not be the case
		if (createdNodes.containsKey(complexMemberNodeId)) {
			return Cytoscape.getCyNode(complexMemberNodeId);
		}

		// haven't seen this node before, lets create a new one
		CyNode complexMemberNode = Cytoscape.getCyNode(complexMemberNodeId, true);
		nodeList.add(complexMemberNode);
		complexMemberNode.setIdentifier(complexMemberNodeId);

		//  set node attributes
		String complexMemberElementType = complexMemberElement.getName();
		boolean isComplex = complexMemberElementType.equals(BioPaxConstants.COMPLEX);
		setNodeAttributes(complexMemberNode, complexMemberNodeName, complexMemberElementType,
						  complexMemberId, (isComplex) ? "" : complexMemberNodeLabel);
		setChemicalModificationAttributes(complexMemberNodeId, chemicalModificationsWrapper);
		setCellularLocationAttributes(complexMemberNodeId, cellularLocationsWrapper);

		// if complex, save its cellular location wrapper -
		// may be inherited by complex members later on
		if (isComplex && cellularLocationsWrapper != null) {
			complexCellularLocationWrapperMap.put(complexMemberNodeId, cellularLocationsWrapper);
		}

		// update our created nodes map
		createdNodes.put(complexMemberNodeId, complexMemberId);

		// outta here
		return complexMemberNode;
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
		
		String query = null;
		if (type == PARTICIPANT) {
			query = "PARTICIPANTS/*";
		}
		else if (type == BioPaxConstants.COMPLEX){
			query = "COMPONENTS/*";
		}
		else {
			query = type + "/*";
		}
		Element participantElement = getParticipantElement(bindingElement, physicalElement,
															   query);
		if (participantElement != null) {
			chemicalModificationList = rdfQuery.getNodes(participantElement,
														 "SEQUENCE-FEATURE-LIST/*/FEATURE-TYPE/*/TERM");
		}

		// short ciruit routine if empty list
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
					chemicalModificationsMap.put(modification, Integer.valueOf(1));
				} else {
					// we've seen this modification before, just update the count
					int count = ((Integer) chemicalModificationsMap.get(modification)).intValue();
					chemicalModificationsMap.put(modification, Integer.valueOf(count + 1));
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

		String query = null;
		if (type == PARTICIPANT) {
			query = "PARTICIPANTS/*";
		}
		else if (type == BioPaxConstants.COMPLEX) {
			query = "COMPONENTS/*";
		}
	    else {
			query = type + "/*";
		}
		Element participantElement = getParticipantElement(bindingElement, physicalElement,
															   query);
		if (participantElement != null) {
			cellularLocationList = rdfQuery.getNodes(participantElement,
													 "CELLULAR-LOCATION/*/TERM");
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
	 * A helper function to get post-translational modifications string.
	 */
	private String getModificationsString(NodeAttributesWrapper chemicalModificationsWrapper) {

		// check args
		if (chemicalModificationsWrapper == null) return "";

		// get chemical modifications
		String chemicalModification = (chemicalModificationsWrapper != null)
			? chemicalModificationsWrapper.getAbbreviationString()
			: null;

		// outta here
		return (((chemicalModification != null) && (chemicalModification.length() > 0))
				? chemicalModification : "");
	}

	/**
	 * A helper function to get the cellular location as a string.
	 */
	private String getCellularLocationString(NodeAttributesWrapper cellularLocationsWrapper) {

		// check args
		if (cellularLocationsWrapper == null) return "";

		// get cellular locations
		String cellularLocation = (cellularLocationsWrapper != null)
			? cellularLocationsWrapper.getAbbreviationString() : null;

		// outta here
		return ((cellularLocation != null) && (cellularLocation.length() > 0))
				? ("(" + cellularLocation + ")") : "";
	}

	/**
	 * A helper function to set common node attributes.
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
	 * Helper function to insert newline into cellular location 

	/**
	 * A helper function to set chemical modification attributes
	 */
	private void setChemicalModificationAttributes(String cyNodeId, NodeAttributesWrapper chemicalModificationsWrapper) {
		
		Map modificationsMap = (chemicalModificationsWrapper != null)
		                       ? chemicalModificationsWrapper.getMap() : null;

		if (modificationsMap != null) {

			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

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
			nodeAttributes.setListAttribute(cyNodeId,
			                            MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_LIST, list);

			//  Store Complete Map of Chemical Modifications --> # of Modifications
			setMultiHashMap(cyNodeId, nodeAttributes,
			                MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, modificationsMap);

			if (modificationsMap.containsKey(BioPaxConstants.PHOSPHORYLATION_SITE)) {
				nodeAttributes.setAttribute(cyNodeId, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
											BioPaxConstants.PROTEIN_PHOSPHORYLATED);
			}
		}
	}

	/**
	 * A helper function to set cellular location attributes.
	 */
	private void setCellularLocationAttributes(String cyNodeId, NodeAttributesWrapper cellularLocationsWrapper) {

		List cellularLocationsList = (cellularLocationsWrapper != null)
		                             ? cellularLocationsWrapper.getList() : null;

		if (cellularLocationsList != null) {
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			nodeAttributes.setListAttribute(cyNodeId, MapNodeAttributes.BIOPAX_CELLULAR_LOCATIONS,
											cellularLocationsList);
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


}
