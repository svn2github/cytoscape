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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
 

public class HistogramDialog extends JDialog implements ActionListener, ComponentListener{
	double[] inputArray;
	int nBins;
	Histogram histo;

	public HistogramDialog(String title, double[] inputArray, int nBins) {
		super();
		this.inputArray = inputArray;
		this.nBins = nBins;
		setTitle(title);

		initializeOnce();
	}

	//public double getCutoff() {}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("set"))
			histo.setBoolShowLine(true);
		if(e.getActionCommand().equals("close"))
			this.dispose();
	}

	public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}

	public void componentResized(ComponentEvent e) {
		// Get our new size & update histogram
		Dimension dim = e.getComponent().getSize();
		histo.setPreferredSize(new Dimension(dim.width, dim.height));
	}

	private void initializeOnce() {
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.addComponentListener(this);
		

		// Create and add the histogram component
		histo = new Histogram(inputArray, nBins);
		mainPanel.add(histo);
			
		// TODO: Add box to set lower and upper bounds.  Look at JText and JLabel

		// Create our button box
		JPanel buttonBox = new JPanel();

		// Close button
		JButton closeButton = new JButton("close");
		closeButton.addActionListener(this);
		closeButton.setActionCommand("close");

		// OK button
		JButton okButton = new JButton("Set Cutoff");
		okButton.addActionListener(this);
		okButton.setActionCommand("set");

		buttonBox.add(okButton);
		buttonBox.add(closeButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		Dimension buttonDim = buttonBox.getPreferredSize();
		buttonBox.setMinimumSize(buttonDim);
		buttonBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonDim.height));
		mainPanel.add(buttonBox);

		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
	}

	public void addHistoChangeListener(HistoChangeListener h){
		histo.addHistoChangeListener(h);
	}
	
	public void removeHistoChangeListener(HistoChangeListener h){
		histo.removeHistoChangeListener(h);
	}

}
