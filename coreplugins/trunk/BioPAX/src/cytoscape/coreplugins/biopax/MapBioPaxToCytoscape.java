package cytoscape.coreplugins.biopax;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.BioPaxVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.ExternalLinkUtil;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.logger.CyLogger;

import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

import giny.model.Edge;
import giny.view.NodeView;

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

import ding.view.DNodeView;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;


/**
 * Maps a BioPAX Model to Cytoscape Nodes/Edges.
 *
 * @author Ethan Cerami, Igor Rodchenkov (re-factoring using PaxTools API)
 * 
 * TODO later, re-write from scratch using 'AbstractTraverser'
 */
public class MapBioPaxToCytoscape {
	
	public static final CyLogger log = CyLogger.getLogger(MapBioPaxToCytoscape.class);
	
	/**
	 * Cytoscape Attribute:  BioPAX Network.
	 * Stores boolean indicating this CyNetwork
	 * is a BioPAX network.
	 */
	public static final String BIOPAX_NETWORK = "BIOPAX_NETWORK";

	
    public final static String BINARY_NETWORK = "BINARY_NETWORK";
	
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
		
	
	/**
	 * Cytoscape Attribute:  BioPAX RDF ID.
	 */
	public static final String BIOPAX_RDF_ID = "biopax.rdf_id";

	/**
	 * BioPax Node Attribute: Entity TYPE
	 */
	public static final String BIOPAX_ENTITY_TYPE = "biopax.entity_type";

	/**
	 * BioPax Node Attribute: NAME
	 */
	public static final String BIOPAX_NAME = "biopax.name";

	/**
	 * BioPax Node Attribute: CHEMICAL_MODIFICATIONS_MAP
	 */
	public static final String BIOPAX_CHEMICAL_MODIFICATIONS_MAP
            = "biopax.chemical_modifications_map";

	/**
	 * BioPax Node Attribute: CHEMICAL_MODIFICATIONS_LIST
	 */
	public static final String BIOPAX_CHEMICAL_MODIFICATIONS_LIST
            = "biopax.chemical_modifications";

	/**
	 * BioPax Node Attribute: CELLULAR_LOCATION
	 */
	public static final String BIOPAX_CELLULAR_LOCATIONS = "biopax.cellular_location";

	/**
	 * BioPax Node Attribute: SHORT_NAME
	 */
	public static final String BIOPAX_SHORT_NAME = "biopax.short_name";

	/**
	 * BioPax Node Attribute:
	 */
	public static final String BIOPAX_SYNONYMS = "biopax.synonyms";

	/**
	 * BioPax Node Attribute: ORGANISM_NAME
	 */
	public static final String BIOPAX_ORGANISM_NAME = "biopax.organism_name";

	/**
	 * BioPax Node Attribute: COMMENT
	 */
	public static final String BIOPAX_COMMENT = "biopax.comment";

	/**
	 * BioPax Node Attribute: UNIFICATION_REFERENCES
	 */
	public static final String BIOPAX_UNIFICATION_REFERENCES = "biopax.unification_references";

	/**
	 * BioPax Node Attribute: RELATIONSHIP_REFERENCES
	 */
	public static final String BIOPAX_RELATIONSHIP_REFERENCES = "biopax.relationship_references";

	/**
	 * BioPax Node Attribute: PUBLICATION_REFERENCES
	 */
	public static final String BIOPAX_PUBLICATION_REFERENCES = "biopax.publication_references";

	/**
	 * BioPAX Node Attribute:  XREF_IDs.
	 */
	public static final String BIOPAX_XREF_IDS = "biopax.xref_ids";

	/**
	 * BioPAX Node Attribute:  BIOPAX_XREF_PREFIX.
	 */
	public static final String BIOPAX_XREF_PREFIX = "biopax.xref.";

    /**
	 * BioPax Node Attribute: AVAILABILITY
	 */
	public static final String BIOPAX_AVAILABILITY = "biopax.availability";

	/**
	 * BioPax Node Attribute: DATA_SOURCES
	 */
	public static final String BIOPAX_DATA_SOURCES = "biopax.data_sources";

	/**
	 * BioPax Node Attribute: IHOP_LINKS
	 */
	public static final String BIOPAX_IHOP_LINKS = "biopax.ihop_links";

	/**
	 * BioPax Node Attribute: PATHWAY_NAME
	 */
	public static final String BIOPAX_PATHWAY_NAME = "biopax.pathway_name";

	/**
	 * BioPax Node Attribute: AFFYMETRIX_REFERENCES
	 */
	public static final String BIOPAX_AFFYMETRIX_REFERENCES_LIST
            = "biopax.affymetrix_references_list";

	// custom node images (phosphorylation)
	private static final String PHOSPHORYLATION_GRAPHICS = "PHOSPHORYLATION_GRAPHICS";

