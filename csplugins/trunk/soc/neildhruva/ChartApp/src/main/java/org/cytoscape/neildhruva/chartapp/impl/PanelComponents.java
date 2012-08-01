package org.cytoscape.neildhruva.chartapp.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

public class PanelComponents {

	private JCheckBox[] checkBoxArray;
	private int tableColumnCount;
	private CyTable myCyTable;
	private List<Boolean> checkBoxState;
	private List<String> columnNamesList;
	private JComboBox chartTypeComboBox;
	private JFreeChart chart;
	private ChartPanel myChartPanel;
	private CyTable cyTable;
	private CyTableFactory tableFactory;
	private CyNetworkTableManager cyNetworkTableMgr;
	private CyTableManager cyTableManager;
	private MyTableModel myTableModel;
	private AxisMode mode;
	private ArrayList<String> rowNamesList;
	private DefaultCategoryDataset lineChartDataset;
	
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
	 * Initializes a ChartPanel to display the <code>CyTable</code> along with an array of checkboxes with column names of the table 
	 * as titles and sets each checkbox checked/unchecked corresponding to the Boolean values in which track hidden columns. 
	 * It also initializes a <code>JComboBox</code> to select the type of chart.
     * The checkboxes allows user to check/uncheck a particular column.
	 * @param cyTable The <code>CyTable</code> to be plotted in the chart.
	 * @param currentNetwork The network to which the <code>CyTable</code> is attached.
	 * @param mode The {@link AxisMode}.
	 * @param myTableModel The custom table model meant for the <code>CyTable</code>.
	 * @param rowNames The Canonical names of <code>CyRows</code> to be plotted in the chart.
	 * @param columnNames The names of <code>CyColumns</code> to be plotted in the chart.
	 */
	public void initComponents(CyTable cyTable, CyNetwork currentNetwork, 
    						   AxisMode mode, MyTableModel myTableModel, 
    						   List<String> rowNames, List<String> columnNames) {
		
    	this.cyTable = cyTable;
    	this.myTableModel = myTableModel;
    	this.tableColumnCount = myTableModel.getColumnCount();
    	this.mode = mode;
    	
    	this.columnNamesList = new ArrayList<String>();
		this.checkBoxState = new ArrayList<Boolean>();
		this.rowNamesList = new ArrayList<String>();
    	
		if(columnNames==null) {
			for(int i=0; i<tableColumnCount; i++) {
				columnNamesList.add(myTableModel.getColumnName(i));
				checkBoxState.add(false);
			}
		} else {
			for(int i=0; i<tableColumnCount; i++) {
				String columnName = myTableModel.getColumnName(i);
				columnNamesList.add(columnName);
				if(columnNames.contains(columnName)) { 
					checkBoxState.add(true);
				}else
					checkBoxState.add(false);
			}
		}
		
		if(rowNames==null) {
			rowNamesList = (ArrayList<String>) cyTable.getColumn(CyNetwork.NAME).getValues(String.class);
		} else {
			for(String rowName : rowNames) {
				if(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).contains(rowName)) {
					rowNamesList.add(rowName);
				}
			}
		}
		
		myCyTable = tableFactory.createTable("CytoChart "+cyTable.getTitle(), CyIdentifiable.SUID, Long.class, true, true);
		myCyTable.createListColumn("Names", String.class, true);
		myCyTable.createListColumn("States", Boolean.class, true);
		myCyTable.createListColumn("RowNames", String.class, true);
		myCyTable.createColumn("ChartType", String.class, true);
		myCyTable.createColumn("AxisMode", String.class, true);
		
		//create a new row in myCyTable
		CyRow cyrow = myCyTable.getRow(currentNetwork.getSUID());
		cyrow.set("Names", columnNamesList);
		cyrow.set("RowNames", rowNamesList);
		cyrow.set("States", checkBoxState);
		cyrow.set("ChartType", "Line Chart"); //default value is "Line Chart"
		cyrow.set("AxisMode", mode.toString());
		
