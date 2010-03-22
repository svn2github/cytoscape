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

import giny.model.Edge;

import giny.view.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.*;


/**
 * This is a dummy implementation of EdgeView that
 * can be used for unit testing.
 */
public class TestEdgeView implements EdgeView {

	int rootGraphIndex;

	public TestEdgeView() {
		this(1);
	}

	public TestEdgeView(int index) {
		rootGraphIndex = 1;
	}

	public GraphView getGraphView() {
		return null;
	}

	public Edge getEdge() {
		return null;
	}

	public int getGraphPerspectiveIndex() {
		return rootGraphIndex;
	}

	public int getRootGraphIndex() {
		return rootGraphIndex;
	}

	boolean selected;
	public void select() {
		selected = true;
	}

	public void unselect() {
		selected = false;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean getSelected() {
		return selected;
	}

	public boolean setSelected(boolean s) {
		selected = s;

		return selected;
	}

	String toolTip;
	public void setToolTip(String tip) {
		toolTip = tip;
	}

	public String getToolTip() {
		return toolTip;
	} 

	double label_offset_x;
	double label_offset_y;
	int edge_label_anchor;
	public void setLabelOffsetX(double x) {
		label_offset_x = x;
	}

	public double getLabelOffsetX() {
		return label_offset_x;
	}

	public void setLabelOffsetY(double y) {
		label_offset_y = y;
	}

	public double getLabelOffsetY() {
		return label_offset_y;
	}

	public void setEdgeLabelAnchor(int position) {
		edge_label_anchor = position;
	}

	public int getEdgeLabelAnchor() {
		return edge_label_anchor;
	}

	double label_width;
	public double getLabelWidth() {
		return label_width;
	}

	public void setLabelWidth(double w) {
		label_width = w;
	}

	float strokeWidth;
	public void setStrokeWidth ( float width ) {
		strokeWidth = width;
	}
	public float getStrokeWidth () {
		return strokeWidth;
	}
	Stroke stroke;
	public void setStroke ( Stroke stroke ) { 
		this.stroke = stroke;
	}
	public Stroke getStroke () {
		return stroke;
	}
	int line_type;
	public void setLineType ( int line_type ) {
		this.line_type = line_type;
	}
	public int getLineType () {
		return line_type;
	}
	Paint unselectedPaint;
	public Paint getUnselectedPaint () {
		return unselectedPaint;
	}
	Paint selectedPaint;
	public Paint getSelectedPaint () {
		return selectedPaint;
	}
	Paint srcEdgeEndPaint;
	public Paint getSourceEdgeEndPaint () {
		return srcEdgeEndPaint;
	}
	Paint srcEdgeEndSelectedPaint;
	public Paint getSourceEdgeEndSelectedPaint () {
		return srcEdgeEndSelectedPaint;
	}
	Paint trgEdgeEndPaint;
	public Paint getTargetEdgeEndPaint () {
		return trgEdgeEndPaint;
	}
	Paint trgEdgeEndSelectedPaint;
	public Paint getTargetEdgeEndSelectedPaint () {
		return trgEdgeEndSelectedPaint;
	}
	public void setSelectedPaint ( Paint paint ) {
		selectedPaint = paint;
	}
	public void setUnselectedPaint ( Paint paint ) {
		unselectedPaint = paint;
	}
	public void setSourceEdgeEndSelectedPaint ( Paint paint ) {
		srcEdgeEndSelectedPaint = paint;
	}
	public void setTargetEdgeEndSelectedPaint ( Paint paint ) {
		trgEdgeEndSelectedPaint = paint;
	}
	public void setSourceEdgeEndStrokePaint ( Paint paint ) {
		srcEdgeEndPaint = paint;
	}
	public void setTargetEdgeEndStrokePaint ( Paint paint ) {
		trgEdgeEndPaint = paint;
	}
	public void setSourceEdgeEndPaint ( Paint paint ) {
		srcEdgeEndPaint= paint;
	}
	public void setTargetEdgeEndPaint ( Paint paint ) {
		trgEdgeEndPaint = paint;
	}

	public void updateEdgeView () {}
	public void updateTargetArrow () {}
	public void updateSourceArrow () {}
	int srcEdgeEnd;
	public void setSourceEdgeEnd(int type) {
		srcEdgeEnd = type;
	}
	int trgEdgeEnd;
	public void setTargetEdgeEnd(int type) {
		trgEdgeEnd = type;
	}
	public int getSourceEdgeEnd() { 
		return srcEdgeEnd;
	}
	public int getTargetEdgeEnd() {
		return trgEdgeEnd;
	}
	public void updateLine() {}
	public void drawSelected() {}
	public void drawUnselected() {}
	public Bend getBend () { return null; }
	public void clearBends () {}
	public Label getLabel() { return null; }
}
