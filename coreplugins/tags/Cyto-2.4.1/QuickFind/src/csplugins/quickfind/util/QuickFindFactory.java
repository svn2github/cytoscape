package csplugins.quickfind.util;

import cytoscape.Cytoscape;

/**
 * Factory for Creating QuickFind Classes.
 *
 * @author Ethan Cerami.
 */
public class QuickFindFactory {
    private static QuickFind quickFind;

    /**
     * Gets instance of Global QuickFind Singleton.
     *
     * @return Global QuickFind Class.
     */
    public static QuickFind getGlobalQuickFindInstance() {
        if (quickFind == null) {
            quickFind = new QuickFindImpl(Cytoscape.getNodeAttributes(),
                    Cytoscape.getEdgeAttributes());
        }
        return quickFind;
    }
}
