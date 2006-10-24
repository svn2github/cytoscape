package GOlorize.BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 * * Date: Apr.20.2005
 * * Class that extends JPanel and implements ActionListener. Makes
 * * a panel with a drop-down box of ontology choices. Last option custom... opens FileChooser   
 **/


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.Cytoscape ;
import cytoscape.CytoscapeInit ;


/******************************************************************
 * ChooseOntologyPanel.java:   Steven Maere (c) April 2005
 * -----------------------
 *
 * Class that extends JPanel and implements ActionListener. Makes
 * a panel with a drop-down box of ontology choices. Last option custom... opens FileChooser
 ********************************************************************/

public class ChooseOntologyPanel extends JPanel implements ActionListener{
    
    
    
    
  /*--------------------------------------------------------------
    Fields.
   --------------------------------------------------------------*/
    	private final String NONE = "---" ;
		private final String PROCESS = "GO Biological Process" ;
    	private final String FUNCTION = "GO Molecular Function" ;
		private final String COMPONENT = "GO Cellular Component" ;
		private final String GO_FULL = "full GO" ;
		private final String GOSLIM_GENERIC = "GOSlim generic" ;
		private final String GOSLIM_GOA = "GOSlim goa" ;
		private final String GOSLIM_PLANTS = "GOSlim plants" ;
		private final String GOSLIM_YEAST = "GOSlim yeast" ;
		private final String CUSTOM = "Custom..." ;
	
	
		/** array of strings with the options for the ontology choice.*/
    	private final String [] choiceArray = {NONE,
    										PROCESS,
    										FUNCTION,
											COMPONENT,
											GO_FULL,
											GOSLIM_GENERIC,
											GOSLIM_GOA,
											GOSLIM_PLANTS,
											GOSLIM_YEAST,
											CUSTOM
											};
    	/** JComboBox with the possible choices.*/
		private JComboBox choiceBox;
		/** the selected file.*/
		private File openFile = null;
		/**parent window*/
		private Component settingsPanel ;
 		/** default = true, custom = false*/
		private boolean def = true ;
		/** BiNGO directory path*/
		private String bingoDir ;
		/** BiNGO annotations directory path*/
		private File annotationFilePath ;
    
  /*-----------------------------------------------------------------
    CONSTRUCTOR.
   -----------------------------------------------------------------*/

    /**
     * Constructor with a string argument that becomes part of the label of
     * the button.
     *
     * @param sort string that denotes part of the name of the button.
	 * @param settingsPanel : parent window
     */
    public ChooseOntologyPanel (Component settingsPanel, String bingoDir) {  	
    	super();
			this.bingoDir = bingoDir ;
			this.settingsPanel = settingsPanel ;
			annotationFilePath = new File(bingoDir, "BiNGO") ;
			setOpaque(false);
			makeJComponents();
			// Layout with GridLayout.
			setLayout(new GridLayout(1, 0));
			add(choiceBox);	
			
			//defaults
			choiceBox.setSelectedItem("GO Biological Process") ;
			openFile = new File(annotationFilePath, "GO_Biological_Process") ;
			def = true ;
    }
    
    
    
    
    
    /*----------------------------------------------------------------
      PAINTCOMPONENT.
      ----------------------------------------------------------------*/

		/**
	 	 * Paintcomponent, part where the drawing on the panel takes place.
	 	 */
    public void paintComponent (Graphics g){
        super.paintComponent(g);
    }		  
    
    /*----------------------------------------------------------------
      METHODS.
      ----------------------------------------------------------------*/ 
    
    /**
     * Method that creates the JComponents.
     *
     * @param sort string that denotes part of the name of the button.
     */
    public void makeJComponents(){
    	
    	// button.
    	choiceBox = new JComboBox(choiceArray);
		choiceBox.setEditable(false) ;
		choiceBox.addActionListener(this);

				
    }
    
    
    /**
     * Method that returns the selected file.
     *
     * @return File selected file.
     */
    public File getFile(){
    	return openFile;
    }
	
	 /**
     * Method that returns the selected item.
     *
     * @return String selection.
     */
	public String getSelection(){
		return choiceBox.getSelectedItem().toString() ;	
	}	
    
	/**
     * Method that returns 1 if one of default choices was chosen, 0 if custom
     */
	public boolean getDefault(){
		return def ;	
	}
	
    
    /*----------------------------------------------------------------
      LISTENER-PART.
      ----------------------------------------------------------------*/    

		/**
	 	 * Method performed when button clicked.
	   *
	 	 * @param event event that triggers action, here clicking of the button.
	 	 */
    public void actionPerformed (ActionEvent event) {
    	
		if(choiceBox.getSelectedItem().equals(PROCESS)){
			openFile = new File(annotationFilePath, "GO_Biological_Process") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(FUNCTION)){
			openFile = new File(annotationFilePath, "GO_Molecular_Function") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(COMPONENT)){
			openFile = new File(annotationFilePath, "GO_Cellular_Component") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(GO_FULL)){
			openFile = new File(annotationFilePath, "GO_Full") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(GOSLIM_GENERIC)){
			openFile = new File(annotationFilePath, "GOSlim_Generic") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(GOSLIM_GOA)){
			openFile = new File(annotationFilePath, "GOSlim_GOA") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(GOSLIM_PLANTS)){
			openFile = new File(annotationFilePath, "GOSlim_Plants") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(GOSLIM_YEAST)){
			openFile = new File(annotationFilePath, "GOSlim_Yeast") ;
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(CUSTOM)){
			JFileChooser chooser = new JFileChooser(bingoDir);
			int returnVal = chooser.showOpenDialog(settingsPanel);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				openFile = chooser.getSelectedFile();
				choiceBox.setEditable(true) ;	
				choiceBox.setSelectedItem(openFile.toString()) ;
				choiceBox.setEditable(false) ;
				def = false ;
	    	}
			if(returnVal == JFileChooser.CANCEL_OPTION) {
			    choiceBox.setSelectedItem(NONE) ;
				openFile = null ;
				def = true ;
			}	
		}	
		else{
			choiceBox.setSelectedItem(NONE) ;
			openFile = null ;
			def = true ;
		}	
    }
}
