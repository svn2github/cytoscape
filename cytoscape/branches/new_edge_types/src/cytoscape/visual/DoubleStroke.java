
package cytoscape.visual;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;

/*
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */
class DoubleStroke implements Stroke {
	BasicStroke stroke1, stroke2; // the two strokes to use

	public DoubleStroke(float width1, float width2) {
		stroke1 = new BasicStroke(width1); // Constructor arguments specify
		stroke2 = new BasicStroke(width2); // the line widths for the strokes
	}

	public Shape createStrokedShape(Shape s) {
		// Use the first stroke to create an outline of the shape
		Shape outline = stroke1.createStrokedShape(s);
		// Use the second stroke to create an outline of that outline.
		// It is this outline of the outline that will be filled in
		return stroke2.createStrokedShape(outline);
	}
}


