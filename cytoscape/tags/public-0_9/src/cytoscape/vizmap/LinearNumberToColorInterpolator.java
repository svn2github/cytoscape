//LinearNumberToColorInterpolator.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
import java.awt.Color;
//----------------------------------------------------------------------------
/**
 * The class provides a linear interpolation between color values. The
 * (red,green,blue,alpha) values of the returned color are linearly
 * interpolated from the associated values of the lower and upper colors,
 * according the the fractional distance frac from the lower value.
 *
 * If either object argument is not a Color, null is returned.
 */
public class LinearNumberToColorInterpolator extends LinearNumberInterpolator {

    public LinearNumberToColorInterpolator() {}

    public Object getRangeValue(double frac, Object lowerRange,
				Object upperRange) {
	if ( !(lowerRange instanceof Color) ) {return null;}
	if ( !(upperRange instanceof Color) ) {return null;}

	Color lowerColor = (Color)lowerRange;
	Color upperColor = (Color)upperRange;

	double red = lowerColor.getRed()
	    + frac*( upperColor.getRed() - lowerColor.getRed() );
	double green = lowerColor.getGreen()
	    + frac*( upperColor.getGreen() - lowerColor.getGreen() );
	double blue = lowerColor.getBlue()
	    + frac*( upperColor.getBlue() - lowerColor.getBlue() );
	double alpha = lowerColor.getAlpha()
	    + frac*( upperColor.getAlpha() - lowerColor.getAlpha() );

	return new Color( (int)Math.round(red),(int)Math.round(green),
			  (int)Math.round(blue),(int)Math.round(alpha) );
    }
}
