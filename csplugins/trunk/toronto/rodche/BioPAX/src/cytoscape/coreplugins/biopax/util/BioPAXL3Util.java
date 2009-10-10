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
*/

import org.biopax.paxtools.model.level3.*;
import org.biopax.paxtools.model.level3.Process;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.BioPAXElement;
import cytoscape.coreplugins.biopax.mapping.MapNodeAttributes;
import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.links.ExternalLinkUtil;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.coreplugins.biopax.style.BioPax3VisualStyleUtil;
import cytoscape.data.Semantics;

import java.util.*;

/**
 * 
 * TODO compare to BioPaxUtil and merge
 * 
 * @deprecated (Rex's version)
 *
 */
public class BioPAXL3Util extends BioPAXUtilRex {

   public static String getNameSmart(Entity bpe) {
	   String name = stringOr(bpe.getStandardName(),bpe.getDisplayName());
       if(!name.equals("")) return name;
       else if( !bpe.getName().isEmpty() )
           return bpe.getName().iterator().next();
       else
           return bpe.getRDFId();
   }

   public static String getShortNameSmart(Entity bpe) {
	   String name = wrapName(stringOr(bpe.getStandardName(),bpe.getDisplayName()));
       if(!name.equals("")) return name;
       else if( !bpe.getName().isEmpty() )
           return wrapName(bpe.getName().iterator().next());
       else
           return bpe.getRDFId();
   }

    
   public static CytoscapeGraphElements bioPAXtoCytoscapeGraph(Model biopaxModel) {
       // This will help us to keep track of what we have created
       Map<String, CyNode> nodes = new HashMap<String, CyNode>();
       Map<Integer, CyEdge> edges = new HashMap<Integer, CyEdge>();

       Set<Interaction> interactions = biopaxModel.getObjects(Interaction.class);

       /* Part 1 : Let's create all interactions ... not quite sure why we can't wait.  */
       log.setDebug(true);
       for(Interaction anInteraction: interactions) {
    	   if ((anInteraction instanceof Control) && !createNodesForControls) continue;
    	   String interactionID = anInteraction.getRDFId();
           CyNode interactionNode = getCyNode(interactionID, CREATE);
           log.debug(" creating node: "+interactionID+" "+interactionNode);
           nodes.put(interactionID, interactionNode);
           interactionNode.setIdentifier(interactionID);

           setNodeAttributes(interactionID, anInteraction);
           /* */
       }
       log.setDebug(false);

       /* Part 2 : Now, we are sure that all interaction nodes were created */
       for(Interaction anInteraction: interactions) {
           // Complexes and interactions require recursive iterations
           CyNode interactionNode = nodes.get(anInteraction.getRDFId());
           log.debug("Visiting node "+anInteraction.getRDFId()+" "+interactionNode);
           assert interactionNode != null; // because of part 1

           if (anInteraction instanceof Conversion) bpToGraph((Conversion) anInteraction, nodes,edges);
           else if (anInteraction instanceof Control) bpToGraph((Control) anInteraction, nodes,edges);
           //else if (anInteraction instanceof GeneticInteraction) bpToGraph((GeneticInteraction) aInteraction, nodes,edges);
           //else if (anInteraction instanceof MolecularInteraction) bpToGraph((MolecularInteraction) aInteraction, nodes,edges);
           else if (anInteraction instanceof TemplateReaction) bpToGraph((TemplateReaction) anInteraction, nodes,edges);
           else bpToGraph((Interaction) anInteraction, nodes,edges);
       }
       MapNodeAttributes.initAttributes(nodeAttributes);
       
       return new CytoscapeGraphElements(nodes.values(), edges.values());
   }
                  
