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
package cytoscape.coreplugins.biopax.mapping;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.coreplugins.biopax.style.BioPaxVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.logger.CyLogger;

import cytoscape.task.TaskMonitor;

import giny.model.Edge;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level2.complex;
import org.biopax.paxtools.model.level2.control;
import org.biopax.paxtools.model.level2.conversion;
import org.biopax.paxtools.model.level2.interaction;
import org.biopax.paxtools.model.level2.physicalEntity;
import org.biopax.paxtools.model.level2.physicalEntityParticipant;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.Control;
import org.biopax.paxtools.model.level3.Conversion;
import org.biopax.paxtools.model.level3.Interaction;
import org.biopax.paxtools.model.level3.PhysicalEntity;


import java.util.*;

import javax.swing.*;


/**
 * Maps a BioPAX Model to Cytoscape Nodes/Edges.
 *
 * @author Ethan Cerami.
 * @author Igor Rodchenkov (re-factoring using PaxTools API)
 * 
 * 
 * TODO This probably must be re-written from scratch - using PaxTools 'Traverser' class, so that all the properties will be mapped...
 */
public class MapBioPaxToCytoscape {
	
	private static final CyLogger log = CyLogger.getLogger(MapBioPaxToCytoscape.class);
	
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
		
	
	private Model model;
	private List<CyNode> nodeList = new ArrayList<CyNode>();
	private List<Edge> edgeList = new ArrayList<Edge>();
	private TaskMonitor taskMonitor;
	private List<String> warningList = new ArrayList<String>();

    // created cynodes - cyNodeId is key, cpath id is value
	private Map<String, String> createdCyNodes;
	
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
		private Map<String, Object> attributesMap;

		// abbreviations string
		private String abbreviationString;

		// contructor
		NodeAttributesWrapper(Map<String,Object> attributesMap, String abbreviationString) {
			this.attributesMap = attributesMap;
			this.abbreviationString = abbreviationString;
		}

		// gets the attributes map
		Map<String,Object> getMap() {
			return attributesMap;
		}

