/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 * 
 * This file is part of PaxtoolsPlugin.
 *
 *  PaxtoolsPlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.biyoenformatik.cytoscape.util;

import org.biopax.paxtools.model.level2.*;
import org.biopax.paxtools.model.Model;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import java.util.*;

public class BioPAXUtil {
    /* org.mskcc.biopax_plugin.mapping.MapNodeAttributes constants */
    public static final String BIOPAX_RDF_ID = "biopax.rdf_id";
    public static final String BIOPAX_ENTITY_TYPE = "biopax.entity_type";
    public static final String BIOPAX_NAME = "biopax.name";
    public static final String BIOPAX_CHEMICAL_MODIFICATIONS_MAP = "biopax.chemical_modifications_map";
    public static final String BIOPAX_CHEMICAL_MODIFICATIONS_LIST = "biopax.chemical_modifications";
    public static final String BIOPAX_CELLULAR_LOCATIONS = "biopax.cellular_location";
    public static final String BIOPAX_SHORT_NAME = "biopax.short_name";
    public static final String BIOPAX_SYNONYMS = "biopax.synonyms";
    public static final String BIOPAX_ORGANISM_NAME = "biopax.organism_name";
    public static final String BIOPAX_COMMENT = "biopax.comment";
    public static final String BIOPAX_UNIFICATION_REFERENCES = "biopax.unification_references";
    public static final String BIOPAX_RELATIONSHIP_REFERENCES = "biopax.relationship_references";
    public static final String BIOPAX_PUBLICATION_REFERENCES = "biopax.publication_references";
    public static final String BIOPAX_XREF_IDS = "biopax.xref_ids";
    public static final String BIOPAX_XREF_PREFIX = "biopax.xref.";
    public static final String BIOPAX_AVAILABILITY = "biopax.availability";
    public static final String BIOPAX_DATA_SOURCES = "biopax.data_sources";
    public static final String BIOPAX_IHOP_LINKS = "biopax.ihop_links";
    public static final String BIOPAX_PATHWAY_NAME = "biopax.pathway_name";
    public static final String BIOPAX_AFFYMETRIX_REFERENCES_LIST
            = "biopax.affymetrix_references_list";

    /* org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape constants */
    public static final String BIOPAX_NETWORK = "BIOPAX_NETWORK";
    public static final String BIOPAX_EDGE_TYPE = "BIOPAX_EDGE_TYPE";
    public static final String RIGHT = "RIGHT";
    public static final String LEFT = "LEFT";
    public static final String PARTICIPANT = "PARTICIPANT";
    public static final String CONTROLLER = "CONTROLLER";
    public static final String CONTROLLED = "CONTROLLED";
    public static final String COFACTOR = "COFACTOR";
    public static final String CONTAINS = "CONTAINS";

    /* ~ end of constants ~ */

    public static final int MAX_SHORT_NAME_LENGTH = 25;

    private static final boolean CREATE = true;

    // Just to shorten the names
    private static CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    private static CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

    public static String getNameSmart(entity bpe) {
        String name = bpe.getNAME();
        String sName = bpe.getSHORT_NAME();

        if( !name.equals("") )
            return name;
        else if( !sName.equals("") )
            return sName;
        else if( !bpe.getSYNONYMS().isEmpty() )
            return bpe.getSYNONYMS().iterator().next();
        else
            return bpe.getRDFId();
    }

    public static String getShortNameSmart(entity bpe) {
        String name = bpe.getNAME();
        String sName = bpe.getSHORT_NAME();

        if( !sName.equals("") )
            return sName;
        else if( !name.equals("") )
            return wrapName(name);
        else if( !bpe.getSYNONYMS().isEmpty() )
            return wrapName(bpe.getSYNONYMS().iterator().next());
        else
            return bpe.getRDFId(); // TODO: should i wrap it as well?
    }

