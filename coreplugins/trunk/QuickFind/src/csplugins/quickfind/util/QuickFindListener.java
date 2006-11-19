package csplugins.quickfind.util;

import cytoscape.CyNetwork;
import csplugins.widgets.autocomplete.index.Hit;

/**
 * Quick Find Listener Interface.
 *
 * @author Ethan Cerami.
 */
public interface QuickFindListener {

    /**
     * Network has been added to the Quick Find Index.
     *
     * @param network CyNetwork Object.
     */
    void networkAddedToIndex(CyNetwork network);

    /**
     * Network has been removed from the Quick Find Index.
     *
     * @param network CyNetwork Object.
     */
    void networkRemovedfromIndex(CyNetwork network);

    /**
     * Indexing started.
     *
     * @param cyNetwork     CyNetwork.
     * @param indexType     QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
     * @param controllingAttribute Controlling Attribute.
     */
    void indexingStarted(CyNetwork cyNetwork,
        int indexType, String controllingAttribute);

    /**
     * Indexing operation ended.
     */
    void indexingEnded();

    /**
     * Indicates that the user has selected a hit within the QuickFind
     * search box.
     *
     * @param network       the current CyNetwork.
     * @param hit           hit value chosen by the user.
     */
    void onUserSelection (CyNetwork network, Hit hit);

    /**
     * Indicates that the user has selected a range within the QuickFind
     * range selector.
     *
     * @param network       the current CyNetwork.
     * @param low           the low value of the range.
     * @param high          the high value of the range.
     */
    void onUserRangeSelection (CyNetwork network, Number low, Number high);
}
