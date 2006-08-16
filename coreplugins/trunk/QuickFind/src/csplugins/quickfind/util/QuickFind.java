package csplugins.quickfind.util;

import csplugins.widgets.autocomplete.index.TextIndex;
import cytoscape.CyNetwork;
import cytoscape.task.TaskMonitor;

/**
 * Cytoscape Quick Find.
 * <p/>
 * The Cytoscape Quick Find class provides a convenient utility class for
 * quickly searching nodes or edges by any attribute.
 * <p/>
 * The following example illustrates the utility of this class.  To begin,
 * consider that network 1 is defined by the following SIF File:
 * <br/>
 * <pre>
 * YKR026C pp YGL122C
 * YGR218W pp YGL097W
 * YGL097W pp YOR204W
 * YLR249W pp YPR080W
 * YLR249W pp YBR118W
 * YLR293C pp YGL097W
 * </pre>
 * After this network is loaded, it can be automatically added to the quick
 * find index via the
 * {@link QuickFind#addNetwork(cytoscape.CyNetwork, cytoscape.task.TaskMonitor)}
 * method.  By default, this method iterates through each node in the network,
 * and indexes each node by its unique identifier, e.g. node.getIdentifier().
 * <p/>
 * A few minutes later, the end-user enters the String:  "YLR" in the Cytoscape
 * quick search box, and we want to quickly find all matching nodes that begin
 * with this prefix.
 * <p/>
 * To do so, we must first obtain the text index associated with this network
 * via the
 * {@link QuickFind#getTextIndex(cytoscape.CyNetwork)} method.  For example:
 * <BR/>
 * <PRE>
 * CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
 * TextIndex textIndex = QuickFind.getTextIndex (currentNetwork);
 * </PRE>
 * We can then retrieve all hits that begin with the prefix:  "YLR" via the
 * {@link TextIndex#getHits(String, int)} method.
 * <BR/>
 * <PRE>
 * Hit hits[] = textIndex.getHits ("YLR");
 * </PRE>
 * Technical Details:
 * <UL>
 * <LI>By default, this class will automatically index node objects based on the
 * their unique node identifier, e.g. node.getIdentifier().</LI>
 * <LI>You can index by a different attribute or choose to index edge
 * objects instead by calling the
 * {@link QuickFind#reindexAllNetworks(IndexType, String,
 * cytoscape.task.TaskMonitor)}.
 * <LI>You can specify any attribute key you like.  However, only those
 * attributes of type:  {@link cytoscape.data.CyAttributes#TYPE_STRING},
 * {@link cytoscape.data.CyAttributes#TYPE_INTEGER},
 * {@link cytoscape.data.CyAttributes#TYPE_FLOATING}, and type
 * {@link cytoscape.data.CyAttributes#TYPE_BOOLEAN} will be indexed.
 * All other types, e.g. {@link cytoscape.data.CyAttributes#TYPE_SIMPLE_LIST}
 * and {@link cytoscape.data.CyAttributes#TYPE_SIMPLE_MAP} will trigger an
 * IllegalArgumentException.
 * <LI>All registered networks must be indexed by the same attribute and
 * must be of type node or edge.  For example, it is not possible to index one
 * network by the "NAME"  attribute, and index another network by the "TYPE"
 * attribute.  This is automatically enforced by
 * {@link QuickFind#reindexAllNetworks(IndexType, String,
 * cytoscape.task.TaskMonitor)}.</LI>
 * <LI>QuickFind uses a {@link csplugins.widgets.autocomplete.index.Trie}
 * data structure for very fast look-ups.</LI>
 * <LI>QuickFind does *not* support regular expression queries.
 * </UL>
 * <p/>
 *
 * @author Ethan Cerami.
 */
public interface QuickFind {
    /**
     * Node / Edge Unique Identifier.
     */
    String UNIQUE_IDENTIFIER = "Unique Identifier";

    /**
     * Adds a new network to the global index, and indexes all nodes or edges
     * described by this network.
     * <P>By default, this class will automatically index node objects based
     * on their unique identifier, e.g. node.getIdentifier()</LI>
     *
     * @param network     Cytoscape Network.
     * @param taskMonitor TaskMonitor Object.
     */
    void addNetwork(CyNetwork network, TaskMonitor taskMonitor);

    /**
     * Removes the specified network from the global index.
     * <p/>
     * To free up memory, this method should be called whenever a network
     * is destroyed.
     *
     * @param network CyNetwork Object.
     */
    void removeNetwork(CyNetwork network);

    /**
     * Gets the text index associated with the specified network.
     *
     * @param network Cytoscape Network.
     * @return TextIndex Object.
     */
    TextIndex getTextIndex(CyNetwork network);

    /**
     * Gets current attribute key, used to create the global index.
     * The default is set to {@link QuickFind.UNIQUE_IDENTIFIER}.
     *
     * @return Current Attribute Hit.
     */
    String getCurrentAttributeKey();

    /**
     * Indicates whether we are currently indexing nodes or edges.
     * The default is set to {@link IndexType#NODE_INDEX}.
     *
     * @return IndexType Object:
     *         {@link IndexType#NODE_INDEX} or
     *         {@link IndexType#EDGE_INDEX}.
     */
    IndexType getCurrentIndexType();

    /**
     * Reindexes all nodes or edges within all registered networks.
     * <p/>
     * This method will iterate through all nodes or edges within all
     * registered networks, and add each node or edge to the global index.
     * For each node or edge, the attribute specified by attributeKey will be
     * used to create the text index.
     * <p/>For example, if you want to quickly search all nodes by their
     * "BIOPAX_NAME" attribute, you would use this code:
     * <br/>
     * <pre>reindexAllNetworks (IndexType.NODE_INDEX, "BIOPAX_NAME", tm);</pre>
     *
     * @param type         IndexType:
     *                     {@link IndexType#NODE_INDEX} or
     *                     {@link IndexType#EDGE_INDEX}.  One can index
     *                     all nodes or all edges, but not both.
     * @param attributeKey Attribute key used to index all node / edges.
     * @param taskMonitor  Task Monitor, used to monitor long-term progress
     *                     of task.
     */
    void reindexAllNetworks(IndexType type, String attributeKey,
            TaskMonitor taskMonitor);

    /**
     * Adds a new QuickFind Listener.
     *
     * @param listener QuickFindListener Object.
     */
    void addQuickFindListener(QuickFindListener listener);

    /**
     * Removes the specified QuickFind Listener Object.
     *
     * @param listener QuickFindListener Object.
     */
    void removeQuickFindListener(QuickFindListener listener);

    /**
     * Gets an array of all registered QuickFind Listener Objects.
     *
     * @return Array of QuickFindListener Objects.
     */
    QuickFindListener[] getQuickFindListeners();
}