   static void bpToGraph(Interaction anInteraction, Map<String, CyNode> nodes, Map<Integer, CyEdge> edges) {
	   CyNode interactionNode = nodes.get(anInteraction.getRDFId());
	   for(Entity participant: anInteraction.getParticipant()) {
		   if( participant instanceof Interaction ) { // Wooh, easy one
			   CyNode interactedNode = nodes.get(participant.getRDFId());
			   assert interactedNode != null; // By part 1
			   
			   CyEdge edge = createInteractionCyEdge(
					   interactionNode, interactedNode, MapBioPaxToCytoscape.PARTICIPANT);
			   edges.put(edge.getRootGraphIndex(), edge);
		   } else if( participant instanceof PhysicalEntity ) {
			   PhysicalEntity pep = (PhysicalEntity) participant;
			   createNodesAndEdges(nodes, edges, pep, interactionNode, MapBioPaxToCytoscape.PARTICIPANT);
		   } // TODO: else? I don't think so, or wait!?! hmm?
	   }
   }
   
   static void bpToGraph(TemplateReaction aTReaction, Map<String, CyNode> nodes, Map<Integer, CyEdge> edges) {
       CyNode interactionNode = nodes.get(aTReaction.getRDFId());
       PhysicalEntity template = aTReaction.getTemplate();
       if (template != null)
    	   createNodesAndEdges(nodes, edges, template, interactionNode, BioPax3VisualStyleUtil.TEMPLATE);
       // Once again for the other side
       for(PhysicalEntity product: aTReaction.getProduct())
    	   createNodesAndEdges(nodes, edges, product, interactionNode, BioPax3VisualStyleUtil.PRODUCT);
   } 

   static void bpToGraph(Conversion aConversion, Map<String, CyNode> nodes, Map<Integer, CyEdge> edges) {
       CyNode interactionNode = nodes.get(aConversion.getRDFId());
       for(PhysicalEntity leftPEP: aConversion.getLeft())
    	   createNodesAndEdges(nodes, edges, leftPEP, interactionNode, MapBioPaxToCytoscape.LEFT);
       // Once again for the other side
       for(PhysicalEntity rightPEP: aConversion.getRight())
    	   createNodesAndEdges(nodes, edges, rightPEP, interactionNode, MapBioPaxToCytoscape.RIGHT);
   } 
   
       
   static void bpToGraph(Control aControl, Map<String, CyNode> nodes, Map<Integer, CyEdge> edges) {
	   CyNode interactionNode = nodes.get(aControl.getRDFId());
	   ControlType controlType = aControl.getControlType();
	   if (controlType==null) controlType = ControlType.ACTIVATION;  // e.g., Catalysis
	   String controlStr = controlType.toString();
	   
	   if (createNodesForControls) {
		   for(PhysicalEntity pep: aControl.getController()) {
			   createNodesAndEdges(nodes, edges, pep, interactionNode, MapBioPaxToCytoscape.CONTROLLER);
		   }
		   
		   for(Process aProcess: aControl.getControlled() ) {
			   if(aProcess instanceof Interaction) {
				   CyNode controlledNode = nodes.get(aProcess.getRDFId());
				   assert controlledNode != null; // Again because of part 2
				   CyEdge edge = createInteractionCyEdge(interactionNode, controlledNode, controlStr);				   
				   setEdgeType(edge, controlStr);
				   
				   edges.put(edge.getRootGraphIndex(), edge);
			   } // TODO: else? what we gonna do if it is a pathway? Wups?
		   }
		   
		   if (aControl instanceof Catalysis) {
			   for(PhysicalEntity pep: ((Catalysis) aControl).getCofactor()) {
				   createNodesAndEdges(nodes, edges, pep, interactionNode, MapBioPaxToCytoscape.COFACTOR);
			   }
		   }
	   }
	   else if (aControl.getController() == null) {
		   return;
	   }
	   else {
		   Set<CyNode> controllers = new HashSet<CyNode>();
		   for(PhysicalEntity pep: aControl.getController()) {
			   controllers.add(createNodes(nodes, edges, pep));
		   }
		   Set<CyNode> cofactors = new HashSet<CyNode>();
		   if (aControl instanceof Catalysis) {
			   for(PhysicalEntity pep: ((Catalysis) aControl).getCofactor()) {
				   cofactors.add(createNodes(nodes, edges, pep));
			   }
		   }
		   Set<Process> indirectlyControlled = new HashSet<Process>();
		   Set<Process> directlyControlled = new HashSet<Process>();
		   for(Process aProcess: aControl.getControlled() ) {
			   if(!(aProcess instanceof Interaction)) continue; // should probably complain.
			   if (aProcess instanceof Control) {
				   indirectlyControlled.addAll(((Control) aProcess).getControlled());
			   }
			   else directlyControlled.add(aProcess);
		   }
		   List<Process> allControlled = new ArrayList<Process>(indirectlyControlled);
		   allControlled.addAll(directlyControlled);
		   List<CyNode> allControllers = new ArrayList<CyNode>(controllers);
		   allControllers.addAll(cofactors);
		   
		   String tip = getNameSmart(aControl);
		   for (Process aProcess: allControlled) {
			   CyNode controlled = nodes.get(aProcess.getRDFId());
			   String lineStyle = BioPax3VisualStyleUtil.SOLID;
			   if (indirectlyControlled.contains(aProcess)) lineStyle = BioPax3VisualStyleUtil.DASHED;
			   for (CyNode controller: allControllers) {
				   String lineType = controlStr;
				   if (aControl instanceof Catalysis) lineType = BioPax3VisualStyleUtil.CATALYSIS;
				   if (cofactors.contains(controller)) lineType = MapBioPaxToCytoscape.COFACTOR;
				   CyEdge edge = createInteractionCyEdge(controller, controlled, controlStr);				   
				   setEdgeType(edge, lineType);
				   String id = edge.getIdentifier();
				   setEdgeAttribute(id, BioPax3VisualStyleUtil.BIOPAX_EDGE_STYLE, lineStyle);
				   setEdgeAttribute(id, BioPax3VisualStyleUtil.BIOPAX_EDGE_TIP, tip);
				   edges.put(edge.getRootGraphIndex(), edge);
			   } // TODO: else? what we gonna do if it is a pathway? Wups?
		   }
	   }
   }

	   
	   
