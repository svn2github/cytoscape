
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

import giny.view.Label;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;


/**
 *
 */
public class TestLabel implements Label {
	int position = 0;
	Paint textPaint = Color.RED;
	double greekThreshold = 0.0;
	String text = "";
	Font font = new Font("plain", Font.PLAIN, 10);
	int textAnchor = 0;
	int justify = 0;

	/**
	 * Creates a new TestLabel object.
	 */
	public TestLabel() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param p DOCUMENT ME!
	 */
	public void setPositionHint(int p) {
		position = p;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Paint getTextPaint() {
		return textPaint;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param tp DOCUMENT ME!
	 */
	public void setTextPaint(Paint tp) {
		textPaint = tp;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getGreekThreshold() {
		return greekThreshold;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 */
	public void setGreekThreshold(double t) {
		greekThreshold = t;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getText() {
		return text;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 */
	public void setText(String t) {
		text = t;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Font getFont() {
		return font;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param f DOCUMENT ME!
	 */
	public void setFont(Font f) {
		font = f;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param p DOCUMENT ME!
	 */
	public void setTextAnchor(int p) {
		textAnchor = p;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param j DOCUMENT ME!
	 */
	public void setJustify(int j) {
		justify = j;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getTextAnchor() {
		return textAnchor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getJustify() {
		return justify;
	}
}
