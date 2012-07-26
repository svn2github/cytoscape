package org.cytoscape.neildhruva.chartapp.impl;

import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.model.CyTable;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.cytoscape.neildhruva.chartapp.CytoChart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class CytoChartImpl implements CytoChart {

	private JFreeChart chart;
    private JPanel jpanel;
	private CyTable cyTable;
	private AxisMode mode;
	private PanelComponents panelComponents;
	private PanelLayout panelLayout;
	private ChartPanel myChartPanel;
    
	public CytoChartImpl(JPanel jpanel, ChartPanel myChartPanel, MyTableModel myTableModel, CyTable cyTable, AxisMode mode, PanelComponents panelComponents, PanelLayout panelLayout) {
		this.jpanel = jpanel;
		this.myChartPanel = myChartPanel;
		this.cyTable = cyTable;
		this.mode = mode;
		this.panelComponents = panelComponents;
		this.panelLayout = panelLayout;
	}
	
	@Override
	public JPanel getJPanel() {
		return jpanel;
	}
	
	@Override
	public ChartPanel getChartPanel() {
		return myChartPanel;
	}

	@Override
	public void setWidth(int width) {
		panelLayout.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		panelLayout.setHeight(height);
		
	}

	@Override
	public void setRows(List<String> rows) {
		/*
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

        chart.getCategoryPlot().setDataset(dataset);
        */
	}

	@Override
	public void setColumns(String[] columns) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCyTable(CyTable cyTable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAxisMode(AxisMode mode) {
		
	}

	@Override
	public void removeColumn(String columnName) {
		panelComponents.removeColumn(columnName);
	}

	@Override
	public void addColumn(String columnName) {
		panelComponents.addColumn(columnName);
	}

	@Override
	public void removeRow(String rowName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addRow(String rowName) {
		// TODO Auto-generated method stub
		
	}

}
