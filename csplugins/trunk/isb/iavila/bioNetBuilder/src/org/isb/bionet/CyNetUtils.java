/**
 * CyNetUtils.java
 */
package org.isb.bionet;
import java.util.*;
import cern.colt.list.*;
import cytoscape.*;
import cytoscape.data.*;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.datasource.synonyms.*;

/**
 * A class with utility methods that manipulate CyNetworks for this plugin
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class CyNetUtils {
    
    /**
     * @param interactions a Vector of Hashtables, each representing an interaction
     * @param networkName the name of the newly created network
     * @return a new CyNetwork
     * @see org.isb.xmlrpc.client.InteractionDataClient for examples on  how the Hashtables look
     */
    public static CyNetwork makeNewNetwork (Vector interactions, String networkName, 
            SynonymsClient synClient, String [] labelOps, boolean genBankDef,boolean xrefs, boolean dburls){
        
        CyNetwork net = null;
        IntArrayList nodes = new IntArrayList();
        IntArrayList edges = new IntArrayList();
        HashSet nodeIDs = new HashSet(); 
        Iterator it = interactions.iterator();
        
        // create CyEdges:
        while(it.hasNext()){
            Hashtable interaction = (Hashtable)it.next();
            CyEdge edge = createEdge(interaction);
            if(edge == null){continue;}
            nodes.add(edge.getSource().getRootGraphIndex());
            nodes.add(edge.getTarget().getRootGraphIndex());
            edges.add(edge.getRootGraphIndex());
            nodeIDs.add(edge.getSource().getIdentifier());
            nodeIDs.add(edge.getTarget().getIdentifier());
        }//whie it.hasNext
        
        // Finally, create the network with the nodes and edges
        nodes.trimToSize();
        edges.trimToSize();
        net = Cytoscape.createNetwork(nodes.elements(), edges.elements(), networkName);
        
        Hashtable giToCN = null;
        //Hashtable prToCN = null;
        //Hashtable kToCN = null;
        try{
            giToCN = synClient.getSynonyms(SynonymsSource.GI_ID, new Vector(nodeIDs), SynonymsSource.COMMON_NAME);
            // the rest of node ids could be prolinks or kegg
            // NO! As it turns out, of the prolinks/kegg ids did not have a gi id, then they don't have a common name in the db either.
            //nodeIDs.removeAll(giToCN.keySet());
            //if(nodeIDs.size() > 0)
              //  prToCN = synClient.getSynonyms(SynonymsSource.PROLINKS_ID, new Vector(nodeIDs), SynonymsSource.COMMON_NAME);
            
           // nodeIDs.removeAll(prToCN.keySet());
            
            //if(nodeIDs.size() > 0)
              //  kToCN = synClient.getSynonyms(SynonymsSource.KEGG_ID, new Vector(nodeIDs), SynonymsSource.COMMON_NAME);
            
        }catch(Exception ex){ex.printStackTrace();}
        
        if(giToCN == null) return net;
        
        it = net.nodesIterator();
        while(it.hasNext()){
            CyNode node = (CyNode)it.next();
            String nodeid = node.getIdentifier();
            String commonName = (String)giToCN.get(nodeid);
            //if(commonName == null && prToCN != null) commonName = (String)prToCN.get(nodeid);
            //if(commonName == null && kToCN != null) commonName = (String)kToCN.get(nodeid);
            //if(commonName == null) commonName = nodeid;
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),Semantics.COMMON_NAME,commonName);
        }
        
        return net;
    }//makeNewNetwork
    
    
    /**
     * 
     * @param net the network to which new interactions will be added
     * @param interactions a Vector of Hashtables, each representing an interaction
     * @see org.isb.xmlrpc.client.InteractionDataClient for examples on  how the Hashtables look
     */
    public static void addInteractionsToNetwork (CyNetwork net, Vector interactions, SynonymsClient synClient, 
            String [] labelOps, boolean genBankDef,boolean xrefs, boolean dburls){
        
        IntArrayList edges = new IntArrayList();
        Iterator it = interactions.iterator();
        HashSet nodeIDs = new HashSet();
        // create the edges
        while(it.hasNext()){
            Hashtable interaction = (Hashtable)it.next();
            CyEdge edge = createEdge(interaction);
            if(edge != null){
                edges.add(edge.getRootGraphIndex());
                nodeIDs.add(edge.getTarget().getIdentifier());
                nodeIDs.add(edge.getSource().getIdentifier());
            }
        }//while it
        
        edges.trimToSize();
        net.restoreEdges(edges.elements());
       
        Hashtable giToCN = null;
        try{
            giToCN = synClient.getSynonyms(SynonymsSource.GI_ID, new Vector(nodeIDs), SynonymsSource.COMMON_NAME);
        }catch(Exception ex){ex.printStackTrace();}
        
        if(giToCN == null) return;
        
        it = net.nodesIterator();
        while(it.hasNext()){
            CyNode node = (CyNode)it.next();
            String commonName = (String)giToCN.get(node.getIdentifier());
            if(commonName == null) commonName = node.getIdentifier();
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),Semantics.COMMON_NAME,commonName);
            //Cytoscape.setNodeAttributeValue(node, Semantics.COMMON_NAME,commonName);
        }
        
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
                    //Cytoscape.setEdgeAttributeValue(edge, attribute, attValue);
                }
            }//if keys[i] is a String
        }//for i
        return edge;
    }//createEdge
    
    /**
     * For all nodes in the given network, it creates as a node attribute Rosetta Benchmark URLs 
     */
   
    public static void createHPFURLNodeAttribute (CyNetwork net){
        
        Iterator it = net.nodesIterator();
        
        while(it.hasNext()){
          CyNode node = (CyNode)it.next();
          String nodeID = node.getIdentifier();
          int index = nodeID.indexOf(":");
          if(index > 0){
              String url = "http://bench.bakerlab.org/cgi-bin/2ddb/bddb.cgi?si=112682726728836&s=cytoscape&ac="+nodeID;
              Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"HPFP", url);
              //Cytoscape.setNodeAttributeValue(node,"RosettaBenchmark",url);
          }
        }//while it.hasNext
    }
    
}//CyNetUtils