		// gets the attributes map as list
		List<String> getList() {
			return (attributesMap != null) ? new ArrayList<String>(attributesMap.keySet()) : null;
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
	public MapBioPaxToCytoscape(Model model, TaskMonitor taskMonitor) {
		this(model);
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Constructor.
	 *
	 * @param bpUtil BioPAX Utility Class.
	 */
	public MapBioPaxToCytoscape(Model model) {
		this.model = model;
		this.warningList = new ArrayList<String>();
		this.createdCyNodes = new HashMap<String,String>();
		this.complexCellularLocationWrapperMap = new HashMap<String, NodeAttributesWrapper>();
    }

	/**
	 * Execute the Mapping.
	 *
	 * @throws JDOMException Error Parsing XML via JDOM.
	 */
	public void doMapping()  {
		log.setDebug(true);
		// map interactions
		// note: this will now map complex nodes that participate in interactions.
		mapInteractionNodes();
		
		mapInteractionEdges();

		// process all complexes
		mapComplexes();

		// map attributes
		MapNodeAttributes.doMapping(model, nodeList);
		
		for(String mess : getWarningList()) {
			log.warn(mess);
		}
		
		log.setDebug(false);
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
	public List<String> getWarningList() {
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

		try {
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			Iterator iter = cyNetwork.nodesIterator();
			CyNode node = (CyNode) iter.next();

			if (node != null) {
				String pathwayName = 
					nodeAttributes.getStringAttribute(node.getIdentifier(), MapNodeAttributes.BIOPAX_PATHWAY_NAME);
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
	private void mapInteractionNodes() {
		//  Extract the List of all Interactions
		Collection<? extends BioPAXElement> interactionList = 
			BioPaxUtil.getObjects(model, interaction.class, Interaction.class);
		
		if (taskMonitor != null) {
			taskMonitor.setStatus("Adding Interactions");
			taskMonitor.setPercentCompleted(0);
		}

		int i=0; // progress counter
		for (BioPAXElement itr : interactionList) {
			String id = BioPaxUtil.getLocalPartRdfId(itr);
			
			if(log.isDebugging()) {
				log.debug("Mapping " + BioPaxUtil.getType(itr) + " node : " + id);
			}

			// have we already created this interaction ?
			if (createdCyNodes.containsKey(id)) {
				continue;
			}

			//  Create node symbolizing the interaction
			CyNode interactionNode = Cytoscape.getCyNode(id, true);

			//  Add New Interaction Node to Network
			nodeList.add(interactionNode);

			//  Set Node Identifier
			//interactionNode.setIdentifier(id);

			//  set node attributes
			setNodeAttributes(interactionNode, itr, null);

			// update our map
			createdCyNodes.put(id, id);

			if (taskMonitor != null) {
				double perc = (double) i++ / interactionList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mapInteractionEdges() {
		//  Extract the List of all Interactions
		Collection<? extends BioPAXElement> interactionList = 
			BioPaxUtil.getObjects(model, interaction.class, Interaction.class);

		if (taskMonitor != null) {
			taskMonitor.setStatus("Creating BioPAX Links");
			taskMonitor.setPercentCompleted(0);
		}

		int i = 0;
		for (BioPAXElement itr : interactionList) {
			String id = BioPaxUtil.getLocalPartRdfId(itr);

			
			if(log.isDebugging()) {
				log.debug("Mapping " + BioPaxUtil.getType(itr) + " edges : " + id);
			}
			
			//  Get the node symbolizing the interaction
			CyNode interactionNode = Cytoscape.getCyNode(id, true);

			if (BioPaxUtil.isOneOfBiopaxClasses(itr, conversion.class, Conversion.class)) {
				addConversionInteraction(interactionNode, itr);
			} else if (BioPaxUtil.isOneOfBiopaxClasses(itr, control.class, Control.class)) {
				addControlInteraction(itr);
			} else {
				addPhysicalInteraction(interactionNode, itr);
			}

			if (taskMonitor != null) {
				double perc = (double) i++ / interactionList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}

	/**
	 * Creates complexs nodes (for complexs outside of interactions).  
	 *  Maps complex edges for all complexes (attach members),
	 */
	private void mapComplexes() {
		// create complex edges/attach members for complexes that are part of interactions
		// (nodes created in mapInteractionNodes)
		Collection<BioPAXElement> complexElementList = (Collection<BioPAXElement>) 
			(((HashSet)BioPaxUtil.getObjects(model, complex.class, Complex.class)).clone());
			
		while (!complexElementList.isEmpty()) {
			mapComplexEdges(complexElementList);
		}

		// now we need to process complexes that are not part of interactions
		// clone the set as it is going to be modified
		Collection<BioPAXElement> complexElementListClone = 
			(Collection<BioPAXElement>) (((HashSet)complexElementList).clone());
		
		Map<String, String> localCreatedCyNodes = (Map<String,String>)(((HashMap)createdCyNodes).clone());
		for (BioPAXElement complexElement : complexElementListClone) {
			String complexCPathId = BioPaxUtil.getLocalPartRdfId(complexElement);
			if (localCreatedCyNodes.containsValue(complexCPathId)) {
				// a cynode for this complex has already been created, remove from complex element list
				complexElementList.remove(complexElement);
			}
			else {
				// a cynode has not been created for this complex, do it now
				getCyNode(complexElement, complexElement);
			}
		}
		
		while (!complexElementList.isEmpty()) {
			mapComplexEdges(complexElementList);
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
	//TODO CAREFULLY understand and re-write this  (it modifies sets in the loop!)
	private void mapComplexEdges(final Collection<BioPAXElement> complexElementList) {

		// ref to node/edge attributes
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// need to clone the complex ElementList
		Set<BioPAXElement> complexElementListClone = (Set<BioPAXElement>)(((HashSet)complexElementList).clone());

		// interate through all pe's
		for (BioPAXElement complexElement : complexElementListClone) {

			// get source id
			String complexCPathId = BioPaxUtil.getLocalPartRdfId(complexElement);

			// iterate through all created nodes
			// note: a complex can occur multiple times in createdNodes map
			for (String cyNodeId : (Set<String>)createdCyNodes.keySet()) {

				// is this a complex that maps to the current complex (complexElement) ?
				if (createdCyNodes.get(cyNodeId).equals(complexCPathId)) {

					// get Cynode for this complexElement
					CyNode complexCyNode = Cytoscape.getCyNode(cyNodeId);
					//  get all components.  There can be 0 or more
					
					for (Object complexMemberElement : BioPaxUtil
							.getValues(complexElement, "component","COMPONENTS")) {
						BioPAXElement member = (BioPAXElement) complexMemberElement;
						CyNode complexMemberCyNode = 
							getComplexCyNode(complexElement, complexCyNode.getIdentifier(), member); 
						if (complexMemberCyNode != null) {
							// create edge, set attributes
							Edge edge = Cytoscape.getCyEdge(complexCyNode, complexMemberCyNode, 
									Semantics.INTERACTION, CONTAINS, true);
							edgeAttributes.setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, CONTAINS);
							edgeList.add(edge);
							// if there was a complex, add it to complexElementList for processing next time
							if (BioPaxUtil.isOneOfBiopaxClasses(member, complex.class, Complex.class)) {
								complexElementList.add(member);
							}
						}
					}
				}
			}
			// remove the complex element we just processed
			complexElementList.remove(complexElement);
		}
	}

	/**
	 * Adds a Physical Interaction, such as a binding interaction between
	 * two proteins.
	 */
	private void addPhysicalInteraction(CyNode interactionNode, BioPAXElement interactionElement) {
		//  Add all Participants
		Collection<?> participantElements = 
			BioPaxUtil.getValues(interactionElement, "PARTICIPANTS", "participant");
		for (Object participantElement : participantElements) {
			linkNodes(interactionElement, interactionNode, (BioPAXElement) participantElement, PARTICIPANT);
		}
	}

	/**
	 * Adds a Conversion Interaction.
	 */
	private void addConversionInteraction(CyNode interactionNode, BioPAXElement interactionElement) {
		//  Add Left Side of Reaction
		Collection<?> leftSideElements = BioPaxUtil.getValues(interactionElement, LEFT, "left");
		for (Object leftElement: leftSideElements) {
			linkNodes(interactionElement, interactionNode, (BioPAXElement) leftElement, LEFT);
		}

		//  Add Right Side of Reaction
		Collection<?> rightSideElements = BioPaxUtil.getValues(interactionElement, RIGHT, "right");
		for (Object rightElement : rightSideElements) {
			linkNodes(interactionElement, interactionNode, (BioPAXElement) rightElement, RIGHT);
		}
	}

	/**
	 * Add Edges Between Interaction Node and Physical Entity Nodes.
	 *
	 */
	private void linkNodes(BioPAXElement interactionElement, CyNode nodeA, BioPAXElement participantElement, String type) 
	{
		// edge attributes
		CyAttributes attributes = Cytoscape.getEdgeAttributes();
		
		CyNode nodeB = getCyNode(interactionElement, participantElement);
		if (nodeB == null) {
			return;
		}

		CyEdge edge = null;
		if (type.equals(RIGHT) || type.equals(COFACTOR)
				|| type.equals(PARTICIPANT)) {
			edge = Cytoscape.getCyEdge(nodeA, nodeB, Semantics.INTERACTION,
					type, true);
		} else {
			edge = Cytoscape.getCyEdge(nodeB, nodeA, Semantics.INTERACTION,
					type, true);
		}

		attributes.setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, type);
		edgeList.add(edge);
	}

	/**
	 * Adds a BioPAX Control Interaction.
	 */
	private void addControlInteraction(BioPAXElement interactionElement) {
		//  Get the Interaction Node represented by this Interaction Element
		String interactionId = BioPaxUtil.getLocalPartRdfId(interactionElement);
		CyNode interactionNode = Cytoscape.getCyNode(interactionId);

		//  Get the Controlled Element
		//  We assume there is only 1 or no controlled element
		Collection<?> controlledList = 
			BioPaxUtil.getValues(interactionElement, "CONTROLLED", "controlled");
		
		if (controlledList.size() > 1) {
			warningList.add("Warning!  Control Interaction: " + interactionId
		                + "has more than one CONTROLLED " + "Element.");
		}
		
		BioPAXElement controlledElement = 
			(BioPAXElement) controlledList.iterator().next();
		
		String controlledId = BioPaxUtil.getLocalPartRdfId(controlledElement);
		CyNode controlledNode = Cytoscape.getCyNode(controlledId);

		if (controlledNode == null) {
			this.warningList.add(new String("Warning!  Cannot find:  "
					+ controlledId));
		} else {
			// Determine the BioPAX Edge Type
			String typeStr = CONTROLLED;
			Object cType = BioPaxUtil.getValue(interactionElement,
					"CONTROL-TYPE", "controlType");
			String controlType = (cType == null) ? null : cType.toString();

			// Create Edge from Control Interaction Node to the
			// Controlled Node
			Edge edge = Cytoscape.getCyEdge(interactionNode, controlledNode,
					Semantics.INTERACTION, typeStr, true);
			Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),
					BIOPAX_EDGE_TYPE, typeStr);
			edgeList.add(edge);

			// Create Edges from the Controller(s) to the
			// Control Interaction
			Collection<?> controllerList = BioPaxUtil.getValues(
					interactionElement, "CONTROLLER", "controller");
			for (Object controllerElement : controlledList) {
				linkNodes(interactionElement, interactionNode,
						(BioPAXElement) controllerElement, CONTROLLER);
			}

			mapCoFactors(interactionElement);
		}
	}

	/**
	 * Map All Co-Factors.
	 */
	private void mapCoFactors(BioPAXElement interactionElement) {
		Collection<?> coFactorList = BioPaxUtil.getValues(interactionElement, "COFACTOR","cofactor");

		if (coFactorList.size() == 1) {
			BioPAXElement coFactor = (BioPAXElement) coFactorList.iterator().next();
			if (coFactor!= null) {
				String coFactorId = BioPaxUtil.getLocalPartRdfId(coFactor);
				CyNode coFactorNode = Cytoscape.getCyNode(coFactorId);

				if (coFactorNode == null) {
					coFactorNode = getCyNode(interactionElement, coFactor);
				}

				//  Create Edges from the CoFactors to the Controllers
				Collection<?> controllerList = 
					BioPaxUtil.getValues(interactionElement, "CONTROLLER", "controller");
				for (Object controllerElement : controllerList) {
					linkNodes(interactionElement, coFactorNode, (BioPAXElement) controllerElement, COFACTOR);
				}
			} 
		} else if (coFactorList.size() > 1) {
			warningList.add("Warning!  Control Interaction:  " + BioPaxUtil.getLocalPartRdfId(interactionElement)
			                + " has more than one COFACTOR Element.  " + "I am not yet equipped "
			                + "to handle this.");
		}
	}

	/**
	 * Creates required CyNodes given a binding element (complex or interaction).
	 *
	 * @param bindingElement Element
	 * @param physicalEntity Element
	 * @return CyNode
	 */
	private CyNode getCyNode(BioPAXElement bindingElement, BioPAXElement bpe) {

		// setup a few booleans used later on
		boolean isComplex = BioPaxUtil.isOneOfBiopaxClasses(bpe, physicalEntity.class, PhysicalEntity.class);
		boolean isInteraction = BioPaxUtil.isOneOfBiopaxClasses(bpe, interaction.class, Interaction.class);

		// extract id
		String id = BioPaxUtil.getLocalPartRdfId(bpe);
		if ((id == null) || (id.length() == 0)) return null; // this should never happen

		if (createdCyNodes.containsKey(id)) {
			return Cytoscape.getCyNode(id);
		}

		// create a node label & CyNode id
		String cyNodeId = id;
		String nodeName = BioPaxUtil.getNodeName(bpe);
		String cyNodeLabel = BioPaxUtil.truncateLongStr(nodeName);
		
		if(log.isDebugging()) {
			log.debug("label " + id + " as " + cyNodeLabel);
		}
		
		NodeAttributesWrapper cellularLocationsWrapper = null;
		NodeAttributesWrapper chemicalModificationsWrapper = null;

		if (!isInteraction) {
			// get chemical modification & cellular location attributes
			chemicalModificationsWrapper = 
				getInteractionChemicalModifications(bindingElement, bpe);
			cellularLocationsWrapper = 
				getInteractionCellularLocations(bindingElement, bpe);

			// add modifications to id & label
			String modificationsString = getModificationsString(chemicalModificationsWrapper);
			cyNodeId += modificationsString;
			cyNodeLabel += modificationsString;

			// add cellular location str to node id & label
			String cellularLocationString = getCellularLocationString(cellularLocationsWrapper);
			cyNodeId += cellularLocationString;
			cyNodeLabel += (cellularLocationString.length() > 0) ? ("\n" + cellularLocationString) : "";
			
			// have we seen this node before
			if (createdCyNodes.containsKey(cyNodeId)) {
				return Cytoscape.getCyNode(cyNodeId);
			}	
		}

		// haven't seen this node before, lets create a new one
		CyNode node = Cytoscape.getCyNode(cyNodeId, true);
		nodeList.add(node);
		node.setIdentifier(cyNodeId);

		//  set node attributes
		setNodeAttributes(node, bpe, (isInteraction || isComplex) ? null : cyNodeLabel);
		setChemicalModificationAttributes(cyNodeId, chemicalModificationsWrapper);
		setCellularLocationAttributes(cyNodeId, cellularLocationsWrapper);

		// if complex, save its cellular location wrapper -
		// may be inherited by complex members later on
		if (isComplex && cellularLocationsWrapper != null) {
			complexCellularLocationWrapperMap.put(cyNodeId, cellularLocationsWrapper);
		}

		// update our created nodes map
		createdCyNodes.put(cyNodeId, id);

		return node;
	}

	/**
	 * Gets complex member node.
	 *
	 * @param complexElement BioPAX Element
	 * @param complexCyNodeId String
	 * @param complexMemberElement BioPAX Element
	 * @return CyNode
	 */
	private CyNode getComplexCyNode(BioPAXElement complexElement, String complexCyNodeId, BioPAXElement complexMemberElement) {

		// extract id
		String complexMemberId = BioPaxUtil.getLocalPartRdfId(complexMemberElement);
		if ((complexMemberId == null) || (complexMemberId.length() == 0)) return null;

		// get node attributes
		NodeAttributesWrapper cellularLocationsWrapper =
			getInteractionCellularLocations(complexElement, complexMemberElement);
		
		NodeAttributesWrapper chemicalModificationsWrapper =
			getInteractionChemicalModifications(complexElement, complexMemberElement);

		// get node name
		String complexMemberNodeName = BioPaxUtil.getNodeName(complexMemberElement);

		// create node id & label strings
		String complexMemberCyNodeId = complexMemberId;
		String complexMemberCyNodeLabel = BioPaxUtil.truncateLongStr(complexMemberNodeName);

		// add modifications to id & label
		// note: modifications do not get set on a complex, so if modifications string
		// is null, we do not try to inherit complex modifications
		String modificationsString = getModificationsString(chemicalModificationsWrapper);
		complexMemberCyNodeId += modificationsString;
		complexMemberCyNodeLabel += modificationsString;

		// add cellular location str to node id & label
		// if member cellular location string is null, attempt to inherit from complex
		String cellularLocationString = getCellularLocationString(cellularLocationsWrapper);
		if (cellularLocationString.length() == 0) {
			cellularLocationsWrapper = complexCellularLocationWrapperMap.get(complexCyNodeId);
			if (cellularLocationsWrapper != null) {
				cellularLocationString = getCellularLocationString(cellularLocationsWrapper);
			}
		}
		if (cellularLocationString.length() > 0) {
			complexMemberCyNodeId += cellularLocationString;
			complexMemberCyNodeLabel += "\n" + cellularLocationString;
		}

		// tack on complex id
		complexMemberCyNodeId += ("-" + complexCyNodeId);

		// have we seen this node before - this should not be the case
		if (createdCyNodes.containsKey(complexMemberCyNodeId)) {
			return Cytoscape.getCyNode(complexMemberCyNodeId);
		}

		// haven't seen this node before, lets create a new one
		CyNode complexMemberCyNode = Cytoscape.getCyNode(complexMemberCyNodeId, true);
		nodeList.add(complexMemberCyNode);
		complexMemberCyNode.setIdentifier(complexMemberCyNodeId);

		//  set node attributes
		boolean isComplex = 
			BioPaxUtil.isOneOfBiopaxClasses(complexMemberElement, complex.class, Complex.class);
		setNodeAttributes(complexMemberCyNode, complexMemberElement,
						  (isComplex) ? "" : complexMemberCyNodeLabel);
		setChemicalModificationAttributes(complexMemberCyNodeId, chemicalModificationsWrapper);
		setCellularLocationAttributes(complexMemberCyNodeId, cellularLocationsWrapper);

		// if complex, save its cellular location wrapper -
		// may be inherited by complex members later on
		if (isComplex && cellularLocationsWrapper != null) {
			complexCellularLocationWrapperMap.put(complexMemberCyNodeId, cellularLocationsWrapper);
		}

		// update our created nodes map
		createdCyNodes.put(complexMemberCyNodeId, complexMemberId);

		return complexMemberCyNode;
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
	private NodeAttributesWrapper getInteractionChemicalModifications(BioPAXElement bindingElement,
	                                                                  BioPAXElement participantElement) 
	{
		
		if(participantElement == null) {
			return null;
		}
		
		// both of these objects will be used to contruct
		// the NodeAttributesWrapper which gets returned
		Map<String,Object> chemicalModificationsMap = null;
		String chemicalModifications = null;

		// if we are dealing with PARTICIPANTS (physical interactions
		// or complexes), we have to through the participants to get the
		// proper chemical modifications
		Collection<?> modificationFeatures =
				BioPaxUtil.getValues(participantElement, "SEQUENCE-FEATURE-LIST", "feature", "notFeature");
		// short ciruit routine if empty list
		if (modificationFeatures == null) {
			return null;
		}

		// interate through the list returned from the query
		for (Object modification : modificationFeatures) {
			if (modification != null) {
				// initialize chemicalModifications string if necessary
				chemicalModifications = (chemicalModifications == null || chemicalModifications.length()==0) 
					? "-" : chemicalModifications;
				// initialize chemicalModifications hashmap if necessary
				chemicalModificationsMap = (chemicalModificationsMap == null) 
					? new HashMap<String, Object>() : chemicalModificationsMap;

				Object value = BioPaxUtil.getValue((BioPAXElement)modification, 
						"FEATURE_TYPE", "modificationType");
				String mod = (value == null) ? "" : value.toString();
				
				// is this a new type of modification ?
				if (!chemicalModificationsMap.containsKey(mod)) {
					// determine abbreviation
					String abbr = BioPaxUtil.getAbbrChemModification(mod);

					// add abreviation to modifications string
					// (the string "-P...")
					chemicalModifications += abbr;

					// update our map - modification, count
					chemicalModificationsMap.put(mod, new Integer(1));
				} else {
					// we've seen this modification before, just update the count
					Integer count = (Integer) chemicalModificationsMap.get(mod);
					chemicalModificationsMap.put(mod, ++count);
				}
			}
		}

		return new NodeAttributesWrapper(chemicalModificationsMap, chemicalModifications);
	}

	/**
	 * Given a binding element (complex or interaction)
	 * and type (like left or right),
	 * returns cellular location (abbreviated form).
	 *
	 * @param bindingElement  Element
	 * @param physicalElement Element
	 * @return NodeAttributesWrapper
	 */
	private NodeAttributesWrapper getInteractionCellularLocations(BioPAXElement bindingElement,
	                                                              BioPAXElement participantElement) 
	{
		// both of these objects will be used to contruct
		// the NodeAttributesWrapper which gets returned
		String cellularLocation = null;
		Map<String, Object> nodeAttributes = new HashMap<String, Object>();

		// if we are dealing with PARTICIPANTS (physical interactions
		// or complexes), we have to through the participants to get the
		// proper cellular location
		Object location = 
			BioPaxUtil.getValue(participantElement, "CELLULAR-LOCATION", "cellularLocation");
		if (location != null) {
			cellularLocation = BioPaxUtil.getAbbrCellLocation(location.toString());
			// add location to attributes list (we dont care about key)
			nodeAttributes.put(cellularLocation, cellularLocation);
		}

		return new NodeAttributesWrapper(nodeAttributes, cellularLocation);
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
	private void setNodeAttributes(CyNode node, BioPAXElement bpe, String label) {
		String nodeID = node.getIdentifier();
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		//  Must set the Canonical Name;  otherwise the select node by
		// name feature will not work.
		String name = BioPaxUtil.getNodeName(bpe);
		attributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, name);

		attributes.setAttribute(nodeID, MapNodeAttributes.BIOPAX_NAME, name);
		
		attributes.setAttribute(nodeID, 
				MapNodeAttributes.BIOPAX_ENTITY_TYPE, BioPaxUtil.getType(bpe));
		
		if(bpe instanceof physicalEntityParticipant 
				&& ((physicalEntityParticipant)bpe).getPHYSICAL_ENTITY() != null) {
			attributes.setAttribute(nodeID, 
					MapNodeAttributes.BIOPAX_RDF_ID, 
					((physicalEntityParticipant)bpe).getPHYSICAL_ENTITY().getRDFId());
		} else {
			attributes.setAttribute(nodeID, MapNodeAttributes.BIOPAX_RDF_ID, bpe.getRDFId());
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
		
		Map<String, Object> modificationsMap = (chemicalModificationsWrapper != null)
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
			List<String> list = new ArrayList<String>(modificationsMap.keySet());
			nodeAttributes.setListAttribute(cyNodeId,
			                            MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_LIST, list);

			//  Store Complete Map of Chemical Modifications --> # of Modifications
			setMultiHashMap(cyNodeId, nodeAttributes,
			                MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, modificationsMap);

			if (modificationsMap.containsKey(BioPaxUtil.PHOSPHORYLATION_SITE)) {
				nodeAttributes.setAttribute(cyNodeId, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
											BioPaxUtil.PROTEIN_PHOSPHORYLATED);
			}
		}
	}

	/**
	 * A helper function to set cellular location attributes.
	 */
	private void setCellularLocationAttributes(String cyNodeId, NodeAttributesWrapper cellularLocationsWrapper) {

		List<String> cellularLocationsList = (cellularLocationsWrapper != null)
		                             ? cellularLocationsWrapper.getList() : null;

		if (cellularLocationsList != null && !cellularLocationsList.isEmpty()) {
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
