/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: ScatterView.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:49 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview.plugin.scatterview;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;

/**
 * a class that makes scatter plots given an SPDataSource
 */
class ScatterView extends ModelView  {
		private JScrollPane scrollPane;
		private ScatterColorSet defaultColorSet;
		/** Setter for defaultColorSet */
		public void setDefaultColorSet(ScatterColorSet defaultColorSet) {
			this.defaultColorSet = defaultColorSet;
		}
		/** Getter for defaultColorSet */
		public ScatterColorSet getDefaultColorSet() {
			return defaultColorSet;
		}

	/**
	 *  resets the Colors to the default...
	 */
	public void setDefaultColors() {
//		System.out.println("setDefaultColors called");
		colorSet.copyStateFrom(defaultColorSet);
		repaint();
	}
		
		
	private final ScatterColorSet colorSet = new ScatterColorSet("ScatterColorSet");
	/** Setter for colorSet */
	public void setColorSet(ScatterColorSet colorSet) {
		this.colorSet.copyStateFrom(colorSet);
	}
	/** Getter for colorSet */
	public ScatterColorSet getColorSet() {
		return colorSet;
	}

	private ConfigNode configNode = new DummyConfigNode("DefaultScatterView");
	/** Setter for configNode 
	* NOTE: This method will likely cause the AxisInfo, and possibly
	* the ColorSet to be changed.
	*
	*/
	public void setConfigNode(ConfigNode configNode) {
		this.configNode = configNode;
		if (configNode.fetchFirst("ScatterColorSet") == null) {
		  getColorSet().bindConfig(getFirst("ScatterColorSet"));
		  setDefaultColors();
		} else {
		  getColorSet().bindConfig(getFirst("ScatterColorSet"));
		}
		  setupAxisInfo();
	}
	/** Getter for configNode */
	public ConfigNode getConfigNode() {
		return configNode;
	}
	

	/**
	* Array with names of possible drawing orders. 
	* MAKE SURE THE CONSTANTS MATCH THE ORDER OF THE NAMES.
	*/
	public static final String [] drawStrings = new String [] {"Selected Last", "Selected First", "Row Order"};

	public static final int SELECTED_LAST = 0;
	public static final int SELECTED_FIRST = 1;
	public static final int ROW_ORDER = 2;
	private int defaultDrawSize = 3;
	/** Setter for drawSize */
	public void setDrawSize(int drawSize) {
		if (getDrawSize() == drawSize) return;
		offscreenValid = false;
		configNode.setAttribute("drawSize", drawSize, defaultDrawSize);
	}
	/** Getter for drawSize */
	public int getDrawSize() {
		return configNode.getAttribute("drawSize", defaultDrawSize);
	}
	
