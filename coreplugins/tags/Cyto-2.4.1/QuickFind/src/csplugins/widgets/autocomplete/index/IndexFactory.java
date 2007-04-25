package csplugins.widgets.autocomplete.index;

/**
 * Factory for creating Index objects.
 *
 * @author Ethan Cerami.
 */
public class IndexFactory {

    /**
     * Gets the default implementation of the TextIndex interface.
     *
     * @param indexType QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
     * @return TextIndex Object.
     */
    public static TextIndex createDefaultTextIndex(int indexType) {
        return new TextIndexImpl(indexType);
    }

    /**
     * Gets the default implementation of the NumberIndex interface.
     *
     * @param indexType QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
     * @return NumberIndex Object.
     */
    public static NumberIndex createDefaultNumberIndex(int indexType) {
        return new NumberIndexImpl(indexType);
    }
}
