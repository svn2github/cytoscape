package clusterExplorerPlugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import cytoscape.Cytoscape;


public class HistogramPlotWindow extends JFrame implements ActionListener, ComponentListener {
	
	private Vector<Vector<Float>> datas;
	private int numClasses;
	private int numClassesStart;
	
	private String title;
	private String xLabel;
	private String yLabel;
	private String[] labels;
	
	private JPanel histoPanel = new JPanel();
	private JPanel attributesPanel = new JPanel();
	
	private float absoluteMin, absoluteMax;
	
	private JSpinner minSpinner;
	private JSpinner maxSpinner;
	private JSpinner bucketsSpinner;
	
	private HistogramPlot histogram;
	
	private JCheckBox logYcheckBox = new JCheckBox("Log10-scale Y-axis");
	private JCheckBox showToCheckBox = new JCheckBox("'N to M' X-axis labels");
	
	public HistogramPlotWindow(Vector<Vector<Float>> datas, int numClasses, String title, String xLabel, String yLabel, String[] labels) {
		super("Histogram");
		
		this.datas = datas;
		this. numClasses = numClasses;
		this.numClassesStart = numClasses;
		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.labels = labels;
		
		
		// check for empty vectors
		Vector<String> labelsVector = new Vector<String>();
		for (int j = 0; j < labels.length; j++) {
			labelsVector.add(labels[j]);
		}
		for (int i = 0; i < datas.size(); i++) {
			Vector<Float> d = datas.get(i);
			if (d.size() == 0) {
				datas.removeElementAt(i);
				labelsVector.removeElementAt(i);
				i--;
			}
		}
		this.labels = new String[labelsVector.size()];
		for (int i = 0; i < labelsVector.size(); i++) {
			this.labels[i] = labelsVector.get(i);
		}
		
		
		Point2D.Float p = HistogramPlot.getMinMax(datas);
		this.absoluteMin = p.x;
		this.absoluteMax = p.y;
		
		logYcheckBox.setSelected(false);
		showToCheckBox.setSelected(false);
		
		double stepsize = (this.absoluteMax - this.absoluteMin) / 100;
		
		SpinnerNumberModel snmMin = new SpinnerNumberModel((double) this.absoluteMin, (double) this.absoluteMin, (double) this.absoluteMax, stepsize); 
		SpinnerNumberModel snmMax = new SpinnerNumberModel((double) this.absoluteMax, (double) this.absoluteMin, (double) this.absoluteMax, stepsize);
		SpinnerNumberModel snmBuckets = new SpinnerNumberModel(this.numClasses, 2, 50, 1);
		
		this.minSpinner = new JSpinner(snmMin);
		this.maxSpinner = new JSpinner(snmMax);
		this.bucketsSpinner = new JSpinner(snmBuckets);
		
		this.setSize(700, 400);
		
		JPanel fieldsPanel = new JPanel();
		
		fieldsPanel.setLayout(new SpringLayout());
		
		fieldsPanel.add(new JLabel("Min: "));
		fieldsPanel.add(this.minSpinner);
		
		fieldsPanel.add(new JLabel("Max: "));
		fieldsPanel.add(this.maxSpinner);
		
		fieldsPanel.add(new JLabel("Buckets: "));
		fieldsPanel.add(this.bucketsSpinner);
		
		SpringUtilities.makeCompactGrid(fieldsPanel, 3, 2, 5, 5, 5, 5);
		
		JButton updateButton = new JButton("Update plot");
		updateButton.addActionListener(this);
		updateButton.setActionCommand("update");
		
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		resetButton.setActionCommand("reset");
		
		JPanel dummy1 = new JPanel();
		dummy1.setLayout(new SpringLayout());
		
		dummy1.add(this.logYcheckBox);
		dummy1.add(this.showToCheckBox);
		dummy1.add(updateButton);
		dummy1.add(resetButton);
		
		SpringUtilities.makeCompactGrid(dummy1, 4, 1, 5, 5, 5, 5);
		
		
		JPanel dummy2 = new JPanel();
		dummy2.setLayout(new BoxLayout(dummy2,BoxLayout.Y_AXIS));
		dummy2.add(fieldsPanel);
		dummy2.add(dummy1);
		
		this.attributesPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Histogram plot parameters"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		this.attributesPanel.add(dummy2);
		
		plot();
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.X_AXIS));
		this.getContentPane().add(this.histoPanel);
		this.getContentPane().add(this.attributesPanel);
		
		
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width-this.getSize().width)/2;
		int y = (screenSize.height-this.getSize().height)/2;
		this.setLocation(x, y);
		
		this.addComponentListener(this);
		
	}
	
	private void plot() {
		
		this.numClasses = ((Integer) this.bucketsSpinner.getValue()).intValue();
		
		float min = ((Double) this.minSpinner.getValue()).floatValue();
		float max = ((Double) this.maxSpinner.getValue()).floatValue();
		
		
		if (min > max) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - Min vale > max value!");
		} else {
			this.histogram = new HistogramPlot(this.datas, this.numClasses, this.title, this.xLabel, this.yLabel, this.labels, this.showToCheckBox.isSelected(), true, this.logYcheckBox.isSelected(), min, max);
			
			int height = this.getSize().height - 40;
			int width = this.getSize().width - this.attributesPanel.getPreferredSize().width - 15;
			
			this.histoPanel.removeAll();
			this.histoPanel.add(this.histogram);
			
			this.histogram.setPreferredSize(new Dimension(width, height));
			
			this.histoPanel.paintImmediately(this.histoPanel.getBounds());
			this.histoPanel.updateUI();
			
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		
		String c = e.getActionCommand();
		if (c.equalsIgnoreCase("update")) {
			plot();
		} else if (c.equalsIgnoreCase("reset")) {
			this.minSpinner.setValue((double) this.absoluteMin);
			this.maxSpinner.setValue((double) this.absoluteMax);
			this.bucketsSpinner.setValue(this.numClassesStart);
			plot();
		}
		
	}


	public void componentResized(ComponentEvent arg0) {
		
		int height = this.getSize().height - 40;
		int width = this.getSize().width - this.attributesPanel.getPreferredSize().width - 15;
		
		this.histogram.setPreferredSize(new Dimension(width, height));
		
		this.histoPanel.paintImmediately(this.histoPanel.getBounds());
		this.histoPanel.updateUI();
		
	}
	
	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentShown(ComponentEvent arg0) {
	}
	
	

}







































