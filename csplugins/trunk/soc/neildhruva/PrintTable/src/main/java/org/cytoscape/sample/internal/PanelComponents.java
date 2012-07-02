package org.cytoscape.sample.internal;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
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
	private Long networkSUID;
	private CyTable cytable;
	private MyCytoPanel myCytoPanel;
    
    public PanelComponents(MyCytoPanel myCytoPanel) {
    	this.myCytoPanel = myCytoPanel;
    }
    
    /*public void initCyTable(CyTableFactory tableFactory) {
    	//myCyTable stores a network's SUID and a Boolean List that suggests whether a particular column of
    	//the JTable is visible or hidden based on its true/false value. Each element in the list corresponds 
    	//to a particular column in a network
		if(myCyTable==null) {
			myCyTable = tableFactory.createTable("MyCyTable", "SUID", Long.class, true, true);
			myCyTable.createListColumn("Names", String.class, true);
			myCyTable.createListColumn("States", Boolean.class, true);
		}
    }*/
    
    /**
     * Initializes an array of checkboxes with column names of the table as titles and
     * sets each checkbox checked/unchecked corresponding to the Boolean values in which track hidden columns.
     * The checkboxes allows user to check/uncheck a particular column.  
     * 
     * @return JCheckBox[] Array of checkboxes initialized with column names as titles
     */
    public JCheckBox[] initCheckBoxArray(CyTable myCyTable, Long networkSUID, CyTable cytable){
		
    	this.myCyTable = myCyTable;
    	this.cytable = cytable;
    	this.table = new JTable(new MyTableModel(cytable));
    	this.tableColumnModel = table.getColumnModel();
    	this.columnCount = table.getColumnCount();
    	this.networkSUID = networkSUID;
    	
    	this.columnNamesList = new ArrayList<String>();
		this.checkBoxState = new ArrayList<Boolean>();
    	checkBoxState = myCyTable.getRow(networkSUID).getList("States", Boolean.class);
    	columnNamesList = myCyTable.getRow(networkSUID).getList("Names", String.class);
    	
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
        
        return checkBoxArray;
    }
    
    /**
     * Hides the column from the table view by removing it from the table column model.
     * 
     * @param columnName Name of the column that has to be hidden
     */
    public void hideColumn(String columnName) {
        
    	int columnIndex = tableColumnModel.getColumnIndex(columnName);
        TableColumn column = tableColumnModel.getColumn(columnIndex);
        
        columnIndex = columnNamesList.indexOf(columnName);
        checkBoxState.set(columnIndex, false);
        myCyTable.getRow(networkSUID).set("States", checkBoxState);
        
        tableColumnModel.removeColumn(column);
        
    }

    /**
     * Makes a column visible in the table view by refreshing the CytoPanel.
     * 
     * @param columnName Name of the column that has to be made visible
     */
	public void showColumn(String columnName) {
		
		int columnIndex = columnNamesList.indexOf(columnName);
        checkBoxState.set(columnIndex, true);
        myCyTable.getRow(networkSUID).set("States", checkBoxState);
        table = new JTable(new MyTableModel(cytable));
        tableColumnModel = table.getColumnModel();
        for(int i=0;i<columnCount;i++){
        	if(!checkBoxState.get(i)) {
        		TableColumn column = tableColumnModel.getColumn(tableColumnModel.getColumnIndex(checkBoxArray[i].getText()));
        		tableColumnModel.removeColumn(column);
        	}
        }
        
        //refresh the CytoPanel
        myCytoPanel.initComponents(table, checkBoxArray, columnCount);
    }
	
	/**
	 * 
	 * @return JCheckBox[] The modified checkbox array after the user has selected/deselected
	 * 					   some checkboxes.
	 */
	public JCheckBox[] getCheckBoxArray(){
		return this.checkBoxArray;
	}
	
	/**
	 * 
	 * @return int The initial column count of the table.
	 */
	public int getTableColumnCount(){
		return this.columnCount;
	}
	
	/**
	 * 
	 * @return JTable The modified JTable after some rows have been made invisible.
	 */
	public JTable getTable(){
		return this.table;
	}

}
