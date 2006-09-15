<<<<<<< .working
/*
  File: InternalFrameComponent.java
  
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

package cytoscape.view;

// imports
import ding.view.DGraphView;
import ding.view.DingCanvas;

import java.awt.Color;
import java.awt.Paint;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 * This class manages the JLayeredPane that resides in
 * each internal frame of cytoscape.  Its intended to be the
 * class which encapsulates the multiple canvases that are created 
 * by the DGraphView class.
 */
public class InternalFrameComponent extends JComponent {

	/**
	 * alpha setting enumeration
	 */
	private static enum AlphaSetting { OPAQUE, TRANSLUCENT }

	/**
	 * z-order enumeration
	 */
	private static enum ZOrder {
		BACKGROUND_PANE, NETWORK_PANE, FOREGROUND_PANE;
		int layer() {
			if (this==BACKGROUND_PANE) return -30000;
			if (this==NETWORK_PANE) return 0;
			if (this==FOREGROUND_PANE) return 301;
			return 0;
		}
	}

	/**
	 * ref to the JInternalFrame's JLayeredPane
	 */
	private JLayeredPane layeredPane;

	/**
	 * ref to DGraphView that contains the set of inner canvas's we manage
	 */
	private DGraphView dGraphView;

	/**
	 * ref to background canvas
	 */
	private DingCanvas backgroundCanvas;

	/**
	 * ref to network canvas
	 */
	private DingCanvas networkCanvas;

	/**
	 * ref to foreground canvas
	 */
	private DingCanvas foregroundCanvas;

	/**
	 * ref to active canvas
	 */
	private DingCanvas activeCanvas;

	/**
	 * Constructor.
	 *
	 * @param layeredPane JLayedPane
	 * @param dGraphView dGraphView
	 */
    public InternalFrameComponent(JLayeredPane layeredPane, DGraphView dGraphView) {

		// init members
		this.layeredPane = layeredPane;
		this.dGraphView = dGraphView;
		this.backgroundCanvas = dGraphView.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
		this.networkCanvas = dGraphView.getCanvas(DGraphView.Canvas.NETWORK_CANVAS);
		this.foregroundCanvas = dGraphView.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);

