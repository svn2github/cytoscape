package org.cytoscape.sample.internal;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;

public class PanelComponents {

    private JTable table;
	private TableColumnModel tableColumnModel;
	public static Map<String, Serializable> hiddenColumnsColumn;
    public static Map<String, Serializable> hiddenColumnsIndex;
	private JCheckBox[] checkBoxArray;
	private int columnCount;
	public static CyTable myCyTable;
	private List<Boolean> checkBoxState;
	private Long networkSUID;
    
    public PanelComponents() {
    	
    }
    
    static {
    	hiddenColumnsColumn = new HashMap<String, Serializable>();
        hiddenColumnsIndex = new HashMap<String, Serializable>();
    }

    public void initCyTable(CyTableFactory tableFactory) {
    	//myCyTable stores a network's SUID and a Boolean List that suggests whether a particular column of
    	//the JTable is visible or hidden based on its true/false value. Each element in the list corresponds 
    	//to a particular column in a network
		if(myCyTable==null) {
			myCyTable = tableFactory.createTable("MyCyTable", "SUID", Long.class, true, true);
			myCyTable.createListColumn("States", Boolean.class, true);
		}
    }
    
    /**
     * Initializes an array of checkboxes with column names of the table as titles and
     * sets each checkbox checked/unchecked corresponding to the Boolean values in which track hidden columns.
     * The checkboxes allows user to check/uncheck a particular column.  
     * 
     * @return JCheckBox[] Array of checkboxes initialized with column names as titles
     */
    public JCheckBox[] initCheckBoxArray(List<Boolean> checkBoxState, Long networkSUID, JTable table){
		
    	this.table = table;
    	this.tableColumnModel = table.getColumnModel();
    	this.columnCount = table.getColumnCount();
    	this.checkBoxState = checkBoxState;
    	this.networkSUID = networkSUID;
    	
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
     * <code>HashMap</code>s store the index and the column that is removed.
     * 
     * @param columnName Name of the column that has to be hidden
     */
    public void hideColumn(String columnName) {
        int columnIndex = tableColumnModel.getColumnIndex(columnName);
        TableColumn column = tableColumnModel.getColumn(columnIndex);
        //appending column name with network SUID to uniquely identify the column
        //of a given network
        columnName+=networkSUID;
        //enter the columnName-column combination in the HashMap hiddenColumnsColumn
        hiddenColumnsColumn.put(columnName, column);
        //enter the columnName-index combination in the HashMap hiddenColumnsIndex
        hiddenColumnsIndex.put(columnName, new Integer(columnIndex));
        tableColumnModel.removeColumn(column);
        
        checkBoxState.set(columnIndex, false);
        myCyTable.getRow(networkSUID).set("States", checkBoxState);
        
    }

    /**
     * Makes a column visible in the table view by adding it to the table column model.
     * The column is then moved to where it was deleted from.
     * 
     * @param columnName Name of the column that has to be made visible
     */
	public void showColumn(String columnName) {
		//appending column name with network SUID to uniquely identify the column
        //of a given network
		columnName+=networkSUID;
		//o acquires the column corresponding to the column name
		Object o = hiddenColumnsColumn.remove(columnName);
        if (o == null) {
            return;
        }
        tableColumnModel.addColumn((TableColumn) o);
        //o acquires the index corresponding to the column name
        o = hiddenColumnsIndex.remove(columnName);
        if (o == null) {
            return;
        }
        int columnIndex = ((Integer) o).intValue();
        //tableColumnModel recalculates the number of columns
        int lastColumn = tableColumnModel.getColumnCount() - 1;
        if(columnIndex < lastColumn){
        	tableColumnModel.moveColumn(lastColumn, columnIndex);
        }
        
        checkBoxState.set(columnIndex, true);
        myCyTable.getRow(networkSUID).set("States", checkBoxState);
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
