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
 * A plug-in for calculating and visualizing biomodules.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */
package biomodules;
import cytoscape.*;
import cytoscape.plugin.*;
import biomodules.algorithm.rgalgorithm.*;
import biomodules.algorithm.rgalgorithm.gui.*;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

public class BiomodulesPlugIn extends CytoscapePlugin{

  /**
   * Constructor
   */
  public BiomodulesPlugIn (){
   
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
             new AbstractAction("Calculate Biomodules..."){
               public void actionPerformed (ActionEvent e){
                 CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
                 if(cyNetwork == null || cyNetwork.getNodeCount() == 0){
                   JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                                 "Please load a network.",
                                                 "Error",
                                                 JOptionPane.ERROR_MESSAGE);
                   return;
                 }
                 RGAlgorithmData algorithmData = RGAlgorithm.getClientData(cyNetwork);
                 RGAlgorithmGui dialog = algorithmData.getGUI();
                 if(dialog == null){
                   dialog = new RGAlgorithmGui(cyNetwork);
                   algorithmData.setGUI(dialog);
                 }
                 dialog.pack();
                 dialog.setLocationRelativeTo(Cytoscape.getDesktop());
                 dialog.setVisible(true);
               }
             });
  }//BiomodulesPlugIn
  
  /**
   * @return a String describing the plugin.
   */
  public String toString (){
    return "A plugin that calculates and visualizes biological modules in a network.";
  }//toString

}//class BiomodulesPlugIn
