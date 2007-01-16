package csplugins.widgets.autocomplete.index;

import java.util.*;

/**
 * Basic implementation of the Number Index Interface.
 *
 * @author Ethan Cerami.
 */
class NumberIndexImpl extends GenericIndexImpl implements NumberIndex {
    private TreeMap treeMap;

    public NumberIndexImpl(int indexType) {
        super (indexType);
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

        //  Calculate successor of upper via IEEE 754 method.
        //  Used to create a closed range between lower (inclusive) and
        //  upper (inclusive).
        //  For background, see:
        //  http://www.cygnus-software.com/papers/comparingfloats/
        //  comparingfloats.htm
        if (upper instanceof Double) {
            long bits = Double.doubleToLongBits (upper.doubleValue());
            upper = new Double (Double.longBitsToDouble(bits + 1));
        } else if (upper instanceof Integer) {
            upper = new Integer(upper.intValue() + 1);
        }
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