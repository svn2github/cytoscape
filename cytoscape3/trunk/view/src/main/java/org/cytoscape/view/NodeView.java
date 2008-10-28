
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.view;

import cytoscape.render.stateful.CustomGraphic;

import org.cytoscape.*;

import java.awt.*;
import java.awt.geom.Point2D;

import java.util.Iterator;


/**
 * Any and all questions should be directed to me.

 * @author Rowan Christmas
 */
public interface NodeView extends GraphViewObject {
	/**
	 * 
	 */
	public static final int TRIANGLE = 0;

	/**
	 * 
	 */
	public static final int DIAMOND = 1;

	/**
	 * 
	 */
	public static final int ELLIPSE = 2;

	/**
	 * 
	 */
	public static final int HEXAGON = 3;

	/**
	 * 
	 */
	public static final int OCTAGON = 4;

	/**
	 * 
	 */
	public static final int PARALELLOGRAM = 5;

	/**
	 * 
	 */
	public static final int RECTANGLE = 6;

	/**
	 * 
	 */
	public static final int ROUNDED_RECTANGLE = 7;

	/**
	 * @return The Node we are a view on
	 */
	public Node getNode();

	/**
	 * @return the index of this node in the perspective to which we are in a view on.
	 */
	public int getGraphPerspectiveIndex();

	/**
	 * @return the index of this node in the root graph to which we are in a view on.
	 */
	public int getRootGraphIndex();

	/**
	 * @return The list of EdgeViews connecting these two nodes. Possibly null.
	 */
	public java.util.List<EdgeView> getEdgeViewsList(NodeView otherNode);

	/**
	 * Shape is currently defined via predefined variables in
	 * the NodeView interface. To get the actual java.awt.Shape
	 * use getPathReference()
	 * @return the current int-tpye shape
	 */
	public int getShape();

	/**
	 * This sets the Paint that will be used by this node
	 * when it is painted as selected.
	 * @param paint The Paint to be used
	 */
	public void setSelectedPaint(Paint paint);

	/**
	 * @return the currently set selection Paint
	 */
	public Paint getSelectedPaint();

	/**
	  * Set the deafult paint of this node
	  * @param paint the default Paint of this node
	  */
	public void setUnselectedPaint(Paint paint);

	/**
	 * @return the currently set paint
	 */
	public Paint getUnselectedPaint();

	/**
	 * @param b_paint the paint the border will use
	 */
	public void setBorderPaint(Paint b_paint);

	/**
	 * @return the currently set BOrder Paint
	 */
	public Paint getBorderPaint();

	/**
	 * @param border_width The width of the border.
	 */
	public void setBorderWidth(float border_width);

	/**
	 * @return the currently set Border width
	 */
	public float getBorderWidth();

	/**
	 * @param stroke the new stroke for the border
	 */
	public void setBorder(Stroke stroke);

	/**
	 * @return the current border
	 */
	public Stroke getBorder();

	/**
	 * @param trans new value for the transparency
	 */
	public void setTransparency(float trans);

	/**
	 * @return the value for the transparency for this node
	 */
	public float getTransparency();

	/**
	 * TODO: Reconcile with Border Methods
	 * @param width the currently set width of this node
	 */
	public boolean setWidth(double width);

	/**
	 * TODO: Reconcile with Border Methods
	 * @return the currently set width of this node
	 */
	public double getWidth();

	/**
	 * TODO: Reconcile with Border Methods
	 * @param height the currently set height of this node
	 */
	public boolean setHeight(double height);

	/**
	 * TODO: Reconcile with Border Methods
	 * @return the currently set height of this node
	 */
	public double getHeight();

	/**
	 * @return The Value of the label
	 */
	public org.cytoscape.view.Label getLabel();

	/**
	 * @return the degree of the Node in the GraphPerspective.
	 */
	public int getDegree();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public void setOffset(double x, double y);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Point2D getOffset();

	/**
	 * @param new_x_position the new X position for this node
	 */
	public void setXPosition(double new_x_position);

	/**
	 * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
	 // TODO -- HACKY
	 * @param  new_x_position for this node
	 * @param  update if this is true, the node will move immediatly.
	 */
	public void setXPosition(double new_x_position, boolean update);

	/**
	 * note that unless updateNode() has been called, this may not be
	 * the "real" location of this node
	 * @return the current x position of this node
	 * @see #setXPosition
	 */
	public double getXPosition();

	/**
	 * @param new_y_position the new Y position for this node
	 */
	public void setYPosition(double new_y_position);

	/**
	 * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
	 // TODO -- HACKY
	 * @param  new_y_position for this node
	 * @param  update if this is true, the node will move immediatly.
	*/
	public void setYPosition(double new_y_position, boolean update);

	/**
	 * note that unless updateNode() has been called, this may not be
	 * the "real" location of this node
	 * @return the current y position of this node
	 * @see #setYPosition
	 */
	public double getYPosition();

	/**
	 * moves this node to its stored x and y locations.
	 */
	public void setNodePosition(boolean animate);

	/**
	 * This draws us as selected
	 */
	public void select();

	/**
	 * This draws us as unselected
	 */
	public void unselect();

	/**
	 *
	 */
	public boolean isSelected();

	/**
	 *
	 */
	public boolean setSelected(boolean selected);

	/**
	 * Set a new shape for the Node, based on one of the pre-defined shapes
	 * <B>Note:</B> calling setPathTo( Shape ), allows one to define their own
	 * java.awt.Shape ( i.e. A picture of Johnny Cash )
	 */
	public void setShape(int shape);

