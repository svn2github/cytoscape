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
     * @return TextIndex Object.
     */
    public static TextIndex createDefaultTextIndex() {
        return new TextIndexImpl();
    }
}
