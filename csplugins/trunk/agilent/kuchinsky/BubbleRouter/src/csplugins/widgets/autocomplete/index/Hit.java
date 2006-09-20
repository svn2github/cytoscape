package csplugins.widgets.autocomplete.index;

/**
 * Encapsulates a hit within the text index.
 * <p/>
 * Each hit has two pieces of data:
 * <UL>
 * <LI>a String keyword.
 * <LI>1 or more associated Objects.
 * </UL>
 * For example, we may index two Objects with the same name, "YRC00441".
 * If we subsequently search for "YRC00441", we get back one Hit object with
 * the following data: keyword = "YRC00441", objects = [Object1][Object2].
 *
 * @author Ethan Cerami.
 */
public class Hit {
    private String keyword;
    private Object objects[];

    /**
     * Constructor.
     *
     * @param keyword Keyword String.
     * @param objects Objects associated with this hit.
     */
    public Hit(String keyword, Object objects[]) {
        this.keyword = keyword;
        this.objects = objects;
    }

    /**
     * Gets keyword value of hit.
     *
     * @return String keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Gets objects associated with this hit.
     *
     * @return Objects associated with this hit.
     */
    public Object[] getAssociatedObjects() {
        return objects;
    }

    /**
     * toString() method.
     *
     * @return Same as getKeyword().
     */
    public String toString() {
        return getKeyword();
    }
}
