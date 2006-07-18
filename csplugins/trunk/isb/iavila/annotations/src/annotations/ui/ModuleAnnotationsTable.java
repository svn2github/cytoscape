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
 * A <code>javax.swing.JFrame</code> that displays a table with module names, their
 * annotations, and the corresponding p-values.
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org>
 */

package annotations.ui;

import annotations.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import cytoscape.data.annotation.*;
import cytoscape.*;
import cytoscape.data.*;

public class ModuleAnnotationsTable extends JFrame {

  /**
   * Whether the table should contain only the most 
   * specific <code>ModuleAnnotation</code> objects
   * for the modules.
   */
  protected boolean mostSpecific;

  /**
   * The maximum number of annotations per module
   * that the table should display.
   */
  protected int maxNumAnnotations;

  /**
   * The data contained in the table.
   */
  protected String [][] data;

  /**
   * If maxNumAnnotations is set to this value,
   * all annotations are shown.
   */
  public static final int SHOW_ALL_ANNOTATIONS = Integer.MAX_VALUE;

  /**
   * The JTable that contains the data.
   */
  protected JTable jTable;

  /**
   * The AnnotationsFrequencyTable for this ModuleAnnotationsTable
   */
  protected AnnotationsFrequencyTable freqTable;
  
  /**
   * The ModuleAnnotationsMap that contains the information that this table displays.
   */
  protected ModuleAnnotationsMap annotationsMap;
  
  /**
   * The cytoscape.data.annotations.Annotation that contains the ontology.
   */
  protected Annotation annotation;
  
  /**
   * The name of the node attribute to use to get the names of nodes to display in the table
   * By default, this is "commonName" for historical reasons
   */
  protected String nodeNameAttribute = "commonName";
  
  /**
   * Constructor.&nbsp; Creates a table that displays the annotations
   * in <code>annotations_map</code>.&nbsp;The column headers of the 
   * table are MODULE NAME, CLASSIFICATION, P-VALUE.&nbsp;The module
   * names are obtained from the given <code>Map</code> of module ids
   * (<code>Integer</code> keys) to <code>String</code> objects.
   *
   * @param annotation the Annotation that contains the Ontology
   * @param annotations_map the <code>ModuleAnnotationMap</code> that 
   * contains the annotations to be displayed, if the keys in this map are CyNodes,
   * then their Semantics.COMMON_NAME attribute will be used in the table for identifying that module
   * @param make_specific if true, only the most specific <code>ModuleAnnotation</code>s are represented 
   * in the table
   * @param max_num_annotaions the maximum number of annotations per module that the table should display.
   * @param title the title for the table
   * @param buttonActions an array of actions each of which will be added to a <code>JButton</code> 
   * that will be at the bottom of the table by default, only a "Save To file..." button is added
   * <p>
   * So that the <code>AbstractAction</code> objects contained in <code>buttonActions</code>
   * can access the <code>ModuleAnnotationsTable</code> that contains the <code>JButton</code>
   * that was clicked on, the <code>JButton</code> hash code is mapped to 
   * <code>ModuleAnnotationsTable</code> objects. For an action to get a hold of the associated 
   * <code>ModuleAnnotationsTable</code> do the following:
   *
   * public void actionPerformed (ActionEvent event){
   *   Object source = event.getSource();
   *   if(source instanceof JButton){
   *      JButton button = (JButton)source;
   *      int hashCode = button.hashCode();
   *      Object value = getValue(String.valueOf(hashCode));
   *      if(value instanceof ModuleAnnotationsTable){
   *        ModuleAnnotationsTable table = (ModuleAnnotationsTable)value;
   *        // Do whatever needs to be done
   *      }
   *   }
   * }
   */
  public ModuleAnnotationsTable (Annotation annotation,
                                 ModuleAnnotationsMap annotations_map,
                                 boolean make_specific,
                                 int max_num_annotaions,
                                 String title,
                                 AbstractAction [] buttonActions){
    super(title);
    this.mostSpecific = make_specific;
    this.annotationsMap = annotations_map;
    this.annotation = annotation;
    setMaxNumAnnotations(max_num_annotaions);
    create(annotations_map, buttonActions, annotation, null, null);
  }//ModuleAnnotationsTable

