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
 * * Date: Mar.25.2005
 * * Description: Class that extends JPanel and implements ActionListener ; makes panel
 * * that allows user to choose the type of gene identifiers used in the analysis.    
 **/
	
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;


/******************************************************************
 * TypeOfIdentifierPanel.java:       Steven Maere (c) March 2005
 * -----------------------
 *
 * Class that extends JPanel and implements ActionListener ; makes panel
 * that allows user to choose the type of gene identifiers used in the analysis.
 * 
 ********************************************************************/


public class TypeOfIdentifierPanel extends JPanel implements ActionListener{   
    
    /*--------------------------------------------------------------
      Fields.
      --------------------------------------------------------------*/
    
	/** radiobutton Entrez Gene GeneID (formerly LocusLink) input.*/
	private JRadioButton geneIDButton ;		
	/** radiobutton Official Gene Symbol input.*/
	private JRadioButton symbolButton ;
	/** radiobutton Unigene input.*/
	private JRadioButton unigeneButton ;		
	/** radiobutton LocusTag input (e.g. YKL035W for S. cerevisiae or AGI codes for Arabidopsis).*/
	private JRadioButton locusTagButton ;	
	/** button group */
	private ButtonGroup group ;		
	/** panel with radio buttons.*/	
	private JPanel radioPanel ;		
	// icons for the checkboxes.
	/** Icon for unchecked box.*/
	private Icon unchecked = new ToggleIcon (false);
	/** Icon for checked box.*/
	private Icon checked = new ToggleIcon (true);
    
    private static String GENEIDSTRING = "Entrez GeneID";
    private static String SYMBOLSTRING = "Gene Symbol";
    private static String UNIGENESTRING = "Unigene";
    private static String LOCUSTAGSTRING = "LocusTag";

    /*private final int DIM_WIDTH = 500 ;
    private final int DIM_HEIGHT = 200 ;*/
    
    /*-----------------------------------------------------------------
      CONSTRUCTOR.
      -----------------------------------------------------------------*/

    public TypeOfIdentifierPanel () {
    	
    	super();
			//setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
			setOpaque(false);
		
			makeJComponents();

			// Layout with GridBagLayout.

			GridBagLayout gridbag = new GridBagLayout() ;		
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);
			c.weightx = 1 ;
			c.weighty = 1 ;
      		c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
		
			gridbag.setConstraints(radioPanel, c);
      		add(radioPanel) ;

    }
    
    
    
    
    
    /*----------------------------------------------------------------
      PAINTCOMPONENT.
      ----------------------------------------------------------------*/

	 /**
	  * Paintcomponent, draws panel.
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
    	
    	geneIDButton = new JRadioButton(GENEIDSTRING, false);
		geneIDButton.setIcon(unchecked);
    	geneIDButton.setSelectedIcon(checked);
    	geneIDButton.setMnemonic(KeyEvent.VK_G);
    	geneIDButton.setActionCommand(GENEIDSTRING);

    	symbolButton = new JRadioButton(SYMBOLSTRING, false);
		symbolButton.setIcon(unchecked);
    	symbolButton.setSelectedIcon(checked);
    	symbolButton.setMnemonic(KeyEvent.VK_S);
    	symbolButton.setActionCommand(SYMBOLSTRING);
		
		unigeneButton = new JRadioButton(UNIGENESTRING, false);
		unigeneButton.setIcon(unchecked);
    	unigeneButton.setSelectedIcon(checked);
    	unigeneButton.setMnemonic(KeyEvent.VK_U);
    	unigeneButton.setActionCommand(UNIGENESTRING);

    	locusTagButton = new JRadioButton(LOCUSTAGSTRING, true);
		locusTagButton.setIcon(unchecked);
    	locusTagButton.setSelectedIcon(checked);
    	locusTagButton.setMnemonic(KeyEvent.VK_L);
    	locusTagButton.setActionCommand(LOCUSTAGSTRING);
		

  
  	  //Group the radio buttons.
    	group = new ButtonGroup();
    	group.add(geneIDButton);
    	group.add(symbolButton);
		group.add(unigeneButton);
    	group.add(locusTagButton);

  	  //Register a listener for the radio buttons.
    	geneIDButton.addActionListener(this);
    	symbolButton.addActionListener(this);
		unigeneButton.addActionListener(this);
		locusTagButton.addActionListener(this);
	
	  //Put the radio buttons in a row in a panel.
	
		radioPanel = new JPanel(new GridLayout(1, 0));
		radioPanel.add(geneIDButton);
		radioPanel.add(symbolButton);
		radioPanel.add(unigeneButton);
		radioPanel.add(locusTagButton);
 
	}

    /**
     * Boolean method for checking whether box is checked or not.
     *
     * @return boolean checked or not checked.
     */
    public String getCheckedButton(){
		String id = GENEIDSTRING ;
    	if(geneIDButton.isSelected()){id = GENEIDSTRING ;}
		else if (symbolButton.isSelected()){id = SYMBOLSTRING ;}
		else if (unigeneButton.isSelected()){id = UNIGENESTRING ;}
		else if (locusTagButton.isSelected()){id = LOCUSTAGSTRING ;}
		return id ;
    }
    
	public void disableButtons(){
		geneIDButton.setEnabled(false) ;
		symbolButton.setEnabled(false) ;
		unigeneButton.setEnabled(false) ;
		locusTagButton.setEnabled(false) ;
	}	
	
	public void enableButtons(){
		geneIDButton.setEnabled(true) ;
		symbolButton.setEnabled(true) ;
		unigeneButton.setEnabled(true) ;
		locusTagButton.setEnabled(true) ;
	}	
	
    /*----------------------------------------------------------------
      ACTIONLISTENER-PART.
      ----------------------------------------------------------------*/    

	/**
	 * Method performed when radiobutton clicked.
	 *
	 * @param event event that triggers action, here clicking of the button.
	 */
	
		
     public void actionPerformed(ActionEvent e) {
	   //empty for now
     }

}
