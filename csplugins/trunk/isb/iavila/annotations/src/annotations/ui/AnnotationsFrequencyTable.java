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
 * A <code>javax.swing.JFrame</code> that displays two columns: annotations, and their
 * frequencies (how many times they appear as overrepresented annotations).
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org> 
 */

package annotations.ui;
import annotations.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import cytoscape.data.annotation.*;

public class AnnotationsFrequencyTable extends JFrame{
  
  protected ModuleAnnotationsMap map;
  protected Annotation annotation;
  protected AnnotationsTreeView treeView;
  protected JTable table;
  protected String [][] data;
  protected boolean mostSpecific;
  protected boolean recursiveCount;
  protected ModuleAnnotationsTable moduleAnnotsTable;

  /**
   * Constructor.
   */
  public AnnotationsFrequencyTable (){
    super("Frequency Table");
  }//constructor
  
  /**
   * Prepares this table for being displayed by giving it the parameters that it
   * needs.
   *
   * @param _map the ModuleAnnotationsMap that contains the annotations that will
   * be counted
   * @param _annotation the Annotation that contains the Ontology from which the annotations
   * come from
   * @param most_specific whether or not annotations that have parent-child relationships (or
   * container-contained) and have the same p-values should be screened so that only the child
   * annotations are shown
   * @param recursive_count whether or not frequencies for annotations that are ancestors of other
   * overrepresented annotations whould include their descendant annotations in the frequency count
   * (for exampe, 'biological process' includes all the counts of itself plus 'cellular development',
   * and 'celluar development' children as well)
   * @param module_annots_table the ModuleAnnotationsTable from which this table came from
   * it can be null
   * @param table_actions buttons will be created for each of these actions and added to the button
   * panel of this table (can be null)
   * @param tree_view_actions buttons will be created for each of these actions and added to the button
   * panel of the tree view (can be null)
  */
  public void prepare (ModuleAnnotationsMap _map, 
                       Annotation _annotation, 
                       boolean most_specific,
                       boolean recursive_count,
                       ModuleAnnotationsTable module_annots_table,
                       AbstractAction [] table_actions,
                       AbstractAction [] tree_view_actions
                       ){
    setTitle("Frequency Table: most specific = " + most_specific + 
             ", cumulative freq = " + recursive_count);
    this.map = _map;
    this.annotation = _annotation;
    this.mostSpecific = most_specific;
    this.recursiveCount = recursive_count;
    this.moduleAnnotsTable = module_annots_table;
    create(table_actions,tree_view_actions);
  }//AnnotationsFrequencyTable
  
