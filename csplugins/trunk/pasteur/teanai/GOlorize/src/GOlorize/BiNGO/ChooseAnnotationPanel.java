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
 * * a panel with a drop-down box of organism/annotation choices. Custom... opens FileChooser   
 **/


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.Cytoscape ;
import cytoscape.CytoscapeInit ;


/******************************************************************
 * ChooseAnnotationPanel.java:   Steven Maere (c) April 2005
 * -----------------------
 *
 * Class that extends JPanel and implements ActionListener. Makes
 * a panel with a drop-down box of organism/annotation choices. Custom... opens FileChooser
 ********************************************************************/

public class ChooseAnnotationPanel extends JPanel implements ActionListener{
    
    
    
    
  /*--------------------------------------------------------------
    Fields.
   --------------------------------------------------------------*/
	    private final String NONE = "---" ;
		private final String YEAST = "Saccharomyces cerevisiae" ;
    	private final String ARABIDOPSIS = "Arabidopsis thaliana" ;
		private final String ORYZA_NIVARA = "Oryza nivara" ; 
		private final String POMBE = "Schizosaccharomyces pombe" ;
		private final String TRYPANOSOMA = "Trypanosoma brucei" ;
		private final String C_ELEGANS = "Caenorhabditis elegans" ;
		private final String DROSOPHILA = "Drosophila melanogaster" ;
		private final String ZEBRA = "Brachydanio rerio" ;
		private final String HUMAN = "Homo Sapiens" ;
		private final String MOUSE = "Mus musculus" ;
		private final String RAT = "Rattus norvegicus" ;
		private final String PLASMODIUM = "Plasmodium falsiparum" ;
 		private final String ORYZA_SATIVA = "Oryza sativa" ; 
		private final String ANTHRAX = "Bacillus anthracis" ;
		private final String SHEWANELLA = "Shewanella oneidensis" ;
		private final String PSEUDOMONAS_SYRINGAE = "Pseudomonas syringae" ;
		private final String COXIELLA_BURNETII = "Coxiella burnetii" ;
		private final String GEOBACTER_SULFURREDUCENS = "Geobacter sulfurreducens" ;
		private final String METHYLOCOCCUS_CAPSULATUS = "Methylococcus capsulatus" ;
		private final String LISTERIA_MONOCYTOGENES = "Listeria monocytogenes" ;	
		private final String CUSTOM = "Custom..." ;
		
	/*
	    private final String NONE = "---" ;
		private final String YEAST = "Saccharomyces cerevisiae" = 4932 ;
    	private final String ARABIDOPSIS = "Arabidopsis thaliana" = 3702 ;
		private final String ORYZA_NIVARA = "Oryza nivara" = 4536 ; 
		private final String POMBE = "Schizosaccharomyces pombe" = 4896;
		private final String TRYPANOSOMA = "Trypanosoma brucei" = 5691 ;
		private final String C_ELEGANS = "Caenorhabditis elegans" = 6239 ;
		private final String DROSOPHILA = "Drosophila melanogaster" = 7227 ;
		private final String ZEBRA = "Brachydanio rerio" = 7955 ;
		private final String HUMAN = "Homo Sapiens" = 9606 ;
		private final String MOUSE = "Mus musculus" = 10090 ;
		private final String RAT = "Rattus norvegicus" = 10116 ;
		private final String PLASMODIUM = "Plasmodium falsiparum" = 36329 ;
 		private final String ORYZA_SATIVA = "Oryza sativa" = 39947 ; 
		private final String ANTHRAX = "Bacillus anthracis" = 198094 ;
		private final String SHEWANELLA = "Shewanella oneidensis" = 211586 ;
		private final String PSEUDOMONAS_SYRINGAE = "Pseudomonas syringae" = 223283 ;
		private final String COXIELLA_BURNETII = "Coxiella burnetii" = 227377 ;
		private final String GEOBACTER_SULFURREDUCENS = "Geobacter sulfurreducens" = 243231 ;
		private final String METHYLOCOCCUS_CAPSULATUS = "Methylococcus capsulatus" = 243233 ;
		private final String LISTERIA_MONOCYTOGENES = "Listeria monocytogenes" = 265669 ;	
		private final String CUSTOM = "Custom..." ;
	*/	
		
		
		/** array of strings with the options for the ontology choice.*/
    	private final String [] choiceArray = {NONE,
											ARABIDOPSIS,
											ANTHRAX,
											ZEBRA,
											C_ELEGANS,
											COXIELLA_BURNETII,
											DROSOPHILA,
											GEOBACTER_SULFURREDUCENS,
											HUMAN,
											LISTERIA_MONOCYTOGENES,
											METHYLOCOCCUS_CAPSULATUS,
											MOUSE,
    										ORYZA_NIVARA,
											ORYZA_SATIVA,
											PLASMODIUM,
											PSEUDOMONAS_SYRINGAE,
											RAT,
											YEAST,
											POMBE,
											SHEWANELLA,
											TRYPANOSOMA,
											CUSTOM
											};
    	/** JComboBox with the possible choices.*/
		private JComboBox choiceBox;
		/** Type Of Identifier choice panel for precompiled annotations*/
		private TypeOfIdentifierPanel typeOfIdentifierPanel ;
		/** the selected file.*/
		private File openFile = null;
		/**parent window*/
		private Component settingsPanel ;
		/** boolean to assess default or custom input*/
		private boolean def = true ;
		/** BiNGO directory path*/
		private String bingoDir ;
                
                
                
 
    
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
    public ChooseAnnotationPanel (Component settingsPanel, String bingoDir) {  	
    	super();
			this.bingoDir = bingoDir ;
			this.settingsPanel = settingsPanel;
			setOpaque(false);
			makeJComponents();
                        
                             
                                
                                
			// Layout with GridBagLayout.
                        
			GridBagLayout gridbag = new GridBagLayout() ;	
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);
                        
                        
                        
                        
                        
