/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: KaryoView.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:50 $
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
package edu.stanford.genetics.treeview.plugin.karyoview;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;

import edu.stanford.genetics.treeview.*;


/**
 * This class is a model view that displays the karyoscope view for a
 * single experiment in the dataset.
 *
 * There is really quite a lot of complexity involved. I have decided to factor
 * out all knowledge of where things are positioned into the KaryoDrawer class.
 *
 * I have retained knowledge of averaging in this class, as well as most of the 
 * component mechanics. This is to facilitate the use of KaryoDrawer for drawing
 * to images with configurable parameters.
 */

public class KaryoView extends ModelView implements Observer {
    
	private KaryoViewParameterPanel parameterPanel = null;
	public KaryoViewParameterPanel getParameterPanel(){return parameterPanel;}
	public void setParameterPanel(KaryoViewParameterPanel p) { parameterPanel = p;}
	
    /**
     * current experiment column to view 
     */
    private int currentCol = 0;
    public int getCurrentCol() { return currentCol;}
    public void setCurrentCol(int n) { 
		if (currentCol == n) return;
		currentCol = n;
		recalculateAverages();
		offscreenValid = false;
	}

	private KaryoDrawer karyoDrawer = null;
	/** Setter for karyoDrawer */
	public void setKaryoDrawer(KaryoDrawer karyoDrawer) {
		if (this.karyoDrawer != null) {
			this.karyoDrawer.deleteObserver(this);
		}
		this.karyoDrawer = karyoDrawer;
		if (this.karyoDrawer != null) {
			this.karyoDrawer.addObserver(this);
		}
	}
	/** Getter for karyoDrawer */
	public KaryoDrawer getKaryoDrawer() {
		return karyoDrawer;
	}
	
	/* averaging accessors */
	private AveragerSettingsPanel averagerSettingsPanel;
	public SettingsPanel getAveragerSettingsPanel() {
		return averagerSettingsPanel;
	}
	public int getAveragingType() {
		return averager.getType();
	}
	public String getAveragingArg() {
		return averager.getArg();
	}
	private int defaultAverager = Averager.INTERVAL;
	public void setSimpleAveraging() {
		averagerSettingsPanel.setEnabled(false);
		configNode.setAttribute("averager", simpleAverager.getType(), defaultAverager);
		recalculateAverages();
	}
	public void setNearestAveraging(int num) {
		averagerSettingsPanel.setEnabled(false);
		configNode.setAttribute("averager", nearestAverager.getType(), defaultAverager);
		 nearestAverager.setNum(num);
		 recalculateAverages();
	}
	public void setNeighborAveraging(int num) {
		averagerSettingsPanel.setEnabled(false);
		configNode.setAttribute("averager", neighborAverager.getType(), defaultAverager);
		 neighborAverager.setNum(num);
		 recalculateAverages();
	}
	public void setIntervalAveraging(double width) {
		averagerSettingsPanel.setEnabled(false);
		configNode.setAttribute("averager", intervalAverager.getType(), defaultAverager);
		 intervalAverager.setWidth(width);
		 recalculateAverages();
	}
	
	private Averager getAverager(int type) {
		switch(type) {
			case Averager.SIMPLE:
				return simpleAverager;
			case Averager.NEAREST:
				return nearestAverager;
			case Averager.NEIGHBOR:
				return neighborAverager;
			case Averager.INTERVAL:
				return intervalAverager;
		}
		return getAverager(defaultAverager);
	}
	
