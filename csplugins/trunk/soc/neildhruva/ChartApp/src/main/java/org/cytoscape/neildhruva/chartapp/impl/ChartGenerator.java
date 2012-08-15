package org.cytoscape.neildhruva.chartapp.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class ChartGenerator {

	private DefaultCategoryDataset lineChartDataset;
	private List<Boolean> checkBoxState;
	private List<String> columnNamesList;
	private List<String> rowNamesList;
	private AxisMode mode;
	private CyTable cyTable;
	private JFreeChart chart;
	private String chartType;
	
	public ChartGenerator() {
		
	}
	
	/**
	 * Creates a chart/graph and puts it in a chart panel.
	 * @param chartType The type of chart to be created.
	 * @return The newly created chart.
	 */
	public JFreeChart generateChart(String chartType){
		
		this.chartType = chartType;
		
		if(chartType.equals("Line Chart")){
		    
	        this.lineChartDataset = new DefaultCategoryDataset();
	        
	        List<String> columnNames = new ArrayList<String>();
	        int count = checkBoxState.size();
	        for(int i=0;i<count;i++) {
	        	if(checkBoxState.get(i)) {
	        		columnNames.add(columnNamesList.get(i));
	        	}
	        }
	        
	        int columnCount = columnNames.size();
	        CyRow singleRow;
	        
        	for(int i=0; i<columnCount; i++) {
        		String columnName = columnNames.get(i);
        		for(String rowName : rowNamesList) {
        			singleRow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
        			lineChartDataset.addValue(singleRow.get(columnName, Number.class),       //y-axis 
        							 columnName, 									    //label for the line
        						     singleRow.get(CyNetwork.NAME, String.class));  //x-axis		
        		}
        	}
	        
	     // create the chart...
	        chart = ChartFactory.createLineChart(
	            cyTable.getTitle(),       // chart title
	            "NAME",                    // domain axis label
	            "Value",                   // range axis label
	            lineChartDataset,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            true,                      // include legend
	            true,                      // tooltips
	            false                      // urls
	        );
	        
	    } else if(chartType.equals("Time Series")) {
	        	
	    	this.lineChartDataset = new DefaultCategoryDataset();
	        
	        List<String> rowNames = new ArrayList<String>();
	        int count = checkBoxState.size();
	        for(int i=0;i<count;i++) {
	        	if(checkBoxState.get(i)) {
	        		rowNames.add(rowNamesList.get(i));
	        	}
	        }
	        
	        int columnCount = columnNamesList.size();
	        CyRow singleRow;
	        String columnName;
        	for(String rowName : rowNames) {
        		singleRow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
        		for(int i=0; i<columnCount; i++) {
        			columnName = columnNamesList.get(i);
        			lineChartDataset.addValue(singleRow.get(columnName, Number.class),   //y-axis 
        							 rowName,		    								 //label for the line
        							 columnName);  										 //x-axis		
        		}
        	}
        
	        
	     // create the chart...
	        chart = ChartFactory.createLineChart(
	            cyTable.getTitle(),       // chart title
	            "Time",                    // domain axis label
	            "Value",                   // range axis label
	            lineChartDataset,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            true,                      // include legend
	            true,                      // tooltips
	            false                      // urls
	        );
	        
	        	
	    } else {
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
		
			
		}
		return chart;
	}
	
	public void setParameters(List<Boolean> checkBoxState, List<String> columnNamesList, List<String> rowNamesList, 
						      AxisMode mode, CyTable cyTable) {
		this.checkBoxState = checkBoxState;
		this.columnNamesList = columnNamesList;
		this.rowNamesList = rowNamesList;
		this.mode = mode;
		this.cyTable = cyTable;
		
	}
	
	public DefaultCategoryDataset getDataset() {
		return lineChartDataset;
		
	}
	
}
