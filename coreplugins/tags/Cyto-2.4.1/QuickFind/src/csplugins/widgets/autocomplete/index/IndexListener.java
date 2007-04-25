package csplugins.widgets.autocomplete.index;

/**
 * Interface for listening to Index events.
 *
 * @author Ethan Cerami.
 */
public interface IndexListener {

    /**
     * Index has been reset.
     */
    void indexReset();

    /**
     * Item has been added to the index.
     *
     * @param key Object key.
     * @param o   Object o.
     */
    void itemAddedToIndex(Object key, Object o);
}
