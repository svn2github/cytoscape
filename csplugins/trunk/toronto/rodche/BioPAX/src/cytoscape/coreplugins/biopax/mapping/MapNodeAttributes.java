// $Id: MapNodeAttributes.java,v 1.17 2006/08/29 16:50:43 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.style.BioPaxVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.links.ExternalLink;
import cytoscape.coreplugins.biopax.util.links.ExternalLinkUtil;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.view.CyNetworkView;

import ding.view.DNodeView;

import giny.view.NodeView;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;


import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * BioPAX Details Panel.
 *
 * @author Ethan Cerami.
 * @author Igor Rodchenkov (re-factoring, using PaxTools API)
 */
public class MapNodeAttributes {
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
                    (MapNodeAttributes.class.getResource("resources/phos-node.jpg"));
			phosNodeSelectedTop = javax.imageio.ImageIO.read
                    (MapNodeAttributes.class.getResource("resources/phos-node-selected-top.jpg"));
			phosNodeSelectedRight = javax.imageio.ImageIO.read
                    (MapNodeAttributes.class.getResource("resources/phos-node-selected-right.jpg"));
			phosNodeSelectedBottom = javax.imageio.ImageIO.read
                    (MapNodeAttributes.class.getResource("resources/phos-node-selected-bottom.jpg"));
			phosNodeSelectedLeft = javax.imageio.ImageIO.read
                    (MapNodeAttributes.class.getResource("resources/phos-node-selected-left.jpg"));
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

	/**
	 * Maps BioPAX details to node attributes.
	 * This class is based on MapBioPaxToCytoscape.
	 *
	 * @param model   PaxTools BioPAX Model
	 * @param nodeList Nodes
	 */
	public static void doMapping(Model model, Collection<CyNode> nodeList) {
		// get the node attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		initAttributes(nodeAttributes);

		for (CyNode node : nodeList) {
			// get node id
			String nodeID = node.getIdentifier();
			
			// get node element
			String biopaxID = nodeAttributes.getStringAttribute(nodeID, BIOPAX_RDF_ID);
			BioPAXElement resource = model.getByID(biopaxID);
			
            mapNodeAttribute(resource, nodeAttributes);
        }
	}

    /**
     * Maps Attributes for a Single Node.
     * @param resource          BioPAX Object.
     * @param nodeAttributes    Node Attributes.
     */
    public static void mapNodeAttribute(BioPAXElement resource, CyAttributes nodeAttributes) {
        if (resource != null) {
            String stringRef;
            String nodeID = resource.getRDFId();

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
            if (synList != null) {
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
            stringRef = BioPaxUtil.getParentPathwayName(resource);
            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_PATHWAY_NAME, stringRef);
            }

            //  add all xref ids for global lookup
            List<ExternalLink> xList = BioPaxUtil.getAllXRefs(resource);
            List<String> idList = addXRefIds(xList);
            if (idList != null) {
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
                    //  Truncate long labels.
                    if (label.length() > 25) {
                        label = label.substring(0, 25) + "...";
                    }
                    nodeAttributes.setAttribute(nodeID, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL,
                            label);
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
                               MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, null);

				// do we have phosphorylation ?
				while (modsIt.hasNext()) {
					String modification = (String) modsIt.next();

					if (modification.equals(BioPaxUtil.PHOSPHORYLATION_SITE)) {
						isPhosphorylated = true;

						Object[] key = { BioPaxUtil.PHOSPHORYLATION_SITE };
						String countStr = (String) mhmap.getAttributeValue(node.getIdentifier(),
                            MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, key);
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
		MultiHashMapDefinition mhmdef = nodeAttributes.getMultiHashMapDefinition();

		// first check if attribute exists
		if (mhmdef.getAttributeValueType(BIOPAX_CHEMICAL_MODIFICATIONS_MAP) != -1) {
			MultiHashMap mhmap = nodeAttributes.getMultiHashMap();
			CountedIterator modsIt = mhmap.getAttributeKeyspan(bpe.getRDFId(),
			                                                   BIOPAX_CHEMICAL_MODIFICATIONS_MAP,
			                                                   null);
			while (modsIt.hasNext()) {
				String modification = (String) modsIt.next();
				if (modification.equals(BioPaxUtil.PHOSPHORYLATION_SITE)) {
					return BioPaxUtil.PROTEIN_PHOSPHORYLATED;
				}
			}
		}

		return BioPaxUtil.getTypeInPlainEnglish(bpe.getModelInterface().getSimpleName());
	}

	private static String addDataSource(BioPAXElement resource) {
		return BioPaxUtil.getDataSource(resource);
	}

	private static String addPublicationXRefs(BioPAXElement resource) {
		
		if(!(resource instanceof org.biopax.paxtools.model.level2.XReferrable 
				|| resource instanceof org.biopax.paxtools.model.level3.XReferrable)) {
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
