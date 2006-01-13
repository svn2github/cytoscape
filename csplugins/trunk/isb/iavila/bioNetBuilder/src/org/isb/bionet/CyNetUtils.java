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
    
    
    /**
     * @param interactions a Vector of Hashtables, each representing an interaction
     * @param networkName the name of the newly created network
     * @return a new CyNetwork
     * @see org.isb.xmlrpc.client.InteractionDataClient for examples on  how the Hashtables look
     */
    public static CyNetwork makeNewNetwork (Collection node_ids, Collection interactions, String networkName, 
            SynonymsClient synClient, String [] labelOps, Hashtable atts){
        
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
        
        Hashtable giToLabel = new Hashtable();
        try{
            for (int i = 0; i < labelOps.length; i++){
                Hashtable temp = synClient.getSynonyms(SynonymsSource.GI_ID, new Vector(nodeIDs), labelOps[i]);
                it = giToLabel.keySet().iterator();
                // Make sure we don't replace mappings with higher ID priorities
                while(it.hasNext()){
                    temp.remove(it.next());
                }
                giToLabel.putAll(temp);
                if(giToLabel.size() == nodeIDs.size()){
                    // all nodes have a label
                    break;
                }
            }
            
        }catch(Exception ex){ex.printStackTrace();}
        
        it = net.nodesIterator();
        while(it.hasNext()){
            CyNode node = (CyNode)it.next();
            String nodeid = node.getIdentifier();
            String commonName = (String)giToLabel.get(nodeid);
            if(commonName == null) commonName = nodeid;
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),Semantics.COMMON_NAME,commonName);
        }
        
        createAttributes(nodeIDs,edgeIDs,synClient,atts);
        return net;
    }//makeNewNetwork
    
    
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
      
        Hashtable giToLabel = new Hashtable();
        try{
            for (int i = 0; i < labelOps.length; i++){
                Hashtable temp = synClient.getSynonyms(SynonymsSource.GI_ID, new Vector(nodeIDs), labelOps[i]);
                it = giToLabel.keySet().iterator();
                // Make sure we don't replace mappings with higher ID priorities
                while(it.hasNext()){
                    temp.remove(it.next());
                }
                giToLabel.putAll(temp);
                if(giToLabel.size() == nodeIDs.size()){
                    // all nodes have a label
                    break;
                }
            }
            
        }catch(Exception ex){ex.printStackTrace();}
        
        it = net.nodesIterator();
        while(it.hasNext()){
            CyNode node = (CyNode)it.next();
            String nodeid = node.getIdentifier();
            String commonName = (String)giToLabel.get(nodeid);
            if(commonName == null) commonName = nodeid;
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),Semantics.COMMON_NAME,commonName);
        }
        
        createAttributes(nodeIDs,edgeIDs,synClient,atts);
    }//addInteractionsToNetwork
    
    /**
     * Creates a CyEdge in Cytoscape with information obtained from the Hashtable
     * @param interaction a Hashtable that represents an edge
     * @return a CyEdge with edge attributes obtained from the Hashtable
     * @see org.isb.xmlrpc.client.InteractionDataClient for examples on  how the Hashtables look
     */
    public static CyEdge createEdge (Hashtable interaction){
        
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

        CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, type, true);
     
        if(edge == null){
            throw new IllegalStateException("CyEdge with nodes " + node1 + " and " + node2 + " of type " + type + " is null!");
        }
       
        
        // Now lets see what other information the interaction has to set as an edge attribute
        Object [] keys = interaction.keySet().toArray();
        for(int i = 0; i < keys.length; i++){
            if(keys[i] instanceof String){
                String attribute = (String)keys[i];
                if(!attribute.equals(InteractionsDataSource.INTERACTOR_1) &&
                        !attribute.equals(InteractionsDataSource.INTERACTOR_2) &&
                        !attribute.equals(InteractionsDataSource.INTERACTION_TYPE)){
                    Object attValue = interaction.get(attribute);
                    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),attribute,attValue.toString());
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
                Iterator it2 = xids.iterator();
                while(it2.hasNext()){
                    String anID = (String)it2.next();
                    int index = anID.indexOf(":");
                    if(index >= 0){
                        String attName = anID.substring(0,index);
                        nodeAtts.setAttribute(nodeID,attName,anID);
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
                String name = (String)geneNames.get(nodeID);
                nodeAtts.setAttribute(nodeID,SynonymsSource.GENE_NAME,name);
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
                String name = (String)prodNames.get(nodeID);
                nodeAtts.setAttribute(nodeID,SynonymsSource.PROD_NAME,name);
            }
        }// if prod name
        
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
                nodeAtts.setAttribute(nodeID,AttributesPanel.ENCODED_BY,name);
            } 
        }// if encoded by
        
        if( ((Boolean)atts.get(AttributesPanel.DB_URLS)).booleanValue() ){
            // For now do nothing
        }// if db urls
        
        if( ((Boolean)atts.get(AttributesPanel.HPFP)).booleanValue() ){
            Iterator it = nodeIDs.iterator();
            while(it.hasNext()){
                String nodeID = (String)it.next();
                int index = nodeID.indexOf(":");
                if(index > 0){
                    String url = "http://bench.bakerlab.org/cgi-bin/2ddb/bddb.cgi?si=112682726728836&s=cytoscape&ac="+nodeID;
                    Cytoscape.getNodeAttributes().setAttribute(nodeID,"HPFP", url);
                }
              }//while it.hasNext
        }// if db urls
        
    }
    
}//CyNetUtils










