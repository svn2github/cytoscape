package cytoscape.coreplugins.biopax;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.BioPaxVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.ExternalLink;
import cytoscape.coreplugins.biopax.util.ExternalLinkUtil;
import cytoscape.coreplugins.biopax.util.NodeAttributesWrapper;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.logger.CyLogger;

import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

import cytoscape.render.stateful.NodeDetails;
import cytoscape.render.stateful.CustomGraphic;

import giny.model.Edge;
import giny.view.NodeView;

import org.apache.commons.lang.StringUtils;
import org.biopax.paxtools.controller.AbstractTraverser;
import org.biopax.paxtools.controller.ObjectPropertyEditor;
import org.biopax.paxtools.controller.PropertyEditor;
import org.biopax.paxtools.controller.SimpleEditorMap;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Catalysis;
import org.biopax.paxtools.model.level3.CellularLocationVocabulary;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.Control;
import org.biopax.paxtools.model.level3.ControlType;
import org.biopax.paxtools.model.level3.ControlledVocabulary;
import org.biopax.paxtools.model.level3.Controller;
import org.biopax.paxtools.model.level3.Conversion;
import org.biopax.paxtools.model.level3.Entity;
import org.biopax.paxtools.model.level3.Interaction;
import org.biopax.paxtools.model.level3.Pathway;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.biopax.paxtools.model.level3.Process;
import org.biopax.paxtools.model.level3.PublicationXref;
import org.biopax.paxtools.model.level3.RelationshipTypeVocabulary;
import org.biopax.paxtools.model.level3.RelationshipXref;
import org.biopax.paxtools.model.level3.SimplePhysicalEntity;
import org.biopax.paxtools.model.level3.Stoichiometry;
import org.biopax.paxtools.model.level3.UnificationXref;
import org.biopax.paxtools.model.level3.XReferrable;
import org.biopax.paxtools.model.level3.Xref;
import org.biopax.paxtools.util.ClassFilterSet;
import org.biopax.paxtools.util.Filter;

import ding.view.DNodeView;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;