        // set default ordering
		defaultOrder();
    }

	/**
	 * Sets the default zorder of the canvases.
	 * top - bottom: foreground, network, background
	 */
	public void defaultOrder() {

		// remove all canvases from layered pane
		layeredPane.removeAll();

		// foreground followed by network followed by background
		placeOnPane(foregroundCanvas, ZOrder.FOREGROUND_PANE.layer(), AlphaSetting.TRANSLUCENT);
		placeOnPane(networkCanvas, ZOrder.NETWORK_PANE.layer(), AlphaSetting.TRANSLUCENT);
		placeOnPane(backgroundCanvas, ZOrder.BACKGROUND_PANE.layer(), AlphaSetting.OPAQUE);
		activeCanvas = foregroundCanvas;
	}

	/**
	 * Brings the desired canvas to the top of the layer.
	 * Uses canned rules:
	 *
	 * if canvas is foreground, show default ordering
	 * if canvas is network, show network and background (network background is translucent)
	 * if canvas is background, only background is displayed
	 *
	 */
	public void bringToTop(DGraphView.Canvas canvas) {

		// remove all canvases from layered pane
		layeredPane.removeAll();

		// determine top canvas
		if (canvas == DGraphView.Canvas.BACKGROUND_CANVAS) {
			// only display background
			placeOnPane(backgroundCanvas, ZOrder.BACKGROUND_PANE.layer(), AlphaSetting.OPAQUE);
			activeCanvas = backgroundCanvas;
		}
		else if (canvas == DGraphView.Canvas.NETWORK_CANVAS) {
			// network followed by background
			placeOnPane(networkCanvas, ZOrder.NETWORK_PANE.layer(), AlphaSetting.TRANSLUCENT);
			placeOnPane(backgroundCanvas, ZOrder.BACKGROUND_PANE.layer(), AlphaSetting.OPAQUE);
			activeCanvas = networkCanvas;
		}
		else if (canvas == DGraphView.Canvas.FOREGROUND_CANVAS) {
			// foreground followed by network followed by background
			defaultOrder();
		}
	}

	/**
	 * We implementation reshape to propagate the event down to the inner canvases.
	 */
	public void reshape(int x, int y, int width, int height) {

		// call reshape on each innercanvas
		backgroundCanvas.reshape(x, y, width, height);
		networkCanvas.reshape(x, y, width, height);
		foregroundCanvas.reshape(x, y, width, height);
	}

	/**
	 * Places the given canvas on the given given pane with the desired alpha setting.
	 *
	 * @param dingCanvas DingCanvas
	 * @param pane int
	 * @param alphaSetting AlphaSetting
	 */
	private void placeOnPane(DingCanvas dingCanvas, int pane, AlphaSetting alphaSetting) {

		// set alpha and place on given pane
		setBackgroundAlpha(dingCanvas, alphaSetting);
		layeredPane.add(dingCanvas, new Integer(pane));
	}

	/**
	 * Sets the alpha channel on the desired canvas.
	 *
	 * @param dingCanvas DingCanvas
	 * @param alphaSetting AlphaSetting
	 */
	private void setBackgroundAlpha(DingCanvas dingCanvas, AlphaSetting alphaSetting) {

		// get the current background color
		Paint backgroundPaint = dingCanvas.getBackgroundPaint();

		if (backgroundPaint instanceof Color) {
			Color currentBackgroundColor = (Color)backgroundPaint;

			// modify its alpha as desired
			int alpha = (alphaSetting == AlphaSetting.OPAQUE) ? 255 : 0;
			Color newBackgroundColor = new Color(currentBackgroundColor.getRed(),
												 currentBackgroundColor.getGreen(),
												 currentBackgroundColor.getBlue(),
												 alpha);

			// set the modified current background color
			dingCanvas.setBackgroundPaint(newBackgroundColor);
		}
	}
}=======
/*
  File: InternalFrameComponent.java
  
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

package cytoscape.view;

// imports
import ding.view.DGraphView;
import ding.view.DingCanvas;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 * This class manages the JLayeredPane that resides in
 * each internal frame of cytoscape.  Its intended to be the
 * class which encapsulates the multiple canvases that are created 
 * by the DGraphView class.
 */
public class InternalFrameComponent extends JComponent {

	/**
	 * alpha setting enumeration
	 */
	private static enum AlphaSetting { OPAQUE, TRANSLUCENT }

	/**
	 * z-order enumeration
	 */
	private static enum ZOrder {
		BACKGROUND_PANE, NETWORK_PANE, FOREGROUND_PANE;
		int layer() {
			if (this==BACKGROUND_PANE) return -30000;
			if (this==NETWORK_PANE) return 0;
			if (this==FOREGROUND_PANE) return 301;
			return 0;
		}
	}

	/**
	 * ref to the JInternalFrame's JLayeredPane
	 */
	private JLayeredPane layeredPane;

	/**
	 * ref to DGraphView that contains the set of inner canvas's we manage
	 */
	private DGraphView dGraphView;

	/**
	 * ref to background canvas
	 */
	private DingCanvas backgroundCanvas;

	/**
	 * ref to network canvas
	 */
	private DingCanvas networkCanvas;

	/**
	 * ref to foreground canvas
	 */
	private DingCanvas foregroundCanvas;

	/**
	 * ref to active canvas
	 */
	private DingCanvas activeCanvas;

	/**
	 * Constructor.
	 *
	 * @param layeredPane JLayedPane
	 * @param dGraphView dGraphView
	 */
    public InternalFrameComponent(JLayeredPane layeredPane, DGraphView dGraphView) {

		// init members
		this.layeredPane = layeredPane;
		this.dGraphView = dGraphView;
		this.backgroundCanvas = dGraphView.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
		this.networkCanvas = dGraphView.getCanvas(DGraphView.Canvas.NETWORK_CANVAS);
		this.foregroundCanvas = dGraphView.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);