  /**
   * Constructor.&nbsp; Creates a table that displays the annotations
   * in <code>annotations_map</code>.&nbsp;The column headers of the 
   * table are MODULE NAME, CLASSIFICATION, P-VALUE.&nbsp;The module
   * names are obtained from the given <code>Map</code> of module ids
   * (<code>Integer</code> keys) to <code>String</code> objects.
   *
   * @param annotation the Annotation that contains the Ontology
   * @param annotations_map the <code>ModuleAnnotationMap</code> that 
   * contains the annotations to be displayed, if the keys in this map are CyNodes,
   * then their Semantics.COMMON_NAME attribute will be used in the table for identifying that module
   * @param make_specific if true, only the most specific <code>ModuleAnnotation</code>s are represented 
   * in the table
   * @param max_num_annotaions the maximum number of annotations per module that the table should display.
   * @param title the title for the table
   * @param buttonActions an array of actions each of which will be added to
   * a <code>JButton</code> that will be at the bottom of the table
   * by default, only a "Save To file..." button is added
   * @param freq_table_actions an array of actions each of which will be added to a 
   * <code>JButton</code> that will be located in the button panel
   * of the <code>AnnotationsFrequencyTable</code>
   * @param annots_tree_view_actions an array of actions each of which will be added to a
   * <code>JButton</code> that will be located in the button
   * panel of the <code>AnnotationsTreeView</code> that belongs
   * to the <code>AnnotationsFrequencyTable</code>
   * <p>
   * So that the <code>AbstractAction</code> objects contained in <code>buttonActions</code>
   * can access the <code>ModuleAnnotationsTable</code> that contains the <code>JButton</code>
   * that was clicked on, the <code>JButton</code> hash code is mapped to 
   * <code>ModuleAnnotationsTable</code> objects. For an action to get a hold of the associated 
   * <code>ModuleAnnotationsTable</code> do the following:
   *
   * public void actionPerformed (ActionEvent event){
   *   Object source = event.getSource();
   *   if(source instanceof JButton){
   *      JButton button = (JButton)source;
   *      int hashCode = button.hashCode();
   *      Object value = getValue(String.valueOf(hashCode));
   *      if(value instanceof ModuleAnnotationsTable){
   *        ModuleAnnotationsTable table = (ModuleAnnotationsTable)value;
   *        // Do whatever needs to be done
   *      }
   *   }
   * }
   * The same applies to buttons for the <code>AnnotationsFrequencyTable</code> and 
   * <code>AnnotationsTreeView</code>, except that the class of the Object returned
   * by getValue() is the same as the class of the dialog in which the button is located
   * ( a <code>AnnotationsFrequencyTable</code> or a <code>AnnotationsTreeView</code>).
   */
  public ModuleAnnotationsTable (Annotation annotation,
                                 ModuleAnnotationsMap annotations_map,
                                 boolean make_specific,
                                 int max_num_annotaions,
                                 String title,
                                 AbstractAction [] buttonActions,
                                 AbstractAction [] freq_table_actions,
                                 AbstractAction [] annots_tree_view_actions){
    super(title);
    this.mostSpecific = make_specific;
    this.annotationsMap = annotations_map;
    this.annotation = annotation;
    setMaxNumAnnotations(max_num_annotaions);
    create(annotations_map, buttonActions, annotation, freq_table_actions,annots_tree_view_actions);
  }//ModuleAnnotationsTable
  
  /**
   * Same as previous constructor, except it takes an additional parameter to set the name of the node
   * attribute to use to display the names of modules and module members
   * @param annotation
   * @param annotations_map
   * @param make_specific
   * @param max_num_annotaions
   * @param title
   * @param buttonActions
   * @param freq_table_actions
   * @param annots_tree_view_actions
   * @param node_label_attribute a String node attribute
   */
  public ModuleAnnotationsTable(Annotation annotation,
			ModuleAnnotationsMap annotations_map, boolean make_specific,
			int max_num_annotaions, String title,
			AbstractAction[] buttonActions,
			AbstractAction[] freq_table_actions,
			AbstractAction[] annots_tree_view_actions,
			String node_label_attribute) {
		super(title);
		this.mostSpecific = make_specific;
		this.annotationsMap = annotations_map;
		this.annotation = annotation;
		setMaxNumAnnotations(max_num_annotaions);
		setNodeLabelAttribute(node_label_attribute);
		create(annotations_map, buttonActions, annotation, freq_table_actions,
				annots_tree_view_actions);
	}// ModuleAnnotationsTable
  
