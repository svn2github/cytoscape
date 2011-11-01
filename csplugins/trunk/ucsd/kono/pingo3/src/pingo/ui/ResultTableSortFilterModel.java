/* * Modified Date: Jul.27.2010
 * * by : Steven Maere
 * */

/*
 * ResultTableSortFilterModel.java
 *
 * Created on August 3, 2006, 7:59 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package pingo.ui;
import pingo.GOlorize.*;
import java.util.*;
import javax.swing.table.*;
/**
 *
 * @author ogarcia
 */
public class ResultTableSortFilterModel extends AbstractTableModel{
    private TableModel model;
    private int sortColumn;
    private Row[] rows;
    /**
     * Creates a new instance of ResultTableSortFilterModel
     */
    public ResultTableSortFilterModel(TableModel m) {
        model =m;
        rows = new Row[model.getRowCount()];
        for (int i =0;i<rows.length;i++){
            rows[i] = new Row();
            rows[i].index = i;
            
        }
    }
    public void sort(int c){
        sortColumn =c;
        Arrays.sort(rows);
        fireTableDataChanged();
    }
    public Object getValueAt(int r,int c){
        return model.getValueAt(rows[r].index,c);
        
    }
    public boolean isCellEditable(int r,int c){
        return model.isCellEditable(rows[r].index,c);
    }
    public void setValueAt(Object aValue, int r,int c){
        model.setValueAt(aValue,rows[r].index,c);
        this.fireTableDataChanged();
    }
    public int getRowCount(){
        return model.getRowCount();
    }
    public int getColumnCount(){
        return model.getColumnCount();
    }
    public String getColumnName(int c){
        return model.getColumnName(c);
    }
    public Class getColumnClass(int c){
        return model.getColumnClass(c);
    }
    
    
    private class Row implements Comparable {
        public int index;
        public int compareTo(Object other){
            Row otherRow = (Row)other;
            Object a = model.getValueAt(index,sortColumn);
            Object b = model.getValueAt(otherRow.index,sortColumn);
            
            
            if (a instanceof Comparable)
                return ((Comparable)a).compareTo(b);
            else 
                return a.toString().compareTo(b.toString());
            
            
            
        }
    }
}