   public static String getBPEntityType(BioPAXElement bpElement) {
		   String rawType = "", plainEng = rawType;

       if(bpElement != null) {
           // Thank God, Java hackers are really smart
           rawType = bpElement.getClass().getName();

           String[] tempStr = rawType.split("\\.");
           if( tempStr.length > 0 ) {
               rawType = tempStr[tempStr.length-1].replace("Impl", "");
//             plainEng =  BioPaxPlainEnglish.getTypeInPlainEnglish(rawType);
               plainEng =  BioPax3VisualStyleUtil.unCamel(rawType);
           }
       }
	   System.err.println("Entity type of "+bpElement+"("+rawType+") is "+plainEng);
       return plainEng;
   }
   
   public static CyNode getPEPStateNode(PhysicalEntity pep) {
       return createPEStateNode(null, pep);
   }

   private static void setNodeAttributes(String nodeID, Entity bpe) {
	   String name = getNameSmart(bpe),
	   sName = getShortNameSmart(bpe),
	   rdfID = bpe.getRDFId();
	   
	   setNodeAttribute(nodeID, Semantics.CANONICAL_NAME, name);
	   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_NAME, name);
	   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_SHORT_NAME, sName);
	   
	   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
			   getBPEntityType(bpe));
	   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_RDF_ID, rdfID);
	   
	   ArrayList<Object> objList = new ArrayList<Object>(bpe.getName());
	   ArrayList<String> synList = new ArrayList<String>();
	   for (Object name1: objList) {
		   if (name1 instanceof String) synList.add((String) name1);
	   }
	   setNodeListAttribute(nodeID, MapNodeAttributes.BIOPAX_SYNONYMS, synList);
	   
	   String comment = "";
	   for(String aComment: bpe.getComment())
		   comment += aComment + "<BR><BR>";
	   
	   if(!nul(comment))
