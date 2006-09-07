
/*
  File: CytoscapeCanvas.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
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

package ding.view;

// imports
import java.awt.Image;
import java.awt.Paint;
import javax.swing.JComponent;

/**
 * This class is meant to be extended by a class which 
 * is meant to exist within the InternalFrameComponent class.
 * It provides the services required to draw onto it.
 *
 * Currently (9/7/06), two classes will extend CytoscapeCanves, ding.view.InnerCanvas
 * and ding.view.ArbitraryGraphicsCanvas.
 */
public abstract class DingCanvas extends JComponent {

	/**
	 * ref to image we maintain
	 */
	protected Image m_img;

	/**
	 * ref to our background paint
	 */
	protected Paint m_bgPaint;

	/**
	 * Returns the image we maintain.
	 *
	 * @return Image
	 */
	public Image getImage() {
		return m_img;
	}

	/**
	 * Returns the current set background paint.
	 *
	 * @return Paint
	 */
	public Paint getBackgroundPaint() {
		return m_bgPaint;
	}

	/**
	 * Sets our background paint.
	 *
	 * backgroundPaint Paint
	 */
	public void setBackgroundPaint(Paint backgroundPaint) {
		m_bgPaint = backgroundPaint;
	}
}