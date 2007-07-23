/* -*-Java-*-
********************************************************************************
*
* File:         MapUtils.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/utils/MapUtils.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Sat Sep 24 07:58:15 2005
* Modified:     Thu Nov 02 17:13:20 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
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
public class MapUtils {
    /**
     * @return true iff all elements of values were added to the List
     * value for key in map.
     */

    //    static public boolean addListValuesToMap(
    //        Map<?, List<?>> map, Object key,
    //        Iterator<?> values_it, boolean check_for_duplicates) {
    //        if (key == null) {
    //            HEUtils.throwIllegalArgumentException(
    //                "addListValuesToMap was given a null key!");
    //        }
    //
    //        boolean ret_value = true;
    //        List<?> values = MapUtils.ensureListValueForMap(map, key);
    //        Object value;
    //
    //        while (values_it.hasNext()) {
    //            value = values_it.next();
    //
    //            if (check_for_duplicates && values.contains(value)) {
    //                ret_value = false;
    //            } else {
    //                values.add(value);
    //            }
    //        }
    //
    //        return ret_value;
    //    }
    static public <KeyType, ValueType> boolean addListValuesToMap(Map<KeyType, List<ValueType>> map,
                                                                  KeyType                       key,
                                                                  Iterator<ValueType>           values_it,
                                                                  boolean                       check_for_duplicates) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addListValuesToMap was given a null key!");
        }

        boolean         ret_value = true;
        List<ValueType> values = MapUtils.ensureListValueForMap(map, key);
        ValueType       value;

        while (values_it.hasNext()) {
            value = values_it.next();

            if (check_for_duplicates && values.contains(value)) {
                ret_value = false;
            } else {
                values.add(value);
            }
        }

        return ret_value;
    }

    /**
     * @return true iff value was added to the List value for key in map.
     */
    static public <KeyType, ValueType> boolean addListValueToMap(Map<KeyType, List<ValueType>> map,
                                                                 KeyType                       key,
                                                                 ValueType                     value,
                                                                 boolean                       check_for_duplicates) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addListValueToMap was given a null key!");
        }

        List<ValueType> values = MapUtils.ensureListValueForMap(map, key);

        if (check_for_duplicates && values.contains(value)) {
            return false;
        }

        values.add(value);

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
     * @return true iff value was added to the List value for key in map.
     */
    static public <KeyType, ValueType> boolean addSetValueToMap(Map<KeyType, Set<ValueType>> map,
                                                                KeyType                      key,
                                                                ValueType                    value) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addSetValueToMap was given a null key!");
        }

        Set<ValueType> values = MapUtils.ensureSetValueForMap(map, key);

        return values.add(value);
    }

    /**
     * @return true iff all elements of values were added to the List
     * value for key in map.
     */
    static public <KeyType, ValueType> boolean addSetValuesToMap(Map<KeyType, Set<ValueType>> map,
                                                                 KeyType                      key,
                                                                 Iterator<ValueType>          values_it) {
        if (key == null) {
            HEUtils.throwIllegalArgumentException(
                "addSetValuesToMap was given a null key!");
        }

        boolean        ret_value = true;
        Set<ValueType> values = MapUtils.ensureSetValueForMap(map, key);
        ValueType      value;

        while (values_it.hasNext()) {
            value = values_it.next();

            if (!values.add(value)) {
                ret_value = false;
            }
        }

        return ret_value;
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
    //                                                              Iterator<RefE>                         values_it) {
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
    //        while (values_it.hasNext()) {
    //            value = values_it.next();
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

    /**
     * @return true iff all iterator values were removed from the List
     * value for key in map.
     */
    static public boolean removeCollectionValuesFromMap(Map map, Object key,
                                                        Iterator values_it) {
        Collection values = (Collection) map.get(key);

        if (values == null) {
            return false;
        }

        if (values_it == null) {
            return false;
        }

        boolean ret_value = true;
        Object  value;

        while (values_it.hasNext()) {
            value = values_it.next();

            if (!values.remove(value)) {
                ret_value = false;
            }
        }

        if (values.isEmpty()) {
            map.remove(key);
        }

        return ret_value;
    }

    /**
     * @return true iff value was removed.
     */
    static public boolean removeCollectionValueFromMap(Map map, Object key,
                                                       Object value) {
        Collection value_collection = (Collection) map.get(key);

        if (value_collection == null) {
            return false;
        }

        boolean ret_val = value_collection.remove(value);

        if (value_collection.isEmpty()) {
            map.remove(key);
        }

        return ret_val;
    }

    //    static public <KeyType, RefE> boolean removeValuesFromRefCountMap(Map<KeyType, RefCountMultiValue<RefE>> map,
    //                                                                      KeyType                                key,
    //                                                                      Iterator<RefE>                         values_it) {
    //        RefCountMultiValue<RefE> ref_count_obj = map.get(key);
    //
    //        if (ref_count_obj == null) {
    //            return false;
    //        }
    //
    //        if (values_it == null) {
    //            return false;
    //        }
    //
    //        boolean ret_value = true;
    //        RefE    value;
    //
    //        while (values_it.hasNext()) {
    //            value = values_it.next();
    //
    //            if (!ref_count_obj.remove(value)) {
    //                ret_value = false;
    //            }
    //        }
    //
    //        if (ref_count_obj.isEmpty()) {
    //            map.remove(key);
    //        }
    //
    //        return ret_value;
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
    //        boolean ret_val = ref_count_obj.remove(value);
    //
    //        if (ref_count_obj.isEmpty()) {
    //            map.remove(key);
    //        }
    //	
    //        return ret_val;
    //    }

    static public <KeyType, Element> Set<Element> ensureSetValueForMap(Map<KeyType, Set<Element>> map,
								       KeyType                    key) {
        Set<Element> value = map.get(key);

        if (value == null) {
            value = new HashSet<Element>();
            map.put(key, value);
        }

        return value;
    }

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

    static public <KeyType, Element> List<Element> ensureListValueForMap(Map<KeyType, List<Element>> map,
                                                                         KeyType                     key) {
        List<Element> value = map.get(key);

        if (value == null) {
            value = new ArrayList<Element>();
            map.put(key, value);
        }

        return value;
    }
}
