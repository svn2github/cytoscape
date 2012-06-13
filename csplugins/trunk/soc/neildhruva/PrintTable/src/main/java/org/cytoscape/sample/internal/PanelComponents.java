package org.cytoscape.sample.internal;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.HashMap;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class PanelComponents {

    private JTable table;
	private TableColumnModel tableColumnModel;
	private HashMap<String, Serializable> hiddenColumnsColumn;
    private HashMap<String, Serializable> hiddenColumnsIndex;
	private JCheckBox[] checkBoxArray;
	private int columnCount;
	
    
    public PanelComponents(JTable table) {
    
    	this.table = table;
    	this.tableColumnModel = table.getColumnModel();
    	this.columnCount = table.getColumnCount();
    	this.hiddenColumnsColumn = new HashMap<String, Serializable>();
        this.hiddenColumnsIndex = new HashMap<String, Serializable>();
    }

    /**
     * Initializes an array of checkboxes with column names of the table as titles and
     * sets each checkbox selected by default. The checkboxes allows user to select/deselect
     * a particular column.  
     * 
     * @return JCheckBox[] Array of checkboxes initialized with column names as titles
     */
    public JCheckBox[] initialiseCheckBoxArray(){
		
        checkBoxArray = new JCheckBox[columnCount];
        
        for(int i=0;i<columnCount;i++){
        	checkBoxArray[i] = new JCheckBox();
        	checkBoxArray[i].setText(table.getColumnName(i));
        	checkBoxArray[i].setSelected(true);
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
    
        return checkBoxArray;
    }
    
    /**
     * Hides the column from the table view by removing it from the table column model.
     * <code>HashMap</code>s store the index and the column that is removed.
     * 
     * @param columnName Name of the column that has to be hidden
     */
    public void hideColumn(String columnName) {
        int index = tableColumnModel.getColumnIndex(columnName);
        TableColumn column = tableColumnModel.getColumn(index);
        //enter the columnName-column combination in the HashMap hiddenColumnsColumn
        hiddenColumnsColumn.put(columnName, column);
        //enter the columnName-index combination in the HashMap hiddenColumnsIndex
        hiddenColumnsIndex.put(columnName, new Integer(index));
        tableColumnModel.removeColumn(column);
    }

    /**
     * Makes a column visible in the table view by adding it to the table column model.
     * The column is then moved to where it was deleted from.
     * 
     * @param columnName Name of the column that has to be made visible
     */
	public void showColumn(String columnName) {
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