	private int defaultDrawOrder = 0;
	/** Setter for drawOrder */
	public void setDrawOrder(int drawOrder) {
		if (getDrawOrder() == drawOrder) return;
		offscreenValid = false;
		configNode.setAttribute("drawOrder", drawOrder, defaultDrawOrder);
	}
	/** Getter for drawOrder */
	public int getDrawOrder() {
		return configNode.getAttribute("drawOrder", defaultDrawOrder);
	}
	
	
	SPDataSource dataSource;
	double xMin, xMax, yMin, yMax;
	public final static void main(String [] argv) {
		SPDataSource data = new DummySource(271, 137);
		ScatterView sp = new ScatterView(data);
		
		JFrame top = new JFrame("ScatterView Test");
		top.getContentPane().add(sp.getComponent());
		sp.setToolTipText("test sp");
		sp.getComponent().setToolTipText("This Turns Tooltips On");
		top.setSize(500,500);
		top.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent)
			{
				System.exit(0);
			}		
		});
		
		top.show();
		
	}
    private JLabel position;
	public ScatterView() {
		this( new DummySource(271, 137));
		setToolTipText("This Turns Tooltips On");
	}
	public ScatterView(SPDataSource spd) {
		this(spd, new DummyConfigNode("ScatterViewDummy"));
		setToolTipText("This Turns Tooltips On");
	}
	public ScatterView(SPDataSource spd, ConfigNode configNode) {
		setLayout((LayoutManager) null); //no layout manager
		setConfigNode(configNode);
		setToolTipText("This Turns Tooltips On");
		dataSource = spd;
		int n = dataSource.getNumPoints();
		// need to actually use these guys...
		setupAxisInfo();
		
		// must find range of data...
		initializeMinMax();
		for (int i = 1; i < n; i++) { // this loop finds range...
			try {
				double x = dataSource.getX(i);
				double y = dataSource.getY(i);
				if (x < xMin) xMin = x;
				if (x > xMax) xMax = x;
				if (y < yMin) yMin = y;
				if (y > yMax) yMax = y;
			} catch (NoValueException e) {
			}
		}


	  // set a default defaultColorSet... should be superceded by a user setting...
	  defaultColorSet = new ScatterColorSet();
	  setDefaultColorSet(defaultColorSet);
	  setDefaultColors();

		position = new JLabel(dataSource.getTitle());
		position.setToolTipText("Test postion");
		add(position);
		EventTracker eventTracker  = new EventTracker();
		addMouseListener(eventTracker);
		addMouseMotionListener(eventTracker);
		addKeyListener(eventTracker);
		scrollPane = new JScrollPane(this);
		panel = scrollPane;
		panel.setToolTipText("Test panel");
		
	}

	/**
	* makes the current axis info objects consistent with the confignode
	*/
	private void setupAxisInfo() {
		AxisInfo oldX = getXAxisInfo();
		AxisInfo oldY = getYAxisInfo();
	
		xAxisInfo = null;
		yAxisInfo = null;

		ConfigNode [] nodes = configNode.fetch("AxisInfo");
		for (int i =0; i < nodes.length; i++) {
			AxisInfo temp = new AxisInfo(nodes[i]);
			if (temp.getType().equals("x")) {
				setXAxisInfo(temp);
			}
			if (temp.getType().equals("y")) {
				setYAxisInfo(temp);
			}
		}

		if (getXAxisInfo() == null) {
			ConfigNode newNode = configNode.create("AxisInfo");
			AxisInfo temp = new AxisInfo(newNode);
			if (oldX != null) {
				temp.copyStateFrom(oldX);
			} else {
				temp.setType("x");
			}
			setXAxisInfo(temp);
		}
		if (getYAxisInfo() == null) {
			AxisInfo temp = new AxisInfo(configNode.create("AxisInfo"));
			if (oldY != null) {
				temp.copyStateFrom(oldY);
			} else {
				temp.setType("y");
			}
			setYAxisInfo(temp);
		}
		if (dataSource != null) {
			getXAxisInfo().setTitle(dataSource.getXLabel());
			getYAxisInfo().setTitle(dataSource.getYLabel());
		}
	}
	
	
	
    public void initializeMinMax() {
	int n = dataSource.getNumPoints();
	for (int i = 0;i < n; i++) {
	    try {
		xMin = dataSource.getX(0);
		xMax = xMin;
		yMin = dataSource.getY(0);
		yMax = yMin;
		return;
	    } catch (NoValueException e) {
	    }
	}
    }
	/* Zooming stuff */
	public Dimension getPreferredSize() {
		Dimension preferredSize =	super.getPreferredSize();
		/*
		if (getXAxisInfo().getAttribute(AxisParameter.PIXELS) == true) {
			preferredSize.width = getXAxisInfo().getAttribute(AxisParameter.PIXELS).getValue();
		}
		if (getYAxisInfo().getAttribute(AxisParameter.PIXELS) == true) {
			preferredSize.width = getYAxisInfo().getAttribute(AxisParameter.PIXELS).getValue();
		}
*/
//		System.out.println("returning preferred size " + preferredSize);
		return preferredSize;
	}
	private boolean justZoomed = false;
	private Point zoomPoint = new Point();
	private void zoomRectangle(Rectangle r) {
	  JViewport viewport = scrollPane.getViewport();
	  Dimension visible = viewport.getExtentSize();
	  // calculate scale factors...
	  double sx = (double) visible.width / r.width;
	  double sy = (double) visible.height / r.height;

	  /*
	  Dimension scrollPaneSize = scrollPane.getSize();
	  Dimension viewportSize = viewport.getSize();
	  Dimension viewSize = viewport.getViewSize();
	  System.out.println("scrollPaneSize " + scrollPaneSize);
	  System.out.println("viewPort " + viewport);
	  System.out.println("viewportSize " + viewportSize);
	  System.out.println("extentSize " + visible);
	  System.out.println("viewSize " + viewSize);
	  System.out.println("zoomto " + r);
	  */
//	  System.out.println("was " + getPreferredSize());
		Dimension dim = new Dimension();
		dim.width  = (int) (getWidth()  * sx);
		dim.height = (int) (getHeight() * sy);
		setPreferredSize(dim);
		revalidate();
		zoomPoint.setLocation((int) (sx * r.x), 
		(int) (sy * r.y));
		justZoomed = true;
//		System.out.println("zoomRectangle called");
		scrollPane.repaint();
		/*
	  if (parameterPanel != null) {
		  parameterPanel.getValues();
	  }
	  */
	}
	/**
	* Zoom with the specified factor, keeping the specified point in the same relative place.
	*
	* If the point is null, it keeps the center in the same place.
	*/
	private void zoomFactor(double factor, Point point) {
		JViewport viewport = scrollPane.getViewport();
		Dimension visible = viewport.getExtentSize();
		Point r = viewport.getViewPosition();
		if (point == null) {
			point = new Point(r.x + visible.width/2, r.y + visible.height/2);
		}
		// zooms view out...
		Dimension dim = new Dimension();
		dim.width  = (int) (getWidth()     * factor);
		dim.height = (int) (getHeight()    * factor);
		setPreferredSize(dim);
		setSize(dim);
		invalidate();
		revalidate();
		zoomPoint.setLocation((int) (point.x*factor - (point.x - r.x)), 
							(int) (point.y*factor - (point.y - r.y)));
		justZoomed = true;
//				System.out.println("zoomFocus called");
		scrollPane.repaint();
		/*
		if (parameterPanel != null) {
			parameterPanel.getValues();
		}
		*/
	}

	/* ModelView stuff */
	public String viewName() { return "ScatterView";};
	public void update(java.util.Observable obs ,java.lang.Object obj) {
		LogBuffer.println("ScatterView got update from " + obs);
	}
	
    /** This method is called when the selection is changed.
     */
    public void selectionChanged() {
	  offscreenValid = false;
//		System.out.println("selectionChanged called");
	  repaint();
	}
	private void drawPoint(double x, double y, Graphics g) {
		drawPoint((int)x, (int)y, g);
	}
	private void drawPoint(int xi, int yi, Graphics g) {
		int out = (getDrawSize() - 1) / 2;
		g.drawLine(xi - out, yi, xi+out ,yi);
		g.drawLine(xi, yi-out, xi ,yi+out);
	}
    LinearTransformation xTrans, yTrans;
	AxisInfo xAxisInfo;
	/** Setter for xAxisInfo */
	public void setXAxisInfo(AxisInfo xAxisInfo) {
		this.xAxisInfo = xAxisInfo;
	}
	/** Getter for xAxisInfo */
	public AxisInfo getXAxisInfo() {
		return xAxisInfo;
	}
	
	AxisInfo yAxisInfo;
	/** Setter for yAxisInfo */
	public void setYAxisInfo(AxisInfo yAxisInfo) {
		if (yAxisInfo.getType().equals("y") == false) {
			System.out.println("error! wrong type " + yAxisInfo.getType());
			Exception e = new Exception(); e.printStackTrace();
		}
		this.yAxisInfo = yAxisInfo;
	}
	/** Getter for yAxisInfo */
	public AxisInfo getYAxisInfo() {
		return yAxisInfo;
	}

    public Image ensureCapacity(Image i, Dimension req) {
	if (i == null) {
	    return createImage(req.width, req.height);
	}
	
	int w = i.getWidth(null);
	int h = i.getHeight(null);
	if ((w < req.width) || (h < req.height)) {
	    if (w < req.width) { w = req.width;}
	    if (h < req.height) { h = req.height;}
	    // should I try to free something?
	    Image n = createImage(w, h);
	    n.getGraphics().drawImage(i, 0,0,null);
	    return n;
	} else {
	    return i;
	}
    }
    Dimension offscreenSize = new Dimension();
	/**
	* paints component. I used to use an offscreen buffer for reasons that are no longer valid.
	*/
	public void paintComponent(Graphics g) {
		Dimension newsize = getSize();
		if (newsize == null) { return;}
		
		Dimension reqSize;
		reqSize = newsize;

//	METHOD A: uses an offscreenbuffer.
/*
		if ((offscreenBuffer == null) ||
			(reqSize.width != offscreenSize.width) ||
			(reqSize.height != offscreenSize.height)) {
				offscreenSize.setSize(reqSize);
				offscreenBuffer = ensureCapacity(offscreenBuffer, offscreenSize);
				offscreenValid = false;
				centerPosition();
		}
		if (offscreenValid == false) {
			updateBuffer(offscreenBuffer.getGraphics());
			offscreenValid = true;
		}
		g.drawImage(offscreenBuffer, 0, 0, this);
*/
//	METHOD B: paint directly...

		if ((reqSize.width != offscreenSize.width) ||
			(reqSize.height != offscreenSize.height)) {
				offscreenSize.setSize(reqSize);
				centerPosition();
		}
		 updateBuffer(g);

    }
    public void drawText(Graphics g) {
	    Dimension size = getSize();
	    g.setColor(colorSet.getColor("Axis"));
	    // find ascent...
	    FontMetrics metrics = getFontMetrics(g.getFont());
	    int ascent = metrics.getAscent();
	    
	    // title
	    //String out = dataSource.getTitle();
	    //int length = metrics.stringWidth(out);
	    //	g.drawString(out, (size.width - length) / 2, ascent + ascent/2);
	    
	    // xLabel
	    String out = dataSource.getXLabel();
	    int length = metrics.stringWidth(out);
	    g.drawString(out, (size.width - length) / 2, size.height-ascent/2);
	    
	    // yLabel
	    out = dataSource.getYLabel();
	    length = metrics.stringWidth(out);
	    
	    /* Can't use java2 stuff (damn macs!)
	       g.rotate(-90 * 3.14159/180);
	       g.translate(-size.height, 0);
	       g.drawString(out, (size.height - length) /2, ascent + ascent /2);	
	       g2d.translate(size.height, 0);
	       g2d.rotate(90 * 3.14159/180);
	    */
	    Image temp = createImage(length + ascent/2, ascent + ascent/2);
	    Graphics tg = temp.getGraphics();
	    tg.setColor(colorSet.getColor("Background"));
	    tg.fillRect(0,0,size.width, size.height);
	    tg.setColor(colorSet.getColor("Axis"));
	    tg.drawString(out, ascent/2, ascent);	
	    temp = RotateImageFilter.rotate(this, temp);
	    g.drawImage(temp, 0, (size.height - length) /2, null);
	}
    /**
	* figures out what the right values to store are, and puts them in the confignode...
	*/
	public void recalculateValues() {
		AxisParameter xMinParameter = getXAxisInfo().getAxisParameter(AxisParameter.MIN);
		AxisParameter yMinParameter = getYAxisInfo().getAxisParameter(AxisParameter.MIN);
		AxisParameter xMaxParameter = getXAxisInfo().getAxisParameter(AxisParameter.MAX);
		AxisParameter yMaxParameter = getYAxisInfo().getAxisParameter(AxisParameter.MAX);
		if (xMinParameter.getEnabled() == false) xMinParameter.setValue(xMin);
		if (yMinParameter.getEnabled() == false) yMinParameter.setValue(yMin);
		if (xMaxParameter.getEnabled() == false) xMaxParameter.setValue(xMax);
		if (yMaxParameter.getEnabled() == false) yMaxParameter.setValue(yMax);
	}
    public void updateBuffer(Graphics g) {
	  if (justZoomed) {
		justZoomed = false;
		scrollPane.getViewport().setViewPosition(zoomPoint);
		repaint();
		return;
	  }
	    Dimension size = getSize();
	    FontMetrics metrics = getFontMetrics(g.getFont());
	    int ascent = metrics.getAscent();
	    g.setColor(colorSet.getColor("Background"));
		g.fillRect(0,0,size.width, size.height);
		g.setColor(colorSet.getColor("Axis"));
		drawText(g);
		// This are the actual min vals to plot, not the minimum values in the distribution 
		recalculateValues();
		double minXVal = getXAxisInfo().getAxisParameter(AxisParameter.MIN).getValue();
		double minYVal = getYAxisInfo().getAxisParameter(AxisParameter.MIN).getValue();
		double maxXVal = getXAxisInfo().getAxisParameter(AxisParameter.MAX).getValue();
		double maxYVal = getYAxisInfo().getAxisParameter(AxisParameter.MAX).getValue();
		
		
	    // data points...
	    xTrans = new LinearTransformation
		(minXVal, 3*ascent, maxXVal, size.width - 3*ascent);
	    yTrans = new LinearTransformation
		(minYVal, size.height - 3*ascent, maxYVal, 3*ascent);

	    switch(getDrawOrder()) {
			case ROW_ORDER:
				int n = dataSource.getNumPoints();
				Rectangle clipRect =  g.getClipBounds();
				Point loc = new Point();
				for (int i = 0; i < n; i++) {
					try {
						double x = dataSource.getX(i); double y = dataSource.getY(i);
						loc.setLocation((int) xTrans.transform(x),(int) yTrans.transform(y));
						if (clipRect.contains(loc)) {
							g.setColor(dataSource.getColor(i));
							drawPoint(loc.x, loc.y, g);
						}
					} catch (NoValueException e) {
						//		System.out.println("no value for point " + i);
					}
				}
				break;
			case SELECTED_FIRST:
				drawSelected(g, xTrans, yTrans);
				drawNonselected(g, xTrans, yTrans);
				break;
			case SELECTED_LAST:
				drawNonselected(g, xTrans, yTrans);
				drawSelected(g, xTrans, yTrans);
				break;
		}
	    
	    g.setColor(colorSet.getColor("Axis"));
	    // box outline.
	    g.drawRect(ascent*2, ascent*2,
		       size.width - ascent*4, size.height - ascent *4);
			   
	    g.drawString("" + maxYVal, 0, ascent*2);
	    g.drawString("" + minYVal, 0, size.height - ascent);
	    g.drawString("" + minXVal, ascent,            ascent);
		String out = "" + maxXVal;
		int width = metrics.stringWidth(out);
	    g.drawString(out, size.width-ascent-width, ascent);
    }
	public void drawSelected(Graphics g, LinearTransformation xTrans, LinearTransformation yTrans) {
	    int n = dataSource.getNumPoints();
		Rectangle clipRect =  g.getClipBounds();
		Point loc = new Point();
		for (int i = 0; i < n; i++) {
			if (dataSource.isSelected(i) == false) continue;
			try {
				double x = dataSource.getX(i); double y = dataSource.getY(i);
				loc.setLocation((int)xTrans.transform(x),(int)yTrans.transform(y));
				if (clipRect.contains(loc)) {
					g.setColor(dataSource.getColor(i));
					drawPoint(loc.x, loc.y, g);
				}
			} catch (NoValueException e) {
				//		System.out.println("no value for point " + i);
			}
		}
	}
	
	public void drawNonselected(Graphics g, LinearTransformation xTrans, LinearTransformation yTrans) {
	    int n = dataSource.getNumPoints();
		Rectangle clipRect =  g.getClipBounds();
		Point loc = new Point();
		for (int i = 0; i < n; i++) {
			if (dataSource.isSelected(i) == true) continue;
			try {
				double x = dataSource.getX(i); double y = dataSource.getY(i);
				loc.setLocation((int)xTrans.transform(x),(int)yTrans.transform(y));
				if (clipRect.contains(loc)) {
					g.setColor(dataSource.getColor(i));
					drawPoint(loc.x, loc.y, g);
				}
			} catch (NoValueException e) {
				//		System.out.println("no value for point " + i);
			}
		}
	}
	/**
	* returns index into datasource of closest point to requested pixel location
	*/
	public void calculateClosest(Point pixelLocation) {
		    int n = dataSource.getNumPoints();
		Point loc = new Point();
		closestPoint = -1;
		for (int i = 0; i < n; i++) {
			try {
				double x = dataSource.getX(i); double y = dataSource.getY(i);
				loc.setLocation((int)xTrans.transform(x),(int)yTrans.transform(y));
				int dx = loc.x - pixelLocation.x;
				int dy = loc.y - pixelLocation.y;
				int dist = dx*dx + dy*dy;
				if ((dist < closestDist2) || (closestPoint == -1)) {
					closestPoint = i;
					closestDist2 = dist;
				}
			} catch (NoValueException e) {
				//		System.out.println("no value for point " + i);
			}
		}
	}
	int closestPoint = -1;
	int closestDist2 = 1000000;
	int threshold2 = 10*10;
	public String getToolTipText(MouseEvent event) {
		calculateClosest(startPoint);
		// early return with closest threshold if matches..
		if (closestDist2 < threshold2) {
			try {
			double x = dataSource.getX(closestPoint);
			double y = dataSource.getY(closestPoint);
			String label = dataSource.getLabel(closestPoint);
			return (label + "\n  (" + x +", "+ y + ")");
			} catch (NoValueException e) {
				//		System.out.println("no value for point " + i);
			}
		}
		double x = xTrans.inverseTransform(event.getX());
		double y = yTrans.inverseTransform(event.getY());
		// position.setText(dataSource.getXLabel() + " = " + x +", " + dataSource.getYLabel() + " = "+ y);
		return("X = " + x +", " + "Y = "+ y);
//		return getToolTipText();
	}
	public Point getToolTipLocation(MouseEvent event) {
		if (closestDist2 < threshold2) {
			try {
			double x = dataSource.getX(closestPoint);
			double y = dataSource.getY(closestPoint);
			Point loc = new Point();
			loc.setLocation((int)xTrans.transform(x),(int)yTrans.transform(y));
			return loc;
			} catch (NoValueException e) {
				//		System.out.println("no value for point " + i);
			}
		}
		return null;
	}

	private void centerPosition() {
		position.revalidate();
		Dimension mysize = position.getPreferredSize();
		position.setSize(mysize);
		Dimension size = getSize();


		JViewport viewport = scrollPane.getViewport();
		Dimension visible = viewport.getExtentSize();
		Point r = viewport.getViewPosition();
		position.setLocation((int)(size.width - mysize.width)/2, (int)0);
		position.setLocation((int)r.x + (visible.width - mysize.width)/2,(int) 0);
		position.setForeground(colorSet.getColor("Axis"));
		position.repaint();
	}
	
	

	class EventTracker implements MouseMotionListener, MouseListener, KeyListener, ComponentListener {
		/* mouse listener */
	private boolean dragging = false;
    // Mouse Listener 
	public void mousePressed(MouseEvent e) {
		startPoint.setLocation((int)e.getX(), (int)e.getY());
		endPoint.setLocation((int)startPoint.x, (int)startPoint.y);
		calcRect.setLocation((int)startPoint.x, (int)startPoint.y);
		calcRect.setSize(0,0);
		dragRect.setBounds(calcRect);
		dragRect.setToolTipText("Test dragRect");

		add(dragRect);
//		System.out.println("mousepressed called");
		dragRect.repaint();
		dragging = true;
	}
	public void mouseReleased(MouseEvent e) {
		dragging = false;
		mouseDragged(e);
		remove(dragRect);
		if (xTrans == null) {
			return;
		}
		
		// take some action...
		if (!e.isControlDown()) {
			dataSource.deselectAll();
		} 
		
		double xL = xTrans.inverseTransform(calcRect.x);
		double xU = xTrans.inverseTransform(calcRect.x + calcRect.width);
		
		double yU = yTrans.inverseTransform(calcRect.y);
		double yL = yTrans.inverseTransform(calcRect.y + calcRect.height);
		
		dataSource.select(xL, yL, xU, yU);
		
		offscreenValid = false;
//		System.out.println("mouse released called");
		repaint();
	}
		
		
	    /* container listener */
	public void componentResized(ComponentEvent e) {
//		debug("componentResized", e);
		centerPosition();
    }

    public void componentMoved(ComponentEvent e) {
//	debug("componentMoved", e);
    }

    public void componentShown(ComponentEvent e) {
//	debug("componentShown", e);
    }

    public void componentHidden(ComponentEvent e) {
//	debug("componentHidden", e);
    }


		/* key listener */
		public void keyPressed (KeyEvent e) {
				// Invoked when a key has been pressed. 
		}
		public void keyReleased (KeyEvent e) {
				//ÊÊInvoked when a key has been released. 
		}
		public void keyTyped (KeyEvent e) {
				//ÊÊInvoked when a key has been typed
			switch (e.getKeyChar()) {
				case '-':
					zoomFactor(0.5, startPoint);
					startPoint.x = (int)(startPoint.x * 0.5);
					startPoint.y = (int)(startPoint.y * 0.5);
					mouseMoved(new MouseEvent (ScatterView.this, MouseEvent.MOUSE_MOVED, 10, 0, 
					startPoint.x, startPoint.y, 1, false));
					break;
				case '+':
					zoomFactor(2.0, startPoint);
					startPoint.x = (int)(startPoint.x * 2.0);
					startPoint.y = (int)(startPoint.y * 2.0);
					mouseMoved(new MouseEvent (ScatterView.this, MouseEvent.MOUSE_MOVED, 10, 0, 
					startPoint.x, startPoint.y, 1, false));
					break;
			}
		}


    // MouseMotionListener
	
	public void mouseDragged(MouseEvent e) {
		// rubber band?
		//	drawBand(dragRect.x, dragRect.y, dragRect.width, dragRect.height);
		endPoint.setLocation((int)e.getX(), (int)e.getY());
		
		calcRect.setLocation((int)startPoint.x, (int)startPoint.y);
		calcRect.setSize(0,0);
		calcRect.add(endPoint.x, endPoint.y);
		if (e.isControlDown()) {
			dragRect.setColor(Color.white);
		} else {
			dragRect.setColor(Color.yellow);
		}
		
		dragRect.setBounds(calcRect);
    }
    public void mouseClicked(java.awt.event.MouseEvent e) {
    }
    public void mouseEntered(java.awt.event.MouseEvent e) {	
		requestFocus();
    }
	public void mouseExited(java.awt.event.MouseEvent e) {
		position.setText(dataSource.getTitle());
		centerPosition();
    }

	public void mouseMoved(java.awt.event.MouseEvent e) {
		if (xTrans == null) return;
		if (yTrans == null) return;
		if (dragging) {
			mouseDragged(e);
		} else {
			startPoint.setLocation((int)e.getX(), (int)e.getY());
		}
		position.setText(getToolTipText(e));
		centerPosition();
    }

	}
	
	public void trackMouse(java.awt.event.MouseEvent e) {
		position.revalidate();
		Dimension mySize = position.getPreferredSize();
		position.setSize(mySize);
		Dimension size = getSize();
		int tx = e.getX();
		if (e.getX() > size.width /2) {
			tx = e.getX() - mySize.width;
		}
		int ty = e.getY();;
		if (e.getY() > size.height /2) {
			ty = e.getY() - mySize.height;
		}
		position.setLocation((int)tx, (int)ty);
//		System.out.println("trackmouse called");
		position.repaint();
	}
   
   /** may have so many data points I'm better off buffering...
   * OTOH, perhaps I would be even better off if I had a smart way to keep
   * track of exactly when needs to be repainted... oh well!
   */
    protected Image        offscreenBuffer = null;
	protected boolean      offscreenValid    = false;
	/** Setter for offscreenValid */
	public void setOffscreenValid(boolean offscreenValid) {
		this.offscreenValid = offscreenValid;
	}
	/** Getter for offscreenValid */
	public boolean getOffscreenValid() {
		return offscreenValid;
	}

    /**
     * This rectangle keeps track of where the drag rect was drawn
     */

    private DragRect dragRect = new DragRect();
    private Rectangle calcRect = new Rectangle();
    private Point startPoint = new Point();
    private Point endPoint = new Point();

		  	/**
	* always returns an instance of the node, even if it has to create it.
	*/
	private ConfigNode getFirst(String name) {
		ConfigNode cand = getConfigNode().fetchFirst(name);
		return (cand == null)? getConfigNode().create(name) : cand;
	}
}

