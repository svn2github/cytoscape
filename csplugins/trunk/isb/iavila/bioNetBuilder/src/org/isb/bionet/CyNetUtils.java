/**
 * CyNetUtils.java
 */
package org.isb.bionet;
import java.util.*;
import cern.colt.list.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.data.CyAttributes;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.datasource.synonyms.*;
import org.isb.bionet.gui.wizard.*;

/**
 * A class with utility methods that manipulate CyNetworks for this plugin
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class CyNetUtils {
    
    // Attribute names
    public static final String DEFINITION_ATT = "Definition";
    public static final String ALIASES_ATT = "Aliases";
    public static final String ALTERNATE_UID_ATT = "UIDs"; // confusing, but necessary!
    
    
    /**
     * @param interactions a Vector of Hashtables, each representing an interaction
     * @param networkName the name of the newly created network
     * @return a new CyNetwork
     * @see org.isb.xmlrpc.client.InteractionDataClient for examples on  how the Hashtables look
     */
    public static CyNetwork makeNewNetwork (Collection node_ids, Collection interactions, String networkName, 
            SynonymsClient synClient, String [] labelOps, Hashtable atts){
        
        System.out.println("CyNetUtils.makeNewNetwork: Num node_ids = " + node_ids.size() + " num interactions = " + interactions.size());
        
        CyNetwork net = null;
        IntArrayList nodes = new IntArrayList();
        IntArrayList edges = new IntArrayList();
        HashSet nodeIDs = new HashSet();
        HashSet edgeIDs = new HashSet();
        
        // create CyNodes in node_ids
        Iterator it = node_ids.iterator();
        while(it.hasNext()){
            String nodeID = (String)it.next();
            CyNode node = Cytoscape.getCyNode(nodeID, true);
            if(node == null){
                throw new IllegalStateException("Could not create/find node with ID = " + nodeID);
            }
            nodeIDs.add(node.getIdentifier());
            nodes.add(node.getRootGraphIndex());
        }
        
        System.out.println("CyNetUtilities.makeNewNetwork: Created " + nodeIDs.size() + " nodes from starting nodes.");
        
        it = interactions.iterator();
        // create CyEdges:
        while(it.hasNext()){
            Hashtable interaction = (Hashtable)it.next();
            CyEdge edge = createEdge(interaction);
            if(edge == null){continue;}
            int sourceIndex = edge.getSource().getRootGraphIndex(); 
            int targetIndex = edge.getTarget().getRootGraphIndex();
            if(!nodes.contains(sourceIndex)) nodes.add(sourceIndex);
            if(!nodes.contains(targetIndex)) nodes.add(targetIndex);
            edges.add(edge.getRootGraphIndex());
            edgeIDs.add(edge.getIdentifier());
            nodeIDs.add(edge.getSource().getIdentifier());
            nodeIDs.add(edge.getTarget().getIdentifier());
        }//whie it.hasNext
        
        // Create the network with the nodes and edges
        nodes.trimToSize();
        edges.trimToSize();
        net = Cytoscape.createNetwork(nodes.elements(), edges.elements(), networkName);
        setNodeLabelAndAliases(net,nodeIDs,synClient,labelOps);
        createAttributes(nodeIDs,edgeIDs,synClient,atts);
        
        // Remove:
        System.out.println("Edges that were already created: " + CyNetUtils.edges);
        
        return net;
    }//makeNewNetwork
    
    /**
     * Sets the Semantics.COMMON_NAME attribute for nodes, and an "ALIASES" attribute as well
     * 
     * @param net contains nodes to be labeled
     * @param nodeIDs list of GI ids for nodes in the network
     * @param synClient the client that will be requested for synonyms
     * @param labelOptions an array of Strings that represents the prioritized label options for nodes
     * @see org.isb.bionet.datasource.synonyms.SynonymsSource for label options
     */
    public static void setNodeLabelAndAliases (CyNetwork net, Collection nodeIDs, SynonymsClient synClient, String [] labelOptions){
        Hashtable giToLabel = new Hashtable();
        Vector nodeidVector = new Vector(nodeIDs);
        for (int i = 0; i < labelOptions.length; i++){
            Hashtable temp = null;
            try {
                temp = synClient.getSynonyms(SynonymsSource.GI_ID, nodeidVector, labelOptions[i]);
            }catch(Exception e){e.printStackTrace();}
            if(temp == null) continue;    
            Iterator it = giToLabel.keySet().iterator();
            // Make sure we don't replace mappings with higher ID priorities
            while(it.hasNext()) temp.remove(it.next());
            giToLabel.putAll(temp);
            if(giToLabel.size() == nodeIDs.size()) break;
        }
        
        Iterator it = net.nodesIterator();
        while(it.hasNext()){
            CyNode node = (CyNode)it.next();
            String nodeid = node.getIdentifier();
            String nodeName = "";
            Vector commonNames = (Vector)giToLabel.get(nodeid);
            if(commonNames == null || commonNames.size() == 0) nodeName = nodeid;
            else nodeName = (String)commonNames.get(0);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),Semantics.COMMON_NAME,nodeName);
            if(commonNames == null || commonNames.size() == 0) continue;
            String allNames = "";
            if(commonNames.size() == 1) continue;
            else{
                Iterator it2 = commonNames.iterator();
                while (it2.hasNext()) allNames += (String)it2.next() + ", ";
            }
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), ALIASES_ATT, allNames);
        }  
    }
    
    /**
     * 
     * @param net the network to which new interactions will be added
     * @param interactions a Vector of Hashtables, each representing an interaction
     * @see org.isb.xmlrpc.client.InteractionDataClient for examples on  how the Hashtables look
     */
    public static void addInteractionsToNetwork (CyNetwork net, Collection node_ids,Collection interactions, SynonymsClient synClient, 
            String [] labelOps,Hashtable atts){
        
        IntArrayList edges = new IntArrayList();
        IntArrayList nodes = new IntArrayList();
        HashSet nodeIDs = new HashSet();
        HashSet edgeIDs = new HashSet();
        
        // create CyNodes in node_ids
        Iterator it = node_ids.iterator();
        while(it.hasNext()){
            String nodeID = (String)it.next();
            CyNode node = Cytoscape.getCyNode(nodeID, true);
            if(node == null){
                throw new IllegalStateException("Could not create/find node with ID = " + nodeID);
            }
            nodeIDs.add(node.getIdentifier());
            nodes.add(node.getRootGraphIndex());
        }
        System.out.println("CyNetUtilities.addInteractionsToNetwork: Created " + nodeIDs.size() + " nodes from starting nodes.");
        it = interactions.iterator();
        // create the edges
        while(it.hasNext()){
            Hashtable interaction = (Hashtable)it.next();
            CyEdge edge = createEdge(interaction);
            if(edge != null){
                edges.add(edge.getRootGraphIndex());
                nodeIDs.add(edge.getTarget().getIdentifier());
                nodeIDs.add(edge.getSource().getIdentifier());
                edgeIDs.add(edge.getIdentifier());
            }
        }//while it
        edges.trimToSize();
        net.restoreNodes(nodes.elements());
        net.restoreEdges(edges.elements());
      
        setNodeLabelAndAliases(net,nodeIDs,synClient,labelOps);
        createAttributes(nodeIDs,edgeIDs,synClient,atts);
        
       
    }//addInteractionsToNetwork
    
    /**
     * Creates a CyEdge in Cytoscape with information obtained from the Hashtable
     * @param interaction a Hashtable that represents an edge
     * @return a CyEdge with edge attributes obtained from the Hashtable
     * @see org.isb.xmlrpc.client.InteractionDataClient for examples on  how the Hashtables look
     */
    // for testing, remove:
    public static int edges = 0;
    public static CyEdge createEdge (Hashtable interaction){
        edges = 0;
        String interactor1 = (String)interaction.get(InteractionsDataSource.INTERACTOR_1);
        if(interactor1 == null){
            System.out.println("Hashtable does not contain key " + InteractionsDataSource.INTERACTOR_1);
            return null;
        }
        
        String interactor2 = (String)interaction.get(InteractionsDataSource.INTERACTOR_2);
        if(interactor2 == null){
            System.out.println("Hashtable does not contain key " + InteractionsDataSource.INTERACTOR_2);
            return null;
        }

        String type = (String)interaction.get(InteractionsDataSource.INTERACTION_TYPE);
        if(type == null){
            System.out.println("Hashtable does not contain key " + InteractionsDataSource.INTERACTION_TYPE);
            return null;
        }
        
        // We have the minimum requirements to create an edge now
        CyNode node1 = Cytoscape.getCyNode(interactor1, true);
        if(node1 == null){
            throw new IllegalStateException("CyNode for interactor1 = [" + interactor1 + "] is null!");
        }
        
        CyNode node2 = Cytoscape.getCyNode(interactor2, true);
        if(node2 == null){
            throw new IllegalStateException("CyNode for interactor2 = [" + interactor2 + "] is null!");
        }

        // For testing:
        CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, type, false);
        if(edge != null){
            //System.out.println("Edge (" + node1.getIdentifier() + " " + type + " " + node2.getIdentifier() + ") exists.");
            edges++;
        }
        edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, type, true);
     
        if(edge == null){
            throw new IllegalStateException("CyEdge with nodes " + node1 + " and " + node2 + " of type " + type + " is null!");
            
        }
       
        
        // Now lets see what other information the interaction has to set as an edge/node attribute
        Object [] keys = interaction.keySet().toArray();
        for(int i = 0; i < keys.length; i++){
            if(keys[i] instanceof String){
                String attribute = (String)keys[i];
                
                if(!attribute.equals(InteractionsDataSource.INTERACTOR_1) &&
                        !attribute.equals(InteractionsDataSource.INTERACTOR_2) &&
                        !attribute.equals(InteractionsDataSource.INTERACTION_TYPE)){
                    
                    Object attValue = interaction.get(attribute);
                    String stringAttValue = "";
                    int numVals = 0;
                    if(attValue instanceof Vector){
                        Vector vAtt = (Vector)attValue;
                        numVals = vAtt.size();
                        Iterator it = vAtt.iterator();
                        if(numVals > 1){
                            // TODO: Remove (for testing purposes)
                            //throw new NullPointerException (attribute +" "+ vAtt);
                            if(it.hasNext()) stringAttValue = it.next().toString();
                            while(it.hasNext()) stringAttValue += "|" + it.next().toString();
                        }else if(numVals == 1)
                            stringAttValue = vAtt.get(0).toString();
                        else continue;
                    }else stringAttValue = attValue.toString();
                   
                    if(attribute.equals(InteractionsDataSource.INTERACTOR_1_IDS) && numVals > 1){    
                        Cytoscape.getNodeAttributes().setAttribute(edge.getSource().getIdentifier(),ALTERNATE_UID_ATT,stringAttValue);
                    }else if(attribute.equals(InteractionsDataSource.INTERACTOR_2_IDS) && numVals > 1){        
                        Cytoscape.getNodeAttributes().setAttribute(edge.getTarget().getIdentifier(),ALTERNATE_UID_ATT,stringAttValue);    
                    }else{
                        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),attribute,stringAttValue);
                    }
                }
            }//if keys[i] is a String
        }//for i
        return edge;
    }//createEdge
     
    /**
     * 
     * @param nodesIDs
     * @param edgesIDs
     * @param synonyms_client
     * @param def
     * @param xrefs
     * @param dburls
     */
    public static void createAttributes (Collection nodeIDs, Collection edgeIDs,SynonymsClient synonyms_client,Hashtable atts){
        
        Vector idVector = new Vector(nodeIDs);
        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        
        if( ((Boolean)atts.get(AttributesPanel.DEFS)).booleanValue() ){
            Hashtable defs = new Hashtable();
            try{
                defs =  synonyms_client.getDefinitions(idVector);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            Iterator it = defs.keySet().iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                String definition = (String)defs.get(nodeID);
                if(definition == null) continue;
                nodeAtts.setAttribute(nodeID, DEFINITION_ATT, definition);
            }//while
        
        }//def
        
        if( ((Boolean)atts.get(AttributesPanel.XREFS)).booleanValue() ){
            Hashtable xrefTable = new Hashtable();
            try{
               xrefTable = synonyms_client.getXrefIds(idVector);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            Iterator it = xrefTable.keySet().iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                Vector xids = (Vector)xrefTable.get(nodeID);
                if(xids == null) continue;
                Iterator it2 = xids.iterator();
                while(it2.hasNext()){
                    String anID = (String)it2.next();
                    int index = anID.indexOf(":");
                    if(index >= 0){
                        String attName = anID.substring(0,index);
                        anID = anID.substring(index + 1,anID.length());
                        String idList = nodeAtts.getStringAttribute(nodeID, attName);
                        if(idList != null && idList.length() > 0) idList += "|" + anID;
                        else idList = anID;
                        nodeAtts.setAttribute(nodeID,attName,idList);
                    }
                }//inner while
            }//while
            
        }//if xrefs
        
        if( ((Boolean)atts.get(AttributesPanel.GENE_NAME)).booleanValue() ){
            Hashtable geneNames = new Hashtable();
            try{
                geneNames = synonyms_client.getGeneNames(idVector);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            Iterator it = geneNames.keySet().iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                Vector names = (Vector)geneNames.get(nodeID);
                if(names == null || names.size() == 0) continue;
                String allNames = "";
                if(names.size() == 1) allNames = (String)names.get(0);
                else{
                    Iterator it2 = names.iterator();
                    allNames = (String)it2.next();
                    while(it2.hasNext()) allNames += "|" + (String)it2.next();
                }
                nodeAtts.setAttribute(nodeID,SynonymsSource.GENE_NAME,allNames);
            }
            
        }// if gene name
        
        if( ((Boolean)atts.get(AttributesPanel.PROD_NAME)).booleanValue() ){
            Hashtable prodNames = new Hashtable();
            try{
                prodNames = synonyms_client.getProdNames(idVector);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            Iterator it = prodNames.keySet().iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                Vector names = (Vector)prodNames.get(nodeID);
                if(names == null || names.size() == 0) continue;
                String allNames = "";
                if(names.size() == 1) allNames = (String)names.get(0);
                else{
                    Iterator it2 = names.iterator();
                    allNames = (String)it2.next();
                    while(it2.hasNext()) allNames += "|" + (String)it2.next();
                }
                nodeAtts.setAttribute(nodeID,SynonymsSource.PROD_NAME,allNames);
            }
        }// if prod name
        
        if( ((Boolean)atts.get(AttributesPanel.LOCUS_NAME)).booleanValue() ){
            Hashtable locNames = new Hashtable();
            try{
                locNames = synonyms_client.getSynonyms(InteractionsHandler.UNIVERSAL_GENE_ID_TYPE,idVector, SynonymsSource.ORF_ID);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            Iterator it = locNames.keySet().iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                Vector names = (Vector)locNames.get(nodeID);
                if(names == null || names.size() == 0) continue;
                String allNames = "";
                if(names.size() == 1) allNames = (String)names.get(0);
                else{
                    Iterator it2 = names.iterator();
                    allNames = (String)it2.next();
                    while(it2.hasNext()) allNames += "|" + (String)it2.next();
                }
                nodeAtts.setAttribute(nodeID,SynonymsSource.ORF_ID,allNames);
            }
        }// if loc name
        
        
        if(((Boolean)atts.get(AttributesPanel.ENCODED_BY)).booleanValue() ){
            Hashtable encodedTable = new Hashtable();
            try{
                encodedTable = synonyms_client.getEncodedBy(idVector);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            Iterator it = encodedTable.keySet().iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                String name = (String) encodedTable.get(nodeID);
                if(name == null) continue;
                nodeAtts.setAttribute(nodeID,AttributesPanel.ENCODED_BY,name);
            } 
        }// if encoded by
        
        if( ((Boolean)atts.get(AttributesPanel.DB_URLS)).booleanValue() ){
             // These use GI numbers:
            String prolinksURL = "http://mysql5.mbi.ucla.edu/cgi-bin/functionator/pronav?seq_id=";
            String refseqURL = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=";
            String pirURL = "http://pir.georgetown.edu/cgi-bin/ipcEntry?id=";// append PIR id like O75936
            
            // We need to get the KEGG ids
            Hashtable giToKegg = null;
            try{
                giToKegg = synonyms_client.getSynonyms(SynonymsSource.GI_ID, idVector,SynonymsSource.KEGG_ID);
            }catch(Exception e){
                e.printStackTrace();
            }
            String keggURL = "http://www.genome.jp/dbget-bin/www_bget?";
            
            // We need to get UniProt ids
//            Hashtable giToUniprot = null;
//            try{
//                giToUniprot = synonyms_client.getSynonyms(SynonymsSource.GI_ID, idVector,SynonymsSource.UNIPROT_ID);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
            
            // We need to get the PIR ids
            Hashtable giToPir = null;
            try{
                giToPir = synonyms_client.getSynonyms(SynonymsSource.GI_ID, idVector,SynonymsSource.PIR_ID);
            }catch(Exception e){
                e.printStackTrace();
            }
            
          
            // The attribute names
            String prolinksAttName = ProlinksInteractionsSource.NAME + "_URL";
            String keggAttName = KeggInteractionsSource.NAME + "_URL";
            String refseqAttName = "RefSeq_URL";
            String pirAttName = "PIR_URL";
            
            //TODO: Get all the possible GI ids for each.
            Iterator it = nodeIDs.iterator();
            while (it.hasNext()){
            
                String nodeid = (String)it.next();
                int index = nodeid.indexOf(SynonymsSource.GI_ID + ":");
                if(index < 0) continue;
                
                String gi = nodeid.substring(index+ SynonymsSource.GI_ID.length() + 1);
                
                // Prolinks: seq_id is GI
                // http://mysql5.mbi.ucla.edu/cgi-bin/functionator/pronav?seq_id=1502861&tab=general
                nodeAtts.setAttribute(nodeid,prolinksAttName,prolinksURL + gi + "&tab=general");
                
                // KEGG: need to get the KEGG gene id
                // http://www.genome.jp/dbget-bin/www_bget?aae:aq_021
                Vector keggIDs = (Vector)giToKegg.get(nodeid);
                int keggIDNum = 0;
                if(keggIDs != null){
                    Iterator it2 = keggIDs.iterator();
                    while(it2.hasNext()){
                        String keggid = (String)it2.next();
                        index = keggIDs.indexOf(SynonymsSource.KEGG_ID + ":");
                        if(index >= 0){
                           keggid = keggid.substring(index + SynonymsSource.KEGG_ID.length() + 1);
                           if(keggIDNum > 0){
                               nodeAtts.setAttribute(nodeid, keggAttName + Integer.toString(keggIDNum), keggURL + keggid);
                               keggIDNum++;
                           }else{
                               nodeAtts.setAttribute(nodeid,keggAttName, keggURL + keggid);
                               keggIDNum++;
                           }
                        }
                    }                   
                }
                
                // RefSeq: val is GI
                // http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=71836199
                nodeAtts.setAttribute(nodeid,refseqAttName,refseqURL + gi);
            
                // UniProt: needs TrEMBL id (need to add GI to TrEMBL mapping method to synonyms)
                
                Vector pirs = (Vector)giToPir.get(nodeid);
                int prNum = 0;
                if(pirs != null){
                    Iterator it2 = pirs.iterator();
                    while(it2.hasNext()){
                        String id = (String)it2.next();
                        index = id.indexOf(":");
                        if(index >= 0){
                            id = id.substring(index + 1, id.length());
                           if(prNum > 0){ 
                               nodeAtts.setAttribute(nodeid, pirAttName + Integer.toString(prNum), pirURL + id);
                               prNum++;
                           }else{
                               nodeAtts.setAttribute(nodeid,pirAttName, pirURL + id);
                               prNum++;
                           }
                        }// index >= 0
                    } //while   
                }// if tremblids != null
            
            }// for a nodeid
            
        }// if db urls
        
        if( ((Boolean)atts.get(AttributesPanel.HPFP)).booleanValue() ){
            Iterator it = nodeIDs.iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                int index = nodeID.indexOf(":");
                if(index > 0){
                    String url = "http://bench.bakerlab.org/cgi-bin/2ddb/bddb.cgi?si=112682726728836&s=cytoscape&ac="+nodeID;
                    Cytoscape.getNodeAttributes().setAttribute(nodeID,"HPFP_URL", url);
                }
              }//while it.hasNext
        }// if db urls
   
    }
    
}//CyNetUtils










