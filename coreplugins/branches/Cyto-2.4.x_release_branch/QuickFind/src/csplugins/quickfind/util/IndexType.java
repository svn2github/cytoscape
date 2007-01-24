package csplugins.quickfind.util;

/**
 * IndexType Type.
 * <p/>
 * Used to indicate whether we are currently indexing nodes or edges.
 *
 * @author Ethan Cerami.
 */
public class IndexType {
    private String name;

    /**
     * Private Constructor. Enumeration Pattern.
     *
     * @param name Type Name.
     */
    private IndexType(String name) {
        this.name = name;
    }

    /**
     * Gets Type Name.
     *
     * @return Type Name.
     */
    public String toString() {
        return name;
    }

    /**
     * IndexType Type:  NODE_INDEX.
     */
    public static final IndexType NODE_INDEX
            = new IndexType("NODE_INDEX");

    /**
     * IndexType Type:  EDGE_INDEX.
     */
    public static final IndexType EDGE_INDEX
            = new IndexType("EDGE_INDEX");
}