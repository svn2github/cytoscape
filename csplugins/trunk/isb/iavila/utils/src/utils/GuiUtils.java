/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A class with static methods for general GUI functions.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version 1.0
 * @since 2.0
 */
package utils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class GuiUtils {
    
    /**
     * Creates and returns a JDialog with a white background and an "OK" button that displays
     * the given text
     * 
     * @param title the title of the returned dialog
     * @param text the text contained in the white background panel
     * @param owner the owner of this dialog
     * @return a JDialog
     */
    public static JDialog createAboutDialog (String title, String text, Dialog owner){ 
        final JDialog dialog = new JDialog(owner);
        dialog.setTitle(title);
        dialog.getContentPane().setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(text);
        panel.add(label);
        
        dialog.getContentPane().add(panel,BorderLayout.CENTER);
        
        JPanel okPanel = new JPanel();
        JButton okB = new JButton("OK");
        okB.addActionListener(new AbstractAction(){
           public void actionPerformed (ActionEvent event){
              dialog.dispose(); 
           } //event
        });
        okPanel.add(okB);
        dialog.getContentPane().add(okPanel,BorderLayout.SOUTH);
        return dialog;
        
    }
    
    /**
     * Creates and returns a JDialog with a white background and an "OK" button that displays
     * the given text
     * 
     * @param title the title of the returned dialog
     * @param text the text contained in the white background panel
     * @param owner the owner of this dialog
     * @return a JDialog
     */
    public static JDialog createAboutDialog (String title, String text, Frame owner){
        final JDialog dialog = new JDialog(owner);
        dialog.setTitle(title);
        dialog.getContentPane().setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(text);
        panel.add(label);
        
        dialog.getContentPane().add(panel,BorderLayout.CENTER);
        
        JPanel okPanel = new JPanel();
        JButton okB = new JButton("OK");
        okB.addActionListener(new AbstractAction(){
           public void actionPerformed (ActionEvent event){
              dialog.dispose(); 
           } //event
        });
        okPanel.add(okB);
        dialog.getContentPane().add(okPanel,BorderLayout.SOUTH);
        return dialog;
        
    }
	
	/**
	 * Creates a JTable with the given column names. The row names are copied to the 1st column of the table,
	 * the Objects in <code>rows</code> are converted to String via toString().
	 * 
	 * @param column_names the column names
	 * @param row_names the row names
	 * @param rows an Object 2D array that holds the table data (rows[i][j].toString() will be used as table elements)
	 * @return a JTable
	 */
	public static JTable createTable (String [] column_names, String [] row_names, Object [][] rows){
		
		String [][] data = new String[row_names.length][column_names.length + 1];
		
		for(int i = 0; i < row_names.length; i++){
			data[i][0] = row_names[i];
		}//for i
		
		for(int i = 0; i < rows.length; i++){
			for(int j = 0; j < rows[i].length; j++){
				data[i][j+1] = rows[i][j].toString();
			}//for j
		}//for i
		
		String [] colNames = new String[column_names.length + 1];
		System.arraycopy(column_names,0,colNames,1,column_names.length);
		colNames[0] = " ";
		
		JTable table = new JTable(data,colNames);
		
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(100); //gene name column is bigger
		
		return table;
	}//createTable
	
	/**
	 * Creates a JTable with the given column names. The row names are copied to the 1st column of the table,
	 * the double values in <code>rows</code> are converted to Strings.
	 * 
	 * @param column_names the column names
	 * @param row_names the row names
	 * @param rows a double 2D array that holds the table data
	 * @return a JTable
	 */
	public static JTable createTable (String [] column_names, String [] row_names, double [][] rows){
		
		String [][] data = new String[row_names.length][column_names.length + 1];
		
		for(int i = 0; i < row_names.length; i++){
			data[i][0] = row_names[i];
		}//for i
		
		for(int i = 0; i < rows.length; i++){
			for(int j = 0; j < rows[i].length; j++){
				data[i][j+1] = Double.toString(rows[i][j]);
			}//for j
		}//for i
		
		String [] colNames = new String[column_names.length + 1];
		System.arraycopy(column_names,0,colNames,1,column_names.length);
		colNames[0] = " ";
		
		JTable table = new JTable(data,colNames);
		
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(100); //gene name column is bigger
		
		return table;
	}//createTable

	
	/**
	 * Creates a JTable with the given column names. The row names are copied to the 1st column of the table,
	 * the int values in <code>rows</code> are converted to Strings.
	 * 
	 * @param column_names the column names
	 * @param row_names the row names
	 * @param rows an int 2D array that holds the table data
	 * @return a JTable
	 */
	public static JTable createTable (String [] column_names, String [] row_names, int [][] rows){
		
		String [][] data = new String[row_names.length][column_names.length + 1];
		
		for(int i = 0; i < row_names.length; i++){
			data[i][0] = row_names[i];
		}//for i
		
		for(int i = 0; i < rows.length; i++){
			for(int j = 0; j < rows[i].length; j++){
				data[i][j+1] = Integer.toString(rows[i][j]);
			}//for j
		}//for i
		
		String [] colNames = new String[column_names.length + 1];
		System.arraycopy(column_names,0,colNames,1,column_names.length);
		colNames[0] = " ";
		
		JTable table = new JTable(data,colNames);
		
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(100); //gene name column is bigger
		
		return table;
	}//createTable
	
}//GuiUtils