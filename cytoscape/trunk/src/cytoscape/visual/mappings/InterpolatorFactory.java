//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import java.util.Properties;
import cytoscape.vizmap.*;
//----------------------------------------------------------------------------
/**
 * Provides static factory methods for constructing known interpolators from
 * a recognized name, for example from a properties object.
 */
public class InterpolatorFactory {
    
    /**
     * Attempt to construct one of the standard interpolators. The argument
     * should be the simple class name of a known interpolator (i.e., no
     * package information).
     * 
     */
    public static Interpolator newInterpolator(String typeName) {
        if (typeName == null) {
            String s = "InterpolatorFactory: no Interpolator class specified";
            System.err.println(s);
            return null;
        } else if (typeName.equals("LinearNumberToColorInterpolator")) {
            return new LinearNumberToColorInterpolator();
        } else if (typeName.equals("LinearNumberToNumberInterpolator")) {
            return new LinearNumberToNumberInterpolator();
        } else if (typeName.equals("FlatInterpolator")) {
            return new FlatInterpolator();
        } else {
            String s = "InterpolatorFactory: unknown Interpolator type: " + typeName;
            System.err.println(s);
            return null;
        }
    }
}
