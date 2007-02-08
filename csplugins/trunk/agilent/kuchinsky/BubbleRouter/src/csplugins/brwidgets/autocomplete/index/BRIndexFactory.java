package csplugins.brwidgets.autocomplete.index;

/**
 * Factory for creating Index objects.
 *
 * @author Ethan Cerami.
 */
public class BRIndexFactory {

    /**
     * Gets the default implementation of the TextIndex interface.
     *
     * @return TextIndex Object.
     */
    public static BRTextIndex createDefaultTextIndex() {
        return new BRTextIndexImpl();
    }

    /**
     * Gets the default implementation of the NumberIndex interface.
     * @return NumberIndex Object.
     */
    public static BRNumberIndex createDefaultNumberIndex() {
        return new BRNumberIndexImpl();
    }
}
