package org.cytoscape.neildhruva.chartapp.impl;

import java.util.Random;
import java.util.Vector;

import javax.swing.JTable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

public class CytoChart {

	private JFreeChart chart;
    Vector<String> plottableColumns;
	private JTable table;
	private CyTable cyTable;
    
	public CytoChart(JTable table, CyTable cyTable) {
		this.table = table;
		this.cyTable = cyTable;
	}
	
	/**
	 * Creates a chart/graph and puts it in a chart panel.
	 * 
	 * @return The <code>ChartPanel</code> that contains the newly created chart.
	 */
	public JFreeChart createChart(String chartType){
		if(chartType.equals("Line Chart")){
			return XYChart();
		} else {
			return plotHistogram();
		}
	}
	
	/**
	 * Creates an XY Plot (Line Chart).
	 * @return Chart containing the XY plot.
	 */
	public JFreeChart XYChart() {
		
	    // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int rowCount = table.getRowCount();
        int columnCount = table.getColumnCount();
        
        
        for(int i=0; i<columnCount; i++) {
        	String columnName = table.getColumnName(i);
        	for(int j=0; j<rowCount; j++) {
        		dataset.addValue(cyTable.getAllRows().get(j).get(columnName, Number.class),      //y-axis 
        						 columnName, 													 //label for the line
        						 cyTable.getAllRows().get(j).get(CyNetwork.NAME, String.class)); //x-axis		
        	}
        }
        
        // create the chart...
        chart = ChartFactory.createLineChart(
            cyTable.getTitle(),       // chart title
            "NAME",                    // domain axis label
            "Value",                   // range axis label
            dataset,                   // data
            PlotOrientation.VERTICAL,  // orientation
            true,                      // include legend
            true,                      // tooltips
            false                      // urls
        );
        chart.getCategoryPlot().setNoDataMessage("Please select a column");
		return chart;
	}
	
	
	/**
	 * Creates a Histogram.
	 * @return A <code>JFreeChart</code> containing the histogram.
	 */
	public JFreeChart plotHistogram() {
		
		double[] value = new double[100];
	       Random generator = new Random();
	       for (int i=1; i < 100; i++) {
	       value[i] = generator.nextDouble();
	           int number = 10;
	       HistogramDataset dataset = new HistogramDataset();
	       dataset.setType(HistogramType.RELATIVE_FREQUENCY);
	       dataset.addSeries("Histogram",value,number);
	       
	       String plotTitle = "Sample Random Number Histogram"; 
	       String xaxis = "number";
	       String yaxis = "value"; 
	       PlotOrientation orientation = PlotOrientation.VERTICAL; 
	       chart = ChartFactory.createHistogram( plotTitle, xaxis, yaxis, 
	    		   								 dataset, orientation, true, 
	    		   								 true, false);
		}
	       return chart;
	}
}
