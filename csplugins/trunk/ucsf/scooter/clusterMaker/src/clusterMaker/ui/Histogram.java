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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;

import java.lang.Math;

import java.util.ArrayList;
import java.util.List;

import java.text.DecimalFormat;

import javax.swing.JComponent;

class Histogram extends JComponent implements MouseMotionListener, MouseListener{

	// The histogram
	private int[] histoArray;

	// Original data
	private double[] graphData;

	// Y scale values
	private int histoMax = Integer.MIN_VALUE;
	private int histoMin = 0;
	private int histoMaxUp;

	// X scale values
	private double minValue = Double.MAX_VALUE;
	private double maxValue = Double.MIN_VALUE;
	private int low;
	private int high;

	private final int XSTART = 100;
	private final int YEND = 50;
	private final int NBINS;
	private int mouseX;
	private boolean boolShowLine = false;
	private List<HistoChangeListener> listeners = null;
	private Font adjSizeFont;
	private int fontSize;
	private double xInterval;

	DecimalFormat form = new DecimalFormat("0.0E0"); //rounds values for drawString
		
	Histogram(double[] inputData, int nBins) {
		super();
		NBINS = nBins;
		setPreferredSize(new Dimension(1000,400));
		histoArray = new int[NBINS];
		this.graphData = inputData;
		listeners = new ArrayList();

		createHistogram(graphData);

		addMouseMotionListener(this);
		addMouseListener(this);
		fontSize = 5+ (int)(this.getPreferredSize().getWidth()/(NBINS));
		if(fontSize>18)
			fontSize=18;
		adjSizeFont = new Font("Helvetica", Font.PLAIN, fontSize);
		// System.out.println("fontSize = "+fontSize);
	}

	public void updateData(double[] graphData) {
		// Trigger redraw
		histoArray = new int[NBINS];
		this.graphData = graphData;

		createHistogram(graphData);
	}

