package csplugins.widgets.autocomplete.index;

/**
 * Factory for creating TextIndex objects.
 *
 * @author Ethan Cerami.
 */
public class TextIndexFactory {

    /**
     * Gets the default implementation of the TextIndex interface.
     *
     * @return TextIndex Object.
     */
    public static TextIndex createDefaultTextIndex() {
        return new TextIndexImpl();
    }
}