    private static String wrapName(String name) {
         return (name.length() > MAX_SHORT_NAME_LENGTH
                        ? name.subSequence(0, MAX_SHORT_NAME_LENGTH-3) + "..."
                        : name );
    }

    public static CytoscapeGraphElements bioPAXtoCytoscapeGraph(Model biopaxModel) {
        // This will help us to keep track of what we have created
        Map<String, CyNode> nodes = new HashMap<String, CyNode>();
        Map<Integer, CyEdge> edges = new HashMap<Integer, CyEdge>();

        Set<interaction> interactions = biopaxModel.getObjects(interaction.class);

        /* Part 1 : Let's create all interactions */
        for(interaction aInteraction: interactions) {
            String interactionID = aInteraction.getRDFId();

            CyNode interactionNode = Cytoscape.getCyNode(interactionID, CREATE);
            nodes.put(interactionID, interactionNode);
            interactionNode.setIdentifier(interactionID);
            String name = BioPAXUtil.getNameSmart(aInteraction);
            nodeAttributes.setAttribute(interactionID, Semantics.CANONICAL_NAME, name);
            nodeAttributes.setAttribute(interactionID, BioPAXUtil.BIOPAX_NAME, name);
            nodeAttributes.setAttribute(interactionID, BioPAXUtil.BIOPAX_ENTITY_TYPE,
                                                    aInteraction.getClass().getName());
            nodeAttributes.setAttribute(interactionID, BioPAXUtil.BIOPAX_RDF_ID, interactionID);
            /* */
        }

        /* Part 2 : Now, we are sure that all interaction nodes were created */
        for(interaction aInteraction: interactions) {
            // Complexes and interactions require recursive iterations
            CyNode interactionNode = nodes.get(aInteraction.getRDFId());
            assert interactionNode != null; // because of part 1

            if( aInteraction instanceof conversion) {
                conversion aConversion = (conversion) aInteraction;

                for(physicalEntityParticipant leftPEP: aConversion.getLEFT())
                    createNodesAndEdges(nodes, edges, leftPEP, interactionNode, LEFT);
                // Once again for the other side
                for(physicalEntityParticipant rightPEP: aConversion.getRIGHT())
                    createNodesAndEdges(nodes, edges, rightPEP, interactionNode, RIGHT);

            } else if( aInteraction instanceof control) {
                control aControl = (control) aInteraction;
                ControlType controlType = aControl.getCONTROL_TYPE();
                String controlStr = controlTypeToString(controlType);

                for(process aProcess: aControl.getCONTROLLED() ) {
                    if(aProcess instanceof interaction) {
                        CyNode controlledNode = nodes.get(aProcess.getRDFId());
                        assert controlledNode != null; // Again because of part 2
                        CyEdge edge = Cytoscape.getCyEdge(interactionNode, controlledNode,
                                                            Semantics.INTERACTION, controlStr,
                                                            CREATE);
                        edgeAttributes.setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, controlStr);
                        edges.put(edge.getRootGraphIndex(), edge);
                    } // TODO: else? what we gonna do if it is a pathway? Wups?
                }

                for(physicalEntityParticipant pep: aControl.getCONTROLLER())
                    createNodesAndEdges(nodes, edges, pep, interactionNode, CONTROLLER);

                if(aControl instanceof catalysis) {
                    for(physicalEntityParticipant pep: ((catalysis) aControl).getCOFACTOR())
                        createNodesAndEdges(nodes, edges, pep, interactionNode, COFACTOR);
                }

            } else if( aInteraction instanceof physicalInteraction ) {
                physicalInteraction pi = (physicalInteraction) aInteraction;

                for(InteractionParticipant participant: pi.getPARTICIPANTS()) {
                    if( participant instanceof interaction ) { // Wooh, easy one
                        CyNode interactedNode = nodes.get(participant.getRDFId());
                        assert interactedNode != null; // By part 1

                        CyEdge edge = Cytoscape.getCyEdge(interactionNode, interactedNode,
                                                            Semantics.INTERACTION, PARTICIPANT,
                                                            CREATE);
                        edges.put(edge.getRootGraphIndex(), edge);
                    } else if( participant instanceof physicalEntityParticipant ) {
                        physicalEntityParticipant pep = (physicalEntityParticipant) participant;
                        createNodesAndEdges(nodes, edges, pep, interactionNode, PARTICIPANT);
                    } // TODO: else? I don't think so, or wait!?! hmm?
                }
            }
            /* */

        }