	public void paint(Graphics g) {
		super.paint(g);
		fontSize = 3 +(int)(this.getPreferredSize().getWidth()/(NBINS));
		if(fontSize>18)
			fontSize=18;

		adjSizeFont = new Font("Helvetica", Font.PLAIN, fontSize);
		System.out.println("fontSize = "+fontSize);
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
			double binValue = minValue+(xInterval*histoMousePos);
			// System.out.println("histoArray["+histoMousePos+"] = "+ histoArray[histoMousePos]+", "+form.format((binValue)));
			if (listeners.size() == 0) return;
			for (HistoChangeListener listener: listeners)
				listener.histoValueChanged(binValue);
		}

	}

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
		g.setFont(adjSizeFont);
		g.drawString(toSciNotation(form.format((minValue+(xInterval*histoMousePos))).toString()," ("+histoArray[histoMousePos]+" values)"),mX-50,YEND-5);
	}

	private void createHistogram(double[] inputData){
		calculateXScale();
		
		// Bin the data
		for(double dataItr : inputData){
			for(int nI=0; nI < NBINS; nI++){
				if(dataItr==minValue){
					histoArray[0]+=1;
					break;
				}
				if(dataItr>minValue+xInterval*nI && dataItr<=minValue+xInterval*(nI+1) ){
					histoArray[nI]+=1;
					break;
				}
			}
		}
		calculateYScale();
	}

	private void calculateXScale() {

		// Calculate our minimum and maximum X values
		for (int i=0; i < graphData.length; i++) {
			minValue = Math.min(minValue, graphData[i]);
			maxValue = Math.max(maxValue, graphData[i]);
		}

		System.out.println("range = "+minValue+" - "+maxValue);

		// Calculate our X scale
		double range = maxValue - minValue;
		double oomRange = Math.log10(range); //order of magnitude
		// System.out.println("oomRange = "+oomRange);
		oomRange = oomRange + (.5*oomRange/Math.abs(oomRange)); // Increase our oom by .5
		// System.out.println("oomRange = "+oomRange);
		oomRange = (int)(oomRange); //make it an integer

		double high = (Math.rint((maxValue/Math.pow(10, oomRange))+.5)) * (Math.pow(10, oomRange)); // This is our initial high value

		// System.out.println("high = "+high);
		if (maxValue <= high/2) 
			high = high/2; // A little fine-tuning

		double low = (Math.rint((minValue/Math.pow(10, oomRange))-.5)) * Math.pow(10,oomRange);

		if (minValue >= low/2) 
			low = low/2;

		xInterval = (high - low) / NBINS;
		
	}

	private void calculateYScale() {
		histoMin = 0;

		// First, determine the max value
		for(int nI=0; nI<histoArray.length; nI++){ 
			histoMax = Math.max(histoMax, histoArray[nI]);
		}

		while(histoMax > histoMaxUp)
			histoMaxUp += (int)(Math.pow(10,(int)(Math.log10(histoMax))));

		if(histoMaxUp<10)
			histoMaxUp = 10;
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

		int xIncrement = (width-125)/NBINS;
		int maxX = xIncrement*NBINS+XSTART;

		// Draw the Y axis
		g.setColor(Color.black);
		g.drawLine(XSTART,YEND,XSTART,height);

		// Draw the X axis
		g.drawLine(XSTART,height,maxX,height);
		
		// Draw the Y incremental lines
		double yIncrement = (height-YEND)/(double)histoMaxUp;
		for(int nI=1;nI<=histoMaxUp;nI++){
			if(((double)nI%((double)histoMaxUp/10.0)) == 0.0){
				g.setColor(Color.red);
				g.drawLine(XSTART-5,(int)(height-(yIncrement*nI)),maxX,(int)(height-(yIncrement*nI)));
			}
			else if(((double)nI%((double)histoMaxUp/20.0)) == 0.0){
				g.setColor(Color.gray);
				g.drawLine(XSTART,(int)(height-(yIncrement*nI)),maxX,(int)(height-(yIncrement*nI)));
			}
		}

		g.setColor(Color.black);
		for(int nI=0; nI<=NBINS; nI++){
			if(nI%10==0){
				g.drawLine(XSTART+xIncrement*nI,height,XSTART+xIncrement*nI,height+10);
			}
		}
	}

	private void drawLabels(Graphics g, int height, int width) {
		g.setColor(Color.black);
		g.setFont(adjSizeFont);
		FontMetrics metrics = g.getFontMetrics();

		// Draw the Y labels
		double yIncrement = (height-YEND)/(double)histoMaxUp;
		for(int nI=1;nI<=histoMaxUp;nI++){
			String str = ""+nI;
			int offset = 90-metrics.stringWidth(str);

			if(nI%(histoMaxUp/10)==0)
				g.drawString(str, offset, height-(int)(yIncrement*nI)+5);
		}

		// Now draw the X labels
		int xIncrement = (width-125)/NBINS;
		for(int nI=0; nI<=NBINS; nI++){
			double value = low+(xInterval*nI);
			String str = form.format(value);
			int offset = XSTART+metrics.stringWidth(str)/2 - 50;
			if (value == 0 || (value > 1 && value < 10))
				offset += 20;

			if(nI%20==0)
				g.drawString(toSciNotation(str, ""),offset+xIncrement*nI,height+25);
			if(nI%20==10)
				g.drawString(toSciNotation(str, ""),offset+xIncrement*nI,height+30);
		}
	}
	
	
	// TODO: Change this method to use height and width.  You may need to scale the
	// the font also.
	private void drawData(Graphics g, int height, int width){
		int nBlueChange = 100;
		double yIncrement = (height-50)/(double)(histoMaxUp);
		//System.out.println("yIncrement = "+yIncrement);
		int xIncrement = (width-125)/NBINS;
		double xValue = low;
		int histoIndex = 0;
		
		for(int nI=0; nI<=NBINS; nI++){
			if (xValue >= minValue) {
				g.setColor(new Color(0,0,nBlueChange));
				g.fillRect(XSTART+xIncrement*nI, (int)(height-(histoArray[histoIndex]*yIncrement)), xIncrement, (int)(histoArray[histoIndex]*yIncrement));
				g.setColor(Color.black);
				g.drawRect(XSTART+xIncrement*nI, (int)(height-(histoArray[histoIndex]*yIncrement)), xIncrement, (int)(histoArray[histoIndex]*yIncrement));
				histoIndex++;

				nBlueChange+=15;
				if(nBlueChange >= 250)
					nBlueChange = 100;
			}
			xValue += xInterval;
		}
	}
	
	private AttributedCharacterIterator toSciNotation(String d, String suffix){
		String returnString = "";
		for(int i=0; i<d.length(); i++){
			if(d.charAt(i)== 'E')
				break;
			returnString+=d.charAt(i);
		}

		String exponent = "";
		for(int i=d.length()-1; i>0; i--){
			if(d.charAt(i)== 'E')
				exponent+=d.substring(i+1,d.length());
		}

		AttributedString str;
		if (Integer.parseInt(exponent) == 0) {
			str = new AttributedString(returnString+suffix);
		} else {
			returnString += "x10";
			int superOffset = returnString.length();
			returnString += exponent;
			int superEnd = returnString.length();

			str = new AttributedString(returnString+suffix);
			str.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, superOffset, superEnd);
		}

		str.addAttribute(TextAttribute.FONT, adjSizeFont, 0, returnString.length());
		return str.getIterator();
	}
}
