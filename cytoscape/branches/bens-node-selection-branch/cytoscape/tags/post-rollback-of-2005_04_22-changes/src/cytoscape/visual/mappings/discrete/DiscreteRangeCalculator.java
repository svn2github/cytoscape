//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Range Calculator for the Discrete Mapper.
 */
public class DiscreteRangeCalculator {
    private TreeMap map;
    private String attrName;

    /**
     * Constructor.
     * @param map Discrete Map.
     * @param attrName Controlling Attribute Name.
     */
    public DiscreteRangeCalculator(TreeMap map, String attrName) {
        this.map = map;
        this.attrName = attrName;
    }

    /**
     * Calculates Range Value.
     * @param attrBundle Attribute Bundle.
     * @return Object.
     */
    public Object calculateRangeValue(Map attrBundle) {
        if (attrBundle == null || attrName == null) {
            return null;
        }
        //extract the data value for our controlling attribute name
        Object attrValue = attrBundle.get(attrName);

        if (attrValue == null) {
            return null;
        }
        //from here we have to catch ClassCastExceptions that will be
        //thrown if the data value is not of a type comparable to the keys
        //in this SortedMap
        try {
            //if the attrValue is a List, search for an object in the List
            //that maps to a non-null value, and return the matching value
            if (attrValue instanceof List) {
                Iterator attrValueIt = ((List) attrValue).iterator();
                while (attrValueIt.hasNext()) {
                    Object attrSubValue = attrValueIt.next();
                    if (map.get(attrSubValue) != null) {
                        return map.get(attrSubValue);
                    }
                }
                //if not found, return null
                return null;
            } else {
                //OK, try the attrValue itself as a key
                return map.get(attrValue); //returns null if not found
            }
        } catch (ClassCastException e) {
            return null;
        }
    }
}