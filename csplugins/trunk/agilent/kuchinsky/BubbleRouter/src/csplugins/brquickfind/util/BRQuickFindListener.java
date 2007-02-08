package csplugins.brquickfind.util;

import cytoscape.CyNetwork;


/**
 * Quick Find Listener Interface.
 *
 * @author Ethan Cerami.
 */
public interface BRQuickFindListener {

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
     * Indexing operation started.
     */
    void indexingStarted();

    /**
     * Indexing operation ended.
     */
    void indexingEnded();
}
