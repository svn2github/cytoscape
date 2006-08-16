package csplugins.widgets.autocomplete.index;

/**
 * Text index interface.
 * <p/>
 * This is a core data structure for indexing arbitrary Objects, based on a
 * key string.
 *
 * @author Ethan Cerami.
 */
public interface TextIndex {

    /**
     * Resets the index, wipes everything clean.
     */
    void resetIndex();

    /**
     * Adds new object to index.
     *
     * @param key String Key.
     * @param o   Any Java Object.
     */
    void addToIndex(String key, Object o);

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
     * Adds a new TextIndexListener object.
     * <P>The TextIndexListener object will be notified each time the text
     * index is modified.
     *
     * @param listener TextIndexListener Object.
     */
    void addTextIndexListener(TextIndexListener listener);

    /**
     * Deletes the specified TextIndexListener Object.
     * <P>After being deleted, this listener will no longer receive any
     * notification events.
     *
     * @param listener TextIndexListener Object.
     */
    void deleteTextIndexListener(TextIndexListener listener);

    /**
     * Gets number of registered listeners who are receving notification events.
     *
     * @return number of registered listeners.
     */
    int getNumListeners();

    /**
     * Gets a text description of the text index, primarily used for debugging
     * purposes.
     *
     * @return text description of the text index.
     */
    String toString();
}