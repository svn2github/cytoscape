package org.cytoscape.neildhruva.chartapp.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class PanelComponents {

	private JCheckBox[] checkBoxArray;
	private int tableColumnCount;
	private CyTable myCyTable = null;
	private List<Boolean> checkBoxState;
	private List<String> columnNamesList;
	private List<String> rowNamesList;
	private JComboBox chartTypeComboBox;
	private JFreeChart chart;
	private ChartPanel myChartPanel = null;
	private CyTable cyTable;
	private CyTableFactory tableFactory;
	private CyTableManager cyTableManager;
	private MyTableModel myTableModel;
	private AxisMode mode;
	private DefaultCategoryDataset lineChartDataset;
	private ChartGenerator chartGenerator;
	private int tableRowCount;
	private PanelLayout panelLayout;
	
	public enum ChartTypes {
		LINE {
		    public String toString() {
		        return "Line Chart";
		    }
		},
		HISTOGRAM {
		    public String toString() {
		        return "Histogram";
		    }
		},
		TIMESERIES {
			public String toString() {
				return "Time Series";
			}
		}
		
	}
	
	public PanelComponents(CyTableFactory tableFactory, CyTableManager cyTableManager, PanelLayout panelLayout) {
		
		this.tableFactory = tableFactory;
		this.cyTableManager = cyTableManager;
		this.panelLayout = panelLayout;
		this.chartGenerator = new ChartGenerator();
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
	public void initComponents(CyTable cyTable, AxisMode mode, MyTableModel myTableModel, 
    						   List<String> rowNames, List<String> columnNames, String chartType) {
		
		
		
    	this.cyTable = cyTable;
    	this.myTableModel = myTableModel;
    	this.tableColumnCount = myTableModel.getColumnCount();
    	this.mode = mode;
    	
    	this.columnNamesList = new ArrayList<String>();
		this.checkBoxState = new ArrayList<Boolean>();
		this.rowNamesList = new ArrayList<String>(cyTable.getRowCount());
    	
		if(mode.equals(AxisMode.ROWS)) {
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
				rowNamesList = new ArrayList<String>();
			} else {
				for(String rowName : rowNames) {
					if(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).contains(rowName)) {
						rowNamesList.add(rowName);
					}
				}
				
			}
		
		} else {
			rowNamesList = myTableModel.getPlottableRows();
			tableRowCount = rowNamesList.size();
			
			if(columnNames==null) {
				for(int i=0; i<tableColumnCount; i++) {
					columnNamesList.add(myTableModel.getColumnName(i));
				}
			} else {
				for(String columnName : columnNames) {
					if(cyTable.getColumn(columnName)!=null)
						columnNamesList.add(columnName);
				}
			}
			
			if(rowNames==null) {
				for(int i=0; i<tableRowCount; i++) {
					checkBoxState.add(false);
				}
			} else {
				for(String rowName : rowNamesList) {
					if(rowNames.contains(rowName)) {
						checkBoxState.add(true);
					} else {
						checkBoxState.add(false);
					}
				}
				
			}
			
		}
		
		if(myCyTable!=null) {
			cyTableManager.deleteTable(myCyTable.getSUID());
		}
		
		myCyTable = tableFactory.createTable("CytoChart "+cyTable.getTitle(), CyIdentifiable.SUID, Long.class, true, true);
		myCyTable.createListColumn("Names", String.class, true); //Column Names
		myCyTable.createListColumn("States", Boolean.class, true);
		myCyTable.createListColumn("RowNames", String.class, true);
		myCyTable.createColumn("ChartType", String.class, true);
		myCyTable.createColumn("AxisMode", String.class, true);
		
		//create a new row in myCyTable
		CyRow cyrow = myCyTable.getRow(cyTable.getSUID());
		cyrow.set("Names", columnNamesList);
		cyrow.set("RowNames", rowNamesList);
		cyrow.set("States", checkBoxState);
		if(chartType==null) {
			chartType = "Line Chart";
		}
		cyrow.set("ChartType", chartType); //default value is "Line Chart"
		cyrow.set("AxisMode", mode.toString());
		
		//add myCyTable to the CyTableManager in order to preserve it across sessions
		cyTableManager.addTable(myCyTable);
		
		myCyTable.setSavePolicy(SavePolicy.SESSION_FILE);
		
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
		rowNamesList = myCyTable.getAllRows().get(0).getList("RowNames", String.class);
		String modeString = myCyTable.getAllRows().get(0).get("AxisMode", String.class);
		String chartType = myCyTable.getAllRows().get(0).get("ChartType", String.class);
		
		this.tableRowCount = rowNamesList.size();
		
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
    	
    	if(mode.equals(AxisMode.ROWS)) {
	    	
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
	        
    	} else {
    		
    		checkBoxArray = new JCheckBox[tableRowCount];
    		
	        for(int i=0;i<tableRowCount;i++) {
	        	checkBoxArray[i] = new JCheckBox();
	        	checkBoxArray[i].setText(rowNamesList.get(i));
	        	checkBoxArray[i].setSelected(checkBoxState.get(i));
	        	
	        	final int j=i;
	        	
	        	//A listener is add to each checkbox so that when the corresponding
	        	//checkbox is clicked, the removeColumn() and addColumn() methods can be
	        	//invoked.
	        	checkBoxArray[i].addItemListener(new ItemListener() {
					
	        		@Override
					public void itemStateChanged(ItemEvent arg0) {
						if(!checkBoxArray[j].isSelected()){
							removeRow(checkBoxArray[j].getText());
						}else{
							addRow(checkBoxArray[j].getText());
						}
					}
	        	});
	        }
    	}
        
    }
    
    /**
     * Initializes JComboBox containing the types of charts.
     */
    public void initJComboBox() {
    	//initialize the JComboBox which selects the type of chart to be displayed.
        //every time it is changed, the graph changes as well
        if(chartTypeComboBox==null) {
        	chartTypeComboBox = new JComboBox(ChartTypes.values());
        	if(mode.equals(AxisMode.ROWS)) {
        		chartTypeComboBox.setSelectedItem("Line Chart");
        	} else {
        		chartTypeComboBox.setSelectedItem("Time Series");
        	}
        	chartTypeComboBox.addActionListener(new ActionListener () {
        	    public void actionPerformed(ActionEvent e) {
        	    	String chartType = ((JComboBox) e.getSource()).getSelectedItem().toString();
        	    	resetChartPanel(chartType);
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
    	
    	chartGenerator.setParameters(checkBoxState, columnNamesList, rowNamesList, mode, cyTable);
    	this.chart = chartGenerator.generateChart(chartTypeComboBox.getSelectedItem().toString());
		if(myChartPanel==null) {
			myChartPanel = new ChartPanel(chart);
			myChartPanel.setPreferredSize(myChartPanel.getPreferredSize());
			myChartPanel.setMouseWheelEnabled(true);
		} else {
			myChartPanel.setChart(chart);
		}
		lineChartDataset = (DefaultCategoryDataset) chartGenerator.getDataset();
				
    }
    
    
	/**
     * Hides the column from the chart.
     * @param columnName Name of the column that has to be hidden.
     */
    public void removeColumn(String columnName) {
    	
    	if(!(columnNamesList.contains(columnName))) 
			return;
    	
    	if(mode.equals(AxisMode.ROWS)) {
	    	checkBoxState.set(columnNamesList.indexOf(columnName), false);
	        myCyTable.getAllRows().get(0).set("States", checkBoxState);
	        for(String rowName : rowNamesList) {
				lineChartDataset.removeValue(columnName, 						   //label for the line
											 rowName);								//x-axis
	        }
	        
    	} else {
    		columnNamesList.remove(columnName);
    		int rowCount = rowNamesList.size();
			for(int i=0;i<rowCount;i++) {
				if(checkBoxState.get(i)) {
					lineChartDataset.removeValue(rowNamesList.get(i),	//label for the line
						     					 columnName); 			//x-axis
					
				}
			}
    		
    	}
        
    }

    /**
     * Makes a column visible in the chart.
     * @param columnName Name of the column that has to be made visible.
     */
	public void addColumn(String columnName) {
		
		if(mode.equals(AxisMode.ROWS)) {
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
	        
		} else {
			
			columnNamesList.add(columnName);
			
		    int rowCount = rowNamesList.size();
			String rowName;
			CyRow cyrow;
			for(int i=0;i<rowCount;i++) {
				if(checkBoxState.get(i)) {
					rowName = rowNamesList.get(i);
					cyrow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
					lineChartDataset.addValue(cyrow.get(columnName, Number.class),      //y-axis 
							 rowName, 									    		    //label for the line
						     columnName); 			   									//x-axis
					
				}
			}
		}
        
        
	}

    /**
     * Hides the row from the chart.
     * @param rowName Canonical name of the row that has to be hidden.
     */
    public void removeRow(String rowName) {
    	
    	//First check if CyRow corresponding to given Canonical name exists or not 
    	if(!(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).contains(rowName))) 
   			return;
    	
		if(mode.equals(AxisMode.ROWS)) {
			rowNamesList.remove(rowName);	
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
    		if(!rowNamesList.contains(rowName))
    			return;
    		
    		checkBoxState.set(rowNamesList.indexOf(rowName), false);
	        myCyTable.getAllRows().get(0).set("States", checkBoxState);
	        for(String columnName : columnNamesList) {
				lineChartDataset.removeValue(rowName, 						   //label for the line
											 columnName);								//x-axis
	        }
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
		
		if(mode.equals(AxisMode.ROWS)) {
			rowNamesList.add(rowName);
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
			if(!rowNamesList.contains(rowName))
    			return;
    		
			if(!(rowNamesList.contains(rowName))) 
				return;
			
			checkBoxState.set(rowNamesList.indexOf(rowName), true);
			myCyTable.getAllRows().get(0).set("States", checkBoxState);
			String columnName;
		    CyRow singleRow = cyTable.getAllRows().get(cyTable.getColumn(CyNetwork.NAME).getValues(String.class).indexOf(rowName));
		    for(int i=0; i<tableColumnCount; i++) {
    			columnName = columnNamesList.get(i);
    			lineChartDataset.addValue(singleRow.get(columnName, Number.class),   //y-axis 
    							 rowName,		    								 //label for the line
    							 columnName);  										 //x-axis		
    		}
	        
		}
		
	}
	
	/**
	 * Sets rows of the <code>CyTable</code> displayed in the chart.
	 * @param rows The <code>List</code> of Canonical names of rows to be displayed in the chart.
	 */
	public void setRows(List<String> rowNames) {
		
		List<String> tempList = new ArrayList<String>();
		int rowCount = rowNamesList.size();
		for(int i=0;i<rowCount;i++) {
			tempList.add(rowNamesList.get(i));
		}
			
		for(String rowName : tempList) {
			if(!rowNames.contains(rowName)) {
				removeRow(rowName);
			}
		}
		
		for(String rowName: rowNames) {
			if(!rowNamesList.contains(rowName)) {
				addRow(rowName);
			}
		}
		
	}
	
	/**
	 * Sets columns of the <code>CyTable</code> displayed in the chart.
	 * @param columns The names of columns to be displayed in the chart.
	 */
	public void setColumns(List<String> columnNames) {
		int columnNamesLength = columnNamesList.size();
		String columnName;
		for(int i=0; i<columnNamesLength; i++) {
			columnName = columnNamesList.get(i);
			if(!checkBoxState.get(i) && columnNames.contains(columnName)) {
				addColumn(columnName);
			} else if (checkBoxState.get(i) && !columnNames.contains(columnName)) {
				removeColumn(columnName);
			}
		}
	}
	
	/**
	 * Returns the number of checkboxes created.
	 * @return The number of checkboxes created.
	 */
	public int getCheckBoxCount() {
		if(myCyTable.getAllRows().get(0).get("ChartType", String.class).equals("Line Chart")) {
			return tableColumnCount;
		} else if(myCyTable.getAllRows().get(0).get("ChartType", String.class).equals("Time Series")) {
			return tableRowCount;
		} 
		
		return 0;
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
	 * Returns the Canonical names of rows plotted in the chart.
	 * @return The Canonical names of rows plotted in the chart.
	 */
	public List<String> getRows() {
		return rowNamesList;
	}
	
	/**
	 * Returns the names of columns plotted in the chart.
	 * @return The names of columns plotted in the chart.
	 */
	public List<String> getColumns() {
		List<String> columnNames = new ArrayList<String>();
		int columnCount = checkBoxState.size();
		for(int i=0;i<columnCount;i++) {
			if(checkBoxState.get(i)) {
				columnNames.add(columnNamesList.get(i));
			}
		}
		return columnNames;
	}
	
	
	/**
	 * Sets the new chart within the {@link ChartPanel}.
	 */
	public void resetChartPanel(String chartType) {
		
		/*TODO Better way to associate ChartType to AxisMode; not with an IF statement as below
		 * ->Below works because default is "Line Chart" and AxisMode.ROWS
		 * Probably move this method to another class
		 */ 
		if(chartType.equals("Line Chart")) {
			mode = AxisMode.ROWS;
		} else if(chartType.equals("Time Series")) {
			mode = AxisMode.COLUMNS;
		}
		myCyTable.getAllRows().get(0).set("AxisMode", mode.toString());
		
		MyTableModel myTableModel = new MyTableModel(cyTable);
		
		initComponents(cyTable, mode, myTableModel, null, null, chartType);
		panelLayout.initLayout(getCheckBoxCount(), checkBoxArray, chartTypeComboBox, myChartPanel);
		//myChartPanel.setChart(chart); //this myChartPanel is the same ChartPanel that is displayed using PanelLayout.java
	}

}
