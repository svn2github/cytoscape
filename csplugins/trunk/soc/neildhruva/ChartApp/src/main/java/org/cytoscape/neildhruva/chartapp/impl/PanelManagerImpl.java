package org.cytoscape.neildhruva.chartapp.impl;

import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.model.CyTable;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.cytoscape.neildhruva.chartapp.PanelManager;
import org.jfree.chart.ChartPanel;

public class PanelManagerImpl implements PanelManager {

	private PanelComponents panelComponents;
	private PanelLayout panelLayout;

	public PanelManagerImpl(PanelComponents panelComponents, PanelLayout panelLayout) {
		this.panelComponents = panelComponents;
		this.panelLayout = panelLayout;
	}
	
	@Override
	public JPanel getJPanel() {
		return panelLayout.getJPanel(); 
	}

	@Override
	public ChartPanel getChartPanel() {
		return panelComponents.getChartPanel();
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
		panelComponents.setRows(rows);
		
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

}
