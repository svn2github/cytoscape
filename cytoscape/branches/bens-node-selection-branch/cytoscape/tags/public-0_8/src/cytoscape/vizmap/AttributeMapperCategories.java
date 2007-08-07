//AttributeMapperCategories.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
import java.util.Map;
//----------------------------------------------------------------------------
/**
 * This interface specifies the methods needed to describe a set of
 * range attributes to which domain attributes can be mapped. An
 * individual range attribute is identified by an associated constant
 * Integer object.
 */
public interface AttributeMapperCategories {

    /**
     * Returns a map in which the keys are Integer objects identifying
     * the range attributes, and the values are some subclass of Object
     * representing sensible default values for the associated range
     * attribute.
     */
    Map getInitialDefaults();
    /**
     * Returns a map in which the keys are Integer objects identifying
     * the range attributes, and the values are Strings representing the
     * first part of the keys in a Properties object that are associated
     * with the matching range attribute. The AttributeMapperPropertiesAdapter
     * class uses this method when examining a Properties object.
     */
    Map getPropertyNamesMap();
    /**
     * Given a String representing an object for the given range attribute,
     * this method parses the String and returns an instance of the proper
     * class with the state described by the String. Returns null if the
     * requested rangeAttribute is unknown, or if the String cannot be
     * parsed into an appropriate object.
     */
    Object parseRangeAttributeValue(Integer rangeAttribute,
				    String value);
    /**
     * Given the requested rangeAttribute, returns an Interpolator object
     * that knows how to interpolate objects of the type associated with
     * that rangeAttribute. Returns null if the requested rangeAttribute
     * is unknown, or if no acceptable Interplator exists.
     */
    Interpolator getInterpolator(Integer rangeAttribute);
}
