package cytoscape.coreplugins.biopax.util;
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
*
*/

import org.biopax.paxtools.model.level2.*;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.BioPAXElement;
import cytoscape.coreplugins.biopax.mapping.MapNodeAttributes;
import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.links.ExternalLinkUtil;
import cytoscape.coreplugins.biopax.style.BioPaxVisualStyleUtil;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

import java.util.*;


/**
 * TODO compare to BioPaxUtil and merge
 * 
 * @deprecated (Rex's version)
 *
 */
public class BioPAXL2Util extends BioPAXUtilRex {

   public static String getNameSmart(entity bpe) {
       String name = bpe.getNAME();
       String sName = bpe.getSHORT_NAME();

       if( name != null && !name.equals("") )
           return name;
       else if( sName != null && !sName.equals("") )
           return sName;
       else if( !bpe.getSYNONYMS().isEmpty() )
           return bpe.getSYNONYMS().iterator().next();
       else
           return bpe.getRDFId();
   }

   public static String getShortNameSmart(entity bpe) {
       String name = bpe.getNAME();
       String sName = bpe.getSHORT_NAME();

       if( sName != null && !sName.equals("") )
           return sName;
       else if( name != null && !name.equals("") )
           return wrapName(name);
       else if( !bpe.getSYNONYMS().isEmpty() )
           return wrapName(bpe.getSYNONYMS().iterator().next());
       else
           return bpe.getRDFId(); // TODO: should i wrap it as well?
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

           setNodeAttributes(interactionID, aInteraction);
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
               String controlStr = (controlType==null)? "" : controlType.toString();

               for(process aProcess: aControl.getCONTROLLED() ) {
                   if(aProcess instanceof interaction) {
                       CyNode controlledNode = nodes.get(aProcess.getRDFId());
                       assert controlledNode != null; // Again because of part 2
                       CyEdge edge = Cytoscape.getCyEdge(interactionNode, controlledNode,
                                                           Semantics.INTERACTION, controlStr,
                                                           CREATE);

                      Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE,"control");

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

       MapNodeAttributes.initAttributes(Cytoscape.getNodeAttributes());

       return new CytoscapeGraphElements(nodes.values(), edges.values());
   }

   public static CytoscapeGraphElements bioPAXL3toCytoscapeGraph(Model biopaxModel) {
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

           setNodeAttributes(interactionID, aInteraction);
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
               String controlStr = controlType.toString();

               for(process aProcess: aControl.getCONTROLLED() ) {
                   if(aProcess instanceof interaction) {
                       CyNode controlledNode = nodes.get(aProcess.getRDFId());
                       assert controlledNode != null; // Again because of part 2
                       CyEdge edge = Cytoscape.getCyEdge(interactionNode, controlledNode,
                                                           Semantics.INTERACTION, controlStr,
                                                           CREATE);

                       Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE,
                                                                                           "control");

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

       MapNodeAttributes.initAttributes(Cytoscape.getNodeAttributes());

       return new CytoscapeGraphElements(nodes.values(), edges.values());
   }

   public static String getBPEntityType(BioPAXElement bpElement) {
       String rawType = "", plainEng = rawType;

       if(bpElement != null) {
           // Thank God, Java hackers are really smart
           rawType = bpElement.getClass().getName();

           String[] tempStr = rawType.split("\\.");
           if( tempStr.length > 0 ) {
               rawType = tempStr[tempStr.length-1].replace("Impl", "");
               plainEng =  BioPaxUtil.getTypeInPlainEnglish(rawType);
           }
       }

       return plainEng;
   }
   
   public static CyNode getPEPStateNode(physicalEntityParticipant pep) {
       return createPEStateNode(null, pep);
   }

   private static void setNodeAttributes(String nodeID, entity bpe) {
       String name = getNameSmart(bpe),
              sName = getShortNameSmart(bpe),
              rdfID = bpe.getRDFId();

       setNodeAttribute(nodeID, Semantics.CANONICAL_NAME, name);
       setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_NAME, name);
       setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_SHORT_NAME, sName);

       setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
                                                               getBPEntityType(bpe));
       setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_RDF_ID, rdfID);

       ArrayList<String> synList = new ArrayList<String>(bpe.getSYNONYMS());
       Cytoscape.getNodeAttributes().setListAttribute(nodeID, MapNodeAttributes.BIOPAX_SYNONYMS, synList);

       String comment = "";
       for(String aComment: bpe.getCOMMENT())
           comment += aComment + "<BR><BR>";

       if(comment.length() > 0)
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_COMMENT, comment);

       String uniRefs = "", pubRefs = "", relRefs = "";
       ArrayList<String> otherRefs = new ArrayList<String>();

       ArrayList<String> dbList = new ArrayList<String>();

       for(xref ref: bpe.getXREF()) {
           if( ref.getDB() == null || ref.getID() == null )
               continue;
           else
               dbList.add( ref.getDB() );

           if( ref instanceof unificationXref && !ref.getDB().equalsIgnoreCase("CPATH") ) {
               uniRefs += "<LI>- " + ExternalLinkUtil.createLink(ref.getDB(), ref.getID()) + "</LI>";
           } else if( ref instanceof publicationXref) {
               publicationXref pRef = (publicationXref) ref;
               String pubTxt = "";

               String authors = "";
               for(String anAuthor: pRef.getAUTHORS())
                   authors += anAuthor + ", ";
               pubTxt += (authors.length() > 0 ) ? authors + " et. al, " : authors;

               if( pRef.getTITLE() != null && pRef.getTITLE().length() > 0 )
                   pubTxt += pRef.getTITLE();

               String sources = "";
               for(String aSrc: pRef.getSOURCE())
                   sources += aSrc + ", ";
               if( pRef.getYEAR() > 0 )
                   sources += pRef.getYEAR();
               pubTxt += (sources.length() > 0 ) ? "(" + sources + ") " : sources;

               pubTxt += ExternalLinkUtil.createLink(pRef.getDB(), pRef.getID());
               pubTxt += "<BR>";
               pubRefs += pubTxt;
           } else if( ref instanceof relationshipXref  && !ref.getDB().equalsIgnoreCase("CPATH") ) {
               relRefs += "<LI>- " + ExternalLinkUtil.createLink(ref.getDB(), ref.getID()) + "</LI>";
           } else {
               otherRefs.add(ExternalLinkUtil.createLink(ref.getDB(), ref.getID()));
           }
       }

       if( uniRefs.length() > 0 )
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_UNIFICATION_REFERENCES, uniRefs);

       if( relRefs.length() > 0 )
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_RELATIONSHIP_REFERENCES, relRefs);

       if( pubRefs.length() > 0 )
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_PUBLICATION_REFERENCES, pubRefs);

       if( !otherRefs.isEmpty() )
           Cytoscape.getNodeAttributes().setListAttribute(nodeID, MapNodeAttributes.BIOPAX_AFFYMETRIX_REFERENCES_LIST, otherRefs);

       String avaliability = "";
       for( String anAvaliability : bpe.getAVAILABILITY() )
           avaliability += anAvaliability + "<BR>";
       if( avaliability.length() > 0 )
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_AVAILABILITY, avaliability);

       String datasources = "";
       for( dataSource aDataSrc: bpe.getDATA_SOURCE() )
           for(String aName: aDataSrc.getNAME() )
               datasources += "<LI> - " + aName + "</LI>";
       if( datasources.length() > 0 )
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_DATA_SOURCES, datasources);

       if( bpe instanceof sequenceEntity ) {
           bioSource bioSrc = ((sequenceEntity) bpe).getORGANISM();
           if( bioSrc != null
                       && bioSrc.getTAXON_XREF() != null
                       && bioSrc.getTAXON_XREF().getID() != null ) {

               setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_ORGANISM_NAME, bioSrc.getNAME());

               String httpLink = ExternalLinkUtil.createIHOPLink(getBPEntityType(bpe), synList, dbList,
                                                                   Integer.parseInt(bioSrc.getTAXON_XREF().getID()) );

               if( httpLink != null )
                   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_IHOP_LINKS, "- " + httpLink);
           }
       }

       String pathways = "";
       for(interaction anInteraction: bpe.isPARTICIPANTSof())
           for(pathway aPathway: anInteraction.isPATHWAY_COMPONENTSof() )
               pathways += aPathway.getNAME() + "<BR>";

       if( pathways.length() > 0 )
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_PATHWAY_NAME, pathways);
   }

   private static void setNodeAttributes(String nodeID, entity bpe, String labelName) {
       setNodeAttributes(nodeID, bpe);
       setNodeAttribute(nodeID, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL, labelName);
   }

   private static void createNodesAndEdges(Map<String, CyNode> nodes, Map<Integer, CyEdge> edges,
                                                   physicalEntityParticipant pep,
                                                   CyNode mainNode, String type) {
       physicalEntity pe = pep.getPHYSICAL_ENTITY();
       CyNode peNode = createPEStateNode(nodes, pep);

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

       Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE, type);
       edges.put(edge.getRootGraphIndex(), edge);
   }

   private static CyNode createPEStateNode(Map<String, CyNode> nodes, physicalEntityParticipant pep) {
       physicalEntity pe = pep.getPHYSICAL_ENTITY();
       String rdfId= pe.getRDFId(), nodeId = rdfId;
       String nodeName = getNameSmart(pe);

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

                           String abbr = BioPaxUtil.getAbbrChemModification(aTerm);
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
           String abbr = BioPaxUtil.getAbbrCellLocation(aTerm);
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

       if( nodes != null )
           nodes.put(nodeId, node);

       node.setIdentifier(nodeId);

       String nid = node.getIdentifier();
       setNodeAttributes(nid, pe, nodeName);

       List<String> chemModList = new ArrayList<String>(BioPaxUtil.getChemModificationsMap().keySet());
       Cytoscape.getNodeAttributes().setListAttribute(nid, 
    		   MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_LIST, chemModList);
       setMultiHashMap(nid, Cytoscape.getNodeAttributes(),
			   MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, chemModCounts);

	    if (chemModCounts.containsKey(BioPaxUtil.PHOSPHORYLATION_SITE)) {
		    setNodeAttribute(nid, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
                   BioPaxUtil.PROTEIN_PHOSPHORYLATED);
		}


       if (cellularLocation != null && cellularLocation.length() != 0) {
           List<String> cellularLocationsList = new ArrayList<String>();
           cellularLocationsList.add(cellularLocation);
           setNodeListAttribute(nid, MapNodeAttributes.BIOPAX_CELLULAR_LOCATIONS,
											cellularLocationsList);
		}

       return node;
   }

}



