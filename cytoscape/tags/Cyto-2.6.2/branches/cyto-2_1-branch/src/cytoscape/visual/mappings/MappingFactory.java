//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import java.util.Properties;
import java.lang.reflect.Constructor;

import cytoscape.visual.parsers.ValueParser;
//----------------------------------------------------------------------------
/**
 * This class provides a static factory method for constructing an instance
 * of ObjectMapping as specified by a Properties object and other arguments.
 *
 * Since there are currently only a few types of mappings known, it's easiest
 * to simply check each case and construct the right mapping without going
 * through a dynamic class-discovery instantiation process. If the number of
 * mappings ever gets out of hand, we can always switch to a dynamic algorithm.
 */
public class MappingFactory {
    
    /**
     * Attempt to construct an instance of ObjectMapping as defined by
     * the supplied arguments. Checks the value of a recognized key in
     * the Properties argument against a list of known Mappings. If found,
     * constructs the Mapping object and then customizes it by calling its
     * applyProperties method.
     */
    public static ObjectMapping newMapping(Properties props, String baseKey,
                                           ValueParser parser, Object defObj,
                                           byte mapType) {
        String typeName = props.getProperty(baseKey + ".type");
        if (typeName == null) {
            System.err.println("MappingFactory: no Mapping class specified in properties");
            return null;
        } else if (typeName.equals("DiscreteMapping")) {
            DiscreteMapping m = new DiscreteMapping(defObj, mapType);
            m.applyProperties(props, baseKey, parser);
            return m;
        } else if (typeName.equals("ContinuousMapping")) {
            ContinuousMapping m = new ContinuousMapping(defObj, mapType);
            m.applyProperties(props, baseKey, parser);
            return m;
        } else if (typeName.equals("PassThroughMapping")) {
            PassThroughMapping m = new PassThroughMapping(defObj, mapType);
            m.applyProperties(props, baseKey, parser);
            return m;
        } else {
            System.err.println("MappingFactory: unknown Mapping type: " + typeName);
            return null;
        }
    }
    
    /**
     * Gets a description of the supplied ObjectMapping as properties.
     * This method calls the getProperties() method of the ObjectMapping
     * argument and then adds a property to identify the mapping class,
     * in a form recognized by the newMapping method.
     */
    public static Properties getProperties(ObjectMapping m, String baseKey) {
        if (m == null) {return null;}
        Properties newProps = m.getProperties(baseKey);
        if (m instanceof DiscreteMapping) {
            newProps.setProperty(baseKey + ".type", "DiscreteMapping");
        } else if (m instanceof ContinuousMapping) {
            newProps.setProperty(baseKey + ".type", "ContinuousMapping");
        } else if (m instanceof PassThroughMapping) {
            newProps.setProperty(baseKey + ".type", "PassThroughMapping");
        } else {//highly unexpected type
            String c = m.getClass().getName();
            System.err.println("MappingFactory: unknown Mapping type: " + c);
            return null;
        }
        return newProps;
    }
}

