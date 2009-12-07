/**
 * @(#)HistogramDialog.java
 *
 * A JDialog component that displays a histogram.  This 
 *
 * @author 
 * @version 
 */

package clusterMaker.ui;
 
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.ScrollPane;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
 

public class HistogramDialog extends JDialog implements ActionListener, ComponentListener, HistoChangeListener{
	double[] inputArray;
	int nBins;
	int currentBins;
	Histogram histo;
	JPanel mainPanel;
	JPanel buttonBox;
	ScrollPane scrollPanel;
	JButton zoomOutButton;
	boolean isZoomed = false;
	List<HistoChangeListener> changeListenerList = null;

	public HistogramDialog(String title, double[] inputArray, int nBins) {
		super();
		this.inputArray = inputArray;
		this.nBins = nBins;
		this.currentBins = nBins;
		this.changeListenerList = new ArrayList();
		setTitle(title);

		initializeOnce();
	}

	public void updateData(double[] inputArray) {
		this.inputArray = inputArray;
		if (histo != null) {
			histo.updateData(inputArray);
		}
	}

	//public double getCutoff() {}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("set"))
			histo.setBoolShowLine(true);
		if(e.getActionCommand().equals("close"))
			this.dispose();
		if(e.getActionCommand().equals("zoom")){
			currentBins = currentBins * 2;
			isZoomed = true;
			zoom(inputArray, false);
			zoomOutButton.setEnabled(true);
		}
		if(e.getActionCommand().equals("zoomOut")){
			currentBins = currentBins / 2;
			if (currentBins == nBins) {
				isZoomed = false;
				zoomOutButton.setEnabled(false);
			}
			zoom(inputArray, true);
		}
		
	}

	public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}

	public void componentResized(ComponentEvent e) {
		// Get our new size & update histogram
		Dimension dim = e.getComponent().getSize();
		if(!isZoomed){
			histo.setPreferredSize(new Dimension(dim.width, dim.height));
		}
		else{
			histo.setPreferredSize(new Dimension(2000, dim.height));
			histo.repaint();
			scrollPanel.setPreferredSize(new Dimension(dim.width, dim.height));
		}
	}

	private void initializeOnce() {
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.addComponentListener(this);
		

		// Create and add the histogram component
		histo = new Histogram(inputArray, nBins);
		histo.addHistoChangeListener(this);
		mainPanel.add(histo);
			
		// TODO: Add box to set lower and upper bounds.  Look at JText and JLabel

		// Create our button box
		buttonBox = new JPanel();

		// Close button
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		closeButton.setActionCommand("close");

		// OK button
		JButton okButton = new JButton("Set Cutoff");
		okButton.addActionListener(this);
		okButton.setActionCommand("set");
		
		JButton zoomButton = new JButton("Zoom In");
		zoomButton.addActionListener(this);
		zoomButton.setActionCommand("zoom");

		zoomOutButton = new JButton("Zoom Out");
		zoomOutButton.addActionListener(this);
		zoomOutButton.setActionCommand("zoomOut");
		zoomOutButton.setEnabled(false);

		buttonBox.add(okButton);
		buttonBox.add(closeButton);
		buttonBox.add(zoomButton);
		buttonBox.add(zoomOutButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		Dimension buttonDim = buttonBox.getPreferredSize();
		buttonBox.setMinimumSize(buttonDim);
		buttonBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonDim.height));
		mainPanel.add(buttonBox);

		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
	}

	public void addHistoChangeListener(HistoChangeListener h){
		if (changeListenerList.contains(h)) return;
		changeListenerList.add(h);
	}
	
	public void removeHistoChangeListener(HistoChangeListener h){
		changeListenerList.remove(h);
	}
	
	public void histoValueChanged(double bounds){
		if (changeListenerList.size() == 0) return;
		for (HistoChangeListener listener: changeListenerList)
			listener.histoValueChanged(bounds);
	}
	
	private void zoom(double[] inputArray, boolean zoomOut){
		
		mainPanel.removeAll();
		// Get the width of the current histogram
		Dimension histoDim = histo.getSize();
		int histoWidth = histoDim.width*2;

		if (zoomOut)
			histoWidth = histoDim.width / 2;
			
		// Create a new histogram
		histo = new Histogram(inputArray, currentBins);
		histo.addHistoChangeListener(this);

		// Get the size of the dialog
		Dimension dim = this.getSize();
		int height = dim.height-50; // Account for the button box

		if (isZoomed) {
			scrollPanel = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
			scrollPanel.addComponentListener(this);
			scrollPanel.add(histo);
			scrollPanel.setPreferredSize(new Dimension(dim.width, histoDim.height));
			histo.setPreferredSize(new Dimension(histoWidth, histoDim.height));
			mainPanel.add(scrollPanel);
		} else {
			histo.setPreferredSize(new Dimension(dim.width, dim.height));
			mainPanel.add(histo);
		}
		
		mainPanel.add(buttonBox);
		
		// Trigger a relayout
		pack();
	}
    
	/**
	 * Main method for testing purposes.
	 */
	public static void main(String [] args) {
	
		double[] randArray = new double[100000]; //used for test
		for(int nI=0; nI < randArray.length; nI++){
			randArray[nI] = Math.random()*0.0000007;//assigning random doubles to randArray (used for test)
		}

		JDialog dialog = new HistogramDialog("Test Dialog", randArray, 100);
		dialog.pack();
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

}
