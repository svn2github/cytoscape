package org.cytoscape.sample.internal;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class CytoChart {

	private JFreeChart chart;
    private ChartPanel myChart;
    private DefaultCategoryDataset dataset;
    
	public CytoChart() {
		
		dataset = new DefaultCategoryDataset();			
	}
	
	/**
	 * Creates a chart/graph and puts it in a chart panel.
	 * 
	 * @return The <code>ChartPanel</code> that contains the newly created chart.
	 */
	public ChartPanel createChart(){
		dataset.setValue(6, "Profit", "Jane");
		dataset.setValue(7, "Profit", "Tom");
		dataset.setValue(8, "Profit", "Jill");
		dataset.setValue(5, "Profit", "John");
		dataset.setValue(12, "Profit", "Fred");
		chart = ChartFactory.createBarChart("Comparison between Salesman",
		"Salesman", "Profit", dataset, PlotOrientation.VERTICAL,
		false, true, false);
		myChart = new ChartPanel(chart);
		myChart.setMouseWheelEnabled(true);
		return myChart;
	}
}
