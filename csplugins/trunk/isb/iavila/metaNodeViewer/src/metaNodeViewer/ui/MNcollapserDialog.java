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
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */
package metaNodeViewer.ui;
import cytoscape.view.CyWindow;
import metaNodeViewer.actions.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class MNcollapserDialog extends JDialog {

  public static final String title = "Node Collapser";
  protected JCheckBox rememberCheckBox;
  protected JCheckBox recursiveCheckBox;
  protected JCheckBox defaultNameCheckBox;
  protected JCheckBox collapseParentsCheckBox;
  protected CollapseSelectedNodesAction collapseAction;
  protected UncollapseSelectedNodesAction expandAction;
  protected AbstractAction resetAction;
  
  /**
   * Constructor
   */
  public MNcollapserDialog (CyWindow cy_window){
    super(cy_window.getMainFrame(), title);
    initialize(cy_window);
  }//MNcollapserDialog

  /**
   * Sets the selected state of the checkboxes that correspond
   * to the given variables, they are all false (except for use_default_names) by default.
   */
  public void setOptions (boolean collapse_existent_parents,
                          boolean use_default_names,
                          boolean remember_hierarchy,
                          boolean recursive_expand
                          ){
    this.collapseParentsCheckBox.setSelected(collapse_existent_parents);
    this.defaultNameCheckBox.setSelected(use_default_names);
    this.rememberCheckBox.setSelected(remember_hierarchy);
    this.recursiveCheckBox.setSelected(recursive_expand);
  }//setOptions

  
  protected void initialize (CyWindow cy_window){
    
    JButton collapseButton = new JButton("Collapse Selected Nodes");
    this.collapseAction = 
      (CollapseSelectedNodesAction)ActionFactory.createCollapseSelectedNodesAction(cy_window, false);
    collapseButton.addActionListener(this.collapseAction);
    
    JButton expandButton = new JButton("Expand Selected Nodes");
    this.expandAction =
      (UncollapseSelectedNodesAction)ActionFactory.createUncollapseSelectedNodesAction(cy_window, 
                                                                                  false,
                                                                                  false);
    expandButton.addActionListener(this.expandAction);
    
    JButton resetButton = new JButton("Reset Graph");
    //TODO: Create an action for reset for now popup 'unimplemented' dialog
    resetButton.addActionListener(
                                  new AbstractAction(){
                                    public void actionPerformed (ActionEvent e){
                                      JOptionPane.showMessageDialog(MNcollapserDialog.this,
                                                                    "Not implemented yet."
                                                                    );
                                    }
                                  }
                                  );
    
    this.rememberCheckBox = new JCheckBox("Remember parents after expanding");
    // isSelected == true means temporary
    // isSelected == false means not temporary
    // TODO: If not remember, then we need to delete the meta-nodes from RootGraph that
    // have been expanded (don't do this here, maybe do it in UncollapseSelectedNodesAction).
    this.rememberCheckBox.addActionListener(
      new AbstractAction(){
        public void actionPerformed (ActionEvent e){
          MNcollapserDialog.this.expandAction.setTemporaryUncollapse(
                                     MNcollapserDialog.this.rememberCheckBox.isSelected());
        }
      });
    this.recursiveCheckBox = new JCheckBox("Recursive Expand");
    this.recursiveCheckBox.addActionListener(
     new AbstractAction(){
       public void actionPerformed (ActionEvent e){
         MNcollapserDialog.this.expandAction.setRecursiveUncollapse(
                               MNcollapserDialog.this.recursiveCheckBox.isSelected());
       }
     });
    
    this.defaultNameCheckBox = new JCheckBox("Use default names for parents");
    this.defaultNameCheckBox.addActionListener(
     new AbstractAction(){
       public void actionPerformed (ActionEvent e){
         boolean isSelected = MNcollapserDialog.this.defaultNameCheckBox.isSelected();
         if(!isSelected){
           JOptionPane.showMessageDialog(MNcollapserDialog.this,
                                         "Warning: Meta-nodes will not have names!");
         }
         MNcollapserDialog.this.collapseAction.setAssignDefaultNames(isSelected);
       }
     });
    this.defaultNameCheckBox.setSelected(true); // default  
    
    this.collapseParentsCheckBox = new JCheckBox("Collapse existent parents");
    this.collapseParentsCheckBox.addActionListener(
     new AbstractAction(){
       public void actionPerformed (ActionEvent e){
         MNcollapserDialog.this.collapseAction.setCollapseExistentParents(
                               MNcollapserDialog.this.collapseParentsCheckBox.isSelected());
       }
     });
    
    JPanel borderedPanel = new JPanel();
    borderedPanel.setLayout(new GridLayout(5,2)); //rows, cols
    borderedPanel.setBorder(BorderFactory.createEtchedBorder());
    
    borderedPanel.add(collapseButton);
    borderedPanel.add(this.collapseParentsCheckBox);
    borderedPanel.add(Box.createGlue());
    borderedPanel.add(this.defaultNameCheckBox);
    borderedPanel.add(expandButton);
    borderedPanel.add(this.rememberCheckBox);
    borderedPanel.add(Box.createGlue());
    borderedPanel.add(this.recursiveCheckBox);
    borderedPanel.add(resetButton);
     
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
    mainPanel.add(borderedPanel);
    JButton closeWinButton = new JButton("Close");
    closeWinButton.addActionListener(new AbstractAction(){
        public void actionPerformed (ActionEvent e){
          MNcollapserDialog.this.dispose();
        }
      });
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(closeWinButton);
    mainPanel.add(buttonPanel);
    setContentPane(mainPanel);
    
  }//initialize
}//class MNcollapserDialog