//		   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_COMMENT, comment);
		   setNodeListAttribute(nodeID, MapNodeAttributes.BIOPAX_COMMENT, new ArrayList<String>(bpe.getComment()));
	   
	   String uniRefs = "", pubRefs = "", relRefs = "";
	   ArrayList<String> otherRefs = new ArrayList<String>();
	   
	   ArrayList<String> dbList = new ArrayList<String>();
	   
	   for(Xref ref: bpe.getXref()) {
		   String db = ref.getDb(), id = ref.getId();
		   if( nul(db) || nul(id)) continue;
		   if (db.equalsIgnoreCase("CPATH")) continue;
		   dbList.add( db );
		   String linkOut = ExternalLinkUtil.createLink(db,id);
		   
		   if( ref instanceof UnificationXref) {
			   uniRefs += "<LI>- " + linkOut + "</LI>";
		   } 
		   else if( ref instanceof PublicationXref) {
			   PublicationXref pRef = (PublicationXref) ref;
			   String pubTxt = "";
			   for(String anAuthor: pRef.getAuthor()) pubTxt += anAuthor + ", ";
			   if( !nul(pRef.getTitle())) pubTxt += pRef.getTitle();
			   
			   String sources = "";
			   for(String aSrc: pRef.getSource())
				   sources += aSrc + ", ";
			   if( pRef.getYear() > 0 )
				   sources += pRef.getYear();
			   pubTxt += (!nul(sources))? "(" + sources + ") " : sources;
			   
			   pubTxt += ExternalLinkUtil.createLink(pRef.getDb(), pRef.getId());
			   pubTxt += "<BR>";
			   pubRefs += pubTxt;
		   } else if( ref instanceof RelationshipXref ) {
			   relRefs += "<LI>- " + linkOut + "</LI>";
           } else {
               otherRefs.add(linkOut);
           }
       }

       if(!nul(uniRefs)) setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_UNIFICATION_REFERENCES, uniRefs);
       if(!nul(relRefs)) setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_RELATIONSHIP_REFERENCES, relRefs);
       if(!nul(pubRefs)) setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_PUBLICATION_REFERENCES, pubRefs);
       if(!otherRefs.isEmpty())
    	   setNodeListAttribute(nodeID, MapNodeAttributes.BIOPAX_AFFYMETRIX_REFERENCES_LIST, otherRefs);

       String avaliability = "";
       for( String anAvailability : bpe.getAvailability() )
           avaliability += anAvailability + "<BR>";
       if( avaliability.length() > 0 )
           setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_AVAILABILITY, avaliability);
       
       String datasources = "";
       for( Provenance aDataSrc: bpe.getDataSource() )
    	   for(String aName: aDataSrc.getName() )
    		   datasources += "<LI> - " + aName + "</LI>";
       if( datasources.length() > 0 )
    	   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_DATA_SOURCES, datasources);
       
       if( bpe instanceof SimplePhysicalEntity ) {
    	   SimplePhysicalEntity spe = (SimplePhysicalEntity ) bpe;
    	   EntityReference er = spe.getEntityReference();
    	   if (er instanceof SequenceEntityReference) {
    		   BioSource bioSrc = ((SequenceEntityReference) er).getOrganism();
    		   if( bioSrc != null
    				   && bioSrc.getTaxonXref() != null
    				   && bioSrc.getTaxonXref().getId() != null ) {
    			   
    			   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_ORGANISM_NAME, bioSrc.getStandardName());
    			   
    			   String httpLink = ExternalLinkUtil.createIHOPLink(getBPEntityType(bpe), synList, dbList,
    					   Integer.parseInt(bioSrc.getTaxonXref().getId()) );
    			   
    			   if( httpLink != null )
    				   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_IHOP_LINKS, "- " + httpLink);
    		   }
    	   }
       }
       
       String pathways = "";
       for(Interaction anInteraction: bpe.isParticipantsOf())
    	   for(Pathway aPathway: anInteraction.isPathwayComponentsOf() )
    		   pathways += aPathway.getName() + "<BR>";
       
       if( pathways.length() > 0 )
    	   setNodeAttribute(nodeID, MapNodeAttributes.BIOPAX_PATHWAY_NAME, pathways);
   }
   
   private static void setNodeAttributes(String nodeID, Entity bpe, String labelName) {
       setNodeAttributes(nodeID, bpe);
       setNodeAttribute(nodeID, BioPax3VisualStyleUtil.BIOPAX_NODE_LABEL, labelName);
   }

   private static CyNode createNodes(Map<String, CyNode> nodes, Map<Integer, CyEdge> edges,
           PhysicalEntity pep) {
	   CyNode peNode = createPEStateNode(nodes, pep);
       if(pep instanceof Complex) {
    	   for(PhysicalEntity aPEP: ((Complex) pep).getComponent())
    		   createNodesAndEdges(nodes, edges, aPEP, peNode, MapBioPaxToCytoscape.CONTAINS);
       }
       return peNode;
   }
   
   private static void createNodesAndEdges(Map<String, CyNode> nodes, Map<Integer, CyEdge> edges,
           PhysicalEntity pep, CyNode mainNode, String type) {
	   
	   CyNode peNode = createNodes(nodes,edges,pep);
       CyEdge edge;
       edge = createInteractionCyEdge(mainNode, peNode, type);
       setEdgeType(edge, type);
       edges.put(edge.getRootGraphIndex(), edge);
   }
   
   private static CyEdge createInteractionCyEdge(CyNode node1, CyNode node2, String type) {
	   System.err.println("new "+type+" edge: "+node1+" -> "+node2);
	   return getCyEdge(node1, node2, Semantics.INTERACTION, type, CREATE);
   }

   private static CyNode createPEStateNode(Map<String, CyNode> nodes, PhysicalEntity pe) {
       String rdfId= pe.getRDFId(), nodeId = rdfId;
       String nodeName = getNameSmart(pe);

       String chemicalModifications = "";
       Map<String, Integer> chemModCounts = new HashMap<String, Integer>();
       for(EntityFeature sf: pe.getFeature()) {
    	   if (!(sf instanceof ModificationFeature)) continue;
    	   SequenceModificationVocabulary modType = 
    		   ((ModificationFeature) sf).getModificationType();
    	   if(modType == null) continue;
    	   for( String aTerm: modType.getTerm() ) {
    		   Integer cnt = chemModCounts.get(aTerm);
    		   if (cnt != null) chemModCounts.put(aTerm,cnt+1);
    		   else {
    			   chemModCounts.put(aTerm, 1);
    			   chemicalModifications += stringOr(BioPaxUtil.getAbbrCellLocation(aTerm), aTerm);
    		   }
    	   }
       }
       
       if(!nul(chemicalModifications)) {
           nodeName += '-' + chemicalModifications;
           nodeId += '-' + chemicalModifications;
       }
      
       String cellularLocation = "";
       ControlledVocabulary ocv = pe.getCellularLocation();
       if( ocv != null && ocv.getTerm().iterator().hasNext() ) {
           String aTerm =  ocv.getTerm().iterator().next();
           String abbr = stringOr(BioPaxUtil.getAbbrCellLocation(aTerm),aTerm);
           if( abbr.length() > 0 )
               abbr = "(" + abbr + ")";

           nodeId += abbr;
           nodeName += (abbr.length() == 0) ? abbr : "\n" + abbr;
           cellularLocation = aTerm;
       }

       CyNode node = getCyNode(nodeId, CREATE);
       nodes.put(nodeId, node);
       setNodeAttributes(nodeId, pe, nodeName);

       if (chemModCounts.size() > 0) {
    	   List<String> chemModList = new ArrayList<String>(chemModCounts.size());
    	   for (String s: chemModCounts.keySet()) { 
    		   chemModList.add(stringOr(BioPaxUtil.getAbbrChemModification(s), s));
    	   }
    	   setNodeListAttribute(nodeId, 
    			   MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_LIST, 
    			   chemModList);
           if (chemModCounts.containsKey(BioPaxUtil.PHOSPHORYLATION_SITE)) {
        	   setNodeAttribute(nodeId, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
        			   BioPaxUtil.PROTEIN_PHOSPHORYLATED);
           }
       }
       setMultiHashMap(nodeId, nodeAttributes,
    		   MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_MAP, chemModCounts);

       if (!nul(cellularLocation)) {
           List<String> cellularLocationsList = new ArrayList<String>();
           cellularLocationsList.add(cellularLocation);
           setNodeListAttribute(nodeId, MapNodeAttributes.BIOPAX_CELLULAR_LOCATIONS,
											cellularLocationsList);
		}

       return node;
   }
}   


