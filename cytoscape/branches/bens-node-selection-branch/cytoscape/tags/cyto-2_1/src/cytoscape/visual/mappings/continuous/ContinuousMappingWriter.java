//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.InterpolatorFactory;
import cytoscape.visual.parsers.ObjectToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Writes out ContinuousMapping Properties.
 */
public class ContinuousMappingWriter {
    private Properties newProps;

    /**
     * Constructor.
     * @param points ArrayList of ContinuousMappintPoints.
     * @param baseKey Base Key String.
     * @param attrName Controlling Attribute String.
     * @param interp Interpolator Object.
     */
    public ContinuousMappingWriter(ArrayList points, String baseKey,
            String attrName, Interpolator interp) {
        newProps = new Properties();
        loadProperties(points, baseKey, attrName, interp);
    }

    /**
     * Gets Newly Defined Properties Object.
     * @return Properties Object.
     */
    public Properties getProperties() {
        return newProps;
    }

    /**
     * Return a Properties object with entries suitable for customizing this
     * object via the applyProperties method.
     */
    private void loadProperties(ArrayList points, String baseKey,
            String contAttrName, Interpolator interp) {

        // save the controlling attribute name
        String contAttrKey = baseKey + ".controller";

        if (contAttrName != null) {
            newProps.setProperty(contAttrKey, contAttrName);
        }

        // save the interpolator
        String intKey = baseKey + ".interpolator";
        String intName = InterpolatorFactory.getName(interp);
        newProps.setProperty(intKey, intName);

        //  save the number of boundary values
        String bvNumKey = baseKey + ".boundaryvalues";
        int numBV = points.size();
        String numString = Integer.toString(numBV);
        newProps.setProperty(bvNumKey, numString);

        //  save each of the boundary values
        int count = 0;
        for (Iterator si = points.iterator(); si.hasNext(); count++) {
            String bvBase = baseKey + ".bv" + count;

            //  save the domain value
            String bvKey = bvBase + ".domainvalue";
            ContinuousMappingPoint cmp = (ContinuousMappingPoint) si.next();
            Double dVal = new Double(cmp.getValue());
            String dValString = dVal.toString();
            newProps.setProperty(bvKey, dValString);

            //  save the fields of the brv object
            BoundaryRangeValues brv = (BoundaryRangeValues) cmp.getRange();
            String lKey = bvBase + ".lesser";
            String lString = ObjectToString.getStringValue(brv.lesserValue);
            newProps.setProperty(lKey, lString);
            String eKey = bvBase + ".equal";
            String eString = ObjectToString.getStringValue(brv.equalValue);
            newProps.setProperty(eKey, eString);
            String gKey = bvBase + ".greater";
            String gString = ObjectToString.getStringValue(brv.greaterValue);
            newProps.setProperty(gKey, gString);
        }
    }
}
