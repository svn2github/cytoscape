/**
 * A <code>javax.swing.JDialog</code> that accepts user parameters for the
 * <code>StatementCalculator</code>.
 *
 * @author Greg Carter
 * @author Iliana Avila
 */

package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import annotations.*;
import cytoscape.*;
import cytoscape.data.servers.*;
import cytoscape.data.annotation.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class StatementDialog extends JDialog{

  protected static final double WRONG_PVAL = -1;
  protected static final int WRONG_POPULATION = -1;
  protected static final int WRONG_MAX_NUM_ANNOTS = -1;
  
  /**
   * The <code>BioDataServer</code> from which available annotations
   * can be ontained.
   */
  protected BioDataServer bioDataServer;

  /**
   * The radio button that determined whether the calculation
   * of annotations will only show the most specific annotations or not.
   */
  protected JRadioButton specificRadioButton;

  /**
   * The box that shows the available annotations.
   */
  protected JComboBox ontologyOptions;

  /**
   * The actions that will be added to buttons that will 
   * be added to <code>ModuleAnnotationsTable</code>
   * objects that pop-up when the "Calculate" button is pressed.
   */
  protected AbstractAction [] tableActions;

  /**
   * The input field for pvalue threshold.
   */
  protected JTextField pvalueField;

  /**
   * The input field for population threshold.
   */
  protected JTextField populationField;

  /**
   * Constructor.
   * 
   * @param bio_data_server the server from which available annotations can be
   *                        obtained
   */
  public StatementDialog(BioDataServer bio_data_server){
    this.tableActions = new AbstractAction[0];
    setBioDataServer(bio_data_server);
    create();
    setFavoriteAnnotation("GO","Biological Process"); // curator, type
  }//StatementDialog

  /**
   * Sets the <code>BioDataServer</code> from which annotations
   * can be obtained.
   */
  public void setBioDataServer (BioDataServer bio_data_server){
    this.bioDataServer = bio_data_server;
  }//setBioDataServer

  /**
   * Sets the array of <code>JButton</code> objects that
   * will get added to the bottom of the <code>StatementTable</code>
   * objects that pop-up when the user presses the "Calculate" button.
   */
  public void setActionsForTable (AbstractAction [] button_actions){
    this.tableActions = button_actions;
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
   * Creates the dialog.
   */
  protected void create (){
    
    JPanel mainPanel  = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createTitledBorder("Biological Statements Parameters"));

    Border panelPadding = BorderFactory.createEmptyBorder(5,5,5,5); 
    Component padding = Box.createRigidArea(new Dimension(20,0));

    JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new GridLayout(0, 1));
    JLabel ontologyLabel = new JLabel("Available Annotations:");
    JLabel pvalueLabel = new JLabel("Minimum -Log(P-Value) Score:"); 
    JLabel populationLabel = new JLabel("Minimum Number of Nodes Involved: "); 
    labelPanel.add(ontologyLabel);
    labelPanel.add(pvalueLabel);
    labelPanel.add(populationLabel);

    JPanel fieldPanel = new JPanel();
    fieldPanel.setLayout(new GridLayout(0, 1));
    
    // Get the available annotaitons for the ontology options
    Object [] annotations = null;
    boolean annotationsAvailable = true;
    if(this.bioDataServer != null){
      annotations = this.bioDataServer.getAnnotationDescriptions();
    }
    if(annotations == null){
      annotations = new String[1];
      annotations[0] = "None available";
      annotationsAvailable = false;
    }
    this.ontologyOptions = new JComboBox(annotations);
    fieldPanel.add(this.ontologyOptions);
    
    double maxPValue = StatementCalculator.getMinNegLogP();
    this.pvalueField = new JTextField(String.valueOf(maxPValue), 3);
    this.pvalueField.addActionListener(new PValueFieldListener());
    fieldPanel.add(this.pvalueField);

    int minPopulation = StatementCalculator.getMinPopulation();
    this.populationField = new JTextField(String.valueOf(minPopulation), 3);
    this.populationField.addActionListener(new PopulationFieldListener());
    fieldPanel.add(this.populationField);
        
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
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setBorder(panelPadding);
    JButton calculateButton = new JButton("Calculate");
    calculateButton.addActionListener(new CalculateButtonListener());
    if(!annotationsAvailable){
      calculateButton.setEnabled(false);
    }
    buttonPanel.add(calculateButton);
    JButton okButton = new JButton("Dismiss");
    okButton.addActionListener(new OKButtonListener());
    buttonPanel.add(padding);
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
                                     "Please enter a positive decimal number",
                                     "Input Error",
                                     JOptionPane.ERROR_MESSAGE);
      return WRONG_PVAL;
    }
    if(pval < 0){
      
      // Tell the user that he has to enter a double
      JOptionPane.showMessageDialog(this,
                                    "Please enter a positive decimal number",
                                    "Input Error",
                                    JOptionPane.ERROR_MESSAGE);
      return WRONG_PVAL;
    }
    return pval;
  }//checkPValueInput

  /**
   * Checks for correct population input, if correct it returns it.
   */
  protected int checkPopulationInput (String text){
    int pop;
    try{
      pop = Integer.parseInt(text);
    }catch (NumberFormatException ex){
      // Tell the user that he has to enter an integer
      JOptionPane.showMessageDialog( this,
                                     "Please enter an integer greater than 1",
                                     "Input Error",
                                     JOptionPane.ERROR_MESSAGE);
      return WRONG_POPULATION;
    }
    if(pop < 2){
      
      // Tell the user that he has to enter an integer
      JOptionPane.showMessageDialog(this,
                                    "Please enter an integer greater than 1",
                                    "Input Error",
                                    JOptionPane.ERROR_MESSAGE);
      return WRONG_POPULATION;
    }
    return pop;
  }//checkPopulationInput

  //------------------- Listeners ----------------------//

  protected class PValueFieldListener extends AbstractAction{
    PValueFieldListener (){
      super("");
    }//PValueFieldListener
    
    public void actionPerformed (ActionEvent e){
      Object source = e.getSource();
      JTextField tf;
      if(source instanceof JTextField){
        tf = (JTextField)source;
      }else{
        return;
      }
      // Validate input
      String input = tf.getText();
      double pval = StatementDialog.this.checkPValueInput(input);
      if(pval != StatementDialog.WRONG_PVAL){
        StatementCalculator.setMinNegLogP(pval);
      }
    }//actionPerformed
  }//class PValueFieldListener

  protected class PopulationFieldListener extends AbstractAction{
    PopulationFieldListener (){
      super("");
    }//PopulationFieldListener
    
    public void actionPerformed (ActionEvent e){
      Object source = e.getSource();
      JTextField tf;
      if(source instanceof JTextField){
        tf = (JTextField)source;
      }else{
        return;
      }
      // Validate input
      String input = tf.getText();
      int pop = StatementDialog.this.checkPopulationInput(input);
      if(pop != StatementDialog.WRONG_POPULATION){
        StatementCalculator.setMinPopulation(pop);
      }
    }//actionPerformed
  }//class PopulationFieldListener

  protected class OKButtonListener extends AbstractAction {

    OKButtonListener (){
      super("");
    }//OKButtonListener

    public void actionPerformed (ActionEvent e){
      StatementDialog.this.dispose();
    }//actionPerformed
  }//class OKButtonListener

  protected class CalculateButtonListener extends AbstractAction {
    CalculateButtonListener (){
      super("");
    }//CalculateButtonListener

    public void actionPerformed (ActionEvent e){
      // Set the type of annotation
      Object selected = StatementDialog.this.ontologyOptions.getSelectedItem();
      AnnotationDescription annotationDesc = null;
      if(selected instanceof AnnotationDescription){
        annotationDesc = (AnnotationDescription)selected;
      }else{
        return;
      }
      
      String input = StatementDialog.this.pvalueField.getText();
      double pvalue = StatementDialog.this.checkPValueInput(input);
      if(pvalue != StatementDialog.WRONG_PVAL){
        StatementCalculator.setMinNegLogP(pvalue);
      }else{
        return;
      }
      
      String input2 = StatementDialog.this.populationField.getText();
      int pop = StatementDialog.this.checkPopulationInput(input2);
      if(pop != StatementDialog.WRONG_POPULATION){
        StatementCalculator.setMinPopulation(pop);
      }else{
        return;
      }
      
      Annotation annotation = 
        StatementDialog.this.bioDataServer.getAnnotation(annotationDesc);
      if(annotation == null){
        return;
      }
      StatementCalculator.setAnnotation(annotation);
            
      //  Do the calculation and return an array of Statements
      boolean makeSpecific = StatementDialog.this.specificRadioButton.isSelected();
      CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
      Statement[] statementSet = 
        StatementCalculator.calculateStatements(cyNetwork,
                                                makeSpecific,
                                                GeneticInteraction.ATTRIBUTE_GENETIC_CLASS);
 
      System.out.println( "Done calculating statements." );

      //  Do some output preliminaries
      String title = annotation.getCurator() + " " + annotation.getType() + ", -log(p) > " +
        StatementCalculator.getMinNegLogP() +
        ", most specific = " + makeSpecific + " ("+statementSet.length+" statements)";

      //  Make the table
      StatementTable table = 
        new StatementTable(cyNetwork,
                           statementSet,
                           StatementDialog.this.specificRadioButton.isSelected(),
                           title, StatementDialog.this.tableActions);
      table.pack();
      table.setLocationRelativeTo(StatementDialog.this);
      table.setVisible(true);
    }//actionPerformed
  }//class CalculateButtonListener

}//class StatementDialog
