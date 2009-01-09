//AttributeMapperCategories.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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
     * Given a range attribute value object, it returns its string representation.
     * If the type of the value object does not match the type of the range attribute
     * (for example, specifying EDGE_LINETYPE range attribute, and passing in 
     * a Color value) then it returns the empty string, and prints a message to System.error.
     */
    String rangeAttributeValueToString(Integer rangeAttribute,
				       Object rangeAttributeValue);
    
    /**
     * Given the requested rangeAttribute, returns an Interpolator object
     * that knows how to interpolate objects of the type associated with
     * that rangeAttribute. Returns null if the requested rangeAttribute
     * is unknown, or if no acceptable Interplator exists.
     */
    Interpolator getInterpolator(Integer rangeAttribute);
}


