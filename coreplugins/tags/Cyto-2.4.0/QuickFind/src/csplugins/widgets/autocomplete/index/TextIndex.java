package csplugins.widgets.autocomplete.index;

/**
 * Text index interface.
 * <p/>
 * This is a core data structure for indexing arbitrary Objects, based on a
 * key string.
 *
 * @author Ethan Cerami.
 */
public interface TextIndex extends GenericIndex {
    /**
     * Default Max Key Length
     */
    int DEFAULT_MAX_KEY_LENGTH = 100;

    /**
     * Set max key length;  all keys greater than this length will
     * be automatically truncated.
     *
     * <P>Default is set to {@link TextIndex#DEFAULT_MAX_KEY_LENGTH}
     * @param len max key length.
     */
    void setMaxKeyLength (int len);

    /**
     * Gets max key length;  all keys greater than this length will
     * be automatically truncated.
     * <P>Default is set to {@link TextIndex#DEFAULT_MAX_KEY_LENGTH}
     *
     * @return max key length.
     */
    int getMaxKeyLength();

    /**
     * Gets all hits which begin with the specified prefix.
     *
     * @param prefix String prefix.
     * @param maxHits Maximum number of hits.
     * @return Array of Hits.
     */
    Hit[] getHits(String prefix, int maxHits);

    /**
     * Gets total number of keys in index.
     *
     * @return number of keys in index.
     */
    int getNumKeys();

    /**
     * Gets a text description of the text index, primarily used for debugging
     * purposes.
     *
     * @return text description of the text index.
     */
    String toString();
}