  /**
	 * @return the ModuleAnnotationsMap that contains the module annotations
	 *         that this table displays
	 */
  public ModuleAnnotationsMap getModuleAnnotationsMap (){
  	return this.annotationsMap;
  }//getModuleAnnotationsMap
  
  /**
   * @return the cytoscape.data.annotations.Annotation that contains annotations in this table.
   */
  public Annotation getAnnotation (){ return this.annotation;}//getAnnotation

  /**
   * @return an array containing the rows that are selected in the table, the first column
   * contains the name of the module, the 2nd row contains the annotations, and the 3rd row
   * contains the p-values
   */
  public String [][] getSelectedRows () {
    if(this.jTable == null){
      return new String [0][0];
    }
    
    int [] selectedRowIndices = this.jTable.getSelectedRows();
    if(selectedRowIndices.length == 0){
      return new String[0][0];
    }
    
    String [][] selectedRows = new String[selectedRowIndices.length][];
    for(int i = 0; i < selectedRowIndices.length; i++){
      selectedRows[i] = this.data[selectedRowIndices[i]];
    }//for i
    
    return selectedRows;
        
  }//getSelectedRows

  /**
   * Returns <code>this.mostSpecific</code>
   */
  public boolean getMostSpecific (){
    return this.mostSpecific;
  }//getMostSpecific

  /**
   * @return the maximum number of annotations to display per module.
   */
  public int getMaxNumAnnotations (){
    return this.maxNumAnnotations;
  }//getMaxNumAnnotations
     

  /**
   * Sets the maximum number of annotations to display per module.
   * 
   * @param max_num_annotations the maximum number of annotations
   */
  public void setMaxNumAnnotations (int max_num_annotations){
    this.maxNumAnnotations = max_num_annotations;
  }//setMaxNumAnnotations
  
  /**
   * Set the name of the node attribute to use to display the names of modules and module members
   * 
   * @param attribute_name a String node attribute
   */
  public void setNodeLabelAttribute (String attribute_name){
	  this.nodeNameAttribute = attribute_name;
  }
  /**
   *  Get the name of the node attribute to use to display the names of modules and module members
   *  
   *  @return the name of the node String attribute
   */
  public String getNodeLabelAttribute (){
	  return this.nodeNameAttribute;
  }

  /**
   * Returns the data contained in the table.
   *
   * @return a <code>String[][]</code> object
   */
  public String [][] getData (){
    return this.data;
  }//getData

