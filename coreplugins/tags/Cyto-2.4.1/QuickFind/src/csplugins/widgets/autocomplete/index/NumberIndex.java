package csplugins.widgets.autocomplete.index;

import java.util.List;

/**
 * Number index interface.
 * <p/>
 * This is a core data structure for indexing arbitrary Objects, based on a
 * numeric value, e.g. Integer or Double.
 *
 * @author Ethan Cerami.
 */
public interface NumberIndex extends GenericIndex {

    /**
     * Gets a closed range of indexed values between lower and upper.
     *
     * Returns a view of the portion of this set whose elements range from
     * lower, inclusive, to upper, inclusive.
     *
     * @param lower lower bound.
     * @param upper upper bound.
     * @return List of Objects.
     */
    List getRange (Number lower, Number upper);

    /**
     * Gets minimum value in index.
     * @return min value.
     */
    Number getMinimumValue();

    /**
     * Gets maximum value in index.
     * @return max value.
     */
    Number getMaximumValue();
}
