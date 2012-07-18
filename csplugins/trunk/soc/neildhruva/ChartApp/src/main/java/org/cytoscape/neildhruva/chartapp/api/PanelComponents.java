package org.cytoscape.neildhruva.chartapp;

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

import org.cytoscape.model.CyTable;

public class PanelComponents {

    private JTable table;
	private TableColumnModel tableColumnModel;
	private JCheckBox[] checkBoxArray;
	private int columnCount;
	private CyTable myCyTable;
	private List<Boolean> checkBoxState;
	private List<String> columnNamesList;
	private JComboBox chartTypeComboBox;
	private PanelLayout panelLayout;
	
	public enum chartTypes {
		BAR {
		    public String toString() {
		        return "Bar Chart";
		    }
		},
		 
		LINE {
		    public String toString() {
		        return "Line Chart";
		    }
		}
	}
	
	public PanelComponents(PanelLayout panelLayout) {
	    this.panelLayout = panelLayout;
    }
	
	/**
	 * Initializes an array of checkboxes with column names of the table as titles and
     * sets each checkbox checked/unchecked corresponding to the Boolean values in which track hidden columns.
     * The checkboxes allows user to check/uncheck a particular column.
	 * @param myCyTable The custom CyTable that stores information about hidden columns, type of chart and column names.
	 * @param cytable The CyTable supplied by the user.
	 */
    @SuppressWarnings("unchecked")
	public void initComponents(CyTable myCyTable, CyTable cytable){
		
    	this.myCyTable = myCyTable;
    	this.table = new JTable(new MyTableModel(cytable));
    	this.tableColumnModel = table.getColumnModel();
    	this.columnCount = table.getColumnCount();
    	
    	this.columnNamesList = new ArrayList<String>();
		this.checkBoxState = new ArrayList<Boolean>();
    	checkBoxState = myCyTable.getAllRows().get(0).getList("States", Boolean.class);
    	columnNamesList = myCyTable.getAllRows().get(0).getList("Names", String.class);
    	
    	checkBoxArray = new JCheckBox[columnCount];
    	
        for(int i=0;i<columnCount;i++){
        	checkBoxArray[i] = new JCheckBox();
        	checkBoxArray[i].setText(table.getColumnName(i));
        	checkBoxArray[i].setSelected(checkBoxState.get(i));
        	
        	final int j=i;
        	
        	/* 
        	 * A listener is add to each checkbox so that when the corresponding
        	 * checkbox is clicked, the hideColumn and showColumn methods can be
        	 * invoked.
        	 */
        	checkBoxArray[i].addItemListener(new ItemListener() {
				
        		@Override
				public void itemStateChanged(ItemEvent arg0) {
					if(!checkBoxArray[j].isSelected()){
						hideColumn(checkBoxArray[j].getText());
					}else{
						showColumn(checkBoxArray[j].getText());
					}
				}
        	});
        }
    
        //hide all the columns that the user intends to hide in the JTable
        for(int i=0;i<columnCount;i++){
        	if(!checkBoxState.get(i)) {
        		TableColumn column = tableColumnModel.getColumn(tableColumnModel.getColumnIndex(checkBoxArray[i].getText()));
        		tableColumnModel.removeColumn(column);
        	}
        }
        
        //initialize the JComboBox which selects the type of chart to be displayed
        //every time it is changed, the graph changes as well
        if(chartTypeComboBox==null) {
        	chartTypeComboBox = new JComboBox(chartTypes.values());
        	chartTypeComboBox.setSelectedItem("Bar Chart"); //by default
        	
        	chartTypeComboBox.addActionListener(new ActionListener () {
        	    public void actionPerformed(ActionEvent e) {
        	    	String chartType = ((JComboBox) e.getSource()).getSelectedItem().toString();
        	    	panelLayout.setChartPanel(chartType);
        	    	updateChartType(chartType);
        	    }
        	});
        	
        } else {
        	chartTypeComboBox.getModel().setSelectedItem(myCyTable.getAllRows().get(0).get("ChartType", String.class));
        }
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
		
		/* after calling fireTableStructureChanged(), the entire JTable is refreshed. This is done because
		 * table.getAutoCreateColumnsFromModel() is true. So now, all columns corresponding to unchecked 
		 * checkboxes need to be hidden.
		 */
		for(int i=0;i<columnCount;i++) {
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
	
}
