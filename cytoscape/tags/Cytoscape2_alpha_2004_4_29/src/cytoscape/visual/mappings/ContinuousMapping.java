//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;

import cytoscape.data.CyNetwork;
import cytoscape.visual.SubjectBase;
import cytoscape.visual.mappings.continuous.*;
import cytoscape.visual.parsers.ValueParser;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Implements an interpolation table mapping data to values of a particular
 * class.  The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 *
 * For refactoring changes in this class, please refer to:
 * cytoscape.visual.mappings.continuous.README.txt.
 *
 */
public class ContinuousMapping extends SubjectBase implements ObjectMapping {

    Object defaultObj;          //  the default value held by this mapping
    Class rangeClass;           //  the class of values held by this mapping
    String attrName;            //  the name of the controlling data attribute
    Interpolator interpolator;  //  used to interpolate between boundaries
    private byte mapType;       //  mapping type value

    //  Contains List of Data Points
    private ArrayList points = new ArrayList();

    /**
     *  Constructor.
     *	@param	defaultObj default object to map to
     *	@param	mapType	Type of mapping, one of
     *  {@link ObjectMapping#EDGE_MAPPING} or {@link ObjectMapping#NODE_MAPPING}
     */
    public ContinuousMapping(Object defaultObj, byte mapType)
            throws IllegalArgumentException {
        this.rangeClass = defaultObj.getClass();
        this.defaultObj = defaultObj;
        this.mapType = mapType;

        //  Validate Map Type
        if (mapType != ObjectMapping.EDGE_MAPPING &&
                mapType != ObjectMapping.NODE_MAPPING) {
            throw new IllegalArgumentException("Unknown mapping type "
                    + mapType);
        }

        //  Create Interpolator
        if (Color.class.isAssignableFrom(this.rangeClass)) {
            interpolator = new LinearNumberToColorInterpolator();
        } else if (Number.class.isAssignableFrom(this.rangeClass)) {
            interpolator = new LinearNumberToNumberInterpolator();
        } else {
            interpolator = new FlatInterpolator();
        }
    }

    /**
     * Clones the Mapping Object.
     * @return Cloned Mapping Object.
     */
    public Object clone() {
        ContinuousMapping clone = new ContinuousMapping(defaultObj, mapType);
        //  Copy over all listeners...
        for (int i=0; i<observers.size(); i++) {
            clone.addChangeListener((ChangeListener) observers.get(i));
        }
        for (int i = 0; i < points.size(); i++) {
            ContinuousMappingPoint cmp = (ContinuousMappingPoint) points.get(i);
            ContinuousMappingPoint cmpClone = (ContinuousMappingPoint)
                    cmp.clone();
            clone.addPoint(cmpClone.getValue(), cmpClone.getRange());
        }
        return clone;
    }

    /**
     * Gets all Data Points.
     * @return ArrayList of ContinuousMappingPoint objects.
     */
    public ArrayList getAllPoints() {
        return points;
    }

    /**
     *  Adds a New Data Point.
     */
    public void addPoint(double value, BoundaryRangeValues brv) {
        ContinuousMappingPoint cmp = new ContinuousMappingPoint(value, brv);
        points.add(cmp);
    }

    /**
     * Removes a Point from the List.
     */
    public void removePoint(int index) {
        points.remove(index);
    }

    /**
     * Gets Total Point Count.
     */
    public int getPointCount() {
        return points.size();
    }

    /**
     * Gets Specified Point.
     * @param index Index Value.
     * @return ContinuousMappingPoint.
     */
    public ContinuousMappingPoint getPoint(int index) {
        return (ContinuousMappingPoint) points.get(index);
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
        ContinuousMappingReader reader = new ContinuousMappingReader(props,
                baseKey, parser);
        this.points = reader.getPoints();
        this.attrName = reader.getControllingAttributeName();
        this.interpolator = reader.getInterpolator();
    }


    /**
     * Returns a Properties object with entries suitable for customizing this
     * object via the applyProperties method.
     * Required by the ObjectMapping interface.
     * @param baseKey Base Key for creating properties.
     * @return Properties Object.
     */
    public Properties getProperties(String baseKey) {
        ContinuousMappingWriter writer = new ContinuousMappingWriter
            (points, baseKey, attrName, interpolator);
        Properties newProps = writer.getProperties();
        return newProps;
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
        // only numbers supported
        Class[] ret = {Number.class};
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
     * Sets the Name of the Controlling Attribte.
     * @param attrName Attribute Name.
     * @param network CytoscapeNetwork Object.
     * @param preserveMapping Flag to preserve mapping.
     */
    public void setControllingAttributeName(String attrName, CyNetwork network,
            boolean preserveMapping) {
        this.attrName = attrName;
    }

    /**
     * Gets the Mapping Interpolator.
     * Required by the ObjectMapping interface.
     * @return Interpolator Object.
     */
    public Interpolator getInterpolator() {
        return interpolator;
    }

    /**
     * Sets the Mapping Interpolator.
     * Required by the ObjectMapping interface.
     * @param interpolator Interpolator Object.
     */
    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    /**
     * Gets the UI Object Associated with the Mapper.
     * Required by the ObjectMapping interface.
     * @param dialog Parent Dialog.
     * @param network CyNetwork.
     * @return JPanel Object.
     */
    public JPanel getUI(JDialog dialog, CyNetwork network) {
        ContinuousUI ui = new ContinuousUI(dialog, defaultObj, network, this);
        return ui;
    }

    /**
     * Calculates the Range Value.
     * Required by the ObjectMapping interface.
     * @param attrBundle A Bundle of Attributes.
     * @return Mapping object.
     */
    public Object calculateRangeValue(Map attrBundle) {
        ContinuousRangeCalculator calc = new ContinuousRangeCalculator
            (points, interpolator, attrBundle);
        Object object = calc.calculateRangeValue(attrName);
        return object;
    }
}
