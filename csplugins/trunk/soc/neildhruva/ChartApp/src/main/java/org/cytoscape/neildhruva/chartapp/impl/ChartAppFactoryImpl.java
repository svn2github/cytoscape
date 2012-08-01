package org.cytoscape.neildhruva.chartapp.impl;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;
import org.cytoscape.neildhruva.chartapp.CytoChart;

import org.jfree.chart.ChartPanel;

public class ChartAppFactoryImpl implements ChartAppFactory {

	private final AxisMode DEFAULT_MODE = AxisMode.ROWS;
	private final int DEFAULT_HEIGHT = 600;
	private final int DEFAULT_WIDTH = 800;
	
	private CyTableFactory tableFactory;
	private CyNetworkTableManager cyNetworkTableMgr;
	private CyTableManager cyTableManager;
	private JPanel jpanel;
	
	public ChartAppFactoryImpl(	CyTableFactory tableFactory,
								CyNetworkTableManager cyNetworkTableMgr,
								CyTableManager cyTableManager) {

		this.tableFactory = tableFactory;
		this.cyNetworkTableMgr = cyNetworkTableMgr;
		this.cyTableManager = cyTableManager;
		
	}
	
	@Override
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable) {
		return createChart(currentNetwork, cyTable, DEFAULT_MODE, DEFAULT_HEIGHT, DEFAULT_WIDTH, null, null);
	}
	
	@Override
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode) {
		return createChart(currentNetwork, cyTable, mode, DEFAULT_HEIGHT, DEFAULT_WIDTH, null, null);
	}
	
	@Override
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width){
		return createChart(currentNetwork, cyTable, mode, height, width, null, null);
	}
	
	@Override
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width, List<String> rows, List<String> columns){
		
		//if the chart corresponding to this cyTable+currentNetwork combination exists, delete it
		CyTable myCyTable = cyNetworkTableMgr.getTable(currentNetwork, CyNetwork.class, "CytoChart "+cyTable.getTitle());
		if(myCyTable != null) {
			cyNetworkTableMgr.removeTable(currentNetwork, CyNetwork.class, "CytoChart "+cyTable.getTitle());
			cyTableManager.deleteTable(myCyTable.getSUID());
		}
		
		PanelLayout panelLayout = new PanelLayout();
		PanelComponents panelComponents = new PanelComponents(tableFactory, cyNetworkTableMgr, cyTableManager);
		
		MyTableModel myTableModel = new MyTableModel(cyTable);
		//tableColumnCount is the count of the plottable columns - int, long, double
		int tableColumnCount = myTableModel.getColumnCount();
		
		ChartPanel myChartPanel = null;
		if(tableColumnCount>0) {
			panelComponents.initComponents(cyTable, currentNetwork, mode, myTableModel, rows, columns);
			
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
	public CytoChart getSavedChart(CyNetwork currentNetwork, CyTable cyTable) {
		
		CyTable myCyTable = cyNetworkTableMgr.getTable(currentNetwork, CyNetwork.class, "CytoChart "+cyTable.getTitle());
		//if the chart doesn't exist, create a new one
		if(myCyTable==null) {
			//TODO show error message that chart doesn't exist
			return createChart(currentNetwork, cyTable);
		} else {
			PanelLayout panelLayout = new PanelLayout();
			PanelComponents panelComponents = new PanelComponents(tableFactory, cyNetworkTableMgr, cyTableManager);
		
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
	public Boolean isChartSaved(CyTable cyTable, CyNetwork currentNetwork) {
		if(cyNetworkTableMgr.getTable(currentNetwork, CyNetwork.class, "CytoChart "+cyTable.getTitle()) != null) {
			return true;
		} else {
			return false;
		}
	}
}