class DragRect extends JComponent {
	private Color color = Color.white;
	/** Setter for color */
	public void setColor(Color color) {
		this.color = color;
	}
	/** Getter for color */
	public Color getColor() {
		return color;
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(getColor());
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
    }

	public DragRect() {
		setToolTipText("Test dragRect");
	}
}


class DummySource implements SPDataSource {
    public Color foreColor = Color.green;
    public Color selColor = Color.red;

    int xmod, ymod;
    boolean selected[];
    DummySource(int x, int y) {
	xmod = x;
	ymod = y;
	selected = new boolean[10000];
    }

    public int getNumPoints() {
	return selected.length;
    }
    public double getX(int i) throws NoValueException {
	return i % xmod;
    }
    public double getY(int i) throws NoValueException {
	return i % ymod;
    }
	public String getLabel(int i) {
		return "Dummy " + i;
	}
    public java.awt.Color getColor(int i) {
	if (selected[i]) {	
	    return selColor;
	} else {
	    return foreColor;
	}
    }
    public String getTitle() {
	return "Modulo Fun!";
    }
    public String getXLabel() {
	return "Index mod " + xmod;
    }
    public String getYLabel() {
	return "Index mod " + ymod;
    }
	public boolean isSelected(int i) {
		return selected[i];
	}
    public void select(int i) {
	try {
	    System.out.println("Selected point " + i + " at (" + getX(i) + ", " + getY(i) + ")");
	} catch (NoValueException e) {
	}
	selected[i] = true;
    }

		public void select(double xL, double yL, double xU, double yU) {
		int n = getNumPoints();
		for (int i = 0; i < n; i++) {
			try {
				double x = getX(i);
				double y = getY(i);
				if ((x > xL) && (x < xU) &&
					(y > yL) && (y < yU))
					select(i);
			} catch (NoValueException ex) {
			}
		}
	}

	
    public void deselectAll() {
	for (int i = 0; i < selected.length; i++) {
	    selected[i] = false;
	}
    }
}