  /**
   * Creates the table.
   *
   * @param table_actions buttons will be created for each of these actions and added to the button
   * panel of this table (can be null)
   * @param tree_view_actions buttons will be created for each of these actions and added to the button
   * panel of the tree view (can be null)
   */
  protected void create (AbstractAction [] table_actions,
                         AbstractAction [] tree_view_actions){
    
    // Start clean
    getContentPane().removeAll();
    
    final SortedMap termToFreq = this.map.getAnnotationsFrequency(this.mostSpecific,
                                                                  this.recursiveCount,
                                                                  this.annotation.getOntology());
    // TODO: Somehow handle this
    if(termToFreq == null || termToFreq.size() == 0){
      //JOptionPane.showMessageDialog(ModuleAnnotationsTable.this,"No annotations.");
      System.err.println("AnnotationsFrequencyTable: termToFreq is null or empty");
    }
      
    // Create the data for the table
    this.data = new String [termToFreq.size()][2];
    Iterator it = termToFreq.keySet().iterator();
    int i = 0;
    while(it.hasNext()){
      OntologyTerm term = (OntologyTerm)it.next();
      Integer freq = (Integer)termToFreq.get(term);
      if(term == null){
        System.err.println("The term is null");
        this.data[i][0] = "null";
        this.data[i][1] = "-";
        i++;
        continue;
      }
      if(freq == null){
        System.err.println("The Integer for term " + term.getName() + " is null.");
        this.data[i][0] = term.getName();
        this.data[i][1] = "null";
        i++;
        continue;
      }
      this.data[i][0] = term.getName();
      this.data[i][1] = freq.toString();
      i++;
    }//while it
      
    String [] columnNames = {"ANNOTATION","FREQUENCY"};
    this.table = new JTable(this.data, columnNames);
    this.table.setPreferredScrollableViewportSize(new Dimension(550, 250));
         
    //Create the scroll pane and add the table to it. 
    JScrollPane scrollPane = new JScrollPane(this.table);
    
    //Buttons
    JPanel buttonPanel = new JPanel();
    JButton saveToFile = new JButton("Save To File...");
    saveToFile.addActionListener( 
                                 new AbstractAction(){
                                   public void actionPerformed (ActionEvent e){
                                     ModuleAnnotationsTable.saveToFile(data,
                                                                       AnnotationsFrequencyTable.this);
                                   }
                                 });
    buttonPanel.add(saveToFile);
    JButton treeViewButton = new JButton("Show Tree View");
    this.treeView = new AnnotationsTreeView(termToFreq,tree_view_actions);
    treeViewButton.addActionListener(
                                     new AbstractAction(){
                                       public void actionPerformed (ActionEvent e){
                                         treeView.pack();
                                       treeView.setLocationRelativeTo(AnnotationsFrequencyTable.this);
                                       treeView.setVisible(true);
                                       }//actionPerformed
                                     }
                                     );
    buttonPanel.add(treeViewButton);
    
    if(this.moduleAnnotsTable != null){
      JButton selectButton = new JButton("Select from module annotations table");
      selectButton.addActionListener(
                                     new AbstractAction(){
                                       public void actionPerformed (ActionEvent e){
                                         String [][] sr = moduleAnnotsTable.getSelectedRows();
                                         HashSet annots = new HashSet();
                                         for(int i = 0; i < sr.length; i++){
                                           annots.add(sr[i][1]);
                                         }
                                         selectRowsWithAnnotations (
                                                 (String[])annots.toArray(new String[annots.size()])
                                                 );
                                         
                                       }
                                     }
                                     );
      buttonPanel.add(selectButton);
    }// moduleAnnotsTable != null

    if(table_actions != null){
      for(i = 0; i < table_actions.length; i++){
        JButton newButton = new JButton(table_actions[i]);
        table_actions[i].putValue(String.valueOf(newButton.hashCode()) , this);
        buttonPanel.add(newButton);
      }
    }
    
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(
                                  new AbstractAction (){
                                    public void actionPerformed (ActionEvent e){
                                      AnnotationsFrequencyTable.this.dispose();
                                      AnnotationsFrequencyTable.this.treeView.dispose();
                                    }//actionPerformed
                                  });
    buttonPanel.add(closeButton);
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
  }//create

  /**
   * Selects the rows in the table that have the given annotation names
   */
  public void selectRowsWithAnnotations (String [] annotations){
    for(int i = 0; i < annotations.length; i++){
      for(int j = 0; j < this.data.length; j++){
        if(annotations[i].equals(this.data[j][0])){
          this.table.addRowSelectionInterval(j,j);
          break;
        }
      }//for j
    }//for i
  }//selectRowsWithAnnotations

  /**
   * @return an array of selected rows in the frequency table, the 1st column corresponds
   * to the annotation name, and the 2nd corresponds to the frequency
   */
  public String [][] getSelectedRows (){
    if(this.table == null){
      return new String [0][0];
    }
    
    int [] selectedRowIndices = this.table.getSelectedRows();
    if(selectedRowIndices.length == 0){
      return new String[0][0];
    }
    
    String [][] selectedRows = new String[selectedRowIndices.length][];
    for(int i = 0; i < selectedRowIndices.length; i++){
      selectedRows[i] = this.data[selectedRowIndices[i]];
    }//for i
    
    return selectedRows;
  }//getSelectedRows

  // --- Internal class, possibly separate class at some point --//
  public class AnnotationsTreeView extends JFrame {
    
    SortedMap termToFreq;
    JTree jtree;
    
    /**
     * @param term_To_Freq a SortedMap whose keys are OntologyTerm, and values are Integer
     * that represent the frequency of the OntologyTerms.
     * @param button_actions buttons will be created for each of these actions and added to the
     * button panel of the annotations tree viewer (can be null, in which case, no extra buttons
     * will be added)
     */
    AnnotationsTreeView (SortedMap term_To_Freq, AbstractAction [] button_actions){
      super("Annotations Tree View");
      this.termToFreq = term_To_Freq;
      create(button_actions);
    }//AnnotationsTreeView

