//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.BoundaryRangeValues;

/**
 * Encapsulates a ContinuousMapping Point with a single point value
 * and associated BoundaryRangeValues.
 *
 */
public class ContinuousMappingPoint implements Cloneable {
    private double value;
    private BoundaryRangeValues range;

    /**
     * Constructor.
     * @param value double.
     * @param range BoundaryRangeValues object.
     */
    public ContinuousMappingPoint(double value, BoundaryRangeValues
            range) {
        this.value = value;
        this.range = range;
    }

    /**
     * Gets Point Value.
     * @return double value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets Point Value.
     * @param value double value.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets BoundaryRangeValues.
     * @return BoundaryRangeValues Object.
     */
    public BoundaryRangeValues getRange() {
        return range;
    }

    /**
     * Sets BoundaryRangeValues.
     * @param range BoundaryRangeValues Object.
     */
    public void setRange(BoundaryRangeValues range) {
        this.range = range;
    }

    /**
     * Clones the object.
     * @return Cloned Object.
     */
    public Object clone() {
        BoundaryRangeValues newRange = new BoundaryRangeValues();
        newRange.lesserValue = range.lesserValue;
        newRange.equalValue = range.equalValue;
        newRange.greaterValue = range.greaterValue;
        ContinuousMappingPoint newPoint = new ContinuousMappingPoint
                (value, newRange);
        return newPoint;
    }
}