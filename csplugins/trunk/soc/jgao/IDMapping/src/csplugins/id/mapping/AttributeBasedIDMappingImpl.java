/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package csplugins.id.mapping;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.task.TaskMonitor;

import giny.model.Node;

import org.bridgedb.Xref;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperStack;
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
public class AttributeBasedIDMappingImpl
        implements AttributeBasedIDMapping{
    protected TaskMonitor taskMonitor;
    protected boolean interrupted;
    protected String report;

    public void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
        interrupted = false;
    }

    public void interrupt() {
            interrupted = true;
            report = "Aborted!";
     }

    public String getReport() {
        return report;
    }

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
        updateTaskMonitor("Mapping IDs...");
        IDMapperStack idMapperStack = IDMappingClientManager.selectedIDMapperStack();
        Map<Xref, Set<Xref>> idMapping = idMapperStack.mapID(srcXrefs, tgtTypes);

        // define target attribute
        defineTgtAttributes(new HashSet(MapTgtIDTypeAttrName.values()));

        // set target attribute
        Map<Node,Set<Xref>> mapNodeTgtXrefs = getNodeTgtXrefs(mapNodeSrcXrefs, idMapping);
        setTgtAttribute(mapNodeTgtXrefs, MapTgtIDTypeAttrName);

        report = "Identifiers mapped for "+mapNodeTgtXrefs.size()+" nodes (out of "+nodes.size()+")!";
        updateTaskMonitor(report,100);
    }

    private Set<Node> nodesUnion(Set<CyNetwork> networks) {
        int nNode = 0;
        for (CyNetwork network : networks) {
            nNode += network.getNodeCount();
        }

        Set nodes = new HashSet();

        int i = 0;
        for (CyNetwork network : networks) {
			for (Iterator<Node> nodeIt = network.nodesIterator(); nodeIt.hasNext();) {
                if (interrupted) return null;
                updateTaskMonitor("Retrieving nodes...\n"+i+"/"+nNode,(i+1)*100/nNode);
                i++;

				nodes.add(nodeIt.next());
			}
		}
        return nodes;
    }

    private Map<Node,Set<Xref>> prepareNodeSrcXrefs(Set<Node> nodes, Map<String,Set<DataSource>> mapSrcAttrIDTypes) {
        Map<Node,Set<Xref>> ret = new HashMap();
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

        int nNode = nodes.size();
        int i=0;
        for (Node node : nodes) {
            if (interrupted) return null;
            updateTaskMonitor("Preparing cross reference for nodes...\n"+i+"/"+nNode,(i+1)*100/nNode);
            i++;

            Set<Xref> xrefs = new HashSet();
            ret.put(node, xrefs);

            String nodeID = node.getIdentifier();
            for (Map.Entry<String,Set<DataSource>> entryAttrIDTypes : mapSrcAttrIDTypes.entrySet()) {
                String attrName = entryAttrIDTypes.getKey();
                Set<DataSource> dss = entryAttrIDTypes.getValue();

                //TODO: remove this in Cy3
                if (attrName.compareTo("ID")==0) {
                    for (DataSource ds : dss) {
                        xrefs.add(new Xref(nodeID, ds));
                    }
                    continue;
                }

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

    private Map<Node,Set<Xref>> getNodeTgtXrefs (Map<Node,Set<Xref>> mapNodeSrcXrefs,
                                                 Map<Xref, Set<Xref>> idMapping) {
        Map<Node,Set<Xref>> mapNodeTgtXrefs = new HashMap();

        for (Map.Entry<Node,Set<Xref>> entryNodeXrefs : mapNodeSrcXrefs.entrySet()) {
            Node node = entryNodeXrefs.getKey();
            Set<Xref> tgtXrefs = new HashSet();
            Set<Xref> srcXrefs = entryNodeXrefs.getValue();
            //TODO: deal with ambiguity--same node, same attribute, different data source
            for (Xref srcXref : srcXrefs) {
                Set<Xref> xrefs = idMapping.get(srcXref);
                if (xrefs!=null) {
                    tgtXrefs.addAll(xrefs);
                }
            }

            if (!tgtXrefs.isEmpty())
                mapNodeTgtXrefs.put(node, tgtXrefs);
        }

        return mapNodeTgtXrefs;
    }

    private void defineTgtAttributes(Set<String> attrs) {
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        List<String> attrNames = java.util.Arrays.asList(nodeAttributes.getAttributeNames());

        MultiHashMapDefinition mmapDef = nodeAttributes.getMultiHashMapDefinition();

        for (String attr : attrs) {
            if (attrNames.contains(attr)) {
                // for existing attribute, check if its type is String or List
                byte attrType = nodeAttributes.getType(attr);
                if (attrType!=CyAttributes.TYPE_STRING && attrType!=CyAttributes.TYPE_SIMPLE_LIST) {
                    throw new java.lang.UnsupportedOperationException("Only String and List target attributes are supported.");
                }
            } else {
                // define the new attribute as List
                byte[] keyTypes = new byte[] { MultiHashMapDefinition.TYPE_INTEGER };
                mmapDef.defineAttribute(attr,
                                    MultiHashMapDefinition.TYPE_STRING,
                                    keyTypes);
            }
        }
    }

    private void setTgtAttribute(Map<Node,Set<Xref>> mapNodeTgtXrefs,
                                 Map<DataSource, String> MapTgtIDTypeAttrName) {
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        Map<String, Byte> mapAttrNameType = new HashMap();
        for (String attrName : MapTgtIDTypeAttrName.values()) {
            mapAttrNameType.put(attrName, nodeAttributes.getType(attrName));
        }

        int i = 0;
        int nNode = mapNodeTgtXrefs.size();
        for (Map.Entry<Node,Set<Xref>> entryNodeXrefs : mapNodeTgtXrefs.entrySet()) {
            if (interrupted) return;
            updateTaskMonitor("Preparing cross reference for nodes...\n"+i+"/"+nNode,(i+1)*100/nNode);
            i++;

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
                        // only returns the first ID
                        //TODO: is that a way to get the "best" one?
                        if (!ids.isEmpty()) {
                            nodeAttributes.setAttribute(nodeID, attrName, ids.iterator().next());
                        }
                    }
                }
            }

        }

    }

    private void updateTaskMonitor(String status, int percentage) {
        if (this.taskMonitor!=null) {
            taskMonitor.setStatus(status);
            taskMonitor.setPercentCompleted(percentage);
        }
    }

    private void updateTaskMonitor(String status) {
        if (this.taskMonitor!=null) {
            taskMonitor.setStatus(status);
        }
    }
}