	/**
	 * Sets what the tooltip will be for this NodeView
	 */
	public void setToolTip(String tip);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 */
	public void setLabelOffsetX(double x);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param y DOCUMENT ME!
	 */
	public void setLabelOffsetY(double y);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param position DOCUMENT ME!
	 */
	public void setNodeLabelAnchor(int position);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getLabelOffsetX();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getLabelOffsetY();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getNodeLabelAnchor();

	// MLC 09/18/08 BEGIN:
	//	public int getCustomGraphicCount();
	//	public Shape getCustomGraphicShape(int index);
	//	public Paint getCustomGraphicPaint(int index);
	//	public void removeCustomGraphic(int index);
	//	public void addCustomGraphic(Shape s, Paint p, int index);

	// KONO 10/28/2008 Deprecated method are removed.

	// NEW CUSTOM GRAPHIC OPS:

	/**
	 * Adds a custom graphic, <EM>in draw order</EM>, to this
	 * NodeView in a thread-safe way.  This is a convenience method
	 * that is equivalent to calling:
	 * <CODE>
	 *   addCustomGraphic (new CustomGraphic (shape,paint,anchor))
	 * </CODE>
	 * except the the new CustomGraphic created is returned.
	 * @param shape
	 * @param paint
	 * @param anchor The byte value from NodeDetails, that defines where the graphic anchor point lies on this NodeView's extents rectangle. A common anchor is NodeDetails.ANCHOR_CENTER.
	 * @since Cytoscape 2.6
	 * @throws IllegalArgumentException if shape or paint are null or anchor is not in the range 0 <= anchor <= NodeDetails.MAX_ANCHOR_VAL.
	 * @return The CustomGraphic added to this NodeView.
	 * @see #addCustomGraphic(CustomGraphic)
	 * @see cytoscape.render.stateful.CustomGraphic
	 */
	public CustomGraphic addCustomGraphic(Shape s, Paint p, byte anchor);

	/**
	 * Adds a given CustomGraphic, <EM>in draw order</EM>, to this
	 * NodeView in a thread-safe way.  Each CustomGraphic will be
	 * drawn in the order is was added. So, if you care about draw
	 * order (as for overlapping graphics), make sure you add them in
	 * the order you desire.  Note that since CustomGraphics may be
	 * added by multiple plugins, your additions may be interleaved
	 * with others.
	 *
	 * <P>A CustomGraphic can only be associated with a NodeView
	 * once.  If you wish to have a custom graphic, with the same
	 * paint and shape information, occur in multiple places in the
	 * draw order, simply create a new CustomGraphic and add it.
	 *
	 * @since Cytoscape 2.6
	 * @throws IllegalArgumentException if shape or paint are null.
	 * @return true if the CustomGraphic was added to this NodeView.
	 *         false if this NodeView already contained this CustomGraphic.
	 * @see cytoscape.render.stateful.CustomGraphic
	 */
	public boolean addCustomGraphic(CustomGraphic cg);

	/**
	 * A thread-safe way to determine if this NodeView contains a given custom graphic.
	 * @param cg the CustomGraphic for which we are checking containment.
	 * @since Cytoscape 2.6
	 */
	public boolean containsCustomGraphic(CustomGraphic cg);

	/**
	 * Return a non-null, read-only Iterator over all CustomGraphics contained in this NodeView.
	 * The Iterator will return each CustomGraphic in draw order.
	 * The Iterator cannot be used to modify the underlying set of CustomGraphics.
	 * @return The CustomGraphics Iterator. If no CustomGraphics are
	 * associated with this DNOdeView, an empty Iterator is returned.
	 * @throws UnsupportedOperationException if an attempt is made to use the Iterator's remove() method.
	 * @since Cytoscape 2.6
	 */
	public Iterator<CustomGraphic> customGraphicIterator();

	/**
	 * A thread-safe method for removing a given custom graphic from this NodeView.
	 * @return true if the custom graphic was found an removed. Returns false if
	 *         cg is null or is not a custom graphic associated with this NodeView.
	 * @since Cytoscape 2.6
	 */
	public boolean removeCustomGraphic(CustomGraphic cg);

	/**
	 * A thread-safe method returning the number of custom graphics
	 * associated with this NodeView. If none are associated, zero is
	 * returned.
	 * @since Cytoscape 2.6
	 */
	public int getNumCustomGraphics();

	/**
	 * Obtain the lock used for reading information about custom
	 * graphics.  This is <EM>not</EM> needed for thread-safe custom graphic
	 * operations, but only needed for use with
	 * thread-compatible methods, such as customGraphicIterator().
	 * For example, to iterate over all custom graphics without fear of
	 * the underlying custom graphics being mutated, you could perform:
	 * <PRE>
	 *    NodeView nv = ...;
	 *    CustomGraphic cg = null;
	 *    synchronized (nv.customGraphicLock()) {
	 *       Iterator<CustomGraphic> cgIt = nv.customGraphicIterator();
	 *       while (cgIt.hasNext()) {
	 *          cg = cgIt.next();
	 *          // PERFORM your operations here.
	 *       }
	 *   }
	 * </PRE>
	 * NOTE: A better concurrency approach would be to return the read
	 *       lock from a
	 *       java.util.concurrent.locks.ReentrantReadWriteLock.
	 *       However, this requires users to manually lock and unlock
	 *       blocks of code where many times try{} finally{} blocks
	 *       are needed and if any mistake are made, a NodeView may be
	 *       permanently locked. Since concurrency will most
	 *       likely be very low, we opt for the simpler approach of
	 *       having users use synchronized {} blocks on a standard
	 *       lock object.
	 * @return the lock object used for custom graphics of this NodeView.
	 */
	public Object customGraphicLock();

	// MLC 09/18/08 END.
}