    /* Some private state variables... */
	private Averager         simpleAverager;
	private NeighborAverager neighborAverager;
	private NearestAverager  nearestAverager;
	private IntervalAverager intervalAverager;
	private JScrollPane scrollPane;
	private DataMatrix dataMatrix;
	/** Setter for dataMatrix */
	public void setDataMatrix(DataMatrix dataMatrix) {
		this.dataMatrix = dataMatrix;
	}
	/** Getter for dataMatrix */
	public DataMatrix getDataMatrix() {
		return dataMatrix;
	}
	/** Getter for numCol */
	public int getNumCol() {
		return dataMatrix.getNumCol();
	}
	private int numRow;
	private double nodata;
	/** Setter for nodata */
	public void setNodata(double nodata) {
		this.nodata = nodata;
	}
	/** Getter for nodata */
	public double getNodata() {
		return nodata;
	}
 private HeaderInfo experimentInfo;
	private HeaderInfo geneInfo;
	/** Setter for geneInfo */
	public void setGeneInfo(HeaderInfo geneInfo) {
		this.geneInfo = geneInfo;
	}
	/** Getter for geneInfo */
	public HeaderInfo getGeneInfo() {
		return geneInfo;
	}

	/**
	* removes any pointers to exteral objects to aid GC
	*/
	public void cleanup() {
		dataMatrix = null;
		scrollPane = null;
		karyoDrawer = null;
		experimentInfo = null;
		geneInfo = null;
	}
	
	public void update(Observable o, Object arg) {
		if (o == karyoDrawer) {
			offscreenValid = false;
			revalidate();
			repaint();
		} else {
			LogBuffer.println("KaryoView got weird update from " + o);
		}
	}
	
