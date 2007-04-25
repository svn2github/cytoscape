package csplugins.widgets.autocomplete.index;

import csplugins.quickfind.util.QuickFind;

import java.util.ArrayList;

/**
 * Abstract Base class for all Index implementations.
 *
 * @author Ethan Cerami
 */
public abstract class GenericIndexImpl implements GenericIndex {
    private int indexType;
    private String attributeName = QuickFind.UNIQUE_IDENTIFIER;
    private ArrayList observerList;

    /**
     * Default constructor.
     *
     * @param indexType QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
     */
    public GenericIndexImpl(int indexType) {
        this.indexType = indexType;
        observerList = new ArrayList();
    }

    /**
     * Gets Index Type:  QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
     *
     * @return QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
     */
    public int getIndexType() {
        return indexType;
    }

    /**
     * Resets the index, e.g. wipes everything clean.
     */
    public void resetIndex() {
        //  Explicitly notify all observers
        for (int i = 0; i < observerList.size(); i++) {
            IndexListener observer =
                    (IndexListener) observerList.get(i);
            observer.indexReset();
        }
    }

    /**
     * Adds new item to index.
     * @param key Key value.
     * @param o Object value.
     */
    public void addToIndex(Object key, Object o) {
        //  Explicitly notify all observers
        for (int i = 0; i < observerList.size(); i++) {
            IndexListener observer =
                    (IndexListener) observerList.get(i);
            observer.itemAddedToIndex(key, o);
        }
    }

    /**
     * Sets the controlling attribute.
     * 
     * @param attributeName Attribute name.
     */
    public void setControllingAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * Gets the controlling attribute.
     *
     * @return attribute name.
     */
    public String getControllingAttribute() {
        return this.attributeName;
    }

    /**
     * Adds a new IndexListener Object.
     * <P>Each IndexListener object will be notified each time the text
     * index is modified.
     *
     * @param listener IndexListener Object.
     */
    public void addIndexListener(IndexListener listener) {
        observerList.add(listener);
    }

    /**
     * Deletes the specified IndexListener Object.
     * <P>After being deleted, this listener will no longer receive any
     * notification events.
     *
     * @param listener IndexListener Object.
     */
    public void deleteIndexListener(IndexListener listener) {
        observerList.remove(listener);
    }

    /**
     * Gets number of registered listeners who are receving notification
     * events.
     *
     * @return number of registered listeners.
     */
    public int getNumListeners() {
        return observerList.size();
    }
}