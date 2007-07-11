package BiNGO ;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere
 * * Date: Apr.11.2005
 * * Description: Class for monitoring jobs of initially unknown length.          
 **/

/***************************************************************
 * IndeterminateJobCalculator.java   
 * --------------------------
 *
 * Steven Maere (c) April 2005
 *
 * Class for monitoring jobs of initially unknown length.  
 ***************************************************************/


import java.io.IOException ;
import javax.swing.* ;
import javax.swing.border.* ;
import java.awt.*;
import java.awt.event.*;
import cytoscape.Cytoscape ;
import cytoscape.view.CytoscapeDesktop ;

public class IndeterminateJobCalculator implements Runnable{

	//Fields

	private JProgressBar progressBar ;
	private JTextField textField ;
	private JPanel panel ;
	private JFrame frame ;
	private MonitorableTask test ;
	public final static int ONE_SECOND = 1000;
	private boolean determined = false ;
	//Constructor
	
	public IndeterminateJobCalculator(MonitorableTask test){		
		this.test = test ;
	}	

	
	public void run(){

		//progress bar
		progressBar = new JProgressBar(0, 100);
		//progressBar.setIndeterminate(true);
		
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		//display text
		textField = new JTextField(test.getTaskDescription()) ;
		textField.setBorder(new EmptyBorder(textField.getInsets())) ;
		panel = new JPanel() ;
		panel.setPreferredSize(new Dimension(250, 75));
		//create border.
		panel.setBorder (BorderFactory.createEtchedBorder());
		
		GridBagLayout gridbag = new GridBagLayout() ;		
		GridBagConstraints c = new GridBagConstraints();
		
		panel.setLayout(gridbag);
		c.weightx = 1 ;
		c.weighty = 1 ;
      	c.gridwidth = GridBagConstraints.REMAINDER;
		
		gridbag.setConstraints(progressBar, c);
		panel.add(progressBar) ;
		gridbag.setConstraints(textField, c);
		panel.add(textField) ;
		
		//panel.setBackground(Color.WHITE) ;
		textField.setBackground(panel.getBackground()) ;
		
		//Create and set up the window.

		frame = new JFrame("Progress");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//Create and set up the content pane.
		panel.setOpaque(true); 
		frame.getContentPane().add(panel);
		//frame.getContentPane().setBackground(Color.WHITE) ;

		//Display the window.
		frame.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth())/2, (screenSize.height - frame.getHeight())/2);
		frame.setVisible(true);

		test.start(false) ;	

		while(!test.isDone()){
			if((test.getLengthOfTask() != -1)){
				if(determined = false){
					determined = true ;
					progressBar.setMaximum(test.getLengthOfTask()) ;
					progressBar.setStringPainted(true) ;
				}				
				progressBar.setValue(test.getCurrentProgress());
			}
			
			frame.update(frame.getGraphics()) ;

			try{
				Thread.sleep(100) ;
			}
			catch (InterruptedException e){
				System.out.println(e) ;
			}	
		} 
	  	frame.dispose() ;
	}
	
	//getters

	/**
	* @return <code>true</code> if the task is done, false otherwise
	*/
    public boolean isDone() {
        return test.isDone();
    }//isDone

}

