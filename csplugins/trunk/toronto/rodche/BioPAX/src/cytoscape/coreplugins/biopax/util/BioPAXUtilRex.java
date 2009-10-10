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

package cytoscape.coreplugins.biopax.util;


import cytoscape.logger.*;

import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.level2.*;
import org.biopax.paxtools.model.level3.*;
import org.biopax.paxtools.io.simpleIO.SimpleExporter;
import org.biopax.paxtools.io.simpleIO.SimpleReader;
import cytoscape.coreplugins.biopax.mapping.MapNodeAttributes;
import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.style.BioPax3VisualStyleUtil;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.layout.CyLayoutAlgorithm;

import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * TODO compare to BioPaxUtil and merge/delete
 * 
 * @deprecated (Rex's version)
 */
public class BioPAXUtilRex {
	
	protected static boolean createNodesForControls = false;
	public static boolean getCreateNodesForControls() { return createNodesForControls; }
	public static void setCreateNodesForControls(Boolean b) { System.err.println(createNodesForControls = b); }
	public static void toggleCreateNodesForControls() {
		System.err.println(createNodesForControls = !createNodesForControls); }
	
	public static CyLayoutAlgorithm defaultLayoutAlgorithm;
	public static void setDefaultLayoutAlgorithm(CyLayoutAlgorithm algo) { defaultLayoutAlgorithm = algo; }
	public static CyLayoutAlgorithm getDefaultLayoutAlgorithm() { return defaultLayoutAlgorithm; }

	private static boolean inputLevel3 = true;
	public static boolean getInputLevel3() { return inputLevel3; }
	public static void setInputLevel3(Boolean b) { System.err.println(inputLevel3 = b); }
	public static void toggleInputLevel3() {
		System.err.println(inputLevel3 = !inputLevel3); }

    public static final int MAX_SHORT_NAME_LENGTH = 25;
    public static final String BIOPAX_MODEL_STRING = "biopax.model.xml";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String BIOPAX_MERGE_SRC = "biopax.merge.src";

    protected static Map<CyNetwork, Model> networkModelMap = new HashMap<CyNetwork, Model>();
	protected static final CyLogger log = CyLogger.getLogger(BioPAXUtilRex.class);


    protected static final boolean CREATE = true;
    
    // Just to shorten the names
    protected static CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    protected static CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
    protected static CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

    protected static void setNodeAttribute(String arg1, String arg2, String arg3) {
    	log.debug("setNodeAttribute("+arg1+","+arg2+","+arg3+")");
    	nodeAttributes.setAttribute(arg1, arg2, arg3);
    }
    protected static void setNodeListAttribute(String arg1, String arg2, List arg3) {
    	log.debug("setNodeListAttribute("+arg1+","+arg2+","+arg3+")");
    	nodeAttributes.setListAttribute(arg1, arg2, arg3);
    }
    
    protected static void setEdgeType(CyEdge edge, String type) {
    	String id = edge.getIdentifier();
    	setEdgeAttribute(id,  BioPax3VisualStyleUtil.BIOPAX_EDGE_TIP, type);
    	setEdgeAttribute(id,  MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE, type);
    	//setEdgeAttribute(id,  BioPax3VisualStyleUtil.BIOPAX_EDGE_MARK, type.substring(0,1));
    }
    
    protected static void setEdgeAttribute(String arg1, String arg2, String arg3) {
    	log.debug("setEdgeAttribute("+arg1+","+arg2+","+arg3+")");
    	edgeAttributes.setAttribute(arg1, arg2, arg3);
    }
    protected static void setNetAttribute(String arg1, String arg2, String arg3) {
    	log.debug("setNetAttribute("+arg1+","+arg2+","+arg3+")");
    	networkAttributes.setAttribute(arg1, arg2, arg3);
    }
    protected static CyNode getCyNode(String nodeID, boolean create) { 
    	return Cytoscape.getCyNode(nodeID, create); 
    }
    protected static CyEdge getCyEdge(CyNode node_1, CyNode node_2, String attribute, 
    		Object attribute_value, boolean create) {
    	return Cytoscape.getCyEdge(node_1, node_2, attribute, attribute_value, create);
    }

    public static String getNameSmart(entity e) {
    	return BioPAXL2Util.getNameSmart(e);
    }
    public static String getNameSmart(Entity e) {
    	return BioPAXL3Util.getNameSmart(e);
    }
    public static String getShortNameSmart(entity e) {
    	return BioPAXL2Util.getShortNameSmart(e);
    }
    public static String getShortNameSmart(Entity e) {
    	return BioPAXL3Util.getShortNameSmart(e);
    }
    public static CyNode getPEPStateNode(PhysicalEntity pe) {
        return BioPAXL3Util.getPEPStateNode(pe);
    }
    public static CyNode getPEPStateNode(physicalEntityParticipant pep) {
        return BioPAXL2Util.getPEPStateNode(pep);
    }

    protected static String wrapName(String name) {
    	if (name.length() <= MAX_SHORT_NAME_LENGTH) return name;
    	else return name.subSequence(0, MAX_SHORT_NAME_LENGTH-3) + "...";
    }

    public static CytoscapeGraphElements bioPAXtoCytoscapeGraph(Model biopaxModel) {
    	if (biopaxModel.getLevel() == BioPAXLevel.L2) {
    		return BioPAXL2Util.bioPAXtoCytoscapeGraph(biopaxModel);
    	}
    	else if (biopaxModel.getLevel() == BioPAXLevel.L3) {
    		return BioPAXL3Util.bioPAXtoCytoscapeGraph(biopaxModel);
    	}
    	else return null;
    }
    

    public static String getBPEntityType(BioPAXElement bpElement) {
        String rawType = "", plainEng = rawType;

        if(bpElement != null) {
            // Thank God, Java hackers are really smart
            rawType = bpElement.getClass().getName();

            String[] tempStr = rawType.split("\\.");
            if( tempStr.length > 0 ) {
                rawType = tempStr[tempStr.length-1].replace("Impl", "");
                System.err.println("didn't expect to be here!");
                plainEng =  BioPax3VisualStyleUtil.unCamel(rawType);
            }
        }

        return plainEng;
    }
    

    protected static void setMultiHashMap(String cyNodeId, CyAttributes attributes, String attributeName,
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

    public static void resetNetworkModel(CyNetwork cyNetwork) {
        networkModelMap.remove(cyNetwork);
        getNetworkModel(cyNetwork);
    }

    public static boolean setNetworkModel(CyNetwork cyNetwork, Model bpModel) {
        networkModelMap.put(cyNetwork, bpModel);

        SimpleExporter simpleExporter = new SimpleExporter(bpModel.getLevel());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            simpleExporter.convertToOWL(bpModel, outputStream);
        } catch (Exception e) {
            return false;
        }

        try {
            networkAttributes.setAttribute(cyNetwork.getIdentifier(), BIOPAX_MODEL_STRING,
                                                         outputStream.toString(DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            System.out.println(DEFAULT_CHARSET + " is not supported. BioPAX model could not be saved.");
        }

        return true;
    }

    public static Model getNetworkModel(CyNetwork cyNetwork) {
        Model bpModel = networkModelMap.get(cyNetwork);
        if( bpModel != null )
            return bpModel;

        String modelStr = (String) networkAttributes.getAttribute(cyNetwork.getIdentifier(), BIOPAX_MODEL_STRING);
        if( modelStr == null )
            return bpModel; // return null
        else
            modelStr = modelStr.replace("\\n", "\n"); // Hrr...

        ByteArrayInputStream inputStream;
        try {
            inputStream = new ByteArrayInputStream(modelStr.getBytes(DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            return bpModel; // return null
        }

        bpModel = new SimpleReader().convertFromOWL(inputStream);

        if( bpModel != null )
            setNetworkModel(cyNetwork, bpModel);

        return bpModel;
    }

    public static boolean isBioPAXNetwork(CyNetwork cyNetwork) {
        Object answer = networkAttributes.getAttribute(cyNetwork.getIdentifier(),
                MapBioPaxToCytoscape.BIOPAX_NETWORK);

        return (answer != null) && answer.equals(Boolean.TRUE);
    }

    public static class CytoscapeGraphElements {
        public Collection<CyNode> nodes;
        public Collection<CyEdge> edges;

        public CytoscapeGraphElements(Collection<CyNode> nodes, Collection<CyEdge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    public static String stringOr(String a, String b) {
  	   if ((a == null) || a.equals("")) return b==null? "": b;
  	   else return a;
     }
    
    public static String stringOr(String a, String b, String c) {
    	return stringOr(a, stringOr(b,c));
    }
    
    public static boolean nul(String s) {
 	   return ((s == null) || s.length()==0);
    }

}