		//associate myCyTable with this network 
		cyNetworkTableMgr.setTable(currentNetwork, CyNetwork.class, "CytoChart "+cyTable.getTitle(), myCyTable);
		//add myCyTable to the CyTableManager in order to preserve it across sessions
		cyTableManager.addTable(myCyTable);
		
		initCheckBoxArray();
		initJComboBox();
		initChartPanel(mode);
    	
    }
    
    /**
	 * Reinitializes the chart, checkboxes and combobox.
	 * @param cytable The CyTable to be plotted on the chart.
	 * @param myCyTable The custom <code>CyTable</code> which stores information to reinitialize the chart.
	 * @param myTableModel The custom table model meant for the <code>CyTable</code>.
	 */
    public void reInitComponents(CyTable cyTable, CyTable myCyTable, MyTableModel myTableModel){
		
    	this.myCyTable = myCyTable;
    	this.cyTable = cyTable;
    	this.myTableModel = myTableModel;
    	this.tableColumnCount = myTableModel.getColumnCount();
    	
    	this.columnNamesList = new ArrayList<String>();
		this.checkBoxState = new ArrayList<Boolean>();
		this.rowNamesList = new ArrayList<String>();
    	
		checkBoxState = myCyTable.getAllRows().get(0).getList("States", Boolean.class);
		columnNamesList = myCyTable.getAllRows().get(0).getList("Names", String.class);
		rowNamesList = (ArrayList<String>) myCyTable.getAllRows().get(0).getList("RowNames", String.class);
		String modeString = myCyTable.getAllRows().get(0).get("AxisMode", String.class);
		
		if(modeString.equals(AxisMode.ROWS.toString())) {
			mode = AxisMode.ROWS;
		} else {
			mode = AxisMode.COLUMNS;
		}
			
		initCheckBoxArray();
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
        	checkBoxArray[i].setText(myTableModel.getColumnName(i));
        	checkBoxArray[i].setSelected(checkBoxState.get(i));
        	
        	final int j=i;
        	
        	//A listener is add to each checkbox so that when the corresponding
        	//checkbox is clicked, the removeColumn() and addColumn() methods can be
        	//invoked.
        	checkBoxArray[i].addItemListener(new ItemListener() {
				
        		@Override
				public void itemStateChanged(ItemEvent arg0) {
					if(!checkBoxArray[j].isSelected()){
						removeColumn(checkBoxArray[j].getText());
					}else{
						addColumn(checkBoxArray[j].getText());
					}
				}
        	});
        }
        
        //TODO Code for AxisMode.COLUMNS
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
     * @param mode The {@link AxisMode}.
     */
    public void initChartPanel(AxisMode mode) {
    	
    	//initialize the chart
        this.chart = generateChart(chartTypeComboBox.getSelectedItem().toString());
		this.myChartPanel = new ChartPanel(chart);
		myChartPanel.setPreferredSize(myChartPanel.getPreferredSize());
		//new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
		myChartPanel.setMouseWheelEnabled(true);
    			
    }
    
    /**
	 * Creates a chart/graph and puts it in a chart panel.
	 * @param chartType The type of chart to be created.
	 * @return The newly created chart.
	 */
	public JFreeChart generateChart(String chartType){
		if(chartType.equals("Line Chart")){
			
		    // create the dataset...
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
	        if(mode.equals(AxisMode.ROWS)) {
	        	for(int i=0; i<columnCount; i++) {
	        		String columnName = columnNames.get(i);
	        		for(String rowName : rowNamesList) {
	        			singleRow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
	        			lineChartDataset.addValue(singleRow.get(columnName, Number.class),       //y-axis 
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
	
    /**
     * Hides the column from the chart.
     * @param columnName Name of the column that has to be hidden.
     */
    public void removeColumn(String columnName) {
    	
    	if(!(columnNamesList.contains(columnName))) 
			return;
    	
    	checkBoxState.set(columnNamesList.indexOf(columnName), false);
        myCyTable.getAllRows().get(0).set("States", checkBoxState);
        CyRow singleRow;
        for(String rowName : rowNamesList) {
			singleRow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
			lineChartDataset.removeValue(columnName, 						   //label for the line
				                singleRow.get(CyNetwork.NAME, String.class));  //x-axis		
		
		}
        
        //TODO Code for AxisMode.COLUMNS
    }

    /**
     * Makes a column visible in the chart.
     * @param columnName Name of the column that has to be made visible.
     */
	public void addColumn(String columnName) {
		
		if(!(columnNamesList.contains(columnName))) 
			return;
		
		checkBoxState.set(columnNamesList.indexOf(columnName), true);
		myCyTable.getAllRows().get(0).set("States", checkBoxState);
		
        CyRow singleRow;
        for(String rowName : rowNamesList) {
			singleRow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
			lineChartDataset.addValue(singleRow.get(columnName, Number.class),       //y-axis 
							 columnName, 									    //label for the line
						     singleRow.get(CyNetwork.NAME, String.class));  //x-axis		
		}
        
        //TODO Code for AxisMode.COLUMNS
	}

    /**
     * Hides the row from the chart.
     * @param rowName Canonical name of the row that has to be hidden.
     */
    public void removeRow(String rowName) {
    	
    	//First check if CyRow corresponding to given Canonical name exists or not 
    	if(!(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).contains(rowName))) 
   			return;
    			
    	rowNamesList.remove(rowName);
    	
    	if(mode.equals(AxisMode.ROWS)) {
    		
    		int columnCount = columnNamesList.size();
			String columnName;
			for(int i=0;i<columnCount;i++) {
				if(checkBoxState.get(i)) {
					columnName = columnNamesList.get(i);
					lineChartDataset.removeValue(columnName,	//label for the line
						     					 rowName); 		//x-axis
					
				}
			}
					
    				
    	} else {
    		//TODO Code for AxisMode.COLUMNS
    	}
    
    }

    /**
     * Makes a row visible in the chart.
     * @param rowName Canonical name of the row that has to be made visible.
     */
	public void addRow(String rowName) {
		
		//First check if CyRow corresponding to given Canonical name exists or not 
		if(!(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).contains(rowName))) 
			return;
		
		rowNamesList.add(rowName);
		
		if(mode.equals(AxisMode.ROWS)) {
			
			CyRow cyrow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
			int columnCount = columnNamesList.size();
			String columnName;
			for(int i=0;i<columnCount;i++) {
				if(checkBoxState.get(i)) {
					columnName = columnNamesList.get(i);
					lineChartDataset.addValue(cyrow.get(columnName, Number.class),       //y-axis 
							 columnName, 									    		 //label for the line
						     rowName); 			   										 //x-axis
					
				}
			}
				
			
		} else {
			//TODO write code for mode=AxisMode.COLUMNS
		}
		
	}
	
	/**
	 * Sets rows of the <code>CyTable</code> displayed in the chart.
	 * @param rows The <code>List</code> of Canonical names of rows to be displayed in the chart.
	 */
	public void setRows(List<String> rows) {
		//TODO code for this
	}
	
	/**
	 * Sets columns of the <code>CyTable</code> displayed in the chart.
	 * @param columns The names of columns to be displayed in the chart.
	 */
	public void setColumns(List<String> columns) {
		int columnNamesLength = columnNamesList.size();
		String columnName;
		for(int i=0; i<columnNamesLength; i++) {
			columnName = columnNamesList.get(i);
			if(!checkBoxState.get(i) && columns.contains(columnName)) {
				addColumn(columnName);
			} else if (checkBoxState.get(i) && !columns.contains(columnName)) {
				removeColumn(columnName);
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
	 * @return The <code>JComboBox</code> used to select the chart type.
	 */
	public JComboBox getComboBox(){
		return this.chartTypeComboBox;
	}
	
	/**
	 * @return The chart panel.
	 */
	public ChartPanel getChartPanel(){
		return this.myChartPanel;
	}
	
	/**
	 * @return The {@link AxisMode}.
	 */
	public AxisMode getAxisMode(){
		return this.mode;
	}
	
	/**
	 * Sets the new chart within the {@link ChartPanel}.
	 */
	public void refreshChartPanel(String chartType) {
		chart = generateChart(chartType);
		myChartPanel.setChart(chart); //this myChartPanel is the same ChartPanel that is displayed using PanelLayout.java
	}

}