    /**
     * for viewing DataModels
     */
	public KaryoView(KaryoDrawer karyoDrawer, DataModel tvmodel) {
		setKaryoDrawer(karyoDrawer);
		dataMatrix = tvmodel.getDataMatrix();
		nodata = DataModel.NODATA;
		experimentInfo = tvmodel.getArrayHeaderInfo();
		geneInfo = tvmodel.getGeneHeaderInfo();
		
		simpleAverager = new Averager();
		simpleAverager.setKaryoView(this);
		nearestAverager = new NearestAverager();
		nearestAverager.setKaryoView(this);
		neighborAverager = new NeighborAverager();
		neighborAverager.setKaryoView(this);
		intervalAverager = new IntervalAverager();
		intervalAverager.setKaryoView(this);
		averagerSettingsPanel = new AveragerSettingsPanel(this);
		averager = simpleAverager;
		averageTimer = new javax.swing.Timer(1000, new TimerListener());
		averageTimer.stop();
		mouseTracker = new MouseTracker();
		statusText[1] = "Mouse over for info on genes...";
		statusText[2] = "";
		statusText[3] = "";
		scrollPane = new JScrollPane(this);
		panel = scrollPane;
		addMouseMotionListener(mouseTracker);
		addMouseListener(mouseTracker);
		addKeyListener(mouseTracker);
	}
	ProgressMonitor averagerMonitor;
	javax.swing.Timer averageTimer;
	AveragerTask averagerTask;
	class TimerListener implements ActionListener { // manages the averagermonitor
		public void actionPerformed(ActionEvent evt) {
			if (averagerMonitor.isCanceled() || averagerTask.done()) {
				averagerMonitor.close();
				averagerTask.stop();
				// Toolkit.getDefaultToolkit().beep();
				averagerSettingsPanel.setEnabled(true);
				averageTimer.stop();
				if (averagerTask.done()) {
					averagerMonitor.setNote("Averaging complete");
				}
			} else {
				averagerMonitor.setNote(averagerTask.getMessage());
				averagerMonitor.setProgress(averagerTask.getCurrent());
			}
			repaint();
		}
	}
	class AveragerTask {
		private int current = 0;
		private String statMessage;
		
		
		/**
		* Called to start the task. I don't know why we bother with the ActualTask class, so don't ask.
		*/
		void go() {
			setCurrent(0);
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					return new ActualTask();
				}
			};
			worker.start();
		}
		
		
		/**
		* Called from ProgressBarDemo to find out how much work needs
		* to be done.
		*/
		int getLengthOfTask() {
			return karyoDrawer.getGenome().getNumLoci();
		}
		
		/**
		* Called from ProgressBarDemo to find out how much has been done.
		*/
		int getCurrent() {
			return current;
		}
		void setCurrent(int i) {
			current = i;
		}
		public void incrCurrent() { 
			current++;
		}
		/**
		* called to stop the averaging on a cancel...
		*/
		void stop() {
			current = getLengthOfTask();
		}
		
		
		/**
		* Called from ProgressBarDemo to find out if the task has completed.
		*/
		boolean done() {
			if (current >= getLengthOfTask()) {
				return true;
			} else {
				return false;
			}
		}
		
		String getMessage() {
			return statMessage;
		}
		class ActualTask {
			ActualTask() {
				karyoDrawer.setNodata(nodata);
				Genome genome = karyoDrawer.getGenome();
				int nchr = genome.getMaxChromosome();
				setCurrent(0);
//				System.out.println("Actual task started, length " + getLengthOfTask());
				for (int i = 1; i <= nchr; i++) {
					statMessage = "Processing Chromosome " + i;
					ChromosomeLocus start = genome.getChromosome(i).getLeftEnd();
					if (start == null) { // nothing on chromosome...
						continue;
					}
					ChromosomeLocus current = start;
					int currentCol = getCurrentCol();
					double nodata = getNodata();
					do  {
						if (done()) break;
						if (current.getCdtIndex() != -1) {
							try {
								karyoDrawer.setMapValue(current, averager.getValue(current, currentCol));
							} catch (java.lang.ArrayIndexOutOfBoundsException e) {
								karyoDrawer.setMapValue(current, nodata);
							}
						} else {
							karyoDrawer.setMapValue(current, nodata);
						}
						current = current.getRight();
						
						incrCurrent();
					} while ((current != start) && (current != null));
					if (done()) break;
				}
				stop();
			}
		}
	}
	public void recalculateAverages() {
		averager = getAverager(configNode.getAttribute("averager", defaultAverager));
		// only need to calculate averages for loci which are in genome...
		averagerTask = new AveragerTask();
		averagerMonitor = new ProgressMonitor(this,
		"Calculating Averaged Values",
		"Note", 0, averagerTask.getLengthOfTask());
		averagerMonitor.setProgress(0);
		averagerTask.go();
		averageTimer.start();
	}

	Averager averager = null;
	MouseTracker mouseTracker = new MouseTracker();
	public synchronized void paintComposite (Graphics g) {
	  mouseTracker.paintComposite(g);
	}
	
	/**
	* override parent so as to avoid running out of memory at high zooms.
	*/
	public void paintComponent(Graphics g) {
		Dimension newsize = getSize();
		if (newsize == null) { return;}
		//		karyoDrawer.notifyObservers();
		if (karyoDrawer.getPixelPerVal() == 0) {
			if (getKaryoDrawer().getGenome().getMaxChromosome() > 0) {
				if (getKaryoDrawer().getGenome().getNonemptyCount() > 0) {
					redoScale();
				} else {
					return;
				}
			}
		}
//		System.out.println("repaint called on KaryoView " + newsize);
		// update offscreenBuffer if necessary
		g.setColor(Color.white);
		g.fillRect(0,0,newsize.width, newsize.height);
		if (isEnabled()) {
			offscreenValid = false;
			updateBuffer(g);
			paintComposite(g);
		}
	}
	Rectangle clipRect = new Rectangle();
	public void updateBuffer(Graphics g) {
	  if (justZoomed) {
		justZoomed = false;
		scrollPane.getViewport().setViewPosition(zoomPoint);
		repaint();
		return;
	  }
	  try {
		  clipRect = g.getClipBounds(clipRect);
	  } catch (java.lang.NoSuchMethodError e) {
		  clipRect.setBounds(g.getClipBounds());
	  }
	  karyoDrawer.paint(g, clipRect);
	}
	
	
	/*
	private int nChromosomes = 0;
	private int chrIndex = -1;
	private int armIndex = -1;
	private int posIndex = -1;
	private int orfIndex = -1;
	*/
    public String viewName() {
	return "KaryoView";
    }

	/**
	* This method is called to make the karyoview fit on the screen. 
	* It will set the desired width and height, and then ask the KaryoDrawer
	* to adjust the pixels per value and pixels per row to something sensible.
	*/
	Rectangle repaintRect = new Rectangle();
	public void redoScale() {
//		Exception e = new Exception();		e.printStackTrace();
		Dimension size = scrollPane.getViewport().getExtentSize();
		karyoDrawer.setWidth(size.width);
		karyoDrawer.setHeight(size.height);
		karyoDrawer.autoScale();
		
		revalidate();
		repaintRect.setBounds(0, 0, size.width, size.height);
		repaint(repaintRect);

		if (parameterPanel != null) {
			parameterPanel.getValues();
		}
	}
    public String [] getExperiments() {
	  String [] names = new String[getNumCol()];
	  for (int i = 0; i < getNumCol();i++) {
		names[i] = experimentInfo.getHeader(i)[0];
	  }
	  return names;
	}
    public Dimension getPreferredSize() {
	if (karyoDrawer.getWidth() >= 0) {
	    Dimension p = new Dimension(karyoDrawer.getWidth(),karyoDrawer.getHeight());
	    return p;
	} else {
	    return super.getPreferredSize();
	}
    }

	public boolean isChromosomeVisible(int i) {
	  JViewport viewport = scrollPane.getViewport();
	  Rectangle clipRect = viewport.getViewRect();
		int min = karyoDrawer.minVisibleChromosome(clipRect);
		int max = karyoDrawer.maxVisibleChromosome(clipRect);
		System.out.println("min " + min + " max " + max);
		if (i <= max && i >= min) {
			return true;
		} else {
			return false;
		}
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
	  karyoDrawer.setWidth((int) (karyoDrawer.getWidth()  * sx));
	  karyoDrawer.setHeight((int) (karyoDrawer.getHeight() * sy));
	  karyoDrawer.setPixelPerMap(sx * karyoDrawer.getPixelPerMap());
	  karyoDrawer.setPixelPerVal(sy * karyoDrawer.getPixelPerVal());
//	  System.out.println("now " + getPreferredSize());
	  revalidate();
	  zoomPoint.setLocation((int) (sx * r.x), 
	  						(int) (sy * r.y));
	  justZoomed = true;
	  scrollPane.repaint();
	  if (parameterPanel != null) {
		  parameterPanel.getValues();
	  }
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
		karyoDrawer.setWidth((int) (karyoDrawer.getWidth()      * factor));
		karyoDrawer.setHeight((int) (karyoDrawer.getHeight()    * factor));
		karyoDrawer.setPixelPerMap(karyoDrawer.getPixelPerMap() * factor);
		karyoDrawer.setPixelPerVal(karyoDrawer.getPixelPerVal() * factor);
		revalidate();
		zoomPoint.setLocation((int) (point.x*factor - (point.x - r.x)), 
							(int) (point.y*factor - (point.y - r.y)));
		justZoomed = true;
		scrollPane.repaint();
		if (parameterPanel != null) {
			parameterPanel.getValues();
		}
	}
	/**
	* follows mouse around, communicates with KaryoView by calling drawBad
	* and being called by paintComposite()
	*/
	class MouseTracker implements MouseMotionListener, MouseListener, KeyListener {
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
					mouseMoved(new MouseEvent (KaryoView.this, MouseEvent.MOUSE_MOVED, 10, 0, 
					startPoint.x, startPoint.y, 1, false));
					break;
				case '+':
					zoomFactor(2.0, startPoint);
					startPoint.x = (int)(startPoint.x * 2.0);
					startPoint.y = (int)(startPoint.y * 2.0);
					mouseMoved(new MouseEvent (KaryoView.this, MouseEvent.MOUSE_MOVED, 10, 0, 
					startPoint.x, startPoint.y, 1, false));
					break;
			}
		}

		Point startPoint = new Point();
	  Point endPoint = new Point();
	  /* dragRect is non-null when the mouse is dragging */
	  Rectangle dragRect = null;

	  /*
	   * tip and highlight are non-null when the mouse is in the view
	   * they are also set to null when the mouse is released on a zoom
	   */
	  Point tip = null;           // keeps track of the tip of the  most recent gene
	  Rectangle highlight = null; // box around tip in which to draw highlight

	  Rectangle repaintRect = new Rectangle();
	  public void mouseEntered(MouseEvent e) {
		  requestFocus();
	  }
	  
	  public void mouseExited(MouseEvent e) {
		repaint(repaintRect);
		if (tip != null) {
			repaintRect.setLocation(tip.x, tip.y);
		}
		repaintRect.setSize(0,0);
		if (highlight != null) {
			repaintRect.add(highlight);
		}
		tip       = null;
		highlight = null;
		repaint(repaintRect);
		updateStatus(null);
	  }

	  public void mouseClicked(MouseEvent e) {
		  if (viewFrame.windowActive() == false) return;
		int xpos = (int) e.getX();
		int ypos = (int) e.getY();
		startPoint.setLocation(xpos, ypos);
		ChromosomeLocus closest = karyoDrawer.getClosest(startPoint);
		if (closest != null) {
			int closestIndex = closest.getCdtIndex();
			if (closestIndex != -1) {
				if (viewFrame != null) {
					viewFrame.displayURL(closestIndex);
				}
			}
			
		}
	  }	  
	  
	  // MouseMotionListener
	  public void mouseMoved(MouseEvent e) {
		int xpos = e.getX();
		int ypos = e.getY();
		startPoint.setLocation(xpos, ypos);
		ChromosomeLocus closest = karyoDrawer.getClosest(startPoint);
		updateStatus(closest);
		moveHighlight(closest);
	  }
	  // Mouse Listener 
	  public void mousePressed(MouseEvent e) {
		  mouseExited(e);
		if (viewFrame.windowActive() == false) return;
		// initialize startpoint and endpoint
		startPoint.setLocation(e.getX(), e.getY());
		endPoint.setLocation(startPoint.x, startPoint.y);
		// setup dragrect
		dragRect = new Rectangle();
		dragRect.setLocation(startPoint.x, startPoint.y);
		dragRect.setSize(endPoint.x - dragRect.x, endPoint.y - dragRect.y);
		// repaint.
		repaint(dragRect);
	  }
	  public void mouseReleased(MouseEvent e) {
		if (viewFrame.windowActive() == false) return;
		if (dragRect == null) return;
		mouseDragged(e);
		repaintRect.setBounds(dragRect);
		// need to set null for repaint....
		tip = null;
		highlight = null;
		dragRect = null;
//		System.out.println("Repainting rect");
		repaint(repaintRect);
		if (repaintRect.width > 3) {
		  if (repaintRect.height > 3) {
			zoomRectangle(repaintRect);
		  }
		}
	  }
	  // MouseMotionListener
	  public void mouseDragged(MouseEvent e) {
		// move dragRect
		
		if (dragRect == null) {
			LogBuffer.println("dragRect null");
			return;
		}
		endPoint.setLocation(e.getX(), e.getY());
		dragRect.setLocation(startPoint.x, startPoint.y);
		dragRect.setSize(0,0);
		dragRect.add(endPoint.x, endPoint.y);

		// animate!
		repaint(repaintRect);	
		repaintRect.setBounds(dragRect);
		repaintRect.grow(1,1);
		repaint(repaintRect);
	  }
	  
	  public void paintComposite (Graphics g) {
		// composite the rectangles...
		if (highlight != null) {
		  g.setColor(karyoDrawer.getKaryoColorSet().getColor("Highlight"));
		  int lx = highlight.x;
		  int  ux = lx + highlight.width;
		  int uy = highlight.y;
		  int  ly = uy + highlight.height;
		  g.drawLine(lx,ly,ux,uy);
		  g.drawLine(lx,uy,ux,ly);
		  //		    g.fillRect(highlight.x, highlight.y, 
		  //				       highlight.width, highlight.height);
		}
		if (tip != null) {
		  int mouseX = (int) startPoint.x;
		  int mouseY = (int) startPoint.y;
		  g.drawLine(mouseX, mouseY, tip.x, tip.y);
		}
		if (dragRect != null) {
		  drawBand(dragRect, g);
		}
	  }
	  private void drawBand(Rectangle l, Graphics g) { 
		  g.setColor(Color.yellow);
//		  g.setXORMode(getBackground()); doesn't work. don't know why not - probbaly not using setBackground()???
		g.drawRect(l.x, l.y, l.width, l.height);
		g.setPaintMode();
	  }

	  private void removeHighlight() { // don't you love english?
		  highlight = null;
		  tip = null;
		  repaint(repaintRect);
		  return;
	  }
	  private void moveHighlight(ChromosomeLocus locus) {
		  if (locus == null) {
			  removeHighlight();
			  return;
		  }
		  
		  double val = 0;
		  try {
			  val = averager.getValue(locus, getCurrentCol());
		  } catch (Exception e) {
			  removeHighlight();
			  return;
		  }
		  
		  if (val == nodata) {
			  removeHighlight();
			  return;
		  }
		  
		  // locate pixel of top of gene...
		  tip = karyoDrawer.getEnd(locus);
		  
		  if (highlight == null) {
			  highlight = new Rectangle();
		  }
		  highlight.setBounds(tip.x-5, tip.y-5, 11, 11);
		  // 	  System.out.println("moved highlight to " + geneX + ", " + geneY + " locus " + locus.toString());
		  repaint(repaintRect);
		  repaintRect.setLocation(tip.x, tip.y);
		  repaintRect.setSize(0,0);
		  repaintRect.add(highlight);
		  repaintRect.add(tip);
		  repaintRect.add(startPoint);
		  repaintRect.grow(1,1);
		  repaint(repaintRect);
	  }
	}
	private void updateStatus(ChromosomeLocus locus) {
		if (locus == null) {
			statusText[0] = "KeyBoard Shortcuts:";
			statusText[1] = "";
			statusText[2] = "'+' zooms in on mouse";
			statusText[3] = "'-' zooms out on mouse";
			statusText[4] = "";
			//			LogPanel.println("KaryoView.updateStatus(): Locus was " + locus);
		} else {
			int chr = locus.getChromosome();
			int arm = locus.getArm();
			double pos = locus.getPosition();
			int closestIndex = locus.getCdtIndex();
			statusText[0] = "Cursor is over Chromosome " + chr + " arm " + arm + " position " + pos;
			if (closestIndex != -1) {
				statusText[1] = geneInfo.getHeader(closestIndex,"NAME");
				double val = karyoDrawer.getMapValue(closestIndex);
				if (val == nodata) {
					statusText[2] = "Value: No Data";
				} else {
					statusText[2] = "Value: " + val;
				}
				String [] desc = averager.getDescription(locus, getCurrentCol());
				statusText[3] = desc[0];
				statusText[4] = desc[1];
			} else {
				statusText[1] = "Mouse over for info on genes...";
				statusText[2] = "";
				statusText[3] = "";
				statusText[4] = "";
			}
		}
		if (status != null) {
			status.setMessages(getStatus());	
		}
	}
    private String [] statusText = new String[5];
    // method from ModelView
    public String[]  getStatus() {
	return statusText;
    }


	private int getClosest(int chr, int arm, double pos) {
/*
		return chromosomeLoci.getClosestLocus(chr, arm, pos).getCdtIndex();
*/
return 0;
	}
	
	  private ConfigNode configNode = new DummyConfigNode("KaryoView");
	  /** Setter for configNode */
	  public void bindConfig(ConfigNode configNode) {
		  this.configNode = configNode;
		  simpleAverager.bindConfig(  getFirst("SimpleAverager"));
		  intervalAverager.bindConfig(getFirst("IntervalAverager"));
		  nearestAverager.bindConfig( getFirst("NearestAverager"));
		  neighborAverager.bindConfig( getFirst("NeighborAverager"));
	  }
	  /** Getter for configNode */
	  public ConfigNode getConfigNode() {
		  return configNode;
	  }

	  	/**
	* always returns an instance of the node, even if it has to create it.
	*/
	private ConfigNode getFirst(String name) {
		ConfigNode cand = getConfigNode().fetchFirst(name);
		return (cand == null)? getConfigNode().create(name) : cand;
	}

  /**
  * Inner class to manage the settings of the extant averagers.
  */
  private class AveragerSettingsPanel extends JPanel implements SettingsPanel {
	  private KaryoView karyoView;
	  /** Setter for karyoView */
	  public void setKaryoView(KaryoView karyoView) {
		  this.karyoView = karyoView;
	  }
	  /** Getter for karyoView */
	  public KaryoView getKaryoView() {
		  return karyoView;
	  }
	  
	  public void setEnabled(boolean enabled) {
		  simpleButton.setEnabled(enabled);
		  nearestButton.setEnabled(enabled);
		  neighborButton.setEnabled(enabled);
		  intervalButton.setEnabled(enabled);
		  
		  nearestField.setEnabled(enabled);
		  neighborField.setEnabled(enabled);
		  intervalField.setEnabled(enabled);
	  }
	  
	  private JButton simpleButton, nearestButton, neighborButton, intervalButton;
	  private JTextField nearestField, neighborField, intervalField;
	  public AveragerSettingsPanel(KaryoView karyoView) {
		  setKaryoView(karyoView);
		  configureWidgets();
		  addWidgets();
	  }
	  private void addWidgets() {
		  setLayout(new GridBagLayout());
		  GridBagConstraints gc = new GridBagConstraints();
		  gc.weightx = 100;
		  gc.weighty = 100;
		  gc.gridx = 0;
		  gc.gridy = 0;
		  gc.gridwidth = 1;
		  gc.gridheight = 1;
		  add(new JLabel("Options"));
		  gc.gridy = 1;
		  add(simpleButton, gc);

		  gc.gridy = 2;
		  add(nearestButton, gc);
		  gc.gridy = 3;
		  add(neighborButton, gc);
		  gc.gridy = 4;
		  add(intervalButton, gc);

		  gc.gridx = 1;
		  gc.gridy = 0;
		  add(new JLabel("Options"));
		  gc.gridy = 2;
		  add(nearestField, gc);
		  gc.gridy = 3;
		  add(neighborField, gc);
		  gc.gridy = 4;
		  add(intervalField, gc);
	  }
	  private void configureWidgets() {
		  // fields to enter data in
		  nearestField = new JTextField("" + nearestAverager.getNum());
		  neighborField = new JTextField("" + neighborAverager.getNum());
		  intervalField = new JTextField("" + intervalAverager.getWidth());
		  nearestField.setColumns(5);
		  neighborField.setColumns(5);
		  intervalField.setColumns(5);
		  // buttons to choose from...
		  
		  //		  type = new ButtonGroup();
		  simpleButton = new JButton("No Averaging");
		  simpleButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  karyoView.setSimpleAveraging();
			  }
		  });
		  
		  //		  type.add(simpleButton);
		  
		  nearestButton = new JButton("Nearest :");
		  nearestButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  karyoView.setNearestAveraging(Integer.parseInt(nearestField.getText()));
			  }
		  });
		  
		  neighborButton = new JButton("Neighbor :");
		  neighborButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  karyoView.setNeighborAveraging(Integer.parseInt(neighborField.getText()));
			  }
		  });

		  intervalButton = new JButton("Interval :");
		  intervalButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  Double width = new Double(intervalField.getText());
				  karyoView.setIntervalAveraging(width.doubleValue());
			  }
		  });
	  }
	  
	  public void synchronizeTo() {
		  
	  }
	  
	  public void synchronizeFrom() {
	  }
  }
  
  
}

/**
 * just for parsing chromosome info...
 */
class KaryoParseException extends Exception {
    public KaryoParseException(String m) {
	super(m);
    }
}
