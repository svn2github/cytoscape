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
import java.util.*;
import java.awt.*;

public class DataTable extends JDialog{
	
	/**
	 * Constructor
	 * 
	 * @param row_names the names of the rows to be displayed in the 1st column
	 * @param column_names the names of the columns
	 * @param data the data to be displayed
	 */
	public DataTable (String [] row_names, String [] column_names, double [][] data){
		create(row_names, column_names, data);
	}//DataTable
	
	/**
	 * Constructor
	 * 
	 * @param row_names the names of the rows to be displayed in the 1st column
	 * @param column_names the names of the columns
	 * @param data the data to be displayed
	 */
	public DataTable (String [] row_names, String [] column_names, int [][] data){
		create(row_names, column_names, data);
	}//DataTable
	
	protected void create (String [] row_names, String [] column_names, double [][] data){
		
		JTable jtable = GuiUtils.createTable(column_names, row_names, data);
		JScrollPane scrollPane = new JScrollPane(jtable);
		jtable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.add(scrollPane);
		
		setContentPane(mainPanel);
		
	}//create
	
	protected void create (String [] row_names, String [] column_names, int [][] data){
		
		JTable jtable = GuiUtils.createTable(column_names, row_names, data);
		JScrollPane scrollPane = new JScrollPane(jtable);
		jtable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(scrollPane);
		
		setContentPane(mainPanel);
	
	}//create

}//DataTable 