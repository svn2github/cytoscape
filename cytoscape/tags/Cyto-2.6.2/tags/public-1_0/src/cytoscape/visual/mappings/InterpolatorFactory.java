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

    /**
     * Given an Interpolator, returns an identifying name as recognized
     * by the newInterpolator method. null will be returned if the argument
     * is null or of an unrecognized class type.
     */
    public static String getName(Interpolator fInt) {
        if (fInt == null) {
            return null;
        } else if (fInt instanceof LinearNumberToColorInterpolator) {
            return new String("LinearNumberToColorInterpolator");
        } else if (fInt instanceof LinearNumberToNumberInterpolator) {
            return new String("LinearNumberToNumberInterpolator");
        } else if (fInt instanceof FlatInterpolator) {
            return new String("FlatInterpolator");
        } else {
            String c = fInt.getClass().getName();
            System.err.println("Unknown Interpolator type: " + c);
            return null;
        }
    }
}
