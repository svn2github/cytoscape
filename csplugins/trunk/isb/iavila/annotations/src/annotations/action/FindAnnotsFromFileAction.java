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
 * This class defines an action that reads a file with a description of groups of genes for which 
 * overrepresented annotations are to be found, and then pops-up a dialog for user-settings to
 * calculate the annotations which are then written to an output file.
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org, iliana.avila@gmail.com>
 */

package annotations.action;
import cytoscape.data.servers.*;
import annotations.FileReader;
import annotations.ui.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class FindAnnotsFromFileAction extends AbstractAction {
  
  protected BioDataServer bioDataServer;
  protected ModuleAnnotationsDialog dialog;
  protected Component parentComponent;

  /**
   * Constructor.
   *
   * @param component the parent java.awt.Component for the dialogs
   * @param bio_data_server the server from which available annotations can be ontained
   */
  public FindAnnotsFromFileAction (Component component,
                                   BioDataServer bio_data_server){
    super("Find annotations from file...");
    this.bioDataServer = bio_data_server;
    this.parentComponent = component;
  }//FindAnnotsFromFileAction
  
  
  /**
   * Asks the user for an input file, for the desired annotations,
   * and then, calculates the overrepresented annotations for the groups 
   * of genes in the file, and, writes the output to an outfile.
   */
  public void actionPerformed (ActionEvent event){
    
    // Ask for an input file
    String homedir = System.getProperty("user.home");
    JFileChooser fileChooser = new JFileChooser(homedir);
    int returnVal = fileChooser.showOpenDialog(this.parentComponent);
    final String infile;
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      System.err.println("You chose to open this file: " +
                         fileChooser.getSelectedFile().getName());
      infile = fileChooser.getSelectedFile().getAbsolutePath();
    }else{
      return;
    }
    
    // Read the input file
    String [][] geneGroups = FileReader.readFile(infile);
    if(geneGroups == null){
      JOptionPane.showMessageDialog(null,
                                    "Errors were encountered while reading " + infile);
      return;
    }
    // Create a ModuleAnnotationsCalculator with the read output from the file
    ArrayList ids = new ArrayList();
    for(int i = 0; i < geneGroups.length; i++){
      ids.add(geneGroups[i][0]);
    }
    
    // Create a ModuleAnnotationsDialog with the ModuleAnnotationsCalculator and pop-it-up
    if(this.dialog == null){
      this.dialog = new ModuleAnnotationsDialog(ids.toArray(), geneGroups, false);
    }
    this.dialog.pack();
    this.dialog.setLocationRelativeTo(this.parentComponent);
    this.dialog.setVisible(true);
    
  }//actionPerformed
  
}//class FindAnnotsFromFileAction
