package org.cytoscape.neildhruva.chartapp.impl;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class PanelComponents {

	private final int DEFAULT_WIDTH = 800;
	private final int DEFAULT_HEIGHT = 600;
	
    private JTable table;
	private TableColumnModel tableColumnModel;
	private JCheckBox[] checkBoxArray;
	private int tableColumnCount;
	private CyTable myCyTable;
	private List<Boolean> checkBoxState;
	private List<String> columnNamesList;
	private JComboBox chartTypeComboBox;
	private CytoChart cytoChart;
	private JFreeChart chart;
	private ChartPanel myChartPanel;
	private CyTable cyTable;
	private CyTableFactory tableFactory;
	private CyNetworkTableManager cyNetworkTableMgr;
	private CyTableManager cyTableManager;
	
	public enum chartTypes {
		LINE {
		    public String toString() {
		        return "Line Chart";
		    }
		},
		HISTOGRAM {
		    public String toString() {
		        return "Histogram";
		    }
		}
	}
	
	public PanelComponents(CyTableFactory tableFactory,
							CyNetworkTableManager cyNetworkTableMgr,
							CyTableManager cyTableManager) {
		
		this.tableFactory = tableFactory;
		this.cyNetworkTableMgr = cyNetworkTableMgr;
		this.cyTableManager = cyTableManager;
    }
	
	/**
	 * Initializes an array of checkboxes with column names of the table as titles and
     * sets each checkbox checked/unchecked corresponding to the Boolean values in which track hidden columns.
     * The checkboxes allows user to check/uncheck a particular column.
	 * @param myCyTable The custom CyTable that stores information about hidden columns, type of chart and column names.
	 * @param cytable The CyTable supplied by the user.
	 */
    public void initComponents(CyTable cyTable, CyNetwork currentNetwork, JTable table, AxisMode mode){
		
    	this.myCyTable = cyNetworkTableMgr.getTable(currentNetwork, CyNetwork.class, "PrintTable "+cyTable.getTitle());;
    	this.cyTable = cyTable;
    	this.table = table;
    	this.tableColumnModel = table.getColumnModel();
    	this.tableColumnCount = table.getColumnCount();
    	
    	this.columnNamesList = new ArrayList<String>();
		this.checkBoxState = new ArrayList<Boolean>();
    	
		if(myCyTable!=null) {
			checkBoxState = myCyTable.getAllRows().get(0).getList("States", Boolean.class);
			columnNamesList = myCyTable.getAllRows().get(0).getList("Names", String.class);
		} else {
			
			for(int i=0; i<tableColumnCount; i++) {
				columnNamesList.add(table.getColumnName(i));
				checkBoxState.add(false);
			}
			
			//if myCyTable is null, create a new CyTable and associate it with the current network.
			myCyTable = tableFactory.createTable("PrintTable "+cyTable.getTitle(), CyIdentifiable.SUID, Long.class, true, true);
			myCyTable.createListColumn("Names", String.class, true);
			myCyTable.createListColumn("States", Boolean.class, true);
			myCyTable.createColumn("ChartType", String.class, true);
		
			//create a new row in myCyTable
			CyRow cyrow = myCyTable.getRow(currentNetwork.getSUID());
			cyrow.set("Names", columnNamesList);
			cyrow.set("States", checkBoxState);
			cyrow.set("ChartType", "Line Chart"); //default value is "Line Chart"
		
			//associate myCyTable with this network 
			cyNetworkTableMgr.setTable(currentNetwork, CyNetwork.class, "PrintTable "+cyTable.getTitle(), myCyTable);
			//add myCyTable to the CyTableManager in order to preserve it across sessions
			cyTableManager.addTable(myCyTable);
		}
		
		initCheckBoxArray();
		
		//hide all the columns that the user intends to hide in the JTable
        for(int i=0;i<tableColumnCount;i++){
        	if(!checkBoxState.get(i)) {
        		TableColumn column = tableColumnModel.getColumn(tableColumnModel.getColumnIndex(columnNamesList.get(i)));
        		tableColumnModel.removeColumn(column);
        	}
        }
        
        initJComboBox();
        initChartPanel(mode);
    }
    
    /**
     * Initializes the checkbox array containing column names.
     */
    public void  initCheckBoxArray() {
    	checkBoxArray = new JCheckBox[tableColumnCount];
    	
        for(int i=0;i<tableColumnCount;i++) {
        	checkBoxArray[i] = new JCheckBox();
        	checkBoxArray[i].setText(table.getColumnName(i));
        	checkBoxArray[i].setSelected(checkBoxState.get(i));
        	
        	final int j=i;
        	
        	//A listener is add to each checkbox so that when the corresponding
        	//checkbox is clicked, the hideColumn and showColumn methods can be
        	//invoked.
        	checkBoxArray[i].addItemListener(new ItemListener() {
				
        		@Override
				public void itemStateChanged(ItemEvent arg0) {
					if(!checkBoxArray[j].isSelected()){
						hideColumn(checkBoxArray[j].getText());
					}else{
						showColumn(checkBoxArray[j].getText());
					}
					
					refreshChartPanel(myCyTable.getAllRows().get(0).get("ChartType", String.class));
        	    	updateChartType(myCyTable.getAllRows().get(0).get("ChartType", String.class));
				}
        	});
        }
    }
    
    /**
     * Initializes JComboBox containing the types of charts.
     */
    public void initJComboBox() {
    	//initialize the JComboBox which selects the type of chart to be displayed.
        //every time it is changed, the graph changes as well
        if(chartTypeComboBox==null) {
        	chartTypeComboBox = new JComboBox(chartTypes.values());
        	chartTypeComboBox.setSelectedItem("Line Chart"); //by default
        	
        	chartTypeComboBox.addActionListener(new ActionListener () {
        	    public void actionPerformed(ActionEvent e) {
        	    	String chartType = ((JComboBox) e.getSource()).getSelectedItem().toString();
        	    	refreshChartPanel(chartType);
        	    	updateChartType(chartType);
        	    }
        	});
        	
        } else {
        	chartTypeComboBox.getModel().setSelectedItem(myCyTable.getAllRows().get(0).get("ChartType", String.class));
        }
    }
    
    /**
     * Initializes the Chart Panel which contains the chart.
     */
    public void initChartPanel(AxisMode mode) {
    	//initialize the chart
        this.cytoChart = new CytoChart(table, cyTable, mode);
		this.chart = cytoChart.createChart(chartTypeComboBox.getSelectedItem().toString());
		this.myChartPanel = new ChartPanel(chart);
		myChartPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		myChartPanel.setMouseWheelEnabled(true);
		
    }
    
    /**
     * Hides the column from the table view by removing it from the table column model.
     * @param columnName Name of the column that has to be hidden.
     */
    public void hideColumn(String columnName) {
    	
    	int columnIndex = tableColumnModel.getColumnIndex(columnName);
        TableColumn column = tableColumnModel.getColumn(columnIndex);
        
        columnIndex = columnNamesList.indexOf(columnName);
        checkBoxState.set(columnIndex, false);
        myCyTable.getAllRows().get(0).set("States", checkBoxState);
        
        tableColumnModel.removeColumn(column);
    }

    /**
     * Makes a column visible in the JTable.
     * @param columnName Name of the column that has to be made visible.
     */
	public void showColumn(String columnName) {
		
		int columnIndex = columnNamesList.indexOf(columnName);
		((MyTableModel) table.getModel()).fireTableStructureChanged();
		
		checkBoxState.set(columnIndex, true);
		
		//after calling fireTableStructureChanged(), the entire JTable is refreshed. This is done because
		//table.getAutoCreateColumnsFromModel() is true. So now, all columns corresponding to unchecked 
		//checkboxes need to be hidden.
		for(int i=0;i<tableColumnCount;i++) {
        	if(!checkBoxState.get(i)) {
        		hideColumn(columnNamesList.get(i));
        	}
        }
		
    }
	
	/**
	 * Update the chart type in the custom CyTable attached to the network.
	 * @param chartType One of the types of charts listed in {@link chartTypes}.
	 */
	public void updateChartType(String chartType) {
		myCyTable.getAllRows().get(0).set("ChartType", chartType);
	}
	
	/**
	 * @return The modified checkbox array after the user has selected/deselected
	 * 		   some checkboxes.
	 */
	public JCheckBox[] getCheckBoxArray(){
		return this.checkBoxArray;
	}
	
	/**
	 * @return The modified checkbox array after the user has selected/deselected
	 * 		   some checkboxes.
	 */
	public JComboBox getComboBox(){
		return this.chartTypeComboBox;
	}
	
	/**
	 * @return The modified JTable after some rows have been made invisible.
	 */
	public JTable getTable(){
		return this.table;
	}
	
	/**
	 * @return The chart panel.
	 */
	public ChartPanel getChartPanel(){
		return this.myChartPanel;
	}
	
	/**
	 * Sets the new chart within the {@link ChartPanel}.
	 */
	public void refreshChartPanel(String chartType) {
		chart = cytoChart.createChart(chartType);
		myChartPanel.setChart(chart); //this myChartPanel is the same ChartPanel that is displayed using PanelLayout.java
	}
	
	/**
	 * Sets rows of the <code>CyTable</code> displayed in the chart.
	 * @param rows The rows to be displayed in the chart.
	 */
	public void setRows(List<String> rows) {
		myChartPanel.setChart(cytoChart.setRows(rows));
	}
}
