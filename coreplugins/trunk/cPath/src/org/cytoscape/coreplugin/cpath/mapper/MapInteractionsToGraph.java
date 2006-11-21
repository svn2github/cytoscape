/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath.mapper;

import csplugins.task.BaseTask;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import giny.model.Node;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.CommonVocab;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.mapper.Mapper;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.types.RoleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Maps DataService Interaction objects to Cytoscape Node/Edge Objects.
 * This mapper will work on a new empty CyNetwork, or an existing CyNetwork
 * with pre-existing data.  If the CyNetwork has pre-existing nodes/edges,
 * the mapper will automatically check for duplicates when new interactions
 * are added.
 *
 * @author Ethan Cerami
 */
public class MapInteractionsToGraph implements Mapper {
    /**
     * Spoke View.
     */
    public static final int SPOKE_VIEW = 1;

    /**
     * Matrix View.
     */
    public static final int MATRIX_VIEW = 2;

    /**
     * ROOT_GRAPH_INDEXES Attribute Name.
     */
    public static final String ROOT_GRAPH_INDEXES = "ROOT_GRAPH_INDEXES";

    /**
     * Data Service Interactor Reference
     */
    public static final String DS_INTERACTOR = "DS_INTERACTOR";

    /**
     * Data Service Interaction Reference
     */
    public static final String DS_INTERACTION = "DS_INTERACTION";

    /**
     * CyNetwork Object
     */
    protected CyNetwork cyNetwork;

    /**
     * CyMap Object.
     */
    private HashMap cyMap;

    /**
     * ArrayList of Interaction Objects.
     */
    private ArrayList interactions;

    /**
     * Graph Type, e.g. SPOKE_VIEW or MATRIX_VIEW.
     */
    private int graphType;

    /**
     * List of Warnings.
     */
    private ArrayList warnings = new ArrayList();

    /**
     * If Number of Intearctors <= MATRIX_CUT_OFF then
     * do Matrix View.  Otherwise, report a warning.
     */
    private static final int MATRIX_CUT_OFF = 5;

    /**
     * Open Paren Constant.
     */
    protected static final String OPEN_PAREN = " (";

    /**
     * Close Paren Constant.
     */
    protected static final String CLOSE_PAREN = ") ";

    /**
     * Base Task Reference.
     */
    private BaseTask task;

    /**
     * Constructor.
     * The graphType parameter determines the method of drawing interactions
     * when the number of interactors > 2.
     * <p/>
     * For example, consider we have an interaction defined for (A, B, C).
     * <p/>
     * If graphType is set to SPOKE_VIEW and A is the "bait" interactor, the
     * mapper will draw the following graph:
     * <p/>
     * A <--> B
     * A <--> C
     * <p/>
     * This looks like a "spoke", with A at the center of the spoke.  Note that
     * the mapper will not draw an edge between B and C.  In order to properly
     * draw a spoke view, one of the interactors must be designated as "bait".
     * If graphType is set to SPOKE_VIEW, but there is no "bait" interactor,
     * a MapperException will be thrown.
     * <p/>
     * If graphType is set to MATRIX_VIEW, the mapper will draw the following
     * graph:
     * <p/>
     * A <--> B
     * A <--> C
     * B <--> C
     * <p/>
     * In the matrix view, each node interacts with all other nodes, and
     * therefore there is now an edge between B and C.  The matrix view does
     * not require a "bait" interactor.
     *
     * @param interactionList interactionList ArrayList of Interaction objects.
     * @param cyNetwork       CyNetwork
     * @param graphType       graphType (SPOKE_VIEW or MATRIX_VIEW).
     */
    public MapInteractionsToGraph(ArrayList interactionList,
            CyNetwork cyNetwork, int graphType) {
        if (graphType < SPOKE_VIEW || graphType > MATRIX_VIEW) {
            throw new IllegalArgumentException("Illegal GraphType Parameter.");
        }
        this.cyMap = new HashMap();
        this.interactions = interactionList;
        this.cyNetwork = cyNetwork;
        this.graphType = graphType;
    }

    /**
     * Sets the Base Task.
     *
     * @param task Base Task.
     */
    public void setBaseTask(BaseTask task) {
        this.task = task;
    }

    /**
     * Gets a Map of all Interactors/Interactions indexed by Node/Edge ID.
     *
     * @return HashMap Object.
     */
    public HashMap getCyMap() {
        return this.cyMap;
    }

