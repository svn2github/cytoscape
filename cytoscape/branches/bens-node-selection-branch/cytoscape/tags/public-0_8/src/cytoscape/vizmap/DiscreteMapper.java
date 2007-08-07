//DiscreteMapper.java
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
 * This class implements a mapping from discrete domain values to their
 * associated range values via a simply lookup table.
 */
public class DiscreteMapper implements ValueMapper {

    private Map valueMap;

    public DiscreteMapper(Map valueMap) {
	this.setValueMap(valueMap);
    }

    public Map getValueMap() {return valueMap;}
    public void setValueMap(Map valueMap) {this.valueMap = valueMap;}


    public Object getRangeValue(Object domainValue) {
	if (valueMap == null) {
	    return null;
	} else {
	    return valueMap.get(domainValue);
	}
    }
}
