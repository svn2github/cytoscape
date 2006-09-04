package csplugins.widgets.autocomplete.index;

import java.util.*;

/**
 * Basic implementation of the Number Index Interface.
 *
 * @author Ethan Cerami.
 */
class NumberIndexImpl extends GenericIndexImpl implements NumberIndex {
    private TreeMap treeMap;

    public NumberIndexImpl() {
        treeMap = new TreeMap();
    }

    public void resetIndex() {
        treeMap = new TreeMap();
        super.resetIndex();
    }

    public void addToIndex(Object key, Object o) {
        if (key instanceof Integer || key instanceof Double) {
            List list;
            if (treeMap.containsKey(key)) {
                list = (List) treeMap.get(key);
            } else {
                list = new ArrayList();
                treeMap.put(key, list);
            }
            list.add(o);
        } else {
            throw new IllegalArgumentException ("key parameter must be of "
                + "type Integer or Double.");
        }
        super.addToIndex(key, o);
    }

    public List getRange(Number lower, Number upper) {
        ArrayList list = new ArrayList();
        SortedMap map = treeMap.subMap(lower, upper);
        Iterator iterator = map.values().iterator();
        while (iterator.hasNext()) {
            List subList = (List) iterator.next();
            list.addAll(subList);
        }
        return list;
    }

    public Number getMinimumValue() {
        return (Number) treeMap.firstKey();
    }

    public Number getMaximumValue() {
        return (Number) treeMap.lastKey();
    }
}