    /**
     * Perform Mapping.
     *
     * @throws MapperException Indicates Error in mapping.
     */
    public final void doMapping() throws MapperException {
        //  Collect all existing nodes and edges to avoid redundancy.
        HashMap nodeMap = createNodeMap();
        HashMap edgeMap = createEdgeMap();

        //  Validate Interaction Data
        validateInteractions();

        //  First pass, add all new nodes.
        addNewNodes(nodeMap);

        //  Second pass, add all new interactions.
        addNewEdges(nodeMap, edgeMap);
    }

    /**
     * Gets Mapping Warnings.
     *
     * @return Mapping Warnings.
     */
    public ArrayList getWarnings() {
        return this.warnings;
    }

    /**
     * Validates Interactions.
     *
     * @throws MapperException Mapping Exception.
     */
    private void validateInteractions() throws MapperException {
        String errorMsg = "In order to correctly graph your interactions, "
                + "each interaction must specify exactly "
                + "one bait value.";
        if (task != null) {
            task.setProgressMessage("Validating Interactions");
            task.setMaxProgressValue(interactions.size());
        }
        if (graphType == SPOKE_VIEW) {
            for (int i = 0; i < interactions.size(); i++) {
                Interaction interaction = (Interaction) interactions.get(i);
                ArrayList interactors = interaction.getInteractors();
                if (interactors.size() > 2) {
                    HashMap baitMap = (HashMap) interaction.getAttribute
                            (InteractionVocab.BAIT_MAP);
                    if (baitMap == null) {
                        throw new MapperException(errorMsg);
                    } else {
                        Interactor bait = determineBait(interactors, baitMap);
                        if (bait == null) {
                            throw new MapperException(errorMsg);
                        }
                    }
                }
                if (task != null) {
                    task.setProgressValue(i);
                }
            }
        }
    }