	// strange, cannot get this to work with final keyword
	private static BufferedImage phosNode = null;
	private static BufferedImage phosNodeSelectedTop = null;
	private static BufferedImage phosNodeSelectedRight = null;
	private static BufferedImage phosNodeSelectedBottom = null;
	private static BufferedImage phosNodeSelectedLeft = null;
	
	static {
		try {
			phosNode = javax.imageio.ImageIO.read
                    (MapBioPaxToCytoscape.class.getResource("phos-node.jpg"));
			phosNodeSelectedTop = javax.imageio.ImageIO.read
                    (MapBioPaxToCytoscape.class.getResource("phos-node-selected-top.jpg"));
			phosNodeSelectedRight = javax.imageio.ImageIO.read
                    (MapBioPaxToCytoscape.class.getResource("phos-node-selected-right.jpg"));
			phosNodeSelectedBottom = javax.imageio.ImageIO.read
                    (MapBioPaxToCytoscape.class.getResource("phos-node-selected-bottom.jpg"));
			phosNodeSelectedLeft = javax.imageio.ImageIO.read
                    (MapBioPaxToCytoscape.class.getResource("phos-node-selected-left.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage[] customPhosGraphics = {
        phosNodeSelectedTop,
        phosNodeSelectedRight,
        phosNodeSelectedBottom,
        phosNodeSelectedLeft
    };

	
	private List<CyNode> nodeList = new ArrayList<CyNode>();
	private List<Edge> edgeList = new ArrayList<Edge>();
	private TaskMonitor taskMonitor;
    // created cynodes - cyNodeId is key, cpath id is value
	private Map<String, String> createdCyNodes;
	// complex cellular location map
	private Map<String, Set<String>> complexCellularLocationMap;

	/**
	 * Inner class to store a given nodes'
	 * chemical modification(s), etc.,
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
	public MapBioPaxToCytoscape(TaskMonitor taskMonitor) {
		this();
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Constructor.
	 *
	 * @param bpUtil BioPAX Utility Class.
	 */
	public MapBioPaxToCytoscape() {
		this.createdCyNodes = new HashMap<String,String>();
		this.complexCellularLocationMap = new HashMap<String, Set<String>>();
    }

	/**
	 * Execute the Mapping.
	 *
	 * @throws JDOMException Error Parsing XML via JDOM.
	 */
	public void doMapping(Model model)  {
		// map interactions
		// note: this will now map complex nodes that participate in interactions.
		mapInteractionNodes(model);
		
		mapInteractionEdges(model);

		// process all complexes
		mapComplexes(model);

		// map attributes
		mapTheRest(model, nodeList);				
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
	 * Maps Select Interactions to Cytoscape Nodes.
	 */
	private void mapInteractionNodes(Model model) {
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
			setBasicNodeAttributes(interactionNode, itr, null);

			// update our map
			createdCyNodes.put(id, id);

			if (taskMonitor != null) {
				double perc = (double) i++ / interactionList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mapInteractionEdges(Model model) {
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
			CyNode interactionNode = Cytoscape.getCyNode(id);

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
	private void mapComplexes(Model model) {
		// create complex edges/attach members for complexes that are part of interactions
		// (nodes created in mapInteractionNodes)
		Collection<BioPAXElement> complexElementList = 
			new HashSet<BioPAXElement>(BioPaxUtil.getObjects(model, complex.class, Complex.class));
		while (!complexElementList.isEmpty()) {
			mapComplexEdges(complexElementList);
		}

		// now we need to process complexes that are not part of interactions
		// clone the set as it is going to be modified
		complexElementList = 
			new HashSet<BioPAXElement>(BioPaxUtil.getObjects(model, complex.class, Complex.class));
		Map<String, String> localCreatedCyNodes = (Map<String,String>)(((HashMap)createdCyNodes).clone());
		for (BioPAXElement complexElement : BioPaxUtil.getObjects(model, complex.class, Complex.class)) {
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
		Set<BioPAXElement> complexElementListClone = new HashSet<BioPAXElement>(complexElementList);

		// interate through all pe's
		for (BioPAXElement complexElement : complexElementListClone) {

			// get source id
			String complexCPathId = BioPaxUtil.getLocalPartRdfId(complexElement);

			// iterate through all created nodes
			// note: a complex can occur multiple times in createdNodes map
			Set<String> ids = new HashSet<String>(createdCyNodes.keySet());
			for (String cyNodeId : ids) {

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
		
		if(participantElement instanceof physicalEntityParticipant)
		{
			physicalEntity pe = 
				((physicalEntityParticipant)participantElement).getPHYSICAL_ENTITY();
			if(pe != null) {
				linkNodes(interactionElement, nodeA, pe, type);
			}
		}
		
		// edge attributes
		CyAttributes attributes = Cytoscape.getEdgeAttributes();
		
		// Note: getCyNode also assigns cellular location attribute...
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

		// Get the Controlled Element
		// We assume there is only 1 or no controlled element
		Collection<?> controlledList = 
			BioPaxUtil.getValues(interactionElement,
				"CONTROLLED", "controlled");
		if (controlledList.size() > 1) 
		{
			log.warn("Warning!  Control Interaction: " + interactionId
					+ " has more than one CONTROLLED Element.");
		} 
		else if (controlledList != null && !controlledList.isEmpty()) 
		{
			BioPAXElement controlledElement = 
				(BioPAXElement) controlledList.iterator().next();
			String controlledId = 
				BioPaxUtil.getLocalPartRdfId(controlledElement);
			CyNode controlledNode = Cytoscape.getCyNode(controlledId);
			if (controlledNode != null) 
			{
			// Determine the BioPAX Edge Type
				String typeStr = CONTROLLED;
				Object cType = BioPaxUtil.getValue(interactionElement,
						"CONTROL-TYPE", "controlType");
				typeStr = (cType == null) ? typeStr : cType.toString();

				// Create Edge from Control Interaction Node to the
				// Controlled Node
				Edge edge = Cytoscape.getCyEdge(interactionNode,
						controlledNode, Semantics.INTERACTION, typeStr, true);
				Cytoscape.getEdgeAttributes().setAttribute(
						edge.getIdentifier(), BIOPAX_EDGE_TYPE, typeStr);
				edgeList.add(edge);
			} 
			else
			{
				log.warn("Cannot find node by 'controlled' id: " + controlledId);
			}
		} 
		else 
		{
			log.warn(interactionId + " has no CONTROLLED Elements");
		}

		// Create Edges from the Controller(s) to the
		// Control Interaction
		Collection<?> controllerList = 
			BioPaxUtil.getValues(interactionElement,
				"CONTROLLER", "controller");
		if (controllerList != null) {
			for (Object controllerElement : controllerList) {
				linkNodes(interactionElement, interactionNode,
						(BioPAXElement) controllerElement, CONTROLLER);
			}
		} else {
			log.warn(interactionId + " has no CONTROLLER Elements");
		}

		mapCoFactors(interactionElement);
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
			log.warn("Warning!  Control Interaction:  " + BioPaxUtil.getLocalPartRdfId(interactionElement)
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

		boolean isComplex = BioPaxUtil.isOneOfBiopaxClasses(bpe, 
				physicalEntity.class, PhysicalEntity.class);
		boolean isInteraction = BioPaxUtil.isOneOfBiopaxClasses(bpe, 
				interaction.class, Interaction.class);

		// extract id
		String id = BioPaxUtil.getLocalPartRdfId(bpe);
		if ((id == null) || (id.length() == 0)) return null; // this never happens

		if (createdCyNodes.containsKey(id)) {
			return Cytoscape.getCyNode(id);
		}

		// NEW node label & CyNode id
		String cyNodeId = id;
		String nodeName = BioPaxUtil.getNodeName(bpe);
		String cyNodeLabel = BioPaxUtil.truncateLongStr(nodeName);
		
		if(log.isDebugging()) {
			log.debug("label " + id + " as " + cyNodeLabel);
		}
		
		NodeAttributesWrapper chemicalModificationsWrapper = null;
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		Set<String> cellLocations = new HashSet<String>();
		
		if (!isInteraction) {
			// get chemical modification & cellular location attributes
			chemicalModificationsWrapper = 
				getInteractionChemicalModifications(bindingElement, bpe);
			// add modifications to id & label
			String modificationsString = getModificationsString(chemicalModificationsWrapper);
			//cyNodeId += modificationsString;
			cyNodeLabel += modificationsString;
			
			if(bpe instanceof physicalEntity) 
			{
				for(physicalEntityParticipant pep : ((physicalEntity)bpe).isPHYSICAL_ENTITYof()) 
				{
					Object location = 
						BioPaxUtil.getValue(pep, "CELLULAR-LOCATION", "cellularLocation");
					if (location != null) {
						cellLocations.add(location.toString()); 
					}
				}
			} 
			else 
			{
				Object location = 
					BioPaxUtil.getValue(bpe, "CELLULAR-LOCATION", "cellularLocation");
				if (location != null) {
					cellLocations.add(location.toString()); 
				}
			}

			// add cellular location to the node label (and id?)
			if(!cellLocations.isEmpty()) {
				Set<String> abbreviatedCLs = new HashSet<String>(cellLocations.size());
				for(String cl : cellLocations) {
					abbreviatedCLs.add(BioPaxUtil.getAbbrCellLocation(cl));
				}
				String cellularLocationString = 
					abbreviatedCLs.toString().replaceAll("\\[|\\]", "");
				//cyNodeId += cellularLocationString;
				cyNodeLabel += (cellularLocationString.length() > 0) 
					? ("\n" + cellularLocationString) : "";
			}
			
			// have we seen this node before
			if (createdCyNodes.containsKey(cyNodeId)) {
				return Cytoscape.getCyNode(cyNodeId);
			}	
			
			// if complex, add its cellular location, which may be inherited by members
			if (isComplex && !cellLocations.isEmpty()) {
				if(!complexCellularLocationMap.containsKey(cyNodeId)) {
					complexCellularLocationMap.put(cyNodeId, new HashSet<String>());
				}
				complexCellularLocationMap.get(cyNodeId).addAll(cellLocations);
			}
			
			if (!cellLocations.isEmpty()) {
				List<String>l = new ArrayList<String>();
				l.addAll(cellLocations);
				nodeAttributes.setListAttribute(cyNodeId, BIOPAX_CELLULAR_LOCATIONS, l);	
			}
		}

		// haven't seen this node before, lets create a new one
		CyNode node = Cytoscape.getCyNode(cyNodeId, true);
		nodeList.add(node);
		node.setIdentifier(cyNodeId);

		// set node attributes
		setBasicNodeAttributes(node, bpe, (isInteraction || isComplex) ? null : cyNodeLabel);
		setChemicalModificationAttributes(cyNodeId, chemicalModificationsWrapper);

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
		
		boolean isMemberComplex = 
			BioPaxUtil.isOneOfBiopaxClasses(complexMemberElement, 
					complex.class, Complex.class);
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		
		// get node name
		String complexMemberNodeName = BioPaxUtil.getNodeName(complexMemberElement);
		// create node id & label strings
		String complexMemberCyNodeId = complexMemberId;
		String complexMemberCyNodeLabel = BioPaxUtil.truncateLongStr(complexMemberNodeName);

		NodeAttributesWrapper chemicalModificationsWrapper =
			getInteractionChemicalModifications(complexElement, complexMemberElement);		
		// add modifications to id & label
		// note: modifications do not get set on a complex, so if modifications string
		// is null, we do not try to inherit complex modifications
		String modificationsString = getModificationsString(chemicalModificationsWrapper);
		//complexMemberCyNodeId += modificationsString;
		complexMemberCyNodeLabel += modificationsString;
		
		Set<String> parentLocations = new HashSet<String>();
		if(complexCellularLocationMap.containsKey(complexCyNodeId)) {
			parentLocations = complexCellularLocationMap.get(complexCyNodeId);
		} else {
			complexCellularLocationMap.put(complexCyNodeId, parentLocations);
		}
		
		if(complexMemberElement instanceof physicalEntity) // Level2 PEPs fix
		{
			for(physicalEntityParticipant pep : 
				((physicalEntity)complexMemberElement).isPHYSICAL_ENTITYof()) 
			{
				Object location = 
					BioPaxUtil.getValue(pep, "CELLULAR-LOCATION", "cellularLocation");
				if (location != null) {
					parentLocations.add(location.toString());
				}
			}
		} 
		else 
		{
			Object location = 
				BioPaxUtil.getValue(complexMemberElement, "CELLULAR-LOCATION", "cellularLocation");
			if (location != null) {
				parentLocations.add(location.toString());
			}
		}
					
		if (isMemberComplex) { // also save locations for members
			if (!complexCellularLocationMap.containsKey(complexMemberCyNodeId)) {
				complexCellularLocationMap.put(complexMemberCyNodeId, parentLocations);
			} else {
				complexCellularLocationMap.get(complexMemberCyNodeId).addAll(parentLocations);
			}
		}
		
	
		Set<String> abbreviatedCLs = new HashSet<String>(parentLocations.size());
		for(String cl : parentLocations) {
			abbreviatedCLs.add(BioPaxUtil.getAbbrCellLocation(cl));
		}
		String cellularLocationString = 
			abbreviatedCLs.toString().replaceAll("\\[|\\]", "");
		//complexMemberCyNodeId += cellularLocationString;
		complexMemberCyNodeLabel += "\n" + cellularLocationString;
		// tack on complex id
		complexMemberCyNodeId += ("-" + complexCyNodeId);
		
		// have we seen this node before - this should not be the case
		if (createdCyNodes.containsKey(complexMemberCyNodeId)) {
			return Cytoscape.getCyNode(complexMemberCyNodeId);
		}

		// save/set it at last
		List<String> allCellLocations = new ArrayList<String>(parentLocations);
		nodeAttributes.setListAttribute(complexMemberCyNodeId, 
				BIOPAX_CELLULAR_LOCATIONS, allCellLocations);	
		
		// haven't seen this node before, lets create a new one
		CyNode complexMemberCyNode = Cytoscape.getCyNode(complexMemberCyNodeId, true);
		nodeList.add(complexMemberCyNode);
		complexMemberCyNode.setIdentifier(complexMemberCyNodeId);

		setBasicNodeAttributes(complexMemberCyNode, complexMemberElement,
						  (isMemberComplex) ? "" : complexMemberCyNodeLabel);
		
		setChemicalModificationAttributes(complexMemberCyNodeId, chemicalModificationsWrapper);

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
	 * A helper function to set common node attributes.
	 */
	public static void setBasicNodeAttributes(CyNode node, BioPAXElement bpe, String label) {
		String nodeID = node.getIdentifier();
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		//  Must set the Canonical Name;  otherwise the select node by
		// name feature will not work.
		if(bpe == null) {
			attributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, "null");
			attributes.setAttribute(nodeID, 
					BIOPAX_ENTITY_TYPE, BioPaxUtil.NULL_ELEMENT_TYPE);
			return;
		}
		
		String name = BioPaxUtil.getNodeName(bpe) + "";
		attributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, name);

		attributes.setAttribute(nodeID, BIOPAX_NAME, name);
		
		attributes.setAttribute(nodeID, 
				BIOPAX_ENTITY_TYPE, BioPaxUtil.getType(bpe));
		
		if(bpe instanceof physicalEntityParticipant 
				&& ((physicalEntityParticipant)bpe).getPHYSICAL_ENTITY() != null) {
			attributes.setAttribute(nodeID, BIOPAX_RDF_ID, 
					((physicalEntityParticipant)bpe).getPHYSICAL_ENTITY().getRDFId());
		} else {
			attributes.setAttribute(nodeID, BIOPAX_RDF_ID, bpe.getRDFId());
		}
			
		if ((label != null) && (label.length() > 0)) {
			attributes.setAttribute(nodeID, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL, label);
		}
	}


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
			nodeAttributes.setListAttribute(cyNodeId, BIOPAX_CHEMICAL_MODIFICATIONS_LIST, list);

			//  Store Complete Map of Chemical Modifications --> # of Modifications
			setMultiHashMap(cyNodeId, nodeAttributes, BIOPAX_CHEMICAL_MODIFICATIONS_MAP, modificationsMap);

			if (modificationsMap.containsKey(BioPaxUtil.PHOSPHORYLATION_SITE)) {
				nodeAttributes.setAttribute(cyNodeId, BIOPAX_ENTITY_TYPE,
											BioPaxUtil.PROTEIN_PHOSPHORYLATED);
			}
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
	 * Maps BioPAX details to node attributes.
	 * This class is based on MapBioPaxToCytoscape.
	 *
	 * @param model   PaxTools BioPAX Model
	 * @param nodeList Nodes
	 */
	public static void mapTheRest(Model model, Collection<CyNode> nodeList) {
		log.setDebug(true);
		
		// get the node attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		initAttributes(nodeAttributes);

		for (CyNode node : nodeList) {
			// get node id
			String nodeID = node.getIdentifier();
			
			// get node element
			String biopaxID = nodeAttributes.getStringAttribute(nodeID, BIOPAX_RDF_ID);
			BioPAXElement resource = model.getByID(biopaxID);
			
            mapNodeAttribute(resource, model, nodeID);
        }
		
		log.setDebug(false);
	}

    /**
     * Maps Attributes for a Single Node.
     * @param resource          BioPAX Object.
     * @param model TODO
     * @param nodeId TODO
     * @param nodeAttributes    Node Attributes.
     */
    public static void mapNodeAttribute(BioPAXElement resource, Model model, String nodeID) {
        if (resource != null) {
            String stringRef;
            
            CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

            // type
            stringRef = addType(resource, nodeAttributes);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_ENTITY_TYPE, stringRef);
            }

            // short name
            stringRef = BioPaxUtil.getShortName(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_SHORT_NAME, stringRef);
            }

            // name
            stringRef = BioPaxUtil.getStandardName(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_NAME, stringRef);
            }

            // synonyms
            List<String> synList = new ArrayList<String>(BioPaxUtil.getSynonymList(resource));
            if (synList != null && !synList.isEmpty()) {
                nodeAttributes.setListAttribute(nodeID, BIOPAX_SYNONYMS, synList);
            }

            // organism
            stringRef = BioPaxUtil.getOrganismName(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_ORGANISM_NAME, stringRef);
            }

            // comment
            stringRef = BioPaxUtil.getComment(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_COMMENT, stringRef);
            }

            // unification references
            stringRef = addXRefs(BioPaxUtil.getUnificationXRefs(resource));
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_UNIFICATION_REFERENCES, stringRef);
            }

            // the following code should replace the old way to set
            // relationship references
            List<String> xrefList = getXRefList(resource, BIOPAX_AFFYMETRIX_REFERENCES_LIST);
            if ((xrefList != null) && !xrefList.isEmpty()) {
                nodeAttributes.setListAttribute(nodeID, BIOPAX_AFFYMETRIX_REFERENCES_LIST,
                                                xrefList);
            }

            // relationship references - old way
            stringRef = addXRefs(BioPaxUtil.getRelationshipXRefs(resource));
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_RELATIONSHIP_REFERENCES, stringRef);
            }

            // publication references
            stringRef = addPublicationXRefs(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_PUBLICATION_REFERENCES, stringRef);
            }

            // availability
            stringRef = BioPaxUtil.getAvailability(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_AVAILABILITY, stringRef);
            }

