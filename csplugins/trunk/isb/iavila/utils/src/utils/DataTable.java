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
 * A graphical user interface for the algorithm implemented in <code>RGAlgorithm</code>.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */

package utils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DataTable extends JDialog{
	
	protected JFileChooser chooser;
	
	/**
	 * Constructor
	 * 
	 * @param row_names the names of the rows to be displayed in the 1st column
	 * @param column_names the names of the columns
	 * @param data the data to be displayed
	 */
	public DataTable (String [] row_names, String [] column_names, double [][] data, String title){
		setTitle(title);
		create(row_names, column_names, data);
	}//DataTable
	
	/**
	 * Constructor
	 * 
	 * @param row_names the names of the rows to be displayed in the 1st column
	 * @param column_names the names of the columns
	 * @param data the data to be displayed
	 */
	public DataTable (String [] row_names, String [] column_names, int [][] data, String title){
		setTitle(title);
		create(row_names, column_names, data);
	}//DataTable
	
	protected void create (String [] row_names, String [] column_names, double [][] data){
		
		JTable jtable = GuiUtils.createTable(column_names, row_names, data);
		JScrollPane scrollPane = new JScrollPane(jtable);
		jtable.setPreferredScrollableViewportSize(new Dimension(400, 70));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton toFileButton = new JButton("Save to file...");
		final String [] rowNames = row_names;
		final String [] colNames = column_names;
		final double [][] d = data;
		toFileButton.addActionListener(new AbstractAction (){
											
											public void actionPerformed (ActionEvent e){
												writeToFile(rowNames,colNames,d);
											}//actionPerformed
							
										});
		buttonPanel.add(toFileButton);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new AbstractAction(){
												
											public void actionPerformed (ActionEvent e){
												DataTable.this.dispose();
											}//actionEvent
											
										});
		buttonPanel.add(closeButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.setOpaque(true);
		setContentPane(mainPanel);
		
	}//create
	
	protected void create (String [] row_names, String [] column_names, int [][] data){
		
		JTable jtable = GuiUtils.createTable(column_names, row_names, data);
		JScrollPane scrollPane = new JScrollPane(jtable);
		jtable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton toFileButton = new JButton("Save to file...");
		buttonPanel.add(toFileButton);
		final String [] rowNames = row_names;
		final String [] colNames = column_names;
		final int [][] d = data;
		toFileButton.addActionListener(new AbstractAction (){
											
											public void actionPerformed (ActionEvent e){
												writeToFile(rowNames,colNames,d);
											}//actionPerformed
							
										});
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new AbstractAction(){
			
			public void actionPerformed (ActionEvent e){
				DataTable.this.dispose();
			}//actionEvent
		
		});
		buttonPanel.add(closeButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		mainPanel.setOpaque(true);
		setContentPane(mainPanel);
	
	}//create
	
	/**
	 * Pops up a file browser, and saves the given data into the selected file.
	 * 
	 * @param cols the names of the columns
	 * @param rows the names of the rows
	 * @param data the rows/columns
	 */
	public void writeToFile (String [] cols, String [] rows, double [][] data){
		if(this.chooser == null){
			this.chooser = new JFileChooser();
		}
		int returnVal = this.chooser.showSaveDialog(this);
	    File file;
		if(returnVal == JFileChooser.APPROVE_OPTION) {
	       file = this.chooser.getSelectedFile();
	    }else{
	    	return;
	    }
		
		StringBuffer buffer = new StringBuffer();
		String eol = System.getProperty("line.separator");
		for(int i = 0; i < cols.length; i++){
			buffer.append("\t");
			buffer.append(cols[i]);
		}//for i
		buffer.append(eol);
		for(int i = 0; i < data.length; i++){
			buffer.append(rows[i]);
			for(int j = 0; j < data[i].length; j++){
				buffer.append("\t");
				buffer.append(Double.toString(data[i][j]));
			}//for j
			buffer.append(eol);
		}//for i
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file,false));
			out.write(buffer.toString());
			out.flush();
			out.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"An error was encountered while writing file.",  
					"Oops!", 
					 JOptionPane.ERROR_MESSAGE); 

			return;
		}

	}//writeToFile
	
	/**
	 * Pops up a file browser, and saves the given data into the selected file.
	 *
	 * @param cols the names of the columns
	 * @param rows the names of the rows
	 * @param data the rows/columns
	 */
	public void writeToFile (String [] cols, String [] rows, int [][] data){
		if(this.chooser == null){
			this.chooser = new JFileChooser();
		}
		this.chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int returnVal = this.chooser.showSaveDialog(this);
	    File file;
		if(returnVal == JFileChooser.APPROVE_OPTION) {
	       file = this.chooser.getSelectedFile();
	    }else{
	    	return;
	    }
		
		StringBuffer buffer = new StringBuffer();
		String eol = System.getProperty("line.separator");
		for(int i = 0; i < cols.length; i++){
			buffer.append("\t");
			buffer.append(cols[i]);
		}//for i
		buffer.append(eol);
		for(int i = 0; i < data.length; i++){
			buffer.append(rows[i]);
			for(int j = 0; j < data[i].length; j++){
				buffer.append("\t");
				buffer.append(Integer.toString(data[i][j]));
			}//for j
			buffer.append(eol);
		}//for i
		
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file,false));
			out.write(buffer.toString());
			out.flush();
			out.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"An error was encountered while writing file.",  
					"Oops!", 
					 JOptionPane.ERROR_MESSAGE); 

			return;
		}

	}//writeToFile


}//DataTable 