                        c.gridx=1;
                        c.gridy=2;///////////
			c.weightx = 1 ;
			c.weighty = 1 ;
                        
                        c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
		
			gridbag.setConstraints(choiceBox, c);
                         
			add(choiceBox);	
                        
                        
                        
                        c.fill = GridBagConstraints.HORIZONTAL;
                        
                        
			
			c.gridheight = 2;
                        c.gridy=3;
			c.weighty = 2 ;
			gridbag.setConstraints(typeOfIdentifierPanel, c);
                         
			add(typeOfIdentifierPanel);
			typeOfIdentifierPanel.enableButtons() ;
			
			//defaults
			
			choiceBox.setSelectedItem("Saccharomyces cerevisiae") ;
			File tmp = new File(bingoDir,"BiNGO") ;
			openFile = new File(tmp,"S_cerevisiae_default") ;
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
    	
    	// choiceBox.
    	choiceBox = new JComboBox(choiceArray);
		choiceBox.setEditable(false) ;
		choiceBox.addActionListener(this);
		
		typeOfIdentifierPanel = new TypeOfIdentifierPanel() ;
				
    }
    
    /**
     * Method that returns the TypeOfIdentifierPanel.
     *
     * @return File selected file.
     */
    public TypeOfIdentifierPanel getTypeOfIdentifierPanel(){
    	return typeOfIdentifierPanel;
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
		typeOfIdentifierPanel.enableButtons() ;
		File tmp = new File(bingoDir,"BiNGO") ;
		if(choiceBox.getSelectedItem().equals(NONE)){
			openFile = null ; def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(CUSTOM)){
			JFileChooser chooser = new JFileChooser(bingoDir);
			int returnVal = chooser.showOpenDialog(settingsPanel);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				openFile = chooser.getSelectedFile();
				choiceBox.setEditable(true) ;	
				choiceBox.setSelectedItem(openFile.toString()) ;
				choiceBox.setEditable(false) ;
				typeOfIdentifierPanel.disableButtons() ;
				def = false ;
	    	}
			if(returnVal == JFileChooser.CANCEL_OPTION) {
			    choiceBox.setSelectedItem(NONE) ;
				def = true ;
			}	
		}
		/*else{
		  File tmp = new File(bingoDir,"BiNGO") ;
			openFile = new File(tmp,"Default_annotations") ;			
			def = true ;
		}*/
			
		else if(choiceBox.getSelectedItem().equals(ANTHRAX)){
			openFile = new File(tmp,"B_anthracis_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(ZEBRA)){
			openFile = new File(tmp,"B_rerio_default") ;			
			def = true ;
		}
    	else if(choiceBox.getSelectedItem().equals(ARABIDOPSIS)){
			openFile = new File(tmp,"A_thaliana_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(C_ELEGANS)){
			openFile = new File(tmp,"C_elegans_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(COXIELLA_BURNETII)){
			openFile = new File(tmp,"C_burnetii_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(DROSOPHILA)){
			openFile = new File(tmp,"D_melanogaster_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(GEOBACTER_SULFURREDUCENS)){
			openFile = new File(tmp,"G_sulfurreducens_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(HUMAN)){
			openFile = new File(tmp,"H_sapiens_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(LISTERIA_MONOCYTOGENES)){
			openFile = new File(tmp,"L_monocytogenes_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(METHYLOCOCCUS_CAPSULATUS)){
			openFile = new File(tmp,"M_capsulatus_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(MOUSE)){
			openFile = new File(tmp,"M_musculus_default") ;			
			def = true ;
		}
    	else if(choiceBox.getSelectedItem().equals(ORYZA_NIVARA)){
			openFile = new File(tmp,"O_nivara_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(ORYZA_SATIVA)){
			openFile = new File(tmp,"O_sativa_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(PLASMODIUM)){
			openFile = new File(tmp,"P_falsiparum_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(PSEUDOMONAS_SYRINGAE)){
			openFile = new File(tmp,"P_syringae_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(RAT)){
			openFile = new File(tmp,"R_norvegicus_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(YEAST)){
			openFile = new File(tmp,"S_cerevisiae_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(POMBE)){
			openFile = new File(tmp,"S_pombe_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(SHEWANELLA)){
			openFile = new File(tmp,"S_oneidensis_default") ;			
			def = true ;
		}
		else if(choiceBox.getSelectedItem().equals(TRYPANOSOMA)){
			openFile = new File(tmp,"T_brucei_default") ;			
			def = true ;
		}
		else{
			choiceBox.setSelectedItem(NONE) ;
			def = true ;
		}
    }
}
