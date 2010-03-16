

package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class SineWaveStroke extends ShapeStroke {

	// TODO consider making the wavelength and amplitude Cytoscape properties.

	public SineWaveStroke(float width, String name) {
		// second arg here is the advance - advance must equal wavelength below
		super( new Shape[] { getSineWave(width) }, 10f, name, width );
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new SineWaveStroke(w,name);
	}

	static Shape getSineWave(final float width) {
		GeneralPath shape = new GeneralPath();

		// wavelength must equal advance specified in constructor or 
		// else the waves won't line up!
		final float wavelength = 10f; 
		final float amplitude = 5f; 

		shape.moveTo(0f,0f);
		shape.lineTo(0f,width);
		shape.quadTo(0.25f*wavelength,amplitude+width,   0.5f*wavelength,width);
		shape.quadTo(0.75f*wavelength,-amplitude-width,      wavelength,width);
		shape.lineTo(wavelength,0f);
		shape.quadTo(0.75f*wavelength,-amplitude-width,   0.5f*wavelength,0f);
		shape.quadTo(0.25f*wavelength,amplitude+width,      0f,0f);

		return shape;
	}
}


