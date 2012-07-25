package org.cytoscape.neildhruva.chartapp.impl;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JTable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
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
	private AxisMode mode;
    
	public CytoChart(JTable table, CyTable cyTable, AxisMode mode) {
		this.table = table;
		this.cyTable = cyTable;
		this.mode = mode;
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
        
        List<CyRow> cyrows = cyTable.getAllRows();
        CyRow singleRow;
        
        if(mode.equals(AxisMode.COLUMNS)) {
        	for(int i=0; i<columnCount; i++) {
        		String columnName = table.getColumnName(i);
        		for(int j=0; j<rowCount; j++) {
        			singleRow = cyrows.get(j);
        			dataset.addValue(singleRow.get(columnName, Number.class),       //y-axis 
        							 columnName, 									    //label for the line
        						     singleRow.get(CyNetwork.NAME, String.class));  //x-axis		
        		}
        	}
        } else {
        	
        	//TODO: write code for AxisMode.ROWS
        	
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
	
	/**
	 * Sets the rows to be diplayed in the <code>JFreeChart</code>.
	 * @param rows The row values to be displayed in the <code>JFreeChart</code>.
	 * @return The <code>JFreeChart</code> displaying the specified rows.
	 */
	public JFreeChart setRows(List<String> rows) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int rowCount = table.getRowCount();
        int columnCount = table.getColumnCount();
        
        List<CyRow> cyrows = cyTable.getAllRows();
        CyRow singleRow;
        for(int i=0; i<columnCount; i++) {
        	String columnName = table.getColumnName(i);
        	for(int j=0; j<rowCount; j++) {
        		singleRow = cyrows.get(j);
        		if(rows.contains(singleRow.get(CyNetwork.NAME, String.class))) {
        			dataset.addValue(singleRow.get(columnName, Number.class),         //y-axis 
        							 columnName, 									  //label for the line
        							 singleRow.get(CyNetwork.NAME, String.class));    //x-axis
        			System.out.println(singleRow.get(CyNetwork.NAME, String.class));
        		}
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
}
