package csplugins.widgets.autocomplete.index;

import java.util.*;

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
    private static final String WILD_CARD = "*";

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
     * @param prefix  String prefix.
     * @param maxHits Maximum number of hits
     * @return Array of Strings, which begin with the specified prefix.
     */
    public Hit[] getHits(String prefix, int maxHits) {
        Date start = new Date();
        Hit hits[] = null;

        //  Deal with wild card cases.
        if (prefix.equals("")) {
            hits = (Hit[]) cache.get(prefix);
        } else if (prefix.endsWith(WILD_CARD)) {
            hits = getWildCardHits(prefix);
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
     * Executes basic wild card search.  Prefix must end with *.
     * For example:  "YDR*".
     *
     * @param prefix prefix ending in *.
     * @return An array containing 1 hit object or null.
     */
    private Hit[] getWildCardHits(String prefix) {
        Hit[] hits = null;

        //  Remove wildcard.
        String regex = prefix.toLowerCase().substring(0, prefix.length() - 1);

        //  Find all matching words
        String keys[] = trie.getWords(regex);

        //  Find all associated graph objects;  avoid redundant objects.
        Set graphObjectSet = new HashSet();
        for (int i = 0; i < keys.length; i++) {
            Object graphObjects[] = getObjectsByKey(keys[i]);
            for (int j = 0; j < graphObjects.length; j++) {
                graphObjectSet.add(graphObjects[j]);
            }
        }

        //  Return result set
        if (graphObjectSet.size() > 0) {
            hits = new Hit[1];
            Object graphObjects[] = graphObjectSet.toArray
                    (new Object[graphObjectSet.size()]);
            hits[0] = new Hit(prefix, graphObjects);
        }
        return hits;
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