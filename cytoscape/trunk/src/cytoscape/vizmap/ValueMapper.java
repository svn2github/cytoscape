//ValueMapper.java
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
 * Interface to classes that provide a mapping from domain to range values.
 */
public interface ValueMapper {

    public Object getRangeValue(Object domainValue);
}
