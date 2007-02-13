
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

package cytoscape.visual;

import giny.model.Node;

import giny.view.GraphView;
import giny.view.Label;
import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;


/**
 * This is a dummy implementation of NodeView that
 * can be used for unit testing.
 */
public class TestNodeView implements NodeView {
	int shape = 0;
	Paint selectedPaint = Color.RED;
	Paint unselectedPaint = Color.BLUE;
	Stroke border = new BasicStroke();
	float borderWidth = 0;
	Paint borderPaint = Color.GREEN;
	float transparency = 0;
	double width = 0;
	double height = 0;
	double x_pos = 0;
	double y_pos = 0;
	boolean selected = false;
	String toolTip = "";
	Point2D offset = new Point2D.Double();
	int degree = 0;
	Label label = new TestLabel();
	double label_offset_x = 0.0;
	double label_offset_y = 0.0;
	int node_label_anchor = 0;

	/**
	 * Creates a new TestNodeView object.
	 */
	public TestNodeView() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public GraphView getGraphView() {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Node getNode() {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getGraphPerspectiveIndex() {
		return 1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getRootGraphIndex() {
		return 1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param otherNode DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List getEdgeViewsList(NodeView otherNode) {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getShape() {
		return shape;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param s DOCUMENT ME!
	 */
	public void setShape(int s) {
		shape = s;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param p DOCUMENT ME!
	 */
	public void setSelectedPaint(Paint p) {
		selectedPaint = p;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Paint getSelectedPaint() {
		return selectedPaint;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param p DOCUMENT ME!
	 */
	public void setUnselectedPaint(Paint p) {
		unselectedPaint = p;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Paint getUnselectedPaint() {
		return unselectedPaint;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param b DOCUMENT ME!
	 */
	public void setBorderPaint(Paint b) {
		borderPaint = b;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Paint getBorderPaint() {
		return borderPaint;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param bw DOCUMENT ME!
	 */
	public void setBorderWidth(float bw) {
		borderWidth = bw;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public float getBorderWidth() {
		return borderWidth;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param s DOCUMENT ME!
	 */
	public void setBorder(Stroke s) {
		border = s;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Stroke getBorder() {
		return border;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param trans DOCUMENT ME!
	 */
	public void setTransparency(float trans) {
		transparency = trans;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public float getTransparency() {
		return transparency;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param w DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setWidth(double w) {
		width = w;

		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getWidth() {
		return width;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param h DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setHeight(double h) {
		height = h;

		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getHeight() {
		return height;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public giny.view.Label getLabel() {
		return label;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void setLabel(Label l) {
		label = l;
	} // not in interface

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getDegree() {
		return degree;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public void setOffset(double x, double y) {
		offset.setLocation(x, y);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Point2D getOffset() {
		return offset;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 */
	public void setXPosition(double x) {
		x_pos = x;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param update DOCUMENT ME!
	 */
	public void setXPosition(double x, boolean update) {
		x_pos = x;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getXPosition() {
		return x_pos;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param y DOCUMENT ME!
	 */
	public void setYPosition(double y) {
		y_pos = y;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param y DOCUMENT ME!
	 * @param update DOCUMENT ME!
	 */
	public void setYPosition(double y, boolean update) {
		y_pos = y;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getYPosition() {
		return y_pos;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param animate DOCUMENT ME!
	 */
	public void setNodePosition(boolean animate) {
	}
	; // WTF???

	/**
	 *  DOCUMENT ME!
	 */
	public void select() {
		selected = true;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void unselect() {
		selected = false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param s DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setSelected(boolean s) {
		selected = s;

		return selected;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param tip DOCUMENT ME!
	 */
	public void setToolTip(String tip) {
		toolTip = tip;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getToolTip() {
		return toolTip;
	} // not in the interface

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 */
	public void setLabelOffsetX(double x) {
		label_offset_x = x;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getLabelOffsetX() {
		return label_offset_x;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param y DOCUMENT ME!
	 */
	public void setLabelOffsetY(double y) {
		label_offset_y = y;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getLabelOffsetY() {
		return label_offset_y;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param position DOCUMENT ME!
	 */
	public void setNodeLabelAnchor(int position) {
		node_label_anchor = position;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getNodeLabelAnchor() {
		return node_label_anchor;
	}
}
