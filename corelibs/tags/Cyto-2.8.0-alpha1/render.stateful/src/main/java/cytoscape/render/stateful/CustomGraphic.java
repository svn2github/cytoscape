/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

/**
 * Immutable representation of extra graphics associated with a
 * NodeView or EdgeView that are defined by plugins and are
 * independent of visual styles.
 */

package cytoscape.render.stateful;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Represents all the information needed to define a custom graphic for a given
 * NodeView. <H3>Deprecation Note</H3> The use of CustomGraphic replaces the
 * entire old index-based custom graphic API. This includes all the NodeDetails
 * methods (and their overridden implementations) that refer to custom graphics
 * using indices:
 * 
 * <PRE>
 *   public int graphicCount(int node)
 *   public Shape graphicShape(int node, int graphicInx)
 *   public Paint graphicPaint(int node, int graphicInx)
 *   public byte graphicNodeAnchor(int node, int graphicInx)
 * </PRE>
 * 
 * <B>To keep things completetly backwards compatible and to avoid introducing
 * bugs, the new API methods are completely independent from the the old API
 * methods. Thus, a custom graphic added using the new API will not be
 * accessible from the old API and visa versa.</B>
 * <P>
 * The reason for the deprecation is:
 * <OL>
 * <LI>Complexity in managing the indices.
 * <P>
 * In order for multiple plugins to use the old API, each must monitor deletions
 * to custom graphics and update their saved indices, since the indices will
 * shift down as graphics are deleted. This management isn't even possible with
 * the old API because there's no event mechanism to inform plugins when the
 * indices change. Also, each plugin must keep a list of all indices for all
 * graphics added, since the indices may not be contiguous.
 * <LI>There is no way to ensure that an index you want to use will not be used
 * by another plugin by the time you attempt to assign it (thread safety).
 * <P>
 * Using indices forces the need for a locking mechanism to ensure you are
 * guaranteed a unique and correct index independent of any other plugins.
 * </OL>
 * For more information, see <A
 * HREF="http://cbio.mskcc.org/cytoscape/bugs/view.php?id=1500">Mantis Bug
 * 1500</A>.
 * 
 * @author Michael L. Creech
 */

public class CustomGraphic {
	private final Shape shape;
	private PaintFactory pf;	
	private Paint paint;
	
	@Deprecated
	private final byte anchor;

	
	/**
	 * Constructor 
	 * @param shape
	 * @param factory
	 */
	public CustomGraphic(final Shape shape, final PaintFactory factory) {
		if (shape == null)
			throw new IllegalArgumentException("The shape given was null.");
		this.shape = shape;
		this.anchor = NodeDetails.ANCHOR_CENTER;
		this.pf = factory;
	}

	/**
	 * Will be removed March 2011.
	 * 
	 * @param shape
	 * @param paint
	 * @param anchor
	 */
	@Deprecated
	public CustomGraphic(final Shape shape, final Paint paint, final byte anchor) {
		this(shape, null);
		if ((anchor < 0) || (anchor > NodeDetails.MAX_ANCHOR_VAL)) {
			throw new IllegalArgumentException("The anchor value " + anchor
					+ " is not in the range 0 <= anchor <= "
					+ NodeDetails.MAX_ANCHOR_VAL + ".");
		}
		this.paint = paint;
		this.pf = new DefaultPaintFactory(this.paint);
	}

	/**
	 * Return the Shape that makes up this CustomGraphic.
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Return the Paint that makes up this CustomGraphic.
	 */
	public Paint getPaint() {
		if (paint != null)
			return paint;
		else
			return getPaint(shape.getBounds2D());
	}

	public Paint getPaint(Rectangle2D bound) {
		paint = pf.getPaint(bound);
		return paint;
	}
	
	public PaintFactory getPaintFactory() {
		return this.pf;
	}

	/**
	 * Return the anchor for this CustomGraphic. This specifies where on the
	 * node's extents rectangle the graphic anchor point lies. The shape is
	 * rendered at a location which is equal to this anchor point plus the
	 * offset vector.
	 */
	@Deprecated
	public byte getAnchor() {
		return anchor;
	}
	
	
	/**
	 * Very simple paint factory.
	 * Always return original paint passed to the constructor.
	 * 
	 * Will be used in this package only.
	 * 
	 * Eventually, this will be removed.
	 *
	 */
	private class DefaultPaintFactory implements PaintFactory {
		
		private final Paint p;
		
		public DefaultPaintFactory(final Paint p) {	
			this.p = p;
		}
		
		/**
		 * Returns white color paint.  This is the default for all custom graphics.
		 */
		public Paint getPaint(final Rectangle2D bound) {
			return p;
		}

	}

}
