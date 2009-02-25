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
package org.mskcc.biopax_plugin.mapping;


// imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;

import cytoscape.view.CyNetworkView;

import ding.view.DNodeView;

import giny.view.NodeView;

import org.jdom.Element;

import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;
import org.mskcc.biopax_plugin.util.biopax.BioPaxConstants;
import org.mskcc.biopax_plugin.util.biopax.BioPaxEntityParser;
import org.mskcc.biopax_plugin.util.biopax.BioPaxPlainEnglish;
import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;
import org.mskcc.biopax_plugin.util.links.ExternalLink;
import org.mskcc.biopax_plugin.util.links.ExternalLinkUtil;
import org.mskcc.biopax_plugin.util.rdf.RdfQuery;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * BioPAX Details Panel.
 *
 * @author Ethan Cerami.
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

	// strange, cant get this to work with final keyword
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
	 * Maps biopax details to node attributes.
	 * This class is based on MapBioPaxToCytoscape.
	 *
	 * @param bpUtil   BioPaxUtil
	 * @param nodeList ArrayList of Nodes
	 */
	public static void doMapping(BioPaxUtil bpUtil, ArrayList nodeList) {
		// class instances we need for rdf queries
		HashMap rdfMap = bpUtil.getRdfResourceMap();


		// get the node attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		initAttributes(nodeAttributes);

		// interate through all nodes in this network
		Iterator nodeIterator = nodeList.iterator();

		while (nodeIterator.hasNext()) {
			// get node id
			CyNode node = (CyNode) nodeIterator.next();
			String nodeID = node.getIdentifier();

			// get node element
			String biopaxID = nodeAttributes.getStringAttribute(nodeID, BIOPAX_RDF_ID);
			Element resource = (Element) rdfMap.get(biopaxID);

            mapNodeAttribute(resource, nodeID, nodeAttributes, bpUtil);
        }
	}

    /**
     * Maps Attributes for a Single Node.
     * @param resource          JDOM Element Object.
     * @param nodeID            Node ID
     * @param nodeAttributes    Node Attributes.
     * @param bpUtil            BioPAX Util.
     */
    public static void mapNodeAttribute(Element resource, String nodeID,
            CyAttributes nodeAttributes, BioPaxUtil bpUtil) {
        if (resource != null) {
            String stringRef;
            RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());
            BioPaxEntityParser bpParser = new BioPaxEntityParser(resource,
                    bpUtil.getRdfResourceMap());

            // type
            stringRef = addType(bpParser, nodeID, nodeAttributes);

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_ENTITY_TYPE, stringRef);
            }

            // short name
            stringRef = bpParser.getShortName();

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_SHORT_NAME, stringRef);
            }

            // name
            stringRef = bpParser.getName();

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_NAME, stringRef);
            }

            // synonyms
            ArrayList synList = bpParser.getSynonymList();

            if (synList != null) {
                nodeAttributes.setListAttribute(nodeID, BIOPAX_SYNONYMS, synList);
            }

            // organism
            stringRef = bpParser.getOrganismName();

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_ORGANISM_NAME, stringRef);
            }

            // comment
            stringRef = bpParser.getComment();

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_COMMENT, stringRef);
            }

            // unification references
            stringRef = addXRefs(bpParser.getUnificationXRefs());

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_UNIFICATION_REFERENCES, stringRef);
            }

            // the following code should replace the old way to set
            // relationship references
            List xrefList = getXRefList(bpParser, BIOPAX_AFFYMETRIX_REFERENCES_LIST);

            if ((xrefList != null) && (xrefList.size() > 0)) {
                nodeAttributes.setListAttribute(nodeID, BIOPAX_AFFYMETRIX_REFERENCES_LIST,
                                                xrefList);
            }

            // relationship references - old way
            stringRef = addXRefs(bpParser.getRelationshipXRefs());

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_RELATIONSHIP_REFERENCES, stringRef);
            }

            // publication references
            stringRef = addPublicationXRefs(rdfQuery, resource);

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_PUBLICATION_REFERENCES, stringRef);
            }

            // availability
            stringRef = bpParser.getAvailability();

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_AVAILABILITY, stringRef);
            }

            // data sources
            stringRef = addDataSource(rdfQuery, resource);

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_DATA_SOURCES, stringRef);
            }

            // ihop links
            stringRef = addIHOPLinks(bpParser);

            if (stringRef != null) {
                nodeAttributes.setAttribute(nodeID, BIOPAX_IHOP_LINKS, stringRef);
            }

            // pathway name
            if (bpUtil != null) {
                stringRef = addPathwayMembership(bpParser.getRdfId(), bpUtil);

                if (stringRef != null) {
                    nodeAttributes.setAttribute(nodeID, BIOPAX_PATHWAY_NAME, stringRef);
                }
            }

            //  add all xref ids for global lookup
            List idList = addXRefIds(bpParser.getAllXRefs());

            if (idList != null) {
                nodeAttributes.setListAttribute(nodeID, BIOPAX_XREF_IDS, idList);
                for (int i=0; i<idList.size(); i++) {
                    String idPair = (String) idList.get(i);
                    String parts[] = idPair.split(":");
					if (parts.length != 2) continue;
                    String dbName = parts[0];
                    String id = parts[1];
                    String key = BIOPAX_XREF_PREFIX + dbName.toUpperCase();
                    //  Set individual XRefs;  Max of 1 per database.
                    String existingId = nodeAttributes.getStringAttribute(nodeID, key);
                    if (existingId == null) {
                        nodeAttributes.setAttribute(nodeID, key, id);
                    }
                }
            }

            //  Optionally add Node Label
            String label = nodeAttributes.getStringAttribute
                    (nodeID, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
            if (label == null) {
                BioPaxNameUtil nameUtil = new BioPaxNameUtil(rdfQuery);
                label = nameUtil.getNodeName(nodeID, resource);
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
	 * Addes custom node shapes to biopax nodes.
	 *
	 * @param networkView CyNetworkView
	 */
	public static void customNodes(CyNetworkView networkView) {
		// grab node attributes
		CyNetwork cyNetwork = networkView.getNetwork();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// iterate through the nodes
		Iterator nodesIt = cyNetwork.nodesIterator();

		if (nodesIt.hasNext()) {
			// grab the node
			CyNode node = (CyNode) nodesIt.next();

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

					if (modification.equals(BioPaxConstants.PHOSPHORYLATION_SITE)) {
						isPhosphorylated = true;

						Object[] key = { BioPaxConstants.PHOSPHORYLATION_SITE };
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

	private static String addPathwayMembership(String rdfId, BioPaxUtil bpUtil) {
		//  Get pathway membership map
		HashMap pathwayMembershipMap = bpUtil.getPathwayMembershipMap();

		//  Get list of pathways that this RDF ID is a member of
		ArrayList pathwayList = (ArrayList) pathwayMembershipMap.get(rdfId);

		if (pathwayList != null) {
			HashMap rdfMap = bpUtil.getRdfResourceMap();
			StringBuffer temp = new StringBuffer();

			for (int i = 0; i < pathwayList.size(); i++) {
				String pathwayId = (String) pathwayList.get(i);
				Element pathway = (Element) rdfMap.get(pathwayId);

				if (pathway != null) {
					BioPaxEntityParser bpParser2 = new BioPaxEntityParser(pathway, rdfMap);

					if (pathwayList.size() == 1) {
						temp.append(getName(bpParser2));
					} else {
						temp.append("- " + getName(bpParser2));
					}

					// we should think about how to delimit items
					if (i < (pathwayList.size() - 1)) {
						temp.append("<BR>");
					}
				}
			}

			return temp.toString();
		}

		// outta here
		return null;
	}

	private static String getName(BioPaxEntityParser bpParser) {
		if (bpParser.getName() != null) {
			return bpParser.getName();
		} else if (bpParser.getShortName() != null) {
			return bpParser.getShortName();
		} else {
			return bpParser.getRdfId();
		}
	}

	private static String addType(BioPaxEntityParser bpParser, String nodeID,
	                              CyAttributes nodeAttributes) {
		MultiHashMapDefinition mhmdef = nodeAttributes.getMultiHashMapDefinition();

		// first check if attribute exists
		if (mhmdef.getAttributeValueType(BIOPAX_CHEMICAL_MODIFICATIONS_MAP) != -1) {
			MultiHashMap mhmap = nodeAttributes.getMultiHashMap();
			CountedIterator modsIt = mhmap.getAttributeKeyspan(nodeID,
			                                                   BIOPAX_CHEMICAL_MODIFICATIONS_MAP,
			                                                   null);

			while (modsIt.hasNext()) {
				String modification = (String) modsIt.next();

				if (modification.equals(BioPaxConstants.PHOSPHORYLATION_SITE)) {
					return BioPaxConstants.PROTEIN_PHOSPHORYLATED;
				}
			}
		}

		String type = bpParser.getType();

		return BioPaxPlainEnglish.getTypeInPlainEnglish(type);
	}

	private static String addDataSource(RdfQuery rdfQuery, Element resource) {
		ArrayList dataSourceList = rdfQuery.getNodes(resource, "DATA-SOURCE/dataSource");

		if (dataSourceList.size() > 0) {
			StringBuffer temp = new StringBuffer();

			for (int i = 0; i < dataSourceList.size(); i++) {
				Element dataSource = (Element) dataSourceList.get(i);
				Element dsNameElement = rdfQuery.getNode(dataSource, "NAME");
				Element dsCommentElement = rdfQuery.getNode(dataSource, "COMMENT");

				if (dsNameElement != null) {
					temp.append(dsNameElement.getTextNormalize());
				}

				if ((dsNameElement != null) && (dsCommentElement != null)) {
					temp.append(":  ");
				}

				if (dsCommentElement != null) {
					temp.append(dsCommentElement.getTextNormalize());
				}
			}

			return temp.toString();
		}

		// outta here
		return null;
	}

	private static String addPublicationXRefs(RdfQuery rdfQuery, Element resource) {
		ArrayList pubList = rdfQuery.getNodes(resource, "XREF/publicationXref");

		if (pubList.size() > 0) {
			StringBuffer temp = new StringBuffer();

			for (int i = 0; i < pubList.size(); i++) {
				Element ref = (Element) pubList.get(i);
				Element dbElement = rdfQuery.getNode(ref, "DB");
				Element idElement = rdfQuery.getNode(ref, "ID");
				Element authorElement = rdfQuery.getNode(ref, "AUTHORS");
				Element titleElement = rdfQuery.getNode(ref, "TITLE");
				Element sourceElement = rdfQuery.getNode(ref, "SOURCE");
				Element yearElement = rdfQuery.getNode(ref, "YEAR");

				if (authorElement != null) {
					temp.append(authorElement.getTextNormalize() + " et. al, ");
				}

				if (titleElement != null) {
					temp.append(titleElement.getTextNormalize());
				}

				if (sourceElement != null) {
					temp.append(" (" + sourceElement.getTextNormalize());

					if (yearElement != null) {
						temp.append(", " + yearElement.getTextNormalize());
					}

					temp.append(")");
				}

				if ((dbElement != null) && (idElement != null)) {
					String dbName = dbElement.getTextNormalize();
					String id = idElement.getTextNormalize();
					temp.append(ExternalLinkUtil.createLink(dbName, id));
				}

				if (i < (pubList.size() - 1)) {
					// we should think about how to delimit items
					temp.append("<BR>");
				}
			}

			return temp.toString();
		}

		// outta here
		return null;
	}

	private static String addXRefs(ArrayList xrefList) {
		if (xrefList.size() > 0) {
			StringBuffer temp = new StringBuffer();

			for (int i = 0; i < xrefList.size(); i++) {
				ExternalLink link = (ExternalLink) xrefList.get(i);
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

		// outta here
		return null;
	}

	private static ArrayList addXRefIds(ArrayList xrefList) {
		ArrayList idList = new ArrayList();

		if ((xrefList != null) && (xrefList.size() > 0)) {
			for (int i = 0; i < xrefList.size(); i++) {
				ExternalLink link = (ExternalLink) xrefList.get(i);
				idList.add(link.getDbName() + ":" + link.getId());
			}
		}

		return idList;
	}

	private static List getXRefList(BioPaxEntityParser bpParser, String xrefType) {
		// our list to return
		List listToReturn = new ArrayList();

		// get the xref list
		ArrayList list = bpParser.getRelationshipXRefs();

		// what type of xref are we interested in ?
		String type = null;

		if (xrefType.equals(BIOPAX_AFFYMETRIX_REFERENCES_LIST)) {
			type = "AFFYMETRIX";
		}

		// do we have anything to process
		if (list.size() > 0) {
			for (int lc = 0; lc < list.size(); lc++) {
				ExternalLink link = (ExternalLink) list.get(lc);

				if (link.getDbName().toUpperCase().startsWith(type)) {
					listToReturn.add(link.getId());
				}
			}
		}

		// outta here
		return listToReturn;
	}

	private static String addIHOPLinks(BioPaxEntityParser bpParser) {
		ArrayList synList = bpParser.getSynonymList();
		ArrayList dbList = bpParser.getAllXRefs();

		if ((synList.size() > 0) || (dbList.size() > 0)) {
			String htmlLink = ExternalLinkUtil.createIHOPLink(bpParser.getType(), synList, dbList,
			                                                  bpParser.getOrganismTaxonomyId());

			if (htmlLink != null) {
				return ("- " + htmlLink);
			}
		}

		// outta here
		return null;
	}
}
