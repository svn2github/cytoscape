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
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape;
import org.mskcc.biopax_plugin.util.biopax.BioPaxChemicalModificationMap;
import org.mskcc.biopax_plugin.util.biopax.BioPaxCellularLocationMap;
import org.mskcc.biopax_plugin.util.biopax.BioPaxConstants;
import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.attr.MultiHashMap;

import java.util.*;

public class BioPAXUtil {
    public static final int MAX_SHORT_NAME_LENGTH = 25;

    private static Map<String, String> chemModAbbrList = new BioPAXChemModAbbrMap();
    private static Map<String, String> cellularLocAbbrList = new BioPAXCellularLocAbbrMap();


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
            nodeAttributes.setAttribute(interactionID, MapNodeAttributes.BIOPAX_NAME, name);
            nodeAttributes.setAttribute(interactionID, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
                                                    aInteraction.getClass().getName());
            nodeAttributes.setAttribute(interactionID, MapNodeAttributes.BIOPAX_RDF_ID, interactionID);
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
                    createNodesAndEdges(nodes, edges, leftPEP, interactionNode, MapBioPaxToCytoscape.LEFT);
                // Once again for the other side
                for(physicalEntityParticipant rightPEP: aConversion.getRIGHT())
                    createNodesAndEdges(nodes, edges, rightPEP, interactionNode, MapBioPaxToCytoscape.RIGHT);

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
                        edgeAttributes.setAttribute(edge.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE, controlStr);
                        edges.put(edge.getRootGraphIndex(), edge);
                    } // TODO: else? what we gonna do if it is a pathway? Wups?
                }

                for(physicalEntityParticipant pep: aControl.getCONTROLLER())
                    createNodesAndEdges(nodes, edges, pep, interactionNode, MapBioPaxToCytoscape.CONTROLLER);

                if(aControl instanceof catalysis) {
                    for(physicalEntityParticipant pep: ((catalysis) aControl).getCOFACTOR())
                        createNodesAndEdges(nodes, edges, pep, interactionNode, MapBioPaxToCytoscape.COFACTOR);
                }

            } else if( aInteraction instanceof physicalInteraction ) {
                physicalInteraction pi = (physicalInteraction) aInteraction;

                for(InteractionParticipant participant: pi.getPARTICIPANTS()) {
                    if( participant instanceof interaction ) { // Wooh, easy one
                        CyNode interactedNode = nodes.get(participant.getRDFId());
                        assert interactedNode != null; // By part 1

                        CyEdge edge = Cytoscape.getCyEdge(interactionNode, interactedNode,
                                                            Semantics.INTERACTION, MapBioPaxToCytoscape.PARTICIPANT,
                                                            CREATE);
                        edges.put(edge.getRootGraphIndex(), edge);
                    } else if( participant instanceof physicalEntityParticipant ) {
                        physicalEntityParticipant pep = (physicalEntityParticipant) participant;
                        createNodesAndEdges(nodes, edges, pep, interactionNode, MapBioPaxToCytoscape.PARTICIPANT);
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
        CyNode peNode = createPEStateNode(nodes, pep, type);

        if(pe instanceof complex) {
            complex aComplex = (complex) pep.getPHYSICAL_ENTITY();
            for(physicalEntityParticipant aPEP: aComplex.getCOMPONENTS())
                createNodesAndEdges(nodes, edges, aPEP, peNode, MapBioPaxToCytoscape.CONTAINS);
        }

        CyEdge edge;
        if( type.equals(MapBioPaxToCytoscape.RIGHT) || type.equals(MapBioPaxToCytoscape.COFACTOR) || type.equals(MapBioPaxToCytoscape.PARTICIPANT) )
            edge = Cytoscape.getCyEdge(mainNode, peNode, Semantics.INTERACTION, type, CREATE);
        else if( type.equals(MapBioPaxToCytoscape.LEFT) || type.equals(MapBioPaxToCytoscape.CONTAINS) )
            edge = Cytoscape.getCyEdge(peNode, mainNode, Semantics.INTERACTION, type, CREATE);
        else
            edge = Cytoscape.getCyEdge(peNode, mainNode, Semantics.INTERACTION, type, CREATE);

        edgeAttributes.setAttribute(edge.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE, type);
        edges.put(edge.getRootGraphIndex(), edge);
    }

    private static CyNode createPEStateNode(Map<String, CyNode> nodes, physicalEntityParticipant pep, String type) {
        String rdfId= pep.getPHYSICAL_ENTITY().getRDFId(), nodeId = rdfId;
        String nodeName = BioPAXUtil.getShortNameSmart(pep.getPHYSICAL_ENTITY()); // TODO: Short or long?

        String chemicalModifications = "",
               cellularLocation = "";

        Map<String, Integer> chemModCounts = new HashMap<String, Integer>();

        if( pep instanceof sequenceParticipant ) { // We have some modifications
            for( sequenceFeature sf: ((sequenceParticipant) pep).getSEQUENCE_FEATURE_LIST()) {
                openControlledVocabulary ocv = sf.getFEATURE_TYPE();
                if( ocv != null ) {
                    for( String aTerm: ocv.getTERM() ) {
                        Integer cnt = chemModCounts.get(aTerm);
                        if( cnt == null ) {
                            cnt = 0;

                            String abbr = chemModAbbrList.get(aTerm);
                            if( abbr == null || abbr.length() == 0)
                                abbr = aTerm;

                            chemicalModifications += abbr;
                        }
                        chemModCounts.put(aTerm, ++cnt);
                    }
                }
            }

            if( chemicalModifications.length() > 0 )
                chemicalModifications = "-" + chemicalModifications;

            nodeName += chemicalModifications;
            nodeId += chemicalModifications;
        }

        openControlledVocabulary ocv = pep.getCELLULAR_LOCATION();
        if( ocv != null && ocv.getTERM().iterator().hasNext() ) {
            String aTerm =  ocv.getTERM().iterator().next();
            String abbr = cellularLocAbbrList.get(aTerm);
            if( abbr == null ) {
                if( aTerm == null )
                    abbr = "";
                else
                    abbr = aTerm;
            }

            if( abbr.length() > 0 )
                abbr = "(" + abbr + ")";

            nodeId += abbr;
            nodeName += (abbr.length() == 0) ? abbr : "\n" + abbr;
            cellularLocation = aTerm;
        }

        CyNode node = Cytoscape.getCyNode(nodeId, CREATE);
        nodes.put(nodeId, node);
        node.setIdentifier(nodeId);

        String nid = node.getIdentifier();
        nodeAttributes.setAttribute(nid, Semantics.CANONICAL_NAME, getNameSmart(pep.getPHYSICAL_ENTITY()));
        nodeAttributes.setAttribute(nid, MapNodeAttributes.BIOPAX_NAME, getNameSmart(pep.getPHYSICAL_ENTITY()));
        nodeAttributes.setAttribute(nid, MapNodeAttributes.BIOPAX_ENTITY_TYPE, type);
        nodeAttributes.setAttribute(nid, MapNodeAttributes.BIOPAX_RDF_ID, rdfId);
        nodeAttributes.setAttribute(nid, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL, nodeName);

        List<String> chemModList = new ArrayList<String>(chemModAbbrList.keySet());
        nodeAttributes.setListAttribute(nid, MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_LIST, chemModList);
        setMultiHashMap(nid, nodeAttributes,
			                MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, chemModCounts);

	    if (chemModCounts.containsKey(BioPaxConstants.PHOSPHORYLATION_SITE)) {
		    nodeAttributes.setAttribute(nid, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
                    BioPaxConstants.PROTEIN_PHOSPHORYLATED);
		}


        if (cellularLocation != null && cellularLocation.length() != 0) {
            List<String> cellularLocationsList = new ArrayList<String>();
            cellularLocationsList.add(cellularLocation);
			nodeAttributes.setListAttribute(nid, MapNodeAttributes.BIOPAX_CELLULAR_LOCATIONS,
											cellularLocationsList);
		}

        return node;
    }

    /*
     * A helper function to set a multihashmap consisting of name - value pairs.
     */
    private static void setMultiHashMap(String cyNodeId, CyAttributes attributes, String attributeName,
                                 Map<String, Integer> map) {
        // our key format
        final byte[] mhmKeyFormat = new byte[] { MultiHashMapDefinition.TYPE_STRING };

        // define multihashmap if necessary
        MultiHashMapDefinition mmapDefinition = attributes.getMultiHashMapDefinition();

        try {
            mmapDefinition.getAttributeKeyspaceDimensionTypes(attributeName);
        } catch (IllegalStateException e) {
            // define the multihashmap attribute
            mmapDefinition.defineAttribute(attributeName, MultiHashMapDefinition.TYPE_STRING,
                                           mhmKeyFormat);
        }

        // add the map attributes
        MultiHashMap mhmap = attributes.getMultiHashMap();
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();

        for(Map.Entry<String, Integer> me : entrySet ) {
            String[] key = { me.getKey() };
            Integer value = me.getValue();
            mhmap.setAttributeValue(cyNodeId, attributeName, value.toString(), key);
        }
    }


    public static void customNodes(CyNetworkView networkView) {
        MapNodeAttributes.customNodes(networkView);
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

    // Wrapping the wrappers (generics support, less warnings)
    private static class BioPAXChemModAbbrMap extends HashMap<String, String> {
        public BioPAXChemModAbbrMap() {
            HashMap abbrMap = new BioPaxChemicalModificationMap();
            for(Object key: abbrMap.keySet()) {
                Object value = abbrMap.get(key);
                if( key instanceof String && value instanceof String )
                    this.put((String) key, (String) value);
            }
        }
    }

    private static class BioPAXCellularLocAbbrMap extends HashMap<String, String> {
        public BioPAXCellularLocAbbrMap() {
            HashMap abbrMap = new BioPaxCellularLocationMap();
            for(Object key: abbrMap.keySet()) {
                Object value = abbrMap.get(key);
                if( key instanceof String && value instanceof String )
                    this.put((String) key, (String) value);
            }
        }
    }
}