/**
 * Maps a BioPAX Model to Cytoscape Nodes/Edges.
 *
 * @author Ethan Cerami, Igor Rodchenkov (re-factoring using PaxTools API)
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
	 * Cytoscape Attribute:  BioPAX RDF ID.
	 */
	public static final String BIOPAX_RDF_ID = "URI";

	/**
	 * BioPax Node Attribute: Entity TYPE
	 */
	public static final String BIOPAX_ENTITY_TYPE = "BIOPAX_TYPE";


	/**
	 * BioPax Node Attribute: CHEMICAL_MODIFICATIONS_MAP
	 */
	public static final String BIOPAX_CHEMICAL_MODIFICATIONS_MAP = "CHEMICAL_MODIFICATIONS_MAP";

	/**
	 * BioPax Node Attribute: CHEMICAL_MODIFICATIONS_LIST
	 */
	public static final String BIOPAX_CHEMICAL_MODIFICATIONS_LIST = "CHEMICAL_MODIFICATIONS";

	/**
	 * BioPax Node Attribute: UNIFICATION_REFERENCES
	 */
	public static final String BIOPAX_UNIFICATION_REFERENCES = "UNIFICATION_REFERENCES";

	/**
	 * BioPax Node Attribute: RELATIONSHIP_REFERENCES
	 */
	public static final String BIOPAX_RELATIONSHIP_REFERENCES = "RELATIONSHIP_REFERENCES";

	/**
	 * BioPax Node Attribute: PUBLICATION_REFERENCES
	 */
	public static final String BIOPAX_PUBLICATION_REFERENCES = "PUBLICATION_REFERENCES";

	/**
	 * BioPAX Node Attribute:  XREF_IDs.
	 */
	public static final String BIOPAX_XREF_IDS = "IDENTIFIERS";
	
	
	/**
	 * BioPax Node Attribute: CELLULAR_LOCATION
	 */
	public static final String BIOPAX_CELLULAR_LOCATIONS = "CELLULAR_LOCATIONS";


	/**
	 * BioPax Node Attribute: IHOP_LINKS
	 */
	public static final String BIOPAX_IHOP_LINKS = "IHOP_LINKS";

	/**
	 * BioPax Node Attribute: AFFYMETRIX_REFERENCES
	 */
	public static final String BIOPAX_AFFYMETRIX_REFERENCES_LIST = "AFFYMETRIX_REFERENCES";

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
	
	//BioPAX element to CyNode map
	private final Map<BioPAXElement, CyNode> bpeToCyNodeMap;
	
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
		this.bpeToCyNodeMap = new HashMap<BioPAXElement, CyNode>();
    }

	/**
	 * Execute the Mapping.
	 */
	public void doMapping(Model model)  {
		initAttributes(Cytoscape.getNodeAttributes());

		createEntityNodes(model);
		
		createInteractionEdges(model);
		
		createComplexEdges(model);
		
		createMemberEdges(model);

	}

	
	private void createEntityNodes(Model model) {
		
		if (taskMonitor != null) {
			taskMonitor.setStatus("Creating CyNodes (the first pass)...");
			taskMonitor.setPercentCompleted(0);
		}
		
		int i = 0; //progress counter
		Set<Entity> entities = model.getObjects(Entity.class);
		for(Entity bpe: entities) {	
			// do not make nodes for top/main pathways
			if(bpe instanceof Pathway) {
				if(bpe.getParticipantOf().isEmpty()
					&& ((Process)bpe).getPathwayComponentOf().isEmpty())
					continue;
			}
			
			// generate node id form the BioPAX URI
			String id = BioPaxUtil.generateId(bpe);
			if(log.isDebugging())
				log.debug("Mapping " + BioPaxUtil.type(bpe) + " node : " + id);
			
			//  Create node symbolizing the interaction
			CyNode cyNode = Cytoscape.getCyNode(id, true);			
			nodeList.add(cyNode);			
			bpeToCyNodeMap.put(bpe, cyNode);
				           
			// traverse the model, making attributes from BioPAX properties
			mapNodeAttribute(bpe, model, id);
			
			// update progress bar
			if (taskMonitor != null) {
				double perc = (double) i++ / entities.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
		
		if(log.isDebugging())
			log.debug(nodeList.size() + " nodes created.");
	}
	

	private void createInteractionEdges(Model model) {
		//  Extract the List of all Interactions
		Collection<Interaction> interactionList = model.getObjects(Interaction.class);

		if (taskMonitor != null) {
			taskMonitor.setStatus("Creating edges...");
			taskMonitor.setPercentCompleted(0);
		}

		int i = 0;
		for (Interaction itr : interactionList) {	
			if(log.isDebugging()) {
				log.debug("Mapping " + itr.getModelInterface().getSimpleName() 
					+ " edges : " + itr.getRDFId());
			}
			
			if (itr instanceof Conversion) {
				addConversionInteraction((Conversion)itr);
			} else if (itr instanceof Control) {
				addControlInteraction((Control) itr);
			} else {
				addPhysicalInteraction(itr);
			}
			
			if (taskMonitor != null) {
				double perc = (double) i++ / interactionList.size();
				taskMonitor.setPercentCompleted((int) (100.0 * perc));
			}
		}
	}
	
	
	/**
	 * Adds a Conversion Interaction.
	 */
	private void addConversionInteraction(Conversion interactionElement) {
		//  Add Left Side of Reaction
		Collection<PhysicalEntity> leftSideElements = interactionElement.getLeft();
		for (PhysicalEntity leftElement: leftSideElements) {
			linkNodes(interactionElement, leftElement, "left");
		}

		//  Add Right Side of Reaction
		Collection<PhysicalEntity> rightSideElements = interactionElement.getRight();
		for (PhysicalEntity rightElement : rightSideElements) {
			linkNodes(interactionElement, rightElement, "right");
		}
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


	private void createMemberEdges(Model model) {
		// for each PE,
		for (PhysicalEntity par : model.getObjects(PhysicalEntity.class)) {
			Set<PhysicalEntity> members = par.getMemberPhysicalEntity();
			if(members.isEmpty()) 
				continue;
			
			CyNode cyParentNode = bpeToCyNodeMap.get(par);
			assert cyParentNode != null : "cyParentNode is NULL.";
			// for each its member PE, add the directed edge
			for (PhysicalEntity member : members) 
			{
				CyNode cyMemberNode = bpeToCyNodeMap.get(member);
				CyEdge edge = Cytoscape.getCyEdge(cyParentNode, cyMemberNode, Semantics.INTERACTION, "contains", true);
				Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, "member");
			}
		}
	}
	

	/*
	 * Iterates over complexElementList and connects members.  This routine will
	 * modify and then return the complexElementList argument.  It removes complexes
	 * that get processed during this call, and adds members which are complexes themselves.
	 */
	private void createComplexEdges(Model model) {
		// interate through all pe's
		for (Complex complexElement : model.getObjects(Complex.class)) {
			Set<PhysicalEntity> members = complexElement.getComponent();
			if(members.isEmpty()) 
				continue;

			// get node
			CyNode complexCyNode = bpeToCyNodeMap.get(complexElement);
			
			// get all components. There can be 0 or more
			for (PhysicalEntity member : members) 
			{
				CyNode complexMemberCyNode = bpeToCyNodeMap.get(member);
				// create edge, set attributes
				Edge edge = Cytoscape.getCyEdge(complexCyNode, complexMemberCyNode, 
						Semantics.INTERACTION, "contains", true);
				Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, "contains");
				edgeList.add(edge);
			}
		}
	}

	/*
	 * Adds a Physical Interaction, such as a binding interaction between
	 * two proteins.
	 */
	private void addPhysicalInteraction(Interaction interactionElement) {
		//  Add all Participants
		Collection<Entity> participantElements = interactionElement.getParticipant();
		for (Entity participantElement : participantElements) {
			linkNodes(interactionElement, (BioPAXElement) participantElement, "participant");
		}
	}


	/**
	 * Add Edges Between Interaction/Complex Node and Physical Entity Node.
	 *
	 */
	private void linkNodes(BioPAXElement bpeA, BioPAXElement bpeB, String type) 
	{	
		// Note: getCyNode also assigns cellular location attribute...
		CyNode nodeA = bpeToCyNodeMap.get(bpeA);
		if(nodeA == null) {
			log.debug("linkNodes: no node was created for " 
				+ bpeA.getModelInterface() + " " + bpeA.getRDFId());
			return; //e.g., we do not create any pathway nodes currently...
		}
		
		CyNode nodeB = bpeToCyNodeMap.get(bpeB);
		if(nodeB == null) {
			log.debug("linkNodes: no node was created for " 
					+ bpeB.getModelInterface() + " " + bpeB.getRDFId());
			return; //e.g., we do not create any pathway nodes currently...
		}
		
		CyEdge edge = null;
		if (type.equals("right") || type.equals("cofactor")
				|| type.equals("participant")) {
			edge = Cytoscape.getCyEdge(nodeA, nodeB, Semantics.INTERACTION,
					type, true);
		} else {
			edge = Cytoscape.getCyEdge(nodeB, nodeA, Semantics.INTERACTION,
					type, true);
		}

		Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, type);
		edgeList.add(edge);
	}

	/*
	 * Adds a BioPAX Control Interaction.
	 */
	private void addControlInteraction(Control control) {
		Collection<Process> controlledList = control.getControlled();		
		for (Process process : controlledList) {
			// Determine the BioPAX Edge Type
			String typeStr = "controlled"; //default
			ControlType cType = control.getControlType();
			typeStr = (cType == null) ? typeStr : cType.toString();
			//edge direction (trick) - from control to process (like for 'right', 'cofactor', 'participant')
			linkNodes(process, control, typeStr); 
		} 

		Collection<Controller> controllerList = control.getController();
		for (Controller controller : controllerList) {
			// directed edge - from Controller to Control (like 'left')
			linkNodes(control, controller, "controller");
		}

		// cofactor relationships
		if(control instanceof Catalysis) {
			Collection<PhysicalEntity> coFactorList = ((Catalysis) control).getCofactor();
			for(PhysicalEntity cofactor : coFactorList) {
				// direction - from control to cofactor (like 'right', 'participant', 'controlled')
				linkNodes(control, cofactor, "cofactor");
			}
		}	
	}
	

	/*
	 * A helper function to set chemical modification attributes
	 */
	private static void setChemicalModificationAttributes(String cyNodeId, NodeAttributesWrapper chemicalModificationsWrapper) {
		
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

	/*
	 * A helper function to set a multihashmap consisting of name - value pairs.
	 */
	private static void setMultiHashMap(String cyNodeId, CyAttributes attributes, String attributeName,
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
     * Maps Attributes for a Single Node.
     * 
     * @param element	BioPAX Object.
     * @param model	BioPAX Model
     * @param nodeId	{@link CyNode} identifier
     */
    public static void mapNodeAttribute(BioPAXElement element, Model model, final String nodeID) {
    	
        final CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    	
        if (element != null) {
        	Filter<PropertyEditor> filter = new Filter<PropertyEditor>() {
    			@Override
    			// skips for entity-range properties (which map to edges rather than attributes - not here though),
    			// and several utility classes ranges (for which we do not want generate attributes or do another way)
    			public boolean filter(PropertyEditor editor) {
    				if(editor instanceof ObjectPropertyEditor) {
    					Class c = editor.getRange();
    					String prop = editor.getProperty();
    					if( Entity.class.isAssignableFrom(c)
//    						|| "name".equals(prop) //display/standard name is enough
    						|| Stoichiometry.class.isAssignableFrom(c)
    						|| "nextStep".equals(prop) 
    						) {	
    						return false; 
    					}
    				} 
    				return true;
    			}
    		};
        	
            String stringRef = "";
        	@SuppressWarnings("unchecked")
			AbstractTraverser bpeAutoMapper = new AbstractTraverser(SimpleEditorMap.L3, filter) 
        	{
        		final CyLogger log = CyLogger.getLogger(AbstractTraverser.class);
        		
				@SuppressWarnings("rawtypes")
				@Override
				protected void visit(final Object obj, final BioPAXElement bpe, final Model model, final PropertyEditor editor) 
				{
					String attrName = getAttrName(getProps());
					
	            	// skip those that are either nodes/edges themselves or mapped separately
		            if (obj != null 
		            	&& !(
		            		obj instanceof Entity 
		            		|| obj instanceof Xref
		            		|| obj instanceof ControlledVocabulary 
		            		|| obj instanceof Stoichiometry
		            	)
		            ) {
		            	
		            	// bug fix: biopax.SequenceSite.SequencePosition = -2147483648 ('unknown value') if the site is empty; 2010.03.14
		            	String value = (editor.isUnknown(obj))? "" : obj.toString();
		            	
		            	if(log.isDebugging()) {
		            		log.debug("set attribute '" + attrName 
		            				+ "' for " + bpe + " = " 
		            				+ value);
		            	}
		            	
		                if(editor.isMultipleCardinality()) {
		                	final List<String> vals =  new ArrayList<String>();
		                	if(nodeAttributes.getAttributeNames().toString().contains(attrName)) {
		                		final List oldVals = nodeAttributes.getListAttribute(nodeID, attrName);
		                		if(oldVals != null) {
		                			for(final Object o : oldVals) {
		                				vals.add(o.toString());
		                			}
		                		}
		                	}
		                	
		                	if(value!= null && !"".equalsIgnoreCase(value.toString().replaceAll("\\]|\\[", ""))) 
		                	{
		                		vals.add(value);
		                	}
		                	
		                	if(!vals.isEmpty()) {
		                		nodeAttributes.setListAttribute(nodeID, attrName, vals);
		                	}
		                	
		                } else {
		                	//this strange thing may never happen...
		                	if(nodeAttributes.getAttributeNames().toString().contains(attrName)) {
		                		value += ", " + nodeAttributes.getStringAttribute(nodeID, attrName); 
		                	}
		                	nodeAttributes.setAttribute(nodeID, attrName, value);
		                }
		                
		                if(obj instanceof BioPAXElement) {
			            	traverse((BioPAXElement)obj, null);
		                }
		            }
				}
				
				private String getAttrName(Stack<String> props) {
					return StringUtils.join(props, "/");
				}
				
			};

			// set additional attributes
			
			// this one is important
			nodeAttributes.setAttribute(nodeID, BIOPAX_RDF_ID, element.getRDFId());

			String name = BioPaxUtil.truncateLongStr(BioPaxUtil.getNodeName(element) + "");
			
			nodeAttributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, name);
		
			nodeAttributes.setAttribute(nodeID, BIOPAX_ENTITY_TYPE, BioPaxUtil.type(element));

			// type
            stringRef = biopaxType(element, nodeAttributes);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_ENTITY_TYPE, stringRef);
            }
  
            
    		if (!(element instanceof Interaction)) {
    			// get chemical modification & cellular location attributes
    			NodeAttributesWrapper chemicalModificationsWrapper = getInteractionChemicalModifications(element);
    			// add modifications to the label/name
    			String modificationsString = getModificationsString(chemicalModificationsWrapper);
    			name += modificationsString;				
    			// add cellular location to the label/name
    			if(element instanceof PhysicalEntity) {
    				CellularLocationVocabulary cl = ((PhysicalEntity) element).getCellularLocation();
    				if(cl != null) {
    					String clAbbr = BioPaxUtil.getAbbrCellLocation(cl.toString())
    						.replaceAll("\\[|\\]", "");
    					name += (clAbbr.length() > 0) ? ("\n" + clAbbr) : "";
    				}
    			}
    			// set node attributes
    			setChemicalModificationAttributes(nodeID, chemicalModificationsWrapper);	
    		}
    		// update the name (also used for node's label and quick find)
    		
    		nodeAttributes.setAttribute(nodeID, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL, name);
                 
            bpeAutoMapper.traverse(element, null);      
            
            // create custom (convenience?) attributes, mainly - from xrefs
    		createExtraXrefAttributes(element, nodeID);

        } else {
			nodeAttributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, "null");
			nodeAttributes.setAttribute(nodeID, BIOPAX_ENTITY_TYPE, BioPaxUtil.NULL_ELEMENT_TYPE);
        }
    }
    
    private static void createExtraXrefAttributes(BioPAXElement element, String nodeID) {
    	
    	CyNode node = Cytoscape.getCyNode(nodeID);
    	CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    	
        // the following code should replace the old way to set
        // relationship references
        List<String> xrefList = getXRefList(element, BIOPAX_AFFYMETRIX_REFERENCES_LIST);
        if ((xrefList != null) && !xrefList.isEmpty()) {
            nodeAttributes.setListAttribute(nodeID, BIOPAX_AFFYMETRIX_REFERENCES_LIST,
                                            xrefList);
        }
        
		// ihop links
		String stringRef = ihopLinks(element);
		if (stringRef != null) {
			nodeAttributes.setAttribute(nodeID, BIOPAX_IHOP_LINKS, stringRef);
		}

		List<String> allxList = new ArrayList<String>();
		List<String> unifxfList = new ArrayList<String>();
		List<String> relxList = new ArrayList<String>();
		List<String> pubxList = new ArrayList<String>();
		// add xref ids per database and per xref class
		List<Xref> xList = BioPaxUtil.getXRefs(element, Xref.class);
		for (Xref link : xList) {
			if(link.getDb() == null)
				continue; // too bad (data issue...); skip it
			
			// per db -
			String key = "ID_" + link.getDb().toUpperCase();
			// Set individual XRefs; Max of 1 per database.
			String existingId = nodeAttributes.getStringAttribute(nodeID, key);
			if (existingId == null) {
				nodeAttributes.setAttribute(nodeID, key, link.getId());
			}
			
			StringBuffer temp = new StringBuffer();
			
			if(!"CPATH".equalsIgnoreCase(link.getDb()))
				temp.append(ExternalLinkUtil.createLink(link.getDb(), link.getId()));
			else
				temp.append(link.toString());
			
			if(link instanceof UnificationXref) {
				unifxfList.add(temp.toString());
			}
			else if(link instanceof PublicationXref) {
				PublicationXref xl = (PublicationXref) link;
				temp.append(" ");
				if (!xl.getAuthor().isEmpty()) {
					temp.append(xl.getAuthor().toString() + " et al., ");
				}
				if (xl.getTitle() != null) {
					temp.append(xl.getTitle());
				}
				if (!xl.getSource().isEmpty()) {
					temp.append(" (" + xl.getSource().toString());
					if (xl.getYear() > 0) {
						temp.append(", " + xl.getYear());
					}
					temp.append(")");
				}
				pubxList.add(temp.toString());
			}
			else if(link instanceof RelationshipXref) {
				relxList.add(temp.toString());
			}
			
			allxList.add(link.toString());
		}
		
		nodeAttributes.setListAttribute(nodeID, BIOPAX_XREF_IDS, allxList);
		nodeAttributes.setListAttribute(nodeID, BIOPAX_UNIFICATION_REFERENCES, unifxfList);
		nodeAttributes.setListAttribute(nodeID, BIOPAX_RELATIONSHIP_REFERENCES, relxList);
		nodeAttributes.setListAttribute(nodeID, BIOPAX_PUBLICATION_REFERENCES, pubxList);
	
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
		@SuppressWarnings("unchecked")
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

					if (modification.equalsIgnoreCase(BioPaxUtil.PHOSPHORYLATION_SITE)) {
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
		nodeAttributes.setUserVisible(BIOPAX_PUBLICATION_REFERENCES, false);
		nodeAttributes.setUserVisible(BIOPAX_RELATIONSHIP_REFERENCES, false);
		nodeAttributes.setUserVisible(BIOPAX_UNIFICATION_REFERENCES, false);
		nodeAttributes.setUserVisible(BIOPAX_CHEMICAL_MODIFICATIONS_MAP, false);
		nodeAttributes.setUserVisible(BIOPAX_CELLULAR_LOCATIONS, false);
	
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
		Iterator<CustomGraphic> it = dingNodeView.customGraphicIterator();
		while ( it.hasNext() ) {
			dingNodeView.removeCustomGraphic( it.next() );
		}

		for (int lc = 0; lc < modificationCount; lc++) {
			// set image
			BufferedImage image = null;

			if (shapeType.equalsIgnoreCase(PHOSPHORYLATION_GRAPHICS)) {
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
			dingNodeView.addCustomGraphic(rect, paint, NodeDetails.ANCHOR_CENTER);
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


	private static String biopaxType(BioPAXElement bpe, CyAttributes nodeAttributes) {
		MultiHashMapDefinition mhmdef = nodeAttributes.getMultiHashMapDefinition();
		// first check if attribute exists
		if (mhmdef.getAttributeValueType(BIOPAX_CHEMICAL_MODIFICATIONS_MAP) != -1) {
			MultiHashMap mhmap = nodeAttributes.getMultiHashMap();
			CountedIterator modsIt = mhmap.getAttributeKeyspan(BioPaxUtil.generateId(bpe),
			                                                   BIOPAX_CHEMICAL_MODIFICATIONS_MAP,
			                                                   null);
			while (modsIt.hasNext()) {
				String modification = (String) modsIt.next();
				if (modification.equalsIgnoreCase(BioPaxUtil.PHOSPHORYLATION_SITE)) {
					return BioPaxUtil.PROTEIN_PHOSPHORYLATED;
				}
			}
		}

		return BioPaxUtil.type(bpe);
	}

	private static List<String> getXRefList(BioPAXElement bpe, String xrefType) {
		List<String> listToReturn = new ArrayList<String>();

		// get the xref list
		List<ExternalLink> list = xrefToExternalLinks(bpe, RelationshipXref.class);
		// what type of xref are we interested in ?
		String type = null;
		if (xrefType.equalsIgnoreCase(BIOPAX_AFFYMETRIX_REFERENCES_LIST)) {
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
	
	private static String ihopLinks(BioPAXElement bpe) {
		List<String> synList = new ArrayList<String>(BioPaxUtil.getSynonyms(bpe));
		List<ExternalLink> dbList = xrefToExternalLinks(bpe, Xref.class);
		String htmlLink = null;
		
		if (!synList.isEmpty() || !dbList.isEmpty()) {
			htmlLink = ExternalLinkUtil.createIHOPLink(bpe.getModelInterface().getSimpleName(),
					synList, dbList, BioPaxUtil.getOrganismTaxonomyId(bpe));
		}

		return htmlLink;
	}
	
	
	private static <T extends Xref> List<ExternalLink> xrefToExternalLinks(BioPAXElement bpe, Class<T> xrefClass) {
		
		if(bpe instanceof XReferrable) {
			List<ExternalLink> erefs = new ArrayList<ExternalLink>();
			erefs.addAll(extractXrefs(new ClassFilterSet<Xref,T>(
				((XReferrable)bpe).getXref(), xrefClass) ));
			if(bpe instanceof SimplePhysicalEntity && 
				((SimplePhysicalEntity)bpe).getEntityReference() != null)
			{
				erefs.addAll(extractXrefs(new ClassFilterSet<Xref,T>(
					((SimplePhysicalEntity)bpe).getEntityReference().getXref(), xrefClass) ));
			}
			return erefs;
		}
		return new ArrayList<ExternalLink>();
	}

	
	private static List<ExternalLink> extractXrefs(Collection<? extends Xref> xrefs) {
		List<ExternalLink> dbList = new ArrayList<ExternalLink>();

		for (Xref x: xrefs) {		
			String db = null;
			String id = null;
			String relType = null;
			String title = null;
			String year = null;
			String author = null;
			String url = null;
			String source = null;
			
			db = x.getDb();
			String ver = x.getIdVersion();
			id = x.getId(); // + ((ver!=null) ? "_" + ver : "");
			if(x instanceof RelationshipXref) {
				RelationshipTypeVocabulary v = ((RelationshipXref)x).getRelationshipType();
				if(v != null) relType = v.getTerm().toString();
			}
			if(x instanceof PublicationXref) {
				PublicationXref px = (PublicationXref)x;
				author = px.getAuthor().toString();
				title = px.getTitle();
				source = px.getSource().toString();
				url =px.getUrl().toString();
				year = px.getYear() + "";
			}

			if ((db != null) && (id != null)) {
				ExternalLink link = new ExternalLink(db, id);
				link.setAuthor(author);
				link.setRelType(relType);
				link.setTitle(title);
				link.setYear(year);
				link.setSource(source);
				link.setUrl(url);
				dbList.add(link);
			}
		}

		return dbList;
	}	
	
	
	/*
	 * Given a binding element (complex or interaction)
	 * and type (like left or right),
	 * returns chemical modification (abbreviated form).
	 *
	 */
	private static NodeAttributesWrapper getInteractionChemicalModifications(BioPAXElement participantElement) 
	{
		
		if(participantElement == null) {
			return null;
		}
		
		// both of these objects will be used to contruct
		// the NodeAttributesWrapper which gets returned
		Map<String,Object> chemicalModificationsMap = null;
		String chemicalModifications = null;

		// if we are dealing with participant processes (interactions
		// or complexes), we have to go through the participants to get the
		// proper chemical modifications
		Collection<?> modificationFeatures =
				BioPaxUtil.getValues(participantElement, "feature", "notFeature");
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

				Object value = BioPaxUtil.getValue((BioPAXElement)modification, "modificationType");
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
	
	/*
	 * A helper function to get post-translational modifications string.
	 */
	private static String getModificationsString(NodeAttributesWrapper chemicalModificationsWrapper) 
	{

		// check args
		if (chemicalModificationsWrapper == null) return "";

		// get chemical modifications
		String chemicalModification = (chemicalModificationsWrapper != null)
			? chemicalModificationsWrapper.getAbbreviationString()
			: null;

		return (((chemicalModification != null) && (chemicalModification.length() > 0))
				? chemicalModification : "");
	}
}