  /**
   * Creates the table.
   *
   * @param annotations_map the <code>ModuleAnnotationMap</code> that 
   *                        contains the annotations to be displayed 
   * @param button_actions an array of <code>AbstractAction</code> objects to add
   *                      to <code>JButton</code>s that will be added to the bottom
   *                      of the table
   * @param freq_table_actions an array of actions each of which will be added to a 
   *                           <code>JButton</code> that will be located in the button panel
   *                           of the <code>AnnotationsFrequencyTable</code>
   * @param annots_tree_view_actions an array of actions each of which will be added to a
   *                                 <code>JButton</code> that will be located in the button
   *                                  panel of the <code>AnnotationsTreeView</code> that belongs
   *                                  to the <code>AnnotationsFrequencyTable</code>
   */
  protected void create (
  		ModuleAnnotationsMap annotations_map,
		AbstractAction [] button_actions,
		Annotation annotation,
		AbstractAction [] freq_table_actions,
		AbstractAction [] annots_tree_view_actions){
  	
    ArrayList dataArrayList = new ArrayList(); // ArrayList of String []
    Object [] moduleIds = annotations_map.getModuleIDs();
    CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
    for(int i = 0; i < moduleIds.length; i++){
      ModuleAnnotation [] annotationsArray = annotations_map.get(moduleIds[i],this.mostSpecific);
      String moduleName = null;
      	if(moduleIds[i] instanceof CyNode){
      		moduleName = nodeAtts.getStringAttribute( ( (CyNode)moduleIds[i]).getIdentifier(), getNodeLabelAttribute());
      		if(moduleName == null){
      			moduleName = ((CyNode)moduleIds[i]).getIdentifier();
      		}
      	}
      	if(moduleName == null){
      		moduleName = moduleIds[i].toString();
      	}
      for(int j = 0; j < annotationsArray.length && j < this.maxNumAnnotations; j++){
        String [] tableRow = new String[3];
        tableRow[0] = moduleName;
        tableRow[1] = annotationsArray[j].getOntologyTerm().getName();
        tableRow[2] = String.valueOf(annotationsArray[j].getPValue());
        dataArrayList.add(tableRow);
      }
      
    }//for i

    this.data = (String [][])dataArrayList.toArray( new String[dataArrayList.size()][3]);
    String [] columnNames = {"MODULE NAME", "CLASSIFICATION","P-VALUE"};

    this.jTable = new JTable(this.data, columnNames);
    this.jTable.setPreferredScrollableViewportSize(new Dimension(550, 250));
    
    //Create the scroll pane and add the table to it. 
    JScrollPane scrollPane = new JScrollPane(this.jTable);
    
    JPanel buttonPanel = new JPanel();
    JButton saveToFile = new JButton("Save To File...");
    saveToFile.addActionListener(
                                 new AbstractAction(){
                                   public void actionPerformed(ActionEvent e){
                                     saveToFile(ModuleAnnotationsTable.this.data,
                                                ModuleAnnotationsTable.this);
                                   }
                                 });
    buttonPanel.add(saveToFile);
    
    final ModuleAnnotationsMap fMap = annotations_map;
    final Annotation fAnnotation = annotation;
    final AbstractAction [] freqTableActions = freq_table_actions;
    final AbstractAction [] annotTVactions = annots_tree_view_actions;
    JButton freqTableButton = new JButton("Show Frequency Table");
    this.freqTable = new AnnotationsFrequencyTable();
    freqTableButton.addActionListener(
                                      new AbstractAction (){
                                        public void actionPerformed (ActionEvent e){
                                          freqTable.prepare(fMap,
                                                            fAnnotation,
                                                            ModuleAnnotationsTable.this.mostSpecific,
                                                            true,
                                                            ModuleAnnotationsTable.this,
                                                            freqTableActions,
                                                            annotTVactions);
                                          freqTable.pack();
                                          freqTable.setLocationRelativeTo(ModuleAnnotationsTable.this);
                                          freqTable.setVisible(true);
                                        }//actionPerformed
                                      });
    buttonPanel.add(freqTableButton);
    
    if(button_actions != null){
      for(int i = 0; i < button_actions.length; i++){
        JButton newButton = new JButton(button_actions[i]);
        button_actions[i].putValue(String.valueOf(newButton.hashCode()) , this);
        buttonPanel.add(newButton);
      }
    }
    JButton okButton = new JButton("Close");
    okButton.addActionListener(new CloseWindowAction());
    buttonPanel.add(okButton);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(scrollPane,BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    getContentPane().add(mainPanel, BorderLayout.CENTER);
  }//create

  /**
   * Saves the given data to a user defined file.
   */
  static public void saveToFile (String [][] data, JFrame parent_frame){
    JFileChooser chooser = new JFileChooser();
    String filePath = null;
    if(chooser.showSaveDialog(parent_frame) == JFileChooser.APPROVE_OPTION){
      filePath = chooser.getSelectedFile().toString();
    }//if choseer
    if(filePath == null){return;}
    try{
      File file = new File(filePath);
      if(file.exists()){
        // Ask the user if she wants to overwrite the file
        int n = JOptionPane.showConfirmDialog(parent_frame,
                                              "The file " + filePath + " already exists. Overwrite?",
                                              "File Exists",
                                              JOptionPane.YES_NO_OPTION);
        if(n == JOptionPane.NO_OPTION){
          return;
        }
      }// if file exists
      FileWriter fileWriter = new FileWriter(file);
      StringBuffer strBuffer = new StringBuffer();
      String nl = System.getProperty("line.separator");
      for(int i = 0; i < data.length; i++){
        for(int j = 0; j < data[i].length; j++){
          strBuffer.append(data[i][j]);
          if(j == data[i].length - 1){
            strBuffer.append(nl);
          }else{
            strBuffer.append("\t");
          }
        }//for j
        
      }// for i
      fileWriter.write(strBuffer.toString(), 0, strBuffer.length());
      fileWriter.flush();
    }catch(IOException ioe){
      System.out.println(ioe);
      ioe.printStackTrace();
    }
  }//saveToFile
  

  // ----------- Internal classes --------------//
  protected class CloseWindowAction extends AbstractAction{

    CloseWindowAction(){
      super("Close");
    }//CloseWindowAction

    public void actionPerformed (ActionEvent event){
      ModuleAnnotationsTable.this.dispose();
    }//actionPerformed
  }//CloseWindowAction
  
}//class ModuleAnnotationsTable
