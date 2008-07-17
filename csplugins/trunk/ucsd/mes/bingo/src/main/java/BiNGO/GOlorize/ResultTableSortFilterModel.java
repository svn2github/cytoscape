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

package BiNGO.GOlorize;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
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
            
            /*
            if (a instanceof Comparable)
                return ((Comparable)a).compareTo(b);
            else 
                return a.toString().compareTo(b.toString());*/
            
            if (model.getColumnName(sortColumn).equals(" ")){
                boolean b1=((Boolean)a).booleanValue();
                boolean b2=((Boolean)b).booleanValue();
                if (b1==true && b2==false)
                    return -1;
                if (b1==false && b2==true)
                    return 1;
                else 
                    return 0;
                
            }
            if (model.getColumnName(sortColumn).equals("GO-ID")){
                //return ((Comparable)a).compareTo(b);
                return new Integer((String)a).compareTo(new Integer((String)b));
            }
            if (model.getColumnName(sortColumn).equals("Description")){
                return ((Comparable)((JLabel)a).getText()).compareTo(((JLabel)b).getText()) ; 
            }
            
            if (model.getColumnName(sortColumn).equals("p-val") || model.getColumnName(sortColumn).equals("corr p-val")){
                //int offsa = ((String)a).indexOf(" ");
                //int offsb = ((String)b).indexOf(" ");
                //Double sa = new Double(((String)a).substring(0,offsa)+((String)a).substring(offsa+1));
                
                //Double sb = new Double(((String)b).substring(0,offsb)+((String)a).substring(offsb+1));
                Double sa= new Double(((String)a).replaceAll(" ",""));
                Double sb= new Double(((String)b).replaceAll(" ",""));
                
                
                return sa.compareTo(sb);
            }
            if (model.getColumnName(sortColumn).equals("cluster freq")||model.getColumnName(sortColumn).equals("total freq")){
                int offs = ((String)a).indexOf("/");
                Integer sa = new Integer(((String)a).substring(0,offs));
                offs = ((String)b).indexOf("/");
                Integer sb = new Integer(((String)b).substring(0,offs));
                return sa.compareTo(sb);
            }
            else return 0;
            
        }
    }
}
