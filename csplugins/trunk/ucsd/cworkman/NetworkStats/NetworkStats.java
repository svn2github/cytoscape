package csplugins.ucsd.cworkman.NetworkStats;

import java.util.Iterator;
import java.util.Vector;
import java.lang.Math;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Container;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.list.IntArrayList;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeObj;
import cytoscape.CyNetwork;
import cytoscape.data.GraphObjAttributes;
import cytoscape.plugin.CytoscapePlugin;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.IntervalXYDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.data.XYBarDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

public class NetworkStats extends CytoscapePlugin {
    GraphView graphView;
    CyNetwork network;
    CytoscapeObj cyObj;

    public NetworkStats () {
	this.cyObj = Cytoscape.getCytoscapeObj();
	Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( 
	    new AbstractAction("Display Network Statistics"){
		public void actionPerformed(ActionEvent ae){
		    graphView = Cytoscape.getCurrentNetworkView();
		    network = Cytoscape.getCurrentNetwork();
		    if (graphView == null || network == null) {return;}
		    Thread t = new NetworkStatsThread();
		    t.start();
		}});
    }

    /**
     * NetworkStatsThread class to be spawned by super
     * (no arguments)
     */
    class NetworkStatsThread extends Thread {
	DoubleArrayList nodeDegrees;
	JFrame nsFrame;
	ChartPanel chartPanel = null;
	OpenIntIntHashMap degreeHash;
        Color bgColor = new Color(216, 216, 216);

