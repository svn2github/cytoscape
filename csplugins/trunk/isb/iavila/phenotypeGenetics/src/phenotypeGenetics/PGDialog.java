/**  Copyright (c) 2005 Institute for Systems Biology
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
package phenotypeGenetics;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import cytoscape.*;

/**
 * A simple dialog for the Phenotype Genetics plug-in.
 *
 * @author Iliana Avila-Campillo
 */

public class PGDialog extends JDialog{

  protected PhenotypeGenetics phenotypeGenetics;
  
  /**
   * Constructor.
   *
   * @param phenotype_genetics the main phenotype genetics object that
   * will perform all the main actions
   */
  public PGDialog (PhenotypeGenetics phenotype_genetics){
    setTitle("Phenotype Genetics Analysis");
    setPhenotypeGeneticsObject(phenotype_genetics);
    create();
  }//PGDialog

  /**
   * Sets the <code>PhenotypeGenetics</code> object that will
   * respond to requests made to this dialog.
   */
  public void setPhenotypeGeneticsObject (PhenotypeGenetics phenotype_genetics){
    this.phenotypeGenetics = phenotype_genetics;
  }//setPhenotypeGeneticsObject

  /**
   * Creates the dialog.
   */
  protected void create (){
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    
    JPanel actionsPanel = new JPanel(new GridLayout(3,1)); //rows, cols
    Border titledBorder = 
      BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Operations");
    actionsPanel.setBorder(titledBorder);

    JButton distributionButton =  
      new JButton("Show distribution of interaction classes by allele");
    distributionButton.addActionListener(
        new AbstractAction(){
          public void actionPerformed (ActionEvent event){
            phenotypeGenetics.doNodeDist();
          }//actionPerformed
        }//AbstractAction
        );

    JButton neighborsButton = new JButton("Find Mutual Information Pairs...");
    neighborsButton.addActionListener(
        new AbstractAction (){
          public void actionPerformed (ActionEvent event){
            phenotypeGenetics.doMutualInfo();
          }//actionPerformed
        }//AbstractAction
        );
  
    JButton statementsButton = new JButton("Make Biological-Statements...");
    statementsButton.addActionListener(
        new AbstractAction (){
          public void actionPerformed (ActionEvent event){
            phenotypeGenetics.doStatementMaking();
          }//actionPerformed
        }//AbstractAction
        );

    actionsPanel.add(distributionButton);
    actionsPanel.add(statementsButton);
    actionsPanel.add(neighborsButton); 

    mainPanel.add(actionsPanel);
    
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(
     new AbstractAction (){
       public void actionPerformed (ActionEvent event){
         PGDialog.this.dispose();
       }//actionPerformed
     }//AbstractAction
     );
    buttonsPanel.add(closeButton);
    
    mainPanel.add(buttonsPanel);
    
    setContentPane(mainPanel);
    
  }//create

  
}//class PGDialog