        // set default ordering
		defaultOrder();
    }

	/**
	 * Sets the default zorder of the canvases.
	 * top - bottom: foreground, network, background
	 */
	public void defaultOrder() {

		// remove all canvases from layered pane
		layeredPane.removeAll();

		// foreground followed by network followed by background
		placeOnPane(foregroundCanvas, ZOrder.FOREGROUND_PANE.layer(), AlphaSetting.TRANSLUCENT);
		placeOnPane(networkCanvas, ZOrder.NETWORK_PANE.layer(), AlphaSetting.TRANSLUCENT);
		placeOnPane(backgroundCanvas, ZOrder.BACKGROUND_PANE.layer(), AlphaSetting.OPAQUE);
		activeCanvas = foregroundCanvas;
	}

	/**
	 * Brings the desired canvas to the top of the layer.
	 * Uses canned rules:
	 *
	 * if canvas is foreground, show default ordering
	 * if canvas is network, show network and background (network background is translucent)
	 * if canvas is background, only background is displayed
	 *
	 */
	public void bringToTop(DGraphView.Canvas canvas) {

		// remove all canvases from layered pane
		layeredPane.removeAll();

		// determine top canvas
		if (canvas == DGraphView.Canvas.BACKGROUND_CANVAS) {
			// only display background
			placeOnPane(backgroundCanvas, ZOrder.BACKGROUND_PANE.layer(), AlphaSetting.OPAQUE);
			activeCanvas = backgroundCanvas;
		}
		else if (canvas == DGraphView.Canvas.NETWORK_CANVAS) {
			// network followed by background
			placeOnPane(networkCanvas, ZOrder.NETWORK_PANE.layer(), AlphaSetting.TRANSLUCENT);
			placeOnPane(backgroundCanvas, ZOrder.BACKGROUND_PANE.layer(), AlphaSetting.OPAQUE);
			activeCanvas = networkCanvas;
		}
		else if (canvas == DGraphView.Canvas.FOREGROUND_CANVAS) {
			// foreground followed by network followed by background
			defaultOrder();
		}
	}

	/**
     * Our implementation of JComponent setBounds().
	 * We implementation setBounds to propagate the event down to the DingCanvases.
	 */
	public void setBounds(int x, int y, int width, int height) {

		// call reshape on each innercanvas
		backgroundCanvas.setBounds(x, y, width, height);
		networkCanvas.setBounds(x, y, width, height);
		foregroundCanvas.setBounds(x, y, width, height);
	}

	/**
	 * Places the given canvas on the given given pane with the desired alpha setting.
	 *
	 * @param dingCanvas DingCanvas
	 * @param pane int
	 * @param alphaSetting AlphaSetting
	 */
	private void placeOnPane(DingCanvas dingCanvas, int pane, AlphaSetting alphaSetting) {

		// set alpha and place on given pane
		setBackgroundAlpha(dingCanvas, alphaSetting);
		layeredPane.add(dingCanvas, new Integer(pane));
	}

	/**
	 * Sets the alpha channel on the desired canvas.
	 *
	 * @param dingCanvas DingCanvas
	 * @param alphaSetting AlphaSetting
	 */
	private void setBackgroundAlpha(DingCanvas dingCanvas, AlphaSetting alphaSetting) {

		// get the current background color
		Color backgroundColor = dingCanvas.getBackground();

		// modify its alpha as desired
		int alpha = (alphaSetting == AlphaSetting.OPAQUE) ? 255 : 0;
		Color newBackgroundColor = new Color(backgroundColor.getRed(),
											 backgroundColor.getGreen(),
											 backgroundColor.getBlue(),
											 alpha);

		// set the modified current background color
		dingCanvas.setBackground(newBackgroundColor);
	}
}>>>>>>> .merge-right.r8215
