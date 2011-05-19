
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

/* 
*
* Revisions:
*
* Tue Aug 22 15:23:29 2006 (Michael L. Creech) creech@w235krbza760
*  Changed all major operations to be Java 1.5 generics.
********************************************************************************
*/
package cytoscape.hyperedge.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Convenience operations for adding and removing from Maps.
 * @author Michael L. Creech
 * @version 1.0
 */
public final class MapUtils {
    // Don't want people to manipulate the utility class constructor.
    private MapUtils() {}

    /**
     * Add a set of values contained in an Iterator to the List associated with a given map's key.
     * If the map doesn't contain a value for  the key, a new List value will be created and associated with the key.
     * @param map the Map that is to contain the Iterator values in a set associated with key.
     * @param key the KeyType key from which to retrieve the List. 
     * @param toAddIt an Iterator over a set of ValueType values to add to the List.
     * @param checkForDuplicates if true, duplicates are not added to the list (items are unique).
     * @param <KeyType> the type of the key.
     * @param <ValueType>  the type of the elements in the List.
     * @return true iff all elements of toAddIt were added to the List
     * value for key in map.
     */
    public static <KeyType, ValueType> boolean addListValuesToMap(final Map<KeyType, List<ValueType>> map,
                                                                  final KeyType                       key,
                                                                  final Iterator<ValueType>           toAddIt,
                                                                  final boolean                       checkForDuplicates) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addListValuesToMap was given a null key!");
        }

        boolean         retValue = true;
        final List<ValueType> values = MapUtils.ensureListValueForMap(map, key);
        ValueType       value;

        while (toAddIt.hasNext()) {
            value = toAddIt.next();

            if (checkForDuplicates && values.contains(value)) {
                retValue = false;
            } else {
                values.add(value);
            }
        }

        return retValue;
    }

    /**
     * Add a value to the List associated with a given map's key.
     * If the map doesn't contain a value for the key, a new List value will be created and associated with the key.
     * @param map the Map that is to contain the new value  in a set associated with key.
     * @param key the KeyType key from which to retrieve the List. 
     * @param toAdd  a <ValueType> to add to the List.
     * @param checkForDuplicates if true, duplicates are not added to the list (items are unique).
     * @param <KeyType> the type of the key.
     * @param <ValueType>  the type of the elements in the List.
     * @return true iff toAdd was added to the List value for key in map.
     */
    public static <KeyType, ValueType> boolean addListValueToMap(final Map<KeyType, List<ValueType>> map,
                                                                 final KeyType                       key,
                                                                 final ValueType                     toAdd,
                                                                 final boolean                       checkForDuplicates) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addListValueToMap was given a null key!");
        }

        final List<ValueType> values = MapUtils.ensureListValueForMap(map, key);

        if (checkForDuplicates && values.contains(toAdd)) {
            return false;
        }

        values.add(toAdd);

        return true;
    }

    //    static public boolean addListValueToMap(Map map, Object key, Object value,
    //        boolean check_for_duplicates) {
    //        if (key == null) {
    //            HEUtils.throwIllegalArgumentException(
    //                "addListValueToMap was given a null key!");
    //        }
    //
    //        List values = MapUtils.ensureListValueForMap(map, key);
    //
    //        if (check_for_duplicates && values.contains(value)) {
    //            return false;
    //        }
    //
    //        values.add(value);
    //
    //        return true;
    //    }

    /**
     * Add a value to the Set associated with a given map's key.
     * If the map doesn't contain a value for the key, a new Set value will be created and associated with the key.
     * @param map the Map that is to contain the new value  in a set associated with key.
     * @param key the KeyType key from which to retrieve the Set. 
     * @param toAdd  a <ValueType> to add to the Set.
     * @param <KeyType> the type of the key.
     * @param <ValueType>  the type of the elements in the Set.
     * @return true iff toAdd was added to the Set value for key in map.
     */
    public static <KeyType, ValueType> boolean addSetValueToMap(final Map<KeyType, Set<ValueType>> map,
                                                                final KeyType                      key,
                                                                final ValueType                    toAdd) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addSetValueToMap was given a null key!");
        }

        final Set<ValueType> values = MapUtils.ensureSetValueForMap(map, key);

        return values.add(toAdd);
    }

    /**
     * Add a set of values contained in an Iterator to the Set associated with a given map's key.
     * If the map doesn't contain a value for  the key, a new Set value will be created and associated with the key.
     * @param map the Map that is to contain the Iterator values in a set associated with key.
     * @param key the KeyType key from which to retrieve the Set. 
     * @param toAddIt an Iterator over a set of ValueType values to add to the Set.
     * @param <KeyType> the type of the key.
     * @param <ValueType>  the type of the elements in the Set.
     * @return true iff all elements of toAddIt were added to the Set
     * value for key in map.
     */
    public static <KeyType, ValueType> boolean addSetValuesToMap(final Map<KeyType, Set<ValueType>> map,
                                                                 final KeyType                      key,
                                                                 final Iterator<ValueType>          toAddIt) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addSetValuesToMap was given a null key!");
        }

        boolean        retValue = true;
        final Set<ValueType> values = MapUtils.ensureSetValueForMap(map, key);
        ValueType      value;

        while (toAddIt.hasNext()) {
            value = toAddIt.next();

            if (!values.add(value)) {
                retValue = false;
            }
        }

        return retValue;
    }

    /**
     * Remove a set of values contained in an Iterator from the collection associated with a given map's key.
     * If no values remain in the Collection, the collection is removed from the map.
     * @param map the Map containing the Collection value associated with key from which to remove toRemove.
     * @param key the Object key from which to retrieve the Collection. 
     * @param toRemoveIt an Iterator over a set of value to remove from the Collection.
     * @param <KeyType> the type of the key.
     * @param <Element> the type of the elements in the Collection.
     * @return true iff all iterator values were removed from the Collection
     * value for key in map.
     */

    public static <KeyType,Element> boolean removeCollectionValuesFromMap(final Map<KeyType,? extends Collection<Element>> map, final KeyType key,
                                                        final Iterator<Element> toRemoveIt) {
        final Collection<Element> values = map.get(key);

        if (values == null) {
            return false;
        }

        if (toRemoveIt == null) {
            return false;
        }

        boolean retValue = true;
        Object  value;

        while (toRemoveIt.hasNext()) {
            value = toRemoveIt.next();

            if (!values.remove(value)) {
                retValue = false;
            }
        }

        if (values.isEmpty()) {
            map.remove(key);
        }

        return retValue;
    }


    /**
     * Remove a given value from the collection associated with a given map's key.
     * If no values remain in the Collection, the collection is removed from the map.
     * @param map the Map containing the Collection value associated with key from which to remove toRemove.
     * @param key the Object key from which to retrieve the Collection. 
     * @param toRemove the object to remove from the Collection.
     * @param <KeyType> the type of the key.
     * @param <Element> the type of the elements in the Collection.
     * @return true iff a Collection was found and toRemove was removed.
     */

    public static <KeyType,Element> boolean removeCollectionValueFromMap(final Map<KeyType,? extends Collection<Element>> map, final KeyType key,
									 final Element toRemove) {
        final Collection<Element> valueCollection = map.get(key);

        if (valueCollection == null) {
            return false;
        }

        final boolean retVal = valueCollection.remove(toRemove);

        if (valueCollection.isEmpty()) {
            map.remove(key);
        }

        return retVal;
    }


    /**
     * Ensure a given map has a non-null Set value for a given key.
     * @param map the Map for which to ensure a given key has a Set value.
     * @param key the key whose associated value must be a Set.
     * @param <KeyType> the type of the key.
     * @param <Element> the type of the elements in the Set.
     * @return the non-null Set found or created.
     */
    public static <KeyType, Element> Set<Element> ensureSetValueForMap(final Map<KeyType, Set<Element>> map,
								       final KeyType                    key) {
        Set<Element> value = map.get(key);

        if (value == null) {
            value = new HashSet<Element>();
            map.put(key, value);
        }

        return value;
    }

    /**
     * Ensure a given map has a non-null List value for a given key.
     * @param map the Map for which to ensure a given key has a List value.
     * @param key the key whose associated value must be a List.
     * @param <KeyType> the type of the key.
     * @param <Element> the type of the elements in the List.
     * @return the non-null List found or created.
     */
    public static <KeyType, Element> List<Element> ensureListValueForMap(final Map<KeyType, List<Element>> map,
                                                                         final KeyType                     key) {
        List<Element> value = map.get(key);

        if (value == null) {
            value = new ArrayList<Element>();
            map.put(key, value);
        }

        return value;
    }


    //    /**
    //     * Add a given key and set of values (given in an iterator) to a given Map 
    //     * whose values are RefCountMultiValues.
    //     * Sample generic instantiation: replace all references to KeyType
    //     * to be 'GraphPerspective', and all references to RefE to be
    //     * 'Node'.
    //     */
    //
    //    static public <KeyType, RefE> void addValuesToRefCountMap(Map<KeyType, RefCountMultiValue<RefE>> map,
    //                                                              KeyType                                key,
    //                                                              Iterator<RefE>                         toRemoveIt) {
    //        if (key == null) {
    //            HEUtils.throwIllegalArgumentException(
    //                "addValueToRefCountMap was given a null key!");
    //        }
    //
    //        RefCountMultiValue<RefE> ref_count_obj = MapUtils.ensureRefCountValueForMap(
    //            map,
    //            key);
    //        RefE                     value;
    //
    //        while (toRemoveIt.hasNext()) {
    //            value = toRemoveIt.next();
    //            ref_count_obj.add(value);
    //        }
    //    }
    //
    //    /**
    //     * Add a given key and value to a given Map whose values are
    //     * RefCountMultiValues.  Sample generic instantiation: replace all
    //     * references to KeyType to be 'GraphPerspective', and all
    //     * references to RefE to be 'Node'.
    //     */
    //    static public <KeyType, RefE> void addValueToRefCountMap(Map<KeyType, RefCountMultiValue<RefE>> map,
    //                                                             KeyType                                key,
    //                                                             RefE                                   value) {
    //        if (key == null) {
    //            HEUtils.throwIllegalArgumentException(
    //                "addValueToRefCountMap was given a null key!");
    //        }
    //
    //        RefCountMultiValue<RefE> ref_count_obj = MapUtils.ensureRefCountValueForMap(
    //            map,
    //            key);
    //        ref_count_obj.add(value);
    //    }

    //    static public <KeyType, RefE> boolean removeValuesFromRefCountMap(Map<KeyType, RefCountMultiValue<RefE>> map,
    //                                                                      KeyType                                key,
    //                                                                      Iterator<RefE>                         toRemoveIt) {
    //        RefCountMultiValue<RefE> ref_count_obj = map.get(key);
    //
    //        if (ref_count_obj == null) {
    //            return false;
    //        }
    //
    //        if (toRemoveIt == null) {
    //            return false;
    //        }
    //
    //        boolean retValue = true;
    //        RefE    value;
    //
    //        while (toRemoveIt.hasNext()) {
    //            value = toRemoveIt.next();
    //
    //            if (!ref_count_obj.remove(value)) {
    //                retValue = false;
    //            }
    //        }
    //
    //        if (ref_count_obj.isEmpty()) {
    //            map.remove(key);
    //        }
    //
    //        return retValue;
    //    }
    //
    //    static public <KeyType, RefE> boolean removeValueFromRefCountMap(Map<KeyType, RefCountMultiValue<RefE>> map,
    //                                                                     KeyType                                key,
    //                                                                     RefE                                   value) {
    //        RefCountMultiValue<RefE> ref_count_obj = map.get(key);
    //
    //        if (ref_count_obj == null) {
    //            return false;
    //        }
    //
    //        boolean retVal = ref_count_obj.remove(value);
    //
    //        if (ref_count_obj.isEmpty()) {
    //            map.remove(key);
    //        }
    //	
    //        return retVal;
    //    }

    //    static public <KeyType, RefE> RefCountMultiValue<RefE> ensureRefCountValueForMap(Map<KeyType, RefCountMultiValue<RefE>> map,
    //                                                                                     KeyType                                key) {
    //        RefCountMultiValue<RefE> value = map.get(key);
    //
    //        if (value == null) {
    //            value = new RefCountMultiValue<RefE>();
    //            map.put(key, value);
    //        }
    //
    //        return value;
    //    }
    //    public static boolean removeCollectionValuesFromMap(final Map<Object,Collection<Object>> map, final Object key,
    //                                                        final Iterator<Object> toRemoveIt) {
    //        final Collection<Object> values = map.get(key);
    //
    //        if (values == null) {
    //            return false;
    //        }
    //
    //        if (toRemoveIt == null) {
    //            return false;
    //        }
    //
    //        boolean retValue = true;
    //        Object  value;
    //
    //        while (toRemoveIt.hasNext()) {
    //            value = toRemoveIt.next();
    //
    //            if (!values.remove(value)) {
    //                retValue = false;
    //            }
    //        }
    //
    //        if (values.isEmpty()) {
    //            map.remove(key);
    //        }
    //
    //        return retValue;
    //    }
    //    public static boolean removeCollectionValueFromMap(final Map<Object,Collection<Object>> map, final Object key,
    //                                                       final Object toRemove) {
    //        final Collection<Object> valueCollection = map.get(key);
    //
    //        if (valueCollection == null) {
    //            return false;
    //        }
    //
    //        final boolean retVal = valueCollection.remove(toRemove);
    //
    //        if (valueCollection.isEmpty()) {
    //            map.remove(key);
    //        }
    //
    //        return retVal;
    //    }

}