    /**
     * @return the <code>ModuleAnnotationsMap</code> of the <code>AnnotationsFrequencyTable</code>
     * in which this <code>AnnotationsTreeView</code> is contained
     */
    public ModuleAnnotationsMap getModuleAnnotationsMap (){
      return AnnotationsFrequencyTable.this.map;
    }//getModuleAnnotationsMap

    protected void create (AbstractAction [] button_actions){
      
      String text = "Overrepresented annotations";
      DefaultMutableTreeNode rootNode =
        new DefaultMutableTreeNode(text);
      
      OntologyTerm [] topTerms = getTopTerms();
      for(int i = 0; i < topTerms.length; i++){
        createNodes(rootNode, topTerms[i]);
      }
      
      this.jtree = new JTree(rootNode);
      JScrollPane treeView = new JScrollPane(this.jtree);
      
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
      mainPanel.add(treeView);
      
      JPanel buttonPanel = new JPanel();
      JButton saveSelectedButton = new JButton("Save selected to file...");
      saveSelectedButton.addActionListener(
                                           new AbstractAction(){
                                             public void actionPerformed (ActionEvent e){
                                               saveSelectedToFile();
                                             }
                                           }
                                           );
      buttonPanel.add(saveSelectedButton);

      JButton selectButton = new JButton("Select from frequency table");
      selectButton.addActionListener(
                                     new AbstractAction (){
                                       public void actionPerformed (ActionEvent e){
                                         String[][] r=AnnotationsFrequencyTable.this.getSelectedRows();
                                         HashSet annots = new HashSet();
                                         for(int i = 0; i < r.length; i++){
                                           annots.add(r[i][0]);
                                         }
                                         jtree.clearSelection();
                                         jtree.setExpandsSelectedPaths(true);
                                         selectTreeNodesWithAnnotations(
                                                  (String[])annots.toArray(new String[annots.size()]),
                                                  (DefaultMutableTreeNode)jtree.getModel().getRoot()
                                                   );
                                       }//actionPerformed
                                     }
                                     );
      buttonPanel.add(selectButton);
      
      if(button_actions != null){
        for(int i = 0; i < button_actions.length; i++){
          JButton newButton = new JButton(button_actions[i]);
          button_actions[i].putValue(String.valueOf(newButton.hashCode()) , this);
          buttonPanel.add(newButton);
        }
      }
            
      mainPanel.add(buttonPanel);
      //mainPanel.setPreferredSize(new Dimension(550,250));
      
      getContentPane().add(mainPanel);
      setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    }//create

    /**
     * Selects the tree nodes with the given annotations, if a tree node for an annotations
     * is not displayed, then its closest displayed ancestor is selected.
     */
    public void selectTreeNodesWithAnnotations (String [] annotations, DefaultMutableTreeNode root){
      
      Object userObject = root.getUserObject();
      if(userObject instanceof OntologyTreeNodeObject){
        String ontoName = ((OntologyTreeNodeObject)userObject).getOntologyTerm().getName();
        for(int i = 0; i < annotations.length; i++){
          if(ontoName.equals(annotations[i])){
            TreeNode[] path = root.getPath();
            TreePath treePath = new TreePath(path);
            this.jtree.addSelectionPath(treePath);
            break;
          }
        }//for i
      }// if the user object is an OntologyTreeNodeObject
      
      int numChildren = root.getChildCount();
      for(int i = 0; i < numChildren; i++){
        DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getChildAt(i);
        selectTreeNodesWithAnnotations(annotations,child);
      }//for i
     
    }//selectTreeNodesWithAnnotations
    
    /**
     * Saves the selected annotations tab separated to their frequencies to a file.
     */
    public void saveSelectedToFile (){
      OntologyTerm [] selectedTerms = getSelectedTerms();
      String [][] data = new String [selectedTerms.length][2];
      for(int i = 0; i < selectedTerms.length; i++){
        Integer freq = (Integer)this.termToFreq.get(selectedTerms[i]);
        data[i][0] = selectedTerms[i].getName();
        data[i][1] = freq.toString();
      }//for i
      
      ModuleAnnotationsTable.saveToFile(data, this);
      
    }//saveSelectedToFile
    
