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
 * A simple dialog for creating/destroying/collapsing/expanding meta-nodes.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */

//TODO: Add a tab for meta-node attribute settings.

package metaNodeViewer.ui;
import metaNodeViewer.actions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import cytoscape.*;
import edu.umd.cs.piccolo.PNode;

public class MNcollapserDialog extends JDialog {

  public static final String title = "Meta-Node Abstraction";
  protected JCheckBox recursiveCheckBox;
  protected CollapseSelectedNodesAction createMetaNodeAction;
  protected CollapseSelectedNodesAction collapseAction;
  protected UncollapseSelectedNodesAction expandAction;
  protected UncollapseSelectedNodesAction destroyMetaNodeAction;
  protected static final String CREATE_MN_TITLE = "Create Meta-Node";
  protected static final String DESTROY_MN_TITLE = "Destroy Meta-Node(s)";
  protected static final String COLLAPSE_MN_TITLE = "Collapse to Meta-Node(s)";
  protected static final String EXPAND_MN_TITLE = "Expand Children";
  
  /**
   * Constructor
   */
  public MNcollapserDialog (){
    super(Cytoscape.getDesktop(), title, false);
    initialize();
    setRecursiveOperations(false);
    AbstractMetaNodeMenu.setCollapserDialog(this);
  }//MNcollapserDialog

  /**
   * Sets whether or not the operations (that apply) should be performed recursively or not, false by default.
   */
  public void setRecursiveOperations (boolean recursive_operations){
  	if(this.recursiveCheckBox == null){
  		this.recursiveCheckBox = new JCheckBox("Apply operations recursively");
  	}
    this.recursiveCheckBox.setSelected(recursive_operations);
  }//setRecursiveOperations
  
  /**
   * @return whether or not the operations are to be performed recursively
   */
  public boolean areOperationsRecursive(){
  	return this.recursiveCheckBox.isSelected();
  }//areOperationsRecursive
  
  protected void initialize (){

  	  JButton createMetaNodeButton = new JButton(CREATE_MN_TITLE);
  	  createMetaNodeButton.setToolTipText("Creates a new meta-node with selected nodes as its children and collapses it.");
  	  JButton destroyMetaNodeButton = new JButton(DESTROY_MN_TITLE);
  	  destroyMetaNodeButton.setToolTipText("Permanently removes the selected meta-nodes and expands them.");
      JButton collapseButton = new JButton(COLLAPSE_MN_TITLE);
      collapseButton.setToolTipText("Finds existing parent meta-nodes of selected nodes and collapses them.");
      JButton expandButton = new JButton(EXPAND_MN_TITLE);
      expandButton.setToolTipText("Displays the children of selected meta-nodes.");
      if(this.recursiveCheckBox == null){
      	this.recursiveCheckBox = new JCheckBox("Apply operations recursively");
      }
      this.recursiveCheckBox.setToolTipText("Meta-nodes can have meta-nodes as children.");
      this.createMetaNodeAction = (CollapseSelectedNodesAction)ActionFactory.createCollapseSelectedNodesAction(false,
      		areOperationsRecursive(), CREATE_MN_TITLE);
      createMetaNodeButton.addActionListener(this.createMetaNodeAction);
      
      this.destroyMetaNodeAction = (UncollapseSelectedNodesAction)ActionFactory.createUncollapseSelectedNodesAction(areOperationsRecursive(),
      		false,DESTROY_MN_TITLE);
      destroyMetaNodeButton.addActionListener(this.destroyMetaNodeAction);
      
      this.collapseAction = (CollapseSelectedNodesAction)ActionFactory.createCollapseSelectedNodesAction(true,
      		areOperationsRecursive(),COLLAPSE_MN_TITLE);
      collapseButton.addActionListener(this.collapseAction);
    
      this.expandAction = (UncollapseSelectedNodesAction)ActionFactory.createUncollapseSelectedNodesAction(areOperationsRecursive(),
      		true,EXPAND_MN_TITLE);
      expandButton.addActionListener(this.expandAction);
      
      this.recursiveCheckBox.addActionListener(
      		new AbstractAction (){
      			public void actionPerformed (ActionEvent event){
      				boolean recursive = MNcollapserDialog.this.recursiveCheckBox.isSelected();
      				MNcollapserDialog.this.destroyMetaNodeAction.setRecursiveUncollapse(recursive);
      				MNcollapserDialog.this.expandAction.setRecursiveUncollapse(recursive);
      				MNcollapserDialog.this.collapseAction.setCollapseRecursively(recursive);
      			}//actionPerformed
      		}//AbstractAction
      
      );
    JPanel gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(2,2)); //rows, cols
    gridPanel.add(createMetaNodeButton);
    gridPanel.add(collapseButton);
    gridPanel.add(destroyMetaNodeButton);
    gridPanel.add(expandButton);
    
    JPanel optionsPanel = new JPanel();
    optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));//rows, cols
    optionsPanel.add(this.recursiveCheckBox);
    
    JPanel operationsPanel = new JPanel();
    operationsPanel.setLayout(new BoxLayout(operationsPanel, BoxLayout.Y_AXIS));
    operationsPanel.setBorder(BorderFactory.createTitledBorder("Meta-Node Operations"));
    operationsPanel.add(gridPanel);
    operationsPanel.add(optionsPanel);
    
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    JButton closeWinButton = new JButton("Close");
    closeWinButton.addActionListener(new AbstractAction(){
        public void actionPerformed (ActionEvent e){
          MNcollapserDialog.this.dispose();
        }
      });
    buttonsPanel.add(closeWinButton);
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
    mainPanel.add(operationsPanel);
    mainPanel.add(buttonsPanel);
    setContentPane(mainPanel);
    
  }//initialize
  
  public AbstractAction getCreateMetaNodeAction (){
  	return this.createMetaNodeAction;
  }//getCreateMetaNodeAction
  
  public AbstractAction getDestroyMetaNodeAction (){
  	return this.destroyMetaNodeAction;
  }//getDestroyMetaNodeAction
  
  public AbstractAction getCollapseMetaNodesAction (){
  	return this.collapseAction;
  }//getCollapseMetaNodesAction
  
  public AbstractAction getExpandChildrenAction (){
  	return this.expandAction;
  }//getExpandChildrenAction
  
  /**
   * @return a JMenu with operations to create new meta-nodes, destroy meta-nodes, collapse to meta-nodes and expand children.
   */
  public JMenu getMenu (Object[] args, PNode node){
  	JMenu menu = new JMenu("Meta-Node Operations");
  	menu.add(this.createMetaNodeAction);
  	menu.add(this.destroyMetaNodeAction);
  	menu.add(this.collapseAction);
  	menu.add(this.expandAction);
  	return menu;
  }//getMenu
}//class MNcollapserDialog