    /**
     * Adds New Nodes to Network.
     *
     * @param nodeMap HashMap of current nodes.
     */
    private void addNewNodes(HashMap nodeMap) {
        if (task != null) {
            task.setProgressMessage("Adding Nodes to Network");
            task.setMaxProgressValue(interactions.size());
        }
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            ArrayList interactors = interaction.getInteractors();
            for (int j = 0; j < interactors.size(); j++) {
                Interactor interactor = (Interactor) interactors.get(j);
                addNode(interactor, nodeMap);
            }
            if (task != null) {
                task.setProgressValue(i);
            }
        }
    }

    /**
     * Adds New edges to Network.
     *
     * @param nodeMap Current Nodes.
     * @param edgeMap Current Edges.
     */
    private void addNewEdges(HashMap nodeMap, HashMap edgeMap) {
        if (task != null) {
            task.setProgressMessage("Adding Edges to Network");
            task.setMaxProgressValue(interactions.size());
        }
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            ArrayList interactors = interaction.getInteractors();

            if (graphType == MATRIX_VIEW) {
                doMatrixView(interactors, nodeMap, interaction, edgeMap);
            } else {
                doSpokeView(interactors, nodeMap, interaction, edgeMap);
            }
            if (task != null) {
                task.setProgressValue(i);
            }
        }
    }

    /**
     * Map to MATRIX_VIEW Graph Type
     */
    private void doMatrixView(ArrayList interactors, HashMap nodeMap,
            Interaction interaction, HashMap edgeMap) {
        if (interactors.size() <= MATRIX_CUT_OFF) {
            for (int j = 0; j < interactors.size(); j++) {
                for (int k = j + 1; k < interactors.size(); k++) {
                    //  Get Interactors
                    Interactor interactor1 = (Interactor) interactors.get(j);
                    Interactor interactor2 = (Interactor) interactors.get(k);

                    //  Conditionally Create Edge
                    createEdge(interactor1, interactor2, interaction, nodeMap,
                            edgeMap);
                }
            }
        } else {
            ExternalReference refs[] = interaction.getExternalRefs();
            StringBuffer refList = new StringBuffer();
            if (refs != null && refs.length > 0) {
                for (int i = 0; i < refs.length; i++) {
                    String db = refs[i].getDatabase();
                    String id = refs[i].getId();
                    refList.append("[" + db + ":" + id + "] ");
                }
            } else {
                refList.append("[No Ids available]");
            }

            String warningMsg = new String("Interaction contains more"
                    + " than " + MATRIX_CUT_OFF
                    + " interactors.  The interaction will not be mapped to "
                    + " any Cytoscape edges.  The offending interaction is"
                    + " identified with the following identifiers:  "
                    + refList);
            warnings.add(warningMsg);
        }
    }

    /**
     * Map to SPOKE_VIEW Graph Type
     */
    private void doSpokeView(ArrayList interactors, HashMap nodeMap,
            Interaction interaction, HashMap edgeMap) {
        HashMap baitMap = (HashMap) interaction.getAttribute
                (InteractionVocab.BAIT_MAP);

        if (interactors.size() > 2) {
            //  Determine bait interactor
            Interactor bait = determineBait(interactors, baitMap);
            String baitName = bait.getName();

            //  Create Edges between Bait and all other interactors.
            for (int i = 0; i < interactors.size(); i++) {
                Interactor interactor = (Interactor) interactors.get(i);
                RoleType role = (RoleType) baitMap.get(interactor.getName());
                if (role == null || role != RoleType.BAIT) {
                    createEdge(bait, interactor, interaction, nodeMap,
                            edgeMap);
                }
            }
        } else {
            Interactor interactor0 = (Interactor) interactors.get(0);
            Interactor interactor1 = (Interactor) interactors.get(1);
            createEdge(interactor0, interactor1, interaction, nodeMap,
                    edgeMap);
        }

    }

    private Interactor determineBait(ArrayList interactors, HashMap baitMap) {
        Interactor bait = null;
        for (int i = 0; i < interactors.size(); i++) {
            Interactor interactor = (Interactor) interactors.get(i);
            String name = interactor.getName();
            RoleType roleType = (RoleType) baitMap.get(name);
            if (roleType != null && roleType == RoleType.BAIT) {
                bait = interactor;
            }
        }
        return bait;
    }

    /**
     * Creates Edge Between Node1 and Node2.
     */
    private void createEdge(Interactor interactor1, Interactor interactor2,
            Interaction interaction, HashMap nodeMap, HashMap edgeMap) {

        //  Get Matching Nodes
        CyNode node1 = (CyNode) nodeMap.get(interactor1.getName());
        CyNode node2 = (CyNode) nodeMap.get(interactor2.getName());

        //  Create Edge between node1 and node2.
        CyEdge edge = Cytoscape.getCyEdge(node1, node2,
                Semantics.INTERACTION, "pp", true);
        cyNetwork.addEdge(edge);

        //  Create node1 --> node2 edge key
        String key = this.createEdgeKey(node1, node2,
                interaction);

        //  Now get the new edge and set the edge identifier.
        edge.setIdentifier(key);

        //  Set the Interaction Attribute
        String interactionAttributeValue = node1.getIdentifier() + " (pp) "
                + node2.getIdentifier();
        Cytoscape.getEdgeAttributes().setAttribute(key, Semantics.INTERACTION,
                interactionAttributeValue);

        //  Map Edge Attributes, e.g. any PUBMED Ids or
        //  experimental evidence.
        mapEdgeAttributes(interaction, edge);

        //  Add EdgeIndex to DataService Object
        //  Useful for quick look-ups
        int edgeRootGraphIndex = edge.getRootGraphIndex();
        ArrayList indexes = (ArrayList) interaction.getAttribute
                (ROOT_GRAPH_INDEXES);
        if (indexes == null) {
            indexes = new ArrayList();
            interaction.addAttribute(ROOT_GRAPH_INDEXES, indexes);
        }
        indexes.add(new Integer(edgeRootGraphIndex));

        //  Add to CyMap
        cyMap.put(key, interaction);

        //  Add to Edge Map
        edgeMap.put(key, edge);
    }

    /**
     * Determines if an edge already exists between the two nodes.
     */
    private boolean edgeExists(CyNode node1, CyNode node2, Interaction
            interaction, HashMap edgeMap) {

        //  Create node1 --> node2 edge key
        String key1 = this.createEdgeKey(node1, node2, interaction);

        //  Create node2 --> node2 edge key
        String key2 = this.createEdgeKey(node2, node1, interaction);

        //  Check to see if either key already exists
        boolean exists1 = edgeMap.containsKey(key1);
        boolean exists2 = edgeMap.containsKey(key2);
        boolean exists3 = exists1 | exists2;

        return exists3;
    }

    /**
     * Conditionally adds new node to graph.
     *
     * @param interactor Interactor object.
     * @param map        HashMap of current nodes.
     */
    private void addNode(Interactor interactor, HashMap map) {
        String name = interactor.getName();
        boolean inGraph = map.containsKey(name);
        if (!inGraph) {
            //  Create New Node via getCyNode Method.
            CyNode node = Cytoscape.getCyNode(name, true);

            //  Add New Node to Network
            cyNetwork.addNode(node);

            //  Set Node Identifier, Canonical Name, and Common Name.
            node.setIdentifier(name);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),
                    Semantics.CANONICAL_NAME, name);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),
                    Semantics.COMMON_NAME, name);

            //  Add to CyMap
            cyMap.put(name, interactor);

            //  Map Node Attributes, e.g. species, xrefs, and/or sequence.
            mapNodeAttributes(interactor, node);

            //  Add Node to Node Map.
            map.put(name, node);
        }
    }

    /**
     * Maps Node Attributes to Cytoscape GraphObj Attributes.
     * Can be subclassed.
     *
     * @param interactor Interactor object.
     * @param cyNode     CyNode.
     */
    protected void mapNodeAttributes(Interactor interactor, CyNode cyNode) {

        //  Map All Interactor Attributes
        HashMap attributeMap = interactor.getAllAttributes();
        Iterator iterator = attributeMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = (String) attributeMap.get(key);
            if (key != null && value != null) {
                Cytoscape.getNodeAttributes().setAttribute
                        (cyNode.getIdentifier(), key, value);
            }
        }

        //  Map All External References
        ExternalReference refs[] = interactor.getExternalRefs();
        if (refs != null) {
            ArrayList dbList = new ArrayList();
            ArrayList idList = new ArrayList();
            for (int i = 0; i < refs.length; i++) {
                ExternalReference ref = refs[i];
                dbList.add(ref.getDatabase());
                idList.add(ref.getId());
            }
            Cytoscape.getNodeAttributes().setAttributeList(cyNode.getIdentifier(),
                    CommonVocab.XREF_DB_NAME, dbList);
            Cytoscape.getNodeAttributes().setAttributeList(cyNode.getIdentifier(),
                    CommonVocab.XREF_DB_ID, idList);
        }
    }

    /**
     * Maps Edge Attributes to Cytoscape  GraphObj Attributes.
     * Can be subclassed.
     *
     * @param interaction Interaction object.
     * @param cyEdge      CyEdge object.
     */
    protected void mapEdgeAttributes(Interaction interaction, CyEdge cyEdge) {
        HashMap attributeMap = interaction.getAllAttributes();
        Iterator iterator = attributeMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object attrObject = attributeMap.get(key);
            if (attrObject instanceof String) {
                String str = (String) attrObject;
                Cytoscape.getEdgeAttributes().setAttribute
                        (cyEdge.getIdentifier(), key, str);
            }
        }

        //  Map All External References
        ExternalReference refs[] = interaction.getExternalRefs();
        if (refs != null) {
            ArrayList dbList = new ArrayList();
            ArrayList idList = new ArrayList();
            for (int i = 0; i < refs.length; i++) {
                ExternalReference ref = refs[i];
                dbList.add(ref.getDatabase());
                idList.add(ref.getId());
            }
            Cytoscape.getEdgeAttributes().setAttributeList
                    (cyEdge.getIdentifier(), CommonVocab.XREF_DB_NAME, dbList);
            Cytoscape.getEdgeAttributes().setAttributeList
                    (cyEdge.getIdentifier(), CommonVocab.XREF_DB_ID, idList);
        }
    }

    /**
     * Create Canonical name for Interaction type.
     * Can be subclassed.
     *
     * @param interaction Interaction to be named.
     * @return canonical name of interaction type.
     */
    protected String getInteractionTypeId(Interaction interaction) {
        String interactionType = (String) interaction.getAttribute
                (InteractionVocab.INTERACTION_TYPE);
        if (interactionType == null) {
            return " <--> ";
        }
        return new String(OPEN_PAREN + interactionType + CLOSE_PAREN);
    }

    /**
     * Create Hashkey for Edges.
     *
     * @param node1 First node.
     * @param node2 Second node.
     * @return HashKey.
     */
    private String createEdgeKey(Node node1, Node node2, Interaction
            interaction) {
        return new String(node1.getIdentifier()
                + getInteractionTypeId(interaction) + node2.getIdentifier());
    }

    /**
     * Creates a map of all existing nodes.
     * Enables code to detect duplicate nodes.
     *
     * @return HashMap of existing nodes, indexed by Identifer.
     */
    private HashMap createNodeMap() {
        HashMap nodeMap = new HashMap();
        Iterator nodeIterator = cyNetwork.nodesIterator();
        while (nodeIterator.hasNext()) {
            CyNode node = (CyNode) nodeIterator.next();
            String id = node.getIdentifier();
            nodeMap.put(id, node);
        }
        return nodeMap;
    }

    /**
     * Creates a map of all existing edges.
     * Enables code to detect duplicate edges.
     *
     * @return HashMap of existing edges, indexed by Identifier.
     */
    private HashMap createEdgeMap() {
        HashMap edgeMap = new HashMap();
        Iterator edgeIterator = cyNetwork.edgesIterator();
        while (edgeIterator.hasNext()) {
            CyEdge edge = (CyEdge) edgeIterator.next();
            String id = edge.getIdentifier();
            edgeMap.put(id, edge);
        }
        return edgeMap;
    }
}
