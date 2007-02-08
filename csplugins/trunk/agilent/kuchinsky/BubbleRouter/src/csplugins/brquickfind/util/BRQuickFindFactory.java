package csplugins.brquickfind.util;

import cytoscape.Cytoscape;


/**
 * Factory for Creating QuickFind Classes.
 *
 * @author Ethan Cerami.
 */
public class BRQuickFindFactory {
    private static BRQuickFind quickFind;

    /**
     * Gets instance of Global QuickFind Singleton.
     *
     * @return Global QuickFind Class.
     */
    public static BRQuickFind getGlobalQuickFindInstance() {
        if (quickFind == null) {
            quickFind = new BRQuickFindImpl(Cytoscape.getNodeAttributes());
        }
        return quickFind;
    }
}
