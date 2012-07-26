package org.cytoscape.neildhruva.chartapp.impl;

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

	private final AxisMode DEFAULT_MODE = AxisMode.COLUMNS;
	private final int DEFAULT_HEIGHT = 600;
	private final int DEFAULT_WIDTH = 800;
	
	private CyTableFactory tableFactory;
	private CyNetworkTableManager cyNetworkTableManager;
	private CyTableManager cyTableManager;
	private JPanel jpanel;
	private ChartPanel myChartPanel=null;
	
	public ChartAppFactoryImpl(	CyTableFactory tableFactory,
								CyNetworkTableManager cyNetworkTableManager,
								CyTableManager cyTableManager) {

		this.tableFactory = tableFactory;
		this.cyNetworkTableManager = cyNetworkTableManager;
		this.cyTableManager = cyTableManager;
		
	}
	
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable) {
		return createChart(currentNetwork, cyTable, DEFAULT_MODE, DEFAULT_HEIGHT, DEFAULT_WIDTH, null, null);
	}
	
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode) {
		return createChart(currentNetwork, cyTable, mode, DEFAULT_HEIGHT, DEFAULT_WIDTH, null, null);
	}
	
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width){
		return createChart(currentNetwork, cyTable, mode, height, width, null, null);
	}
	
	public CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width, String[] rows, String[] columns){
		
		PanelLayout panelLayout = new PanelLayout();
		PanelComponents panelComponents = new PanelComponents(tableFactory, cyNetworkTableManager, cyTableManager);
		
		MyTableModel myTableModel = new MyTableModel(cyTable);
		//tableColumnCount is the count of the plottable columns - int, long, double
		int tableColumnCount = myTableModel.getColumnCount();
		
		if(tableColumnCount>0) {
			panelComponents.initComponents(cyTable, currentNetwork, mode, myTableModel);
			
			//get all components and send them to the panel layout class.
			JComboBox chartTypeComboBox = panelComponents.getComboBox();
			JCheckBox[] checkBoxArray = panelComponents.getCheckBoxArray();
			this.myChartPanel = panelComponents.getChartPanel();
			
			jpanel = panelLayout.initLayout(tableColumnCount, checkBoxArray, chartTypeComboBox, myChartPanel);
			
		} else {
			jpanel = panelLayout.nullJPanel();
		}
		
		CytoChart cytoChart = new CytoChartImpl(jpanel, myChartPanel, myTableModel, cyTable, mode, panelComponents, panelLayout);
		return cytoChart;
	}
	
}