	/**
	 * run method creates the JFrame and all it's JPanels
	 *
	 */
	public void run(){
	    nodeDegrees = new DoubleArrayList(network.getNodeCount());
	    nsFrame = new JFrame("Network Statistics");
	    degreeHash = new OpenIntIntHashMap();

	    Iterator nodeIt = network.nodesIterator();
	    GraphObjAttributes eAttr = network.getEdgeAttributes();
	    int nodeCount = 0;
	    int edgeCount, val;

	    while(nodeIt.hasNext()){
		Node current = (Node)nodeIt.next();
		Iterator edgeIt = network.getAdjacentEdgesList(current, true, true, true).iterator();
		edgeCount = 0;
		while(edgeIt.hasNext()){
		    edgeCount++;
		    Edge currentEdge = (Edge)edgeIt.next();
		    String interaction = (String)eAttr.get("interaction",
		          eAttr.getCanonicalName(currentEdge));
		    //System.out.println("EDGE "+interaction);
		}
		degreeHash.put(edgeCount, degreeHash.get(edgeCount)+1);
		nodeDegrees.add((double)edgeCount);
	    }

	    JMenuBar nsMenu = new JMenuBar();
            JMenu nsPlotMenu = new JMenu("Plot");
	    JMenuItem cBarOption  = new JMenuItem( new BarChartCCAction() );
	    //JMenuItem lBarOption  = new JMenuItem( new BarChartLCAction() );
	    //JMenuItem cHistOption = new JMenuItem( new HistogramCCAction() );
	    JMenuItem lHistOption = new JMenuItem( new HistogramLCAction() );

	    nsPlotMenu.add(cBarOption);
	    //nsPlotMenu.add(lBarOption);
	    //nsPlotMenu.add(cHistOption);
	    nsPlotMenu.add(lHistOption);
	    nsMenu.add(nsPlotMenu);

	    JPanel statsPanel = new JPanel();
 	    addLabelTextRows(statsPanel);

	    // load the stats fields
	    DoubleArrayList statVals = new DoubleArrayList(4);
	    statVals.add((double)Descriptive.median(nodeDegrees));
	    statVals.add((double)Descriptive.mean(nodeDegrees));
	    statVals.add((double)Descriptive.min(nodeDegrees));
	    statVals.add((double)Descriptive.max(nodeDegrees));

	    for(int i=1, j=0; i<statsPanel.getComponentCount(); i+=2, j++)
		((JTextComponent)statsPanel.getComponent(i)).setText(""+statVals.get(j));

	    JPanel buttonPanel = new JPanel();
	    JButton OK = new JButton("OK");
	    OK.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent ae){
			nsFrame.dispose();
		    }});
	    buttonPanel.add(OK);

	    buttonPanel.setBackground(bgColor);
	    statsPanel.setBackground(bgColor);
	    nsFrame.setBackground(bgColor);

            nsFrame.setJMenuBar(nsMenu);
	    nsFrame.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
	    nsFrame.getContentPane().add(statsPanel,BorderLayout.WEST);

	    plotBarData("Node Degree Distribution", "degree", "nodes", false);
	}

	private void addLabelTextRows(JPanel container) {

	    String meaFieldString = "Mean";
	    String medFieldString = "Median";
	    String minFieldString = "Min";
	    String maxFieldString = "Max";
	    JTextField meaField = new JTextField(5);
	    JTextField medField = new JTextField(5);
	    JTextField minField = new JTextField(5);
	    JTextField maxField = new JTextField(5);
	    JLabel meaFieldLabel = new JLabel(meaFieldString+": ");
	    JLabel medFieldLabel = new JLabel(medFieldString+": ");
	    JLabel minFieldLabel = new JLabel(minFieldString+": ");
	    JLabel maxFieldLabel = new JLabel(maxFieldString+": ");
	    meaFieldLabel.setLabelFor(meaField);
	    medFieldLabel.setLabelFor(medField);
	    minFieldLabel.setLabelFor(minField);
	    maxFieldLabel.setLabelFor(maxField);
 	    //meaField.setActionCommand(meaFieldString);

	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();

	    container.setLayout(gridbag);
	    JLabel[] labels = {medFieldLabel, meaFieldLabel, minFieldLabel, maxFieldLabel};
	    JTextField[] textFields = {medField, meaField, minField, maxField};

	    c.anchor = GridBagConstraints.EAST;
	    int numLabels = labels.length;
	    
	    for (int i = 0; i < numLabels; i++) {
		c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
		c.fill = GridBagConstraints.NONE;      //reset to default
		c.weightx = 0.0;                       //reset to default
		container.add(labels[i], c);
		
		c.gridwidth = GridBagConstraints.REMAINDER;     //end row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		container.add(textFields[i], c);
	    }
	    container.setBorder(BorderFactory.createTitledBorder("Summary"));
	    //container.setBorder(
            //    BorderFactory.createCompoundBorder(
            //                    BorderFactory.createTitledBorder("Summary"),
            //                    BorderFactory.createEmptyBorder(5,5,5,5)));
	}

	private IntervalXYDataset createXYBarDataset(String seriesLabel, boolean logSwtch) {
	    double barWidth = 1.0;
 	    IntArrayList dCounts = new IntArrayList();
	    XYSeries s = new XYSeries(seriesLabel, false, false);
	    degreeHash.keys(dCounts);
            dCounts.reverse();
            for(int i=0; i<dCounts.size(); i++) {
		// System.err.println("DEGREE "+dCounts.get(i)+" "+degreeHash.get(dCounts.get(i)));
		if(logSwtch) s.add(Math.log((double)dCounts.get(i)), degreeHash.get(dCounts.get(i)));
		else s.add(dCounts.get(i), degreeHash.get(dCounts.get(i)));
	    }
	    final XYSeriesCollection collection = new XYSeriesCollection();
	    collection.addSeries(s);
	    if(logSwtch) barWidth = 0.2;
	    return new XYBarDataset(collection, barWidth);
	}

        public void plotBarData(String chartTitle, String xAxisLabel, String yAxisLabel, boolean logSwtch){

	    IntervalXYDataset dataset = createXYBarDataset("degree", logSwtch);
	    JFreeChart chart = ChartFactory.createXYBarChart(
				 chartTitle, xAxisLabel, false, yAxisLabel,
				 dataset, PlotOrientation.VERTICAL,   // orientation
				 false, false, false);  // legend, tootltips, URL 
	    setChart(chart);
	}

        public void plotHistogramData(DoubleArrayList d, int bins, 
				      String chartTitle, String xAxisLabel, String yAxisLabel) {
	    HistogramDataset dataset = new HistogramDataset();
	    dataset.setType(HistogramType.FREQUENCY); // FREQUENCY, RELATIVE_FREQUENCY or SCALE_AREA_TO_1
	    dataset.addSeries("degree", d.elements(), bins);
	    JFreeChart chart = ChartFactory.createHistogram(
					   chartTitle, xAxisLabel, yAxisLabel,
	    				   dataset, PlotOrientation.VERTICAL,   // orientation
					   false, false, false);  // legend, tootltips, URL 
	    setChart(chart);
	}

	/*
	 * Set or reset the chart in the JFrame
	 *
	 */
	public void setChart(JFreeChart chart){

	    //chart.getXYPlot().setForegroundAlpha(0.75f);
	    if (chartPanel == null){
		chartPanel = new ChartPanel(chart);
		//nsFrame.getContentPane().add(chartPanel,BorderLayout.CENTER);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		//chartPanel.setVisible(true);
		//chartPanel.setBorder(
		//	     BorderFactory.createCompoundBorder(
                //                BorderFactory.createTitledBorder("Node Connectivity"),
                //                BorderFactory.createEmptyBorder(5,5,5,5)));
		//chartPanel.setBorder(BorderFactory.createTitledBorder("Node Connectivity"));
	    }
	    else { 
		chartPanel.setChart(chart);
	    }

	    nsFrame.getContentPane().add(chartPanel,BorderLayout.CENTER);
	    nsFrame.pack();
	    nsFrame.setVisible(true);
	    chart.setBackgroundPaint(bgColor);
	}

	public class BarChartCCAction extends AbstractAction {
	    public BarChartCCAction() {super("degree histogram"); }
	    public void actionPerformed(ActionEvent ae) {
		plotBarData("Node Degree Distribution", "degree", "nodes", false);
	    }
	}

	public class BarChartLCAction extends AbstractAction {
	    public BarChartLCAction() {super("log degree histogram"); }
	    public void actionPerformed(ActionEvent ae) {
		plotBarData("Node Degree Distribution", "log10 degree", "nodes", true);
	    }
	}

	public class HistogramCCAction extends AbstractAction {
	    public HistogramCCAction() {super("degree histogram"); }
	    public void actionPerformed(ActionEvent ae) {
		int bins = (int)Descriptive.max(nodeDegrees)-(int)Descriptive.min(nodeDegrees)+1;
		plotHistogramData(nodeDegrees, bins, "Node Degree Distribution", "degree", "nodes");
	    }
	}

	public class HistogramLCAction extends AbstractAction {
	    public HistogramLCAction() {super("log degree histogram"); }
	    public void actionPerformed(ActionEvent ae) {
		DoubleArrayList logNodeDegrees = new DoubleArrayList(nodeDegrees.size());
		for(int i=0; i<nodeDegrees.size(); i++)
		    logNodeDegrees.add(Math.log((double)nodeDegrees.get(i))/Math.log(10.0));
		int bins = 4 * Math.round((float)Math.ceil(Descriptive.max(logNodeDegrees)));
		plotHistogramData(logNodeDegrees, bins, "Node Degree Distribution", "log10 degree", "nodes");
	    }
	}
    }
}

