
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
 * A <code>javax.swing.JDialog</code> that accepts user parameters for the
 * <code>ModuleAnnotationsCalculator</code>.
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org, iliana.avila@gmail.com>
 */

package annotations.ui;
import annotations.calculator.*;
import annotations.*;
import cytoscape.*;
import cytoscape.data.servers.*;
import cytoscape.data.annotation.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ModuleAnnotationsDialog extends JDialog{

  protected static final double WRONG_PVAL = -1;
  protected static final int WRONG_MAX_NUM_ANNOTS = -1;
  
  /**
   * The radio button that determines whether the calculation
   * of annotations will only show the most specific annotations or not.
   */
  protected JRadioButton specificRadioButton;

  /**
   * The box that shows the available annotations.
   */
  protected JComboBox ontologyOptions;

  /**
   * The maximum number of annotations to show
   * in the <code>ModuleAnnotationsTable</code>.
   * Ten by default.
   */
  protected int maxNumAnnots;

  /**
   * The actions that will be added to buttons that will 
   * be added to <code>ModuleAnnotationsTable</code>
   * objects that pop-up when the "Calculate" button is pressed.
   */
  protected AbstractAction [] tableActions;

  /**
   * The actions that will be added to new buttons in the button panel of 
   * the <code>AnnotationsFrequencyTable</code> objects
   */
  protected AbstractAction [] freqTableActions;
  
  /**
   * The actions that will be added to new buttons in the button panel for 
   * <code>AnnotationsTreeView</code>
   */
  protected AbstractAction [] annotsTreeViewActions;
  
  /**
   * The input field for pvalue threshold.
   */
  protected JTextField pvalueField;

  /**
   * The input field for max num annotations.
   */
  protected JTextField maxNumAnnotsText;

  /**
   * A radio button that specifies whether or not module membership should be randomized
   * before calculating annotations (good to make paper figures).
   */
  protected JRadioButton randomizeRadioButton;
  
  // Parameters for the annotations calculator
  /**
   * The ids to be used as module identifiers in the annotations table.
   */
  protected Object [] moduleIds;
  
  /**
   * The groups of genes for which annotations will be calculated. 
   */
  protected String [][] geneGroups;
  
  /**
   * To be considered overrepresented, annotations must have at most this p-value (higher p-values mean less significance)
   */
  protected double maxPval;
  
  protected String nodeNameAttribute = "commonName";
  
  protected static final String newLine = System.getProperty("line.separator");
  protected static final String HELP_MESSAGE = 
    "1. Choose a source of annotations." + newLine +
    "2. If desired, change default P-Value." + newLine +
    "P-Value range is 0 to 1.  Smaller p-values are more stringent." + newLine +
    "3. If desired,  change default maximum number of annotations per" + newLine +
    "Biomodule. Annotations with the smallest p-values will be shown." + newLine +
    "4. If \"Only show most specific annotations\" is selected, then" + newLine +
    "annotations that are parents of others, e.g.metabolism and" + newLine +
    "acid metabolism, and that have the same p-values as their" + newLine +
    "children won't be included." + newLine +
    "5. Press the \"Calculate\" button.";
  
  /**
   * Panel with input fields for the user.
   */
  protected JPanel fieldPanel;
  
  /**
   * The button that when pushed calculated annotations.
   */
  protected JButton calculateButton;
  
  /**
   * The latest calculated annotations.
   */
  protected ModuleAnnotationsMap lastAnnotationsMap;
  
  /**
   * Names of modules to use for display purposes, for example, for displaying a table with a module name column.
   */
  protected String [] moduleNamesForDisplay;
  
  /**
   * Calls <code>ModuleAnnotationsDialog (null, null, false)</code>
   */
  public ModuleAnnotationsDialog (){
  	this(null, null, false);
  }//ModuleAnnotationsDialog
  
  /**
   * Constructor.
   * 
   * @param module_ids the objects to be used as keys to uniquely identify each gene group, their String representations
   * will be used as module identifiers when displaying the annotations, unless 
   * <code>ModuleAnnotationsDialog.setModuleDisplayNames(String[])</code> is called
   * @param gene_groups each row represents a module of genes for which annotations are to be found
   * @param randomize whether to randomize module membership while maintaining the same number of members
   * before calculating annotations
   */
  public ModuleAnnotationsDialog (Object [] module_ids, String [][] gene_groups, boolean randomize){
    this.maxNumAnnots = 10;
    this.tableActions = new AbstractAction[0];
    create();
    setCalculatorParameters(module_ids, gene_groups, randomize);
  }//ModuleAnnotationsDialog
  
  /**
   * Sets the parameters for the annotations calculator that will be used on the next calculation.
   * 
   * @param module_ids the objects to be used as keys to uniquely identify each gene group
   * @param gene_groups each row represents a module of genes for which annotations are to be found
   * @param randomize whether to randomize module membership while maintaining the same number of members
   * before calculating annotations
   */
  public void setCalculatorParameters (Object [] module_ids, String [][] gene_groups, boolean randomize){
  	this.moduleIds = module_ids;
  	this.geneGroups = gene_groups;
  	this.randomizeRadioButton.setSelected(randomize);
  }//setCalculatorParameters
  
  /** 
   * Sets names of modules to use for display purposes, for example, for displaying a table with a module name column.
   * The order of names in the array should be the same as the one for the 'gene_groups' parameter in 
   * <code>ModuleAnnotationsDialog (Object [] module_ids, String [][] gene_groups, boolean randomize)</code> or in
   * <code>setCalculatorParameters (Object [] module_ids, String [][] gene_groups, boolean randomize)</code>
   */
   public void setModuleDisplayNames (String [] module_display_names){
  	this.moduleNamesForDisplay = module_display_names;
  }//setModuleDisplayNames
   
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
   * Sets the arrays of <code>AbstractAction</code> objects that
   * will get added to buttons at the bottom of the <code>ModuleAnnotationsTable</code>,
   * <code>AnnotationsFrequencyTable</code>, and the <code>AnnotationsTreeView</code>
   * respectively, any of these parameters can be null, in which case no new buttons will
   * be created for the specific dialog.
   */
  public void setActionsForTable (AbstractAction [] mod_annots_actions,
                                  AbstractAction [] freq_table_actions,
                                  AbstractAction [] annots_tree_view_actions){
    this.tableActions = mod_annots_actions;
    this.freqTableActions = freq_table_actions;
    this.annotsTreeViewActions = annots_tree_view_actions;
  }//setActionsForTable

  /**
   * Sets the favorite user annotation so that it is shown
   * as the default in <code>this.ontologyOptions</code> combo box.
   * 
   * @param curator the curator of the favorite annotation
   * @param type the type of the favorite annotation
   */
  public void setFavoriteAnnotation (String curator, String type){
    if(this.ontologyOptions == null){return;}
    int numItems = this.ontologyOptions.getItemCount();
    for(int i = 0; i < numItems; i++){
      AnnotationDescription annoDesc = 
        (AnnotationDescription)this.ontologyOptions.getItemAt(i);
      if(curator.equals(annoDesc.getCurator()) &&
         type.equals(annoDesc.getType())){
        this.ontologyOptions.setSelectedItem(annoDesc);
      }
    }
  }//setFavoriteAnnotation
  
  /**
   * @return the annotations map that was calculated last, or null, if none calculated yet
   */
  public ModuleAnnotationsMap getLastCalculatedAnnotationsMap (){
  	return this.lastAnnotationsMap;
  }//getLastCalculatedAnnotationsMap
  
  /**
   * Creates the dialog.
   */
  protected void create (){
    
    JPanel mainPanel  = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createTitledBorder("Module Annotations Parameters"));

    Border panelPadding = BorderFactory.createEmptyBorder(5,5,5,5); 
    Component padding = Box.createRigidArea(new Dimension(20,0));

    JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new GridLayout(0, 1));
    JLabel ontologyLabel = new JLabel("Available Annotations:");
    JLabel pvalueLabel = new JLabel("Maximum P-Value:"); 
    JLabel maxNumAnnotsLabel = new JLabel("Max Num Annotations:");
    labelPanel.add(ontologyLabel);
    labelPanel.add(pvalueLabel);
    labelPanel.add(maxNumAnnotsLabel);

    this.fieldPanel = new JPanel();
    fieldPanel.setLayout(new GridLayout(0, 1));
    
    // Get the available annotaitons for the ontology options
    Object [] annotations = null;
    boolean annotationsAvailable = true;
    BioDataServer bioDataServer = Cytoscape.getBioDataServer();
    if(bioDataServer != null){
      annotations = bioDataServer.getAnnotationDescriptions();
    }
    if(annotations == null || annotations.length == 0){
      annotations = new String[1];
      annotations[0] = "None available";
      annotationsAvailable = false;
    }
    this.ontologyOptions = new JComboBox(annotations);
    fieldPanel.add(this.ontologyOptions);
    
    this.maxPval = ModuleAnnotationsCalculator.DEFAULT_MAX_P_VAL;
    this.pvalueField = new JTextField(String.valueOf(this.maxPval), 3);
    this.pvalueField.addActionListener(new AbstractAction (){
    	public void actionPerformed (ActionEvent e){
    	      Object source = e.getSource();
    	      JTextField tf;
    	      if(source instanceof JTextField){
    	        tf = (JTextField)source;
    	      }else{
    	        return;
    	      }
    	      ModuleAnnotationsDialog.this.readPval(tf);
    	    }//actionPerformed
    	});
    fieldPanel.add(this.pvalueField);
        
    this.maxNumAnnotsText = new JTextField(String.valueOf(this.maxNumAnnots), 3);
    this.maxNumAnnotsText.addActionListener(new AbstractAction (){
    	public void actionPerformed (ActionEvent e){
    		Object source = e.getSource();
    	      JTextField tf;
    	      if(source instanceof JTextField){
    	        tf = (JTextField)source;
    	      }else{
    	        return;
    	      }
    	      readMaxNumAnnotations(tf);
    	}
    });
    fieldPanel.add(this.maxNumAnnotsText);
    
    //Put the panels in another panel, labels on left,
    //text fields on right
    JPanel contentPane = new JPanel();
    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10,
                                                          10, 10));
    contentPane.setLayout(new BorderLayout());
    contentPane.add(labelPanel, BorderLayout.CENTER);
    contentPane.add(fieldPanel, BorderLayout.EAST);
    
    mainPanel.add(contentPane);
    
    JPanel specificPanel = new JPanel();
    specificPanel.setLayout(new BoxLayout(specificPanel, BoxLayout.X_AXIS));
    specificPanel.setBorder(panelPadding);
    this.specificRadioButton = new JRadioButton("Only show most specific annotations");
    this.specificRadioButton.setSelected(true);
    specificPanel.add(this.specificRadioButton);
    
    mainPanel.add(specificPanel);
    
    JPanel randomizePanel = new JPanel();
    randomizePanel.setLayout(new BoxLayout(randomizePanel, BoxLayout.X_AXIS));
    randomizePanel.setBorder(panelPadding);
   
    this.randomizeRadioButton = new JRadioButton("Randomize module membership");
    
    this.randomizeRadioButton.setSelected(false);
    randomizePanel.add(this.randomizeRadioButton);
    
    mainPanel.add(randomizePanel);
    
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setBorder(panelPadding);
    this.calculateButton = new JButton("Calculate");
    this.calculateButton.addActionListener(new CalculateButtonListener());
    if(!annotationsAvailable){
      this.calculateButton.setEnabled(false);
    }
    buttonPanel.add(calculateButton);
    JButton helpButton = new JButton("Help");
    helpButton.addActionListener(new AbstractAction(){
    	public void actionPerformed (ActionEvent e){
    		JOptionPane.showMessageDialog(ModuleAnnotationsDialog.this,ModuleAnnotationsDialog.HELP_MESSAGE);
    	}//actionEvent
    });
    buttonPanel.add(helpButton);
    JButton okButton = new JButton("Close");
    okButton.addActionListener(new AbstractAction(){
    	public void actionPerformed (ActionEvent e){
    	      ModuleAnnotationsDialog.this.dispose();
    	    }//actionPerformed
    });
    buttonPanel.add(okButton);

    mainPanel.add(buttonPanel);
    setContentPane(mainPanel);
  }//create

  /**
   * Checks for correct p-value input, if correct it returns it.
   */
  protected double checkPValueInput (String text){
    double pval;
    try{
      pval = Double.parseDouble(text);
    }catch (NumberFormatException ex){
      // Tell the user that he has to enter a double
      JOptionPane.showMessageDialog( this,
                                     "Please enter a decimal number >=0 and <= 1",
                                     "Wrong Input",
                                     JOptionPane.ERROR_MESSAGE);
      
      return WRONG_PVAL;
      
    }
    if(pval < 0 || pval > 1){
      
      // Tell the user that he has to enter a double
      JOptionPane.showMessageDialog(this,
                                    "Please enter a decimal number >=0 and <= 1",
                                    "Wrong Input",
                                    JOptionPane.ERROR_MESSAGE);
      
      return WRONG_PVAL;
    }
    return pval;
  }//checkPValueInput

  /**
   * Checks for correct max num annotations input.
   */
  protected int checkMaxNumAnnotsInput (String text){
    int newMaxNumAnnots;
    try{
       newMaxNumAnnots = Integer.parseInt(text); 
     }catch(NumberFormatException exception){
       JOptionPane.showMessageDialog(this,
                                     "Please enter an integer.",
                                     "Wrong Input",
                                     JOptionPane.ERROR_MESSAGE);
       return WRONG_MAX_NUM_ANNOTS;
       
     }
      if(newMaxNumAnnots < 0){
        JOptionPane.showMessageDialog(this,
                                      "Please enter a positive integer.",
                                      "Wrong Input",
                                      JOptionPane.ERROR_MESSAGE);
        return WRONG_MAX_NUM_ANNOTS;
        
      }
      return newMaxNumAnnots;
  }//checkMaxNumAnnotsInput
  
  protected void readPval (JTextField field){
	// Validate input
    String input = field.getText();
    double pval = checkPValueInput(input);
    if(pval != ModuleAnnotationsDialog.WRONG_PVAL){
      this.maxPval = pval;
    }
  }//readPval
  
  protected void readMaxNumAnnotations (JTextField field){
  	String text = field.getText();
    int newMaxNumAnnots = checkMaxNumAnnotsInput(text);
    if(newMaxNumAnnots != ModuleAnnotationsDialog.WRONG_MAX_NUM_ANNOTS){
      this.maxNumAnnots = newMaxNumAnnots;
    }
  }//readMaxNumAnnotations
  
  /**
   * Overrides <code>super.setVisible(boolean)</code> so that BioDataServer can be updated if necessary.
   */
  
  public void setVisible (boolean visible){
  	
  	if(visible){
  		BioDataServer server = Cytoscape.getBioDataServer();
  		Object [] annotations = null;
  		if(server != null){
  	      annotations = server.getAnnotationDescriptions();
  	    }
  	    if(annotations == null || annotations.length == 0){
  	      annotations = new String[1];
  	      annotations[0] = "None available";
  	      this.calculateButton.setEnabled(false);
  	    }else{
  	    	this.calculateButton.setEnabled(true);
  	    }
  	    this.fieldPanel.remove(this.ontologyOptions);
  	    this.ontologyOptions = new JComboBox(annotations);
  	    this.fieldPanel.add(this.ontologyOptions,0);
  	    // Argh!!! This does not work!!!! :-{
  	    //this.ontologyOptions.removeAll();
  	    //for(int i = 0; i < annotations.length; i++){
  	    //this.ontologyOptions.addItem(annotations[i]);
  	    //}
  	    //this.ontologyOptions.invalidate();
  	    //this.validateTree();
  	    this.fieldPanel.validate();
  	    this.validate();
  	    this.pack();
  	}// if (visible)
  	
  	super.setVisible(visible);
  }//setVisible

  //------------------- Listeners ----------------------//

  protected class CalculateButtonListener extends AbstractAction {
    CalculateButtonListener (){
      super("");
    }//CalculateButtonListener

    public void actionPerformed (ActionEvent e){
      // Set the type of annotation
      Object selected = ModuleAnnotationsDialog.this.ontologyOptions.getSelectedItem();
      AnnotationDescription annotationDesc = null;
      if(selected instanceof AnnotationDescription){
        annotationDesc = (AnnotationDescription)selected;
      }else{
        return;
      }
      
      BioDataServer bioDataServer = Cytoscape.getBioDataServer();
      Annotation annotation = null;
      if(bioDataServer != null){
      	annotation = bioDataServer.getAnnotation(annotationDesc);
      }
      if(annotation == null){
        return;
      }
      
      readMaxNumAnnotations(ModuleAnnotationsDialog.this.maxNumAnnotsText);
      readPval(ModuleAnnotationsDialog.this.pvalueField);
      
      boolean randomize = ModuleAnnotationsDialog.this.randomizeRadioButton.isSelected();
            
      ModuleAnnotationsDialog.this.lastAnnotationsMap = ModuleAnnotationsCalculator.calculateAnnotations(
      		annotation,
      		ModuleAnnotationsDialog.this.moduleIds,
			ModuleAnnotationsDialog.this.geneGroups,
			ModuleAnnotationsDialog.this.maxPval, 
			randomize);
      String title = Cytoscape.getCurrentNetwork().getTitle() + ": " + annotation.getCurator() + " " + annotation.getType() + " pVal = " +
        ModuleAnnotationsDialog.this.maxPval + " max annotations = " +
        ModuleAnnotationsDialog.this.maxNumAnnots +
        " most specific = " + ModuleAnnotationsDialog.this.specificRadioButton.isSelected() +
        " randomize = " + randomize;
      ModuleAnnotationsTable table = 
        new ModuleAnnotationsTable(annotation,
                                   ModuleAnnotationsDialog.this.lastAnnotationsMap,
                                   ModuleAnnotationsDialog.this.specificRadioButton.isSelected(),
                                   ModuleAnnotationsDialog.this.maxNumAnnots,
                                   title,
                                   ModuleAnnotationsDialog.this.tableActions,
                                   ModuleAnnotationsDialog.this.freqTableActions,
                                   ModuleAnnotationsDialog.this.annotsTreeViewActions,
                                   getNodeLabelAttribute());
      
      table.pack();
      table.setLocationRelativeTo(ModuleAnnotationsDialog.this);
      table.setVisible(true);
    }//actionPerformed
  }//class CalculateButtonListener

}//class ModuleAnnotationsDialog
