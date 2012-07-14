package org.cytoscape.neildhruva.chartapp;

import java.util.Vector;

import javax.swing.JTable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CytoChart {

	private JFreeChart chart;
    private DefaultCategoryDataset dataset;
    Vector<String> plottableColumns;
    
	public CytoChart(JTable table) {
		
		//print all the columns that can be plotted on a graph
		MyTableModel myTableModel = (MyTableModel) table.getModel();
		this.plottableColumns = myTableModel.getPlottableColumns();
		for(int i=0;i<plottableColumns.size();i++){
			System.out.println(plottableColumns.get(i));
			
		}
	}
	
	/**
	 * Creates a chart/graph and puts it in a chart panel.
	 * 
	 * @return The <code>ChartPanel</code> that contains the newly created chart.
	 */
	public JFreeChart createChart(String chartType){
		
		if(chartType.equals("Bar Chart")){
			return BarChart();
		}
			
		else{
			return XYChart();
		}
		
	}
	
	/**
	 * Creates an XY Plot (Line Chart).
	 * @return Chart containing the XY plot.
	 */
	public JFreeChart XYChart() {
		
		// Create a simple XY chart
		XYSeries series = new XYSeries("XYGraph");
		series.add(1, 1);
		series.add(1, 2);
		series.add(2, 1);
		series.add(3, 9);
		series.add(4, 10);
		// Add the series to the data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		
		// Generate the graph
		chart = ChartFactory.createXYLineChart(
		"XY Chart", // Title
		"x-axis", // x-axis Label
		"y-axis", // y-axis Label
		dataset, // Dataset
		PlotOrientation.VERTICAL, // Plot Orientation
		true, // Show Legend
		true, // Use tooltips
		false // Configure chart to generate URLs?
		);
		
		
		return chart;
		
	}
	
	/**
	 * Creates a Bar Chart.
	 * @return The bar chart.
	 */
	public JFreeChart BarChart() {
		
		this.dataset = new DefaultCategoryDataset();
		
		dataset.setValue(6, "Profit", "Jane");
		dataset.setValue(7, "Profit", "Tom");
		dataset.setValue(8, "Profit", "Jill");
		dataset.setValue(5, "Profit", "John");
		dataset.setValue(12, "Profit", "Fred");
		chart = ChartFactory.createBarChart("Comparison between Salesman",
		"Salesman", "Profit", dataset, PlotOrientation.VERTICAL,
		false, true, false);
		
		return chart;
	}
	
	/**
	 * 
	 * @return The current chart.
	 */
	public JFreeChart getTheChart() {
		return this.chart;
	}
}