            // data sources
            stringRef = addDataSource(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_DATA_SOURCES, stringRef);
            }

            // ihop links
            stringRef = addIHOPLinks(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_IHOP_LINKS, stringRef);
            }

            // pathway name
            stringRef = BioPaxUtil.getParentPathwayName(resource, model)
            	.toString().replaceAll("\\]|\\[", "").trim();
            nodeAttributes.setAttribute(nodeID, BIOPAX_PATHWAY_NAME, stringRef);

            //  add all xref ids for global lookup
            List<ExternalLink> xList = BioPaxUtil.getAllXRefs(resource);
            List<String> idList = addXRefIds(xList);
            if (idList != null && !idList.isEmpty()) {
                nodeAttributes.setListAttribute(nodeID, BIOPAX_XREF_IDS, idList);
                for (ExternalLink link : xList) {
                    String key = BIOPAX_XREF_PREFIX + link.getDbName().toUpperCase();
                    //  Set individual XRefs;  Max of 1 per database.
                    String existingId = nodeAttributes.getStringAttribute(nodeID, key);
                    if (existingId == null) {
                        nodeAttributes.setAttribute(nodeID, key, link.getId());
                    }
                }
            }

            //  Optionally add Node Label
            String label = nodeAttributes.getStringAttribute
                    (nodeID, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
            if (label == null) {
                label = BioPaxUtil.getNodeName(resource);
                if (label != null) {
                    nodeAttributes.setAttribute(nodeID, 
                    		BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL,
                    		BioPaxUtil.truncateLongStr(label));
                }
            }
        }
    }

    /**
	 * Adds custom node shapes to BioPAX nodes.
	 *
	 * @param networkView CyNetworkView
	 */
	public static void customNodes(CyNetworkView networkView) {
		// grab node attributes
		CyNetwork cyNetwork = networkView.getNetwork();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// iterate through the nodes
		Iterator<CyNode> nodesIt = cyNetwork.nodesIterator();
		if (nodesIt.hasNext()) {
			// grab the node
			CyNode node = nodesIt.next();

			// get chemical modifications
			int count = 0;
			boolean isPhosphorylated = false;
			MultiHashMapDefinition mhmdef = nodeAttributes.getMultiHashMapDefinition();

			if (mhmdef.getAttributeValueType(BIOPAX_CHEMICAL_MODIFICATIONS_MAP) != -1) {
				MultiHashMap mhmap = nodeAttributes.getMultiHashMap();
				CountedIterator modsIt = mhmap.getAttributeKeyspan(node.getIdentifier(),
                               BIOPAX_CHEMICAL_MODIFICATIONS_MAP, null);

				// do we have phosphorylation ?
				while (modsIt.hasNext()) {
					String modification = (String) modsIt.next();

					if (modification.equals(BioPaxUtil.PHOSPHORYLATION_SITE)) {
						isPhosphorylated = true;

						Object[] key = { BioPaxUtil.PHOSPHORYLATION_SITE };
						String countStr = (String) mhmap.getAttributeValue(node.getIdentifier(),
                            BIOPAX_CHEMICAL_MODIFICATIONS_MAP, key);
						count = ((Integer) Integer.valueOf(countStr)).intValue();

						break;
					}
				}
			}

			// if phosphorylated, add custom node
			if (isPhosphorylated) {
				addCustomShapes(networkView, node, PHOSPHORYLATION_GRAPHICS, count);
			}
		}
	}

	/**
	 * Initializes attribute descriptions and user interaction flags.
	 */
	public static void initAttributes(CyAttributes nodeAttributes) {
		nodeAttributes.setAttributeDescription(BIOPAX_RDF_ID,
		                                       "The Resource Description Framework (RDF) Identifier.");
		nodeAttributes.setAttributeDescription(BIOPAX_ENTITY_TYPE,
                               "The BioPAX entity type.  "
                               + "For example, interactions could be of type:  "
                               + "physical interaction, control, conversion, etc.  "
                               + "Likewise, "
                               + "physical entities could be of type:  complex, DNA, "
                               + "RNA, protein or small molecule.");
		nodeAttributes.setAttributeDescription(BIOPAX_NAME,
		                                       "The preferred full name for this entity.");
		nodeAttributes.setAttributeDescription(BIOPAX_SHORT_NAME,
                               "The abbreviated name for this entity. Preferably a name that "
                               + "is short enough to be used in a visualization "
                               + "application to label a graphical element that "
                               + "represents this entity.");
		nodeAttributes.setAttributeDescription(BIOPAX_SYNONYMS,
                               "One or more synonyms for the name of this entity.  ");
		nodeAttributes.setAttributeDescription(BIOPAX_COMMENT, "Comments regarding this entity.  ");
		nodeAttributes.setAttributeDescription(BIOPAX_AVAILABILITY,
                               "Describes the availability of this data (e.g. a copyright "
                               + "statement).");
		nodeAttributes.setAttributeDescription(BIOPAX_ORGANISM_NAME,
                               "Organism name, e.g. Homo sapiens.");
		nodeAttributes.setAttributeDescription(BIOPAX_CELLULAR_LOCATIONS,
                               "A list of one or more cellular locations, e.g. 'cytoplasm'.  "
                               + "This attribute should reference a term in the "
                               + "Gene Ontology " + "Cellular Component ontology.");
		nodeAttributes.setAttributeDescription(BIOPAX_AFFYMETRIX_REFERENCES_LIST,
                               "A list of one or more Affymetrix probeset identifers "
                               + "associated with the entity.");
		nodeAttributes.setAttributeDescription(BIOPAX_CHEMICAL_MODIFICATIONS_LIST,
                               "A list of one or more chemical modifications "
                               + "associated with the entity.  For example:  "
                               + "phoshorylation, acetylation, etc.");
		nodeAttributes.setAttributeDescription(BIOPAX_DATA_SOURCES,
                               "Indicates the database source of the entity.");
		nodeAttributes.setAttributeDescription(BIOPAX_XREF_IDS,
                               "External reference IDs associated with this entity.  For example, "
                               + "a protein record may be annotated with UNIPROT or "
                               + "REFSeq accession numbers.");

        nodeAttributes.setUserVisible(BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL, true);
        nodeAttributes.setAttributeDescription(BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL,
                "BioPax Node Label.  Short label used to identify each node in the network.");

        //  Hide these attributes from the user, as they currently
		//  contain HTML, and don't make much sense within the default
		//  attribute browser.
		nodeAttributes.setUserVisible(BIOPAX_IHOP_LINKS, false);
		nodeAttributes.setUserVisible(BIOPAX_PATHWAY_NAME, false);
		nodeAttributes.setUserVisible(BIOPAX_PUBLICATION_REFERENCES, false);
		nodeAttributes.setUserVisible(BIOPAX_RELATIONSHIP_REFERENCES, false);
		nodeAttributes.setUserVisible(BIOPAX_UNIFICATION_REFERENCES, false);
		nodeAttributes.setUserVisible(BIOPAX_CHEMICAL_MODIFICATIONS_MAP, false);

		//  Make these attributes non-editable
		nodeAttributes.setUserEditable(BIOPAX_RDF_ID, false);
	}

	/**
	 * Based on given arguments, adds proper custom node shape to node.
	 */
	private static void addCustomShapes(CyNetworkView networkView, CyNode node, String shapeType,
	                                    int modificationCount) {
		// create refs to help views
		CyNetwork cyNetwork = networkView.getNetwork();
		NodeView nodeView = networkView.getNodeView(node);
		DNodeView dingNodeView = (DNodeView) nodeView;

		// remove existing custom nodes
		int numExistingCustomShapes = dingNodeView.getCustomGraphicCount();
		for (int lc = 0; lc < numExistingCustomShapes; lc++) {
			dingNodeView.removeCustomGraphic(0);
		}

		for (int lc = 0; lc < modificationCount; lc++) {
			// set image
			BufferedImage image = null;

			if (shapeType.equals(PHOSPHORYLATION_GRAPHICS)) {
				image = (cyNetwork.isSelected(node)) ? customPhosGraphics[lc] : phosNode;
			}

			// set rect
			Rectangle2D rect = getCustomShapeRect(image, lc);

			// create our texture paint
			Paint paint = null;

			try {
				paint = new java.awt.TexturePaint(image, rect);
			} catch (Exception exc) {
				paint = java.awt.Color.black;
			}

			// add the graphic
			dingNodeView.addCustomGraphic(rect, paint, lc);
		}
	}

	/**
	 * Based on given arguments, determines proper rectangle coordinates
	 * used to render custom node shape.
	 */
	private static Rectangle2D getCustomShapeRect(BufferedImage image, int modificationCount) {
		// our scale factor
		double scale = .1;
		final double[] startX = {
		                            0,
		                            
		(BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH * BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE) / 2,
		                            0,
		                            
		(-1 * BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH * BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE) / 2
		                        };

		final double[] startY = {
		                            (-1 * BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT * BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE) / 2,
		                            0,
		                            
		(BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT * BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE) / 2,
		                            0
		                        };

		// create and return rect
		return new java.awt.geom.Rectangle2D.Double(startX[modificationCount]
		                                            + ((-1 * (image.getWidth() / 2)) * scale),
		                                            startY[modificationCount]
		                                            + ((-1 * (image.getHeight() / 2)) * scale),
		                                            (double) image.getWidth() * scale,
		                                            (double) image.getHeight() * scale);
	}


	private static String addType(BioPAXElement bpe, CyAttributes nodeAttributes) {
		
		if(bpe instanceof physicalEntityParticipant) {
			return addType(((physicalEntityParticipant)bpe).getPHYSICAL_ENTITY(), nodeAttributes);
		}
		
		
		MultiHashMapDefinition mhmdef = nodeAttributes.getMultiHashMapDefinition();
		// first check if attribute exists
		if (mhmdef.getAttributeValueType(BIOPAX_CHEMICAL_MODIFICATIONS_MAP) != -1) {
			MultiHashMap mhmap = nodeAttributes.getMultiHashMap();
			CountedIterator modsIt = mhmap.getAttributeKeyspan(BioPaxUtil.getLocalPartRdfId(bpe),
			                                                   BIOPAX_CHEMICAL_MODIFICATIONS_MAP,
			                                                   null);
			while (modsIt.hasNext()) {
				String modification = (String) modsIt.next();
				if (modification.equals(BioPaxUtil.PHOSPHORYLATION_SITE)) {
					return BioPaxUtil.PROTEIN_PHOSPHORYLATED;
				}
			}
		}

		return BioPaxUtil.getType(bpe);
	}

	private static String addDataSource(BioPAXElement resource) {
		return BioPaxUtil.getDataSource(resource);
	}

	private static String addPublicationXRefs(BioPAXElement resource) {
		
		if( !(resource instanceof org.biopax.paxtools.model.level2.XReferrable)
			&& 
			!(resource instanceof org.biopax.paxtools.model.level3.XReferrable) 
		) {
			return null;
		}
		
		List<ExternalLink> pubList = BioPaxUtil.getPublicationXRefs(resource);

		if (!pubList.isEmpty()) {
			StringBuffer temp = new StringBuffer("<ul>");
			for (ExternalLink xl : pubList) {
				temp.append("<li>");
				if (xl.getAuthor() != null) {
					temp.append(xl.getAuthor() + " et al., ");
				}

				if (xl.getTitle() != null) {
					temp.append(xl.getTitle());
				}

				if (xl.getSource() != null) {
					temp.append(" (" + xl.getSource());

					if (xl.getYear() != null) {
						temp.append(", " + xl.getYear());
					}

					temp.append(")");
				}
				temp.append(ExternalLinkUtil.createLink(xl.getDbName(), xl.getId()));
				temp.append("</li>");
			}
			temp.append("</ul> ");
			return temp.toString();
		}

		return null;
	}

	private static String addXRefs(List<ExternalLink> xrefList) {
		if (!xrefList.isEmpty()) {
			StringBuffer temp = new StringBuffer();
			for (ExternalLink link : xrefList) {
                //  Ignore cPath Link.
                if (link.getDbName() != null && link.getDbName().equalsIgnoreCase("CPATH")) {
                    continue;
                }
                temp.append("<LI>- ");
				temp.append(ExternalLinkUtil.createLink(link.getDbName(), link.getId()));
                temp.append("</LI>");
			}
			return temp.toString();
		}

		return null;
	}

	private static List<String> addXRefIds(List<ExternalLink> xrefList) {
		List<String> idList = new ArrayList<String>();
		if ((xrefList != null) && !xrefList.isEmpty()) {
			for (ExternalLink link: xrefList) {
				idList.add(link.getDbName() + ":" + link.getId());
			}
		}
		return idList;
	}

	private static List<String> getXRefList(BioPAXElement bpe, String xrefType) {
		List<String> listToReturn = new ArrayList<String>();

		// get the xref list
		List<ExternalLink> list = BioPaxUtil.getRelationshipXRefs(bpe);
		// what type of xref are we interested in ?
		String type = null;
		if (xrefType.equals(BIOPAX_AFFYMETRIX_REFERENCES_LIST)) {
			type = "AFFYMETRIX";
		}

		if (!list.isEmpty()) {
			for (ExternalLink link : list) {
				if (link.getDbName().toUpperCase().startsWith(type)) {
					listToReturn.add(link.getId());
				}
			}
		}

		return listToReturn;
	}
	
	private static String addIHOPLinks(BioPAXElement bpe) {
		List<String> synList = new ArrayList<String>(BioPaxUtil.getSynonymList(bpe));
		List<ExternalLink> dbList = BioPaxUtil.getAllXRefs(bpe);

		if (!synList.isEmpty() || !dbList.isEmpty()) {
			String htmlLink = ExternalLinkUtil.createIHOPLink(bpe.getModelInterface().getSimpleName(),
					synList, dbList, BioPaxUtil.getOrganismTaxonomyId(bpe));
			if (htmlLink != null) {
				return ("- " + htmlLink);
			}
		}

		return null;
	}

}
