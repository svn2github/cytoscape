//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;
import javax.swing.JDialog;
import cytoscape.visual.Network;
import cytoscape.visual.parsers.ValueParser;
//----------------------------------------------------------------------------
/**
 * Mappings should implement this interface. Mappings are classes that map from
 * a value stored in the edge attributes or node attributes HashMap in
 * {@link cytoscape.GraphObjAttributes}. The range of the mapping depends on the
 * {@link cytoscape.visual.calculators.AbstractCalculator} that owns
 * the mapping.
 * <p>
 * All classes implementing this interface <b>MUST</b> have a constructor that
 * takes the arguments Object, Network, byte, where Object is the default object
 * the mapper should map to, Network is the Network object representing the network
 * displayed in Cytoscape, and the byte is one of {@link #EDGE_MAPPING} or
 * {@link #NODE_MAPPING}.
 */
public interface ObjectMapping extends Cloneable {
    public static final byte EDGE_MAPPING = 0;
    public static final byte NODE_MAPPING = 1;    

    Class getRangeClass();
    /**
     * Return the classes that the ObjectMapping can map from, eg. the contents
     * of the data of the controlling attribute.
     * <p>
     * For example, DiscreteMapping {@link DiscreteMapping} can only accept
     * String types in the mapped attribute data. Likewise, ContinuousMapping
     * {@link ContinuousMapping} can only accept numeric types in the mapped
     * attribute data since it must interpolate.
     * <p>
     * Return null if this mapping has no restrictions on the domain type.
     * 
     * @return Array of accepted attribute data class types
     */
    Class[] getAcceptedDataClasses();

    /**
     * Set the controlling attribute name. The current mappings will be unchanged
     * if preserveMapping is true and cleared otherwise. The network argument is
     * provided so that the current values for the given attribute name can
     * be loaded for UI purposes. Null values for the network argument are allowed.
     */
    void setControllingAttributeName(String attrName, Network network,
                                     boolean preserveMapping);

    /**
     * Get the controlling attribute name
     */
    String getControllingAttributeName();

    Object calculateRangeValue(Map attrBundle);

    JPanel getUI(JDialog parent, Network network);

    Object clone();
    
    void applyProperties(Properties props, String baseKey, ValueParser parser);

    Properties getProperties(String baseKey);
}