    /**
     * @return an array of OntologyTerm objects that are in termToFreq and that
     * have not parents in termToFreq.
     */
    protected OntologyTerm [] getTopTerms (){
      
      Set keyset = this.termToFreq.keySet();
      OntologyTerm [] terms = (OntologyTerm[])keyset.toArray(new OntologyTerm[keyset.size()]);
      ArrayList topNodes = new ArrayList();
     
      if(AnnotationsFrequencyTable.this.recursiveCount){
        
        for(int i = 0; i < terms.length; i++){
          boolean isTopNode = true;
          for(int j = i - 1; j >= 0; j--){
            if(terms[i].isChildOfOrContainedIn(terms[j])){
              isTopNode = false;
              break;
            }
          }//for j
          if(isTopNode){
            topNodes.add(terms[i]);
          }
        }//for i
      }else{
        // not recursive count (which means, not sorted from parents to children)
        for(int i = terms.length - 1; i >= 0; i--){
          boolean isTopNode = true;
          for(int j = terms.length - 1; j >= 0; j--){
            if(terms[i].isChildOfOrContainedIn(terms[j])){
              isTopNode = false;
              break;
            }
          }//for j
          if(isTopNode){
            topNodes.add(terms[i]);
          }
        }//for i
        
      }
      return (OntologyTerm [])topNodes.toArray(new OntologyTerm[topNodes.size()]);
    }//getTopTerms
    
    /**
     * Creates the tree.
     */
    protected void createNodes (DefaultMutableTreeNode root_node, OntologyTerm child_term){
      
      Integer frequency = (Integer)this.termToFreq.get(child_term);
      OntologyTreeNodeObject ontNode = 
        new OntologyTreeNodeObject(child_term,frequency.intValue());      
            
      DefaultMutableTreeNode childNode = 
        new DefaultMutableTreeNode(ontNode);
      root_node.add(childNode);

      Set keyset = this.termToFreq.keySet();
      OntologyTerm [] terms = (OntologyTerm[])keyset.toArray(new OntologyTerm[keyset.size()]);
      
      for(int i = 0; i < terms.length; i++){
        if(child_term.isParentOrContainerOf(terms[i])){
          createNodes(childNode, terms[i]);
        }
      }//for i
      
    }//createNodes
    
    /**
     * @return the JTree that represents the annotations
     */
    public JTree getTree (){
      return this.jtree;
    }//getTree
    
    /**
     * @return an array of OntologyTerm objects that are currently selected in the tree
     */
    public OntologyTerm [] getSelectedTerms (){
      TreePath [] selectedPaths = this.jtree.getSelectionPaths();
      ArrayList list = new ArrayList();      
      for(int i = 0; i < selectedPaths.length; i++){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPaths[i].getLastPathComponent();
        Object userObject = node.getUserObject();
        if(userObject instanceof OntologyTreeNodeObject){
          list.add( ((OntologyTreeNodeObject)userObject).getOntologyTerm() );
        }
        
      }//for i
      return (OntologyTerm[])list.toArray(new OntologyTerm[list.size()]);
    }//getSelectedTerms
    
  }//AnnotationsTreeView

  //--- Internal class, an object that holds an OntologyTerm, its frequency, and its name ---//
  protected class OntologyTreeNodeObject{
    protected OntologyTerm ontTerm;
    protected int frequency;
    protected String name;
    
    OntologyTreeNodeObject (OntologyTerm ont_term, int freq){
      this.ontTerm = ont_term;
      this.frequency = freq;
      this.name = this.ontTerm.getName() + " (" + Integer.toString(this.frequency) + ")";
    }
    
    public OntologyTerm getOntologyTerm (){
      return this.ontTerm;
    }//getOntologyTerm
    
    public int getFrequency (){
      return this.frequency;
    }//getFrequency
    
    public String getName (){
      return this.name;
    }//getName
    
    public String toString (){
      return this.name; 
    }
  }//OntologyTreeNode
  
}//AnnotationsFrequencyTable
