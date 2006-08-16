package csplugins.widgets.autocomplete.index;

/**
 * Interface for listening to TextIndex events.
 *
 * @author Ethan Cerami.
 */
public interface TextIndexListener {

    /**
     * Index has been reset.
     */
    void indexReset();

    /**
     * Item has been added to the index.
     *
     * @param key String key.
     * @param o   Object o.
     */
    void itemAddedToIndex(String key, Object o);
}
