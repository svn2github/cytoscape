/* * Modified Date: Jul.27.2010
 * * by : Steven Maere
 * */

/*
 * ResultTableModel.java
 *
 * Created on July 31, 2006, 4:03 PM
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

package pingo.GOlorize;

import javax.swing.table.* ;



/**
 *
 * @author ogarcia
 */
public class ResultTableModel  extends AbstractTableModel {
       
       
            //JComboBox comboGOID;
             
            private Object[] columnNames;
            private Object[][] data;
            private int SELECT=0;
            
            public ResultTableModel(Object[] columnNames,Object[][] data){
                super();
                this.columnNames = columnNames;
                this.data=data;
            }   

            public int getColumnCount() {
                return columnNames.length;
                
            }

            public int getRowCount() {
                return data.length;
            }

            public String getColumnName(int col) {
                
                
                 return (String)columnNames[col];
                
                
            }

            public Object getValueAt(int row, int col) {
                
              
                
                return data[row][col];
            }

            /*
             * JTable uses this method to determine the default renderer/
             * editor for each cell.  If we didn't implement this method,
             * then the last column would contain text ("true"/"false"),
             * rather than a check box.
             */
            public Class getColumnClass(int c) {         
                return getValueAt(0, c).getClass();
            }

            /*
             * Don't need to implement this method unless your table's
             * editable.
             */
            public boolean isCellEditable(int row, int col) {
                //Note that the data/cell address is constant,
                //no matter where the cell appears onscreen.
                if (col == SELECT){ //si j'utilise pas combobox amigo sgd, laisser ke if col ==1'
                    return true;
                }
                else {
                    return false;
                }
                //if ((col > 0)) {
                //    return false;
                //} else {
                //    return true;
                //}
            }
            

            /*
             * Don't need to implement this method unless your table's
             * data can change.
             */
            public void setValueAt(Object value, int row, int col) {


                data[row][col] = value;
                
                    
                fireTableCellUpdated(row, col);


            }
            
}
