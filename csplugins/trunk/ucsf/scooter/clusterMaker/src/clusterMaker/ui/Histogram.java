/**
 * @(#)Histogram.java
 *
 * Java class that implements a Histogram
 *
 * @author 
 * @version 1.00 2009/6/30
 */

package clusterMaker.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;

import java.lang.Math;

import java.util.ArrayList;
import java.util.List;

import java.text.DecimalFormat;

import javax.swing.JComponent;

class Histogram extends JComponent implements MouseMotionListener, MouseListener{
	private int[] histoArray;
	private int histoMax = Integer.MIN_VALUE;
	private double[] graphData;
	private double minValue = Double.MAX_VALUE;
	private double maxValue = Double.MIN_VALUE;
	private int low;
	private int high;
	private final int XSTART = 100;
	private final int YEND = 50;
	private final int NBINS = 100;
	private int mouseX;
	private boolean boolShowLine = false;
	private double binSize;
	private List<HistoChangeListener> listeners = null;


	DecimalFormat form = new DecimalFormat("0.0##E0"); //rounds values for drawString
		
	Histogram(double[] inputData, int nBins) {
		super();
		setPreferredSize(new Dimension(800,500));
		histoArray = new int[NBINS];
		this.graphData = inputData;
		for (int i=0; i < graphData.length; i++) {
			minValue = Math.min(minValue, graphData[i]);
			maxValue = Math.max(maxValue, graphData[i]);
		}
		listeners = new ArrayList();
		createHistogram(graphData);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public void updateData(double[] graphData) {
		// Trigger redraw
	}

	public void paint(Graphics g) {
		super.paint(g);
		drawGraph(g);
		if(boolShowLine)
			mouseLine(mouseX, g);
	}

	public void mouseMoved(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
		Dimension dim = getPreferredSize();
		int width = dim.width;
		int xIncrement = (width-125)/NBINS;
		int histoMousePos = (e.getX()-XSTART)/xIncrement;

		if(e.getX()>XSTART && boolShowLine){
			mouseX = e.getX();
			repaint();
		}
	}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		Dimension dim = getPreferredSize();
		int width = dim.width;
		int xIncrement = (width-125)/NBINS;
		int histoMousePos = (e.getX()-XSTART)/xIncrement;

		if(e.getX()>XSTART && boolShowLine){
			mouseX = e.getX();
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent e){
		Dimension dim = getPreferredSize();
		int width = dim.width;
		int xIncrement = (width-125)/NBINS;
		int histoMousePos = (e.getX()-XSTART)/xIncrement;
		if(e.getX()>XSTART && e.getX()<(XSTART+xIncrement*histoArray.length) && boolShowLine){
			double binValue = minValue+(binSize*histoMousePos);
			// System.out.println("histoArray["+histoMousePos+"] = "+ histoArray[histoMousePos]+", "+form.format((binValue)));
			if (listeners.size() == 0) return;
			for (HistoChangeListener listener: listeners)
				listener.histoValueChanged(binValue);
		}

	}

	//handle the rest of the wizard buttons in a similar fashion


	
	public void setBoolShowLine(boolean inShowLine){boolShowLine = inShowLine;}

	/**
	 * Add a new change listener to this histogram
	 *
	 * @param listener the HistoChangeListener to call when the cutoff value changes
	 */
	public void addHistoChangeListener(HistoChangeListener listener) {
		if (listeners.contains(listener)) return;
		listeners.add(listener);
	}

	/**
	 * Remove a change listener from this histogram
	 *
	 * @param listener the HistoChangeListener to remove
	 */
	public void removeHistoChangeListener(HistoChangeListener listener) {
		listeners.remove(listener);
	}
			
	private void mouseLine(int mX, Graphics g){
		Dimension dim = getPreferredSize();
		int height = dim.height-100;
		int width = dim.width;
		int xIncrement = (width-125)/NBINS;
		int histoMousePos = (mX-XSTART)/xIncrement;
		if(histoMousePos >= histoArray.length)
			histoMousePos = histoArray.length-1;

		g.setColor(Color.red);
		g.drawLine(mX, YEND, mX, height);
		g.setColor(Color.black);
		g.drawString(form.format((minValue+(binSize*histoMousePos)))+" ("+histoArray[histoMousePos]+" values)",mX-50,YEND-5); //REMOVE THIS LATER
	}

	private void createHistogram(double[] inputData){
		histoMax = Integer.MIN_VALUE;
		binSize = (maxValue - minValue)/NBINS;
		// System.out.println("binSize = "+binSize);
		for(double dataItr : inputData){
			for(int nI=0; nI < NBINS; nI++){
				if(dataItr==minValue){
					histoArray[0]+=1;
					break;
				}
				if(dataItr>minValue+binSize*nI && dataItr<=minValue+binSize*(nI+1) ){
					histoArray[nI]+=1;
					break;
				}
			}
		}
		int test = 0; //REMOVE THIS LATER
		for(int nI=0; nI<histoArray.length; nI++){ 
			histoMax = Math.max(histoMax, histoArray[nI]);
			// System.out.println("hitoArray["+nI+"] = "+histoArray[nI]); //REMOVE THIS LATER
			test+=histoArray[nI]; //REMOVE THIS LATER
		}
		// System.out.println("test = "+test); //REMOVE THIS LATER
		// System.out.println("histoMax = "+histoMax); //REMOVE THIS LATER
	}
	
	private void drawGraph(Graphics g){
		Dimension dim = getPreferredSize();
		int height = dim.height-100;
		int width = dim.width;

		drawAxes(g, height, width);
		drawLabels(g, height, width);
		drawData(g, height, width);
	}

	private void drawAxes(Graphics g, int height, int width) {
		g.setColor(Color.black);
		g.drawLine(XSTART,YEND,XSTART,height);
		g.setColor(Color.green);
		g.drawLine(XSTART,height,width,height);
		
		
		double yIncrement = (height-50)/(double)histoMax;
		for(int nI=1;nI<=histoMax;nI++){
			if(nI%10==0)
				g.setColor(Color.red);
			else
				g.setColor(Color.gray);
			g.drawLine(XSTART-10,height-(int)(yIncrement*nI),width,height-(int)(yIncrement*nI));
		}
		
		int xIncrement = (width-125)/NBINS;
		for(int nI=0; nI<=histoArray.length; nI++){
			if(nI%10==0){
				g.setColor(Color.black);
				g.drawLine(XSTART+xIncrement*nI,height,100+xIncrement*nI,height+10);
			}
				
		}
	}

	private void drawLabels(Graphics g, int height, int width) {
		g.setColor(Color.gray);
		double yIncrement = (height-50)/(double)histoMax;
		for(int nI=1;nI<=histoMax;nI++){
			if(nI%10==0)
				g.drawString(""+nI,70,height-(int)(yIncrement*nI)+5);
		}
		
		int xIncrement = (width-125)/NBINS;
		double binSize = (maxValue - minValue)/NBINS;
		
		g.drawString(""+form.format(minValue),XSTART-25,height+20);
		g.drawString(""+form.format(maxValue),XSTART-25+xIncrement*histoArray.length,height+20);
		for(int nI=1; nI<histoArray.length; nI++){
			if(nI%10==0)
				g.drawString(""+form.format((minValue+(binSize*nI))),XSTART-25+xIncrement*nI,height+20);
		}
	}
	
	
	// TODO: Change this method to use height and width.  You may need to scale the
	// the font also.
	private void drawData(Graphics g, int height, int width){
		int nBlueChange = 100;
		double yIncrement = (height-50)/(double)histoMax;
		//System.out.println("yIncrement = "+yIncrement);
		int xIncrement = (width-125)/NBINS;
		
		for(int nI=0; nI<histoArray.length; nI++){
			g.setColor(new Color(0,0,nBlueChange));
			g.fillRect(XSTART+xIncrement*nI, height-(int)(histoArray[nI]*yIncrement), xIncrement, (int)(histoArray[nI]*yIncrement));
			g.setColor(Color.black);
			g.drawRect(XSTART+xIncrement*nI, height-(int)(histoArray[nI]*yIncrement), xIncrement, (int)(histoArray[nI]*yIncrement));

			nBlueChange+=15;
			if(nBlueChange >= 250)
				nBlueChange = 100;
		}

	}

}
