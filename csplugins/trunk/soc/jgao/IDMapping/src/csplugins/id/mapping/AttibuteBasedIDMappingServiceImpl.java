/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.id.mapping;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;

import giny.model.Node;

import org.bridgedb.Xref;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author gjj
 */
public class AttibuteBasedIDMappingServiceImpl
        implements AttibuteBasedIDMappingService{

    /**
     * For each node in each network, given its attribute and the corresponding
     * source id types, create new attributes of the destination type.
     *
     * @param networks
     * @param mapSrcAttrIDTypes
     *      key: source attribute
     *      value: corresponding ID types
     * @param MapTgtIDTypeAttrNameAttrType
     *      key: target ID type
     *      value: attribute name.
     */
    public void map(final Set<CyNetwork> networks,
                    final Map<String,Set<DataSource>> mapSrcAttrIDTypes,
                    final Map<DataSource, String> MapTgtIDTypeAttrName
                    ) throws IDMapperException {
        // prepare source xrefs
        Set<Node> nodes = nodesUnion(networks);
        Map<Node,Set<Xref>> mapNodeSrcXrefs = prepareNodeSrcXrefs(nodes, mapSrcAttrIDTypes);
        Set<Xref> srcXrefs = srcXrefUnion(mapNodeSrcXrefs);

        // target id types
        Set<DataSource> tgtTypes = MapTgtIDTypeAttrName.keySet();

        // id mapping
        Set<IDMapper> idMappers = IDMappingClientManager.getSelectedIDMappers();
        Map<Xref, Set<Xref>>[] idMapping = new Map[idMappers.size()];
        int imap=0;
        for (IDMapper idMapper : idMappers) {
            idMapping[imap] = idMapper.mapID(srcXrefs, tgtTypes);
        }

        // define target attribute
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        for (String attrName : MapTgtIDTypeAttrName.values()) {
            byte attrType = nodeAttributes.getType(attrName);
            if (attrType!=CyAttributes.TYPE_STRING && attrType!=CyAttributes.TYPE_SIMPLE_LIST) {
                throw new java.lang.UnsupportedOperationException("Only String and List target attributes are supported.");
            }
        }

        // set target attribute
        Map<Node,Set<Xref>> mapNodeTgtXrefs = getNodeTgtXrefs(mapNodeSrcXrefs, idMapping);
        setTgtAttribute(mapNodeTgtXrefs, MapTgtIDTypeAttrName);

    }

    private Set<Node> nodesUnion(Set<CyNetwork> networks) {
        Set nodes = new HashSet();
        for (CyNetwork network : networks) {
			for (Iterator<Node> nodeIt = network.nodesIterator(); nodeIt.hasNext();) {
				nodes.add(new Integer((nodeIt.next()).getRootGraphIndex()));
			}
		}
        return nodes;
    }

    private Map<Node,Set<Xref>> prepareNodeSrcXrefs(Set<Node> nodes, Map<String,Set<DataSource>> mapSrcAttrIDTypes) {
        Map<Node,Set<Xref>> ret = new HashMap();
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();


        for (Node node : nodes) {
            Set<Xref> xrefs = new HashSet();
            ret.put(node, xrefs);

            String nodeID = node.getIdentifier();
            for (Map.Entry<String,Set<DataSource>> entryAttrIDTypes : mapSrcAttrIDTypes.entrySet()) {
                String attrName = entryAttrIDTypes.getKey();
                Set<DataSource> dss = entryAttrIDTypes.getValue();

                byte attrType = nodeAttributes.getType(attrName);
                if (attrType == CyAttributes.TYPE_SIMPLE_LIST) {
                    List attr = nodeAttributes.getListAttribute(nodeID, attrName);
                    for (Object obj : attr) {
                        String str = obj.toString();
                        for (DataSource ds : dss) {
                            xrefs.add(new Xref(str, ds));
                        }
                    }
                } else {
                    Object obj = nodeAttributes.getAttribute(nodeID, attrName).toString();
                    if (obj!=null) {
                        String str = obj.toString();
                        if (str.length()>0) {
                            for (DataSource ds : dss) {
                                xrefs.add(new Xref(str, ds));
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }

    private Set<Xref> srcXrefUnion(Map<Node,Set<Xref>> mapNodeSrcXrefs) {
        Set<Xref> ret = new HashSet();
        for (Set<Xref> xrefs : mapNodeSrcXrefs.values()) {
            ret.addAll(xrefs);
        }
        return ret;
    }

//    private void defineTgtAttributes(Map<String,Byte> attrNameType) {
//        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
//        MultiHashMapDefinition mmapDef = nodeAttributes.getMultiHashMapDefinition();
//
//        for (Map.Entry<String,Byte> entry : attrNameType.entrySet()) {
//            String attrname = entry.getKey();
//            if (java.util.Arrays.asList(nodeAttributes.getAttributeNames()).contains(attrname)) {
//                //throw new java.lang.UnsupportedOperationException("Cannot redefine existing attributes");
//                byte attrType = nodeAttributes.getType(attrname);
//                if (attrType!=CyAttributes.TYPE_STRING && attrType!=CyAttributes.TYPE_SIMPLE_LIST) {
//                    throw new java.lang.UnsupportedOperationException("Only String and List target attributes are supported.");
//                }
//            }
//
//            byte attrtype = entry.getValue();
//
//            byte[] keyTypes;
//            if (attrtype==CyAttributes.TYPE_STRING) {
//                    keyTypes = null;
//            } else if (attrtype==CyAttributes.TYPE_SIMPLE_LIST ) {
//                    keyTypes = new byte[] { MultiHashMapDefinition.TYPE_INTEGER };
//            } else {
//                    keyTypes = null;
//            }
//
//            mmapDef.defineAttribute(attrname,
//                                    MultiHashMapDefinition.TYPE_STRING,
//                                    keyTypes);
//        }
//    }

    private Map<Node,Set<Xref>> getNodeTgtXrefs (Map<Node,Set<Xref>> mapNodeSrcXrefs,
                                                 Map<Xref, Set<Xref>>[] idMappings) {
        Map<Node,Set<Xref>> mapNodeTgtXrefs = new HashMap();

        for (Map.Entry<Node,Set<Xref>> entryNodeXrefs : mapNodeSrcXrefs.entrySet()) {
            Node node = entryNodeXrefs.getKey();
            Set<Xref> tgtXrefs = new HashSet();
            Set<Xref> srcXrefs = entryNodeXrefs.getValue();
            for (Xref srcXref : srcXrefs) {
                for (Map<Xref, Set<Xref>> idMapping : idMappings) {
                    Set<Xref> xrefs = idMapping.get(srcXref);
                    if (xrefs!=null) {
                        tgtXrefs.addAll(xrefs);
                    }
                }
            }

            mapNodeTgtXrefs.put(node, tgtXrefs);
        }

        return mapNodeTgtXrefs;
    }

    private void setTgtAttribute(Map<Node,Set<Xref>> mapNodeTgtXrefs,
                                 Map<DataSource, String> MapTgtIDTypeAttrName) {
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        Map<String, Byte> mapAttrNameType = new HashMap();
        for (String attrName : MapTgtIDTypeAttrName.values()) {
            mapAttrNameType.put(attrName, nodeAttributes.getType(attrName));
        }

        for (Map.Entry<Node,Set<Xref>> entryNodeXrefs : mapNodeTgtXrefs.entrySet()) {
            // type wise
            Map<DataSource, Set<String>> mapDsIds = new HashMap();
            Set<Xref> tgtXrefs = entryNodeXrefs.getValue();
            for (Xref xref : tgtXrefs) {
                DataSource ds = xref.getDataSource();
                Set<String> ids = mapDsIds.get(ds);
                if (ids==null) {
                    ids = new HashSet();
                    mapDsIds.put(ds, ids);
                }
                ids.add(xref.getId());
            }
            
            // set attribute
            Node node = entryNodeXrefs.getKey();
            String nodeID = node.getIdentifier();
            for (Map.Entry<DataSource, Set<String>> entryDsIds : mapDsIds.entrySet()) {
                DataSource ds = entryDsIds.getKey();
                String attrName = MapTgtIDTypeAttrName.get(ds);
                if (attrName!=null) {
                    byte attrType = mapAttrNameType.get(attrName);
                    Set<String> ids = entryDsIds.getValue();
                    if (attrType==CyAttributes.TYPE_SIMPLE_LIST) {
                        nodeAttributes.setListAttribute(nodeID, attrName, new Vector(ids));
                    } else if (attrType==CyAttributes.TYPE_STRING) {
                        nodeAttributes.setAttribute(nodeID, attrName, ids.toString());
                    }
                }
            }

        }

    }
}
