package csplugins.widgets.autocomplete.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;

/**
 * Basic implementation of the Text Index Interface.
 *
 * @author Ethan Cerami.
 */
class TextIndexImpl implements TextIndex {
    private Trie trie;
    private HashMap map;
    private ArrayList observerList;
    private static final boolean OUTPUT_PERFORMANCE_STATS = false;
    private HashMap cache = new HashMap();

    /**
     * Constructor.
     */
    public TextIndexImpl() {
        observerList = new ArrayList();
        init();
    }

    /**
     * Resets the index, e.g. wipes everything clean.
     */
    public void resetIndex() {
        init();

        //  Explicitly notify all observers
        for (int i = 0; i < observerList.size(); i++) {
            TextIndexListener observer =
                    (TextIndexListener) observerList.get(i);
            observer.indexReset();
        }
    }

    /**
     * Adds new object to index.
     *
     * @param key String Hit.
     * @param o   Any Java Object.
     */
    public void addToIndex(String key, Object o) {
        // convert all keys to lowercase
        key = key.toLowerCase();

        //  Add to Try and HashMap
        trie.add(key);
        ArrayList objectList = (ArrayList) map.get(key);
        if (objectList == null) {
            objectList = new ArrayList();
            map.put(key, objectList);
        }
        objectList.add(o);

        //  Explicitly notify all observers
        for (int i = 0; i < observerList.size(); i++) {
            TextIndexListener observer =
                    (TextIndexListener) observerList.get(i);
            observer.itemAddedToIndex(key, o);
        }
    }

    /**
     * Gets all hits which begin with the specified prefix.
     *
     * @param prefix String prefix.
     * @param maxHits Maximum number of hits
     * @return Array of Strings, which begin with the specified prefix.
     */
    public Hit[] getHits(String prefix, int maxHits) {
        Date start = new Date();
        Hit hits[] = null;

        //  Obtain special case of "" from cache.
        if (prefix.equals("")) {
            hits = (Hit[]) cache.get(prefix);
        }

        if (hits == null) {
            String keys[] = trie.getWords(prefix.toLowerCase());
            int size = Math.min(keys.length, maxHits);
            hits = new Hit[size];

            //  Sort the keys
            Arrays.sort(keys);

            //  Create the Hits
            for (int i = 0; i < size; i++) {
                hits[i] = new Hit(keys[i], getObjectsByKey(keys[i]));
            }
        }

        if (prefix.equals("")) {
            cache.put(prefix, hits);
        }

        Date stop = new Date();
        if (OUTPUT_PERFORMANCE_STATS) {
            long interval = stop.getTime() - start.getTime();
            System.out.println("Time to look up:  " + interval + " ms");
        }
        return hits;
    }

    /**
     * Gets total number of keys in index.
     *
     * @return number of keys in index.
     */
    public int getNumKeys() {
        return map.size();
    }

    /**
     * Adds a new TextIndexListener Object.
     * <P>Each TextIndexListener object will be notified each time the text
     * index is modified.
     *
     * @param listener TextIndexListener Object.
     */
    public void addTextIndexListener(TextIndexListener listener) {
        observerList.add(listener);
    }

    /**
     * Deletes the specified TextIndexListener Object.
     * <P>After being deleted, this listener will no longer receive any
     * notification events.
     *
     * @param listener TextIndexListener Object.
     */
    public void deleteTextIndexListener(TextIndexListener listener) {
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

    /**
     * Gets a text description of the text index, primarily used for
     * debugging purposes.
     *
     * @return text description of the text index.
     */
    public String toString() {
        return "Text Index:  [Total number of keys:  " + map.size() + "]";
    }

    /**
     * Gets all objects associated with the specified key.
     * <p/>
     * Each key can be associated with multiple objects.  This method therefore
     * returns an array of Objects.
     *
     * @param key String Hit.
     * @return Array of Java Objects.
     */
    private Object[] getObjectsByKey(String key) {
        if (map.containsKey(key)) {
            ArrayList list = (ArrayList) map.get(key);
            return list.toArray();
        } else {
            throw new IllegalArgumentException
                    ("No objects exist for key:  " + key);
        }
    }

    /**
     * Initializes the Text Index.
     */
    private void init() {
        trie = new Trie();
        map = new HashMap();
        cache = new HashMap();
    }
}