        return new CytoscapeGraphElements(nodes.values(), edges.values());
    }

    private static void createNodesAndEdges(Map<String, CyNode> nodes, Map<Integer, CyEdge> edges,
                                                    physicalEntityParticipant pep,
                                                    CyNode mainNode, String type) {
        physicalEntity pe = pep.getPHYSICAL_ENTITY();
        // TODO: Consider modifications :'(
        String peID = pe.getRDFId();
        CyNode peNode = Cytoscape.getCyNode(peID, CREATE);
        nodes.put(peID, peNode);

        if(pe instanceof complex) {
            complex aComplex = (complex) pep.getPHYSICAL_ENTITY();
            for(physicalEntityParticipant aPEP: aComplex.getCOMPONENTS())
                createNodesAndEdges(nodes, edges, aPEP, peNode, CONTAINS);
        }

        CyEdge edge;
        if( type.equals(RIGHT) || type.equals(COFACTOR) || type.equals(PARTICIPANT) )
            edge = Cytoscape.getCyEdge(mainNode, peNode, Semantics.INTERACTION, type, CREATE);
        else if( type.equals(LEFT) || type.equals(CONTAINS) )
            edge = Cytoscape.getCyEdge(peNode, mainNode, Semantics.INTERACTION, type, CREATE);
        else
            edge = Cytoscape.getCyEdge(peNode, mainNode, Semantics.INTERACTION, type, CREATE);

        edgeAttributes.setAttribute(edge.getIdentifier(), BIOPAX_EDGE_TYPE, type);
        edges.put(edge.getRootGraphIndex(), edge);
    }


    public static class CytoscapeGraphElements {
        public Collection<CyNode> nodes;
        public Collection<CyEdge> edges;

        public CytoscapeGraphElements(Collection<CyNode> nodes, Collection<CyEdge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    public static String controlTypeToString(ControlType ct) {
        // Probably the ugliest code I've written for this plugin
        if(ct == ControlType.ACTIVATION)
            return "ACTIVATION";
        else if(ct == ControlType.ACTIVATION_ALLOSTERIC)
            return "ACTIVATION_ALLOSTERIC";
        else if(ct == ControlType.ACTIVATION_NONALLOSTERIC)
            return "ACTIVATION_NONALLOSTERIC";
        else if(ct == ControlType.ACTIVATION_UNKMECH)
            return "ACTIVATION_UNKMECH";
        else if(ct == ControlType.INHIBITION)
            return "INHIBITION";
        else if(ct == ControlType.INHIBITION_ALLOSTERIC)
            return "INHIBITION_ALLOSTERIC";
        else if(ct == ControlType.INHIBITION_COMPETITIVE)
            return "INHIBITION_COMPETITIVE";
        else if(ct == ControlType.INHIBITION_IRREVERSIBLE)
            return "INHIBITION_IRREVERSIBLE";
        else if(ct == ControlType.INHIBITION_NONCOMPETITIVE)
            return "INHIBITION_NONCOMPETITIVE";
        else if(ct == ControlType.INHIBITION_OTHER)
            return "INHIBITION_OTHER";
        else if(ct == ControlType.INHIBITION_UNCOMPETITIVE)
            return "INHIBITION_UNCOMPETITIVE";
        else if(ct == ControlType.INHIBITION_UNKMECH)
            return "INHIBITION_UNKMECH";
        else
            return "";
    }

}


