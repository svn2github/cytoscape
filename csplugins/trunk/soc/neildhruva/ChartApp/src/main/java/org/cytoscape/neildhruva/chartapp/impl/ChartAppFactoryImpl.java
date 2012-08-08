package org.cytoscape.neildhruva.chartapp.impl;

import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;
import org.cytoscape.neildhruva.chartapp.CytoChart;

import org.jfree.chart.ChartPanel;

public class ChartAppFactoryImpl implements ChartAppFactory {

	private final AxisMode DEFAULT_MODE = AxisMode.ROWS;
	private CyTableFactory tableFactory;
	private CyTableManager cyTableManager;
	private JPanel jpanel;
	
	public ChartAppFactoryImpl(	CyTableFactory tableFactory, CyTableManager cyTableManager) {

		this.tableFactory = tableFactory;
		this.cyTableManager = cyTableManager;
		
	}
	
	@Override
	public CytoChart createChart(String chartName, CyTable cyTable) {
		return createChart(chartName, cyTable, DEFAULT_MODE, null, null);
	}
	
	@Override
	public CytoChart createChart(String chartName, CyTable cyTable, AxisMode mode) {
		return createChart(chartName, cyTable, mode, null, null);
	}
	
	@Override
	public CytoChart createChart(String chartName, CyTable cyTable, AxisMode mode, List<String> rows, List<String> columns){
		
		PanelLayout panelLayout = new PanelLayout();
		PanelComponents panelComponents = new PanelComponents(tableFactory, cyTableManager);
		
		MyTableModel myTableModel = new MyTableModel(cyTable);
		//tableColumnCount is the count of the plottable columns - int, long, double
		int tableColumnCount = myTableModel.getColumnCount();
		
		ChartPanel myChartPanel = null;
		if(tableColumnCount>0) {
			panelComponents.initComponents(cyTable, mode, myTableModel, rows, columns);
			
			//get all components and send them to the panel layout class.
			JComboBox chartTypeComboBox = panelComponents.getComboBox();
			JCheckBox[] checkBoxArray = panelComponents.getCheckBoxArray();
			myChartPanel  = panelComponents.getChartPanel();
			
			jpanel = panelLayout.initLayout(tableColumnCount, checkBoxArray, chartTypeComboBox, myChartPanel);
			
		} else {
			jpanel = panelLayout.nullJPanel();
		}
		
		CytoChart cytoChart = new CytoChartImpl(jpanel, myChartPanel, myTableModel, cyTable, mode, panelComponents, panelLayout);
		return cytoChart;
	}
	
	@Override
	public CytoChart getSavedChart(String chartName, CyTable cyTable) {
		
		//CyTable myCyTable = cyNetworkTableMgr.getTable(currentNetwork, CyNetwork.class, "CytoChart "+cyTable.getTitle());
		Iterator<CyTable> cyIterator = cyTableManager.getAllTables(true).iterator();
		CyTable myCyTable = null;
		while(cyIterator.hasNext()) {
			myCyTable = cyIterator.next();
			if(myCyTable.getTitle().equals("CytoChart "+cyTable.getTitle())) {
				break;
			}
		}
		
		//if the chart doesn't exist, create a new one
		if(myCyTable==null) {
			//TODO show error message that chart doesn't exist
			return createChart(chartName, cyTable);
		} else {
			PanelLayout panelLayout = new PanelLayout();
			PanelComponents panelComponents = new PanelComponents(tableFactory, cyTableManager);
		
			MyTableModel myTableModel = new MyTableModel(cyTable);
			
			//tableColumnCount is the count of the plottable columns - int, long, double
			int tableColumnCount = myTableModel.getColumnCount();
			panelComponents.reInitComponents(cyTable, myCyTable, myTableModel);
			
			//get all components and send them to the panel layout class.
			JComboBox chartTypeComboBox = panelComponents.getComboBox();
			JCheckBox[] checkBoxArray = panelComponents.getCheckBoxArray();
			ChartPanel myChartPanel = panelComponents.getChartPanel();
			AxisMode mode = panelComponents.getAxisMode();
			
			jpanel = panelLayout.initLayout(tableColumnCount, checkBoxArray, chartTypeComboBox, myChartPanel);
		
			CytoChart cytoChart = new CytoChartImpl(jpanel, myChartPanel, myTableModel, cyTable, mode, panelComponents, panelLayout);
			return cytoChart;
		}
	}
	
	@Override
	public Boolean isChartSaved(String chartName, CyTable cyTable) {
		
		Iterator<CyTable> cyIterator = cyTableManager.getGlobalTables().iterator();
		
		CyTable myCyTable = null;
		while(cyIterator.hasNext()) {
			
			myCyTable = cyIterator.next();
			
			if(myCyTable.getTitle().equals("CytoChart "+cyTable.getTitle())) {
				return true;
			}
		}
				
		return false;
		
	}
}
