package org.cytoscape.neildhruva.chartapp.impl;

import java.util.List;
import javax.swing.JPanel;
import org.cytoscape.model.CyTable;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.cytoscape.neildhruva.chartapp.CytoChart;
import org.jfree.chart.ChartPanel;

public class CytoChartImpl implements CytoChart {

    private JPanel jpanel;
	private PanelComponents panelComponents;
	private ChartPanel myChartPanel;
    
	public CytoChartImpl(JPanel jpanel, ChartPanel myChartPanel, MyTableModel myTableModel, CyTable cyTable, AxisMode mode, PanelComponents panelComponents, PanelLayout panelLayout) {
		this.jpanel = jpanel;
		this.myChartPanel = myChartPanel;
		this.panelComponents = panelComponents;
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
	public List<String> getRows() {
		return panelComponents.getRows();
	}

	@Override
	public List<String> getColumns() {
		return panelComponents.getColumns();
	}
	
	@Override
	public void setRows(List<String> rowNames) {
		//if(jpanel.getName().equals("NULL"))
			//return;
		
		panelComponents.setRows(rowNames);
	}

	@Override
	public void setColumns(List<String> columnNames) {
		if(jpanel.getName().equals("NULL"))
			return;
		
		panelComponents.setColumns(columnNames);
		
	}

	@Override
	public void setCyTable(CyTable cyTable) {
		if(jpanel.getName().equals("NULL"))
			return;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAxisMode(AxisMode mode) {
		if(jpanel.getName().equals("NULL"))
			return;
		
	}

	@Override
	public void removeColumn(String columnName) {
		if(jpanel.getName().equals("NULL"))
			return;
		
		panelComponents.removeColumn(columnName);
	}

	@Override
	public void addColumn(String columnName) {
		if(jpanel.getName().equals("NULL"))
			return;
		
		panelComponents.addColumn(columnName);
	}

	@Override
	public void removeRow(String rowName) {
		if(jpanel.getName().equals("NULL"))
			return;
		
		panelComponents.removeRow(rowName);
	}

	@Override
	public void addRow(String rowName) {
		if(jpanel.getName().equals("NULL"))
			return;
		
		panelComponents.addRow(rowName);
	}

}
