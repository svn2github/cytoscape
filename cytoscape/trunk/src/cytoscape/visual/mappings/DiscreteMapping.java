//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;

//----------------------------------------------------------------------------

import cytoscape.CyNetwork;
import cytoscape.visual.SubjectBase;
import cytoscape.visual.mappings.discrete.DiscreteMappingWriter;
import cytoscape.visual.mappings.discrete.DiscreteRangeCalculator;
import cytoscape.visual.mappings.discrete.DiscreteUI;
import cytoscape.visual.mappings.discrete.DiscreteMappingReader;
import cytoscape.visual.parsers.ValueParser;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.util.*;

/**
 * Implements a lookup table mapping data to values of a particular class.
 * The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 */
public class DiscreteMapping extends SubjectBase implements ObjectMapping {
    Object defaultObj;          // the default value held by this mapping
    Class rangeClass;           // the valid range class for this mapping
    String attrName;            // the name of the controlling data attribute
    protected byte mapType;     //  node or edge; specifies which attributes
                                //  to use.
    private TreeMap treeMap;    //  contains the actual map elements (sorted)
    private Object lastKey;

    /**
     * Constructor.
     * @param defObj Default Object.
     * @param mapType Map Type, ObjectMapping.EDGE_MAPPING or
     * ObjectMapping.NODE_MAPPING.
     */
    public DiscreteMapping(Object defObj, byte mapType) {
        this(defObj, null, mapType);
    }

    /**
     * Constructor.
     * @param defObj Default Object.
     * @param attrName Controlling Attribute Name.
     * @param mapType Map Type, ObjectMapping.EDGE_MAPPING or
     * ObjectMapping.NODE_MAPPING.
    */
    public DiscreteMapping(Object defObj, String attrName, byte mapType) {
        treeMap = new TreeMap();
        this.defaultObj = defObj;
        this.rangeClass = defObj.getClass();
        if (mapType != ObjectMapping.EDGE_MAPPING &&
                mapType != ObjectMapping.NODE_MAPPING) {
            throw new IllegalArgumentException("Unknown mapping type "
                    + mapType);
        }
        this.mapType = mapType;
        if (attrName != null)
            setControllingAttributeName(attrName, null, false);
    }

    /**
     * Clones the Object.
     * @return DiscreteMapping Object.
     */
    public Object clone() {
        DiscreteMapping clone = new DiscreteMapping
                (defaultObj, attrName, mapType);
        //  Copy over all listeners...
        for (int i=0; i<observers.size(); i++) {
            clone.addChangeListener((ChangeListener) observers.get(i));
        }
        clone.putAll((TreeMap) treeMap.clone());
        return clone;
    }

    /**
     * Gets Value for Specified Key.
     * @param key String Key.
     * @return Object.
     */
    public Object getMapValue (Object key) {
        return treeMap.get(key);
    }

    /**
     * Puts New Key/Value in Map.
     * @param key Key Object.
     * @param value Value Object.
     */
    public void putMapValue (Object key, Object value) {
        lastKey = key;
        treeMap.put(key, value);
        fireStateChanged();
    }

    /**
     * Gets the Last Modified Key.
     * @return Key Object.
     */
    public Object getLastKeyModified () {
        return lastKey;
    }

    /**
     * Adds All Members of Specified Map.
     * @param map Map.
     */
    public void putAll (Map map) {
        treeMap.putAll(map);
    }

    /**
     * Gets the Range Class.
     * Required by the ObjectMapping interface.
     * @return Class object.
     */
    public Class getRangeClass() {
        return rangeClass;
    }

    /**
     * Gets Accepted Data Classes.
     * Required by the ObjectMapping interface.
     * @return ArrayList of Class objects.
     */
    public Class[] getAcceptedDataClasses() {
        // only strings supported
        Class[] ret = {String.class, Number.class};
        return ret;
    }

    /**
     * Gets the Name of the Controlling Attribute.
     * Required by the ObjectMapping interface.
     * @return Attribue Name.
     */
    public String getControllingAttributeName() {
        return attrName;
    }

    /**
     * Call whenever the controlling attribute changes. If preserveMapping
     * is true, all the currently stored mappings are unchanged; otherwise
     * all the mappings are cleared. In either case, this method calls
     * {@link #getUI} to rebuild the UI for this mapping, which in turn calls
     * loadKeys to load the current data values for the new attribute.
     * <p>
     * Called by event handler from AbstractCalculator
     * {@link cytoscape.visual.calculators.AbstractCalculator}.
     *
     * @param	attrName	The name of the new attribute to map to
     */
    public void setControllingAttributeName(String attrName, CyNetwork n,
            boolean preserveMapping) {
        this.attrName = attrName;
    }

    /**
     * Customizes this object by applying mapping defintions described by the
     * supplied Properties argument.
     * Required by the ObjectMapping interface.
     * @param props Properties Object.
     * @param baseKey Base Key for finding properties.
     * @param parser ValueParser Object.
     */
    public void applyProperties(Properties props, String baseKey,
            ValueParser parser) {
        DiscreteMappingReader reader = new DiscreteMappingReader
                (props, baseKey, parser);
        String contValue = reader.getControllingAttributeName();
        if (contValue != null) {
            setControllingAttributeName(contValue, null, false);
        }
        this.treeMap = reader.getMap();
    }

    /**
     * Returns a Properties object with entries suitable for customizing this
     * object via the applyProperties method.
     * Required by the ObjectMapping interface.
     * @param baseKey Base Key for creating properties.
     * @return Properties Object.
     */
    public Properties getProperties(String baseKey) {
        DiscreteMappingWriter writer = new DiscreteMappingWriter
            (attrName, baseKey, treeMap);
        return writer.getProperties();
    }

    /**
     * Calculates the Range Value.
     * Required by the ObjectMapping interface.
     * @param attrBundle A Bundle of Attributes.
     * @return Mapping object.
     */
    public Object calculateRangeValue(Map attrBundle) {
        DiscreteRangeCalculator calculator = new DiscreteRangeCalculator
            (treeMap, attrName);
        return calculator.calculateRangeValue(attrBundle);
    }

    /**
     * Gets the UI Object Associated with the Mapper.
     * Required by the ObjectMapping interface.
     * @param parent Parent Dialog.
     * @param network CyNetwork.
     * @return JPanel Object.
     */
    public JPanel getUI(JDialog parent, CyNetwork network) {
        DiscreteUI ui = new DiscreteUI (parent, network ,attrName, defaultObj,
                mapType, this);
        return ui;
    }
}
