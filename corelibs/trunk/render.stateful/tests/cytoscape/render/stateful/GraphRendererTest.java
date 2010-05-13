
/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.render.stateful;

import junit.framework.*;

import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.immed.EdgeAnchors;
import java.awt.image.BufferedImage;
import java.awt.Image;

public class GraphRendererTest extends TestCase {

	private GraphGraphics grafx;

	public void setUp() {
		Image img = new BufferedImage(500,500,BufferedImage.TYPE_INT_ARGB);
		grafx = new GraphGraphics( img, false);
	}

/*
    	boolean GraphRenderer.computeEdgeEndpoints(final GraphGraphics grafx,
                                                     final float[] srcNodeExtents,
                                                     final byte srcNodeShape, final byte srcArrow,
                                                     final float srcArrowSize, EdgeAnchors anchors,
                                                     final float[] trgNodeExtents,
                                                     final byte trgNodeShape, final byte trgArrow,
                                                     final float trgArrowSize,
                                                     final float[] rtnValSrc,
                                                     final float[] rtnValTrg);
*/
	public void testComputeEdgeEndpoints() {
		float[] srcNodeExtents = {10.0f,10.0f,20.0f,20.0f};
		float[] trgNodeExtents = {110.0f,110.0f,120.0f,120.0f};
		float[] rtnValSrc = new float[2];
		float[] rtnValTrg = new float[2];
		boolean ret = GraphRenderer.computeEdgeEndpoints(grafx,
		                                  srcNodeExtents,GraphGraphics.SHAPE_ELLIPSE,
                                          GraphGraphics.ARROW_DISC,5.0f,
										  null /*anchors*/,
										  trgNodeExtents,GraphGraphics.SHAPE_RECTANGLE,
										  GraphGraphics.ARROW_DELTA,7.0f,
										  rtnValSrc,rtnValTrg);

		System.out.println("source X: " + rtnValSrc[0] + "  Y: " + rtnValSrc[1]);
		System.out.println("target X: " + rtnValTrg[0] + "  Y: " + rtnValTrg[1]);
		//source X: 18.535534  Y: 18.535534
		//target X: 110.0  Y: 110.0
		assertEquals( rtnValSrc[0], 18.5355f, 0.001f );
		assertEquals( rtnValSrc[1], 18.5355f, 0.001f );
		assertEquals( rtnValTrg[0], 110.000f, 0.001f );
		assertEquals( rtnValTrg[1], 110.000f, 0.001f );
	}

	private class SingleEdgeAnchor implements EdgeAnchors {
		private float[] pt;	
		SingleEdgeAnchor(float[] pt) {
			if ( pt.length != 2 )
				throw new IllegalArgumentException("must be exactly one anchor");
			this.pt = pt;
		}
    	public int numAnchors() { return 1;	}
    	public void getAnchor(int anchorIndex, float[] anchorArr, int offset) {
			anchorArr[0] = pt[0];
			anchorArr[1] = pt[1];
		}
	}
}

